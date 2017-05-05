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
package com.koma.music.util;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by koma on 3/20/17.
 */

public class Constants {
    public static final String MUSIC_PACKAGE_NAME = "com.koma.music";

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

    //AlbumDetail
    public static final String TRANSITION_NAME = "transition_name";
    public static final String ALBUM_ID = "album_id";
    public static final String ALBUM_NAME = "album_name";
}
