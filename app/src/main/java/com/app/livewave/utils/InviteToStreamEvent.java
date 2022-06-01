package com.app.livewave.utils;

import com.app.livewave.models.ResponseModels.FollowModel;

public class InviteToStreamEvent {
    FollowModel followModel;

    public InviteToStreamEvent(FollowModel followModel) {
        this.followModel=followModel;
    }

    public FollowModel getFollowModel() {
        return followModel;
    }

    public void setFollowModel(FollowModel followModel) {
        this.followModel = followModel;
    }
}
