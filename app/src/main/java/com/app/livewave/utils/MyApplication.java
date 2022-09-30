package com.app.livewave.utils;

import android.app.Application;
import android.content.Context;

import io.paperdb.Paper;


public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        Paper.init(this);
        Paper.book().write("newlyLaunched",true);
//        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
//                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
//                .setResizeAndRotateEnabledForNetwork(true)
//                .setDownsampleEnabled(true)
//                .build();
//        Fresco.initialize(this, config);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
