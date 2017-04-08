package com.koma.music.data.local;

import com.koma.music.data.model.Song;
import com.koma.music.util.KomaUtils;

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
                subscriber.onNext(KomaUtils.getAllSongs());
                subscriber.onCompleted();
            }
        });
    }

}
