package com.koma.music.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koma.music.R;
import com.koma.music.service.Constants;
import com.koma.music.service.IMusicService;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.koma.music.util.MusicUtils.mService;


/**
 * Created by koma on 3/20/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    protected Context mContext;
    /**
     * The service token
     */
    private MusicUtils.ServiceToken mToken;

    private TextView mTrackName;
    private TextView mArtistName;

    private ImageView mAlbumArt;

    /**
     * Broadcast receiver
     */
    private PlaybackStatus mPlaybackStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        mContext = BaseActivity.this;

        // Fade it in
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Control the media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Bind Music service
        mToken = MusicUtils.bindToService(this, this);

        // Initialize the broadcast receiver
        mPlaybackStatus = new PlaybackStatus(this);

        if (!needRequestStoragePermission()) {
            init();
        }
    }

    private void init() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart");

        final IntentFilter filter = new IntentFilter();
        // Play and pause changes
        filter.addAction(Constants.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(Constants.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(Constants.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(Constants.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(Constants.TRACK_ERROR);
        registerReceiver(mPlaybackStatus, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        // Set the playback drawables
        //// TODO: 4/8/17
        // Current info
        //// TODO: 4/8/17
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i(TAG, "onPause");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(TAG, "onStop");
        // Unregister the receiver
        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
            LogUtils.e(TAG, "unregister error : " + e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mToken != null) {
            MusicUtils.unbindFromService(mToken);
            mToken = null;
        }
        // Remove any music status listeners
        //// TODO: 4/8/17
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        LogUtils.i(TAG, "onServiceConnected");
        mService = IMusicService.Stub.asInterface(service);
        // Set the playback drawables
        //// TODO: 4/8/17
        // Current info
        //// TODO: 4/8/17
        // if there were any pending intents while the service was started
        //// TODO: 4/8/17
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDisconnected(final ComponentName name) {
        LogUtils.i(TAG, "onServiceDisconnected");
        mService = null;
    }

    /**
     * Used to monitor the state of playback
     */
    private final static class PlaybackStatus extends BroadcastReceiver {
        private final WeakReference<BaseActivity> mReference;

        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(Constants.META_CHANGED)) {
                    //// TODO: 4/8/17
                    //meta changed
                } else if (action.equals(Constants.PLAYSTATE_CHANGED)) {
                    //// TODO: 4/8/17
                    // Set the play and pause image

                } else if (action.equals(Constants.REFRESH)) {
                    //media change so refresh
                } else if (action.equals(Constants.PLAYLIST_CHANGED)) {

                    //playlist changed
                } else if (action.equals(Constants.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(Constants.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public abstract int getLayoutId();

    private boolean needRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        boolean needRequest = false;
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ArrayList<String> permissionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
                needRequest = true;
            }
        }

        if (needRequest) {
            int count = permissionList.size();
            if (count > 0) {
                String[] permissionArray = new String[count];
                for (int i = 0; i < count; i++) {
                    permissionArray[i] = permissionList.get(i);
                }

                requestPermissions(permissionArray, PERMISSION_REQUEST_STORAGE);
            }
        }

        return needRequest;
    }

    private boolean checkPermissionGrantResults(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                if (checkPermissionGrantResults(grantResults)) {
                    //// TODO: 4/8/17
                    init();
                } else {
                    finish();
                }
            }
        }
    }
}
