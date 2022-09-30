package com.app.livewave.models;

public class SaveVideoFromGallery {

    boolean success;
    String message;
    String dataId;
    int errorId;

    public SaveVideoFromGallery(boolean success, String message, String dataId, int errorId) {
        this.success = success;
        this.message = message;
        this.dataId = dataId;
        this.errorId = errorId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }
}
