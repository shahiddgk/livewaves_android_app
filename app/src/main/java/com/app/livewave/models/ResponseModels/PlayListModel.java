package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlayListModel {

    @SerializedName("playlist_id")
    @Expose
    private Integer playlistId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("tracks")
    @Expose
    private ArrayList<Track> tracks = null;
    @SerializedName("is_public")
    @Expose
    private String isPublic;
    @SerializedName("playlist_thumbnail")
    @Expose
    private String playlistThumbnail;

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public String getTitle() {
        return title == null ? "" : title ;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt ;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Track> getTracks() {
        return tracks == null ? new ArrayList<>() : tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public String getIsPublic() {
        return isPublic == null ? "" : isPublic ;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getPlaylistThumbnail() {
        return playlistThumbnail == null ? "" : playlistThumbnail ;
    }

    public void setPlaylistThumbnail(String playlistThumbnail) {
        this.playlistThumbnail = playlistThumbnail;
    }
}

