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
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.koma.music.listener.MusicStateListener;
import com.koma.music.receiver.MusicStateReceiver;
import com.koma.music.service.IMusicService;
import com.koma.music.service.MusicServiceConstants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.koma.music.util.MusicUtils.mService;

/**
 * Created by koma on 4/28/17.
 */

public abstract class BaseMusicStateActivity extends AppCompatActivity implements MusicStateListener,
        ServiceConnection {
    private static final String TAG = BaseMusicStateActivity.class.getSimpleName();

    private MusicStateReceiver mReceiver;

    private static final int PERMISSION_REQUEST_STORAGE = 1;

    protected Context mContext;
    /**
     * The service token
     */
    private MusicUtils.ServiceToken mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        ButterKnife.bind(this);

        mContext = BaseMusicStateActivity.this;

        if (!needRequestStoragePermission()) {
            init();
        }
    }

    private void init() {
        // Control the media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Bind Music service
        mToken = MusicUtils.bindToService(mContext, this);
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        LogUtils.i(TAG, "onServiceConnected");

        mService = IMusicService.Stub.asInterface(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDisconnected(final ComponentName name) {
        LogUtils.i(TAG, "onServiceDisconnected");

        mService = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mReceiver == null) {
            mReceiver = new MusicStateReceiver(this);
        }

        final IntentFilter filter = new IntentFilter();
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

        registerReceiver(mReceiver, filter);

        LogUtils.i(TAG, "onStart  register music state receiver");
    }


    @Override
    public void onStop() {
        super.onStop();

        unregisterReceiver(mReceiver);

        LogUtils.i(TAG, "onStop unregister music state receiver");
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

        LogUtils.i(TAG, "onDestroy unbind the music service");
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
