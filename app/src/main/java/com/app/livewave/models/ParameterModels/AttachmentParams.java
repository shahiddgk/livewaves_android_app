package com.app.livewave.models.ParameterModels;

public class AttachmentParams {

    String path, extension,duration;
//    Double duration;

    public AttachmentParams(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    public AttachmentParams(String path, String extension, String duration) {
        this.path = path;
        this.extension = extension;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
