package com.koma.music.artist;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;
import com.koma.music.data.model.Artist;

import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public interface ArtistsConstract {
    interface View extends BaseView<Presenter> {
        boolean isActive();

        void showEmptyView();

        void hideLoadingView();

        void showArtists(List<Artist> artists);
    }

    interface Presenter extends BasePresenter {
        void loadArtists();

        void onLoadArtistsFinished(List<Artist> artists);
    }
}
