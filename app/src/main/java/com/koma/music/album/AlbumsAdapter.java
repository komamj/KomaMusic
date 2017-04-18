package com.koma.music.album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Album;

import java.util.List;

/**
 * Created by koma on 4/14/17.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {

    private List<Album> mData;

    public AlbumsAdapter(List<Album> albumList) {
        setList(albumList);
    }

    public void replaceData(List<Album> playlists) {
        setList(playlists);
        notifyDataSetChanged();
    }

    private void setList(List<Album> albums) {
        mData = albums;
    }

    @Override
    public AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        return null;
    }

    @Override
    public void onBindViewHolder(AlbumsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class AlbumsViewHolder extends BaseViewHolder {
        public AlbumsViewHolder(View view) {
            super(view);
        }
    }
}
