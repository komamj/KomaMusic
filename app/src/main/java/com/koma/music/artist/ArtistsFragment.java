package com.koma.music.artist;

import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;

/**
 * Created by koma on 3/21/17.
 */

public class ArtistsFragment extends BaseFragment implements ArtistsConstract.View {
    private static final String TAG = ArtistsFragment.class.getSimpleName();
    @NonNull
    private ArtistsConstract.Presenter mPresenter;

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void setPresenter(@NonNull ArtistsConstract.Presenter presenter) {
        mPresenter = presenter;
    }
}
