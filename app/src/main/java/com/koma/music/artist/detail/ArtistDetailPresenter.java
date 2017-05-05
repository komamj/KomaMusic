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
package com.koma.music.artist.detail;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.koma.music.R;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;
import com.koma.music.util.Utils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 5/5/17.
 */

public class ArtistDetailPresenter implements ArtistDetailContract.Presenter {
    private static final String TAG = ArtistDetailPresenter.class.getSimpleName();
    @NonNull
    private ArtistDetailContract.View mView;
    @NonNull
    private MusicRepository mRepository;

    private CompositeSubscription mSubscriptions;

    public ArtistDetailPresenter(ArtistDetailContract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mRepository = repository;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        if (mView != null) {
            loadArtistSongs(mView.getArtistId());

            loadArtWork(mView.getArtistId());
        }

    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void loadArtistSongs(long artistId) {
        LogUtils.i(TAG, "loadArtistSongs atistId : " + artistId);
    }

    @Override
    public void loadArtWork(long artistId) {
        LogUtils.i(TAG, "loadArtWork");

        if (mView != null) {
            Glide.with(mView.getContext()).load(Utils.getAlbumArtUri(artistId))
                    .asBitmap()
                    .error(R.drawable.ic_album)
                    .priority(Priority.IMMEDIATE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);

                            if (mView != null) {
                                mView.showArtwork(errorDrawable);
                            }
                        }

                        @Override
                        public void onResourceReady(Bitmap glideDrawable, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (mView != null) {
                                mView.showArtwork(glideDrawable);
                            }
                        }
                    });
        }
    }
}
