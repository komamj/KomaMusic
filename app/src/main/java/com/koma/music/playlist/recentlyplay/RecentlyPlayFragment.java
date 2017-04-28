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

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.koma.music.base.BaseFragment;
import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 4/20/17.
 */

public class RecentlyPlayFragment extends BaseLoadingFragment implements RecentlyPlayContract.View {
    private static final String TAG = RecentlyPlayFragment.class.getSimpleName();

    @NonNull
    private RecentlyPlayContract.Presenter mPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");
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
        return 0;
    }

    @Override
    public void setPresenter(@NonNull RecentlyPlayContract.Presenter presenter) {
        mPresenter = presenter;
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
}
