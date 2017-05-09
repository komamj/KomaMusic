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
package com.koma.music.album.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.koma.music.R;
import com.koma.music.base.BaseActivity;
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
 * Created by koma on 5/4/17.
 */

public class AlbumDetailActivity extends BaseActivity implements AlbumDetailContract.View {
    private static final String TAG = AlbumDetailActivity.class.getSimpleName();

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

    private long mAlbumId;

    private String mAlbumName;

    @NonNull
    private AlbumDetailContract.Presenter mPresenter;

    private SongsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            mAlbumId = getIntent().getLongExtra(Constants.ALBUM_ID, -1);
            mAlbumName = getIntent().getStringExtra(Constants.ALBUM_NAME);
        }

        if (!TextUtils.isEmpty(mAlbumName)) {
            collapsingToolbarLayout.setTitle(mAlbumName);
        }

        mAlbum.setTransitionName(getIntent().getStringExtra("transition_name"));

        LogUtils.i(TAG, "transiiton name : " + mAlbum.getTransitionName());
        new AlbumDetailPresenter(this, MusicRepository.getInstance());

        mAdapter = new SongsAdapter(this, new ArrayList<Song>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

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
    public int getLayoutId() {
        return R.layout.activity_album_detail;
    }

    @Override
    public void setPresenter(@NonNull AlbumDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public long getAlbumId() {
        return this.mAlbumId;
    }

    @Override
    public void showAlbumSongs(List<Song> songList) {
        LogUtils.i(TAG, "showAlbumSongs size : " + songList.size());
        if (mAdapter != null) {
            mAdapter.replaceData(songList);
        }
    }

    @Override
    public void showAlbum(Drawable albumArt) {
        mAlbum.setImageDrawable(albumArt);
    }

    @Override
    public void showAlbum(Bitmap bitmap) {
        mAlbum.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == android.R.id.home) {
            this.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
