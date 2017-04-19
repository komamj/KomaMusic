package com.koma.music.artist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Artist;

import java.util.List;

/**
 * Created by koma on 4/19/17.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder> {
    private List<Artist> mData;

    public ArtistsAdapter(List<Artist> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, null);
        return new ArtistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    static class ArtistsViewHolder extends BaseViewHolder {
        public ArtistsViewHolder(View view) {
            super(view);
        }
    }
}
