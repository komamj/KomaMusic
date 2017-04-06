package com.koma.music.play;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;

/**
 * Created by koma on 4/5/17.
 */

public interface MusicPlayerContract {
    interface View extends BaseView<Presenter> {
        void updatePlayOrPauseView();

        void updateFavoriteView();

        void updateRepeatView();

        void updateTitle();
    }

    interface Presenter extends BasePresenter {
        void doPlayOrPause();

        void doPrev();

        void doNext();

        void doFavorite();
    }
}
