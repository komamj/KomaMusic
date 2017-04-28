package com.koma.music.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import com.koma.music.R;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.model.Album;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public class AlbumsFragment extends BaseLoadingFragment implements AlbumsConstract.View {
    private static final String TAG = AlbumsFragment.class.getSimpleName();

    @NonNull
    private AlbumsConstract.Presenter mPresenter;

    private AlbumsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        mAdapter = new AlbumsAdapter(mContext, new ArrayList<Album>());

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");
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
    public void onStop() {
        super.onStop();

        LogUtils.i(TAG, "onStop");
    }

    @Override
    public void setPresenter(@NonNull AlbumsConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyView() {
        LogUtils.i(TAG, "showEmptyView");

        super.showEmptyView();
    }

    @Override
    public void hideLoadingView() {
        LogUtils.i(TAG, "hideLoadingView");

        super.hideLoadingView();
    }

    @Override
    public void showAlbums(List<Album> albums) {
        LogUtils.i(TAG, "showAlbums");
        mAdapter.replaceData(albums);
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
