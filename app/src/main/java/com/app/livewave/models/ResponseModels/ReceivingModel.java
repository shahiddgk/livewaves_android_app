package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceivingModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("stream_id")
    @Expose
    private Object streamId;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("post_id")
    @Expose
    private Object postId;
    @SerializedName("transactionID")
    @Expose
    private String transactionID;
    @SerializedName("ticketID")
    @Expose
    private String ticketID;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("tax")
    @Expose
    private Integer tax;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("post_user_id")
    @Expose
    private Object postUserId;
    @SerializedName("event_user_id")
    @Expose
    private Integer eventUserId;
    @SerializedName("stream_user_id")
    @Expose
    private Object streamUserId;

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

    public Object getStreamId() {
        return streamId;
    }

    public void setStreamId(Object streamId) {
        this.streamId = streamId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Object getPostId() {
        return postId;
    }

    public void setPostId(Object postId) {
        this.postId = postId;
    }

    public String getTransactionID() {
        return transactionID == null ? "" : transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTicketID() {
        return ticketID == null ? "" : ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo == null ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Object getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(Object postUserId) {
        this.postUserId = postUserId;
    }

    public Integer getEventUserId() {
        return eventUserId;
    }

    public void setEventUserId(Integer eventUserId) {
        this.eventUserId = eventUserId;
    }

    public Object getStreamUserId() {
        return streamUserId;
    }

    public void setStreamUserId(Object streamUserId) {
        this.streamUserId = streamUserId;
    }

}
