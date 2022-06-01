package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FollowModel implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("followed_name")
    @Expose
    private String followedName;
    @SerializedName("following_name")
    @Expose
    private String followingName;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("following_profile")
    @Expose
    private String followingProfile;
    @SerializedName("followed_profile")
    @Expose
    private String followedProfile;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("following_username")
    @Expose
    private String followingUsername;
    @SerializedName("followed_username")
    @Expose
    private String followedUsername;
    @SerializedName("verified")
    @Expose
    private String verified;
    @SerializedName("total_followers")
    @Expose
    private int totalFollowers;
    @SerializedName("following_id")
    @Expose
    private int followingId;
    @SerializedName("followed_id")
    @Expose
    private int followedId;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFollowingName() {
        return followingName  == null ? "" : followingName;
    }

    public void setFollowingName(String followingName) {
        this.followingName = followingName;
    }

    public String getFollowingProfile() {
        return followingProfile  == null ? "" : followingProfile;
    }

    public void setFollowingProfile(String followingProfile) {
        this.followingProfile = followingProfile;
    }

    public String getFollowingUsername() {
        return followingUsername  == null ? "" : followingUsername;
    }

    public void setFollowingUsername(String followingUsername) {
        this.followingUsername = followingUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name  == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFollowedName() {
        return followedName  == null ? "" : followedName;
    }

    public void setFollowedName(String followedName) {
        this.followedName = followedName;
    }

    public String getPhoto() {
        return photo  == null ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFollowedProfile() {
        return followedProfile  == null ? "" : followedProfile;
    }

    public void setFollowedProfile(String followedProfile) {
        this.followedProfile = followedProfile;
    }

    public String getUsername() {
        return username  == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowedUsername() {
        return followedUsername  == null ? "" : followedUsername;
    }

    public void setFollowedUsername(String followedUsername) {
        this.followedUsername = followedUsername;
    }

    public String getVerified() {
        return verified  == null ? "" : verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public int getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public int getFollowingId() {
        return followingId;
    }

    public void setFollowingId(int followingId) {
        this.followingId = followingId;
    }

    public int getFollowedId() {
        return followedId;
    }

    public void setFollowedId(int followedId) {
        this.followedId = followedId;
    }

}
