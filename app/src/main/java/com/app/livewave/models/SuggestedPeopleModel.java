package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuggestedPeopleModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("verified")
    @Expose
    private String verified;
    @SerializedName("total_followers")
    @Expose
    private String total_followers;
    @SerializedName("total_following")
    @Expose
    private String total_following;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getTotal_followers() {
        return total_followers;
    }

    public void setTotal_followers(String total_followers) {
        this.total_followers = total_followers;
    }

    public String getTotal_following() {
        return total_following;
    }

    public void setTotal_following(String total_following) {
        this.total_following = total_following;
    }
}
