package com.app.livewave.models;

public class SettingsModel {
    String title, des;
    int icon;
    int id;

    public SettingsModel(int id, String title, String des, int icon) {
        this.id = id;
        this.title = title;
        this.des = des;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
