
package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventModel implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("starts_at")
    @Expose
    private String startsAt;
    @SerializedName("ends_at")
    @Expose
    private String endsAt;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("paid")
    @Expose
    private String paid;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("tickets")
    @Expose
    private String tickets;
    @SerializedName("ticketsSold")
    @Expose
    private Integer ticketsSold;
    @SerializedName("platformID")
    @Expose
    private String platformID;
    @SerializedName("subcategory_id")
    @Expose
    private String subcategoryId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("limited")
    @Expose
    private String limited;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("category_title")
    @Expose
    private String categoryTitle;
    @SerializedName("subcategory_title")
    @Expose
    private String subcategoryTitle;
    @SerializedName("ticketID")
    @Expose
    private String ticketID;
    @SerializedName("checked_in")
    @Expose
    private Integer checkedIn;
    @SerializedName("is_paid")
    @Expose
    private Integer isPaid;
    @SerializedName("future")
    @Expose
    private Integer future;
    @SerializedName("sharingID")
    @Expose
    private String sharingID;

    public Integer getId() {
        return id  == null ? 0 : id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId  == null ? 0 : userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId  == null ? 0 : categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title  == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartsAt() {
        return startsAt  == null ? "" : startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getEndsAt() {
        return endsAt  == null ? "" : endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getAttachment() {
        return attachment  == null ? "" : attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getPaid() {
        return paid  == null ? "" : paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTickets() {
        return tickets  == null ? "" : tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public Integer getTicketsSold() {
        return ticketsSold  == null ? 0 : ticketsSold ;
    }

    public void setTicketsSold(Integer ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public String getPlatformID() {
        return platformID  == null ? "" : platformID;
    }

    public void setPlatformID(String platformID) {
        this.platformID = platformID;
    }

    public String getSubcategoryId() {
        return subcategoryId  == null ? "" : subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getLatitude() {
        return latitude  == null ? "" : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude  == null ? "" : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address  == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type  == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status  == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLimited() {
        return limited  == null ? "" : limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getDeleted() {
        return deleted  == null ? "" : deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
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

    public String getName() {
        return name  == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo  == null ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategoryTitle() {
        return categoryTitle  == null ? "" : categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getSubcategoryTitle() {
        return subcategoryTitle  == null ? "" : subcategoryTitle;
    }

    public void setSubcategoryTitle(String subcategoryTitle) {
        this.subcategoryTitle = subcategoryTitle;
    }

    public String getTicketID() {
        return ticketID  == null ? "" : ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Integer getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }

    public Integer getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Integer isPaid) {
        this.isPaid = isPaid;
    }

    public Integer getFuture() {
        return future;
    }

    public void setFuture(Integer future) {
        this.future = future;
    }

    public String getSharingID() {
        return sharingID  == null ? "" : sharingID;
    }

    public void setSharingID(String sharingID) {
        this.sharingID = sharingID;
    }
}
