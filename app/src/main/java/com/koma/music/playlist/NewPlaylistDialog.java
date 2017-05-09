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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.koma.music.R;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by koma on 4/18/17.
 */

public class NewPlaylistDialog extends DialogFragment {
    private static final String TAG = NewPlaylistDialog.class.getSimpleName();

    public static final String DIALOG_TAG = "PlaylistDialog";

    private static int COUNT = 20;

    private Context mContext;

    @BindString(R.string.playlist_title)
    String mPlaylistTitle;

    @BindView(R.id.et_play_list_name)
    EditText mPlaylistName;
    @BindView(R.id.tv_error_info)
    TextView mErrorInfo;
    @BindView(R.id.tv_count)
    TextView mCount;

    @OnTextChanged(value = R.id.et_play_list_name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void queryPlaylistName() {

    }

    public static NewPlaylistDialog newInstance(Song song) {
        long[] songs;
        if (song == null) {
            songs = new long[0];
        } else {
            songs = new long[1];
            songs[0] = song.mSongId;
        }
        return newInstance(songs);
    }

    public static NewPlaylistDialog newInstance(long[] songList) {
        NewPlaylistDialog dialog = new NewPlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songList);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);

        View view = LayoutInflater.from(mContext).inflate(R.layout.new_playlist_layout, null);

        ButterKnife.bind(this, view);

        builder.setTitle(mPlaylistTitle).setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        return builder.create();
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
