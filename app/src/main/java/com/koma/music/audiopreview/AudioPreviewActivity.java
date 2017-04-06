package com.koma.music.audiopreview;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.koma.music.base.BaseActivity;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/21/17.
 */

public class AudioPreviewActivity extends BaseActivity implements AudioPreviewContract.View {
    private static final String TAG = AudioPreviewActivity.class.getSimpleName();

    @NonNull
    AudioPreviewContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void setPresenter(@NonNull AudioPreviewContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
