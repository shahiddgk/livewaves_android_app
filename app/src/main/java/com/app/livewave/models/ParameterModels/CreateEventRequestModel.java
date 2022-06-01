package com.app.livewave.models.ParameterModels;

public class CreateEventRequestModel {

    private String event_id = null;
    private String title = null;
    private String category_id = null;
    private String type = null;
    private String latitude = null;
    private String longitude = null;
    private String limited = null;
    private String starts_at = null;
    private String ends_at = null;
    private String tickets = null;
    private String amount = null;
    private String subcategory_id = null;
    private String attachment = null;
    private String paid = null;
    private String address = null;

    public CreateEventRequestModel(String title, String category_id, String type, String latitude, String longitude, String limited, String starts_at, String ends_at, String tickets, String amount, String subcategory_id, String attachment, String paid, String address) {
        this.title = title;
        this.category_id = category_id;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.limited = limited;
        this.starts_at = starts_at;
        this.ends_at = ends_at;
        this.subcategory_id = subcategory_id;
        this.attachment = attachment;
        this.paid = paid;
        this.address = address;
        this.amount = paid.equals("0") ? null : amount;
        this.tickets = limited.equals("0") ? null : tickets;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getStarts_at() {
        return starts_at;
    }

    public void setStarts_at(String starts_at) {
        this.starts_at = starts_at;
    }

    public String getEnds_at() {
        return ends_at;
    }

    public void setEnds_at(String ends_at) {
        this.ends_at = ends_at;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
