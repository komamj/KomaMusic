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

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.koma.music.R;
import com.koma.music.base.BaseSongInfoViewHolder;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Playlist;
import com.koma.music.util.Constants;
import com.koma.music.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 4/14/17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = PlaylistAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0x00;
    private static final int TYPE_NORMAL = TYPE_HEADER + 1;

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
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_header, parent, false);
            return new PlaylistHeaderVH(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_song_info_base, parent, false);
            return new PlaylistViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            PlaylistHeaderVH viewHolder = (PlaylistHeaderVH) holder;

            Glide.with(mContext).load(Constants.CATEGORY_RECENTLY_ADDED)
                    .crossFade()
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_album)
                    .into(viewHolder.mRecentlyAdded);

            Glide.with(mContext).load(Constants.CATEGORY_RECENTLY_PLAYED)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .placeholder(R.drawable.ic_album)
                    .into(viewHolder.mRecentlyPlayed);

            Glide.with(mContext).load(Constants.CATEGORY_MY_FAVORITE)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .placeholder(R.drawable.ic_album)
                    .into(viewHolder.mFavorite);
        } else {
            holder.itemView.setTag(position - 1);
            PlaylistViewHolder viewHolder = (PlaylistViewHolder) holder;
            viewHolder.mMoreMenu.setTag(position - 1);
            viewHolder.mTitle.setText(mData.get(position - 1).mPlaylistName);
            viewHolder.mInfo.setText(Utils.makeLabel(mContext, R.plurals.num_songs,
                    mData.get(position - 1).mSongCount));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 1 : mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public class PlaylistHeaderVH extends BaseViewHolder {
        @BindView(R.id.iv_recently_played)
        ImageView mRecentlyPlayed;
        @BindView(R.id.iv_recently_added)
        ImageView mRecentlyAdded;
        @BindView(R.id.iv_my_favorite)
        ImageView mFavorite;

        @OnClick(R.id.tv_new_playlist)
        void newPlaylist() {
            NewPlaylistDialog newPlaylistDialog = new NewPlaylistDialog();
            newPlaylistDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                    NewPlaylistDialog.DIALOG_TAG);
        }

        @OnClick(R.id.iv_recently_added)
        void launchRecentlyAddedDetail() {
            Intent intent = new Intent();
            intent.putExtra(Constants.WHICH_DETAIL_PAGE, Constants.RECENTLY_ADDED);

            ComponentName componentName = new ComponentName(Constants.MUSIC_PACKAGE_NAME,
                    Constants.DETAIL_PACKAGE_NAME);

            intent.setComponent(componentName);

            mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    ((AppCompatActivity) mContext), new Pair<View, String>(mRecentlyAdded,
                            "transition_album")).toBundle());
        }

        @OnClick(R.id.iv_recently_played)
        void launchRecentlyPlayedDetail() {
            Intent intent = new Intent();
            intent.putExtra(Constants.WHICH_DETAIL_PAGE, Constants.RECENTLY_PLAYED);

            ComponentName componentName = new ComponentName(Constants.MUSIC_PACKAGE_NAME,
                    Constants.DETAIL_PACKAGE_NAME);

            intent.setComponent(componentName);

            mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    ((AppCompatActivity) mContext), new Pair<View, String>(mRecentlyPlayed,
                            "transition_album")).toBundle());
        }

        @OnClick(R.id.iv_my_favorite)
        void launchMyFavoriteDetail() {
            Intent intent = new Intent();
            intent.putExtra(Constants.WHICH_DETAIL_PAGE, Constants.MY_FAVORITE);

            ComponentName componentName = new ComponentName(Constants.MUSIC_PACKAGE_NAME,
                    Constants.DETAIL_PACKAGE_NAME);

            intent.setComponent(componentName);

            mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    ((AppCompatActivity) mContext), new Pair<View, String>(mFavorite,
                            "transition_album")).toBundle());
        }

        public PlaylistHeaderVH(View view) {
            super(view);
        }
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
