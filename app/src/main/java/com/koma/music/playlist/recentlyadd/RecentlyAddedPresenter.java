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

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.koma.music.MusicApplication;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;
import com.koma.music.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 4/20/17.
 */

public class RecentlyAddedPresenter implements RecentlyAddedContract.Presenter {
    private static final String TAG = RecentlyAddedPresenter.class.getSimpleName();

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");
    }

    public static List<Song> getRecentlyAddedSongs() {
        List<Song> songList = new ArrayList<>();
        // Create the xCursor
        Cursor mCursor = makeLastAddedCursor(MusicApplication.getContext());
        // Gather the data
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                // Copy the song Id
                final long id = mCursor.getLong(0);

                // Copy the song name
                final String songName = mCursor.getString(1);

                // Copy the artist name
                final String artist = mCursor.getString(2);

                // Copy the album id
                final long albumId = mCursor.getLong(3);

                // Copy the album name
                final String album = mCursor.getString(4);

                // Copy the duration
                final long duration = mCursor.getLong(5);

                // Convert the duration into seconds
                final int durationInSecs = (int) duration / 1000;

                // Create a new song
                final Song song = new Song(id, songName, artist, album, albumId, durationInSecs);

                // Add everything up
                songList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return songList;
    }

    /**
     * @param context The {@link Context} to use.
     * @return The {@link Cursor} used to run the song query.
     */
    private static final Cursor makeLastAddedCursor(final Context context) {
        // timestamp of four weeks ago
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        // possible saved timestamp caused by user "clearing" the last added playlist
        long cutoff = PreferenceUtils.getInstance(context).getLastAddedCutoff() / 1000;
        // use the most recent of the two timestamps
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        String selection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''" +
                " AND " + MediaStore.Audio.Media.DATE_ADDED + ">" +
                cutoff;

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
                        /* 6 */
                        MediaStore.Audio.AudioColumns.YEAR,
                }, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }
}
