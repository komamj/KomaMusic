package com.koma.music.song;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;
import com.koma.music.data.model.Song;

import java.util.List;

/**
 * Created by koma on 3/20/17.
 */

public interface SongsContract {
    interface View extends BaseView<Presenter> {
        boolean isActive();

        void showEmptyView();

        void showLoadingView();

        void showSongs(List<Song> songs);
    }

    interface Presenter extends BasePresenter {
        void loadSongs();

        void onLoadSongsFinished(List<Song> songs);
    }
}
