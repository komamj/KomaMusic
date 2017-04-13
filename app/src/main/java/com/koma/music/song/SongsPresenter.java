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
            mView.hideLoadingView();

            if (songs.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showSongs(songs);
            }
        }
    }
}
