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

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.local.db.SortedCursor;
import com.koma.music.data.model.Song;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.PreferenceUtils;

import java.util.ArrayList;
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
    private static final String[] AUDIO_PROJECTION = new String[]{
                        /* 0 */
            MediaStore.Audio.Media._ID,
                        /* 1 */
            MediaStore.Audio.Media.TITLE,
                        /* 2 */
            MediaStore.Audio.Media.ARTIST,
                        /* 3 */
            MediaStore.Audio.Media.ALBUM_ID,
                        /* 4 */
            MediaStore.Audio.Media.ALBUM,
                        /* 5 */
            MediaStore.Audio.Media.DURATION
    };
    @NonNull
    private SongsContract.View mView;

    private CompositeSubscription mSubscriptions;

    private MusicRepository mRepository;

    public SongsPresenter(@NonNull SongsContract.View view, MusicRepository repository) {
        mSubscriptions = new CompositeSubscription();
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
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
                        onLoadSongsFinished(songs);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadSongsFinished(List<Song> songs) {
        if (mView == null) {
            return;
        }

        if (mView.isActive()) {
            mView.hideLoadingView();

            if (songs.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showSongs(songs);
            }
        }
    }

    public static List<Song> getAllSongs() {
        LogUtils.i(TAG, "getAllSongs");

        return getSongsForCursor(makeSongCursor(), false);
    }

    public static List<Song> getSongsForCursor(Cursor cursor, boolean isSingle) {
        List<Song> songs = new ArrayList<>();
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
                final Song song = new Song(id, songName, artist, album, albumId,
                        durationInSecs);

                songs.add(song);
            } while (cursor.moveToNext() && !isSingle);
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return songs;
    }

    public static final Cursor makeSongCursor() {
        Cursor cursor = MusicApplication.getContext().getContentResolver().query(Constants.AUDIO_URI,
                AUDIO_PROJECTION, Constants.MUSIC_ONLY_SELECTION, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return cursor;
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        final String songSortOrder = PreferenceUtils.getInstance(context).getSongSortOrder();
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA},
                selectionStatement, paramArrayOfString, sortOrder);

    }

    /**
     * 根据包含song id的cursor,获取排序好的song cursor
     *
     * @param context
     * @param cursor
     * @param idColumn
     * @return
     */
    public static SortedCursor makeSortedCursor(final Context context, final Cursor cursor,
                                                final int idColumn) {
        if (cursor != null && cursor.moveToFirst()) {

            StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID);
            selection.append(" IN (");

            long[] order = new long[cursor.getCount()];

            long id = cursor.getLong(idColumn);
            selection.append(id);
            order[cursor.getPosition()] = id;

            while (cursor.moveToNext()) {
                selection.append(",");

                id = cursor.getLong(idColumn);
                order[cursor.getPosition()] = id;
                selection.append(String.valueOf(id));
            }

            selection.append(")");

            Cursor songCursor = makeSongCursor(context, selection.toString(), null);
            if (songCursor != null) {
                return new SortedCursor(songCursor, order, BaseColumns._ID, null);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }
}
