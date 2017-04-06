package com.koma.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by koma on 3/21/17.
 */

/**
 * This is used by the music playback service to track the music tracks it is playing
 * It has extra meta data to determine where the track came from so that we can show the appropriate
 * song playing indicator
 */
public class MusicPlaybackTrack implements Parcelable {
    /**
     * The track id
     */
    public long mId;

    /**
     * Where was this track added from? Artist id/Album id/Playlist id
     */
    public long mSourceId;

    /**
     * This is only used for playlists since it is possible that a playlist can contain the same
     * song multiple times.  So to prevent the song indicator showing up multiple times, we need
     * to keep track of the position
     */
    public int mSourcePosition;

    /**
     * Parcelable creator
     */
    public static final Creator<MusicPlaybackTrack> CREATOR = new Creator<MusicPlaybackTrack>() {
        @Override
        public MusicPlaybackTrack createFromParcel(Parcel source) {
            return new MusicPlaybackTrack(source);
        }

        @Override
        public MusicPlaybackTrack[] newArray(int size) {
            return new MusicPlaybackTrack[size];
        }
    };

    public MusicPlaybackTrack(long id, long sourceId, int sourcePosition) {
        mId = id;
        mSourceId = sourceId;
        mSourcePosition = sourcePosition;
    }

    public MusicPlaybackTrack(Parcel in) {
        mId = in.readLong();
        mSourceId = in.readLong();
        mSourcePosition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mSourceId);
        dest.writeInt(mSourcePosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MusicPlaybackTrack) {
            MusicPlaybackTrack other = (MusicPlaybackTrack) o;
            if (other != null) {
                if (mId == other.mId
                        && mSourceId == other.mSourceId
                        //&& mSourceType == other.mSourceType
                        && mSourcePosition == other.mSourcePosition) {
                    return true;
                }

                return false;
            }
        }

        return super.equals(o);
    }
}
