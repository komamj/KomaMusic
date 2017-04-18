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
