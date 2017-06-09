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
package com.koma.music.detail.albumdetail;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;
import com.koma.music.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


/**
 * Created by koma on 5/5/17.
 */

public class AlbumDetailPresenter implements AlbumDetailContract.Presenter {
    private static final String TAG = AlbumDetailPresenter.class.getSimpleName();

    @NonNull
    private AlbumDetailContract.View mView;
    @NonNull
    private MusicRepository mRepository;

    private CompositeDisposable mDisposables;

    public AlbumDetailPresenter(AlbumDetailContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mDisposables = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        if (mView != null) {
            loadAlbum(mView.getAlbumId());

            loadAlbumSongs(mView.getAlbumId());
        }
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "subscribe");

        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadAlbumSongs(long albumID) {
        LogUtils.i(TAG, "loadAlbumSongs albumId : " + albumID);

        if (mDisposables != null) {
            mDisposables.clear();
        }

        if (mView != null) {
            Disposable disposable = mRepository.getAlbumSongs(mView.getAlbumId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSubscriber<List<Song>>() {
                        @Override
                        public void onError(Throwable throwable) {
                            LogUtils.e(TAG, "loadAlbumSongs onError : " + throwable.toString());
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onNext(List<Song> songs) {
                            if (mView != null) {
                                mView.showAlbumSongs(songs);
                            }
                        }
                    });

            mDisposables.add(disposable);
        }
    }

    @Override
    public void loadAlbum(long albumID) {
        if (mView != null) {
            Glide.with(mView.getContext()).asBitmap()
                    .load(Utils.getAlbumArtUri(albumID))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            if (mView != null) {
                                mView.showAlbum(resource);
                            }
                        }

                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);

                            if (mView != null) {
                                mView.showAlbum(errorDrawable);
                            }
                        }
                    });
        }

    }

    public static final List<Song> getAlbumSongs(long albumId) {
        List<Song> songList = new ArrayList<>();

        Cursor cursor = makeAlbumSongCursor(MusicApplication.getContext(), albumId);

        // Gather the data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Copy the song Id
                final long id = cursor.getLong(0);

                // Copy the song name
                final String songName = cursor.getString(1);

                // Copy the artist name
                final String artist = cursor.getString(2);

                // Copy the album name
                final String album = cursor.getString(3);

                // Copy the duration
                final long duration = cursor.getLong(4);

                // Make the duration label
                final int seconds = (int) (duration / 1000);

                // Create a new song
                final Song song = new Song(id, songName, artist, album, albumId, seconds);

                // Add everything up
                songList.add(song);
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return songList;
    }

    /**
     * @param context The {@link Context} to use.
     * @param albumId The Id of the album the songs belong to.
     * @return The {@link Cursor} used to run the query.
     */
    private static final Cursor makeAlbumSongCursor(final Context context, final Long albumId) {
        // Match the songs up with the artist
        String selection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''" +
                " AND " + MediaStore.Audio.AudioColumns.ALBUM_ID + "=" + albumId;
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        MediaStore.Audio.AudioColumns.TITLE,
                        /* 2 */
                        MediaStore.Audio.AudioColumns.ARTIST,
                        /* 3 */
                        MediaStore.Audio.AudioColumns.ALBUM,
                        /* 4 */
                        MediaStore.Audio.AudioColumns.DURATION,
                }, selection, null,
                MediaStore.Audio.Media.TRACK + ", "
                        + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }
}
