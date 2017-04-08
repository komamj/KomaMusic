package com.koma.music.genre;

import android.support.annotation.NonNull;

import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/8/17.
 */

public class GenresPresenter implements GenresContract.Presenter {
    private static final String TAG = GenresPresenter.class.getSimpleName();
    @NonNull
    private GenresContract.View mView;
    @NonNull
    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public GenresPresenter(GenresContract.View view, MusicRepository repository) {
        mRepository = repository;

        mSubscriptions = new CompositeSubscription();

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
