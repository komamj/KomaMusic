package com.koma.music.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by koma on 3/28/17.
 */

public class ImageLoader {

    public static Bitmap getArtworkByAlbumId(final Context context, long albumId) {
        final Bitmap[] artWork = new Bitmap[1];
        Glide.with(context).load(Utils.getAlbumArtUri(albumId))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        artWork[0] = bitmap;
                    }
                });
        return artWork[0];
    }
}
