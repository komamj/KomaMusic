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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.koma.music.R;
import com.koma.music.album.AlbumsFragment;
import com.koma.music.album.AlbumsPresenter;
import com.koma.music.artist.ArtistsFragment;
import com.koma.music.artist.ArtistsPresenter;
import com.koma.music.base.BaseActivity;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.play.MusicPlayerActivity;
import com.koma.music.playlist.PlaylistsFragment;
import com.koma.music.playlist.PlaylistsPresenter;
import com.koma.music.song.SongsFragment;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PAGE_LIMIT = 3;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.audio_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindArray(R.array.tab_title)
    String[] mTitles;

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mFragments = new ArrayList<>();

        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        mFragments.add(playlistsFragment);
        PlaylistsPresenter.newInstance(playlistsFragment, MusicRepository.getInstance());

        SongsFragment songsFragment = new SongsFragment();
        mFragments.add(songsFragment);
        SongsPresenter.newInstance(songsFragment, MusicRepository.getInstance());

        ArtistsFragment artistsFragment = new ArtistsFragment();
        mFragments.add(artistsFragment);
        ArtistsPresenter.newInstance(artistsFragment, MusicRepository.getInstance());

        AlbumsFragment albumsFragment = new AlbumsFragment();
        mFragments.add(albumsFragment);
        AlbumsPresenter.newInstance(albumsFragment, MusicRepository.getInstance());

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mainPagerAdapter);
        mainPagerAdapter.notifyDataSetChanged();

        mViewPager.setOffscreenPageLimit(PAGE_LIMIT);

        mTabLayout.setupWithViewPager(mViewPager);
        if (Utils.isRTL()) {
            mViewPager.setCurrentItem(MainPagerAdapter.SONG_TAB_INDEX_RTL);
        } else {
            mViewPager.setCurrentItem(MainPagerAdapter.SONG_TAB_INDEX);
        }

        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);

                /*startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, Pair.create((View) mAlbum, "share_album")).toBundle());*/
                startActivity(intent);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
