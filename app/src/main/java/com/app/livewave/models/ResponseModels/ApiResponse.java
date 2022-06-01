package com.app.livewave.models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    private final static long serialVersionUID = -3697397832505963548L;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private T data;

    public ApiResponse() {
    }

    public Integer getStatus() {
        return status  == null ? 0 : status ;
    }

    public String getToken() {
        return token  == null ? "" : token;
    }

    public String getMessage() {
        return message  == null ? "" : message;
    }

    public T getData() {
        return data;
    }
}
