package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinStreamModel {
    @SerializedName("stream_id")
    @Expose
    private Integer streamId;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("status")
    @Expose
    private Integer statu;
    @SerializedName("platformID")
    @Expose
    private String platformID;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("parentPlatformID")
    @Expose
    private String parentPlatformID;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParentPlatformID() {
        return parentPlatformID;
    }

    public void setParentPlatformID(String parentPlatformID) {
        this.parentPlatformID = parentPlatformID;
    }

    public Integer getStreamId() {
        return streamId;
    }

    public void setStreamId(Integer streamId) {
        this.streamId = streamId;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getStatu() {
        return statu;
    }

    public void setStatu(Integer statu) {
        this.statu = statu;
    }

    public String getPlatformID() {
        return platformID;
    }

    public void setPlatformID(String platformID) {
        this.platformID = platformID;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
