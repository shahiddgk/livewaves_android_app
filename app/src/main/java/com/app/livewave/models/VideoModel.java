package com.app.livewave.models;

public class VideoModel {
    String title, video, image;

    public VideoModel(String title, String video, String image) {
        this.title = title;
        this.video = video;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
