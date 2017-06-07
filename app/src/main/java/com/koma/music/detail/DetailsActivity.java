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
package com.koma.music.detail;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.koma.music.R;
import com.koma.music.base.BaseControlActivity;
import com.koma.music.detail.albumdetail.AlbumDetailsFragment;
import com.koma.music.detail.artistdetail.ArtistDetailFragment;
import com.koma.music.play.quickcontrol.QuickControlFragment;
import com.koma.music.play.quickcontrol.QuickControlPresenter;
import com.koma.music.playlist.myfavorite.MyFavoriteFragment;
import com.koma.music.playlist.recentlyadd.RecentlyAddedFragment;
import com.koma.music.playlist.recentlyplay.RecentlyPlayFragment;
import com.koma.music.util.Constants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by koma on 5/9/17.
 */

public class DetailsActivity extends BaseControlActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_album)
    ImageView mAlbum;
    @BindView(R.id.fab_play)
    FloatingActionButton mFabPlay;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;


    @OnClick(R.id.fab_play)
    void doPlayAlbum() {

    }

    private int mWhichPage = Constants.ALBUM_DETAIL;

    private long mTargetId;

    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        super.onCreate(savedInstanceState);
    }

    public void init() {
        if (getIntent() != null) {
            mWhichPage = getIntent().getIntExtra(Constants.WHICH_DETAIL_PAGE, Constants.ALBUM_DETAIL);
        }

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(DetailsActivity.this, R.anim.toolbar_slide_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mToolbar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mToolbar.startAnimation(animation);
            }
        });

        QuickControlFragment quickControlFragment = (QuickControlFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_playback_controls);

        new QuickControlPresenter(quickControlFragment);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);

        if (fragment != null) {
            return;
        }

        Bundle bundle;

        switch (mWhichPage) {

            case Constants.RECENTLY_ADDED:
                mTitle = getResources().getString(R.string.recently_add);
                collapsingToolbarLayout.setTitle(mTitle);

                fragment = new RecentlyAddedFragment();
                break;
            case Constants.RECENTLY_PLAYED:
                mTitle = getResources().getString(R.string.recently_play);
                collapsingToolbarLayout.setTitle(mTitle);

                fragment = new RecentlyPlayFragment();
                break;
            case Constants.MY_FAVORITE:
                mTitle = getResources().getString(R.string.my_favorite);
                collapsingToolbarLayout.setTitle(mTitle);

                fragment = new MyFavoriteFragment();
                break;
            case Constants.ALBUM_DETAIL:
                mTargetId = getIntent().getLongExtra(Constants.ALBUM_ID, -1);
                mTitle = getIntent().getStringExtra(Constants.ALBUM_NAME);
                collapsingToolbarLayout.setTitle(mTitle);
                fragment = new AlbumDetailsFragment();
                bundle = new Bundle();
                bundle.putLong(Constants.ALBUM_ID, mTargetId);
                fragment.setArguments(bundle);
                break;
            case Constants.ARTIST_DETAIL:
                mTargetId = getIntent().getLongExtra(Constants.ARTIST_ID, -1);
                mTitle = getIntent().getStringExtra(Constants.ARTIST_NAME);
                collapsingToolbarLayout.setTitle(mTitle);
                fragment = new ArtistDetailFragment();
                bundle = new Bundle();
                bundle.putLong(Constants.ARTIST_ID, mTargetId);
                fragment.setArguments(bundle);
                break;
            default:
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void showAlbum(Drawable albumArt) {
        mAlbum.setImageDrawable(albumArt);
    }

    public void showAlbum(Bitmap bitmap) {
        mAlbum.setImageBitmap(bitmap);
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
        } else if (id == android.R.id.home) {
            this.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mFabPlay.setVisibility(View.INVISIBLE);

        super.onBackPressed();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_details;
    }
}
