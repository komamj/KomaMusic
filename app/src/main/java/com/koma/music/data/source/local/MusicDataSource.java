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
package com.koma.music.data.source.local;

import com.koma.music.data.model.Album;
import com.koma.music.data.model.Artist;
import com.koma.music.data.model.Playlist;
import com.koma.music.data.model.Song;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by koma on 3/20/17.
 */

public interface MusicDataSource {
    Flowable<List<Song>> getAllSongs();

    Flowable<List<Playlist>> getAllPlaylists();

    Flowable<List<Album>> getAllAlbums();

    Flowable<List<Album>> getArtistAlbums(long artistId);

    Flowable<List<Artist>> getAllArtists();

    Flowable<List<Song>> getQueueSongs();

    Flowable<List<Song>> getAlbumSongs(long albumId);

    Flowable<List<Song>> getRecentlyAddedSongs();

    Flowable<List<Song>> getRecentlyPlayedSongs();

    Flowable<List<Song>> getMyFavoriteSongs();
}
