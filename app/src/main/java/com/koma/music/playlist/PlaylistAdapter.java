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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Playlist;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 4/14/17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private static final String TAG = PlaylistAdapter.class.getSimpleName();

    private List<Playlist> mData;

    public PlaylistAdapter(List<Playlist> playlists) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, null);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMoreMenu.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class PlayListHeaderViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_recently_added)
        ImageView mRecentlyAdded;
        @BindView(R.id.iv_recently_played)
        ImageView mRecentlyPlayed;
        @BindView(R.id.iv_my_favorite)
        ImageView mMyFavorite;

        @OnClick(R.id.iv_recently_added)
        void launchRecentlyAdded() {

        }

        @OnClick(R.id.iv_recently_played)
        void launchRecentlyPlayed() {

        }

        @OnClick(R.id.iv_my_favorite)
        void launchMyFavorite() {

        }


        public PlayListHeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class PlaylistViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_more)
        ImageView mMoreMenu;
        @BindView(R.id.iv_album)
        ImageView mAlbum;
        @BindView(R.id.tv_title)
        TextView mTitle;

        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
        }

        public PlaylistViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
        }
    }
}
