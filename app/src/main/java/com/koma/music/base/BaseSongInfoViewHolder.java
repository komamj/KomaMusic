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
package com.koma.music.base;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koma.music.R;

import butterknife.BindView;

/**
 * Created by koma on 4/22/17.
 */

public abstract class BaseSongInfoViewHolder extends BaseViewHolder implements View.OnClickListener {
    protected static final int DOUBLE_CLICK_TIME = 1000;
    protected static final int MESSAGE_ITEM_CLICK = 0x00;
    @BindView(R.id.iv_album)
    public ImageView mAlbum;
    @BindView(R.id.iv_more)
    public ImageView mMoreMenu;
    @BindView(R.id.tv_title)
    public TextView mTitle;
    @BindView(R.id.tv_info)
    public TextView mInfo;

    protected Handler mHandler;

    public BaseSongInfoViewHolder(View view) {
        super(view);

        this.itemView.setOnClickListener(this);
    }
}