package com.koma.music.play.quickcontrol;

import android.graphics.drawable.Drawable;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;

/**
 * Created by koma on 5/3/17.
 */

public interface QuickControlContract {
    interface View extends BaseView<Presenter> {
        void updateBlurArtWork();

        void setBlurArtWork(Drawable blurArtWork);
    }

    interface Presenter extends BasePresenter {
        void doPlayOrPause();

        void doPrev();

        void doNext();

        void doBlurArtWork();
    }
}
