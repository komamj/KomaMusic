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
package com.koma.music.detail.artistdetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.koma.music.R;
import com.koma.music.album.AlbumsAdapter;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.source.local.MusicRepository;
import com.koma.music.data.model.Album;
import com.koma.music.detail.DetailsActivity;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 5/5/17.
 */

public class ArtistDetailFragment extends BaseLoadingFragment implements ArtistDetailContract.View {
    private static final String TAG = ArtistDetailFragment.class.getSimpleName();

    private long mArtistId;

    @NonNull
    private ArtistDetailContract.Presenter mPresenter;

    private AlbumsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ArtistDetailPresenter(this, MusicRepository.getInstance());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtils.i(TAG, "onViewCreated");

        init();
    }

    private void init() {
        if (getArguments() != null) {
            mArtistId = getArguments().getLong(Constants.ARTIST_ID, -1);
        }

        new ArtistDetailPresenter(this, MusicRepository.getInstance());

        mAdapter = new AlbumsAdapter(mContext, new ArrayList<Album>());

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
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

        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    public void setPresenter(@NonNull ArtistDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base;
    }

    @Override
    public long getArtistId() {
        return this.mArtistId;
    }

    @Override
    public void showArtistAlbums(List<Album> albumList) {
        if (mAdapter != null) {
            mAdapter.replaceData(albumList);
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

    @Override
    public void refreshData() {
        if (mPresenter != null) {
            mPresenter.loadArtistAlbums(mArtistId);
            mPresenter.loadArtWork(mArtistId);
        }
    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }
}
