package com.koma.music.data.local;

import com.koma.music.data.model.Song;

import java.util.List;

import rx.Observable;

/**
 * Created by koma on 3/20/17.
 */

public class MusicRepository implements MusicDataSource {
    private static final String TAG = MusicRepository.class.getSimpleName();

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
    public Observable<List<Song>> getAllSongs() {
        return mLocalDataSource.getAllSongs();
    }
}
