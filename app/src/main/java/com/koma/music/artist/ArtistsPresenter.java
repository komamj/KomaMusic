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
package com.koma.music.artist;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.koma.music.MusicApplication;
import com.koma.music.data.source.local.MusicRepository;
import com.koma.music.data.model.Artist;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by koma on 3/21/17.
 */

public class ArtistsPresenter implements ArtistsConstract.Presenter {
    private static final String TAG = ArtistsPresenter.class.getSimpleName();
    @NonNull
    private ArtistsConstract.View mView;

    private CompositeDisposable mDisposables;

    private MusicRepository mRepository;

    public ArtistsPresenter(ArtistsConstract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mDisposables = new CompositeDisposable();

        mRepository = repository;
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadArtists();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadArtists() {
        LogUtils.i(TAG, "loadArtists");

        mDisposables.clear();

        Disposable subscription = mRepository.getAllArtists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Artist>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e(TAG, "loadArtists onError : " + throwable.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(List<Artist> artists) {
                        onLoadArtistsFinished(artists);
                    }
                });

        mDisposables.add(subscription);
    }

    @Override
    public void onLoadArtistsFinished(List<Artist> artists) {
        LogUtils.i(TAG, "onLoadArtistsFinished");

        if (mView.isActive()) {
            mView.hideLoadingView();

            if (artists.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showArtists(artists);
            }
        }
    }

    public static List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();
        Cursor cursor = makeArtistCursor(MusicApplication.getContext());
        // Gather the data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Copy the artist id
                final long id = cursor.getLong(0);

                // Copy the artist name
                final String artistName = cursor.getString(1);

                // Copy the number of albums
                final int albumCount = cursor.getInt(2);

                // Copy the number of songs
                final int songCount = cursor.getInt(3);

                // as per designer's request, don't show unknown artist
                if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
                    continue;
                }

                // Create a new artist
                final Artist artist = new Artist(id, artistName, songCount, albumCount);
                artistList.add(artist);
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return artistList;
    }

    /**
     * Creates the {@link Cursor} used to run the query.
     *
     * @param context The {@link Context} to use.
     * @return The {@link Cursor} used to run the artist query.
     */
    private static final Cursor makeArtistCursor(final Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[]{
                        /* 0 */
                        MediaStore.Audio.Artists._ID,
                        /* 1 */
                        MediaStore.Audio.Artists.ARTIST,
                        /* 2 */
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                        /* 3 */
                        MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                }, null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);

        return cursor;
    }
}
