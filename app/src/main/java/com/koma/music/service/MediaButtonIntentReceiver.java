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
package com.koma.music.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.KeyEvent;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/27/17.
 */

public class MediaButtonIntentReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = MediaButtonIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        LogUtils.i(TAG, "Received intent: " + intent);
        final String intentAction = intent.getAction();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
            startService(context, Constants.CMDPAUSE, System.currentTimeMillis());
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null || event.getAction() != KeyEvent.ACTION_UP) {
                return;
            }

            String command = null;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    command = Constants.CMDHEADSETHOOK;
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = Constants.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    command = Constants.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = Constants.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = Constants.CMDPREVIOUS;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = Constants.CMDPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = Constants.CMDPLAY;
                    break;
            }
            if (command != null) {
                startService(context, command, event.getEventTime());
                if (isOrderedBroadcast()) {
                    abortBroadcast();
                }
            }
        }
    }

    private static void startService(Context context, String command, long timestamp) {
        final Intent i = new Intent(context, MusicService.class);
        i.setAction(Constants.SERVICECMD);
        i.putExtra(Constants.CMDNAME, command);
        i.putExtra(Constants.FROM_MEDIA_BUTTON, true);
        i.putExtra(Constants.TIMESTAMP, timestamp);
        startWakefulService(context, i);
    }
}
