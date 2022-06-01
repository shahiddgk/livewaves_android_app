package com.app.livewave.models;

public class CommentReplyRequestModel {



    String comment, tags, ids,attachmentReply,comment_id;
    int post_id, id,commenter_id;

    public CommentReplyRequestModel() {

    }


    public CommentReplyRequestModel(String comment,int id, int post_id,String comment_id, String tags, String ids,int commenter_id,String attachmentReply) {
        this.comment = comment;
        this.post_id = post_id;
        this.id = id;
        this.tags = tags;
        this.ids = ids;
        this.commenter_id = commenter_id;
        this.attachmentReply = attachmentReply;
        this.comment_id = comment_id;
    }

    public int getParent_id() {
        return id;
    }

    public void setParent_id(int id) {
        this.id = id;
    }

    public String getId() {
        return comment_id;
    }

    public void setId(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getAttachment() {
        return attachmentReply;
    }

    public void setAttachment(String attachmentReply) {
        this.attachmentReply = attachmentReply;
    }

    public int getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(int commenter_id) {
        this.commenter_id = commenter_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
