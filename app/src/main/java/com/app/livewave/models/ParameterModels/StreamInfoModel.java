package com.app.livewave.models.ParameterModels;

import java.io.Serializable;

public class StreamInfoModel implements Serializable {
    String platformId, state;
    Long animation;
    int viewers,guestId;
//    boolean completed;

    public StreamInfoModel(Long animation, String platformId, int viewers, String state,int guestId) {
        this.platformId = platformId;
        this.animation = animation;
        this.viewers = viewers;
        this.state = state;
        this.guestId=guestId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public StreamInfoModel() {
    }


    public Long getAnimation() {
        return animation;
    }

    public void setAnimation(Long animation) {
        this.animation = animation;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
}
