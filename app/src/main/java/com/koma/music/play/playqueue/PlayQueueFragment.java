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
package com.koma.music.play.playqueue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.koma.music.R;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.song.SongsAdapter;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by koma on 4/27/17.
 */

public class PlayQueueFragment extends BaseLoadingFragment implements PlayQueueContract.View {
    private static final String TAG = PlayQueueFragment.class.getSimpleName();

    @NonNull
    private PlayQueueContract.Presenter mPresenter;

    private SongsAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        new PlayQueuePresenter(this, MusicRepository.getInstance());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new SongsAdapter(mContext, new ArrayList<Song>());

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
    public void setPresenter(@NonNull PlayQueueContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void hideLoadingView() {
        super.hideLoadingView();
    }

    @Override
    public void showPlayQueueSongs(List<Song> songs) {
        LogUtils.i(TAG, "showPlayQueue size : " + songs.size());

        mAdapter.replaceData(songs);
    }

    @Override
    public void refreshData() {
        LogUtils.i(TAG, "refreshData");

        if (mPresenter != null) {
            mPresenter.loadPlayQueue();
        }
    }

    @Override
    public void onPlaylistChanged() {
        LogUtils.i(TAG, "onPlaylistChanged");

        if (mPresenter != null) {
            mPresenter.loadPlayQueue();
        }
    }

    @Override
    public void onMetaChanged() {
        LogUtils.i(TAG, "onMetaChanged");

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPlayStateChanged() {

    }
}
