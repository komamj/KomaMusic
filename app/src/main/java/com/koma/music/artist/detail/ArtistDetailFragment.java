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
package com.koma.music.artist.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.song.SongsAdapter;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 5/5/17.
 */

public class ArtistDetailFragment extends BaseFragment implements ArtistDetailContract.View {
    private static final String TAG = ArtistDetailFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_album)
    ImageView mAlbum;
    @BindView(R.id.fab_play)
    FloatingActionButton mFabPlay;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @OnClick(R.id.fab_play)
    void doPlayAlbum() {

    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private long mArtistId;

    private String mArtistName;

    @NonNull
    private ArtistDetailContract.Presenter mPresenter;

    private SongsAdapter mAdapter;

    public static ArtistDetailFragment newInstance(long id, String name, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        args.putString(Constants.ARTIST_NAME, name);
        args.putString(Constants.TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        if (getArguments() != null) {
            mArtistId = getArguments().getLong(Constants.ARTIST_ID, -1);
            mArtistName = getArguments().getString(Constants.ARTIST_NAME);
        }

        if (!TextUtils.isEmpty(mArtistName)) {
            collapsingToolbarLayout.setTitle(mArtistName);
        }

        mAlbum.setTransitionName(getArguments().getString(Constants.TRANSITION_NAME));

        LogUtils.i(TAG, "transiiton name : " + mAlbum.getTransitionName());
        new ArtistDetailPresenter(this, MusicRepository.getInstance());

        mAdapter = new SongsAdapter(mContext, new ArrayList<Song>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

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
        return R.layout.activity_album_detail;
    }

    @Override
    public long getArtistId() {
        return this.mArtistId;
    }

    @Override
    public void showArtistSongs(List<Song> songList) {

    }

    @Override
    public void showArtwork(Drawable albumArt) {
        mAlbum.setImageDrawable(albumArt);
    }

    @Override
    public void showArtwork(Bitmap bitmap) {
        mAlbum.setImageBitmap(bitmap);
    }
}
