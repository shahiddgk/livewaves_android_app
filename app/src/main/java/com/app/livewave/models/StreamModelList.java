package com.app.livewave.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StreamModelList {

    @SerializedName("data")
    @Expose
    private List<StreamModel> data = null;

    public List<StreamModel> getData() {
        return data;
    }

    public void setData(List<StreamModel> data) {
        this.data = data;
    }

}
