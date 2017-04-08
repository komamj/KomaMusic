package com.koma.music.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.koma.music.MusicApplication;
import com.koma.music.data.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public class KomaUtils {
    private static final String TAG = KomaUtils.class.getSimpleName();
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

    public static List<Song> getAllSongs() {
        LogUtils.i(TAG, "getAllSongs");
        List<Song> songs = new ArrayList<>();
        Cursor cursor = MusicApplication.getContext().getContentResolver().query(Constants.AUDIO_URI,
                AUDIO_PROJECTION, Constants.MUSIC_ONLY_SELECTION, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
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
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return songs;
    }
}
