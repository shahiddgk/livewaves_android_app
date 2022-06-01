package com.app.livewave.models;

public class MembersInfo {
    public String name;
    public int id;
    public String photo;
    public String username;
    public boolean hasReadLastMessage;
    public String type;
    public MembersInfo() {
    }

    public MembersInfo(String name, int id, String photo, String username, boolean hasReadLastMessage , String type) {
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.username = username;
        this.hasReadLastMessage = hasReadLastMessage;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHasReadLastMessage() {
        return hasReadLastMessage;
    }

    public void setHasReadLastMessage(boolean hasReadLastMessage) {
        this.hasReadLastMessage = hasReadLastMessage;
    }
}
