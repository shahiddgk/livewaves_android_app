package com.app.livewave.activities;

import static com.app.livewave.utils.Constants.EVENT_ID;
import static com.app.livewave.utils.Constants.EVENT_OBJ;
import static com.app.livewave.utils.Constants.FCM_TOKEN;
import static com.app.livewave.utils.Constants.HAS_EXTRA;
import static com.app.livewave.utils.Constants.POST_ID;
import static com.app.livewave.utils.Constants.SPECIFIC_POST_ID;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;
import static com.app.livewave.utils.Constants.URL;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.currentUser;
import static com.app.livewave.utils.Constants.screenHeight;
import static com.app.livewave.utils.Constants.screenWidth;
import static com.app.livewave.utils.Constants.token;

import android.Manifest;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ParameterModels.StreamInfoModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.SettingsApiModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Double lat, lng;
    private Bundle extras;
    private FirebaseAuth mAuth;
    private Uri sharedLink;
    private boolean deepLink;
    private Location myLocation;
    public UserModel userModel;
    private int haveLocation;
    private String AppVersion;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    JoinStreamModel joinStreamModel;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(SplashActivity.this);
        getFcmToken();
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        getCurrentLocation();
        checkIntentData();
        getSettings();

        getScreenWidthAndHeight();
        UpdateApp();
    }

    private void getScreenWidthAndHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        screenHeight = outMetrics.heightPixels;
        screenWidth = outMetrics.widthPixels;
    }

    private void getFcmToken() {
        FirebaseApp.initializeApp(SplashActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            Paper.book().write(FCM_TOKEN, instanceIdResult.getToken());
        });
    }

    private void getCurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int locationRequestCode = 1000;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20 * 1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d("!@#", " i am here");
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                            getLocation(location);
                        }
                    }
                }
            };
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

    }


    private void getLocation(Location location) {
        try {
            myLocation = location;
            if (myLocation != null) {
                lat = myLocation.getLatitude();
                lng = myLocation.getLongitude();


                if (userModel != null) {
                    userModel.setLatitude(lat);
                    userModel.setLongitude(lng);
                    Paper.book().write(currentUser, userModel);
                }

                if (!lat.isNaN()
                        && !lng.isNaN() && haveLocation < 1) {
                    haveLocation++;
                }
            }
        } catch (Exception e) {
            Log.d("TAG", "onLocationChanged: " + e.getMessage());

        }

    }

    private void checkIntentData() {
        int flags = getIntent().getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {

            if (extras != null) {
                extras.clear();
            }
        }
        if (getIntent().getExtras() != null) {

            extras = getIntent().getExtras();

            if (extras != null) {
                if (getIntent() != null && getIntent().getAction() != null) {
                    if (getIntent().getAction().equals("android.intent.action.VIEW")) {
                        sharedLink = getIntent().getData();
                        deepLink = true;
                    } else if (getIntent().getAction().equals("android.intent.action.SEND")) {
                        if (getIntent().getType().startsWith("image/")) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(getIntent().getClipData().getItemAt(0).getUri()
                                    , filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();
//                            Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                            //TODO handle shared image
//                            Toast.makeText(SplashActivity.this, picturePath, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                }

            }

        }


        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d("keys", key + " is a key in the bundle");
                Log.d("value", extras.getString(key) + " is a value in the bundle");
            }
        }

    }

    private void getSettings() {
        ApiManager.apiCall(ApiClient.getSettingInstance().getInterface().getSettings(), this, new ApiResponseHandler<SettingsApiModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<SettingsApiModel>> data) {
                SettingsApiModel settingsModel = data.body().getData();
                launchApp(settingsModel);
            }
        });
    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 4));
    }

    private void launchApp(SettingsApiModel settingsModel) {
        try {
            PackageInfo info = SplashActivity.this.getPackageManager().getPackageInfo(SplashActivity.this.getPackageName(), PackageManager.GET_ACTIVITIES);
            AppVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (settingsModel.getAndroidApiURL().endsWith("api/")) {
            Constants.BASE_URL = removeLastChar(settingsModel.getAndroidApiURL());
            Constants.BASE_APIS_URL = settingsModel.getAndroidApiURL();
        } else {
            Constants.BASE_URL = settingsModel.getAndroidApiURL() + "/";
            Constants.BASE_APIS_URL = settingsModel.getAndroidApiURL() + "/api/";
        }

        if (AppVersion.equals(settingsModel.getAndroidVersionTest())) {
            Constants.APPENV = ENV.development;
            Constants.BASE_URL = "https://livewavesapp.com/";
            Constants.BASE_APIS_URL = "https://livewavesapp.com/api/";
            Constants.FirestoreBaseDir = "LivewavesTest/";
            Constants.firebaseDatabaseRoot = "TestConversation";
        } else if (AppVersion.equals(settingsModel.getAndroidVersionReview())) {
            Constants.APPENV = ENV.review;
        } else {
            Constants.APPENV = ENV.production;
        }

        if (Constants.APPENV == ENV.production && settingsModel.getAndroidForceUpdate().equals("1")
                && !settingsModel.getAndroidVersionLive().equals(AppVersion)) {
            BaseUtils.showAlertDialog(getString(R.string.update), getString(R.string.new_version_of_app_is_available), this, new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {
                    if (positive) {
                        final String appPackageName = getPackageName(); // Can also use getPackageName(), as below
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        return;
                    }
                    finish();
                }
            });
        } else {
            if (Paper.book().read(Constants.isLogin, false)) {
                userModel = Paper.book().read(Constants.currentUser);
                redirectToActivity();
//                if (Paper.book().read(Constants.forceLogout, false)) {
//                    userModel = Paper.book().read(Constants.currentUser);
//                    redirectToActivity();
//                } else {
//                    Paper.book().write(Constants.forceLogout, true);
//                    Paper.book().write(Constants.isLogin, false);
//                    Paper.book().write(currentUser, new UserModel());
//                    finish();
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                }
            } else {
                Paper.book().write(currentUser, new UserModel());
                startActivity(new Intent(SplashActivity.this, LoginActivityWithWavesFeature.class));
            }
        }
    }

    private void redirectToActivity() {

        if (Paper.book().read(Constants.isLogin, false) && mAuth.getCurrentUser() != null) {

            if (extras != null) {
                if (extras.containsKey("type")) {
                    String key = extras.getString("type");
                    if (key.equalsIgnoreCase("follow")) {
                        //Intent intent = new Intent(SplashActivity.this, UserProfileFragment.class);
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra(HAS_EXTRA, R.string.tag_user_profile);
                        intent.putExtra(SPECIFIC_USER_ID, extras.getString("senderID"));

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
//                        IS_COMING_FROM_NOTIFICATION = true;
                        startActivity(intent);
                        finish();
                    } else if (key.equalsIgnoreCase("comment")
                            || key.equalsIgnoreCase("post")
                            || key.equalsIgnoreCase("comment-tag")
                            || key.equalsIgnoreCase("reaction")
                            || key.equalsIgnoreCase("tag")) {

//                        IS_COMING_FROM_NOTIFICATION = true;
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

                        if (extras.getString(SPECIFIC_USER_ID) != null && extras.getString(SPECIFIC_POST_ID) != null) {
                            if (key.equalsIgnoreCase("post") || key.equalsIgnoreCase("tag") || key.equalsIgnoreCase("reaction") || extras.getString(SPECIFIC_USER_ID) != null) {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(HAS_EXTRA, R.string.tag_post_detail);
                                intent.putExtra(USER_ID, Integer.parseInt(extras.getString(SPECIFIC_USER_ID)));
                                intent.putExtra(POST_ID, extras.getString(SPECIFIC_POST_ID));
                            } else if (key.equalsIgnoreCase("comment-tag") || key.equalsIgnoreCase("comment")) {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(HAS_EXTRA, R.string.tag_post_detail);
                                intent.putExtra(USER_ID, Integer.parseInt(extras.getString(SPECIFIC_USER_ID)));
                                intent.putExtra(POST_ID, extras.getString(SPECIFIC_POST_ID));
                            }
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                            stackBuilder.addNextIntentWithParentStack(intent);
                        } else {
                            if (key.equalsIgnoreCase("post") || key.equalsIgnoreCase("tag") || key.equalsIgnoreCase("reaction") || extras.getString(SPECIFIC_USER_ID) != null) {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(HAS_EXTRA, R.string.tag_post_detail);
                                intent.putExtra(USER_ID, Integer.parseInt(extras.getString("senderID")));
                                intent.putExtra(POST_ID, extras.getString("childID"));
                            } else if (key.equalsIgnoreCase("comment-tag") || key.equalsIgnoreCase("comment")) {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(HAS_EXTRA, R.string.tag_post_detail);
                                intent.putExtra(USER_ID, Integer.parseInt(extras.getString("senderID")));
                                intent.putExtra(POST_ID, extras.getString("contentID"));
                            }
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                            stackBuilder.addNextIntentWithParentStack(intent);
                        }
                        startActivity(intent);
                        finish();
                    } else if (key.equalsIgnoreCase("follow")) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra(HAS_EXTRA, R.string.tag_user_profile);
                        intent.putExtra(SPECIFIC_USER_ID, Integer.parseInt(extras.getString("senderID")));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
//                        IS_COMING_FROM_NOTIFICATION = true;
                        startActivity(intent);
                        finish();
                    } else if (key.equalsIgnoreCase("stream")) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra(HAS_EXTRA, R.string.tag_user_profile);
                        intent.putExtra(SPECIFIC_USER_ID, extras.getString("senderID"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
//                        IS_COMING_FROM_NOTIFICATION = true;
                        startActivity(intent);
                        finish();
                    } else if (key.equalsIgnoreCase("event")) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra(HAS_EXTRA, R.string.tag_events_detail);
                        intent.putExtra(EVENT_ID, extras.getString("contentID"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
//                        IS_COMING_FROM_NOTIFICATION = true;
                        startActivity(intent);
                        finish();


                    } else if (key.equalsIgnoreCase("message")) {

                        String url = "https://livewaves.app/api/chat/" + extras.getString("contentID") + "?token=" + Paper.book().read(token);
                        Intent intent = new Intent(SplashActivity.this, WebviewActivity.class);
                        //intent.putExtra(HAS_EXTRA, R.string.tag_webview);
                        intent.putExtra("intent_type", "5");
                        intent.putExtra(URL, url);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
//                        IS_COMING_FROM_NOTIFICATION = true;
                        startActivity(intent);
                        finish();


                    } else if (key.equalsIgnoreCase("stream-invite") || key.equalsIgnoreCase("event-invite")) {
                        // TODO: 4/21/2021 This Code is comented in firebaseFCMservice search for stream_invite
                        String streamId = extras.getString("stream_id");
                        if (streamId == null)
                            streamId = extras.getString("contentID");
                        String type;
                        if (key.equalsIgnoreCase("stream-invite")) {
                            // api/stream/join
                            type = "stream";
                        } else {
                            // api/stream/join?type=event
                            type = "event";
                        }
                        getStreamById(streamId, type);
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }


                } else {

                    if (deepLink) {

                        String[] array = sharedLink.toString().split("\\/", -1);
                        if (array[array.length - 2].equals("post")) {
                            ApiManager.apiCall(ApiClient.getInstance().getInterface().getPostById(array[array.length - 1]), this, new ApiResponseHandler<PostModel>() {
                                @Override
                                public void onSuccess(Response<ApiResponse<PostModel>> data) {
                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    intent.putExtra(HAS_EXTRA, R.string.tag_post_detail);
                                    intent.putExtra(POST_ID, String.valueOf(data.body().getData().getId()));
                                    intent.putExtra(USER_ID, data.body().getData().getProfileId());
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                                    stackBuilder.addNextIntentWithParentStack(intent);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        } else if (array[array.length - 2].equals("event")) {
//                            EVENT_OBJ
                            ApiManager.apiCall(ApiClient.getInstance().getInterface().getEvent(array[array.length - 1]), this, new ApiResponseHandler<EventModel>() {
                                @Override
                                public void onSuccess(Response<ApiResponse<EventModel>> data) {
                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    intent.putExtra(HAS_EXTRA, R.string.tag_events_detail);
                                    intent.putExtra(EVENT_OBJ, data.body().getData());
                                    intent.putExtra(EVENT_ID, data.body().getData().getId().toString());
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                                    stackBuilder.addNextIntentWithParentStack(intent);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {

                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            finish();
                        }


                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }
                }
            } else {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivityWithWavesFeature.class));
            finish();
        }
    }

    private void getStreamById(String streamId, String type) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getStream(streamId), SplashActivity.this, new ApiResponseHandler<StreamModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                String hostPlatformId = data.body().getData().getPlatformID();
                checkFirebase(hostPlatformId, streamId, type);
            }
        });
    }

    private void checkFirebase(String hostPlatformId, String streamId, String type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String fireStoreStreamInfoUrl = "streams/";
        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot != null) {
                    StreamInfoModel streamInfoModel = documentSnapshot.toObject(StreamInfoModel.class);
                    if (streamInfoModel != null) {
                        int guestId = streamInfoModel.getGuestId();
                        if (guestId != userModel.getId()) {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        } else {
                            joinStream(streamId, type);
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    }
                }
            }
        });
    }

    private void joinStream(String streamId, String type) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().joinStream(streamId, type),
                SplashActivity.this, new ApiResponseHandlerWithFailure<JoinStreamModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Response<ApiResponse<JoinStreamModel>> data) {
                        joinStreamModel = data.body().getData();
                        if (ActivityCompat.checkSelfPermission(SplashActivity.this,
                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(SplashActivity.this,
                                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.RECORD_AUDIO},
                                    1);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, SubscriberActivity.class);
                            intent.putExtra("Title", joinStreamModel.getTitle());
                            intent.putExtra("StreamId", joinStreamModel.getStreamId());
                            intent.putExtra("HostPlatformId", joinStreamModel.getParentPlatformID());
                            intent.putExtra("PlatformId", joinStreamModel.getPlatformID());
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                            stackBuilder.addNextIntentWithParentStack(intent);
                            startActivity(intent);
                            finish();
                        }
