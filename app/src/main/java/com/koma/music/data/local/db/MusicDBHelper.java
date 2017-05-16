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
package com.koma.music.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.koma.music.util.LogUtils;

/**
 * Created by koma on 3/21/17.
 */

public class MusicDBHelper extends SQLiteOpenHelper {
    private static final String TAG = MusicDBHelper.class.getSimpleName();

    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    private static final int VERSION = 1;

    /* Name of database file */
    public static final String DATABASE_NAME = "komamusic.db";

    private static MusicDBHelper sInstance = null;

    private Context mContext = null;

    /**
     * @param context The {@link android.content.Context} to use
     * @return A new instance of this class.
     */
    public static final synchronized MusicDBHelper getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new MusicDBHelper(context);
        }
        return sInstance;
    }

    public MusicDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*PropertiesStore.getInstance(mContext).onCreate(db);
        PlaylistArtworkStore.getInstance(mContext).onCreate(db);
        RecentPlay.getInstance(mContext).onCreate(db);
        SongPlayCount.getInstance(mContext).onCreate(db);
        SearchHistory.getInstance(mContext).onCreate(db);*/
        MusicPlaybackState.getInstance(mContext).onCreate(db);
        // LocalizedStore.getInstance(mContext).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* PropertiesStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        PlaylistArtworkStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        RecentPlay.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        SongPlayCount.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        SearchHistory.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);*/
        MusicPlaybackState.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
        // LocalizedStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.i(TAG,
                "Downgrading from: " + oldVersion + " to " + newVersion + ". Dropping tables");
       /* PropertiesStore.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        PlaylistArtworkStore.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        RecentPlay.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        SongPlayCount.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        SearchHistory.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);*/
        MusicPlaybackState.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
        //LocalizedStore.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
    }
}
