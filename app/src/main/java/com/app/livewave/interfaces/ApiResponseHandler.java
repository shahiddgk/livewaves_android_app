package com.app.livewave.interfaces;

import com.app.livewave.models.ResponseModels.ApiResponse;

import retrofit2.Response;

public interface ApiResponseHandler<T> {
    void onSuccess(Response<ApiResponse<T>> data);
}
