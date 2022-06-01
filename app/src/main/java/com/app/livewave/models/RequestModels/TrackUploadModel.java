package com.app.livewave.models.RequestModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackUploadModel {

    @SerializedName("track_id")
    @Expose
    private int track_id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("artist_name")
    @Expose
    private String artist_name;
    @SerializedName("category_id")
    @Expose
    private String category_id;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("track")
    @Expose
    private String track;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("paid")
    @Expose
    private String paid;
    @SerializedName("amount")
    @Expose
    private String amount;

    public int getTrack_id() {
        return track_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}