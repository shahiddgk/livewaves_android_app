package com.app.livewave.models.ParameterModels;

import java.util.ArrayList;

public class CreatePostModel {

    private String paid;
    private String description;
    private ArrayList<AttachmentParams> attachments;
    private String thumbnail;
    private String tags;
    private String ids;
    private String amount;
    private String action;
    private String duration;
    private String extension;

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    private String preview_url;
    private int profile_id, id;


    public CreatePostModel(String description, int profile_id) {
        this.description = description;
        this.profile_id = profile_id;
    }

    public CreatePostModel(String paid, String description, ArrayList<AttachmentParams> attachments, String thumbnail, String tags, String ids, String amount, int id) {
        this.paid = paid;
        this.description = description;
        this.attachments = attachments;
        this.thumbnail = thumbnail;
        this.tags = tags;
        this.ids = ids;
        this.amount = amount;
        this.id = id;
    }

    public CreatePostModel(String description, ArrayList<AttachmentParams> attachments, String thumbnail, String tags, String ids, String amount, int profile_id, String paid) {
        this.description = description;
        this.attachments = attachments;
        this.thumbnail = thumbnail;
        this.tags = tags;
        this.ids = ids;
        this.amount = amount;
        this.profile_id = profile_id;
        this.paid = paid;
    }

    public CreatePostModel() {
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<AttachmentParams> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<AttachmentParams> attachments) {
        this.attachments = attachments;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public String getPaid() {
        return paid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

