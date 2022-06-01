package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReactionModel {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("reactor_id")
    @Expose
    private int reactorId;
    @SerializedName("post_id")
    @Expose
    private int postId;
    @SerializedName("reaction")
    @Expose
    private String reaction;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("user")
    @Expose
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReactorId() {
        return reactorId;
    }

    public void setReactorId(int reactorId) {
        this.reactorId = reactorId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getReaction() {
        return reaction == null ? "" : reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
