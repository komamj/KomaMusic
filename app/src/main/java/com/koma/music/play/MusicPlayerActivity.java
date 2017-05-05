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

import android.os.Bundle;
import android.view.View;

import com.koma.music.R;
import com.koma.music.base.BaseMusicStateActivity;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/20/17.
 */

public class MusicPlayerActivity extends BaseMusicStateActivity {
    private static final String TAG = MusicPlayerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        super.onCreate(savedInstanceState);

        LogUtils.i(TAG, "onCreate");

        init();
    }

    private void init() {
        MusicPlayerFragment musicPlayerFragment =
                (MusicPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.main_player_ui);
        if (musicPlayerFragment == null) {
            musicPlayerFragment = new MusicPlayerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_player_ui, musicPlayerFragment).commit();
        }

        new MusicPlayerPresenter(this, musicPlayerFragment, MusicRepository.getInstance());
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
}
