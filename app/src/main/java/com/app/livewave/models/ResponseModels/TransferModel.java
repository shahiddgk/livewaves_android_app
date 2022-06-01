package com.app.livewave.models.ResponseModels;

import com.app.livewave.models.StreamModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("stream_id")
    @Expose
    private Integer streamId;
    @SerializedName("event_id")
    @Expose
    private Object eventId;
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
    private Double amount;
    @SerializedName("tax")
    @Expose
    private Double tax;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("event")
    @Expose
    private EventModel event;
    @SerializedName("stream")
    @Expose
    private StreamModel stream;
    @SerializedName("post")
    @Expose
    private PostModel post;

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

    public Integer getStreamId() {
        return streamId;
    }

    public void setStreamId(Integer streamId) {
        this.streamId = streamId;
    }

    public Object getEventId() {
        return eventId;
    }

    public void setEventId(Object eventId) {
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
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

    public Object getEvent() {
        return event;
    }

    public void setEvent(EventModel event) {
        this.event = event;
    }

    public StreamModel getStream() {
        return stream;
    }

    public void setStream(StreamModel stream) {
        this.stream = stream;
    }

    public PostModel getPost() {
        return post;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

}
