package com.app.livewave.models.ParameterModels;

public class CreateStreamModel {
    private String amount,  title, paid, location, category_id;
    private Long longitude,latitude;

    public CreateStreamModel(String amount, String title, String paid, String location, String category_id, Long longitude, Long latitude) {
        this.amount = amount;
        this.title = title;
        this.paid = paid;
        this.location = location;
        this.category_id = category_id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getLongitude() {

        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
