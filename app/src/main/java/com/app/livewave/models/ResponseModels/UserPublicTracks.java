package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserPublicTracks {

    @SerializedName("id")
    @Expose
    private Integer trackId;

    @SerializedName("track_path")
    @Expose
    private String trackPath;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("artist_name")
    @Expose
    private String artistName;

    @SerializedName("category_id")
    @Expose
    private String categoryId;

    @SerializedName("attachment")
    @Expose
    private String attachment;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("paid")
    @Expose
    private String paid;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("deleted")
    @Expose
    private String deleted;

    @SerializedName("is_public")
    @Expose
    private String isPublic;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getTrackPath() {
        return trackPath == null ? "" : trackPath ;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath;
    }

    public String getUserId() {
        return userId == null ? "" : userId ;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title == null ? "" : title ;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName == null ? "" : artistName ;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCategoryId() {
        return categoryId == null ? "" : categoryId ;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getAttachment() {
        return attachment == null ? "" : attachment ;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getStatus() {
        return status == null ? "" : status ;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaid() {
        return paid == null ? "" : paid ;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getAddress() {
        return address == null ? "" : address ;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeleted() {
        return deleted == null ? "" : deleted ;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getIsPublic() {
        return isPublic == null ? "" : isPublic ;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getAmount() {
        return amount == null ? "" : amount ;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description == null ? "" : description ;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt ;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt == null ? "" : updatedAt ;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
