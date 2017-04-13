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
package com.koma.music.song;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/20/17.
 */

public class SongsFragment extends BaseFragment implements SongsContract.View {
    private static final String TAG = SongsFragment.class.getSimpleName();
    @NonNull
    private SongsContract.Presenter mPresenter;

    private SongsAdapter mAdapter;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_songs;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtils.i(TAG, "onViewCreated");

        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

    }

    private void init() {
        mAdapter = new SongsAdapter(new ArrayList<Song>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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
    public void setPresenter(@NonNull SongsContract.Presenter presenter) {
        mPresenter = presenter;
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
        LogUtils.i(TAG, "showLoadingView");

        mLoadingView.onLoadingFinished();
    }

    @Override
    public void showSongs(List<Song> songs) {
        LogUtils.i(TAG, "showSongs");

        mAdapter.replaceData(songs);
        mAdapter.notifyDataSetChanged();
    }
}
