package com.app.livewave.models.RequestModels;

public class ChangeSubscriptionStatus {

    int id;
    String status;

    public ChangeSubscriptionStatus() {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
