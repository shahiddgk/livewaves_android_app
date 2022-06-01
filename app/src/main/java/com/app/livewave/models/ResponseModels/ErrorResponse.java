package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("Message")
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse() {
        this.message = "Server Error";
    }

    public String getMessage() {
        return message  == null ? "" : message;
    }

}