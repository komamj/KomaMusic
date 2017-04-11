package com.koma.music.play;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koma.music.R;
import com.koma.music.base.BaseActivity;
import com.koma.music.base.BaseFragment;
import com.koma.music.listener.MusicStateListener;
import com.koma.music.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by koma on 4/5/17.
 */

public class MusicPlayerFragment extends BaseFragment implements MusicPlayerContract.View, MusicStateListener {
    private static final String TAG = MusicPlayerFragment.class.getSimpleName();
    @BindView(R.id.iv_blur)
    ImageView mBlurImageView;
    @BindView(R.id.iv_finish)
    ImageView mExitImageView;

    @OnClick(R.id.iv_finish)
    void finish() {
        getActivity().finish();
    }

    @NonNull
    private MusicPlayerContract.Presenter mPresenter;

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
    public void updatePlayOrPauseView() {

    }

    @Override
    public void updateFavoriteView() {

    }

    @Override
    public void updateRepeatView() {

    }

    @Override
    public void updateTitle() {

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
        updateTitle();
        updateAlbumImage();
        updatePlayOrPauseView();
        updateFavoriteView();
    }
}
