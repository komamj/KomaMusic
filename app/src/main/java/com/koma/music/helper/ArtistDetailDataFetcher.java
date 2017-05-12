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

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.koma.music.album.AlbumsPresenter;
import com.koma.music.data.model.Album;
import com.koma.music.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by koma on 5/6/17.
 */

public class ArtistDetailDataFetcher implements DataFetcher<InputStream> {
    private static final String TAG = ArtistDetailDataFetcher.class.getSimpleName();

    private long mArtistId;

    private Context mContext;

    private InputStream mInputStream;

    public ArtistDetailDataFetcher(Context context, long artistId) {
        mArtistId = artistId;

        mContext = context;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        // this method is in work thread

        List<Album> albumList = AlbumsPresenter.getArtistAlbums(this.mArtistId);

        mInputStream = mContext.getContentResolver().openInputStream(Utils.getAlbumArtUri(albumList.get(0).getAlbumId()));

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
        return String.valueOf(this.mArtistId);
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
