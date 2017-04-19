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

import com.koma.music.album.AlbumsPresenter;
import com.koma.music.artist.ArtistsPresenter;
import com.koma.music.data.model.Album;
import com.koma.music.data.model.Artist;
import com.koma.music.data.model.Playlist;
import com.koma.music.data.model.Song;
import com.koma.music.playlist.PlaylistsPresenter;
import com.koma.music.song.SongsPresenter;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by koma on 3/20/17.
 */

public class LocalDataSource implements MusicDataSource {
    @Override
    public Observable<List<Song>> getAllSongs() {
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                subscriber.onNext(SongsPresenter.getAllSongs());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Playlist>> getAllPlaylists() {
        return Observable.create(new Observable.OnSubscribe<List<Playlist>>() {
            @Override
            public void call(Subscriber<? super List<Playlist>> subscriber) {
                subscriber.onNext(PlaylistsPresenter.getAllPlaylists());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Album>> getAllAlbums() {
        return Observable.create(new Observable.OnSubscribe<List<Album>>() {
            @Override
            public void call(Subscriber<? super List<Album>> subscriber) {
                subscriber.onNext(AlbumsPresenter.getAllAlbums());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Artist>> getAllArtists() {
        return Observable.create(new Observable.OnSubscribe<List<Artist>>() {
            @Override
            public void call(Subscriber<? super List<Artist>> subscriber) {
                subscriber.onNext(ArtistsPresenter.getAllArtists());
                subscriber.onCompleted();
            }
        });
    }

}
