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
package com.koma.music.song;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 3/23/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private static final String TAG = SongsAdapter.class.getSimpleName();
    private List<Song> mData;

    public SongsAdapter(List<Song> songs) {
        setList(songs);
    }

    public void replaceData(List<Song> songs) {
        setList(songs);
        notifyDataSetChanged();
    }

    private void setList(List<Song> songs) {
        mData = songs;
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);
        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMoreMenu.setTag(position);
        holder.mTrackName.setText(mData.get(position).mSongName);
        holder.mArtistName.setText(mData.get(position).mArtistName);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * @return Gets the list of song ids from the adapter
     */
    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];

        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = mData.get(i).mSongId;
        }

        return ret;
    }

    public class SongsViewHolder extends BaseViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        @BindView(R.id.iv_more)
        ImageView mMoreMenu;
        @BindView(R.id.tv_title)
        TextView mTrackName;
        @BindView(R.id.tv_info)
        TextView mArtistName;

        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
            LogUtils.i(TAG, "position : " + position);
        }

        SongsViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();

            LogUtils.i(TAG, "onClick position : " + position);

            final long[] list = getSongIds();

            if (list != null) {
                MusicUtils.playAll(list, position, -1, false);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
}
