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

import com.koma.music.R;
import com.koma.music.base.BaseActivity;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by koma on 4/5/17.
 */

public class MusicPlayerFragment extends Fragment implements MusicPlayerContract.View, MusicStateListener {
    private static final String TAG = MusicPlayerFragment.class.getSimpleName();
    @BindView(R.id.iv_blur)
    ImageView mBlurImageView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.song_progress)
    SeekBar mSongProgress;
    @BindView(R.id.song_elapsed_time)
    TextView mSongElapsedTime;
    @BindView(R.id.song_duration)
    TextView mDuration;

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

    public MusicPlayerFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
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
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
        init();
    }

    private void init() {
        if (mToolbar != null) {
            ((MusicPlayerActivity) getActivity()).setSupportActionBar(mToolbar);
            final ActionBar ab = ((MusicPlayerActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
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
    }

    @Override
    public void setAlbumImage() {

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
        updateBlurArtWork();
        updateAlbumImage();
        updateFavoriteView();
    }
}
