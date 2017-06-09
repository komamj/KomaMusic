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
import com.koma.music.detail.albumdetail.AlbumDetailPresenter;
import com.koma.music.play.playqueue.PlayQueuePresenter;
import com.koma.music.playlist.PlaylistsPresenter;
import com.koma.music.playlist.myfavorite.MyFavoritePresenter;
import com.koma.music.playlist.recentlyadd.RecentlyAddedPresenter;
import com.koma.music.playlist.recentlyplay.RecentlyPlayPresenter;
import com.koma.music.song.SongsPresenter;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;


/**
 * Created by koma on 3/20/17.
 */

public class LocalDataSource implements MusicDataSource {
    @Override
    public Flowable<List<Song>> getAllSongs() {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(SongsPresenter.getAllSongs());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Playlist>> getAllPlaylists() {
        return Flowable.create(new FlowableOnSubscribe<List<Playlist>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Playlist>> e) throws Exception {
                e.onNext(PlaylistsPresenter.getAllPlaylists());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Album>> getAllAlbums() {
        return Flowable.create(new FlowableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Album>> e) throws Exception {
                e.onNext(AlbumsPresenter.getAllAlbums());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Album>> getArtistAlbums(final long artistId) {
        return Flowable.create(new FlowableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Album>> e) throws Exception {
                e.onNext(AlbumsPresenter.getArtistAlbums(artistId));
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        return Flowable.create(new FlowableOnSubscribe<List<Artist>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Artist>> e) throws Exception {
                e.onNext(ArtistsPresenter.getAllArtists());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Song>> getQueueSongs() {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(PlayQueuePresenter.getQueueSongs());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Song>> getAlbumSongs(final long albumId) {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(AlbumDetailPresenter.getAlbumSongs(albumId));
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Song>> getRecentlyAddedSongs() {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(RecentlyAddedPresenter.getRecentlyAddedSongs());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Song>> getRecentlyPlayedSongs() {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(RecentlyPlayPresenter.getRecentlyPlaySongs());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Flowable<List<Song>> getMyFavoriteSongs() {
        return Flowable.create(new FlowableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Song>> e) throws Exception {
                e.onNext(MyFavoritePresenter.getFavoriteSongs());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

}
