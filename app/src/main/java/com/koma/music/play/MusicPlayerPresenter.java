package com.koma.music.play;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.util.ImageLoader;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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

    @Override
    public void doBlurArtWork() {
        LogUtils.i(TAG, "doArtWork");
        Glide.with(mContext).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId())).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        Observable.create(new Observable.OnSubscribe<Drawable>() {
                            @Override
                            public void call(Subscriber<? super Drawable> subscriber) {
                                subscriber.onNext(ImageLoader.createBlurredImageFromBitmap(bitmap, mContext, 6));
                                subscriber.onCompleted();
                            }
                        }).observeOn(Schedulers.computation())
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
                    }
                });
    }
}
