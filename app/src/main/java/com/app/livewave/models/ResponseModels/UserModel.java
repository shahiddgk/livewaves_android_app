package com.app.livewave.models.ResponseModels;

import com.app.livewave.models.StreamModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserModel implements Serializable {
    private final static long serialVersionUID = -7761184276668503087L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fb_id")
    @Expose
    private Object fbId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("twitter_id")
    @Expose
    private Object twitterId;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("balance")
    @Expose
    private double balance;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private Object latitude;
    @SerializedName("longitude")
    @Expose
    private Object longitude;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("cover_photo")
    @Expose
    private String coverPhoto;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("private")
    @Expose
    private String _private;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("verified")
    @Expose
    private String verified;
    @SerializedName("withdrawl_account")
    @Expose
    private String withdrawlAccount;
    @SerializedName("is_admin")
    @Expose
    private Integer isAdmin;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("web_fcm_token")
    @Expose
    private Object webFcmToken;
    @SerializedName("total_following")
    @Expose
    private double totalFollowing;
    @SerializedName("total_followers")
    @Expose
    private double totalFollowers;
    @SerializedName("total_requests")
    @Expose
    private Integer totalRequests;
    @SerializedName("total_posts")
    @Expose
    private Integer totalPosts;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("follow")
    @Expose
    private Integer follow;
    @SerializedName("requested")
    @Expose
    private Integer requested;
    @SerializedName("stream")
    @Expose
    private StreamModel stream;
    @SerializedName("streaming")
    @Expose
    private Integer streaming;
    @SerializedName("is_blocked")
    @Expose
    private Integer isBlocked;
    @SerializedName("am_i_blocked")
    @Expose
    private Integer amIBlocked;
    @SerializedName("category_id")
    @Expose
    private Integer category_id;
    @SerializedName("subcategory_id")
    @Expose
    private Integer subcategory_id;

    public UserModel() {
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Integer getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(Integer subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public UserModel(Integer id, String name, String username, String photo) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.photo = photo;
    }

    public Integer getFollow() {
        return follow;
    }

    public Integer getRequested() {
        return requested;
    }

    public StreamModel getStream() {
        return stream;
    }

    public Integer getStreaming() {
        return streaming;
    }

    public Integer getIsBlocked() {
        return isBlocked;
    }

    public Integer getAmIBlocked() {
        return amIBlocked;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name == null? "":name;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public Object getFbId() {
        return fbId;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public Object getTwitterId() {
        return twitterId;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public String getBio() {
        return bio == null ? "" : bio;
    }

    public double getBalance() {
        return balance;
    }


    public String getAddress() {
        return address == null ? "" : address;
    }


    public Object getLatitude() {
        return latitude;
    }


    public Object getLongitude() {
        return longitude;
    }

    public String getPhoto() {
        return photo == null ? "" : photo;
    }

    public String getCoverPhoto() {
        return coverPhoto == null ? "" : coverPhoto;
    }

    public String getDeleted() {
        return deleted == null ? "" : deleted;
    }

    public String getPrivate() {
        return _private == null ? "" : _private;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public String getVerified() {
        return verified == null ? "" : verified;
    }

    public String getWithdrawlAccount() {
        return withdrawlAccount == null ? "" : withdrawlAccount;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public String getFcmToken() {
        return fcmToken == null ? "" : fcmToken;
    }

    public Object getWebFcmToken() {
        return webFcmToken;
    }

    public double getTotalFollowing() {
        return totalFollowing;
    }

    public double getTotalFollowers() {
        return totalFollowers;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public Integer getTotalPosts() {
        return totalPosts;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt == null ? "" : updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFbId(Object fbId) {
        this.fbId = fbId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTwitterId(Object twitterId) {
        this.twitterId = twitterId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public void set_private(String _private) {
        this._private = _private;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public void setWithdrawlAccount(String withdrawlAccount) {
        this.withdrawlAccount = withdrawlAccount;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setWebFcmToken(Object webFcmToken) {
        this.webFcmToken = webFcmToken;
    }

    public void setTotalFollowing(double totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public void setTotalFollowers(double totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void setTotalPosts(Integer totalPosts) {
        this.totalPosts = totalPosts;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    public void setRequested(Integer requested) {
        this.requested = requested;
    }

    public void setStream(StreamModel stream) {
        this.stream = stream;
    }

    public void setStreaming(Integer streaming) {
        this.streaming = streaming;
    }

    public void setIsBlocked(Integer isBlocked) {
        this.isBlocked = isBlocked;
    }

    public void setAmIBlocked(Integer amIBlocked) {
        this.amIBlocked = amIBlocked;
    }
}
