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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseSongInfoViewHolder;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by koma on 3/23/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = SongsAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = TYPE_HEADER + 1;

    private Context mContext;

    private List<Song> mData;

    public SongsAdapter(Context context, List<Song> songs) {
        mContext = context;

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
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_song_header, null);

            return new SongsHeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_song_info_base, null);

            return new SongsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_NORMAL) {
            ((SongsViewHolder) holder).itemView.setTag(position - 1);

            ((SongsViewHolder) holder).mMoreMenu.setTag(position - 1);

            Glide.with(mContext).load(Utils.getAlbumArtUri(mData.get(position - 1).mAlbumId))
                    .placeholder(R.drawable.ic_album)
                    .into(((SongsViewHolder) holder).mAlbum);

            ((SongsViewHolder) holder).mTitle.setText(mData.get(position - 1).mSongName);

            ((SongsViewHolder) holder).mInfo.setText(mData.get(position - 1).mArtistName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size() + 1;
    }

    /**
     * @return Gets the list of song ids from the adapter
     */
    public long[] getSongIds() {
        long[] ret = new long[mData.size()];

        for (int i = 0; i < mData.size(); i++) {
            ret[i] = mData.get(i).mSongId;
        }

        return ret;
    }

    static class SongsHeaderViewHolder extends BaseViewHolder implements View.OnClickListener {
        public SongsHeaderViewHolder(View view) {
            super(view);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class SongsViewHolder extends BaseSongInfoViewHolder {
        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
            LogUtils.i(TAG, "position : " + position);
        }

        SongsViewHolder(View view) {
            super(view);
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
    }
}
