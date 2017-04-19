package com.koma.music.artist;

import android.support.annotation.NonNull;

import com.koma.music.data.local.MusicRepository;
import com.koma.music.data.model.Artist;
import com.koma.music.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by koma on 3/21/17.
 */

public class ArtistsPresenter implements ArtistsConstract.Presenter {
    private static final String TAG = ArtistsPresenter.class.getSimpleName();
    @NonNull
    private ArtistsConstract.View mView;

    private CompositeSubscription mSubscriptions;

    private MusicRepository mRepository;

    private ArtistsPresenter(ArtistsConstract.View view, MusicRepository repository) {
        mView = view;
        mView.setPresenter(this);

        mSubscriptions = new CompositeSubscription();

        mRepository = repository;
    }

    public static ArtistsPresenter newInstance(ArtistsConstract.View view, MusicRepository repository) {
        return new ArtistsPresenter(view, repository);
    }


    @Override
    public void subscribe() {
        LogUtils.i(TAG, "subscribe");

        loadArtists();
    }

    @Override
    public void unSubscribe() {
        LogUtils.i(TAG, "unSubscribe");

        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void loadArtists() {
        LogUtils.i(TAG, "loadArtists");

        mSubscriptions.clear();

        Subscription subscription = mRepository.getAllArtists().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Artist>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e(TAG, "loadArtists onError : " + throwable.toString());
                    }

                    @Override
                    public void onNext(List<Artist> artists) {
                        onLoadArtistsFinished(artists);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void onLoadArtistsFinished(List<Artist> artists) {
        LogUtils.i(TAG, "onLoadArtistsFinished");

        if (mView.isActive()) {
            mView.hideLoadingView();

            if (artists.size() == 0) {
                mView.showEmptyView();
            } else {
                mView.showArtists(artists);
            }
        }
    }

    public static List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();
        return artistList;
    }
}
