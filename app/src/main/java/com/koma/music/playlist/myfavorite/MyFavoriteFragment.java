package com.koma.music.playlist.myfavorite;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/20/17.
 */

public class MyFavoriteFragment extends BaseLoadingFragment implements MyFavoriteContract.View {
    private static final String TAG = MyFavoriteFragment.class.getSimpleName();

    @NonNull
    private MyFavoriteContract.Presenter mPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.i(TAG, "onResume");

        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i(TAG, "onPause");

        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void setPresenter(@NonNull MyFavoriteContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }
}
