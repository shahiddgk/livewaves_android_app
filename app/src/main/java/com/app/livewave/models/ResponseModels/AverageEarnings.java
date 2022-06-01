package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AverageEarnings {
    @SerializedName("streams")
    @Expose
    private float streams;
    @SerializedName("events")
    @Expose
    private float events;
    @SerializedName("posts")
    @Expose
    private float posts;
    @SerializedName("tip")
    @Expose
    private float tip;
    @SerializedName("total")
    @Expose
    private String total;

    public float getStreams() {
        return streams;
    }

    public void setStreams(float streams) {
        this.streams = streams;
    }

    public float getEvents() {
        return events;
    }

    public void setEvents(float events) {
        this.events = events;
    }

    public float getPosts() {
        return posts;
    }

    public void setPosts(float posts) {
        this.posts = posts;
    }

    public float getTip() {
        return tip;
    }

    public void setTip(float tip) {
        this.tip = tip;
    }

    public String getTotal() {
        return total  == null ? "" : total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
