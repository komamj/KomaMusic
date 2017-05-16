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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.koma.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by koma on 5/16/17.
 */

public class ArtworkSeekBar extends FrameLayout {
    private Context mContext;

    @BindView(R.id.song_progress)
    SeekBar mSeekBar;
    @BindView(R.id.iv_album)
    ImageView mAlbum;

    LayoutParams mLayoutParams;

    public ArtworkSeekBar(@NonNull Context context) {
        super(context);

        mContext = context;

        mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public ArtworkSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArtworkSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        mLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        inflate(mContext, R.layout.artwork_seekbar_layout, this);

        ButterKnife.bind(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLayoutParams.setMargins(0, getMeasuredWidth() - mSeekBar.getHeight() / 2, 0, 0);
        mSeekBar.setLayoutParams(mLayoutParams);
    }
}
