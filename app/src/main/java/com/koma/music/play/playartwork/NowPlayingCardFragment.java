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
package com.koma.music.play.playartwork;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseMusicStateFragment;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import butterknife.BindView;

/**
 * Created by koma on 4/27/17.
 */

public class NowPlayingCardFragment extends BaseMusicStateFragment {
    private static final String TAG = NowPlayingCardFragment.class.getSimpleName();

    @BindView(R.id.iv_artwork)
    public ImageView mAlbum;


    @Override
    public void onResume() {
        super.onResume();

        updateAlbumImage();
    }

    private void updateAlbumImage() {
        Glide.with(this).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .placeholder(R.drawable.ic_album)
                .into(mAlbum);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.now_playing_card_layout;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        updateAlbumImage();
    }

    @Override
    public void onPlayStateChanged() {

    }
}
