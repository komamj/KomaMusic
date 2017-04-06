package com.koma.music.artist;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;

/**
 * Created by koma on 3/21/17.
 */

public interface ArtistsConstract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
