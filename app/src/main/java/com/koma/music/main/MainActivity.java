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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.album.AlbumsFragment;
import com.koma.music.artist.ArtistsFragment;
import com.koma.music.base.BaseControlActivity;
import com.koma.music.play.quickcontrol.QuickControlFragment;
import com.koma.music.playlist.PlaylistsFragment;
import com.koma.music.song.SongsFragment;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;

public class MainActivity extends BaseControlActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PAGE_LIMIT = 1;

    @BindArray(R.array.tab_title)
    String[] mTitles;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindString(R.string.unknown)
    String mDefaultName;
    ImageView mNavAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private MainPagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }


    private void initViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(mToolbar);
        View navHeadView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        mNavAlbum = (ImageView) navHeadView.findViewById(R.id.iv_nav_album);

        mNavigationView.getMenu().findItem(R.id.nav_library).setChecked(true);

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mTitles);

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
    public void onStart() {
        super.onStart();
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
            //super.onBackPressed();
            backToHome();
        }
    }

    private void backToHome() {
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launcher.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcher);
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
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_library) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_exit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();

        Glide.with(this).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .into(mNavAlbum);
    }

}
