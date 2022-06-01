package com.app.livewave.models.ParameterModels;

import java.io.Serializable;

public class StreamChatModel implements Serializable {
    String image, message, username;
    long sendAt;


    public StreamChatModel(String image, String message, String username, long sendAt) {
        this.image = image;
        this.message = message;
        this.username = username;
        this.sendAt = sendAt;
    }

    public StreamChatModel() {
    }

    public long getSendAt() {
        return sendAt;
    }

    public void setSendAt(long sendAt) {
        this.sendAt = sendAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}