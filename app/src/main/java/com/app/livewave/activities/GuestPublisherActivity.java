package com.app.livewave.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.LiveChatAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.Direction;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ParameterModels.StreamChatModel;
import com.app.livewave.models.ParameterModels.StreamInfoModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ZeroGravityAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
//import com.red5pro.streaming.R5Connection;
//import com.red5pro.streaming.R5Stream;
//import com.red5pro.streaming.R5StreamProtocol;
//import com.red5pro.streaming.config.R5Configuration;
//import com.red5pro.streaming.event.R5ConnectionEvent;
//import com.red5pro.streaming.event.R5ConnectionListener;
//import com.red5pro.streaming.source.R5AdaptiveBitrateController;
//import com.red5pro.streaming.source.R5Camera;
//import com.red5pro.streaming.source.R5Microphone;
//import com.red5pro.streaming.view.R5VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.app.livewave.activities.PublisherActivity.setCameraDisplayOrientation;
import static com.app.livewave.utils.Constants.LIKE;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;

public class GuestPublisherActivity extends AppCompatActivity{

//    R5VideoView hostVideoView;
//    RelativeLayout  heart_view;
//    SurfaceView surfaceView;
//    public R5Configuration configuration;
//    protected Camera camera;
//    protected R5Stream stream, hostStream;
//    String platform_id, title, hostPlatformId, react;
//    Integer id, currentCameraId;
//    TextView tv_watchers, tv_title;
//    RecyclerView rv_chat;
//    LiveChatAdapter adapter;
//    FirebaseFirestore db;
//    List<StreamChatModel> streamChatModelList = new ArrayList<>();
//    String fireStoreStreamChatUrl = "chats/streams/";
//    String fireStoreStreamInfoUrl = "streams/";
//    ImageView img_send, img_like;
//    TextInputEditText et_comment;
//    ListenerRegistration firebaseChatListener, firebaseStreamEvent;
//    UserModel userModel;
//    private Long firebase_like;
//    SurfaceHolder holder;
//    int width, height;
//    MaterialCardView cardLive;
//    boolean isPreview = false;
//    boolean isGuest = false;
//    int guestId = 0;
//    String streamId, streamType;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_publisher);
    }
}
//        getIntentData();
//        initViews();
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(
//                    new String[]{android.Manifest.permission.CAMERA,
//                            android.Manifest.permission.RECORD_AUDIO},
//                    1);
//        } else {
//            preview();
//        }
//
//        findViewById(R.id.img_flip).setOnClickListener(v -> rotateCamera());
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        img_send.setOnClickListener(v -> {
//            if (TextUtils.isEmpty(et_comment.getText().toString())) {
//                et_comment.setError("Empty!");
//                et_comment.requestFocus();
//            } else {
//                sendMessageToFirebase(et_comment.getText().toString());
//            }
//
//
//        });
//        img_like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                react = String.valueOf(System.currentTimeMillis());
//                updateSteamInfoValue(LIKE);
//            }
//        });
//        findViewById(R.id.img_back).setOnClickListener(v -> onBackPressed());
//        getChatFromFirebase();
//        et_comment.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().equals("")) {
//                    img_like.setVisibility(View.VISIBLE);
//                    img_send.setVisibility(View.GONE);
//                } else {
//                    img_like.setVisibility(View.GONE);
//                    img_send.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//    }
//
//    private void updateSteamInfoValue(String type) {
//        if (LIKE.equals(type)) {
//            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("animation", System.currentTimeMillis());
//        }
//    }
//
//    private void sendMessageToFirebase(String message) {
//        if (message.replace(" ", "").length() > 256) {
////            BaseUtils.showToast(GuestPublisherActivity.this, "Message is too long to send");
//            BaseUtils.showLottieDialog(GuestPublisherActivity.this, "Message is too long to send!", R.raw.invalid, new DialogBtnClickInterface() {
//                @Override
//                public void onClick(boolean positive) {
//
//                }
//            });
//            return;
//        }
//        StreamChatModel streamChatModel = new StreamChatModel(userModel.getPhoto(), message, userModel.getUsername(), System.currentTimeMillis());
//        db.collection(fireStoreStreamChatUrl + hostPlatformId)
//                .add(streamChatModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                et_comment.setText("");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("!@#", e.getMessage());
////                BaseUtils.showToast(GuestPublisherActivity.this, "Message sending failed");
//                BaseUtils.showLottieDialog(GuestPublisherActivity.this, "Message sending failed!", R.raw.invalid, new DialogBtnClickInterface() {
//                    @Override
//                    public void onClick(boolean positive) {
//
//                    }
//                });
//            }
//        });
//    }
//
//    private void initViews() {
//        Paper.init(this);
//        userModel = Paper.book().read(Constants.currentUser);
//        db = FirebaseFirestore.getInstance();
//        hostVideoView = (R5VideoView) findViewById(R.id.hostStream);
////        customView = findViewById(R.id.customView);
//        surfaceView = findViewById(R.id.surfaceView);
//        tv_watchers = findViewById(R.id.tv_watchers);
//        tv_title = findViewById(R.id.tv_title);
//        heart_view = findViewById(R.id.heart_view);
//        surfaceView.setZOrderMediaOverlay(true);
//
//        configuration = new R5Configuration(R5StreamProtocol.RTSP, "18.218.226.169", 8554, "live", 1.0f);
//        configuration.setLicenseKey("D1V1-X0GJ-YVGR-BPR6");
//        configuration.setBundleID(getPackageName());
//        configuration.setBufferTime(0.5f);
//
//        cardLive = findViewById(R.id.card_live);
//        BaseUtils.blink(cardLive);
//
//        et_comment = findViewById(R.id.et_comment);
//        img_send = findViewById(R.id.img_send);
//        img_like = findViewById(R.id.img_like);
//
//        tv_title.setText(title);
//
//        rv_chat = findViewById(R.id.rv_chat);
//        rv_chat.setHasFixedSize(true);
//        rv_chat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adapter = new LiveChatAdapter(this);
//        rv_chat.setAdapter(adapter);
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        height = displayMetrics.heightPixels;
//        width = displayMetrics.widthPixels;
//    }
//
//    private void getIntentData() {
////        streamId = getIntent().getStringExtra("StreamId");
////        streamType = getIntent().getStringExtra("StreamType");
//        title = getIntent().getStringExtra("Title");
//        id = getIntent().getIntExtra("StreamId", 0);
//        hostPlatformId = getIntent().getStringExtra("HostPlatformId");
//        platform_id = getIntent().getStringExtra("PlatformId");
//    }
//
//    private void preview() {
//        if (currentCameraId == null) {
//            currentCameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
//        }
//        camera = android.hardware.Camera.open(currentCameraId);
//        if (camera != null)
//            setCameraDisplayOrientation(GuestPublisherActivity.this, currentCameraId, camera);
//        holder = surfaceView.getHolder();
//        holder.addCallback(this);
////        getStreamById();
//        isGuest = true;
//        startPublishing();
//
//    }
//
////    private void getStreamById() {
////        ApiManager.apiCall(ApiClient.getInstance().getInterface().getStream(streamId), GuestPublisherActivity.this, new ApiResponseHandler<StreamModel>() {
////            @Override
////            public void onSuccess(Response<ApiResponse<StreamModel>> data) {
////                String hostPlatformId = data.body().getData().getPlatformID();
////                checkFirebase(hostPlatformId);
////            }
////        });
////    }
//
////    private void checkFirebase(String hostPlatformId) {
////        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////            @Override
////            public void onSuccess(DocumentSnapshot documentSnapshot) {
////
////                if (documentSnapshot != null) {
////                    StreamInfoModel streamInfoModel = documentSnapshot.toObject(StreamInfoModel.class);
////                    if (streamInfoModel != null) {
////                        guestId = streamInfoModel.getGuestId();
////                        if (guestId != userModel.getId()) {
////                            onBackPressed();
////                        } else {
////                            isGuest = true;
////                            joinStream();
////                        }
////                    } else {
////                        onBackPressed();
////                    }
////                }
////            }
////        });
////    }
//
////    private void joinStream() {
////        ApiManager.apiCall(ApiClient.getInstance().getInterface().joinStream(streamId, streamType), GuestPublisherActivity.this, new ApiResponseHandler<JoinStreamModel>() {
////            @Override
////            public void onSuccess(Response<ApiResponse<JoinStreamModel>> data) {
////                title = data.body().getData().getTitle();
////                id = data.body().getData().getStreamId();
////                hostPlatformId = data.body().getData().getParentPlatformID();
////                platform_id = data.body().getData().getPlatformID();
////                startPublishing();
////            }
////        });
////    }
//
//    private void startPublishing() {
//        if (camera != null) {
//            stream = new R5Stream(new R5Connection(configuration));
//            stream.setView(surfaceView);
//            R5Camera r5Camera = new R5Camera(camera, 854, 480);
//            r5Camera.setBitrate(1000);
//            r5Camera.setFramerate(25);
//            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                r5Camera.setOrientation(90);
//            else
//                r5Camera.setOrientation(270);
//            R5Microphone r5Microphone = new R5Microphone();
//
////            R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
////            adaptor.AttachStream(stream);
//
//            stream.attachCamera(r5Camera);
//            stream.attachMic(r5Microphone);
//            stream.publish(platform_id, R5Stream.RecordType.Live);
//            stream.setListener(new R5ConnectionListener() {
//                @Override
//                public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
//                    if (r5ConnectionEvent == R5ConnectionEvent.START_STREAMING) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                BaseUtils.setFullScreen(GuestPublisherActivity.this, customView, width, height);
////                                ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
////                                params.width = width;
////                                params.height = height;
////                                surfaceView.setLayoutParams(params);
//                                db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("platformId", platform_id);
//                                subscribeStream(hostPlatformId);
//                            }
//                        });
//
//                    } else if (r5ConnectionEvent == R5ConnectionEvent.VIDEO_RENDER_START) {
//                    } else if (r5ConnectionEvent == R5ConnectionEvent.CLOSE) {
//                    } else if (r5ConnectionEvent == R5ConnectionEvent.NET_STATUS) {
//                    }
//                    Log.e("!@#$3", r5ConnectionEvent.message + " ");
//                    Log.e("!@#$3", r5ConnectionEvent.name() + " ");
//                    Log.e("!@#$3", r5ConnectionEvent.value() + " ");
//                }
//            });
//        }
//    }
//
//    private void subscribeStream(String id) {
//        hostVideoView.setVisibility(View.VISIBLE);
//        hostStream = new R5Stream(new R5Connection(configuration));
//        R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
//        adaptor.AttachStream(hostStream);
//        hostVideoView.attachStream(hostStream);
//        hostStream.play(id);
//        hostStream.setListener(new R5ConnectionListener() {
//            @Override
//            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
//                if (r5ConnectionEvent == R5ConnectionEvent.VIDEO_RENDER_START) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
////                            BaseUtils.setWidthAndHeight(customView, width, height);
////                            ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
////                            params.width = (width / 100) * 40;
////                            params.height = (height / 100) * 30;
////                            surfaceView.setLayoutParams(params);
//                            getStreamInfo();
//                        }
//                    });
//                } else if (r5ConnectionEvent == R5ConnectionEvent.CLOSE) {
//                } else if (r5ConnectionEvent == R5ConnectionEvent.NET_STATUS) {
//                }
//                Log.e("Guest", r5ConnectionEvent.message);
//                Log.e("Guest", r5ConnectionEvent.name());
//                Log.e("Guest", r5ConnectionEvent.value() + "");
//            }
//        });
//    }
//
//    private void getStreamInfo() {
//        DocumentReference documentReference = db.collection(fireStoreStreamInfoUrl).document(hostPlatformId);
//        firebaseStreamEvent = documentReference.addSnapshotListener(MetadataChanges.EXCLUDE, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.e("!@#", "Listen failed.", error);
//                    return;
//                }
//
//                String source = value != null && value.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";
//                if (value != null && value.exists()) {
//                    StreamInfoModel streamInfoModel = value.toObject(StreamInfoModel.class);
//                    if (streamInfoModel != null) {
//                        if (streamInfoModel.getAnimation() != null) {
//                            if (firebase_like == null) {
//                                firebase_like = streamInfoModel.getAnimation();
//                            } else if (!firebase_like.equals(streamInfoModel.getAnimation())) {
//                                firebase_like = streamInfoModel.getAnimation();
//                                flyEmoji(R.drawable.livewaves_transparent_heart);
//                            }
//                        }
//                        if (streamInfoModel.getViewers() >= 0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv_watchers.setText(String.valueOf(streamInfoModel.getViewers()));
//                                }
//                            });
//
//                        }
//                        if (streamInfoModel.getState().toLowerCase().equals("resumed")) {
//                            hostStream.stop();
//                            subscribeStream(hostPlatformId);
//                        }
//                        if (streamInfoModel.getState().toLowerCase().equals("completed")) {
//                            hostStream.stop();
//                            stream.stop();
//                            finish();
//                        }
//                        if (streamInfoModel.getGuestId() != userModel.getId()) {
//                            if (guestId == 0) {
//                                onBackPressed();
//                            }
//                        }
//                        guestId = 0;
//                    }
//                } else {
//                    Log.e("!@#", source + " data: null");
//                }
//            }
//        });
//    }
//
//    private void flyEmoji(final int like_selected) {
//        Handler mHandler = new Handler();
//        Runnable runnable = new Runnable() {
//            public void run() {
//                ZeroGravityAnimation animation = new ZeroGravityAnimation();
//                animation.setCount(1);
//
//                animation.setScalingFactor(0.6f);
//                animation.setOriginationDirection(Direction.BOTTOM);
//                animation.setDestinationDirection(Direction.TOP);
//                animation.setImage(like_selected);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                                                   @Override
//                                                   public void onAnimationStart(Animation animation) {
//                                                   }
//
//                                                   @Override
//                                                   public void onAnimationEnd(Animation animation) {
//
//                                                   }
//
//                                                   @Override
//                                                   public void onAnimationRepeat(Animation animation) {
//
//                                                   }
//                                               }
//                );
//
//                animation.play(GuestPublisherActivity.this, heart_view);
//            }
//        };
//        mHandler.postDelayed(runnable, 1);
//    }
//
//    void getChatFromFirebase() {
//        firebaseChatListener = db.collection(fireStoreStreamChatUrl + hostPlatformId).orderBy("sendAt", ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                List<StreamChatModel> x = value.toObjects(StreamChatModel.class);
//                if (x.size() > 0) {
//                    streamChatModelList.clear();
//                    streamChatModelList.addAll(value.toObjects(StreamChatModel.class));
//                    adapter.setList(streamChatModelList);
//                    rv_chat.smoothScrollToPosition(streamChatModelList.size() - 1);
//                    Log.d("FirebaseChat", "new data is" + streamChatModelList.get(streamChatModelList.size() - 1));
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    preview();
//                }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (firebaseChatListener != null)
//            firebaseChatListener.remove();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (isGuest) {
//            if (hostStream != null)
//                hostStream.stop();
//            if (stream != null)
//                stream.stop();
//            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("platformId", "", "guestId", 0);
//
//        }
//        if (firebaseStreamEvent != null)
//            firebaseStreamEvent.remove();
//        finish();
//
//    }
//
//    @Override
//    public void surfaceCreated(@NonNull SurfaceHolder holder) {
//        if (camera != null) {
//            try {
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//                isPreview = true;
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//
////        Camera.Parameters parameters = camera.getParameters();
////        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
////        Camera.Size size = allSizes.get(0); // get top size
////        for (int i = 0; i < allSizes.size(); i++) {
////            if (allSizes.get(i).width > size.width)
////                size = allSizes.get(i);
////        }
////        parameters.setPreviewSize(size.width, size.height);
////        camera.setParameters(parameters);
////        camera.startPreview();
//    }
//
//    @Override
//    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//    }
//
//    private void rotateCamera() {
//        if (camera != null && stream != null) {
//            R5Camera publishCam = (R5Camera) stream.getVideoSource();
//            if (isPreview) {
//                camera.stopPreview();
//                publishCam.getCamera().stopPreview();
//                camera.release();
//                publishCam.getCamera().release();
//
//            }
//
//            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//            } else {
//                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//            }
//            camera = Camera.open(currentCameraId);
//            setCameraDisplayOrientation(GuestPublisherActivity.this, currentCameraId, camera);
//            try {
//                camera.setPreviewDisplay(surfaceView.getHolder());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            publishCam.setCamera(camera);
//            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
//                publishCam.setOrientation(90);
//            else
//                publishCam.setOrientation(270);
//            camera.startPreview();
//            isPreview = true;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (stream!= null){
////            stream.restrainAudio(true);
////            stream.restrainVideo(true);
////        }
////        if (hostStream!= null){
////            hostStream.restrainVideo(true);
////            hostStream.restrainAudio(true);
////        }
//    }
//}