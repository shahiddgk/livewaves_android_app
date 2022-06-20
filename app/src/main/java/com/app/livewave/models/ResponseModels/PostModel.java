package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostModel implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("profile_id")
    @Expose
    private int profileId;
    @SerializedName("paid")
    @Expose
    private String paid;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("attachments")
    @Expose
    private List<AttachmentModel> attachments = null;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("attachment_type")
    @Expose
    private String attachmentType;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("ids")
    @Expose
    private String ids;
    @SerializedName("total_reactions")
    @Expose
    private int totalReactions;
    @SerializedName("total_comments")
    @Expose
    private int totalComments;
    @SerializedName("total_shares")
    @Expose
    private int totalShares;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("tagsData")
    @Expose
    private List<TagModel> tagsData = null;
    @SerializedName("is_paid")
    @Expose
    private int isPaid;
    @SerializedName("sharingID")
    @Expose
    private String sharingID;
    @SerializedName("preview_url")
    @Expose
    private String preview_url;
    @SerializedName("is_visible")
    @Expose
    private String is_visible;
    @SerializedName("user")
    @Expose
    private UserModel user;
    @SerializedName("profile")
    @Expose
    private UserModel profile;
    @SerializedName("my_reaction")
    @Expose
    private int myReaction;
    @SerializedName("shared_id")
    @Expose
    private Integer shared_id;
    @SerializedName("sharedPost")
    @Expose
    private PostModel sharedPost;
    @SerializedName("reactions")
    @Expose
    private List<ReactionModel> reactions = null;

    public Integer getShared_id() {
        return shared_id;
    }

    public void setShared_id(Integer shared_id) {
        this.shared_id = shared_id;
    }

    public PostModel getSharedPost() {
        return sharedPost;
    }

    public void setSharedPost(PostModel sharedPost) {
        this.sharedPost = sharedPost;
    }


    public String getPreview_url() {
        return preview_url == null ? "" :  preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getIs_visible() {
        return is_visible == null ? "" :  is_visible;
    }

    public void setIs_visible(String is_visible) {
        this.is_visible = is_visible;
    }


    public int getMyReaction() {
        return myReaction;
    }

    public void setMyReaction(int myReaction) {
        this.myReaction = myReaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getPaid() {
        return paid == null ? "" : paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPrivacy() {
        return privacy == null ? "" : privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AttachmentModel> getAttachments() {
        return attachments == null? new ArrayList<>() :attachments ;
    }

    public void setAttachments(List<AttachmentModel> attachments) {
        this.attachments = attachments;
    }


    public String getThumbnail() {
        return thumbnail  == null ? "" : thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getTags() {
        return tags  == null ? "" :  tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIds() {
        return ids == null ? "" : ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public int getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(int totalReactions) {
        this.totalReactions = totalReactions;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
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

    public List<TagModel> getTagsData() {
        return tagsData  == null? new ArrayList<>() : tagsData;
    }

    public void setTagsData(List<TagModel> tagsData) {
        this.tagsData = tagsData;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public String getSharingID() {
        return sharingID == null ? "" : sharingID;
    }

    public void setSharingID(String sharingID) {
        this.sharingID = sharingID;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getProfile() {
        return profile;
    }

    public void setProfile(UserModel profile) {
        this.profile = profile;
    }

    public List<ReactionModel> getReactions() {
        return reactions  == null? new ArrayList<>() : reactions;
    }

    public void setReactions(List<ReactionModel> reactions) {
        this.reactions = reactions;
    }
}