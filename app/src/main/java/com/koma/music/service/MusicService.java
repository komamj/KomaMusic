package com.koma.music.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.koma.music.R;
import com.koma.music.data.local.db.MusicPlaybackState;
import com.koma.music.data.model.MusicPlaybackTrack;
import com.koma.music.util.ImageLoader;
import com.koma.music.util.LogUtils;
import com.koma.music.util.PreferenceUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by koma on 3/21/17.
 */

public class MusicService extends Service {
    private static final String TAG = MusicService.class.getSimpleName();
    /**
     * Keeps a mapping of the track history
     */
    private static LinkedList<Integer> mHistory = new LinkedList<>();

    /**
     * Used to shuffle the tracks
     */
    private static final Shuffler mShuffler = new Shuffler();

    /**
     * Service stub
     */
    private final IBinder mBinder = new ServiceStub(this);

    /**
     * 4x1 widget
     */
    //private final AppWidgetSmall mAppWidgetSmall = AppWidgetSmall.getInstance();

    /**
     * 4x2 widget
     */
    //private final AppWidgetLarge mAppWidgetLarge = AppWidgetLarge.getInstance();

    /**
     * 4x2 alternate widget
     */
    //private final AppWidgetLargeAlternate mAppWidgetLargeAlternate = AppWidgetLargeAlternate.getInstance();

    /**
     * The media player
     */
    private MultiPlayer mPlayer;

    /**
     * The path of the current file to play
     */
    private String mFileToPlay;

    /**
     * Alarm intent for removing the notification when nothing is playing
     * for some time
     */
    private AlarmManager mAlarmManager;
    private PendingIntent mShutdownIntent;
    private boolean mShutdownScheduled;

    private NotificationManager mNotificationManager;

    /**
     * The cursor used to retrieve info on the current track and run the
     * necessary queries to play audio files
     */
    private Cursor mCursor;

    /**
     * The cursor used to retrieve info on the album the current track is
     * part of, if any.
     */
    private Cursor mAlbumCursor;

    /**
     * Monitors the audio state
     */
    private AudioManager mAudioManager;

    /**
     * Settings used to save and retrieve the queue and history
     */
    private SharedPreferences mPreferences;

    /**
     * Used to know when the service is active
     */
    private boolean mServiceInUse = false;

    /**
     * Used to know if something should be playing or not
     */
    private boolean mIsSupposedToBePlaying = false;

    /**
     * Gets the last played time to determine whether we still want notifications or not
     */
    private long mLastPlayedTime;

    private int mNotifyMode = NOTIFY_MODE_NONE;
    private long mNotificationPostTime = 0;

    private static final int NOTIFY_MODE_NONE = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;

    /**
     * Used to indicate if the queue can be saved
     */
    private boolean mQueueIsSaveable = true;

    /**
     * Used to track what type of audio focus loss caused the playback to pause
     */
    private boolean mPausedByTransientLossOfFocus = false;

    /**
     * Lock screen controls
     */
    private MediaSessionCompat mSession;

    // We use this to distinguish between different cards when saving/restoring
    // playlists
    private int mCardId;

    private int mPlayPos = -1;

    private int mNextPlayPos = -1;

    private int mOpenFailedCounter = 0;

    private int mMediaMountedCount = 0;

    private int mShuffleMode = Constants.SHUFFLE_NONE;

    private int mRepeatMode = Constants.REPEAT_NONE;

    private int mServiceStartId = -1;

    private String mLyrics;

    private ArrayList<MusicPlaybackTrack> mPlaylist = new ArrayList<MusicPlaybackTrack>(100);

    private long[] mAutoShuffleList = null;

    private MusicPlayerHandler mPlayerHandler;
    private HandlerThread mHandlerThread;

    private BroadcastReceiver mUnmountReceiver = null;

    private QueueUpdateTask mQueueUpdateTask;

    private ContentObserver mMediaObserver;
    /**
     * Stores the playback state
     */
    private MusicPlaybackState mPlaybackStateStore;

    /**
     * Shake detector class used for shake to switch song feature
     */
    private ShakeDetector mShakeDetector;

    /**
     * Switch for displaying album art on lockscreen
     */
    private boolean mShowAlbumArtOnLockscreen;

    private boolean mReadGranted = false;

    private PowerManager.WakeLock mHeadsetHookWakeLock;

    private ShakeDetector.Listener mShakeDetectorListener = new ShakeDetector.Listener() {

        @Override
        public void hearShake() {
            /*
             * on shake detect, play next song
             */
            LogUtils.i(TAG, "Shake detected!!!");

            gotoNext(true);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, "Service bound, intent = " + intent);
        cancelShutdown();
        mServiceInUse = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        LogUtils.d(TAG, "Service unbound");
        mServiceInUse = false;
        saveQueue(true);

        if (mReadGranted) {
            if (mIsSupposedToBePlaying || mPausedByTransientLossOfFocus) {
                // Something is currently playing, or will be playing once
                // an in-progress action requesting audio focus ends, so don't stop
                // the service now.
                return true;

                // If there is a playlist but playback is paused, then wait a while
                // before stopping the service, so that pause/resume isn't slow.
                // Also delay stopping the service if we're transitioning between
                // tracks.
            } else if (mPlaylist.size() > 0 || mPlayerHandler.hasMessages(Constants.TRACK_ENDED)) {
                scheduleDelayedShutdown();
                return true;
            }
        }
        stopSelf(mServiceStartId);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRebind(final Intent intent) {
        cancelShutdown();
        mServiceInUse = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        LogUtils.d(TAG, "Creating service");
        super.onCreate();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        } else {
            mReadGranted = true;
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Initialize the favorites and recents databases
        // mRecentsCache = RecentStore.getInstance(this);

        // gets the song play count cache
        // mSongPlayCountCache = SongPlayCount.getInstance(this);

        // gets a pointer to the playback state store
        mPlaybackStateStore = MusicPlaybackState.getInstance(this);

        // Initialize the image fetcher
        //  mImageFetcher = ImageFetcher.getInstance(this);
        // Initialize the image cache
        // mImageFetcher.setImageCache(ImageCache.getInstance(this));

        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work will not disrupt the UI.
        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        // Initialize the handler
        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());
        // Initialize the audio manager and register any headset controls for
        // playback
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Use the remote control APIs to set the playback state
        setUpMediaSession();

        // Initialize the preferences
        mPreferences = getSharedPreferences("MusicService", 0);
        mCardId = getCardId();

        mShowAlbumArtOnLockscreen = mPreferences.getBoolean(
                PreferenceUtils.SHOW_ALBUM_ART_ON_LOCKSCREEN, false);
        setShakeToPlayEnabled(mPreferences.getBoolean(PreferenceUtils.SHAKE_TO_PLAY, false));

        registerExternalStorageListener();

        // Initialize the media player
        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(mPlayerHandler);

        // Initialize the intent filter and each action
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SERVICECMD);
        filter.addAction(Constants.TOGGLEPAUSE_ACTION);
        filter.addAction(Constants.PAUSE_ACTION);
        filter.addAction(Constants.STOP_ACTION);
        filter.addAction(Constants.NEXT_ACTION);
        filter.addAction(Constants.PREVIOUS_ACTION);
        filter.addAction(Constants.PREVIOUS_FORCE_ACTION);
        filter.addAction(Constants.REPEAT_ACTION);
        filter.addAction(Constants.SHUFFLE_ACTION);
        // Attach the broadcast listener
        registerReceiver(mIntentReceiver, filter);

