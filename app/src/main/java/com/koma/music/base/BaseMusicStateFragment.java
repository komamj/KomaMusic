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

import android.content.IntentFilter;

import com.koma.music.listener.MusicStateListener;
import com.koma.music.receiver.MusicStateReceiver;
import com.koma.music.service.MusicServiceConstants;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/28/17.
 */

public abstract class BaseMusicStateFragment extends BaseFragment implements MusicStateListener {
    private static final String TAG = BaseMusicStateFragment.class.getSimpleName();

    private MusicStateReceiver mReceiver;

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

        mContext.registerReceiver(mReceiver, filter);

        LogUtils.i(TAG, "onStart register music state receiver");
    }

    @Override
    public void onStop() {
        super.onStop();

        mContext.unregisterReceiver(mReceiver);

        LogUtils.i(TAG, "onStop unregister music state receiver");
    }
}
