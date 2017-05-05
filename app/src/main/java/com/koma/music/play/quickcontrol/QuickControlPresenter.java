package com.koma.music.play.quickcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.koma.music.util.ImageLoader;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 5/3/17.
 */

public class QuickControlPresenter implements QuickControlContract.Presenter {
    private static final String TAG = QuickControlPresenter.class.getSimpleName();
    @NonNull
    private QuickControlContract.View mView;

    private CompositeSubscription mSubscriptions;

    private Context mContext;

    public QuickControlPresenter(Context context, @NonNull QuickControlContract.View view) {
        mView = view;
        mView.setPresenter(this);

        mContext = context;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void doPlayOrPause() {
        MusicUtils.playOrPause();
    }

    @Override
    public void doPrev() {
        MusicUtils.previous(mContext, false);
    }

    @Override
    public void doNext() {
        MusicUtils.asyncNext(mContext);
    }

    @Override
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

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }
}
