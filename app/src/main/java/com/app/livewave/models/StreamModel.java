package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StreamModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("platformID")
    @Expose
    private String platformID;
    @SerializedName("paid")
    @Expose
    private Integer paid;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("streamer_name")
    @Expose
    private String streamerName;
    @SerializedName("streamer_photo")
    @Expose
    private String streamerPhoto;
    @SerializedName("streamer_username")
    @Expose
    private String streamerUsername;
    @SerializedName("is_paid")
    @Expose
    private Integer iPaid;
    @SerializedName("sharingID")
    @Expose
    private String sharingId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatformID() {
        return platformID;
    }

    public void setPlatformID(String platformID) {
        this.platformID = platformID;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public void setStreamerName(String streamerName) {
        this.streamerName = streamerName;
    }

    public String getStreamerPhoto() {
        return streamerPhoto;
    }

    public void setStreamerPhoto(String streamerPhoto) {
        this.streamerPhoto = streamerPhoto;
    }

    public String getStreamerUsername() {
        return streamerUsername;
    }

    public void setStreamerUsername(String streamerUsername) {
        this.streamerUsername = streamerUsername;
    }

    public Integer getIsPaid() {
        return iPaid;
    }

    public void setiPaid(Integer iPaid) {
        this.iPaid = iPaid;
    }

    public String getSharingId() {
        return sharingId;
    }

    public void setSharingId(String sharingId) {
        this.sharingId = sharingId;
    }

}
