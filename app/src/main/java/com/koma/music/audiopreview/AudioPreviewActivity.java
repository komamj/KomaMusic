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
package com.koma.music.audiopreview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/21/17.
 */

public class AudioPreviewActivity extends AppCompatActivity implements AudioPreviewContract.View {
    private static final String TAG = AudioPreviewActivity.class.getSimpleName();

    @NonNull
    AudioPreviewContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public void setPresenter(@NonNull AudioPreviewContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
