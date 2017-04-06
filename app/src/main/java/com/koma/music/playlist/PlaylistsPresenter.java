package com.koma.music.playlist;

import android.support.annotation.NonNull;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/21/17.
 */

public class PlaylistsPresenter implements PlaylistsConstract.Presenter {
    private static final String TAG = PlaylistsPresenter.class.getSimpleName();
    @NonNull
    private PlaylistsConstract.View mView;

    private PlaylistsPresenter(@NonNull PlaylistsConstract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    public static PlaylistsPresenter newInstance(PlaylistsConstract.View view) {
        return new PlaylistsPresenter(view);
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");
    }
}
