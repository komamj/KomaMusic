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
package com.koma.music.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.koma.music.data.local.db.FavoriteSong;
import com.koma.music.util.Constants;

/**
 * Created by koma on 6/7/17.
 */

public class FavoriteIntentService extends IntentService {
    private static final String TAG = FavoriteIntentService.class.getSimpleName();

    public FavoriteIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        long songId = intent.getLongExtra(Constants.SONG_ID, -1);
        if (songId != -1) {
            if (FavoriteSong.getInstance(this).isFavorite(songId)) {
                FavoriteSong.getInstance(this).removeFavoriteSong(new long[]{songId});
            } else {
                FavoriteSong.getInstance(this).addFavoriteSong(new long[]{songId});
            }
        }
    }
}
