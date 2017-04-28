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
package com.koma.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.koma.music.R;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.service.MusicServiceConstants;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/28/17.
 */

public class MusicStateReceiver extends BroadcastReceiver {
    private static final String TAG = MusicStateReceiver.class.getSimpleName();

    private MusicStateListener mListener;

    public MusicStateReceiver(MusicStateListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (mListener != null) {
            if (action.equals(MusicServiceConstants.META_CHANGED)) {
                //meta changed
                LogUtils.i(TAG, "meta changed notify all listener");
                mListener.onMetaChanged();
            } else if (action.equals(MusicServiceConstants.PLAYSTATE_CHANGED)) {
                // Set the play and pause image
                LogUtils.i(TAG, "play state changed notify all listener");
                mListener.onPlayStateChanged();
            } else if (action.equals(MusicServiceConstants.REFRESH)) {
                //media change so refresh
                LogUtils.i(TAG, "media change notify all listener");
                mListener.refreshData();
            } else if (action.equals(MusicServiceConstants.PLAYLIST_CHANGED)) {
                //playlist changed
                LogUtils.i(TAG, "playlist changed notify all listener");
                mListener.onPlaylistChanged();
            } else if (action.equals(MusicServiceConstants.TRACK_ERROR)) {
                LogUtils.i(TAG, "track error dislay error message");
                final String errorMsg = context.getString(R.string.error_playing_track,
                        intent.getStringExtra(MusicServiceConstants.TRACK_NAME));
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
