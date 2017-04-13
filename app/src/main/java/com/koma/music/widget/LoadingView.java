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
package com.koma.music.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koma.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by koma on 4/8/17.
 */

public class LoadingView extends FrameLayout {
    private Context mContext;

    @BindView(R.id.pb_loading)
    ProgressBar mProgressBar;

    @BindView(R.id.tv_empty)
    TextView mEmptyView;

    public LoadingView(@NonNull Context context) {
        super(context);

        mContext = context;
    }

    public LoadingView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        inflate(mContext, R.layout.loading_view, this);

        ButterKnife.bind(this, this);
    }

    public void onLoadingFinished() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void showEmptyView(String emptyInfo) {
        mEmptyView.setText(emptyInfo);
    }
}
