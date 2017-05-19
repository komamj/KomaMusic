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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/20/17.
 */

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {
    private static final String TAG = MyFavoritePresenter.class.getSimpleName();

    @NonNull
    private MyFavoriteContract.View mView;

    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public MyFavoritePresenter(MyFavoriteContract.View view, MusicRepository repository) {
        mRepository = repository;

        mView = view;
        mView.setPresenter(this);

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadMyFavoriteSongs();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void loadMyFavoriteSongs() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }

        Subscription subscription = mRepository.getMyFavoriteSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "loadMyFavoriteSongs error : " + e.toString());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onLoadFinished(songs);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadFinished(List<Song> songs) {
        LogUtils.i(TAG, "onLoadSongsFinished count : " + songs.size());

        if (mView != null) {
            if (mView.isActive()) {
                mView.hideLoadingView();

                if (songs.size() == 0) {
                    Glide.with(mView.getContext()).load(R.drawable.ic_album).asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    if (mView != null) {
                                        mView.showArtwork(resource);
                                    }
                                }
                            });

                    mView.showEmptyView();
                } else {
                    Glide.with(mView.getContext()).load(Utils.getAlbumArtUri(songs.get(0).mAlbumId))
                            .asBitmap()
                            .placeholder(R.drawable.ic_album)
                            .error(R.drawable.ic_album)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .priority(Priority.IMMEDIATE)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    if (mView != null) {
                                        mView.showArtwork(errorDrawable);
                                    }
                                }

                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    if (mView != null) {
                                        mView.showArtwork(resource);
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
