package com.koma.music.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by koma on 4/13/17.
 */

public class FavoriteButton extends MusicButton {
    public FavoriteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View view) {
        updateFavoriteState();
    }

    /**
     * Sets the correct drawable for the favorite state.
     */
    public void updateFavoriteState() {
    }
}
