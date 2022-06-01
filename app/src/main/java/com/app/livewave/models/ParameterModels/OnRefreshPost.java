package com.app.livewave.models.ParameterModels;

import com.app.livewave.models.ResponseModels.PostModel;

public class OnRefreshPost {
    boolean refresh;
    boolean editPost;
    PostModel postModel;



    public OnRefreshPost(boolean refresh, boolean editPost, PostModel postModel) {
        this.refresh = refresh;
        this.editPost = editPost;
        this.postModel = postModel;
    }


    public boolean isEditPost() {
        return editPost;
    }

    public void setEditPost(boolean editPost) {
        this.editPost = editPost;
    }

    public PostModel getPostModel() {
        return postModel;
    }

    public void setPostModel(PostModel postModel) {
        this.postModel = postModel;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
