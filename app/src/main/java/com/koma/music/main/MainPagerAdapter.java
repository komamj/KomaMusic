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
package com.koma.music.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.koma.music.base.BaseFragment;
import com.koma.music.util.LogUtils;
import com.koma.music.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koma on 3/21/17.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_COUNT = 4;
    /**
     * The Constant PLAY_LIST_TAB_INDEX.
     */
    private static final int PLAY_LIST_TAB_INDEX = 0;

    /**
     * The Constant ALL_MUSIC_TAB_INDEX.
     */
    public static final int SONG_TAB_INDEX = 1;

    /**
     * The Constant ARTIST_TAB_INDEX.
     */
    private static final int ARTIST_TAB_INDEX = 2;
    /**
     * The Constant ALBUM_TAB_INDEX.
     */
    private static final int ALBUM_TAB_INDEX = 3;
    /**
     * Default display tab for RTL language
     */
    public static final int SONG_TAB_INDEX_RTL = 3;

    /**
     * The m titles.
     */
    private String[] mTitles;

    private List<Fragment> mFragments;

    public MainPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);

        mTitles = titles;

        mFragments = new ArrayList<>();
    }

    public void addFragment(BaseFragment fragment) {
        mFragments.add(fragment);
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (getRtlPosition(position)) {
            case PLAY_LIST_TAB_INDEX:
                return mFragments.get(0);
            case SONG_TAB_INDEX:
                return mFragments.get(1);
            case ARTIST_TAB_INDEX:
                return mFragments.get(2);
            case ALBUM_TAB_INDEX:
                return mFragments.get(3);
            default:
                return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return getRtlPosition(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[getRtlPosition(position)];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        LogUtils.i("MainPagerAdapter", "destroyItem");
    }

    /**
     * get position for rtl and ltr
     *
     * @param position
     * @return
     */
    private int getRtlPosition(int position) {
        if (Utils.isRTL()) {
            return getCount() - 1 - position;
        }
        return position;
    }

}
