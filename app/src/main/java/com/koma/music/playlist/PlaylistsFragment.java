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

import android.support.annotation.NonNull;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.widget.LoadingView;

import butterknife.BindView;

/**
 * Created by koma on 3/21/17.
 */

public class PlaylistsFragment extends BaseFragment implements PlaylistsConstract.View {
    private static final String TAG = PlaylistsFragment.class.getSimpleName();

    @BindView(R.id.loding_view)
    protected LoadingView mLoadingView;

    @NonNull
    private PlaylistsConstract.Presenter mPresenter;

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void setPresenter(@NonNull PlaylistsConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base;
    }
}
