package com.koma.music.playlist.myfavorite;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.koma.music.MusicApplication;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.local.db.FavoriteSong;
import com.koma.music.data.local.db.SortedCursor;
import com.koma.music.data.model.Song;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.LogUtils;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/20/17.
 */

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {
    private static final String TAG = MyFavoritePresenter.class.getSimpleName();

    @NonNull
    private MyFavoriteContract.View mView;

    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public MyFavoritePresenter(MyFavoriteContract.View view, MusicRepository repository) {
        mRepository = repository;

        mView = view;
        mView.setPresenter(this);

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void loadMyFavoriteSongs() {

    }

    @Override
    public void onLoadFinished(List<Song> songs) {

    }

    public static final List<Song> getFavoriteSongs() {
        return SongsPresenter.getSongsForCursor(getFavoriteSongCursor(), false);
    }

    public static Cursor getFavoriteSongCursor() {
        Cursor cursor = FavoriteSong.getInstance(MusicApplication.getContext()).getFavoriteSong();
        SortedCursor sortedCursor = SongsPresenter.makeSortedCursor(MusicApplication.getContext(),
                cursor, 0);
        return sortedCursor;
    }
}
