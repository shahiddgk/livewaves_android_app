package com.app.livewave.models.RequestModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddTrackToPlaylistModel {

    @SerializedName("playlist_id")
    @Expose
    private int playlist_id;
    @SerializedName("tracks_id")
    @Expose
    private int tracks_id;

    public AddTrackToPlaylistModel(int playlist_id, int tracks_id) {
        this.playlist_id = playlist_id;
        this.tracks_id = tracks_id;
    }

    public int getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public int getTracks_id() {
        return tracks_id;
    }

    public void setTracks_id(int tracks_id) {
        this.tracks_id = tracks_id;
    }
}