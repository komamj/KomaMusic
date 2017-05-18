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

import android.content.ContentResolver;
import android.content.Context;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.koma.music.album.AlbumsPresenter;
import com.koma.music.playlist.myfavorite.MyFavoritePresenter;
import com.koma.music.playlist.recentlyplay.RecentlyPlayPresenter;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.Constants;
import com.koma.music.util.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by koma on 5/11/17.
 */

public class CategoryArtworkDataFetcher implements DataFetcher<InputStream> {
    private String mCategory;

    private Context mContext;

    private InputStream mInputStream;
    private long mAlbumId;

    public CategoryArtworkDataFetcher(Context context, String category) {
        mContext = context;

        mCategory = category;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        ContentResolver contentResolver = mContext.getContentResolver();

        switch (mCategory) {
            case Constants.CATEGORY_RECENTLY_ADDED:
                mAlbumId = SongsPresenter.getSongsForCursor(SongsPresenter.makeSongCursor(), true)
                        .get(0).mAlbumId;
                mInputStream = contentResolver.openInputStream(Utils.getAlbumArtUri(mAlbumId));
                break;
            case Constants.CATEGORY_RECENTLY_PLAYED:
                mAlbumId = SongsPresenter.getSongsForCursor(RecentlyPlayPresenter
                        .makeRecentPlayCursor(), true).get(0).mAlbumId;

                mInputStream = contentResolver.openInputStream(Utils.getAlbumArtUri(mAlbumId));
                break;
            case Constants.CATEGORY_MY_FAVORITE:
                mAlbumId = SongsPresenter.getSongsForCursor(MyFavoritePresenter
                        .getFavoriteSongCursor(), true).get(0).mAlbumId;
                mInputStream = contentResolver.openInputStream(Utils.getAlbumArtUri(
                        mAlbumId));
                break;
            default:
                mAlbumId = AlbumsPresenter.getArtistAlbums(Integer.parseInt(mCategory)).get(0)
                        .getAlbumId();
                mInputStream = contentResolver.openInputStream(Utils.getAlbumArtUri(mAlbumId));
                break;
        }
        return mInputStream;
    }

    @Override
    public void cleanup() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {

            } finally {
                mInputStream = null;
            }
        }
    }

    @Override
    public String getId() {
        return String.valueOf(mAlbumId) + mCategory;
    }

    @Override
    public void cancel() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {

            } finally {
                mInputStream = null;
            }
        }
    }
}
