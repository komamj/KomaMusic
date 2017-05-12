package com.koma.music.playlist.myfavorite;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.koma.music.base.BaseLoadingFragment;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Song;
import com.koma.music.detail.DetailsActivity;
import com.koma.music.util.LogUtils;

import java.util.List;

/**
 * Created by koma on 4/20/17.
 */

public class MyFavoriteFragment extends BaseLoadingFragment implements MyFavoriteContract.View {
    private static final String TAG = MyFavoriteFragment.class.getSimpleName();

    @NonNull
    private MyFavoriteContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MyFavoritePresenter(this, MusicRepository.getInstance());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.i(TAG, "onResume");

        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i(TAG, "onPause");

        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void setPresenter(@NonNull MyFavoriteContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void hideLoadingView() {

    }

    @Override
    public void showFavoriteSongs(List<Song> songs) {

    }

    @Override
    public void showArtwork(Drawable albumArt) {
        ((DetailsActivity) getActivity()).showAlbum(albumArt);
    }

    @Override
    public void showArtwork(Bitmap bitmap) {
        ((DetailsActivity) getActivity()).showAlbum(bitmap);
    }
}
