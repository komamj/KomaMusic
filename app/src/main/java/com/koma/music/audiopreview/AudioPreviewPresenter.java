package com.koma.music.audiopreview;

import android.support.annotation.NonNull;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/5/17.
 */

public class AudioPreviewPresenter implements AudioPreviewContract.Presenter {
    private static final String TAG = AudioPreviewPresenter.class.getSimpleName();
    @NonNull
    private AudioPreviewContract.View mView;

    public AudioPreviewPresenter(@NonNull AudioPreviewContract.View view) {
        mView = view;
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");
    }
}
