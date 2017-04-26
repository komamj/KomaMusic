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
package com.koma.music.play;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseActivity;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by koma on 4/5/17.
 */

public class MusicPlayerFragment extends Fragment implements MusicPlayerContract.View,
        MusicStateListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = MusicPlayerFragment.class.getSimpleName();

    @BindView(R.id.tv_track_name)
    TextView mTrackName;
    @BindView(R.id.tv_artist_name)
    TextView mArtistName;
    @BindView(R.id.iv_album)
    protected ImageView mAlbum;
    @BindView(R.id.iv_pause)
    ImageView mPauseOrPlay;

    @OnClick(R.id.iv_pause)
    void doPauseOrPlay() {
        MusicUtils.playOrPause();
    }

    @BindView(R.id.iv_blur)
    ImageView mBlurImageView;
    @BindView(R.id.song_progress)
    SeekBar mSongProgress;
    @BindView(R.id.song_elapsed_time)
    TextView mSongElapsedTime;
    @BindView(R.id.song_duration)
    TextView mDuration;
    @BindView(R.id.iv_my_favorite)
    ImageView mFavorite;

    @OnClick(R.id.iv_my_favorite)
    void doMyFavorite() {
    }

    @BindView(R.id.iv_play_mode)
    ImageView mPlayMode;

    @OnClick(R.id.iv_play_mode)
    void switchPlayMode() {

    }

    @OnClick(R.id.iv_previous)
    void doPrev() {
        MusicUtils.previous(mContext, false);
    }

    @OnClick(R.id.iv_next)
    void doNext() {
        MusicUtils.asyncNext(mContext);
    }

    private Context mContext;

    @NonNull
    private MusicPlayerContract.Presenter mPresenter;

    private Runnable mUpdateProgress;
    private Handler mHandler;

    public MusicPlayerFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        ButterKnife.bind(this, view);

        mSongProgress.setOnSeekBarChangeListener(this);

        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((BaseActivity) getActivity()).removeMusicStateListenerListener(this);

        removeUpdate();
    }

    private void init() {
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");

        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        LogUtils.i(TAG, "onStop");

        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    public void setPresenter(@NonNull MusicPlayerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateFavoriteView() {

    }

    @Override
    public void updateRepeatView() {

    }

    @Override
    public void updateBlurArtWork() {
        LogUtils.i(TAG, "updateBlurArtWork");
        if (mPresenter != null) {
            mPresenter.doBlurArtWork();
        }
    }

    @Override
    public void setBlurArtWork(Drawable blurArtWork) {
        if (blurArtWork != null) {
            if (mBlurImageView.getDrawable() != null) {
                final TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                        mBlurImageView.getDrawable(), blurArtWork});
                mBlurImageView.setImageDrawable(td);
                td.startTransition(200);
            } else {
                mBlurImageView.setImageDrawable(blurArtWork);
            }
        }
    }

    @Override
    public void updateAlbumImage() {
        LogUtils.i(TAG, "updateAlbumImage");

        Glide.with(this).load(Utils.getAlbumArtUri(
                MusicUtils.getCurrentAlbumId()))
                .placeholder(R.drawable.ic_album)
                .into(mAlbum);
    }

    @Override
    public void updateTitle() {
        mTrackName.setText(MusicUtils.getTrackName());

        mArtistName.setText(MusicUtils.getArtistName());
    }

    @Override
    public void updateProgressAndElapsedTime(int progress) {
        mSongElapsedTime.setText(MusicUtils.makeShortTimeString(getActivity(), MusicUtils.position() / 1000));
        mSongProgress.setProgress(progress);
    }

    /**
     * Creates and posts the update runnable to the handler
     */
    private void postUpdate() {
        if (mUpdateProgress == null) {
            mUpdateProgress = new Runnable() {
                @Override
                public void run() {
                    long currentSongDuration = MusicUtils.duration();
                    long currentSongProgress = MusicUtils.position();

                    int progress = 0;

                    if (currentSongDuration > 0) {
                        progress = (int) (mSongProgress.getMax() * currentSongProgress / currentSongDuration);
                    }


                    updateProgressAndElapsedTime(progress);
                    mHandler.postDelayed(mUpdateProgress, MusicUtils.UPDATE_FREQUENCY_MS);
                }
            };
        }

        // remove any existing callbacks
        mHandler.removeCallbacks(mUpdateProgress);

        // post ourselves as a delayed
        mHandler.post(mUpdateProgress);
    }

    /**
     * Removes the runnable from the handler
     */
    private void removeUpdate() {
        if (mUpdateProgress != null) {
            mHandler.removeCallbacks(mUpdateProgress);
        }
    }

    /**
     * update duration time
     */
    @Override
    public void updateDuration() {
        String duration = MusicUtils.makeShortTimeString(mContext, MusicUtils.duration() / 1000);
        if (!mDuration.getText().equals(duration)) {
            mDuration.setText(duration);
        }
    }

    @Override
    public void updateNowPlayingInfo() {
        updateAlbumImage();

        updateTitle();

        postUpdate();

        updateDuration();

        onPlayStateChanged();

        updateBlurArtWork();

        updateAlbumImage();

        updateFavoriteView();
    }

    @Override
    public void refreshData() {
        LogUtils.i(TAG, "refreshData");
    }

    @Override
    public void onPlaylistChanged() {
        LogUtils.i(TAG, "onPlaylistChanged");
    }

    @Override
    public void onMetaChanged() {
        LogUtils.i(TAG, "onMetaChanged");

        mSongProgress.setMax((int) MusicUtils.duration());

        updateNowPlayingInfo();
    }

    @Override
    public void onPlayStateChanged() {
        LogUtils.i(TAG, "onPlayStateChanged");

        boolean isPlaying = MusicUtils.isPlaying();

        mPauseOrPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

        if (isPlaying) {
            postUpdate();
        } else {
            removeUpdate();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            MusicUtils.seek((long) i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
