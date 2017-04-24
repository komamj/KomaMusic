package com.koma.music.setting;

import android.support.annotation.NonNull;

import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/24/17.
 */

public class SettingsPresenter implements SettingsContract.Presenter {
    private static final String TAG = SettingsPresenter.class.getSimpleName();
    @NonNull
    private SettingsContract.View mView;

    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public SettingsPresenter(SettingsContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mSubscriptions = new CompositeSubscription();
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
