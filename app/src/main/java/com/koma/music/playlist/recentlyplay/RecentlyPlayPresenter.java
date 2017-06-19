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
package com.koma.music.playlist.recentlyplay;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.koma.music.MusicApplication;
import com.koma.music.R;
import com.koma.music.data.source.local.MusicRepository;
import com.koma.music.data.source.local.db.RecentlyPlay;
import com.koma.music.data.source.local.db.SongPlayCount;
import com.koma.music.data.source.local.db.SortedCursor;
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

public class RecentlyPlayPresenter implements RecentlyPlayContract.Presenter {
    private static final String TAG = RecentlyPlayPresenter.class.getSimpleName();

    @NonNull
    private RecentlyPlayContract.View mView;

    private CompositeDisposable mDisposables;

    private MusicRepository mRepository;

    public RecentlyPlayPresenter(@NonNull RecentlyPlayContract.View view, MusicRepository repository) {
        mDisposables = new CompositeDisposable();

        mView = view;
        mView.setPresenter(this);

        mRepository = repository;
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadRecentlyPlayedSongs();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadRecentlyPlayedSongs() {
        if (mDisposables != null) {
            mDisposables.clear();
        }

        Disposable disposable = mRepository.getRecentlyPlayedSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Song>>() {
                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "loadRecentlyPlayedSongs error : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onLoadPlayedSongsFinished(songs);
                    }
                });

        mDisposables.add(disposable);
    }

    @Override
    public void onLoadPlayedSongsFinished(List<Song> songs) {
        LogUtils.i(TAG, "onLoadSongsFinished count : " + songs.size());

        if (mView != null) {
            if (mView.isActive()) {
                mView.hideLoadingView();

                if (songs.size() == 0) {
                    Glide.with(mView.getContext()).asBitmap().load(R.drawable.ic_album)
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
                    Glide.with(mView.getContext()).asBitmap().load(Utils.getAlbumArtUri(songs.get(0).mAlbumId))
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

                                    LogUtils.e(TAG, "onLoadfailed");

                                    if (mView != null) {
                                        mView.showArtwork(errorDrawable);
                                    }
                                }
                            });

                    mView.showPlayedSongs(songs);
                }
            }
        }
    }

    public static List<Song> getRecentlyPlaySongs() {
        return SongsPresenter.getSongsForCursor(makeRecentPlayCursor(), false);
    }

    /**
     * 获取最近播放歌曲的cursor
     *
     * @return
     */
    public static SortedCursor makeRecentPlayCursor() {

        Cursor songs = RecentlyPlay.getInstance(MusicApplication.getContext()).queryRecentIds(null);

        try {
            return SongsPresenter.makeSortedCursor(MusicApplication.getContext(), songs,
                    songs.getColumnIndex(SongPlayCount.SongPlayCountColumns.ID));
        } finally {
            if (songs != null) {
                songs.close();
            }
        }
    }
}
