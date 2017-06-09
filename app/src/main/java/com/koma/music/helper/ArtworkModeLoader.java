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

import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

/**
 * Created by koma on 5/11/17.
 */

public class ArtworkModeLoader implements ModelLoader<String, InputStream> {
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(String category, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(category), new ArtworkDataFetcher(category));
    }

    @Override
    public boolean handles(String category) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ArtworkModeLoader();
        }

        @Override
        public void teardown() {
        }
    }
}
