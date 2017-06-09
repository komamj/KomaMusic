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
import android.net.Uri;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.koma.music.MusicApplication;
import com.koma.music.album.AlbumsPresenter;
import com.koma.music.playlist.myfavorite.MyFavoritePresenter;
import com.koma.music.playlist.recentlyadd.RecentlyAddedPresenter;
import com.koma.music.playlist.recentlyplay.RecentlyPlayPresenter;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.Constants;
import com.koma.music.util.LogUtils;
import com.koma.music.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by koma on 5/11/17.
 */

public class ArtworkDataFetcher implements DataFetcher<InputStream> {
    private static final String TAG = ArtworkDataFetcher.class.getSimpleName();

    private InputStream mInputStream;

    private String mCategory;

    public ArtworkDataFetcher(String category) {
        mCategory = category;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {
            mInputStream = openThumbInputStream();
        } catch (FileNotFoundException e) {
            LogUtils.d(TAG, "Failed to find thumbnail file," + e.toString());

            callback.onLoadFailed(e);

            return;
        }

        callback.onDataReady(mInputStream);
    }

    private InputStream openThumbInputStream() throws FileNotFoundException {
        ContentResolver contentResolver = MusicApplication.getContext().getContentResolver();

        Uri thumbnailUri;
        switch (mCategory) {
            case Constants.CATEGORY_RECENTLY_ADDED:

                thumbnailUri = Utils.getAlbumArtUri(SongsPresenter.getSongsForCursor(
                        RecentlyAddedPresenter.makeLastAddedCursor(), true).get(0).mAlbumId);
                break;
            case Constants.CATEGORY_RECENTLY_PLAYED:

                thumbnailUri = Utils.getAlbumArtUri(SongsPresenter.getSongsForCursor(
                        RecentlyPlayPresenter.makeRecentPlayCursor(), true).get(0).mAlbumId);
                break;
            case Constants.CATEGORY_MY_FAVORITE:
                thumbnailUri = Utils.getAlbumArtUri(SongsPresenter.getSongsForCursor(
                        MyFavoritePresenter.getFavoriteSongCursor(), true).get(0).mAlbumId);
                break;
            default:
                thumbnailUri = Utils.getAlbumArtUri(AlbumsPresenter.getArtistAlbums(
                        Integer.parseInt(mCategory)).get(0).getAlbumId());
                break;
        }

        try {
            mInputStream = contentResolver.openInputStream(thumbnailUri);
        } catch (NullPointerException e) {
            throw (FileNotFoundException)
                    new FileNotFoundException("NPE opening uri: " + thumbnailUri).initCause(e);
        }

        return mInputStream;
    }

    @Override
    public void cleanup() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                // Ignored.
            }
        }
    }

    @Override
    public void cancel() {
        // Do nothing.
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

}
