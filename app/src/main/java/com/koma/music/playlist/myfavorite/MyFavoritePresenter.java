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
package com.koma.music.playlist.myfavorite;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.koma.music.MusicApplication;
import com.koma.music.R;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.local.db.FavoriteSong;
import com.koma.music.data.local.db.SortedCursor;
import com.koma.music.data.model.Song;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.LogUtils;
import com.koma.music.util.Utils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by koma on 4/20/17.
 */

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {
    private static final String TAG = MyFavoritePresenter.class.getSimpleName();

    @NonNull
    private MyFavoriteContract.View mView;

    private MusicRepository mRepository;

    private CompositeDisposable mDisposables;

    public MyFavoritePresenter(MyFavoriteContract.View view, MusicRepository repository) {
        mRepository = repository;

        mView = view;
        mView.setPresenter(this);

        mDisposables = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadMyFavoriteSongs();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadMyFavoriteSongs() {
        if (mDisposables != null) {
            mDisposables.clear();
        }

        Disposable disposable = mRepository.getMyFavoriteSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Song>>() {
                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "loadMyFavoriteSongs error : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onLoadFinished(songs);
                    }
                });

        mDisposables.add(disposable);
    }

    @Override
    public void onLoadFinished(List<Song> songs) {
        LogUtils.i(TAG, "onLoadSongsFinished count : " + songs.size());

        if (mView != null) {
            if (mView.isActive()) {
                mView.hideLoadingView();

                if (songs.size() == 0) {
                    Glide.with(mView.getContext())
                            .asBitmap()
                            .load(R.drawable.ic_album)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    if (mView != null) {
                                        mView.showArtwork(resource);
                                    }
                                }
                            });

                    mView.showEmptyView();
                } else {
                    Glide.with(mView.getContext())
                            .asBitmap()
                            .load(Utils.getAlbumArtUri(songs.get(0).mAlbumId))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    if (mView != null) {
                                        mView.showArtwork(resource);
                                    }
                                }

                                @Override
                                public void onLoadFailed(Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    if (mView != null) {
                                        mView.showArtwork(errorDrawable);
                                    }
                                }
                            });

                    mView.showFavoriteSongs(songs);
                }
            }
        }
    }

    public static final List<Song> getFavoriteSongs() {
        return SongsPresenter.getSongsForCursor(getFavoriteSongCursor(), false);
    }

    public static Cursor getFavoriteSongCursor() {
        Cursor cursor = FavoriteSong.getInstance(MusicApplication.getContext()).getFavoriteSong();
        SortedCursor sortedCursor = SongsPresenter.makeSortedCursor(MusicApplication.getContext(),
                cursor, 0);
        return sortedCursor;
    }
}
