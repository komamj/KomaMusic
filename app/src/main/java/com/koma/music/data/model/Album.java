package com.koma.music.data.model;

import android.text.TextUtils;

/**
 * A class that represents an Album.
 *
 * @author Koma
 */

public class Album {
    /**
     * The unique Id of the album
     */
    public long mAlbumId;

    /**
     * The name of the album
     */
    public String mAlbumName;

    /**
     * The album artist
     */
    public String mArtistName;

    /**
     * The number of songs in the album
     */
    public int mSongNumber;

    /**
     * The year the album was released
     */
    public String mYear;

    /**
     * Bucket label for the name - may not necessarily be the name - for example albums sorted by
     * artists would be the artist bucket label and not the album name bucket label
     */
    public String mBucketLabel;

    /**
     * Constructor of <code>Album</code>
     *
     * @param albumId    The Id of the album
     * @param albumName  The name of the album
     * @param artistName The album artist
     * @param songNumber The number of songs in the album
     * @param albumYear  The year the album was released
     */
    public Album(final long albumId, final String albumName, final String artistName,
                 final int songNumber, final String albumYear) {
        super();
        mAlbumId = albumId;
        mAlbumName = albumName;
        mArtistName = artistName;
        mSongNumber = songNumber;
        mYear = albumYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) mAlbumId;
        result = prime * result + (mAlbumName == null ? 0 : mAlbumName.hashCode());
        result = prime * result + (mArtistName == null ? 0 : mArtistName.hashCode());
        result = prime * result + mSongNumber;
        result = prime * result + (mYear == null ? 0 : mYear.hashCode());
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
        final Album other = (Album) obj;
        if (mAlbumId != other.mAlbumId) {
            return false;
        }
        if (!TextUtils.equals(mAlbumName, other.mAlbumName)) {
            return false;
        }
        if (!TextUtils.equals(mArtistName, other.mArtistName)) {
            return false;
        }
        if (mSongNumber != other.mSongNumber) {
            return false;
        }
        if (!TextUtils.equals(mYear, other.mYear)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mAlbumName;
    }
}
