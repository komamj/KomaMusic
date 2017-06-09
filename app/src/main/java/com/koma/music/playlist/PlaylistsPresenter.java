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
package com.koma.music.playlist;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Playlist;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

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

public class PlaylistsPresenter implements PlaylistsConstract.Presenter {
    private static final String TAG = PlaylistsPresenter.class.getSimpleName();

    @NonNull
    private PlaylistsConstract.View mView;

    private MusicRepository mRepository;

    private CompositeDisposable mDisposables;

    public PlaylistsPresenter(@NonNull PlaylistsConstract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mDisposables = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadPlaylists();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        mDisposables.clear();
    }

    @Override
    public void loadPlaylists() {
        LogUtils.i(TAG, "loadPlaylists");
        mDisposables.clear();

        Disposable disposable = mRepository.getAllPlaylists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Playlist>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e(TAG, "loadPlaylists Error :" + throwable.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(List<Playlist> playlists) {
                        onLoadPlaylistsFinished(playlists);
                    }
                });

        mDisposables.add(disposable);
    }

    @Override
    public void onLoadPlaylistsFinished(List<Playlist> playlists) {
        LogUtils.i(TAG, "onLoadPlaylistsFinished");

        if (mView.isActive()) {
            mView.hideLoadingView();

            mView.showPlaylist(playlists);
        }
    }

    public static List<Playlist> getAllPlaylists() {
        List<Playlist> playlistList = new ArrayList<>();
        // Create the Cursor
        Cursor cursor;
        cursor = makePlaylistCursor(MusicApplication.getContext());
        // Gather the data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Copy the playlist id
                final long id = cursor.getLong(0);

                // Copy the playlist name
                final String name = cursor.getString(1);

                final int songCount = MusicUtils.getSongCountForPlaylist(MusicApplication.getContext(), id);

                // Create a new playlist
                final Playlist playlist = new Playlist(id, name, songCount);

                // Add everything up
                playlistList.add(playlist);
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return playlistList;
    }

    /**
     * Creates the {@link Cursor} used to run the query.
     *
     * @param context The {@link Context} to use.
     * @return The {@link Cursor} used to run the playlist query.
     */
    private static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        /* 0 */
                        BaseColumns._ID,
                        /* 1 */
                        MediaStore.Audio.PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }
}
