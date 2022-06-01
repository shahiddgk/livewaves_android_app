package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WavesModelResponse implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("duration")
    @Expose
    private double duration;
    @SerializedName("video_thumbnail")
    @Expose
    private String video_thumbnail;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("post_id")
    @Expose
    private int post_id;
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("profile_id")
    @Expose
    private int profile_id;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("total_reactions")
    @Expose
    private int total_reactions;
    @SerializedName("total_comments")
    @Expose
    private int total_comments;
    @SerializedName("total_shares")
    @Expose
    private int total_shares;
    @SerializedName("is_visible")
    @Expose
    private String is_visible;
    @SerializedName("is_following")
    @Expose
    private String is_following;
    @SerializedName("my_reaction")
    @Expose
    private String my_reaction;
    @SerializedName("sharingID")
    @Expose
    private String sharingID;
    @SerializedName("following_id")
    @Expose
    private int following_id;
    @SerializedName("followed_id")
    @Expose
    private int followed_id;

    public String getMy_reaction() {
        return my_reaction;
    }

    public void setMy_reaction(String my_reaction) {
        this.my_reaction = my_reaction;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public int getTotal_reactions() {
        return total_reactions;
    }

    public void setTotal_reactions(int total_reactions) {
        this.total_reactions = total_reactions;
    }

    public int getTotal_comments() {
        return total_comments;
    }

    public void setTotal_comments(int total_comments) {
        this.total_comments = total_comments;
    }

    public int getTotal_shares() {
        return total_shares;
    }

    public void setTotal_shares(int total_shares) {
        this.total_shares = total_shares;
    }

    public String getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(String is_visible) {
        this.is_visible = is_visible;
    }

    public String getIs_following() {
        return is_following;
    }

    public void setIs_following(String is_following) {
        this.is_following = is_following;
    }

    public int getFollowing_id() {
        return following_id;
    }

    public void setFollowing_id(int following_id) {
        this.following_id = following_id;
    }

    public int getFollowed_id() {
        return followed_id;
    }

    public void setFollowed_id(int followed_id) {
        this.followed_id = followed_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getSharingID() {
        return sharingID;
    }

    public void setSharingID(String sharingID) {
        this.sharingID = sharingID;
    }
}
