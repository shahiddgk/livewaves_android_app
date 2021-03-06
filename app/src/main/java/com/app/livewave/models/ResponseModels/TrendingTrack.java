package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrendingTrack {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("track_path")
    @Expose
    private String trackPath;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("artist_name")
    @Expose
    private String artist_name;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("paid")
    @Expose
    private String paid;
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

    private String name;

    private Integer qty ;

    public TrendingTrack(Integer id, String trackPath, Integer userId, String title, String artist_name, Integer categoryId, String attachment, String status, String paid, String deleted, String isPublic, String amount, String description, String createdAt, String updatedAt,Integer qty) {
        this.id = id;
        this.trackPath = trackPath;
        this.userId = userId;
        this.title = title;
        this.artist_name = artist_name;
        this.categoryId = categoryId;
        this.attachment = attachment;
        this.status = status;
        this.paid = paid;
        this.deleted = deleted;
        this.isPublic = isPublic;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.qty = qty;
    }

    public TrendingTrack(Integer id, String trackPath, Integer userId, String title, String artist_name, Integer categoryId, String attachment, String status, String paid, String deleted, String isPublic, String amount, String description, String createdAt, String updatedAt, String name,Integer qty) {
        this.id = id;
        this.trackPath = trackPath;
        this.userId = userId;
        this.title = title;
        this.artist_name = artist_name;
        this.categoryId = categoryId;
        this.attachment = attachment;
        this.status = status;
        this.paid = paid;
        this.deleted = deleted;
        this.isPublic = isPublic;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.qty = qty;
    }

    public Integer getId() {
        return id == null ? 0 : id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrackPath() {
        return trackPath == null ? "" : trackPath;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath;
    }

    public Integer getUserId() {
        return userId == null ? 0 : userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_name() {
        return artist_name == null ? "" : artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public Integer getCategoryId() {
        return categoryId == null ? 0 : categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getAttachment() {
        return attachment == null ? "0" : attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaid() {
        return paid == null ? "0" : paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getDeleted() {
        return deleted == null ? "0" : deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getIsPublic() {
        return isPublic == null ? "0" : isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getAmount() {
        return amount == null ? "0" : amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int isQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
