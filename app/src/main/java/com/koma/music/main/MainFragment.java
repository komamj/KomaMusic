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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.koma.music.R;
import com.koma.music.album.AlbumsFragment;
import com.koma.music.artist.ArtistsFragment;
import com.koma.music.base.BaseFragment;
import com.koma.music.playlist.PlaylistsFragment;
import com.koma.music.song.SongsFragment;
import com.koma.music.util.Utils;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * Created by koma on 5/5/17.
 */

public class MainFragment extends BaseFragment {
    private static final int PAGE_LIMIT = 3;

    @BindArray(R.array.tab_title)
    String[] mTitles;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private MainPagerAdapter mPagerAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void init() {
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), ((MainActivity) getActivity()).getDrawerLayout(),
                mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        ((MainActivity) getActivity()).getDrawerLayout().addDrawerListener(toggle);
        toggle.syncState();

        mPagerAdapter = new MainPagerAdapter(getChildFragmentManager(), mTitles);

        PlaylistsFragment playlistsFragment = new PlaylistsFragment();

        mPagerAdapter.addFragment(playlistsFragment);

        SongsFragment songsFragment = new SongsFragment();

        mPagerAdapter.addFragment(songsFragment);

        ArtistsFragment artistsFragment = new ArtistsFragment();

        mPagerAdapter.addFragment(artistsFragment);

        AlbumsFragment albumsFragment = new AlbumsFragment();

        mPagerAdapter.addFragment(albumsFragment);

        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOffscreenPageLimit(PAGE_LIMIT);

        mTabLayout.setupWithViewPager(mViewPager);
        if (Utils.isRTL()) {
            mViewPager.setCurrentItem(MainPagerAdapter.SONG_TAB_INDEX_RTL);
        } else {
            mViewPager.setCurrentItem(MainPagerAdapter.SONG_TAB_INDEX);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_bar_main;
    }
}
