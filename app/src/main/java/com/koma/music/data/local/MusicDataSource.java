package com.koma.music.data.local;

import com.koma.music.data.model.Song;

import java.util.List;

import rx.Observable;

/**
 * Created by koma on 3/20/17.
 */

public interface MusicDataSource {
    Observable<List<Song>> getAllSongs();
}
