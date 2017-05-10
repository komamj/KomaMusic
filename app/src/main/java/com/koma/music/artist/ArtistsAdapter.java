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
package com.koma.music.artist;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Artist;
import com.koma.music.util.Constants;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 4/19/17.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> {
    private List<Artist> mData;

    private Context mContext;

    public ArtistsAdapter(Context context, List<Artist> data) {
        mContext = context;

        mData = data;
    }

    public void replaceData(List<Artist> artists) {
        setList(artists);
        notifyDataSetChanged();
    }

    private void setList(List<Artist> artists) {
        mData = artists;
    }

    @Override
    public ArtistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        return new ArtistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistsViewHolder holder, int position) {
        holder.itemView.setTag(position);

        holder.mMore.setTag(position);

        holder.mAlbum.setTransitionName(holder.mTransitionName + String.valueOf(position));

        Glide.with(mContext).load(mData.get(position).mArtistId).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.ic_album)
                .placeholder(R.drawable.ic_album)
                .into(holder.mAlbum);

        holder.mTitle.setText(mData.get(position).mArtistName);
        String albumNumber = Utils.makeLabel(mContext,
                R.plurals.num_albums, mData.get(position).mAlbumNumber);
        String songNumber = Utils.makeLabel(mContext,
                R.plurals.num_songs, mData.get(position).mSongNumber);
        holder.mInfo.setText(MusicUtils.makeCombinedString(mContext, albumNumber, songNumber));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ArtistsViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindString(R.string.transition_album)
        String mTransitionName;
        @BindView(R.id.iv_album)
        ImageView mAlbum;
        @BindView(R.id.tv_item_title)
        TextView mTitle;
        @BindView(R.id.tv_item_info)
        TextView mInfo;
        @BindView(R.id.iv_more)
        ImageView mMore;
        @BindString(R.string.transition_album)
        String mAlbumTransitionName;

        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
        }

        public ArtistsViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();

            long artistId = mData.get(position).mArtistId;

            String artistName = mData.get(position).mArtistName;

            Intent intent = new Intent();
            intent.putExtra(Constants.ARTIST_ID, artistId);
            intent.putExtra(Constants.ARTIST_NAME, artistName);
            intent.putExtra(Constants.WHICH_DETAIL_PAGE, Constants.ARTIST_DETAIL);

            ComponentName componentName = new ComponentName(Constants.MUSIC_PACKAGE_NAME,
                    Constants.DETAIL_PACKAGE_NAME);

            intent.setComponent(componentName);

            mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    ((AppCompatActivity) mContext), new Pair<View, String>(mAlbum,
                            mAlbumTransitionName)).toBundle());
        }
    }
}
