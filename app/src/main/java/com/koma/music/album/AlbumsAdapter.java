package com.koma.music.album;

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
import com.koma.music.data.model.Album;
import com.koma.music.util.Utils;

import java.util.List;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, null);
        return new AlbumsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mMore.setTag(position);
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

    static class AlbumsViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_album)
        ImageView mAlbum;
        @BindView(R.id.tv_item_title)
        TextView mTitle;
        @BindView(R.id.tv_item_info)
        TextView mInfo;
        @BindView(R.id.iv_more)
        ImageView mMore;

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
            int position = (int) view.getTag();
        }
    }
}
