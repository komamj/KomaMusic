package com.koma.music.main;

import com.koma.music.base.BasePresenter;
import com.koma.music.base.BaseView;

/**
 * Created by koma on 4/5/17.
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
