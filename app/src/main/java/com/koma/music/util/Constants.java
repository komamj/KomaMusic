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
    /* Name of database file */
    public static final String DATABASENAME = "komamusic.db";

    public static final String MUSIC_PACKAGE_NAME = "com.koma.music";
    public static final String DETAIL_PACKAGE_NAME = "com.koma.music.detail.DetailsActivity";

    public static Uri PLAYLIST_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    public static Uri SONG_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static Uri ARTIST_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    public static Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    public static Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

    public static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

    public static int REFRESH_TIME = 500;

    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";

    //Detail
    public static final String WHICH_DETAIL_PAGE = "which_detail_page";
    public static final int RECENTLY_ADDED = 0x00;
    public static final int RECENTLY_PLAYED = RECENTLY_ADDED + 1;
    public static final int MY_FAVORITE = RECENTLY_PLAYED + 1;
    public static final int ALBUM_DETAIL = MY_FAVORITE + 1;
    public static final int ARTIST_DETAIL = ALBUM_DETAIL + 1;

    //AlbumDetail
    public static final String TRANSITION_NAME = "transition_name";
    public static final String ALBUM_ID = "album_id";
    public static final String ALBUM_NAME = "album_name";

    //ArtistDetail
    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";

    //Category artwork
    public static final String CATEGORY_RECENTLY_ADDED = "category_recently_added";
    public static final String CATEGORY_RECENTLY_PLAYED = "category_recently_played";
    public static final String CATEGORY_MY_FAVORITE = "category_my_favorite";
    public static final String CATEGORY_ARTIST = "category_artist";

    //MyFavorite
    public static final String SONG_ID = "song_id";
}