//                title = data.body().getData().getTitle();
//                id = data.body().getData().getStreamId();
//                hostPlatformId = data.body().getData().getParentPlatformID();
//                platform_id = data.body().getData().getPlatformID();
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        BaseUtils.showLottieDialog(SplashActivity.this, failureCause, R.raw.invalid, positive -> {
                            //appFollowingData(data.body().getData());
                            //wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        BaseUtils.showLottieDialog(SplashActivity.this, "Permission denied", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
//                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            getLocation(location);
                        }
                    });
                } else {
                    BaseUtils.showLottieDialog(SplashActivity.this, "Permission denied", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (joinStreamModel != null) {
                        Intent intent = new Intent(SplashActivity.this, SubscriberActivity.class);
                        intent.putExtra("Title", joinStreamModel.getTitle());
                        intent.putExtra("StreamId", joinStreamModel.getStreamId());
                        intent.putExtra("HostPlatformId", joinStreamModel.getParentPlatformID());
                        intent.putExtra("PlatformId", joinStreamModel.getPlatformID());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashActivity.this);
                        stackBuilder.addNextIntentWithParentStack(intent);
                        startActivity(intent);
                        finish();
                    }
                }
        }
    }

    public void UpdateApp(){

        System.out.println("APP UPDATE");
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(result -> {

            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.updatePriority() >= 4) {
//                requestUpdate(result);
                android.view.ContextThemeWrapper ctw = new android.view.ContextThemeWrapper(this,R.style.Theme_AppCompat);
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctw);
                alertDialogBuilder.setTitle("Update Live Waves");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.drawable.app_launcher);
                alertDialogBuilder.setMessage("Live Waves recommends that you update to the latest version");
                alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id="+getPackageName())));
                        }
                        catch (ActivityNotFoundException e){
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                        }
                    }
                });
                alertDialogBuilder.show();

            } else {

            }
        });
    }

}