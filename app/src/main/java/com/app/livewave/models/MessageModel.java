package com.app.livewave.models;

import java.util.List;

public class MessageModel {
    public String attachment;
    public String message;
    public int senderId;
    public Long sentAt;
    public int attachmentType;
    public String id;
    public List<Integer> deleteMessage;

    public MessageModel() {
    }

    public MessageModel(String attachment, String message, int senderId, Long sentAt, int attachmentType, String id) {
        this.attachment = attachment;
        this.message = message;
        this.senderId = senderId;
        this.sentAt = sentAt;
        this.attachmentType = attachmentType;
        this.id = id;
    }

    public List<Integer> getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(List<Integer> deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public int getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(int attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
