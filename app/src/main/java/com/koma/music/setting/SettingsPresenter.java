package com.koma.music.setting;

import android.support.annotation.NonNull;

import com.koma.music.data.source.local.MusicRepository;
import com.koma.music.util.LogUtils;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by koma on 4/24/17.
 */

public class SettingsPresenter implements SettingsContract.Presenter {
    private static final String TAG = SettingsPresenter.class.getSimpleName();
    @NonNull
    private SettingsContract.View mView;

    private MusicRepository mRepository;

    private CompositeDisposable mDisposables;

    public SettingsPresenter(SettingsContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mDisposables = new CompositeDisposable();
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
