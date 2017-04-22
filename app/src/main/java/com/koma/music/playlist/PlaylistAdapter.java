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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.base.BaseSongInfoViewHolder;
import com.koma.music.data.model.Playlist;
import com.koma.music.util.Utils;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by koma on 4/14/17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private static final String TAG = PlaylistAdapter.class.getSimpleName();

    private List<Playlist> mData;

    private Context mContext;

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        mContext = context;

        setList(playlists);
    }

    public void replaceData(List<Playlist> playlists) {
        setList(playlists);
        notifyDataSetChanged();
    }

    private void setList(List<Playlist> songs) {
        mData = songs;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_info_base, null);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMoreMenu.setTag(position);
        holder.mTitle.setText(mData.get(position).mPlaylistName);
        holder.mInfo.setText(Utils.makeLabel(mContext, R.plurals.num_songs, mData.get(position).mSongCount));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class PlaylistViewHolder extends BaseSongInfoViewHolder {

        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
        }

        public PlaylistViewHolder(View view) {
            super(view);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
        }
    }
}
