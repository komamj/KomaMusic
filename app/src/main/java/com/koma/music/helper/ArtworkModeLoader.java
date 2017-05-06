/*
 * Copyright (C) 2017 Koma MJ
 *
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.koma.music.helper;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by koma on 5/5/17.
 */

public class ArtworkModeLoader implements StreamModelLoader<Long> {
    private static final String TAG = ArtworkModeLoader.class.getSimpleName();

    private Context mContext;

    public ArtworkModeLoader(Context context) {
        mContext = context;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(Long artistId, int width, int height) {
        // this method is working in main thread.

        return new ArtworkDataFetcher(mContext, artistId);
    }

    // ModelLoader工厂，在向Glide注册自定义ModelLoader时使用到
    public static class Factory implements ModelLoaderFactory<Long, InputStream> {

        @Override
        public ModelLoader<Long, InputStream> build(Context context,
                                                    GenericLoaderFactory genericLoaderFactory) {
            return new ArtworkModeLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}
