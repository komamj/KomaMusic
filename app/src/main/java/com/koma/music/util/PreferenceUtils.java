package com.koma.music.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by koma on 3/27/17.
 */

public class PreferenceUtils {
    /* Default start page (Artist page) */
    public static final int DEFFAULT_PAGE = 2;

    /* Saves the last page the pager was on in {@link MusicBrowserPhoneFragment} */
    public static final String START_PAGE = "start_page";

    // Sort order for the artist list
    public static final String ARTIST_SORT_ORDER = "artist_sort_order";

    // Sort order for the artist song list
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";

    // Sort order for the artist album list
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";

    // Sort order for the album list
    public static final String ALBUM_SORT_ORDER = "album_sort_order";

    // Sort order for the album song list
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";

    // Sort order for the song list
    public static final String SONG_SORT_ORDER = "song_sort_order";

    // Key used to download images only on Wi-Fi
    public static final String ONLY_ON_WIFI = "only_on_wifi";

    // Key that gives permissions to download missing album covers
    public static final String DOWNLOAD_MISSING_ARTWORK = "download_missing_artwork";

    // Key that gives permissions to download missing artist images
    public static final String DOWNLOAD_MISSING_ARTIST_IMAGES = "download_missing_artist_images";

    // Key used to set the overall theme color
    public static final String DEFAULT_THEME_COLOR = "default_theme_color";

    // datetime cutoff for determining which songs go in last added playlist
    public static final String LAST_ADDED_CUTOFF = "last_added_cutoff";

    // show lyrics option
    public static final String SHOW_LYRICS = "show_lyrics";

    // show visualizer flag
    public static final String SHOW_VISUALIZER = "music_visualization";

    // shake to play flag
    public static final String SHAKE_TO_PLAY = "shake_to_play";

    // show/hide album art on lockscreen
    public static final String SHOW_ALBUM_ART_ON_LOCKSCREEN = "lockscreen_album_art";

    private static PreferenceUtils sInstance;

    private final SharedPreferences mPreferences;

    /**
     * Constructor for <code>PreferenceUtils</code>
     *
     * @param context The {@link Context} to use.
     */
    public PreferenceUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @param context The {@link Context} to use.
     * @return A singleton of this class
     */
    public static final PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Set the listener for preference change
     *
     * @param listener
     */
    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Returns the last page the user was on when the app was exited.
     *
     * @return The page to start on when the app is opened.
     */
    public final int getStartPage() {
        return mPreferences.getInt(START_PAGE, DEFFAULT_PAGE);
    }

    /**
     * Returns the current theme color.
     *
     * @param context The {@link Context} to use.
     * @return The default theme color.
     */
    public final int getDefaultThemeColor(final Context context) {
        return mPreferences.getInt(DEFAULT_THEME_COLOR,
                context.getResources().getColor(android.R.color.holo_blue_bright));
    }

    /**
     * @return True if the user has checked to only download images on Wi-Fi,
     * false otherwise
     */
    public final boolean onlyOnWifi() {
        return mPreferences.getBoolean(ONLY_ON_WIFI, true);
    }

    /**
     * @return True if the user has checked to download missing album covers,
     * false otherwise.
     */
    public final boolean downloadMissingArtwork() {
        return mPreferences.getBoolean(DOWNLOAD_MISSING_ARTWORK, true);
    }

    /**
     * @return True if the user has checked to download missing artist images,
     * false otherwise.
     */
    public final boolean downloadMissingArtistImages() {
        return mPreferences.getBoolean(DOWNLOAD_MISSING_ARTIST_IMAGES, true);
    }

    /**
     * @parm lastAddedMillis timestamp in millis used as a cutoff for last added playlist
     */
    public void setLastAddedCutoff(long lastAddedMillis) {
        mPreferences.edit().putLong(LAST_ADDED_CUTOFF, lastAddedMillis).commit();
    }

    public long getLastAddedCutoff() {
        return mPreferences.getLong(LAST_ADDED_CUTOFF, 0L);
    }

    /**
     * @return Whether we want to show lyrics
     */
    public final boolean getShowLyrics() {
        return mPreferences.getBoolean(SHOW_LYRICS, true);
    }

    public boolean getShowVisualizer() {
        return mPreferences.getBoolean(SHOW_VISUALIZER, true);
    }

    public boolean getShakeToPlay() {
        return mPreferences.getBoolean(SHAKE_TO_PLAY, false);
    }

    public boolean getShowAlbumArtOnLockscreen() {
        return mPreferences.getBoolean(SHOW_ALBUM_ART_ON_LOCKSCREEN, true);
    }
}
