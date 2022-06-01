package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingsApiModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("app_title")
    @Expose
    private String appTitle;
    @SerializedName("logo")
    @Expose
    private Object logo;
    @SerializedName("icon")
    @Expose
    private Object icon;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("fb_url")
    @Expose
    private Object fbUrl;
    @SerializedName("twitter_url")
    @Expose
    private Object twitterUrl;
    @SerializedName("transaction_share")
    @Expose
    private String transactionShare;
    @SerializedName("androidApiURL")
    @Expose
    private String androidApiURL;
    @SerializedName("iosApiURL")
    @Expose
    private String iosApiURL;
    @SerializedName("androidVersionLive")
    @Expose
    private String androidVersionLive;
    @SerializedName("iosVersionLive")
    @Expose
    private String iosVersionLive;
    @SerializedName("androidVersionTest")
    @Expose
    private String androidVersionTest;
    @SerializedName("iosVersionTest")
    @Expose
    private String iosVersionTest;
    @SerializedName("androidVersionReview")
    @Expose
    private String androidVersionReview;
    @SerializedName("iosVersionReview")
    @Expose
    private String iosVersionReview;
    @SerializedName("androidForceUpdate")
    @Expose
    private String androidForceUpdate;
    @SerializedName("iosForceUpdate")
    @Expose
    private String iosForceUpdate;
    @SerializedName("contact_us")
    @Expose
    private String contactUs;
    @SerializedName("about")
    @Expose
    private Object about;
    @SerializedName("static_tax")
    @Expose
    private Integer staticTax;
    @SerializedName("tax_1")
    @Expose
    private Integer tax1;
    @SerializedName("tax_2")
    @Expose
    private Integer tax2;
    @SerializedName("ppv_tax")
    @Expose
    private String ppvTax;
    @SerializedName("posts_tax")
    @Expose
    private String postsTax;
    @SerializedName("ticket_tax")
    @Expose
    private String ticketTax;
    @SerializedName("tips_tax")
    @Expose
    private String tipsTax;
    @SerializedName("guides")
    @Expose
    private String guides;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public Object getLogo() {
        return logo;
    }

    public void setLogo(Object logo) {
        this.logo = logo;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Object getFbUrl() {
        return fbUrl;
    }

    public void setFbUrl(Object fbUrl) {
        this.fbUrl = fbUrl;
    }

    public Object getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(Object twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getTransactionShare() {
        return transactionShare;
    }

    public void setTransactionShare(String transactionShare) {
        this.transactionShare = transactionShare;
    }

    public String getAndroidApiURL() {
        return androidApiURL;
    }

    public void setAndroidApiURL(String androidApiURL) {
        this.androidApiURL = androidApiURL;
    }

    public String getIosApiURL() {
        return iosApiURL;
    }

    public void setIosApiURL(String iosApiURL) {
        this.iosApiURL = iosApiURL;
    }

    public String getAndroidVersionLive() {
        return androidVersionLive;
    }

    public void setAndroidVersionLive(String androidVersionLive) {
        this.androidVersionLive = androidVersionLive;
    }

    public String getIosVersionLive() {
        return iosVersionLive;
    }

    public void setIosVersionLive(String iosVersionLive) {
        this.iosVersionLive = iosVersionLive;
    }

    public String getAndroidVersionTest() {
        return androidVersionTest;
    }

    public void setAndroidVersionTest(String androidVersionTest) {
        this.androidVersionTest = androidVersionTest;
    }

    public String getIosVersionTest() {
        return iosVersionTest;
    }

    public void setIosVersionTest(String iosVersionTest) {
        this.iosVersionTest = iosVersionTest;
    }

    public String getAndroidVersionReview() {
        return androidVersionReview;
    }

    public void setAndroidVersionReview(String androidVersionReview) {
        this.androidVersionReview = androidVersionReview;
    }

    public String getIosVersionReview() {
        return iosVersionReview;
    }

    public void setIosVersionReview(String iosVersionReview) {
        this.iosVersionReview = iosVersionReview;
    }

    public String getAndroidForceUpdate() {
        return androidForceUpdate;
    }

    public void setAndroidForceUpdate(String androidForceUpdate) {
        this.androidForceUpdate = androidForceUpdate;
    }

    public String getIosForceUpdate() {
        return iosForceUpdate;
    }

    public void setIosForceUpdate(String iosForceUpdate) {
        this.iosForceUpdate = iosForceUpdate;
    }

    public String getContactUs() {
        return contactUs;
    }

    public void setContactUs(String contactUs) {
        this.contactUs = contactUs;
    }

    public Object getAbout() {
        return about;
    }

    public void setAbout(Object about) {
        this.about = about;
    }

    public Integer getStaticTax() {
        return staticTax;
    }

    public void setStaticTax(Integer staticTax) {
        this.staticTax = staticTax;
    }

    public Integer getTax1() {
        return tax1;
    }

    public void setTax1(Integer tax1) {
        this.tax1 = tax1;
    }

    public Integer getTax2() {
        return tax2;
    }

    public void setTax2(Integer tax2) {
        this.tax2 = tax2;
    }

    public String getPpvTax() {
        return ppvTax;
    }

    public void setPpvTax(String ppvTax) {
        this.ppvTax = ppvTax;
    }

    public String getPostsTax() {
        return postsTax;
    }

    public void setPostsTax(String postsTax) {
        this.postsTax = postsTax;
    }

    public String getTicketTax() {
        return ticketTax;
    }

    public void setTicketTax(String ticketTax) {
        this.ticketTax = ticketTax;
    }

    public String getTipsTax() {
        return tipsTax;
    }

    public void setTipsTax(String tipsTax) {
        this.tipsTax = tipsTax;
    }

    public String getGuides() {
        return guides;
    }

    public void setGuides(String guides) {
        this.guides = guides;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
