package com.koma.music.service;

import android.provider.MediaStore;

/**
 * Created by koma on 3/23/17.
 */

public class Constants {
    /**
     * Indicates that the music has paused or resumed
     */
    public static final String PLAYSTATE_CHANGED = "com.cyanogenmod.eleven.playstatechanged";

    /**
     * Indicates that music playback position within
     * a title was changed
     */
    public static final String POSITION_CHANGED = "com.cyanogenmod.eleven.positionchanged";

    /**
     * Indicates the meta data has changed in some way, like a track change
     */
    public static final String META_CHANGED = "com.cyanogenmod.eleven.metachanged";

    /**
     * Indicates the queue has been updated
     */
    public static final String QUEUE_CHANGED = "com.cyanogenmod.eleven.queuechanged";

    /**
     * Indicates the queue has been updated
     */
    public static final String PLAYLIST_CHANGED = "com.cyanogenmod.eleven.playlistchanged";

    /**
     * Indicates the repeat mode changed
     */
    public static final String REPEATMODE_CHANGED = "com.cyanogenmod.eleven.repeatmodechanged";

    /**
     * Indicates the shuffle mode changed
     */
    public static final String SHUFFLEMODE_CHANGED = "com.cyanogenmod.eleven.shufflemodechanged";

    /**
     * Indicates the track fails to play
     */
    public static final String TRACK_ERROR = "com.cyanogenmod.eleven.trackerror";
    /**
     * Name of the track that was unable to play
     */
    public static final String TRACK_NAME = "trackname";
    /**
     * For backwards compatibility reasons, also provide sticky
     * broadcasts under the music package
     */
    public static final String KOMA_MUSIC_PACKAGE_NAME = "com.koma.music";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";

    /**
     * Called to indicate a general service commmand. Used in
     * {@link MediaButtonIntentReceiver}
     */
    public static final String SERVICECMD = "com.cyanogenmod.eleven.musicservicecommand";

    /**
     * Called to go toggle between pausing and playing the music
     */
    public static final String TOGGLEPAUSE_ACTION = "com.cyanogenmod.eleven.togglepause";

    /**
     * Called to go to pause the playback
     */
    public static final String PAUSE_ACTION = "com.cyanogenmod.eleven.pause";

    /**
     * Called to go to stop the playback
     */
    public static final String STOP_ACTION = "com.cyanogenmod.eleven.stop";

    /**
     * Called to go to the previous track or the beginning of the track if partway through the track
     */
    public static final String PREVIOUS_ACTION = "com.cyanogenmod.eleven.previous";

    /**
     * Called to go to the previous track regardless of how far in the current track the playback is
     */
    public static final String PREVIOUS_FORCE_ACTION = "com.cyanogenmod.eleven.previous.force";

    /**
     * Called to go to the next track
     */
    public static final String NEXT_ACTION = "com.cyanogenmod.eleven.next";

    /**
     * Called to change the repeat mode
     */
    public static final String REPEAT_ACTION = "com.cyanogenmod.eleven.repeat";

    /**
     * Called to change the shuffle mode
     */
    public static final String SHUFFLE_ACTION = "com.cyanogenmod.eleven.shuffle";

    public static final String FROM_MEDIA_BUTTON = "frommediabutton";

    public static final String TIMESTAMP = "timestamp";

    /**
     * Used to easily notify a list that it should refresh. i.e. A playlist
     * changes
     */
    public static final String REFRESH = "com.cyanogenmod.eleven.refresh";

    /**
     * Used by the alarm intent to shutdown the service after being idle
     */
    public static final String SHUTDOWN = "com.cyanogenmod.eleven.shutdown";

    /**
     * Called to notify of a timed text
     */
    public static final String NEW_LYRICS = "com.cyanogenmod.eleven.lyrics";

    /**
     * Called to update the remote control client
     */
    public static final String UPDATE_LOCKSCREEN = "com.cyanogenmod.eleven.updatelockscreen";

    public static final String CMDNAME = "command";

    public static final String CMDTOGGLEPAUSE = "togglepause";

    public static final String CMDSTOP = "stop";

    public static final String CMDPAUSE = "pause";

    public static final String CMDPLAY = "play";

    public static final String CMDPREVIOUS = "previous";

    public static final String CMDNEXT = "next";

    public static final String CMDHEADSETHOOK = "headsethook";

    public static final int IDCOLIDX = 0;

    /**
     * Moves a list to the next position in the queue
     */
    public static final int NEXT = 2;

    /**
     * Moves a list to the last position in the queue
     */
    public static final int LAST = 3;

    /**
     * Shuffles no songs, turns shuffling off
     */
    public static final int SHUFFLE_NONE = 0;

    /**
     * Shuffles all songs
     */
    public static final int SHUFFLE_NORMAL = 1;

    /**
     * Party shuffle
     */
    public static final int SHUFFLE_AUTO = 2;

    /**
     * Turns repeat off
     */
    public static final int REPEAT_NONE = 0;

    /**
     * Repeats the current track in a list
     */
    public static final int REPEAT_CURRENT = 1;

    /**
     * Repeats all the tracks in a list
     */
    public static final int REPEAT_ALL = 2;

    /**
     * Indicates when the track ends
     */
    public static final int TRACK_ENDED = 1;

    /**
     * Indicates that the current track was changed the next track
     */
    public static final int TRACK_WENT_TO_NEXT = 2;

    /**
     * Indicates the player died
     */
    public static final int SERVER_DIED = 3;

    /**
     * Indicates some sort of focus change, maybe a phone call
     */
    public static final int FOCUSCHANGE = 4;

    /**
     * Indicates to fade the volume down
     */
    public static final int FADEDOWN = 5;

    /**
     * Indicates to fade the volume back up
     */
    public static final int FADEUP = 6;

    /**
     * Notifies that there is a new timed text string
     */
    public static final int LYRICS = 7;

    /**
     * Indicates a headset hook key event
     */
    public static final int HEADSET_HOOK_EVENT = 8;

    public static final int DOUBLE_CLICK_TIMEOUT = 800;

    /**
     * Indicates waiting for another headset hook event has timed out
     */
    public static final int HEADSET_HOOK_MULTI_CLICK_TIMEOUT = 9;

    /**
     * Idle time before stopping the foreground notfication (5 minutes)
     */
    public static final int IDLE_DELAY = 5 * 60 * 1000;

    /**
     * Song play time used as threshold for rewinding to the beginning of the
     * track instead of skipping to the previous track when getting the PREVIOUS
     * command
     */
    public static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;

    /**
     * The max size allowed for the track history
     */
    public static final int MAX_HISTORY_SIZE = 1000;

    /**
     * The columns used to retrieve any info from the current track
     */
    public static final String[] PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };

    /**
     * The columns used to retrieve any info from the current album
     */
    public static final String[] ALBUM_PROJECTION = new String[]{
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.LAST_YEAR
    };
}
