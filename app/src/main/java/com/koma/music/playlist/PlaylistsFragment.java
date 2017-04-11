package com.koma.music.playlist;

import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;

/**
 * Created by koma on 3/21/17.
 */

public class PlaylistsFragment extends BaseFragment implements PlaylistsConstract.View {
    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    @NonNull
    private PlaylistsConstract.Presenter mPresenter;

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void setPresenter(@NonNull PlaylistsConstract.Presenter presenter) {
        mPresenter = presenter;
    }
}
