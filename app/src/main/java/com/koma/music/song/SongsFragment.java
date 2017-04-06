package com.koma.music.song;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.data.model.Song;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/20/17.
 */

public class SongsFragment extends BaseFragment implements SongsContract.View {
    private static final String TAG = SongsFragment.class.getSimpleName();
    @NonNull
    private SongsContract.Presenter mPresenter;

    private SongsAdapter mAdapter;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        init();
        return view;
    }

    private void init() {
        mAdapter = new SongsAdapter(new ArrayList<Song>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart");
        mContext.getContentResolver().registerContentObserver(Constants.SONG_URI, true,
                mSongsObserver);
    }

    private ContentObserver mSongsObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            mHandler.removeCallbacks(SongsFragment.this);
            mHandler.postDelayed(SongsFragment.this, Constants.REFRESH_TIME);
        }
    };

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
    public void onStop() {
        super.onStop();
        LogUtils.i(TAG, "onStop");
    }

    @Override
    public void run() {
        if (mPresenter != null) {
            mPresenter.loadSongs();
        }
    }

    @Override
    public void setPresenter(@NonNull SongsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyView() {
        LogUtils.i(TAG, "showEmptyView");
    }

    @Override
    public void showLoadingView() {
        LogUtils.i(TAG, "showLoadingView");
    }

    @Override
    public void showSongs(List<Song> songs) {
        LogUtils.i(TAG, "showSongs'");
        mAdapter.replaceData(songs);
        mAdapter.notifyDataSetChanged();
    }
}
