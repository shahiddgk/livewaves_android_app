package com.app.livewave.retrofit;

import com.app.livewave.utils.Constants;
import com.app.livewave.utils.OkHttpUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static ApiClient INSTANCE = null;
    private static ApiInterface apiInterface = null;

    public static ApiClient getInstance() {

//        if (INSTANCE == null) {
            INSTANCE = new ApiClient();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_APIS_URL+Constants.API_VERSION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpUtils.createHttpClient())
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);
//        }


        return INSTANCE;
    }

    public static ApiClient getSettingInstance() {

//        if (INSTANCE == null) {
        INSTANCE = new ApiClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_APIS_URL+Constants.API_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpUtils.createHttpClient())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
//        }

        return INSTANCE;
    }
    public static Retrofit getClient() {
        return retrofit;
    }

    public ApiInterface getInterface() {
        return apiInterface;
    }
}
