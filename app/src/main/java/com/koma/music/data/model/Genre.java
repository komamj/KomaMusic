package com.koma.music.data.model;

import android.text.TextUtils;

/**
 * Created by koma on 3/21/17.
 */

public class Genre {
    /**
     * The unique Id of the genre
     */
    public long mGenreId;

    /**
     * The genre name
     */
    public String mGenreName;

    /**
     * Constructor of <code>Genre</code>
     *
     * @param genreId   The Id of the genre
     * @param genreName The genre name
     */
    public Genre(final long genreId, final String genreName) {
        super();
        mGenreId = genreId;
        mGenreName = genreName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) mGenreId;
        result = prime * result + (mGenreName == null ? 0 : mGenreName.hashCode());
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
        final Genre other = (Genre) obj;
        if (mGenreId != other.mGenreId) {
            return false;
        }
        return TextUtils.equals(mGenreName, other.mGenreName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mGenreName;
    }
}