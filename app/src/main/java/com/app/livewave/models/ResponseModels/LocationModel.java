package com.app.livewave.models.ResponseModels;

public class LocationModel {

    String address;
    String city;
    String state;
    String country;
    String postalCode;

    public String getAddress() {
        return address  == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city  == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state  == null ? "" : state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country  == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode  == null ? "" : postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getKnownName() {
        return knownName  == null ? "" : knownName;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }

    public LocationModel(String address, String city, String state, String country, String postalCode, String knownName) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.knownName = knownName;
    }

    String knownName;
}
