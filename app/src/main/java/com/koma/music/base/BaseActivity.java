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
import android.widget.Toast;

import com.koma.music.R;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.service.Constants;
import com.koma.music.service.IMusicService;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.koma.music.util.MusicUtils.mService;


/**
 * Created by koma on 3/20/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection, MusicStateListener {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_STORAGE = 1;

    protected Context mContext;
    /**
     * The service token
     */
    private MusicUtils.ServiceToken mToken;
    /**
     * Broadcast receiver
     */
    private PlaybackStatus mPlaybackStatus;

    /**
     * Playstate and meta change listener
     */
    private ArrayList<MusicStateListener> mMusicStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        ButterKnife.bind(this);

        mContext = BaseActivity.this;

        mMusicStateListener = new ArrayList<>();

        if (!needRequestStoragePermission()) {
            init();
        }
    }

    private void init() {
        // Control the media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Bind Music service
        mToken = MusicUtils.bindToService(this, this);

        // Initialize the broadcast receiver
        mPlaybackStatus = new PlaybackStatus(this);
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
            LogUtils.e(TAG, "unregister error : " + e.toString());
        }

        // Remove any music status listeners
        mMusicStateListener.clear();
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        LogUtils.i(TAG, "onServiceConnected");
        mService = IMusicService.Stub.asInterface(service);
        // Set the playback drawables
        //// TODO: 4/8/17
        // Current info
        onMetaChanged();
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
                    //meta changed
                    LogUtils.i(TAG, "meta changed notify all listener");
                    baseActivity.onMetaChanged();
                } else if (action.equals(Constants.PLAYSTATE_CHANGED)) {
                    // Set the play and pause image
                    LogUtils.i(TAG, "play state changed notify all listener");
                    baseActivity.onPlayStateChanged();
                } else if (action.equals(Constants.REFRESH)) {
                    //media change so refresh
                    LogUtils.i(TAG, "media change notify all listener");
                    baseActivity.refreshData();
                } else if (action.equals(Constants.PLAYLIST_CHANGED)) {
                    //playlist changed
                    LogUtils.i(TAG, "playlist changed notify all listener");
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(Constants.TRACK_ERROR)) {
                    LogUtils.i(TAG, "track error dislay error message");
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(Constants.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onMetaChanged() {
        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void onPlayStateChanged() {
        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlayStateChanged();
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
