package com.koma.music.artist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.data.model.Artist;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public class ArtistsFragment extends BaseFragment implements ArtistsConstract.View {
    private static final String TAG = ArtistsFragment.class.getSimpleName();
    @NonNull
    private ArtistsConstract.Presenter mPresenter;

    private ArtistsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        mAdapter = new ArtistsAdapter(new ArrayList<Artist>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

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

        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i(TAG, "onPause");

        mPresenter.unSubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();

        LogUtils.i(TAG, "onStop");
    }

    @Override
    public void setPresenter(@NonNull ArtistsConstract.Presenter presenter) {
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
    }

    @Override
    public void hideLoadingView() {
        LogUtils.i(TAG, "hideLoadingView");
    }

    @Override
    public void showArtists(List<Artist> artists) {
        LogUtils.i(TAG, "showArtists");
    }
}
