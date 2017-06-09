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
package com.koma.music.data.local;

import com.koma.music.data.model.Album;
import com.koma.music.data.model.Artist;
import com.koma.music.data.model.Playlist;
import com.koma.music.data.model.Song;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by koma on 3/20/17.
 */

public class MusicRepository implements MusicDataSource {
    private static MusicRepository mRepostory;

    private LocalDataSource mLocalDataSource;

    private MusicRepository() {
        mLocalDataSource = new LocalDataSource();
    }

    public synchronized static MusicRepository getInstance() {
        if (mRepostory == null) {
            synchronized (MusicRepository.class) {
                if (mRepostory == null) {
                    mRepostory = new MusicRepository();
                }
            }
        }
        return mRepostory;
    }

    @Override
    public Flowable<List<Song>> getAllSongs() {
        return mLocalDataSource.getAllSongs();
    }

    @Override
    public Flowable<List<Playlist>> getAllPlaylists() {
        return mLocalDataSource.getAllPlaylists();
    }

    @Override
    public Flowable<List<Album>> getAllAlbums() {
        return mLocalDataSource.getAllAlbums();
    }

    @Override
    public Flowable<List<Album>> getArtistAlbums(long artistId) {
        return mLocalDataSource.getArtistAlbums(artistId);
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        return mLocalDataSource.getAllArtists();
    }

    @Override
    public Flowable<List<Song>> getQueueSongs() {
        return mLocalDataSource.getQueueSongs();
    }

    @Override
    public Flowable<List<Song>> getAlbumSongs(long albumId) {
        return mLocalDataSource.getAlbumSongs(albumId);
    }

    @Override
    public Flowable<List<Song>> getRecentlyAddedSongs() {
        return mLocalDataSource.getRecentlyAddedSongs();
    }

    @Override
    public Flowable<List<Song>> getRecentlyPlayedSongs() {
        return mLocalDataSource.getRecentlyPlayedSongs();
    }

    @Override
    public Flowable<List<Song>> getMyFavoriteSongs() {
        return mLocalDataSource.getMyFavoriteSongs();
    }
}
