package com.koma.music.util;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by koma on 3/20/17.
 */

public class Constants {
    public static final Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static Uri PLAYLIST_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    public static Uri SONG_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static Uri ARTIST_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    public static Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    public static Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

    public static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

    public static int REFRESH_TIME = 500;

    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
}
