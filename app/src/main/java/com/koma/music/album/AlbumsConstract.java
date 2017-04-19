package com.koma.music.album;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;
import com.koma.music.data.model.Album;

import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public interface AlbumsConstract {
    interface View extends BaseView<Presenter> {
        boolean isActive();

        void showEmptyView();

        void hideLoadingView();

        void showAlbums(List<Album> albums);
    }

    interface Presenter extends BasePresenter {
        void loadAlbums();

        void onLoadSongsFinished(List<Album> albums);
    }
}
