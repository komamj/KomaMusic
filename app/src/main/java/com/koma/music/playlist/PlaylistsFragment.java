/*
 * Copyright (C) 2017 Koma MJ
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.koma.music.playlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.data.model.Playlist;
import com.koma.music.util.LogUtils;
import com.koma.music.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 3/21/17.
 */

public class PlaylistsFragment extends BaseFragment implements PlaylistsConstract.View {
    private static final String TAG = PlaylistsFragment.class.getSimpleName();

    @BindView(R.id.loding_view)
    protected LoadingView mLoadingView;

    @BindView(R.id.iv_recently_played)
    ImageView mRecentlyPlayed;

    @BindView(R.id.iv_recently_added)
    ImageView mRecentlyAdded;
    @BindView(R.id.iv_my_favorite)
    ImageView mMyFavorite;

    @OnClick(R.id.iv_my_favorite)
    void launchFavorite() {

    }

    @OnClick(R.id.iv_recently_added)
    void launchRecentlyAdded() {

    }

    @OnClick(R.id.iv_recently_played)
    void launchRecentlyPlayed() {
    }

    @NonNull
    private PlaylistsConstract.Presenter mPresenter;

    private PlaylistAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        Glide.with(this).load("")
                .placeholder(R.mipmap.ic_default_playlist)
                .into(mRecentlyAdded);
        Glide.with(this).load("")
                .placeholder(R.mipmap.ic_default_playlist)
                .into(mRecentlyPlayed);
        Glide.with(this).load("")
                .placeholder(R.mipmap.ic_default_playlist)
                .into(mMyFavorite);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new PlaylistAdapter(new ArrayList<Playlist>());

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
    public void setPresenter(@NonNull PlaylistsConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_playlist;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void hideLoadingView() {
        LogUtils.i(TAG, "showLoadingView");

        mLoadingView.onLoadingFinished();
    }

    @Override
    public void showPlaylist(List<Playlist> Playlists) {
        LogUtils.i(TAG, "showPlaylist");

        mAdapter.replaceData(Playlists);
        mAdapter.notifyDataSetChanged();
    }
}
