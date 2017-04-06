package com.koma.music.service;

import android.database.ContentObserver;
import android.os.Handler;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/27/17.
 */

public class MediaObserver extends ContentObserver implements Runnable {
    // milliseconds to delay before calling refresh to aggregate events
    private static final long REFRESH_DELAY = 500;
    private Handler mHandler;

    public MediaObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        // if a change is detected, remove any scheduled callback
        // then post a new one. This is intended to prevent closely
        // spaced events from generating multiple refresh calls
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, REFRESH_DELAY);
    }

    @Override
    public void run() {
        // actually call refresh when the delayed callback fires
        LogUtils.d("ELEVEN", "calling refresh!");
        //refresh();
    }
}
