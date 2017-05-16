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
package com.koma.music.play;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.koma.music.R;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 4/8/17.
 */

public class MusicPlayerPresenter implements MusicPlayerContract.Presenter {
    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();
    private Context mContext;
    @NonNull
    private MusicPlayerContract.View mView;

    private CompositeSubscription mSubscriptions;

    private MusicRepository mRepository;

    public MusicPlayerPresenter(Context context, @NonNull MusicPlayerContract.View view,
                                MusicRepository repository) {
        mContext = context;

        mSubscriptions = new CompositeSubscription();

        mRepository = repository;

        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void doPlayOrPause() {
        LogUtils.i(TAG, "doPlayOrPause");
    }

    @Override
    public void doPrev() {
        LogUtils.i(TAG, "doPrev");
    }

    @Override
    public void doNext() {
        LogUtils.i(TAG, "doNext");
    }

    @Override
    public void doFavorite() {
        LogUtils.i(TAG, "doFavorite");
    }

    @Override
    public void onFavoriteFinished() {

    }

    /*@Override
    public void doBlurArtWork() {
        LogUtils.i(TAG, "doArtWork");
        Glide.with(mContext).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId())).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        if (mView != null) {
                            mView.setBlurArtWork(null);
                        }
                    }

                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        Subscription subscription = Observable.create(new Observable.OnSubscribe<Drawable>() {
                            @Override
                            public void call(Subscriber<? super Drawable> subscriber) {
                                subscriber.onNext(ImageLoader.createBlurredImageFromBitmap(bitmap, mContext, 6));
                                subscriber.onCompleted();
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Drawable>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable throwable) {

                                    }

                                    @Override
                                    public void onNext(Drawable drawble) {
                                        if (mView != null) {
                                            mView.setBlurArtWork(drawble);
                                        }
                                    }
                                });

                        mSubscriptions.add(subscription);
                    }
                });
    }
*/
    @Override
    public void updateArtWork() {
        Glide.with(mView.getContext()).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .asBitmap()
                .placeholder(R.drawable.ic_album)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mView.updateArtwork(resource);
                    }
                });
    }
}
