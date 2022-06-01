package com.app.livewave.models.RequestModels;

public class CreateSubscriptionRequestModel {

    int id;
    String title;
    String duration;
    String amount;
    int post_access;
    int event_access;
    int track_access;
    int ticket_access;
    int livestream_access;

    public CreateSubscriptionRequestModel(String title, int post_access, int event_access, int track_access, int ticket_access, int livestream_access) {
        this.title = title;
        this.post_access = post_access;
        this.event_access = event_access;
        this.track_access = track_access;
        this.ticket_access = ticket_access;
        this.livestream_access = livestream_access;
    }

    public int id() {
        return id;
    }

    public void setSubscription_id(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPost_access() {
        return post_access;
    }

    public void setPost_access(int post_access) {
        this.post_access = post_access;
    }

    public int getEvent_access() {
        return event_access;
    }

    public void setEvent_access(int event_access) {
        this.event_access = event_access;
    }

    public int getTrack_access() {
        return track_access;
    }

    public void setTrack_access(int track_access) {
        this.track_access = track_access;
    }

    public int getTicket_access() {
        return ticket_access;
    }

    public void setTicket_access(int ticket_access) {
        this.ticket_access = ticket_access;
    }

    public int getLivestream_access() {
        return livestream_access;
    }

    public void setLivestream_access(int livestream_access) {
        this.livestream_access = livestream_access;
    }
}
