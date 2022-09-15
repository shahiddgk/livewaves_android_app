package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReadUnreadMessagesNotification {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("read_status")
    @Expose
    private String read_status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


    public Integer getId() {
        return id  == null ? 0 : id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId  == null ? 0 : userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSenderId() {
        return senderId  == null ? 0 : senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getRead_status() {
        return read_status  == null ? "" : read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }

    public String getCreatedAt() {
        return createdAt  == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt  == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
