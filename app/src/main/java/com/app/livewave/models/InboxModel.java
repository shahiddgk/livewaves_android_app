package com.app.livewave.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class InboxModel implements Serializable {
    public List<MembersInfo> membersInfo;
    public List<Integer> members;
    public int createdBy;
    public String title;
    public String lastMessage;
    public int senderId;
    public long sentAt;
    public String senderName;
    public long createdAt;
    public String id;
    public String lastMessageId;

    public InboxModel() {
    }

    public InboxModel(List<MembersInfo> membersInfo, List<Integer> members, int createdBy, String title, long createdAt, String id , String lastMessageId) {
        this.membersInfo = membersInfo;
        this.members = members;
        this.createdBy = createdBy;
        this.title = title;
        this.createdAt = createdAt;
        this.id = id;
        this.lastMessageId = lastMessageId;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }


}
