package com.koma.music.album;

import android.support.annotation.NonNull;

/**
 * Created by koma on 3/21/17.
 */

public class AlbumsPresenter implements AlbumsConstract.Presenter {
    private static String TAG = AlbumsPresenter.class.getSimpleName();
    @NonNull
    private AlbumsConstract.View mView;

    private AlbumsPresenter(@NonNull AlbumsConstract.View view) {
        mView = view;
    }

    public static final AlbumsPresenter newInstance(@NonNull AlbumsConstract.View view) {
        return new AlbumsPresenter(view);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }
}
