package com.app.livewave.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.app.livewave.R;
import com.app.livewave.activities.LoginActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.ErrorResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {
    public static <T> void apiCall(Call<ApiResponse<T>> call, Context context, ApiResponseHandler<T> handler) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        handler.onSuccess(response);
                    } else if (response.body().getStatus() == 403) {
//                        BaseUtils.showToast(context, "Login Again!");
                        BaseUtils.showLottieDialog(context, "Login Again!", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {
                                context.startActivity(new Intent(context, LoginActivity.class));
                            }
                        });

                    } else if (response.body().getStatus() != null) {
//                        BaseUtils.showToast(context, response.body().getMessage());
                    } else {
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = new ErrorResponse();
                        try {
                            if (response.errorBody() != null) {
                                errorResponse = gson.fromJson(response.errorBody().charStream(), ErrorResponse.class);
                            } else {
                                errorResponse = new ErrorResponse();
                            }
                        } catch (Exception e) {

                        }
//                        BaseUtils.showToast(context, errorResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
//                BaseUtils.showToast(context, t.getLocalizedMessage());
                BaseUtils.showLottieDialog(context, t.getMessage(), R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
            }
        });
    }

    public static <T> void apiCallForCancelFailure(Call<ApiResponse<T>> call) {
        System.out.println("API CALL CANCELLED");
        call.cancel();
    }

    public static <T> void apiCallWithFailure(Call<ApiResponse<T>> call, Context context, ApiResponseHandlerWithFailure<T> handler) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200 || response.body().getStatus() == 400) {
                        handler.onSuccess(response);
                    } else if (response.body().getStatus() == 403) {
//                        BaseUtils.showToast(context, "Login Again!");
                        BaseUtils.showLottieDialog(context, "Login Again!", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {
                                context.startActivity(new Intent(context, LoginActivity.class));
                            }
                        });
                    } else if (response.body().getStatus() != null) {
//                        BaseUtils.showToast(context, response.body().getMessage());
                        handler.onFailure(response.body().getMessage());
                    } else {
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = new ErrorResponse();
                        try {
                            if (response.errorBody() != null) {
                                errorResponse = gson.fromJson(response.errorBody().charStream(), ErrorResponse.class);
                            } else {
                                errorResponse = new ErrorResponse();
                            }
                        } catch (Exception e) {

                        }
                        handler.onFailure(response.body().getMessage());
//                        BaseUtils.showToast(context, errorResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
//                BaseUtils.showToast(context, t.getLocalizedMessage());
                handler.onFailure(t.getMessage());

            }
        });
    }

}
