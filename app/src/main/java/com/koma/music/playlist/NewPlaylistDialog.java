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
package com.koma.music.playlist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.util.LogUtils;

import butterknife.ButterKnife;

/**
 * Created by koma on 4/18/17.
 */

public class NewPlaylistDialog extends DialogFragment {
    private static final String TAG = NewPlaylistDialog.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_playlist_layout, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        LogUtils.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i(TAG, "onPause");
    }
}
