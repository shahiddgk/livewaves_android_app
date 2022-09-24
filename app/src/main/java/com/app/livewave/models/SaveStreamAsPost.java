package com.app.livewave.models;

public class SaveStreamAsPost {

    String streamId;
    String description;
    int streamPrice;

    public SaveStreamAsPost(String streamId, String description, int streamPrice) {
        this.streamId = streamId;
        this.description = description;
        this.streamPrice = streamPrice;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStreamPrice() {
        return streamPrice;
    }

    public void setStreamPrice(int streamPrice) {
        this.streamPrice = streamPrice;
    }
}
