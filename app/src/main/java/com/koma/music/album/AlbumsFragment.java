package com.koma.music.album;

import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;

/**
 * Created by koma on 3/21/17.
 */

public class AlbumsFragment extends BaseFragment implements AlbumsConstract.View {
    private static final String TAG = AlbumsFragment.class.getSimpleName();
    @NonNull
    private AlbumsConstract.Presenter mPresenter;

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void setPresenter(@NonNull AlbumsConstract.Presenter presenter) {
        mPresenter = presenter;
    }
}
