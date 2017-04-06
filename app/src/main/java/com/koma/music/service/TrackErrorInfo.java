package com.koma.music.service;

/**
 * Created by koma on 3/23/17.
 */

public class TrackErrorInfo {
    private long mId;
    private String mTrackName;

    public TrackErrorInfo(long id, String trackName) {
        mId = id;
        mTrackName = trackName;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getId() {
        return this.mId;
    }

    public void setTrackName(String trackName) {
        this.mTrackName = trackName;
    }

    public String getTrackName() {
        return this.mTrackName;
    }
}
