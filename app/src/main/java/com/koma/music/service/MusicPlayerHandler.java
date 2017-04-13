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

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.koma.music.util.LogUtils;

import java.lang.ref.WeakReference;

/**
 * Created by koma on 3/27/17.
 */

public class MusicPlayerHandler extends Handler {
    private static final String TAG = MusicPlayerHandler.class.getSimpleName();
    private final WeakReference<MusicService> mService;
    private float mCurrentVolume = 1.0f;
    private int mHeadsetHookClickCounter = 0;

    /**
     * Constructor of <code>MusicPlayerHandler</code>
     *
     * @param service The service to use.
     * @param looper  The thread to run on.
     */
    public MusicPlayerHandler(final MusicService service, final Looper looper) {
        super(looper);
        mService = new WeakReference<MusicService>(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(final Message msg) {
        final MusicService service = mService.get();
        if (service == null) {
            return;
        }

        synchronized (service) {
            switch (msg.what) {
                case Constants.FADEDOWN:
                    mCurrentVolume -= .05f;
                    if (mCurrentVolume > .2f) {
                        sendEmptyMessageDelayed(Constants.FADEDOWN, 10);
                    } else {
                        mCurrentVolume = .2f;
                    }
                    //service.mPlayer.setVolume(mCurrentVolume);
                    break;
                case Constants.FADEUP:
                    mCurrentVolume += .01f;
                    if (mCurrentVolume < 1.0f) {
                        sendEmptyMessageDelayed(Constants.FADEUP, 10);
                    } else {
                        mCurrentVolume = 1.0f;
                    }
                    // service.mPlayer.setVolume(mCurrentVolume);
                    break;
                case Constants.SERVER_DIED:
                    /*if (service.isPlaying()) {
                        final TrackErrorInfo info = (TrackErrorInfo) msg.obj;
                        service.sendErrorMessage(info.mTrackName);

                        // since the service isPlaying(), we only need to remove the offending
                        // audio track, and the code will automatically play the next track
                        service.removeTrack(info.mId);
                    } else {
                        service.openCurrentAndNext();
                    }*/
                    break;
                case Constants.TRACK_WENT_TO_NEXT:
                    /*service.setAndRecordPlayPos(service.mNextPlayPos);
                    service.setNextTrack();
                    if (service.mCursor != null) {
                        service.mCursor.close();
                        service.mCursor = null;
                    }
                    service.updateCursor(service.mPlaylist.get(service.mPlayPos).mId);
                    service.notifyChange(META_CHANGED);
                    service.updateNotification();*/
                    break;
                case Constants.TRACK_ENDED:
                    /*if (service.mRepeatMode == REPEAT_CURRENT) {
                        service.seek(0);
                        service.play();
                    } else {
                        service.gotoNext(false);
                    }*/
                    break;
                case Constants.LYRICS:
                    /*service.mLyrics = (String) msg.obj;
                    service.notifyChange(NEW_LYRICS);*/
                    break;
                case Constants.FOCUSCHANGE:
                    LogUtils.i(TAG, "Received audio focus change event " + msg.arg1);
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            /*if (service.isPlaying()) {
                                service.mPausedByTransientLossOfFocus =
                                        msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                            }
                            service.pause();*/
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            removeMessages(Constants.FADEUP);
                            sendEmptyMessage(Constants.FADEDOWN);
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            /*if (!service.isPlaying()
                                    && service.mPausedByTransientLossOfFocus) {
                                service.mPausedByTransientLossOfFocus = false;
                                mCurrentVolume = 0f;
                                service.mPlayer.setVolume(mCurrentVolume);
                                service.play();
                            } else {
                                removeMessages(FADEDOWN);
                                sendEmptyMessage(FADEUP);
                            }*/
                            break;
                        default:
                    }
                    break;
                case Constants.HEADSET_HOOK_EVENT: {
                    long eventTime = (Long) msg.obj;

                    mHeadsetHookClickCounter = Math.min(mHeadsetHookClickCounter + 1, 3);
                    LogUtils.i(TAG, "Got headset click, count = " + mHeadsetHookClickCounter);
                    removeMessages(Constants.HEADSET_HOOK_MULTI_CLICK_TIMEOUT);

                    if (mHeadsetHookClickCounter == 3) {
                        sendEmptyMessage(Constants.HEADSET_HOOK_MULTI_CLICK_TIMEOUT);
                    } else {
                        sendEmptyMessageAtTime(Constants.HEADSET_HOOK_MULTI_CLICK_TIMEOUT,
                                eventTime + Constants.DOUBLE_CLICK_TIMEOUT);
                    }
                    break;
                }
                case Constants.HEADSET_HOOK_MULTI_CLICK_TIMEOUT:
                    LogUtils.i(TAG, "Handling headset click");
                    switch (mHeadsetHookClickCounter) {
                        case 1:
                            // service.togglePlayPause();
                            break;
                        case 2:
                            //service.gotoNext(true);
                            break;
                        case 3:
                            //service.prev(false);
                            break;
                    }
                    mHeadsetHookClickCounter = 0;
                    //service.mHeadsetHookWakeLock.release();
                    break;
                default:
                    break;
            }
        }
    }
}
