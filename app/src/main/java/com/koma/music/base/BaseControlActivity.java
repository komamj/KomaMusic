/*
 * Copyright (C) 2017 Koma MJ
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.koma.music.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.koma.music.R;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.play.quickcontrol.QuickControlFragment;
import com.koma.music.service.IMusicService;
import com.koma.music.service.MusicServiceConstants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.koma.music.util.MusicUtils.mService;

/**
 * Created by koma on 6/6/17.
 */

public abstract class BaseControlActivity extends PermissionActivity implements MusicStateListener,
        ServiceConnection {
    private static final String TAG = BaseControlActivity.class.getSimpleName();

    private QuickControlFragment mControlFragment;
    /**
     * Broadcast receiver
     */
    private BaseControlActivity.PlaybackStatus mPlaybackStatus;
    /**
     * Playstate and meta change listener
     */
    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    /**
     * The service token
     */
    private MusicUtils.ServiceToken mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init() {
        // Control the media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Bind Music service
        mToken = MusicUtils.bindToService(this, this);
        // Initialize the broadcast receiver
        mPlaybackStatus = new BaseControlActivity.PlaybackStatus(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart");

        listenPlaybackStatus();

        mControlFragment = (QuickControlFragment) getSupportFragmentManager().findFragmentById(
                R.id.fragment_playback_controls);
        if (mControlFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }

        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            hidePlaybackControls();
        }

        //onMetaChanged();
    }

    private void listenPlaybackStatus() {
        final IntentFilter filter = new IntentFilter();
        // if a queue has changed,notify us
        filter.addAction(MusicServiceConstants.QUEUE_CHANGED);
        // Play and pause changes
        filter.addAction(MusicServiceConstants.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicServiceConstants.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(MusicServiceConstants.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(MusicServiceConstants.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicServiceConstants.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);
    }


    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        LogUtils.i(TAG, "onServiceConnected");

        mService = IMusicService.Stub.asInterface(service);

        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            hidePlaybackControls();
        }
        // Current info
        onMetaChanged();
        // if there were any pending intents while the service was started
        //handlePendingPlaybackRequests();
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
     * @param status The {@link MusicStateListener} to use
     */
    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    /**
     * @param status The {@link MusicStateListener} to use
     */
    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
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

        // Unregister the receiver
        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
            //$FALL-THROUGH$
        }

        // Remove any music status listeners
        mMusicStateListener.clear();

        LogUtils.i(TAG, "onDestroy unbind the music service");
    }

    /**
     * Used to monitor the state of playback
     */
    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseControlActivity> mReference;

        /**
         * Constructor of <code>PlaybackStatus</code>
         */
        public PlaybackStatus(final BaseControlActivity activity) {
            mReference = new WeakReference<BaseControlActivity>(activity);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            LogUtils.i(TAG, "onReceive action : " + action);
            BaseControlActivity baseControlActivity = mReference.get();
            if (baseControlActivity != null) {
                if (action.equals(MusicServiceConstants.QUEUE_CHANGED)) {
                    if (baseControlActivity.shouldShowControls()) {
                        baseControlActivity.showPlaybackControls();
                    } else {
                        baseControlActivity.hidePlaybackControls();
                    }
                } else if (action.equals(MusicServiceConstants.META_CHANGED)) {
                    baseControlActivity.onMetaChanged();
                } else if (action.equals(MusicServiceConstants.PLAYSTATE_CHANGED)) {
                    baseControlActivity.mControlFragment.updateState();
                } else if (action.equals(MusicServiceConstants.REFRESH)) {
                    //refresh data
                    baseControlActivity.refreshData();
                } else if (action.equals(MusicServiceConstants.PLAYLIST_CHANGED)) {
                    baseControlActivity.onPlaylistChanged();
                } else if (action.equals(MusicServiceConstants.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicServiceConstants.TRACK_NAME));
                    Toast.makeText(baseControlActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onMetaChanged() {
        // update action bar info

        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void refreshData() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.refreshData();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    protected void showPlaybackControls() {
        LogUtils.d(TAG, "showPlaybackControls");
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom,
                        R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom)
                .show(mControlFragment)
                .commit();
    }

    protected void hidePlaybackControls() {
        LogUtils.d(TAG, "hidePlaybackControls");
        getSupportFragmentManager().beginTransaction()
                .hide(mControlFragment)
                .commit();
    }

    protected boolean shouldShowControls() {
        if (MusicUtils.getQueueSize() != 0) {
            return true;
        }
        return false;
    }
}
