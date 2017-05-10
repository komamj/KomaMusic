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
package com.koma.music.detail.albumdetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.koma.music.R;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.detail.DetailsActivity;
import com.koma.music.song.SongsAdapter;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 5/9/17.
 */

public class AlbumDetailsFragment extends BaseLoadingFragment implements AlbumDetailContract.View {
    private static final String TAG = AlbumDetailsFragment.class.getSimpleName();

    private SongsAdapter mAdapter;

    @NonNull
    private AlbumDetailContract.Presenter mPresenter;

    private long mAlbumId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AlbumDetailPresenter(this, MusicRepository.getInstance());

        if (getArguments() != null) {
            mAlbumId = getArguments().getLong(Constants.ALBUM_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        mAdapter = new SongsAdapter(mContext, new ArrayList<Song>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(layoutManager.VERTICAL);

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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public Context getContext() {
        return mContext;
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
    protected int getLayoutId() {
        return R.layout.fragment_base;
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

    @Override
    public void setPresenter(AlbumDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public long getAlbumId() {
        return this.mAlbumId;
    }

    @Override
    public void showAlbumSongs(List<Song> songList) {
        if (mAdapter != null) {
            mAdapter.replaceData(songList);
        }
    }

    @Override
    public void showAlbum(Drawable albumArt) {
        ((DetailsActivity) getActivity()).showAlbum(albumArt);
    }

    @Override
    public void showAlbum(Bitmap bitmap) {
        ((DetailsActivity) getActivity()).showAlbum(bitmap);
    }
}
