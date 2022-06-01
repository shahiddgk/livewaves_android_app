package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MonthlyEarnings {
    @SerializedName("january")
    @Expose
    private Integer january;
    @SerializedName("february")
    @Expose
    private Integer february;
    @SerializedName("march")
    @Expose
    private Integer march;
    @SerializedName("april")
    @Expose
    private Integer april;
    @SerializedName("may")
    @Expose
    private Integer may;
    @SerializedName("june")
    @Expose
    private Integer june;
    @SerializedName("july")
    @Expose
    private Integer july;
    @SerializedName("august")
    @Expose
    private Integer august;
    @SerializedName("september")
    @Expose
    private Integer september;
    @SerializedName("october")
    @Expose
    private Integer october;
    @SerializedName("november")
    @Expose
    private Integer november;
    @SerializedName("december")
    @Expose
    private Integer december;

    public Integer getJanuary() {
        return january;
    }

    public void setJanuary(Integer january) {
        this.january = january;
    }

    public Integer getFebruary() {
        return february;
    }

    public void setFebruary(Integer february) {
        this.february = february;
    }

    public Integer getMarch() {
        return march;
    }

    public void setMarch(Integer march) {
        this.march = march;
    }

    public Integer getApril() {
        return april;
    }

    public void setApril(Integer april) {
        this.april = april;
    }

    public Integer getMay() {
        return may;
    }

    public void setMay(Integer may) {
        this.may = may;
    }

    public Integer getJune() {
        return june;
    }

    public void setJune(Integer june) {
        this.june = june;
    }

    public Integer getJuly() {
        return july;
    }

    public void setJuly(Integer july) {
        this.july = july;
    }

    public Integer getAugust() {
        return august;
    }

    public void setAugust(Integer august) {
        this.august = august;
    }

    public Integer getSeptember() {
        return september;
    }

    public void setSeptember(Integer september) {
        this.september = september;
    }

    public Integer getOctober() {
        return october;
    }

    public void setOctober(Integer october) {
        this.october = october;
    }

    public Integer getNovember() {
        return november;
    }

    public void setNovember(Integer november) {
        this.november = november;
    }

    public Integer getDecember() {
        return december;
    }

    public void setDecember(Integer december) {
        this.december = december;
    }
}