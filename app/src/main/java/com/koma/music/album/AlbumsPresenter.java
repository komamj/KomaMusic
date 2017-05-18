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
package com.koma.music.album;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Album;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 3/21/17.
 */

public class AlbumsPresenter implements AlbumsConstract.Presenter {
    private static String TAG = AlbumsPresenter.class.getSimpleName();
    @NonNull
    private AlbumsConstract.View mView;

    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public AlbumsPresenter(@NonNull AlbumsConstract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadAlbums();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubcribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void loadAlbums() {
        LogUtils.i(TAG, "loadAlbums");

        mSubscriptions.clear();

        Subscription subscription = mRepository.getAllAlbums().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Album>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e(TAG, "loadAlbums onError : " + throwable.toString());
                    }

                    @Override
                    public void onNext(List<Album> albumList) {
                        onLoadSongsFinished(albumList);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadSongsFinished(List<Album> albums) {
        LogUtils.i(TAG, "onLoadSongsFinished");

        if (mView.isActive()) {
            mView.hideLoadingView();

            if (albums.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showAlbums(albums);
            }
        }
    }

    public static List<Album> getAllAlbums() {
        return getAlbums(makeAlbucursor(null), false);
    }

    public static List<Album> getArtistAlbums(long artistId) {
        return getAlbums(makeAlbucursor(artistId), false);
    }

    private static List<Album> getAlbums(Cursor cursor, boolean isSingle) {
        List<Album> albumList = new ArrayList<>();

        // Gather the data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Copy the album id
                final long id = cursor.getLong(0);

                // Copy the album name
                final String albumName = cursor.getString(1);

                // Copy the artist name
                final String artist = cursor.getString(2);

                // Copy the number of songs
                final int songCount = cursor.getInt(3);

                // Copy the release year
                final String year = cursor.getString(4);

                // as per designer's request, don't show unknown albums
                if (MediaStore.UNKNOWN_STRING.equals(albumName)) {
                    continue;
                }

                // Create a new album
                final Album album = new Album(id, albumName, artist, songCount, year);
                // Add everything up
                albumList.add(album);
            } while (cursor.moveToNext() && !isSingle);
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        return albumList;
    }

    /**
     * Creates the {@link Cursor} used to run the query.
     *
     * @param artistId The artistId we want to find albums for or null if we want all albums
     * @return The {@link Cursor} used to run the album query.
     */
    private static final Cursor makeAlbucursor(final Long artistId) {
        // requested album ordering
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        if (artistId != null) {
            uri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistId);
        }

        Cursor cursor = MusicApplication.getContext().getContentResolver().query(uri,
                new String[]{
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        MediaStore.Audio.AlbumColumns.ALBUM,
                        /* 2 */
                        MediaStore.Audio.AlbumColumns.ARTIST,
                        /* 3 */
                        MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                        /* 4 */
                        MediaStore.Audio.AlbumColumns.FIRST_YEAR
                }, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

        return cursor;
    }
}
