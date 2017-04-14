package com.koma.music.playlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Playlist;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by koma on 4/14/17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private static final String TAG = PlaylistAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_ONE = 0;
    private static final int VIEW_TYPE_TWO = VIEW_TYPE_ONE + 1;

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
        View view;
        if (viewType == VIEW_TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, null);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 3 : mData.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 3) {
            return VIEW_TYPE_ONE;
        } else {
            return VIEW_TYPE_TWO;
        }
    }

    public class PlaylistViewHolder extends BaseViewHolder {
        @OnClick(R.id.iv_more)
        void doMoreAction(View view) {
            int position = (int) view.getTag();
        }

        public PlaylistViewHolder(View view) {
            super(view);
        }
    }
}
