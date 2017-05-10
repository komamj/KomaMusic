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
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.koma.music.R;
import com.koma.music.base.BaseMusicStateActivity;
import com.koma.music.play.quickcontrol.QuickControlFragment;
import com.koma.music.play.quickcontrol.QuickControlPresenter;
import com.koma.music.util.LogUtils;
import com.koma.music.util.MusicUtils;
import com.koma.music.util.Utils;

import butterknife.BindString;
import butterknife.BindView;

public class MainActivity extends BaseMusicStateActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindString(R.string.unknown)
    String mDefaultName;
    ImageView mNavAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private Handler mHandler;

    private Runnable mNavigateMusicLibrary = new Runnable() {
        public void run() {
            mNavigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                fragment = new MainFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).commit();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    private void init() {
        mHandler = new Handler(Looper.getMainLooper());

        mNavigationView.setNavigationItemSelectedListener(this);

        View navHeadView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        mNavAlbum = (ImageView) navHeadView.findViewById(R.id.iv_nav_album);

        mNavigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();
        }

        QuickControlFragment quickControlFragment = (QuickControlFragment)
                getSupportFragmentManager().findFragmentById(R.id.quickcontrols_container);
        if (quickControlFragment == null) {
            quickControlFragment = new QuickControlFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.quickcontrols_container, quickControlFragment).commit();
        }
        new QuickControlPresenter(quickControlFragment);

        addBackstackListener();
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                LogUtils.i(TAG, "onBackStackChanged");
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void refreshData() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        Glide.with(this).load(Utils.getAlbumArtUri(MusicUtils.getCurrentAlbumId()))
                .error(R.drawable.ic_notification)
                .into(mNavAlbum);
    }

    @Override
    public void onPlayStateChanged() {

    }
}
