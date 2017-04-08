package com.koma.music.genre;

import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;

/**
 * Created by koma on 3/21/17.
 */

public class GenresFragment extends BaseFragment implements GenresContract.View {
    private static final String TAG = GenresFragment.class.getSimpleName();
    @NonNull
    private GenresContract.Presenter mPresenter;

    @Override
    public void run() {

    }

    @Override
    public void setPresenter(@NonNull GenresContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
