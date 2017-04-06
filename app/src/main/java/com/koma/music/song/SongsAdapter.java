package com.koma.music.song;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koma.music.R;
import com.koma.music.data.model.Song;
import com.koma.music.util.LogUtils;

import java.util.List;

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
        holder.mSongTitle.setText(mData.get(position).mSongName);
        holder.mSongInfo.setText(mData.get(position).mArtistName);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class SongsViewHolder extends RecyclerView.ViewHolder {
        private TextView mSongTitle, mSongInfo;

        SongsViewHolder(View view) {
            super(view);
            mSongTitle = (TextView) view.findViewById(R.id.tv_title);
            mSongInfo = (TextView) view.findViewById(R.id.tv_info);
        }
    }
}