        // Get events when MediaStore content changes
        mMediaObserver = new MediaObserver(mPlayerHandler);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, mMediaObserver);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mMediaObserver);

        // Initialize the delayed shutdown intent
        final Intent shutdownIntent = new Intent(this, MusicService.class);
        shutdownIntent.setAction(Constants.SHUTDOWN);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);

        // Listen for the idle state
        scheduleDelayedShutdown();

        // Bring the queue back
        reloadQueue();
        notifyChange(Constants.QUEUE_CHANGED);
        notifyChange(Constants.META_CHANGED);
    }

    private void setUpMediaSession() {
        mSession = new MediaSessionCompat(this, "KomaMusic");
        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {
                pause();
                mPausedByTransientLossOfFocus = false;
            }

            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onSeekTo(long pos) {
                seek(pos);
            }

            @Override
            public void onSkipToNext() {
                gotoNext(true);
            }

            @Override
            public void onSkipToPrevious() {
                prev(false);
            }

            @Override
            public void onStop() {
                pause();
                mPausedByTransientLossOfFocus = false;
                seek(0);
                releaseServiceUiAndStop();
            }

            @Override
            public void onSkipToQueueItem(long id) {
                setQueuePosition((int) id);
            }

            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())) {
                    KeyEvent ke = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (ke != null && ke.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
                        if (ke.getAction() == KeyEvent.ACTION_UP) {
                            handleHeadsetHookClick(ke.getEventTime());
                        }
                        return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });

        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                new Intent(this, MediaButtonIntentReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setMediaButtonReceiver(pi);

        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "Destroying service");
        if (!mReadGranted) {
            return;
        }
        super.onDestroy();
        // Remove any sound effects
        final Intent audioEffectsIntent = new Intent(
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectsIntent);

        // remove any pending alarms
        mAlarmManager.cancel(mShutdownIntent);

        // Remove any callbacks from the handler
        mPlayerHandler.removeCallbacksAndMessages(null);
        // quit the thread so that anything that gets posted won't run
        mHandlerThread.quitSafely();

        // Release the player
        mPlayer.release();
        mPlayer = null;

        // Remove the audio focus listener and lock screen controls
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        mSession.release();

        // remove the media store observer
        getContentResolver().unregisterContentObserver(mMediaObserver);

        // Close the cursor
        closeCursor();

        // Unregister the mount listener
        unregisterReceiver(mIntentReceiver);
        if (mUnmountReceiver != null) {
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }

        // deinitialize shake detector
        stopShakeDetector(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        LogUtils.d(TAG, "Got new intent " + intent + ", startId = " + startId);
        mServiceStartId = startId;

        if (intent != null) {
            final String action = intent.getAction();

            if (Constants.SHUTDOWN.equals(action)) {
                mShutdownScheduled = false;
                releaseServiceUiAndStop();
                return START_NOT_STICKY;
            }

            handleCommandIntent(intent);
        }

        // Make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        scheduleDelayedShutdown();

        if (intent != null && intent.getBooleanExtra(Constants.FROM_MEDIA_BUTTON, false)) {
            MediaButtonIntentReceiver.completeWakefulIntent(intent);
        }

        return START_STICKY;
    }

    private void releaseServiceUiAndStop() {
        if (isPlaying()
                || mPausedByTransientLossOfFocus
                || mPlayerHandler.hasMessages(Constants.TRACK_ENDED)) {
            return;
        }

        LogUtils.d(TAG, "Nothing is playing anymore, releasing notification");
        cancelNotification();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        mSession.setActive(false);

        if (!mServiceInUse) {
            saveQueue(true);
            stopSelf(mServiceStartId);
        }
    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = Constants.SERVICECMD.equals(action) ?
                intent.getStringExtra(Constants.CMDNAME) : null;

        LogUtils.d(TAG, "handleCommandIntent: action = " + action + ", command = " + command);

        if (Constants.CMDNEXT.equals(command) || Constants.NEXT_ACTION.equals(action)) {
            gotoNext(true);
        } else if (Constants.CMDPREVIOUS.equals(command) || Constants.PREVIOUS_ACTION.equals(action)
                || Constants.PREVIOUS_FORCE_ACTION.equals(action)) {
            prev(Constants.PREVIOUS_FORCE_ACTION.equals(action));
        } else if (Constants.CMDTOGGLEPAUSE.equals(command) ||
                Constants.TOGGLEPAUSE_ACTION.equals(action)) {
            togglePlayPause();
        } else if (Constants.CMDPAUSE.equals(command) || Constants.PAUSE_ACTION.equals(action)) {
            pause();
            mPausedByTransientLossOfFocus = false;
        } else if (Constants.CMDPLAY.equals(command)) {
            play();
        } else if (Constants.CMDSTOP.equals(command) || Constants.STOP_ACTION.equals(action)) {
            pause();
            mPausedByTransientLossOfFocus = false;
            seek(0);
            releaseServiceUiAndStop();
        } else if (Constants.REPEAT_ACTION.equals(action)) {
            cycleRepeat();
        } else if (Constants.SHUFFLE_ACTION.equals(action)) {
            cycleShuffle();
        } else if (Constants.CMDHEADSETHOOK.equals(command)) {
            long timestamp = intent.getLongExtra(Constants.TIMESTAMP, 0);
            handleHeadsetHookClick(timestamp);
        }
    }

    private void handleHeadsetHookClick(long timestamp) {
        if (mHeadsetHookWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mHeadsetHookWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "KomaMusic headset button");
            mHeadsetHookWakeLock.setReferenceCounted(false);
        }
        // Make sure we don't indefinitely hold the wake lock under any circumstances
        mHeadsetHookWakeLock.acquire(10000);

        Message msg = mPlayerHandler.obtainMessage(Constants.HEADSET_HOOK_EVENT, Long.valueOf(timestamp));
        msg.sendToTarget();
    }

    /**
     * Updates the notification, considering the current play and activity state
     */
    private void updateNotification() {
        final int newNotifyMode;
        if (isPlaying()) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND;
        } else if (recentlyPlayed()) {
            newNotifyMode = NOTIFY_MODE_BACKGROUND;
        } else {
            newNotifyMode = NOTIFY_MODE_NONE;
        }

        int notificationId = hashCode();
        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == NOTIFY_MODE_FOREGROUND) {
                stopForeground(newNotifyMode == NOTIFY_MODE_NONE);
            } else if (newNotifyMode == NOTIFY_MODE_NONE) {
                mNotificationManager.cancel(notificationId);
                mNotificationPostTime = 0;
            }
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            startForeground(notificationId, buildNotification());
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager.notify(notificationId, buildNotification());
        }

        mNotifyMode = newNotifyMode;
    }

    private void cancelNotification() {
        stopForeground(true);
        mNotificationManager.cancel(hashCode());
        mNotificationPostTime = 0;
        mNotifyMode = NOTIFY_MODE_NONE;
    }

    /**
     * @return A card ID used to save and restore playlists, i.e., the queue.
     */
    private int getCardId() {
        final ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(Uri.parse("content://media/external/fs_id"), null, null,
                null, null);
        int mCardId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            mCardId = cursor.getInt(0);
            cursor.close();
            cursor = null;
        }
        return mCardId;
    }

    /**
     * Called when we receive a ACTION_MEDIA_EJECT notification.
     *
     * @param storagePath The path to mount point for the removed media
     */
    public void closeExternalStorageFiles(final String storagePath) {
        stop(true);
        notifyChange(Constants.QUEUE_CHANGED);
        notifyChange(Constants.META_CHANGED);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications. The
     * intent will call closeExternalStorageFiles() if the external media is
     * going to be ejected, so applications can clean up any files they have
     * open.
     */
    public void registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
            mUnmountReceiver = new BroadcastReceiver() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    final String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                        saveQueue(true);
                        mQueueIsSaveable = false;
                        closeExternalStorageFiles(intent.getData().getPath());
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        mMediaMountedCount++;
                        mCardId = getCardId();
                        reloadQueue();
                        mQueueIsSaveable = true;
                        notifyChange(Constants.QUEUE_CHANGED);
                        notifyChange(Constants.META_CHANGED);
                    }
                }
            };
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addDataScheme("file");
            registerReceiver(mUnmountReceiver, filter);
        }
    }

    private void scheduleDelayedShutdown() {
        LogUtils.d(TAG, "Scheduling shutdown in " + Constants.IDLE_DELAY + " ms");
        if (!mReadGranted) {
            return;
        }
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + Constants.IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }

    private void cancelShutdown() {
        LogUtils.d(TAG, "Cancelling delayed shutdown, scheduled = " + mShutdownScheduled);
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }

    /**
     * Stops playback
     *
     * @param goToIdle True to go to the idle state, false otherwise
     */
    private void stop(final boolean goToIdle) {
        LogUtils.d(TAG, "Stopping playback, goToIdle = " + goToIdle);
        if (mPlayer.isInitialized()) {
            mPlayer.stop();
        }
        mFileToPlay = null;
        closeCursor();
        if (goToIdle) {
            setIsSupposedToBePlaying(false, false);
        } else {
            stopForeground(false);
        }
    }

    /**
     * Removes the range of tracks specified from the play list. If a file
     * within the range is the file currently being played, playback will move
     * to the next file after the range.
     *
     * @param first The first file to be removed
     * @param last  The last file to be removed
     * @return the number of tracks deleted
     */
    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first) {
                return 0;
            } else if (first < 0) {
                first = 0;
            } else if (last >= mPlaylist.size()) {
                last = mPlaylist.size() - 1;
            }

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= last - first + 1;
            }
            final int numToRemove = last - first + 1;

            if (first == 0 && last == mPlaylist.size() - 1) {
                mPlayPos = -1;
                mNextPlayPos = -1;
                mPlaylist.clear();
                mHistory.clear();
            } else {
                for (int i = 0; i < numToRemove; i++) {
                    mPlaylist.remove(first);
                }

                // remove the items from the history
                // this is not ideal as the history shouldn't be impacted by this
                // but since we are removing items from the array, it will throw
                // an exception if we keep it around.  Idealistically with the queue
                // rewrite this should be all be fixed
                // https://cyanogen.atlassian.net/browse/MUSIC-44
                ListIterator<Integer> positionIterator = mHistory.listIterator();
                while (positionIterator.hasNext()) {
                    int pos = positionIterator.next();
                    if (pos >= first && pos <= last) {
                        positionIterator.remove();
                    } else if (pos > last) {
                        positionIterator.set(pos - numToRemove);
                    }
                }
            }
            if (gotonext) {
                if (mPlaylist.size() == 0) {
                    stop(true);
                    mPlayPos = -1;
                    closeCursor();
                } else {
                    if (mShuffleMode != Constants.SHUFFLE_NONE) {
                        mPlayPos = getNextPosition(true);
                    } else if (mPlayPos >= mPlaylist.size()) {
                        mPlayPos = 0;
                    }
                    final boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrentAndNext();
                    if (wasPlaying) {
                        play();
                    }
                }
                notifyChange(Constants.META_CHANGED);
            }
            return last - first + 1;
        }
    }

    /**
     * Adds a list to the playlist
     *
     * @param list     The list to add
     * @param position The position to place the tracks
     */
    private void addToPlayList(final long[] list, int position, long sourceId) {
        final int addlen = list.length;
        if (position < 0) {
            mPlaylist.clear();
            position = 0;
        }

        mPlaylist.ensureCapacity(mPlaylist.size() + addlen);
        if (position > mPlaylist.size()) {
            position = mPlaylist.size();
        }

        final ArrayList<MusicPlaybackTrack> arrayList = new ArrayList<MusicPlaybackTrack>(addlen);
        for (int i = 0; i < list.length; i++) {
            arrayList.add(new MusicPlaybackTrack(list[i], sourceId, i));
        }

        mPlaylist.addAll(position, arrayList);

        if (mPlaylist.size() == 0) {
            closeCursor();
            notifyChange(Constants.META_CHANGED);
        }
    }

    /**
     * @param trackId The track ID
     */
    private void updateCursor(final long trackId) {
        updateCursor("_id=" + trackId, null);
    }

    private void updateCursor(final String selection, final String[] selectionArgs) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Constants.PROJECTION, selection, selectionArgs);
        }
        updateAlbumCursor();
    }

    private void updateCursor(final Uri uri) {
        synchronized (this) {
            closeCursor();
            mCursor = openCursorAndGoToFirst(uri, Constants.PROJECTION, null, null);
        }
        updateAlbumCursor();
    }

    private void updateAlbumCursor() {
        long albumId = getAlbumId();
        if (albumId >= 0) {
            mAlbumCursor = openCursorAndGoToFirst(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    Constants.ALBUM_PROJECTION, "_id=" + albumId, null);
        } else {
            mAlbumCursor = null;
        }
    }

    private Cursor openCursorAndGoToFirst(Uri uri, String[] projection,
                                          String selection, String[] selectionArgs) {
        Cursor c = getContentResolver().query(uri, projection,
                selection, selectionArgs, null, null);
        if (c == null) {
            return null;
        }
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        return c;
    }

    private synchronized void closeCursor() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        if (mAlbumCursor != null) {
            mAlbumCursor.close();
            mAlbumCursor = null;
        }
    }

    /**
     * Called to open a new file as the current track and prepare the next for
     * playback
     */
    private void openCurrentAndNext() {
        openCurrentAndMaybeNext(true);
    }

    /**
     * Called to open a new file as the current track and prepare the next for
     * playback
     *
     * @param openNext True to prepare the next track for playback, false
     *                 otherwise.
     */
    private void openCurrentAndMaybeNext(final boolean openNext) {
        synchronized (this) {
            closeCursor();

            if (mPlaylist.size() == 0) {
                return;
            }
            stop(false);

            boolean shutdown = false;

            updateCursor(mPlaylist.get(mPlayPos).mId);
            while (true) {
                if (mCursor != null
                        && openFile(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/"
                        + mCursor.getLong(Constants.IDCOLIDX))) {
                    break;
                }

                // if we get here then opening the file failed. We can close the
                // cursor now, because
                // we're either going to create a new one next, or stop trying
                closeCursor();
                if (mOpenFailedCounter++ < 10 && mPlaylist.size() > 1) {
                    final int pos = getNextPosition(false);
                    if (pos < 0) {
                        shutdown = true;
                        break;
                    }
                    mPlayPos = pos;
                    stop(false);
                    mPlayPos = pos;
                    updateCursor(mPlaylist.get(mPlayPos).mId);
                } else {
                    mOpenFailedCounter = 0;
                    LogUtils.e(TAG, "Failed to open file for playback");
                    shutdown = true;
                    break;
                }
            }

            if (shutdown) {
                scheduleDelayedShutdown();
                if (mIsSupposedToBePlaying) {
                    mIsSupposedToBePlaying = false;
                    notifyChange(Constants.PLAYSTATE_CHANGED);
                }
            } else if (openNext) {
                setNextTrack();
            }
        }
    }

    private void sendErrorMessage(final String trackName) {
        final Intent i = new Intent(Constants.TRACK_ERROR);
        i.putExtra(Constants.TRACK_NAME, trackName);
        sendBroadcast(i);
    }

    /**
     * @param force True to force the player onto the track next, false
     *              otherwise.
     * @return The next position to play.
     */
    private int getNextPosition(final boolean force) {
        // as a base case, if the playlist is empty just return -1
        if (mPlaylist == null || mPlaylist.isEmpty()) {
            return -1;
        }
        // if we're not forced to go to the next track and we are only playing the current track
        if (!force && mRepeatMode == Constants.REPEAT_CURRENT) {
            if (mPlayPos < 0) {
                return 0;
            }
            return mPlayPos;
        } else if (mShuffleMode == Constants.SHUFFLE_NORMAL) {
            final int numTracks = mPlaylist.size();

            // count the number of times a track has been played
            final int[] trackNumPlays = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                // set it all to 0
                trackNumPlays[i] = 0;
            }

            // walk through the history and add up the number of times the track
            // has been played
            final int numHistory = mHistory.size();
            for (int i = 0; i < numHistory; i++) {
                final int idx = mHistory.get(i).intValue();
                if (idx >= 0 && idx < numTracks) {
                    trackNumPlays[idx]++;
                }
            }

            // also add the currently playing track to the count
            if (mPlayPos >= 0 && mPlayPos < numTracks) {
                trackNumPlays[mPlayPos]++;
            }

            // figure out the least # of times a track has a played as well as
            // how many tracks share that count
            int minNumPlays = Integer.MAX_VALUE;
            int numTracksWithMinNumPlays = 0;
            for (int i = 0; i < trackNumPlays.length; i++) {
                // if we found a new track that has less number of plays, reset the counters
                if (trackNumPlays[i] < minNumPlays) {
                    minNumPlays = trackNumPlays[i];
                    numTracksWithMinNumPlays = 1;
                } else if (trackNumPlays[i] == minNumPlays) {
                    // increment this track shares the # of tracks
                    numTracksWithMinNumPlays++;
                }
            }

            // if we've played each track at least once and all tracks have been played an equal
            // # of times and we aren't repeating all and we're not forcing a track, then
            // return no more tracks
            if (minNumPlays > 0 && numTracksWithMinNumPlays == numTracks
                    && mRepeatMode != Constants.REPEAT_ALL && !force) {
                return -1;
            }

            // else pick a track from the least number of played tracks
            int skip = mShuffler.nextInt(numTracksWithMinNumPlays);
            for (int i = 0; i < trackNumPlays.length; i++) {
                if (trackNumPlays[i] == minNumPlays) {
                    if (skip == 0) {
                        return i;
                    } else {
                        skip--;
                    }
                }
            }

            // Unexpected to land here
            LogUtils.e(TAG, "Getting the next position resulted did not get a result when it should have");
            return -1;
        } else if (mShuffleMode == Constants.SHUFFLE_AUTO) {
            doAutoShuffleUpdate();
            return mPlayPos + 1;
        } else {
            if (mPlayPos >= mPlaylist.size() - 1) {
                if (mRepeatMode == Constants.REPEAT_NONE && !force) {
                    return -1;
                } else if (mRepeatMode == Constants.REPEAT_ALL || force) {
                    return 0;
                }
                return -1;
            } else {
                return mPlayPos + 1;
            }
        }
    }

    /**
     * Sets the track to be played
     */
    private void setNextTrack() {
        setNextTrack(getNextPosition(false));
    }

    /**
     * Sets the next track to be played
     *
     * @param position the target position we want
     */
    private void setNextTrack(int position) {
        mNextPlayPos = position;
        LogUtils.d(TAG, "setNextTrack: next play position = " + mNextPlayPos);
        if (mNextPlayPos >= 0 && mPlaylist != null && mNextPlayPos < mPlaylist.size()) {
            final long id = mPlaylist.get(mNextPlayPos).mId;
            mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
        } else {
            mPlayer.setNextDataSource(null);
        }
    }

    /**
     * Creates a shuffled playlist used for party mode
     */
    private boolean makeAutoShuffleList() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media._ID
                    }, MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
            if (cursor == null || cursor.getCount() == 0) {
                return false;
            }
            final int len = cursor.getCount();
            final long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                cursor.moveToNext();
                list[i] = cursor.getLong(0);
            }
            mAutoShuffleList = list;
            return true;
        } catch (final RuntimeException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    /**
     * Creates the party shuffle playlist
     */
    private void doAutoShuffleUpdate() {
        boolean notify = false;
        if (mPlayPos > 10) {
            removeTracks(0, mPlayPos - 9);
            notify = true;
        }
        final int toAdd = 7 - (mPlaylist.size() - (mPlayPos < 0 ? -1 : mPlayPos));
        for (int i = 0; i < toAdd; i++) {
            int lookback = mHistory.size();
            int idx = -1;
            while (true) {
                idx = mShuffler.nextInt(mAutoShuffleList.length);
                if (!wasRecentlyUsed(idx, lookback)) {
                    break;
                }
                lookback /= 2;
            }
            mHistory.add(idx);
            if (mHistory.size() > Constants.MAX_HISTORY_SIZE) {
                mHistory.remove(0);
            }
            mPlaylist.add(new MusicPlaybackTrack(mAutoShuffleList[idx], -1, -1));
            notify = true;
        }
        if (notify) {
            notifyChange(Constants.QUEUE_CHANGED);
        }
    }

    private boolean wasRecentlyUsed(final int idx, int lookbacksize) {
        if (lookbacksize == 0) {
            return false;
        }
        final int histsize = mHistory.size();
        if (histsize < lookbacksize) {
            lookbacksize = histsize;
        }
        final int maxidx = histsize - 1;
        for (int i = 0; i < lookbacksize; i++) {
            final long entry = mHistory.get(maxidx - i);
            if (entry == idx) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notify the change-receivers that something has changed.
     */
    private void notifyChange(final String what) {
        LogUtils.d(TAG, "notifyChange: what = " + what);

        // Update the lockscreen controls
        updateMediaSession(what);

        if (what.equals(Constants.POSITION_CHANGED)) {
            return;
        }

        final Intent intent = new Intent(what);
        intent.putExtra("id", getAudioId());
        intent.putExtra("artist", getArtistName());
        intent.putExtra("album", getAlbumName());
        intent.putExtra("track", getTrackName());
        intent.putExtra("playing", isPlaying());

        if (Constants.NEW_LYRICS.equals(what)) {
            intent.putExtra("lyrics", mLyrics);
        }

        sendStickyBroadcast(intent);

        final Intent musicIntent = new Intent(intent);
        musicIntent.setAction(what.replace(Constants.ELEVEN_PACKAGE_NAME, Constants.MUSIC_PACKAGE_NAME));
        sendStickyBroadcast(musicIntent);

        if (what.equals(Constants.META_CHANGED)) {
            // Add the track to the recently played list.
           /* mRecentsCache.addSongId(getAudioId());

            mSongPlayCountCache.bumpSongCount(getAudioId());*/
        } else if (what.equals(Constants.QUEUE_CHANGED)) {
            saveQueue(true);
            if (isPlaying()) {
                // if we are in shuffle mode and our next track is still valid,
                // try to re-use the track
                // We need to reimplement the queue to prevent hacky solutions like this
                // https://cyanogen.atlassian.net/browse/MUSIC-175
                // https://cyanogen.atlassian.net/browse/MUSIC-44
                if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size()
                        && getShuffleMode() != Constants.SHUFFLE_NONE) {
                    setNextTrack(mNextPlayPos);
                } else {
                    setNextTrack();
                }
            }
        } else {
            saveQueue(false);
        }

        if (what.equals(Constants.PLAYSTATE_CHANGED)) {
            updateNotification();
        }

        // Update the app-widgets
        /*mAppWidgetSmall.notifyChange(this, what);
        mAppWidgetLarge.notifyChange(this, what);
        mAppWidgetLargeAlternate.notifyChange(this, what);*/
    }

    private void updateMediaSession(final String what) {
        int playState = mIsSupposedToBePlaying
                ? PlaybackStateCompat.STATE_PLAYING
                : PlaybackStateCompat.STATE_PAUSED;

        long playBackStateActions = PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_STOP;

        if (what.equals(Constants.PLAYSTATE_CHANGED) || what.equals(Constants.POSITION_CHANGED)) {
            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setActions(playBackStateActions)
                    .setActiveQueueItemId(getAudioId())
                    .setState(playState, position(), 1.0f).build());
        } else if (what.equals(Constants.META_CHANGED) || what.equals(Constants.QUEUE_CHANGED)) {
            Bitmap albumArt = ImageLoader.getArtworkByAlbumId(this, getAlbumId());
            if (albumArt != null) {
                // RemoteControlClient wants to recycle the bitmaps thrown at it, so we need
                // to make sure not to hand out our cache copy
                Bitmap.Config config = albumArt.getConfig();
                if (config == null) {
                    config = Bitmap.Config.ARGB_8888;
                }
                albumArt = albumArt.copy(config, false);
            }

            mSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getArtistName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, getAlbumArtistName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, getAlbumName())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, getTrackName())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration())
                    .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, getQueuePosition() + 1)
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getQueue().length)
                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, getGenreName())
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                            mShowAlbumArtOnLockscreen ? albumArt : null)
                    .build());

            if (what.equals(Constants.QUEUE_CHANGED)) {
                updateMediaSessionQueue();
            }

            mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setActions(playBackStateActions)
                    .setActiveQueueItemId(getAudioId())
                    .setState(playState, position(), 1.0f).build());
        }
    }

    private synchronized void updateMediaSessionQueue() {
        if (mQueueUpdateTask != null) {
            mQueueUpdateTask.cancel(true);
        }
        mQueueUpdateTask = new QueueUpdateTask(this, getQueue());
        mQueueUpdateTask.execute();
    }

    private Notification buildNotification() {
        final String albumName = getAlbumName();
        final String artistName = getArtistName();
        final boolean isPlaying = isPlaying();
        String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " - " + albumName;

        int playButtonResId = isPlaying
                ? R.drawable.ic_pause_notification : R.drawable.ic_play_notification;
        int playButtonTitleResId = isPlaying
                ? R.string.notification_pause : R.string.notification_play;

        Intent nowPlayingIntent = new Intent("com.koma.music.notification.AUDIO_PLAYER")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, 0);

        Bitmap artwork;
        artwork = ImageLoader.getArtworkByAlbumId(this, getAlbumId());

        if (artwork == null) {
            //artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.icon_album_default);
        }
        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notification)
                .setLargeIcon(artwork)
                .setContentIntent(clickIntent)
                .setContentTitle(getTrackName())
                .setContentText(text)
                .setWhen(mNotificationPostTime)
                .setShowWhen(false)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_previous_notification,
                        getString(R.string.notification_prev),
                        retrievePlaybackAction(Constants.PREVIOUS_ACTION))
                .addAction(playButtonResId, getString(playButtonTitleResId),
                        retrievePlaybackAction(Constants.TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.ic_next_notification,
                        getString(R.string.notification_next),
                        retrievePlaybackAction(Constants.NEXT_ACTION));

        builder.setColor(Palette.from(artwork).generate().getMutedColor(
                getResources().getColor(R.color.colorPrimary)));
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        android.support.v7.app.NotificationCompat.MediaStyle style =
                new android.support.v7.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2, 3);
        builder.setStyle(style);
        return builder.build();
    }

    private final PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(this, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }

    /**
     * Saves the queue
     *
     * @param full True if the queue is full
     */
    private void saveQueue(final boolean full) {
        if (!mQueueIsSaveable || mPreferences == null) {
            return;
        }

        final SharedPreferences.Editor editor = mPreferences.edit();
        if (full) {
            mPlaybackStateStore.saveState(mPlaylist,
                    mShuffleMode != Constants.SHUFFLE_NONE ? mHistory : null);
            editor.putInt("cardid", mCardId);
        }
        editor.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
            editor.putLong("seekpos", mPlayer.position());
        }
        editor.putInt("repeatmode", mRepeatMode);
        editor.putInt("shufflemode", mShuffleMode);
        editor.apply();
    }

    /**
     * Reloads the queue as the user left it the last time they stopped using
     * Apollo
     */
    private void reloadQueue() {
        int id = mCardId;
        if (mPreferences.contains("cardid")) {
            id = mPreferences.getInt("cardid", ~mCardId);
        }
        if (id == mCardId) {
            mPlaylist = mPlaybackStateStore.getQueue();
        }
        if (mPlaylist.size() > 0) {
            final int pos = mPreferences.getInt("curpos", 0);
            if (pos < 0 || pos >= mPlaylist.size()) {
                mPlaylist.clear();
                return;
            }
            mPlayPos = pos;
            updateCursor(mPlaylist.get(mPlayPos).mId);
            if (mCursor == null) {
                SystemClock.sleep(3000);
                updateCursor(mPlaylist.get(mPlayPos).mId);
            }
            synchronized (this) {
                closeCursor();
                mOpenFailedCounter = 20;
                openCurrentAndNext();
            }
            if (!mPlayer.isInitialized()) {
                mPlaylist.clear();
                return;
            }

            final long seekpos = mPreferences.getLong("seekpos", 0);
            seek(seekpos >= 0 && seekpos < duration() ? seekpos : 0);
            LogUtils.d(TAG, "restored queue, currently at position "
                    + position() + "/" + duration()
                    + " (requested " + seekpos + ")");

            int repmode = mPreferences.getInt("repeatmode", Constants.REPEAT_NONE);
            if (repmode != Constants.REPEAT_ALL && repmode != Constants.REPEAT_CURRENT) {
                repmode = Constants.REPEAT_NONE;
            }
            mRepeatMode = repmode;

            int shufmode = mPreferences.getInt("shufflemode", Constants.SHUFFLE_NONE);
            if (shufmode != Constants.SHUFFLE_AUTO && shufmode != Constants.SHUFFLE_NORMAL) {
                shufmode = Constants.SHUFFLE_NONE;
            }
            if (shufmode != Constants.SHUFFLE_NONE) {
                mHistory = mPlaybackStateStore.getHistory(mPlaylist.size());
            }
            if (shufmode == Constants.SHUFFLE_AUTO) {
                if (!makeAutoShuffleList()) {
                    shufmode = Constants.SHUFFLE_NONE;
                }
            }
            mShuffleMode = shufmode;
        }
    }

    /**
     * Opens a file and prepares it for playback
     *
     * @param path The path of the file to open
     */
    public boolean openFile(final String path) {
        LogUtils.d(TAG, "openFile: path = " + path);
        synchronized (this) {
            if (path == null) {
                return false;
            }

            // If mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {
                Uri uri = Uri.parse(path);
                boolean shouldAddToPlaylist = true;     // should try adding audio info to playlist
                long id = -1;
                try {
                    id = Long.valueOf(uri.getLastPathSegment());
                } catch (NumberFormatException ex) {
                    // Ignore
                }

                if (id != -1 && path.startsWith(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())) {
                    updateCursor(uri);

                } else if (id != -1 && path.startsWith(
                        MediaStore.Files.getContentUri("external").toString())) {
                    updateCursor(id);

                    // handle downloaded media files
                } else if (path.startsWith("content://downloads/")) {

                    // extract MediaProvider(MP) uri , if available
                    // Downloads.Impl.COLUMN_MEDIAPROVIDER_URI
                    String mpUri = getValueForDownloadedFile(this, uri, "mediaprovider_uri");
                    LogUtils.i(TAG, "Downloaded file's MP uri : " + mpUri);
                    if (!TextUtils.isEmpty(mpUri)) {
                        // if mpUri is valid, play that URI instead
                        if (openFile(mpUri)) {
                            // notify impending change in track
                            notifyChange(Constants.META_CHANGED);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        // create phantom cursor with download info, if a MP uri wasn't found
                        updateCursorForDownloadedFile(this, uri);
                        shouldAddToPlaylist = false;    // song info isn't available in MediaStore
                    }

                } else {
                    // assuming a "file://" uri by this point ...
                    String where = MediaStore.Audio.Media.DATA + "=?";
                    String[] selectionArgs = new String[]{path};
                    updateCursor(where, selectionArgs);
                }
                try {
                    if (mCursor != null && shouldAddToPlaylist) {
                        mPlaylist.clear();
                        mPlaylist.add(new MusicPlaybackTrack(
                                mCursor.getLong(Constants.IDCOLIDX), -1, -1));
                        // propagate the change in playlist state
                        notifyChange(Constants.QUEUE_CHANGED);
                        mPlayPos = 0;
                        mHistory.clear();
                    }
                } catch (final UnsupportedOperationException ex) {
                    // Ignore
                }
            }

            mFileToPlay = path;
            mPlayer.setDataSource(mFileToPlay);
            if (mPlayer.isInitialized()) {
                mOpenFailedCounter = 0;
                return true;
            }

            String trackName = getTrackName();
            if (TextUtils.isEmpty(trackName)) {
                trackName = path;
            }
            sendErrorMessage(trackName);

            stop(true);
            return false;
        }
    }

    /*
        Columns for a pseudo cursor we are creating for downloaded songs
        Modeled after mCursor to be able to respond to respond to the same queries as it
     */
    private static final String[] PROJECTION_MATRIX = new String[]{
            "_id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };

    /**
     * Creates a pseudo cursor for downloaded audio files with minimal info
     *
     * @param context needed to query the download uri
     * @param uri     the uri of the downloaded file
     */
    private void updateCursorForDownloadedFile(Context context, Uri uri) {
        synchronized (this) {
            closeCursor();  // clear mCursor
            MatrixCursor cursor = new MatrixCursor(PROJECTION_MATRIX);
            // get title of the downloaded file ; Downloads.Impl.COLUMN_TITLE
            String title = getValueForDownloadedFile(this, uri, "title");
            // populating the cursor with bare minimum info
            cursor.addRow(new Object[]{
                    null,
                    null,
                    null,
                    title,
                    null,
                    null,
                    null,
                    null
            });
            mCursor = cursor;
            mCursor.moveToFirst();
        }
    }

    /**
     * Query the DownloadProvider to get the value in the specified column
     *
     * @param context
     * @param uri     the uri of the downloaded file
     * @param column
     * @return
     */
    private String getValueForDownloadedFile(Context context, Uri uri, String column) {

        Cursor cursor = null;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Returns the audio session ID
     *
     * @return The current media player audio session ID
     */
    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }

    /**
     * Indicates if the media storeage device has been mounted or not
     *
     * @return 1 if Intent.ACTION_MEDIA_MOUNTED is called, 0 otherwise
     */
    public int getMediaMountedCount() {
        return mMediaMountedCount;
    }

    /**
     * Returns the shuffle mode
     *
     * @return The current shuffle mode (all, party, none)
     */
    public int getShuffleMode() {
        return mShuffleMode;
    }

    /**
     * Returns the repeat mode
     *
     * @return The current repeat mode (all, one, none)
     */
    public int getRepeatMode() {
        return mRepeatMode;
    }

    /**
     * Removes all instances of the track with the given ID from the playlist.
     *
     * @param id The id to be removed
     * @return how many instances of the track were removed
     */
    public int removeTrack(final long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlaylist.size(); i++) {
                if (mPlaylist.get(i).mId == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }
        }
        if (numremoved > 0) {
            notifyChange(Constants.QUEUE_CHANGED);
        }
        return numremoved;
    }

    /**
     * Removes a song from the playlist at the specified position.
     *
     * @param id       The song id to be removed
     * @param position The position of the song in the playlist
     * @return true if successful
     */
    public boolean removeTrackAtPosition(final long id, final int position) {
        synchronized (this) {
            if (position >= 0 &&
                    position < mPlaylist.size() &&
                    mPlaylist.get(position).mId == id) {

                return removeTracks(position, position) > 0;
            }
        }
        return false;
    }

    /**
     * Removes the range of tracks specified from the play list. If a file
     * within the range is the file currently being played, playback will move
     * to the next file after the range.
     *
     * @param first The first file to be removed
     * @param last  The last file to be removed
     * @return the number of tracks deleted
     */
    public int removeTracks(final int first, final int last) {
        final int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(Constants.QUEUE_CHANGED);
        }
        return numremoved;
    }

    /**
     * Returns the position in the queue
     *
     * @return the current position in the queue
     */
    public int getQueuePosition() {
        synchronized (this) {
            return mPlayPos;
        }
    }

    /**
     * @return the size of the queue history cache
     */
    public int getQueueHistorySize() {
        synchronized (this) {
            return mHistory.size();
        }
    }

    /**
     * @return the position in the history
     */
    public int getQueueHistoryPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mHistory.size()) {
                return mHistory.get(position);
            }
        }

        return -1;
    }

    /**
     * @return the queue of history positions
     */
    public int[] getQueueHistoryList() {
        synchronized (this) {
            int[] history = new int[mHistory.size()];
            for (int i = 0; i < mHistory.size(); i++) {
                history[i] = mHistory.get(i);
            }

            return history;
        }
    }

    /**
     * Returns the path to current song
     *
     * @return The path to the current song
     */
    public String getPath() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA));
        }
    }

    /**
     * Returns the album name
     *
     * @return The current song album Name
     */
    public String getAlbumName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM));
        }
    }

    /**
     * Returns the song name
     *
     * @return The current song name
     */
    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));
        }
    }

    /**
     * Returns the genre name of song
     *
     * @return The current song genre name
     */
    public String getGenreName() {
        synchronized (this) {
            if (mCursor == null || mPlayPos < 0 || mPlayPos >= mPlaylist.size()) {
                return null;
            }
            String[] genreProjection = {MediaStore.Audio.Genres.NAME};
            Uri genreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external",
                    (int) mPlaylist.get(mPlayPos).mId);
            Cursor genreCursor = getContentResolver().query(genreUri, genreProjection,
                    null, null, null);
            if (genreCursor != null) {
                try {
                    if (genreCursor.moveToFirst()) {
                        return genreCursor.getString(
                                genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME));
                    }
                } finally {
                    genreCursor.close();
                }
            }
            return null;
        }
    }

    /**
     * Returns the artist name
     *
     * @return The current song artist name
     */
    public String getArtistName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));
        }
    }

    /**
     * Returns the artist name
     *
     * @return The current song artist name
     */
    public String getAlbumArtistName() {
        synchronized (this) {
            if (mAlbumCursor == null) {
                return null;
            }
            return mAlbumCursor.getString(mAlbumCursor.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ARTIST));
        }
    }

    /**
     * Returns the album ID
     *
     * @return The current song album ID
     */
    public long getAlbumId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID));
        }
    }

    /**
     * Returns the artist ID
     *
     * @return The current song artist ID
     */
    public long getArtistId() {
        synchronized (this) {
            if (mCursor == null) {
                return -1;
            }
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID));
        }
    }

    /**
     * @return The audio id of the track
     */
    public long getAudioId() {
        MusicPlaybackTrack track = getCurrentTrack();
        if (track != null) {
            return track.mId;
        }

        return -1;
    }

    /**
     * Gets the currently playing music track
     */
    public MusicPlaybackTrack getCurrentTrack() {
        return getTrack(mPlayPos);
    }

    /**
     * Gets the music track from the queue at the specified index
     *
     * @param index position
     * @return music track or null
     */
    public synchronized MusicPlaybackTrack getTrack(int index) {
        if (index >= 0 && index < mPlaylist.size() && mPlayer.isInitialized()) {
            return mPlaylist.get(index);
        }

        return null;
    }

    /**
     * Returns the next audio ID
     *
     * @return The next track ID
     */
    public long getNextAudioId() {
        synchronized (this) {
            if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size() && mPlayer.isInitialized()) {
                return mPlaylist.get(mNextPlayPos).mId;
            }
        }
        return -1;
    }

    /**
     * Returns the previous audio ID
     *
     * @return The previous track ID
     */
    public long getPreviousAudioId() {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                int pos = getPreviousPlayPosition(false);
                if (pos >= 0 && pos < mPlaylist.size()) {
                    return mPlaylist.get(pos).mId;
                }
            }
        }
        return -1;
    }

    /**
     * Seeks the current track to a specific time
     *
     * @param position The time to seek to
     * @return The time to play the track at
     */
    public long seek(long position) {
        if (mPlayer.isInitialized()) {
            if (position < 0) {
                position = 0;
            } else if (position > mPlayer.duration()) {
                position = mPlayer.duration();
            }
            long result = mPlayer.seek(position);
            notifyChange(Constants.POSITION_CHANGED);
            return result;
        }
        return -1;
    }

    /**
     * Seeks the current track to a position relative to its current position
     * If the relative position is after or before the track, it will also automatically
     * jump to the previous or next track respectively
     *
     * @param deltaInMs The delta time to seek to in milliseconds
     */
    public void seekRelative(long deltaInMs) {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                final long newPos = position() + deltaInMs;
                final long duration = duration();
                if (newPos < 0) {
                    prev(true);
                    // seek to the new duration + the leftover position
                    seek(duration() + newPos);
                } else if (newPos >= duration) {
                    gotoNext(true);
                    // seek to the leftover duration
                    seek(newPos - duration);
                } else {
                    seek(newPos);
                }
            }
        }
    }

    /**
     * Returns the current position in time of the currenttrack
     *
     * @return The current playback position in miliseconds
     */
    public long position() {
        if (mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    /**
     * Returns the full duration of the current track
     *
     * @return The duration of the current track in miliseconds
     */
    public long duration() {
        if (mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    /**
     * Returns the queue
     *
     * @return The queue as a long[]
     */
    public long[] getQueue() {
        synchronized (this) {
            final int len = mPlaylist.size();
            final long[] list = new long[len];
            for (int i = 0; i < len; i++) {
                list[i] = mPlaylist.get(i).mId;
            }
            return list;
        }
    }

    /**
     * Gets the track id at a given position in the queue
     *
     * @param position
     * @return track id in the queue position
     */
    public long getQueueItemAtPosition(int position) {
        synchronized (this) {
            if (position >= 0 && position < mPlaylist.size()) {
                return mPlaylist.get(position).mId;
            }
        }

        return -1;
    }

    /**
     * @return the size of the queue
     */
    public int getQueueSize() {
        synchronized (this) {
            return mPlaylist.size();
        }
    }

    /**
     * @return True if music is playing, false otherwise
     */
    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    /**
     * Helper function to wrap the logic around mIsSupposedToBePlaying for consistentcy
     *
     * @param value  to set mIsSupposedToBePlaying to
     * @param notify whether we want to fire PLAYSTATE_CHANGED event
     */
    private void setIsSupposedToBePlaying(boolean value, boolean notify) {
        if (mIsSupposedToBePlaying != value) {
            mIsSupposedToBePlaying = value;

            // Update mLastPlayed time first and notify afterwards, as
            // the notification listener method needs the up-to-date value
            // for the recentlyPlayed() method to work
            if (!mIsSupposedToBePlaying) {
                scheduleDelayedShutdown();
                mLastPlayedTime = System.currentTimeMillis();
            }

            if (notify) {
                notifyChange(Constants.PLAYSTATE_CHANGED);
            }
        }
    }

    /**
     * @return true if is playing or has played within the last IDLE_DELAY time
     */
    private boolean recentlyPlayed() {
        return isPlaying() || System.currentTimeMillis() - mLastPlayedTime < Constants.IDLE_DELAY;
    }

    /**
     * Opens a list for playback
     *
     * @param list     The list of tracks to open
     * @param position The position to start playback at
     */
    public void open(final long[] list, final int position, long sourceId) {
        synchronized (this) {
            if (mShuffleMode == Constants.SHUFFLE_AUTO) {
                mShuffleMode = Constants.SHUFFLE_NORMAL;
            }
            final long oldId = getAudioId();
            final int listlength = list.length;
            boolean newlist = true;
            if (mPlaylist.size() == listlength) {
                newlist = false;
                for (int i = 0; i < listlength; i++) {
                    if (list[i] != mPlaylist.get(i).mId) {
                        newlist = true;
                        break;
                    }
                }
            }
            if (newlist) {
                addToPlayList(list, -1, sourceId);
                notifyChange(Constants.QUEUE_CHANGED);
            }
            if (position >= 0) {
                mPlayPos = position;
            } else {
                mPlayPos = mShuffler.nextInt(mPlaylist.size());
            }
            mHistory.clear();
            openCurrentAndNext();
            if (oldId != getAudioId()) {
                notifyChange(Constants.META_CHANGED);
            }
        }
    }

    /**
     * Stops playback.
     */
    public void stop() {
        stopShakeDetector(false);
        stop(true);
    }

    /**
     * Resumes or starts playback.
     */
    public void play() {
        startShakeDetector();
        play(true);
    }

    /**
     * Resumes or starts playback.
     *
     * @param createNewNextTrack True if you want to figure out the next track, false
     *                           if you want to re-use the existing next track (used for going back)
     */
    public void play(boolean createNewNextTrack) {
        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        LogUtils.d(TAG, "Starting playback: audio focus request status = " + status);

        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

        mSession.setActive(true);

        if (createNewNextTrack) {
            setNextTrack();
        } else {
            setNextTrack(mNextPlayPos);
        }

        if (mPlayer.isInitialized()) {
            final long duration = mPlayer.duration();
            if (mRepeatMode != Constants.REPEAT_CURRENT && duration > 2000
                    && mPlayer.position() >= duration - 2000) {
                gotoNext(true);
            }

            mPlayer.start();
            mPlayerHandler.removeMessages(Constants.FADEDOWN);
            mPlayerHandler.sendEmptyMessage(Constants.FADEUP);

            setIsSupposedToBePlaying(true, true);

            cancelShutdown();
            updateNotification();
        } else if (mPlaylist.size() <= 0) {
            setShuffleMode(Constants.SHUFFLE_AUTO);
        }
    }

    private void togglePlayPause() {
        if (isPlaying()) {
            pause();
            mPausedByTransientLossOfFocus = false;
        } else {
            play();
        }
    }

    /**
     * Temporarily pauses playback.
     */
    public void pause() {
        if (mPlayerHandler == null) return;
        LogUtils.d(TAG, "Pausing playback");
        synchronized (this) {
            if (mPlayerHandler != null) {
                mPlayerHandler.removeMessages(Constants.FADEUP);
            }
            if (mIsSupposedToBePlaying) {
                final Intent intent = new Intent(
                        AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
                sendBroadcast(intent);

                if (mPlayer != null) {
                    mPlayer.pause();
                }
                setIsSupposedToBePlaying(false, true);
                stopShakeDetector(false);
            }
        }
    }

    /**
     * Changes from the current track to the next track
     */
    public void gotoNext(final boolean force) {
        LogUtils.d(TAG, "Going to next track");
        synchronized (this) {
            if (mPlaylist.size() <= 0) {
                LogUtils.d(TAG, "No play queue");
                scheduleDelayedShutdown();
                return;
            }
            int pos = mNextPlayPos;
            if (pos < 0) {
                pos = getNextPosition(force);
            }

            if (pos < 0) {
                setIsSupposedToBePlaying(false, true);
                return;
            }

            stop(false);
            setAndRecordPlayPos(pos);
            openCurrentAndNext();
            play();
            notifyChange(Constants.META_CHANGED);
        }
    }

    public void setAndRecordPlayPos(int nextPos) {
        synchronized (this) {
            // save to the history
            if (mShuffleMode != Constants.SHUFFLE_NONE) {
                mHistory.add(mPlayPos);
                if (mHistory.size() > Constants.MAX_HISTORY_SIZE) {
                    mHistory.remove(0);
                }
            }

            mPlayPos = nextPos;
        }
    }

    /**
     * Changes from the current track to the previous played track
     */
    public void prev(boolean forcePrevious) {
        synchronized (this) {
            // if we aren't repeating 1, and we are either early in the song
            // or we want to force go back, then go to the prevous track
            boolean goPrevious = getRepeatMode() != Constants.REPEAT_CURRENT &&
                    (position() < Constants.REWIND_INSTEAD_PREVIOUS_THRESHOLD || forcePrevious);

            if (goPrevious) {
                LogUtils.d(TAG, "Going to previous track");
                int pos = getPreviousPlayPosition(true);
                // if we have no more previous tracks, quit
                if (pos < 0) {
                    return;
                }
                mNextPlayPos = mPlayPos;
                mPlayPos = pos;
                stop(false);
                openCurrent();
                play(false);
                notifyChange(Constants.META_CHANGED);
            } else {
                LogUtils.d(TAG, "Going to beginning of track");
                seek(0);
                play(false);
            }
        }
    }

    public int getPreviousPlayPosition(boolean removeFromHistory) {
        synchronized (this) {
            if (mShuffleMode == Constants.SHUFFLE_NORMAL) {
                // Go to previously-played track and remove it from the history
                final int histsize = mHistory.size();
                if (histsize == 0) {
                    return -1;
                }
                final Integer pos = mHistory.get(histsize - 1);
                if (removeFromHistory) {
                    mHistory.remove(histsize - 1);
                }
                return pos.intValue();
            } else {
                if (mPlayPos > 0) {
                    return mPlayPos - 1;
                } else {
                    return mPlaylist.size() - 1;
                }
            }
        }
    }

    /**
     * We don't want to open the current and next track when the user is using
     * the {@code #prev()} method because they won't be able to travel back to
     * the previously listened track if they're shuffling.
     */
    private void openCurrent() {
        openCurrentAndMaybeNext(false);
    }

    /**
     * Moves an item in the queue from one position to another
     *
     * @param index1 The position the item is currently at
     * @param index2 The position the item is being moved to
     */
    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= mPlaylist.size()) {
                index1 = mPlaylist.size() - 1;
            }
            if (index2 >= mPlaylist.size()) {
                index2 = mPlaylist.size() - 1;
            }

            if (index1 == index2) {
                return;
            }

            final MusicPlaybackTrack track = mPlaylist.remove(index1);
            if (index1 < index2) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index1 && mPlayPos <= index2) {
                    mPlayPos--;
                }
            } else if (index2 < index1) {
                mPlaylist.add(index2, track);
                if (mPlayPos == index1) {
                    mPlayPos = index2;
                } else if (mPlayPos >= index2 && mPlayPos <= index1) {
                    mPlayPos++;
                }
            }
            notifyChange(Constants.QUEUE_CHANGED);
        }
    }

    /**
     * Sets the repeat mode
     *
     * @param repeatmode The repeat mode to use
     */
    public void setRepeatMode(final int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
            setNextTrack();
            saveQueue(false);
            notifyChange(Constants.REPEATMODE_CHANGED);
        }
    }

    /**
     * Sets the shuffle mode
     *
     * @param shufflemode The shuffle mode to use
     */
    public void setShuffleMode(final int shufflemode) {
        synchronized (this) {
            if (mShuffleMode == shufflemode && mPlaylist.size() > 0) {
                return;
            }

            mShuffleMode = shufflemode;
            if (mShuffleMode == Constants.SHUFFLE_AUTO) {
                if (makeAutoShuffleList()) {
                    mPlaylist.clear();
                    doAutoShuffleUpdate();
                    mPlayPos = 0;
                    openCurrentAndNext();
                    play();
                    notifyChange(Constants.META_CHANGED);
                    return;
                } else {
                    mShuffleMode = Constants.SHUFFLE_NONE;
                }
            } else {
                setNextTrack();
            }
            saveQueue(false);
            notifyChange(Constants.SHUFFLEMODE_CHANGED);
        }
    }

    /**
     * Sets the position of a track in the queue
     *
     * @param index The position to place the track
     */
    public void setQueuePosition(final int index) {
        synchronized (this) {
            stop(false);
            mPlayPos = index;
            openCurrentAndNext();
            play();
            notifyChange(Constants.META_CHANGED);
            if (mShuffleMode == Constants.SHUFFLE_AUTO) {
                doAutoShuffleUpdate();
            }
        }
    }

    /**
     * Queues a new list for playback
     *
     * @param list   The list to queue
     * @param action The action to take
     */
    public void enqueue(final long[] list, final int action, long sourceId) {
        synchronized (this) {
            if (action == Constants.NEXT && mPlayPos + 1 < mPlaylist.size()) {
                addToPlayList(list, mPlayPos + 1, sourceId);
                mNextPlayPos = mPlayPos + 1;
                notifyChange(Constants.QUEUE_CHANGED);
            } else {
                addToPlayList(list, Integer.MAX_VALUE, sourceId);
                notifyChange(Constants.QUEUE_CHANGED);
            }

            if (mPlayPos < 0) {
                mPlayPos = 0;
                openCurrentAndNext();
                play();
                notifyChange(Constants.META_CHANGED);
            }
        }
    }

    /**
     * Cycles through the different repeat modes
     */
    private void cycleRepeat() {
        if (mRepeatMode == Constants.REPEAT_NONE) {
            setRepeatMode(Constants.REPEAT_ALL);
        } else if (mRepeatMode == Constants.REPEAT_ALL) {
            setRepeatMode(Constants.REPEAT_CURRENT);
            if (mShuffleMode != Constants.SHUFFLE_NONE) {
                setShuffleMode(Constants.SHUFFLE_NONE);
            }
        } else {
            setRepeatMode(Constants.REPEAT_NONE);
        }
    }

    /**
     * Cycles through the different shuffle modes
     */
    private void cycleShuffle() {
        if (mShuffleMode == Constants.SHUFFLE_NONE) {
            setShuffleMode(Constants.SHUFFLE_NORMAL);
            if (mRepeatMode == Constants.REPEAT_CURRENT) {
                setRepeatMode(Constants.REPEAT_ALL);
            }
        } else if (mShuffleMode == Constants.SHUFFLE_NORMAL || mShuffleMode == Constants.SHUFFLE_AUTO) {
            setShuffleMode(Constants.SHUFFLE_NONE);
        }
    }

    /**
     * Called when one of the lists should refresh or requery.
     */
    public void refresh() {
        notifyChange(Constants.REFRESH);
    }

    /**
     * Called when one of the playlists have changed (renamed, added/removed tracks)
     */
    public void playlistChanged() {
        notifyChange(Constants.PLAYLIST_CHANGED);
    }

    /**
     * Called to set the status of shake to play feature
     */
    public void setShakeToPlayEnabled(boolean enabled) {
        LogUtils.d(TAG, "ShakeToPlay status: " + enabled);
        if (enabled) {
            if (mShakeDetector == null) {
                mShakeDetector = new ShakeDetector(mShakeDetectorListener);
            }
            // if song is already playing, start listening immediately
            if (isPlaying()) {
                startShakeDetector();
            }
        } else {
            stopShakeDetector(true);
        }
    }

    /**
     * Called to set visibility of album art on lockscreen
     */
    public void setLockscreenAlbumArt(boolean enabled) {
        mShowAlbumArtOnLockscreen = enabled;
        notifyChange(Constants.META_CHANGED);
    }

    /**
     * Called to start listening to shakes
     */
    private void startShakeDetector() {
        if (mShakeDetector != null) {
            mShakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    /**
     * Called to stop listening to shakes
     */
    private void stopShakeDetector(final boolean destroyShakeDetector) {
        if (mShakeDetector != null) {
            mShakeDetector.stop();
        }
        if (destroyShakeDetector) {
            mShakeDetector = null;
            LogUtils.d(TAG, "ShakeToPlay destroyed!!!");
        }
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(final int focusChange) {
                    mPlayerHandler.obtainMessage(Constants.FOCUSCHANGE, focusChange, 0).sendToTarget();
                }
            };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String command = intent.getStringExtra(Constants.CMDNAME);

           /* if (AppWidgetSmall.CMDAPPWIDGETUPDATE.equals(command)) {
                final int[] small = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetSmall.performUpdate(MusicService.this, small);
            } else if (AppWidgetLarge.CMDAPPWIDGETUPDATE.equals(command)) {
                final int[] large = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetLarge.performUpdate(MusicService.this, large);
            } else if (AppWidgetLargeAlternate.CMDAPPWIDGETUPDATE.equals(command)) {
                final int[] largeAlt = intent
                        .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                mAppWidgetLargeAlternate.performUpdate(MusicService.this, largeAlt);
            } else {*/
            handleCommandIntent(intent);
            // }
        }
    };

    private static final class ServiceStub extends IMusicService.Stub {
        private WeakReference<MusicService> mService;

        public ServiceStub(MusicService musicService) {
            mService = new WeakReference<>(musicService);
        }

        @Override
        public void openFile(final String path) throws RemoteException {
            mService.get().openFile(path);
        }

        @Override
        public void open(final long[] list, final int position, long sourceId)
                throws RemoteException {
            mService.get().open(list, position, sourceId);
        }

        @Override
        public void stop() throws RemoteException {
            mService.get().stop();
        }

        @Override
        public void pause() throws RemoteException {
            mService.get().pause();
        }

        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {
            mService.get().prev(forcePrevious);
        }

        @Override
        public void next() throws RemoteException {
            mService.get().gotoNext(true);
        }

        @Override
        public void enqueue(final long[] list, final int action, long sourceId)
                throws RemoteException {
            mService.get().enqueue(list, action, sourceId);
        }

        @Override
        public void setQueuePosition(final int index) throws RemoteException {
            mService.get().setQueuePosition(index);
        }

        @Override
        public void setShuffleMode(final int shufflemode) throws RemoteException {
            mService.get().setShuffleMode(shufflemode);
        }

        @Override
        public void setRepeatMode(final int repeatmode) throws RemoteException {
            mService.get().setRepeatMode(repeatmode);
        }

        @Override
        public void moveQueueItem(final int from, final int to) throws RemoteException {
            mService.get().moveQueueItem(from, to);
        }

        @Override
        public void refresh() throws RemoteException {
            mService.get().refresh();
        }

        @Override
        public void playlistChanged() throws RemoteException {
            mService.get().playlistChanged();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.get().isPlaying();
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return mService.get().getQueue();
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return mService.get().getQueueItemAtPosition(position);
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return mService.get().getQueueSize();
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return mService.get().getQueueHistoryPosition(position);
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return mService.get().getQueueHistorySize();
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return mService.get().getQueueHistoryList();
        }

        @Override
        public long duration() throws RemoteException {
            return mService.get().duration();
        }

        @Override
        public long position() throws RemoteException {
            return mService.get().position();
        }

        @Override
        public long seek(final long position) throws RemoteException {
            return mService.get().seek(position);
        }

        @Override
        public void seekRelative(final long deltaInMs) throws RemoteException {
            mService.get().seekRelative(deltaInMs);
        }

        @Override
        public long getAudioId() throws RemoteException {
            return mService.get().getAudioId();
        }

        @Override
        public MusicPlaybackTrack getCurrentTrack() throws RemoteException {
            return mService.get().getCurrentTrack();
        }

        @Override
        public MusicPlaybackTrack getTrack(int index) throws RemoteException {
            return mService.get().getTrack(index);
        }


        @Override
        public long getNextAudioId() throws RemoteException {
            return mService.get().getNextAudioId();
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return mService.get().getPreviousAudioId();
        }

        @Override
        public long getArtistId() throws RemoteException {
            return mService.get().getArtistId();
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return mService.get().getAlbumId();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return mService.get().getArtistName();
        }

        @Override
        public String getTrackName() throws RemoteException {
            return mService.get().getTrackName();
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return mService.get().getAlbumName();
        }

        @Override
        public String getPath() throws RemoteException {
            return mService.get().getPath();
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return mService.get().getQueuePosition();
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return mService.get().getShuffleMode();
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return mService.get().getRepeatMode();
        }

        @Override
        public int removeTracks(final int first, final int last) throws RemoteException {
            return mService.get().removeTracks(first, last);
        }

        @Override
        public int removeTrack(final long id) throws RemoteException {
            return mService.get().removeTrack(id);
        }

        @Override
        public boolean removeTrackAtPosition(final long id, final int position)
                throws RemoteException {
            return mService.get().removeTrackAtPosition(id, position);
        }

        @Override
        public int getMediaMountedCount() throws RemoteException {
            return mService.get().getMediaMountedCount();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mService.get().getAudioSessionId();
        }

        @Override
        public void setShakeToPlayEnabled(boolean enabled) {
            mService.get().setShakeToPlayEnabled(enabled);
        }

        @Override
        public void setLockscreenAlbumArt(boolean enabled) {
            mService.get().setLockscreenAlbumArt(enabled);
        }

    }
}
