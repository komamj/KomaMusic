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
package com.koma.music.play.playqueue;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


/**
 * Created by koma on 4/28/17.
 */

public class PlayQueuePresenter implements PlayQueueContract.Presenter {
    private static final String TAG = PlayQueuePresenter.class.getSimpleName();

    private static final String[] PROJECTION = new String[]{
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
            /* 6 */
            MediaStore.Audio.AudioColumns.YEAR,
    };

    @NonNull
    private PlayQueueContract.View mView;

    private MusicRepository mRepository;

    private CompositeDisposable mDisposables;

    public PlayQueuePresenter(@NonNull PlayQueueContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mDisposables = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadPlayQueue();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadPlayQueue() {
        LogUtils.i(TAG, "loadPlayQueue");

        if (mDisposables != null) {
            mDisposables.clear();
        }

        Disposable disposable = mRepository.getQueueSongs().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Song>>() {
                    @Override
                    public void onNext(List<Song> songs) {
                        onLoadPlayQueueFinished(songs);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        mDisposables.add(disposable);
    }

    @Override
    public void onLoadPlayQueueFinished(List<Song> songs) {
        LogUtils.i(TAG, "onLoadPlayQueueFinished");

        if (mView.isActive()) {
            mView.hideLoadingView();

            mView.showPlayQueueSongs(songs);
        }
    }

    public static List<Song> getQueueSongs() {
        List<Song> queueSongs = new ArrayList<>();

        Cursor cursor = makeNowPlayingCursor();
        // Gather the data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Copy the song Id
                final long id = cursor.getLong(0);

                // Copy the song name
                final String songName = cursor.getString(1);

                // Copy the artist name
                final String artist = cursor.getString(2);

                // Copy the album id
                final long albumId = cursor.getLong(3);

                // Copy the album name
                final String album = cursor.getString(4);

                // Copy the duration
                final long duration = cursor.getLong(5);

                // Convert the duration into seconds
                final int durationInSecs = (int) duration / 1000;

                // Create a new song
                final Song song = new Song(id, songName, artist, album, albumId, durationInSecs);

                // Add everything up
                queueSongs.add(song);
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        return queueSongs;
    }

    /**
     * Actually makes the queue
     */
    private static final Cursor makeNowPlayingCursor() {
        return new NowPlayingCursor(MusicApplication.getContext());
    }
}
