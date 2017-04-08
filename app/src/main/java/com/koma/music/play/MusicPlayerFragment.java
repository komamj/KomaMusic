package com.koma.music.play;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by koma on 4/5/17.
 */

public class MusicPlayerFragment extends BaseFragment implements MusicPlayerContract.View {
    private static final String TAG = MusicPlayerFragment.class.getSimpleName();
    @BindView(R.id.iv_blur)
    ImageView mBlurImageView;
    @BindView(R.id.iv_finish)
    ImageView mExitImageView;
    @BindView(R.id.tv_track_name)
    TextView mTrackName;
    @BindView(R.id.tv_artist_name)
    TextView mArtistName;

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
    public void run() {

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

    }

    @Override
    public void updateAlbumImage() {

    }
}
