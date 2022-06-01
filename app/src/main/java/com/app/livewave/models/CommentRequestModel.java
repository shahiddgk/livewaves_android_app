package com.app.livewave.models;

public class CommentRequestModel {

    String comment, tags, ids,comment_id;
    int post_id;
    String attachment;

    public CommentRequestModel() {

    }


    public CommentRequestModel(String comment, int post_id, String tags, String ids,String attachment) {
        this.comment = comment;
        this.post_id = post_id;
        this.tags = tags;
        this.ids = ids;
        this.attachment = attachment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAttachment() {
        return attachment == " " ? null : attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
