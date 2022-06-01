
package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("duration")
    @Expose
    private double duration;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("post_access")
    @Expose
    private String post_access;
    @SerializedName("event_access")
    @Expose
    private String event_access;
    @SerializedName("track_access")
    @Expose
    private String track_access;
    @SerializedName("livestream_access")
    @Expose
    private String livestream_access;
    @SerializedName("ticket_access")
    @Expose
    private String ticket_access;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("stripe_price")
    @Expose
    private String stripe_price;
    @SerializedName("stripe_plan")
    @Expose
    private String stripe_plan;


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

    public String getTitle() {
        return title  == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPost_access() {
        return post_access  == null ? "" : post_access;
    }

    public void setPost_access(String post_access) {
        this.post_access = post_access;
    }

    public String getEvent_access() {
        return event_access  == null ? "" : event_access;
    }

    public void setEvent_access(String event_access) {
        this.event_access = event_access;
    }

    public String getTrack_access() {
        return track_access  == null ? "" : track_access;
    }

    public void setTrack_access(String track_access) {
        this.track_access = track_access;
    }

    public String getLivestream_access() {
        return livestream_access  == null ? "" : livestream_access;
    }

    public void setLivestream_access(String livestream_access) {
        this.livestream_access = livestream_access;
    }

    public String getTicket_access() {
        return ticket_access  == null ? "" : ticket_access;
    }

    public void setTicket_access(String ticket_access) {
        this.ticket_access = ticket_access;
    }

    public String getStatus() {
        return status  == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleted() {
        return deleted  == null ? "" : deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
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

    public String getStripe_price() {
        return stripe_price  == null ? "" : stripe_price;
    }

    public void setStripe_price(String stripe_price) {
        this.stripe_price = stripe_price;
    }

    public String getStripe_plan() {
        return stripe_plan  == null ? "" : stripe_plan;
    }

    public void setStripe_plan(String stripe_plan) {
        this.stripe_plan = stripe_plan;
    }

}
