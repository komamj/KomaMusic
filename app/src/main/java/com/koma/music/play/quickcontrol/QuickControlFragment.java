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
package com.koma.music.play.quickcontrol;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseMusicStateFragment;
import com.koma.music.play.MusicPlayerActivity;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 4/20/17.
 */

public class QuickControlFragment extends BaseMusicStateFragment implements
        SlidingUpPanelLayout.PanelSlideListener {
    private static final String TAG = QuickControlFragment.class.getSimpleName();

    @BindView(R.id.tv_track_name)
    TextView mTrackName;
    @BindView(R.id.tv_artist_name)
    TextView mArtistName;
    @BindView(R.id.iv_album)
    ImageView mAlbum;
    @BindView(R.id.audio_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.iv_pause)
    ImageView mPauseOrPlay;

    @OnClick(R.id.iv_pause)
    void doPauseOrPlay() {
        MusicUtils.playOrPause();
    }

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.audio_player_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    private void init() {
        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);

                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), Pair.create((View) mTrackName, "share_track_name"),
                        Pair.create((View) mArtistName, "share_artist_name")).toBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        onMetaChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LogUtils.i(TAG, "onDestroyView");
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        LogUtils.i(TAG, "onMetaChanged");

        Glide.with(this).load(Utils.getAlbumArtUri(
                MusicUtils.getCurrentAlbumId()))
                .placeholder(R.drawable.ic_album)
                .into(mAlbum);

        mTrackName.setText(MusicUtils.getTrackName());

        mArtistName.setText(MusicUtils.getArtistName());

        onPlayStateChanged();
    }

    @Override
    public void onPlayStateChanged() {
        LogUtils.i(TAG, "onPlayStateChanged");

        mPauseOrPlay.setImageResource(MusicUtils.isPlaying() ? R.drawable.ic_pause_header :
                R.drawable.ic_play_header);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        LogUtils.i(TAG, "onPanelSlide slideOffeset :" + slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                    SlidingUpPanelLayout.PanelState newState) {

    }
}
