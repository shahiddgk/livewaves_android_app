package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountAnalytics {
    @SerializedName("followers")
    @Expose
    private Integer followers;
    @SerializedName("posts")
    @Expose
    private Integer posts;
    @SerializedName("streams")
    @Expose
    private Integer streams;
    @SerializedName("events")
    @Expose
    private Integer events;
    @SerializedName("videoViews")
    @Expose
    private Integer videoViews;
    @SerializedName("subscriptions")
    @Expose
    private Integer subscriptions;

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public Integer getStreams() {
        return streams;
    }

    public void setStreams(Integer streams) {
        this.streams = streams;
    }

    public Integer getEvents() {
        return events;
    }

    public void setEvents(Integer events) {
        this.events = events;
    }

    public Integer getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(Integer videoViews) {
        this.videoViews = videoViews;
    }

    public Integer getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Integer subscriptions) {
        this.subscriptions = subscriptions;
    }
}
