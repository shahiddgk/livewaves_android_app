package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("commenter_id")
    @Expose
    private Integer commenterId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("total_reactions")
    @Expose
    private int totalReactions;
    @SerializedName("my_reaction")
    @Expose
    private int myReaction;
    @SerializedName("tags")
    @Expose
    private Object tag;
    @SerializedName("ids")
    @Expose
    private Object ids;
    @SerializedName("attachment")
    @Expose
    private Object attachment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("tagsData")
    @Expose
    private List<TagModel> tagData = null;
    @SerializedName("children")
    @Expose
    private List<ReplyModel> children = null;
    @SerializedName("user")
    @Expose
    private UserModel user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(Integer commenterId) {
        this.commenterId = commenterId;
    }

    public String getComment() {
        return comment  == null ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getIds() {
        return ids;
    }

    public void setIds(Object ids) {
        this.ids = ids;
    }

    public int getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(int totalReactions) {
        this.totalReactions = totalReactions;
    }

    public int getMyReaction() {
        return myReaction;
    }

    public void setMyReaction(int myReaction) {
        this.myReaction = myReaction;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public String getCreatedAt() {
        return createdAt  == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt  == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TagModel> getTagData() {
        return tagData  == null ? new ArrayList<>() : tagData ;
    }

    public void setTagData(List<TagModel> tagData) {
        this.tagData = tagData;
    }

    public List<ReplyModel> getChildren() {
        return children  == null ? new ArrayList<>() : children ;
    }

    public void setChildren(List<ReplyModel> children) {
        this.children = children;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

}

