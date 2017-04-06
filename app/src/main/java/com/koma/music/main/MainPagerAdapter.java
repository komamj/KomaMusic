package com.koma.music.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.koma.music.R;
import com.koma.music.data.local.MusicRepository;
import com.koma.music.song.SongsFragment;
import com.koma.music.song.SongsPresenter;
import com.koma.music.util.Utils;

/**
 * Created by koma on 3/21/17.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_COUNT = 5;
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
     * The Constant GENRES_TAB_INDEX.
     */
    private static final int GENRES_TAB_INDEX = 4;

    /**
     * Default display tab for RTL language
     */
    public static final int SONG_TAB_INDEX_RTL = 3;

    /**
     * The m titles.
     */
    private String[] mTitles;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mTitles = new String[]{
                context.getString(R.string.tab_playlist),
                context.getString(R.string.tab_song),
                context.getString(R.string.tab_artist),
                context.getString(R.string.tab_album),
                context.getString(R.string.tab_genre)
        };
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (getRtlPosition(position)) {
            case PLAY_LIST_TAB_INDEX:
            case SONG_TAB_INDEX:
               /* SongsFragment songsFragment = SongsFragment.newInstance();
                SongsPresenter.newInstance(songsFragment);
                return songsFragment;*/
            case GENRES_TAB_INDEX:
            case ARTIST_TAB_INDEX:
            case ALBUM_TAB_INDEX:
            default:
                SongsFragment songsFragment = SongsFragment.newInstance();
                SongsPresenter.newInstance(songsFragment, MusicRepository.getInstance());
                return songsFragment;
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
