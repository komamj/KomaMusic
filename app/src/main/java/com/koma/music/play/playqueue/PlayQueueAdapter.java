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
package com.koma.music.play.playqueue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Song;

import java.util.List;

/**
 * Created by koma on 4/27/17.
 */

public class PlayQueueAdapter extends RecyclerView.Adapter<PlayQueueAdapter.PlayQueueViewHolder> {
    private Context mContext;

    private List<Song> mData;

    public PlayQueueAdapter(Context context, List<Song> data) {
        mContext = context;

        setList(data);
    }

    public void replaceData(List<Song> songs) {
        setList(songs);
        notifyDataSetChanged();
    }

    private void setList(List<Song> songs) {
        mData = songs;
    }

    @Override
    public PlayQueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_play_queue_layout, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(PlayQueueViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class PlayQueueViewHolder extends BaseViewHolder {
        public PlayQueueViewHolder(View view) {
            super(view);
        }
    }
}
