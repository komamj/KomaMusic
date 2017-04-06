package com.koma.music.playlist;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;

/**
 * Created by koma on 3/21/17.
 */

public interface PlaylistsConstract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
