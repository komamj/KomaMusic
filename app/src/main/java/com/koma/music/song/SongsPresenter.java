package com.koma.music.song;

import android.support.annotation.NonNull;

import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 3/20/17.
 */

public class SongsPresenter implements SongsContract.Presenter {
    private static final String TAG = SongsPresenter.class.getSimpleName();
    @NonNull
    private SongsContract.View mView;

    private CompositeSubscription mSubscriptions;

    private MusicRepository mRepository;

    private SongsPresenter(@NonNull SongsContract.View view, MusicRepository repository) {
        mSubscriptions = new CompositeSubscription();
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    public static SongsPresenter newInstance(@NonNull SongsContract.View view,
                                             MusicRepository repository) {
        return new SongsPresenter(view, repository);
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");
        loadSongs();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");
        mSubscriptions.clear();
    }

    @Override
    public void loadSongs() {
        mSubscriptions.clear();
        Subscription subscription = mRepository.getAllSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onStart() {
                        LogUtils.i(TAG, "onSart");
                        if (mView != null) {
                            mView.showLoadingView();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        LogUtils.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e(TAG, "onError :" + throwable.toString());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        LogUtils.i(TAG, "onNext");
                        onLoadSongsFinished(songs);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadSongsFinished(List<Song> songs) {
        if (mView.isActive()) {
            if (songs.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showSongs(songs);
            }
        }
    }
}
