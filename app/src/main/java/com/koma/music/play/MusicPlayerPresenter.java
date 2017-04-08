package com.koma.music.play;

import android.support.annotation.NonNull;

import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/8/17.
 */

public class MusicPlayerPresenter implements MusicPlayerContract.Presenter {
    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();
    @NonNull
    private MusicPlayerContract.View mView;

    private CompositeSubscription mSubscriptions;

    private MusicRepository mRepository;

    public MusicPlayerPresenter(MusicPlayerContract.View view, MusicRepository repository) {
        mSubscriptions = new CompositeSubscription();

        mRepository = repository;

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

    @Override
    public void doPlayOrPause() {
        LogUtils.i(TAG, "doPlayOrPause");
    }

    @Override
    public void doPrev() {
        LogUtils.i(TAG, "doPrev");
    }

    @Override
    public void doNext() {
        LogUtils.i(TAG, "doNext");
    }

    @Override
    public void doFavorite() {
        LogUtils.i(TAG, "doFavorite");
    }

    @Override
    public void doBlurArtWork() {
        LogUtils.i(TAG, "doArtWork");
    }
}
