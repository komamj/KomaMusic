package com.koma.music.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import com.koma.music.R;
import com.koma.music.base.BaseFragment;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/21/17.
 */

public class AlbumsFragment extends BaseFragment implements AlbumsConstract.View {
    private static final String TAG = AlbumsFragment.class.getSimpleName();
    @NonNull
    private AlbumsConstract.Presenter mPresenter;

    private AlbumsAdapter mAdapter;

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.i(TAG, "onActivityCreated");

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);

    }

    @Override
    public void setPresenter(@NonNull AlbumsConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base;
    }
}
