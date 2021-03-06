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
package com.koma.music.playlist.recentlyplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.koma.music.R;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.source.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.detail.DetailsActivity;
import com.koma.music.song.SongsAdapter;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 4/20/17.
 */

public class RecentlyPlayFragment extends BaseLoadingFragment implements RecentlyPlayContract.View {
    private static final String TAG = RecentlyPlayFragment.class.getSimpleName();

    private SongsAdapter mAdapter;

    @NonNull
    private RecentlyPlayContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RecentlyPlayPresenter(this, MusicRepository.getInstance());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        mAdapter = new SongsAdapter(mContext, new ArrayList<Song>());
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
        return R.layout.fragment_base;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setPresenter(@NonNull RecentlyPlayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshData() {
        if (mPresenter != null) {
            mPresenter.loadRecentlyPlayedSongs();
        }
    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyView() {
        super.showEmptyView();
    }

    @Override
    public void hideLoadingView() {
        super.hideLoadingView();
    }

    @Override
    public void showPlayedSongs(List<Song> songs) {
        if (mAdapter != null) {
            mAdapter.replaceData(songs);
        }
    }

    @Override
    public void showArtwork(Drawable albumArt) {
        ((DetailsActivity) getActivity()).showAlbum(albumArt);
    }

    @Override
    public void showArtwork(Bitmap bitmap) {
        ((DetailsActivity) getActivity()).showAlbum(bitmap);
    }
}
