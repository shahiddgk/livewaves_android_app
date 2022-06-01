package com.app.livewave.interfaces;

import com.app.livewave.models.ResponseModels.ApiResponse;

import retrofit2.Response;

public interface ApiResponseHandlerWithFailure<T> {
    void onSuccess(Response<ApiResponse<T>> data);

    void onFailure(String failureCause);
}
