package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class AlertModel {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("sender_id")
    @Expose
    private int senderId;
    @SerializedName("contentID")
    @Expose
    private BigInteger contentID;
    @SerializedName("childID")
    @Expose
    private int childID;
    @SerializedName("photo")
    @Expose
    private String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject  == null ? "" : subject ;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body   == null ? "" : body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type  == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public BigInteger getContentID() {
        return contentID;
    }

    public void setContentID(BigInteger contentID) {
        this.contentID = contentID;
    }

    public int getChildID() {
        return childID;
    }

    public void setChildID(int childID) {
        this.childID = childID;
    }

    public String getPhoto() {
        return photo  == null ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
