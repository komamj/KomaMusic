package com.koma.music.artist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseViewHolder;
import com.koma.music.data.model.Artist;
import com.koma.music.util.Utils;

import java.util.List;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, null);
        return new ArtistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMore.setTag(position);
        Glide.with(mContext).load(Utils.getAlbumArtUri(mData.get(position).mArtistId))
                .placeholder(R.drawable.ic_album)
                .into(holder.mAlbum);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ArtistsViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_album)
        ImageView mAlbum;
        @BindView(R.id.tv_item_title)
        TextView mTitle;
        @BindView(R.id.tv_item_subtitle)
        TextView mSubTitle;
        @BindView(R.id.iv_more)
        ImageView mMore;

        @BindView(R.id.tv_item_subtitle_2)
        TextView mSubTitleInfo;

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
        }
    }
}
