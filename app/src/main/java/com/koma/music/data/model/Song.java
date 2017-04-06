package com.koma.music.data.model;

import android.text.TextUtils;

/**
 * A class that represents an Song.
 *
 * @author Koma
 */

public class Song {
    /**
     * The unique Id of the song
     */
    public long mSongId;

    /**
     * The song name
     */
    public String mSongName;

    /**
     * The song artist
     */
    public String mArtistName;

    /**
     * The song album
     */
    public String mAlbumName;

    /**
     * The album id
     */
    public long mAlbumId;

    /**
     * The song duration in seconds
     */
    public int mDuration;
    /**
     * Bucket label for the name - may not necessarily be the name - for example songs sorted by
     * artists would be the artist bucket label and not the song name bucket label
     */
    public String mBucketLabel;

    /**
     * Constructor of <code>Song</code>
     *
     * @param songId     The Id of the song
     * @param songName   The name of the song
     * @param artistName The song artist
     * @param albumName  The song album
     * @param duration   The duration of a song in seconds
     */
    public Song(final long songId, final String songName, final String artistName,
                final String albumName, final long albumId, final int duration) {
        mSongId = songId;
        mSongName = songName;
        mArtistName = artistName;
        mAlbumName = albumName;
        mAlbumId = albumId;
        mDuration = duration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (mAlbumName == null ? 0 : mAlbumName.hashCode());
        result = prime * result + (int) mAlbumId;
        result = prime * result + (mArtistName == null ? 0 : mArtistName.hashCode());
        result = prime * result + mDuration;
        result = prime * result + (int) mSongId;
        result = prime * result + (mSongName == null ? 0 : mSongName.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Song other = (Song) obj;
        if (mSongId != other.mSongId) {
            return false;
        }
        if (!TextUtils.equals(mAlbumName, other.mAlbumName)) {
            return false;
        }
        if (mAlbumId != other.mAlbumId) {
            return false;
        }
        if (!TextUtils.equals(mArtistName, other.mArtistName)) {
            return false;
        }
        if (mDuration != other.mDuration) {
            return false;
        }
        if (!TextUtils.equals(mSongName, other.mSongName)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mSongName;
    }
}
