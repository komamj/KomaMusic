package com.koma.music.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by koma on 4/14/17.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
