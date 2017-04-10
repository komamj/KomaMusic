package com.koma.music.play;

import android.os.Bundle;

import com.koma.music.R;
import com.koma.music.base.BaseActivity;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/20/17.
 */

public class MusicPlayerActivity extends BaseActivity {
    private static final String TAG = MusicPlayerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        LogUtils.i(TAG, "onStop");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_player;
    }
}
