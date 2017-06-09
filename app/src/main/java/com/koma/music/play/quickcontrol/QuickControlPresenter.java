package com.koma.music.play.quickcontrol;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.koma.music.util.ImageLoader;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by koma on 5/3/17.
 */

public class QuickControlPresenter implements QuickControlContract.Presenter {
    private static final String TAG = QuickControlPresenter.class.getSimpleName();
    @NonNull
    private QuickControlContract.View mView;

    private CompositeDisposable mDisposables;

    public QuickControlPresenter(@NonNull QuickControlContract.View view) {
        mView = view;
        mView.setPresenter(this);

        mDisposables = new CompositeDisposable();
    }

    @Override
    public void doPlayOrPause() {
        MusicUtils.playOrPause();
    }

    @Override
    public void doPrev() {
        MusicUtils.previous(mView.getContext(), false);
    }

    @Override
    public void doNext() {
        MusicUtils.asyncNext(mView.getContext());
    }

    @Override
    public void doBlurArtWork() {
        LogUtils.i(TAG, "doArtWork");

        Glide.with(mView.getContext()).asBitmap()
                .load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
                        Disposable disposable = Flowable.create(new FlowableOnSubscribe<Drawable>() {
                            @Override
                            public void subscribe(@io.reactivex.annotations.NonNull FlowableEmitter<Drawable> e) throws Exception {
                                e.onNext(ImageLoader.createBlurredImageFromBitmap(resource, mView.getContext(), 6));
                                e.onComplete();
                            }
                        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSubscriber<Drawable>() {
                                    @Override
                                    public void onNext(Drawable drawable) {
                                        if (mView != null) {
                                            mView.setBlurArtWork(drawable);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable t) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                        mDisposables.add(disposable);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        if (mView != null) {
                            mView.setBlurArtWork(null);
                        }
                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }
}
