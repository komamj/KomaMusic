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

import com.koma.music.R;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Playlist;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public class PlaylistsFragment extends BaseLoadingFragment implements PlaylistsConstract.View {
    private static final String TAG = PlaylistsFragment.class.getSimpleName();

    @NonNull
    private PlaylistsConstract.Presenter mPresenter;

    private PlaylistAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new PlaylistsPresenter(this, MusicRepository.getInstance());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new PlaylistAdapter(mContext, new ArrayList<Playlist>());

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
        return R.layout.fragment_base;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void hideLoadingView() {
        LogUtils.i(TAG, "showLoadingView");

        super.hideLoadingView();
    }

    @Override
    public void showPlaylist(List<Playlist> Playlists) {
        LogUtils.i(TAG, "showPlaylist");

        mAdapter.replaceData(Playlists);
    }

    @Override
    public void refreshData() {
        if (mPresenter != null) {
            mPresenter.loadPlaylists();
        }
    }

    @Override
    public void onPlaylistChanged() {
        if (mPresenter != null) {
            mPresenter.loadPlaylists();
        }
    }

    @Override
    public void onMetaChanged() {

    }
}
