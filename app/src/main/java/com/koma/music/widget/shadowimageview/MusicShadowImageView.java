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
package com.koma.music.widget.shadowimageview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by koma on 4/24/17.
 */

public class MusicShadowImageView extends CardView {
    private Context mContext;

    @BindView(R.id.iv_album)
    ImageView mAlbum;

    public MusicShadowImageView(Context context) {
        super(context);

        init(context);
    }

    public MusicShadowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicShadowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

        setElevation(100);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        LayoutInflater.from(mContext).inflate(R.layout.music_shadow_layout, this);

        ButterKnife.bind(this, this);
    }

    public void updateArtWork() {
        Glide.with(mContext).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .placeholder(R.drawable.ic_album)
                .into(mAlbum);
    }
}
