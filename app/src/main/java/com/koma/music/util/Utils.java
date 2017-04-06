package com.koma.music.util;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

/**
 * Created by koma on 3/21/17.
 */

public class Utils {
    /**
     * 阅读习惯是否是从右到左
     *
     * @return true:从右到左 false:从左到右
     */
    public static boolean isRTL() {
        final Locale locale = Locale.getDefault();
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
    }

    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }
}
