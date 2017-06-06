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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koma.music.R;
import com.koma.music.service.MusicServiceConstants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by koma on 4/5/17.
 */

public class MusicPlayerFragment extends Fragment implements MusicPlayerContract.View,
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = MusicPlayerFragment.class.getSimpleName();

    @BindView(R.id.tv_track_name)
    TextView mTrackName;
    @BindView(R.id.tv_artist_name)
    TextView mArtistName;
    @BindView(R.id.iv_pause)
    ImageView mPauseOrPlay;

    @OnClick(R.id.iv_pause)
    void doPauseOrPlay() {
        MusicUtils.playOrPause();
    }

    @BindView(R.id.song_progress)
    SeekBar mSongProgress;
    @BindView(R.id.song_elapsed_time)
    TextView mSongElapsedTime;
    @BindView(R.id.song_duration)
    TextView mDuration;
    @BindView(R.id.iv_favorite)
    ImageView mFavorite;

    @BindView(R.id.iv_album)
    ImageView mAlbum;

    @OnClick(R.id.iv_favorite)
    void doMyFavorite() {
        mFavorite.setImageResource(R.drawable.ic_favorite);
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

        removeUpdate();
    }

    private void init() {
        mSongProgress.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");

        registerPlaybackStatusReceiver();

        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    private void registerPlaybackStatusReceiver() {
        IntentFilter filter = new IntentFilter();
        // if a queue has changed,notify us
        // filter.addAction(MusicServiceConstants.QUEUE_CHANGED);
        // Play and pause changes
        filter.addAction(MusicServiceConstants.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicServiceConstants.META_CHANGED);
        // Update a list, probably the playlist fragment's
        // filter.addAction(MusicServiceConstants.REFRESH);
        // If a playlist has changed, notify us
        //filter.addAction(MusicServiceConstants.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicServiceConstants.TRACK_ERROR);

        mContext.registerReceiver(mPlaybackStatus, filter);
    }

    private final BroadcastReceiver mPlaybackStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            LogUtils.i(TAG, "onReceive action : " + action);
            if (action.equals(MusicServiceConstants.META_CHANGED)) {
                onMetaChanged();
            } else if (action.equals(MusicServiceConstants.PLAYSTATE_CHANGED)) {
                onPlayStateChanged();
            } else if (action.equals(MusicServiceConstants.TRACK_ERROR)) {
                final String errorMsg = context.getString(R.string.error_playing_track,
                        intent.getStringExtra(MusicServiceConstants.TRACK_NAME));
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();

        LogUtils.i(TAG, "onStop");

        mContext.unregisterReceiver(mPlaybackStatus);

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
    public void updateArtwork(Bitmap artwork) {
        mAlbum.setImageBitmap(artwork);
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
        if (mPresenter != null) {
            mPresenter.updateArtWork();
        }

        updateTitle();

        postUpdate();

        updateDuration();

        onPlayStateChanged();

        updateFavoriteView();
    }

    private void onMetaChanged() {
        LogUtils.i(TAG, "onMetaChanged");

        mSongProgress.setMax((int) MusicUtils.duration());

        updateNowPlayingInfo();
    }

    private void onPlayStateChanged() {
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
