package com.app.livewave.models;

import android.net.Uri;

public class VideoItem {
    String title,description;
    Uri url;

    public VideoItem(Uri url, String title, String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
