package com.app.livewave.models.ResponseModels;

import com.app.livewave.utils.BaseUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionPlan {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("delete")
    @Expose
    private String delete;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("post_access")
    @Expose
    private String postAccess;
    @SerializedName("event_access")
    @Expose
    private String eventAccess;
    @SerializedName("track_access")
    @Expose
    private String trackAccess;
    @SerializedName("ticket_access")
    @Expose
    private String ticketAccess;
    @SerializedName("livestream_access")
    @Expose
    private String streamAccess;

    public SubscriptionPlan(String title, Integer duration, Integer amount, String postAccess, String eventAccess, String trackAccess, String streamAccess, String ticketAccess) {
        this.title = title;
        this.duration = duration;
        this.amount = amount;
        this.postAccess = postAccess;
        this.eventAccess = eventAccess;
        this.trackAccess = trackAccess;
        this.ticketAccess = ticketAccess;
        this.streamAccess = streamAccess;
    }

//    public SubscriptionPlan(String title, Integer duration, Integer amount) {
//        this.title = title;
//        this.duration = duration;
//        this.amount = amount;
//    }

    public Integer getId() {
        return id == null ? 0 : id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration == null ? 0 : duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getAmount() {
        return amount == null ? 0 : amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelete() {
        return delete == null ? "" : delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getCreatedAt() {
        return BaseUtils.getDateFromUTC(createdAt == null ? "" : createdAt);
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPostAccess() {
        return postAccess == null ? "" : postAccess;
    }

    public void setPostAccess(String postAccess) {
        this.postAccess = postAccess;
    }

    public String getEventAccess() {
        return eventAccess == null ? "" : eventAccess;
    }

    public void setEventAccess(String eventAccess) {
        this.eventAccess = eventAccess;
    }

    public String getTrackAccess() {
        return trackAccess == null ? "" : trackAccess;
    }

    public void setTrackAccess(String trackAccess) {
        this.trackAccess = trackAccess;
    }

    public String getTicketAccess() {
        return ticketAccess == null ? "" : ticketAccess;
    }

    public void setTicketAccess(String ticketAccess) {
        this.ticketAccess = ticketAccess;
    }

    public String getStreamAccess() {
        return streamAccess == null ? "" : streamAccess;
    }

    public void setStreamAccess(String streamAccess) {
        this.streamAccess = streamAccess;
    }
}