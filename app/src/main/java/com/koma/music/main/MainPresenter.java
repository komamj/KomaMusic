package com.koma.music.main;

import android.support.annotation.NonNull;

import com.koma.music.main.MainContract.Presenter;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/5/17.
 */

public class MainPresenter implements Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    @NonNull
    private MainContract.View mView;

    public MainPresenter(@NonNull MainContract.View view) {
        mView = view;
        mView.setPresenter(this);
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
