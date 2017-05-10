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
package com.koma.music.album;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Album;
import com.koma.music.util.Constants;
import com.koma.music.util.Utils;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 4/14/17.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {
    private List<Album> mData;

    private Context mContext;

    public AlbumsAdapter(Context context, List<Album> albumList) {
        mContext = context;

        setList(albumList);
    }

    public void replaceData(List<Album> albumList) {
        setList(albumList);
        notifyDataSetChanged();
    }

    private void setList(List<Album> albums) {
        mData = albums;
    }

    @Override
    public AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        return new AlbumsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMore.setTag(position);
        holder.mAlbum.setTransitionName(holder.mAlbumTransitionName + String.valueOf(position));
        Glide.with(mContext).load(Utils.getAlbumArtUri(mData.get(position).mAlbumId))
                .placeholder(R.drawable.ic_album)
                .into(holder.mAlbum);
        holder.mTitle.setText(mData.get(position).mAlbumName);
        holder.mInfo.setText(mData.get(position).mArtistName);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class AlbumsViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindString(R.string.transition_album)
        String mAlbumTransitionName;
        @BindView(R.id.iv_album)
        ImageView mAlbum;
        @BindView(R.id.tv_item_title)
        TextView mTitle;
        @BindView(R.id.tv_item_info)
        TextView mInfo;
        @BindView(R.id.iv_more)
        ImageView mMore;
        @BindView(R.id.fab_play)
        FloatingActionButton mFabPlay;
        @BindString(R.string.transition_fab_play)
        String mFabTransitionName;

        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
        }

        public AlbumsViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();

            int position = (int) view.getTag();

            long albumId = mData.get(position).mAlbumId;

            String albumName = mData.get(position).mAlbumName;

            intent.putExtra(Constants.ALBUM_ID, albumId);
            intent.putExtra(Constants.ALBUM_NAME, albumName);
            intent.putExtra(Constants.WHICH_DETAIL_PAGE, Constants.ALBUM_DETAIL);

            ComponentName componentName = new ComponentName(Constants.MUSIC_PACKAGE_NAME,
                    Constants.DETAIL_PACKAGE_NAME);

            intent.setComponent(componentName);

            mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    ((AppCompatActivity) mContext), new Pair<View, String>(mAlbum,
                            mAlbumTransitionName),
                    new Pair<View, String>(mFabPlay, mFabTransitionName)).toBundle());
        }
    }
}
