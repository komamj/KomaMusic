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
package com.koma.music.playlist.recentlyadd;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.koma.music.MusicApplication;
import com.koma.music.R;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.PreferenceUtils;
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

public class RecentlyAddedPresenter implements RecentlyAddedContract.Presenter {
    private static final String TAG = RecentlyAddedPresenter.class.getSimpleName();

    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    @NonNull
    private RecentlyAddedContract.View mView;

    public RecentlyAddedPresenter(RecentlyAddedContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadRecentlyAddedSongs();
    }

    @Override
    public void loadRecentlyAddedSongs() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }

        Subscription subscription = mRepository.getRecentlyAddedSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Song>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "loadRecentlyAddedSongs onError : " + e.toString());
                    }

                    @Override
                    public void onNext(List<Song> songs) {
                        onLoadSongsFinished(songs);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadSongsFinished(List<Song> songs) {
        LogUtils.i(TAG, "onLoadSongsFinished");

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

                    mView.showSongs(songs);
                }
            }
        }
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    public static List<Song> getRecentlyAddedSongs() {
        return SongsPresenter.getSongsForCursor(makeLastAddedCursor(), false);
    }

    /**
     * @return The {@link Cursor} used to run the song query.
     */
    public static final Cursor makeLastAddedCursor() {
        // timestamp of four weeks ago
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        // possible saved timestamp caused by user "clearing" the last added playlist
        long cutoff = PreferenceUtils.getInstance(MusicApplication.getContext())
                .getLastAddedCutoff() / 1000;
        // use the most recent of the two timestamps
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        String selection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''" +
                " AND " + MediaStore.Audio.Media.DATE_ADDED + ">" +
                cutoff;

        return MusicApplication.getContext().getContentResolver().query(Constants.SONG_URI,
                new String[]{
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        MediaStore.Audio.AudioColumns.TITLE,
                        /* 2 */
                        MediaStore.Audio.AudioColumns.ARTIST,
                        /* 3 */
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        /* 4 */
                        MediaStore.Audio.AudioColumns.ALBUM,
                        /* 5 */
                        MediaStore.Audio.AudioColumns.DURATION,
                }, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }
}
