package com.app.livewave.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.InviteUserDialogSheet;
import com.app.livewave.DialogSheets.SaveStreamDialog;
import com.app.livewave.DialogSheets.SaveStreamListener;
import com.app.livewave.R;
import com.app.livewave.adapters.LiveChatAdapter;
import com.app.livewave.fragments.live.OnSwipeTouchListener;
import com.app.livewave.fragments.live.StreamEndedListener;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.Direction;
import com.app.livewave.models.ParameterModels.AttachmentParams;
import com.app.livewave.models.ParameterModels.CreatePostModel;
import com.app.livewave.models.ParameterModels.OnRefreshPost;
import com.app.livewave.models.ParameterModels.StreamChatModel;
import com.app.livewave.models.ParameterModels.StreamInfoModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.InviteToStreamEvent;
import com.app.livewave.utils.ZeroGravityAnimation;
import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import io.antmedia.webrtcandroidframework.IWebRTCClient;
import io.antmedia.webrtcandroidframework.IWebRTCListener;
import io.antmedia.webrtcandroidframework.StreamInfo;
import io.antmedia.webrtcandroidframework.WebRTCClient;
import io.antmedia.webrtcandroidframework.apprtc.CallActivity;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.LIKE;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_DATA_CHANNEL_ENABLED;

public class PublisherActivity extends AppCompatActivity implements IWebRTCListener {


    private WebRTCClient webRTCClient, webRTCClientGuest;
    private String webRTCMode, webRTCGuestMode;
    private SurfaceViewRenderer cameraViewRenderer, guestRenderer;
    private String tokenId = Constants.tokenId;
    //    private String serverURL = "wss://1timeused.com:/WebRTCAppEE/websocket";
    private String serverURL = Constants.serverURL;
    String hostPlatformId, title, guestPlatformId, react;
    Integer id;
    boolean isStreaming = false;
    boolean isStreamStarted = false;
    boolean isConnected = false;
    boolean isCompleted = false;
    TextView tv_watchers, tv_title;
    RelativeLayout heart_view, rl_paused;
    MaterialCardView cardKick;
    RecyclerView rv_chat;
    LiveChatAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<StreamChatModel> streamChatModelList = new ArrayList<>();
    String fireStoreStreamChatUrl = "chats/streams/";
    String fireStoreStreamInfoUrl = "streams/";
    ImageView img_send, img_like;
    TextInputEditText et_comment;
    ListenerRegistration firebaseChatListener, firebaseStreamEvent;
    UserModel userModel;
    private Long firebase_like;
    ImageView inviteFollowers;
    boolean isEvent;
    int width, height;
    MaterialCardView cardLive;
    LinearLayout ll_guest, ll_chat;
    CreatePostModel postModel;
    ArrayList<AttachmentParams> arrayListAttachments = new ArrayList<>();
    String path;
    String duration;
    StreamEndedListener streamEndedListener;

    public void setStreamEndedListener(StreamEndedListener streamEndedListener) {
        this.streamEndedListener = streamEndedListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);
        getIntentData();
        initViews();
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.RECORD_AUDIO},
                    1);
        }



        findViewById(R.id.img_flip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webRTCClient != null) {
                    webRTCClient.switchCamera();
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        img_send.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_comment.getText().toString())) {
                et_comment.setError("Empty!");
                et_comment.requestFocus();
            } else {
                sendMessageToFirebase(et_comment.getText().toString());
            }

        });
        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                react = String.valueOf(System.currentTimeMillis());
                updateSteamInfoValue(LIKE);
            }
        });
        findViewById(R.id.img_back).setOnClickListener(v -> onBackPressed());
        inviteFollowers.setOnClickListener(v -> {
            InviteUserDialogSheet inviteUserDialogSheet = new InviteUserDialogSheet(id, isEvent);
            FragmentManager fm = getSupportFragmentManager();
            inviteUserDialogSheet.show(fm, "myDialog");

        });
        cardKick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_guest.setVisibility(View.GONE);
                guestRenderer.setVisibility(View.GONE);
                db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("guestId", 0, "platformId", "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardKick.setVisibility(View.GONE);

                        inviteFollowers.setVisibility(View.VISIBLE);
                        if (webRTCClientGuest != null) {
                            if (webRTCClientGuest.isStreaming()) {
                                webRTCClientGuest.stopStream();
                            }
                        }
                    }
                });

            }
        });

        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    img_like.setVisibility(View.VISIBLE);
                    img_send.setVisibility(View.GONE);
                } else {
                    img_like.setVisibility(View.GONE);
                    img_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void getIntentData() {
        title = getIntent().getStringExtra("TITLE");
        hostPlatformId = getIntent().getStringExtra("PLATFORM_ID");
        Log.e("platform_id", "getIntentData: " + hostPlatformId);
        id = getIntent().getIntExtra("ID", 0);
        isEvent = getIntent().getBooleanExtra("Event", false);
        path = "https://poststream.s3.us-east-2.amazonaws.com/streams/" + hostPlatformId + ".mp4";

    }

    private void sendMessageToFirebase(String message) {

        if (message.replace(" ", "").length() > 256) {
            BaseUtils.showLottieDialog(PublisherActivity.this, "Message is too long to send!", R.raw.invalid, new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {

                }
            });
            return;
        }
        StreamChatModel streamChatModel = new StreamChatModel(userModel.getPhoto(), message, userModel.getUsername(), System.currentTimeMillis());
        db.collection(fireStoreStreamChatUrl + hostPlatformId)
                .add(streamChatModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        et_comment.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("!@#", e.getMessage());
                        BaseUtils.showLottieDialog(PublisherActivity.this, "Message sending failed!", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
                    }
                });
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(PublisherActivity.this, "Resumed", Toast.LENGTH_SHORT).show();

        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Resumed").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Started");
                webRTCClient.enableVideo();
                webRTCClient.enableAudio();
                if (webRTCClientGuest != null) {
                    if (webRTCClientGuest.isStreaming()) {
                        webRTCClientGuest.enableVideo();
                        webRTCClientGuest.enableAudio();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(PublisherActivity.this, "Paused", Toast.LENGTH_SHORT).show();
        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Paused").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (webRTCClient != null) {
                    if (webRTCClient.isStreaming()) {
                        webRTCClient.disableAudio();
                        webRTCClient.disableVideo();
                    }

                }
                if (webRTCClientGuest != null) {
                    if (webRTCClientGuest.isStreaming()) {
                        webRTCClientGuest.disableVideo();
                        webRTCClientGuest.disableAudio();
                    }

                }
            }
        });

    }

    private void addStreamInfo(StreamInfoModel streamInfoModel) {
        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).set(streamInfoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                BaseUtils.showLottieDialog(PublisherActivity.this, "Message sending failed!", R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            }
        });
    }

    private void startStream() {
        if (id != null) {
            ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().startStream(id), this, new ApiResponseHandlerWithFailure<StreamModel>() {
                @Override
                public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                    isStreaming = true;
                }

                @Override
                public void onFailure(String failureCause) {
                    db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Resumed").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Started");
                        }
                    });
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        Paper.init(this);
        userModel = Paper.book().read(Constants.currentUser);
        db = FirebaseFirestore.getInstance();
        inviteFollowers = findViewById(R.id.inviteFollowers);
        cardKick = findViewById(R.id.cardKick);

        cameraViewRenderer = findViewById(R.id.camera_view_renderer);
        ll_guest = findViewById(R.id.ll_guest);
        guestRenderer = findViewById(R.id.guest_renderer);

        tv_watchers = findViewById(R.id.tv_watchers);
        tv_title = findViewById(R.id.tv_title);
        heart_view = findViewById(R.id.heart_view);
        rl_paused = findViewById(R.id.rl_paused);
        cardLive = findViewById(R.id.card_live);
        BaseUtils.blink(cardLive);

        et_comment = findViewById(R.id.et_comment);
        img_send = findViewById(R.id.img_send);
        img_like = findViewById(R.id.img_like);
        rv_chat = findViewById(R.id.rv_chat);
        rv_chat.setHasFixedSize(true);
        rv_chat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LiveChatAdapter(this);
        rv_chat.setAdapter(adapter);

        ll_chat = findViewById(R.id.ll_chat_main);

        ll_chat.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                Toast.makeText(PublisherActivity.this, "Left", Toast.LENGTH_SHORT).show();
                rv_chat.animate().translationX(-700).setDuration(600);
            }

            @Override
            public void onSwipeRight() {
                Toast.makeText(PublisherActivity.this, "Right", Toast.LENGTH_SHORT).show();
                rv_chat.animate().translationX(0).setDuration(600);

            }
        });


        tv_title.setText(title);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

        startAntMedia();
        getChatFromFirebase();
        getStreamInfo();
    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rl_paused.setVisibility(View.GONE);
                }
            });
            if (isStreamStarted) {
                isConnected = true;
                if (webRTCClient != null) {
                    if (!webRTCClient.isStreaming()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webRTCClient.startVideoSource();
                                webRTCClient.startStream();
                            }
                        });

                    }
                }
                if (webRTCClientGuest != null) {
                    if (!webRTCClientGuest.isStreaming()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webRTCClientGuest.startStream();
                            }
                        });
                    }
                }
//                if (webRTCClient != null){
//                    if (!webRTCClient.isStreaming()){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                webRTCClient.startVideoSource();
//                                webRTCClient.startStream();
//                            }
//                        });
//
//                    }
//                }
//                if (webRTCClientGuest != null){
//                    if (!webRTCClientGuest.isStreaming()){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                webRTCClientGuest.startStream();
//                            }
//                        });
//
//                    }
//                }
            }

            Toast.makeText(PublisherActivity.this, "Available", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rl_paused.setVisibility(View.VISIBLE);
                    isConnected = false;
//                    db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Paused").addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            if (webRTCClient != null) {
//                                if (webRTCClient.isStreaming()) {
//                                    webRTCClient.disableAudio();
//                                    webRTCClient.disableVideo();
//                                }
//
//                            }
//                            if (webRTCClientGuest != null) {
//                                if (webRTCClientGuest.isStreaming()) {
//                                    webRTCClientGuest.disableVideo();
//                                    webRTCClientGuest.disableAudio();
//                                }
//
//                            }
//                        }
//                    });
                }
            });

            if (webRTCClient != null) {
                if (webRTCClient.isStreaming()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webRTCClient.stopVideoSource();
                            webRTCClient.stopStream();
                        }
                    });

                }
            }
            if (webRTCClientGuest != null) {
                if (webRTCClientGuest.isStreaming()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webRTCClientGuest.stopStream();
                        }
                    });
                }
            }
            Toast.makeText(PublisherActivity.this, "Lost", Toast.LENGTH_SHORT).show();
        }
    };

    private void startAntMedia() {
        try {
            webRTCClient = new WebRTCClient(this, this);
            webRTCMode = IWebRTCClient.MODE_PUBLISH;
            this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            this.getIntent().putExtra(EXTRA_DATA_CHANNEL_ENABLED, true);
            initStream();
            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
                if (this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (!webRTCClient.isStreaming()) {
                webRTCClient.startStream();
            }
        } catch (Exception ex) {
            Toast.makeText(PublisherActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initStream() {
        cameraViewRenderer.setMirror(true);
        webRTCClient.setVideoRenderers(null, cameraViewRenderer);
        webRTCClient.init(serverURL, hostPlatformId, webRTCMode, tokenId, this.getIntent());
    }

    private void updateSteamInfoValue(String type) {
        if (LIKE.equals(type)) {
            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("animation", System.currentTimeMillis());
        }
    }

    private void getStreamInfo() {
        firebaseStreamEvent = db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                StreamInfoModel x = (StreamInfoModel) value.toObject(StreamInfoModel.class);
                if (x != null) {
                    if (x.getAnimation() != null) {
                        if (firebase_like == null) {
                            firebase_like = x.getAnimation();
                        } else if (!firebase_like.equals(x.getAnimation())) {
                            firebase_like = x.getAnimation();
                            flyEmoji(R.drawable.livewaves_transparent_heart);
                        }
                    }

                    if (x.getPlatformId() != null && !x.getPlatformId().equals("")) {
                        if (guestPlatformId == null) {
                            guestPlatformId = x.getPlatformId();
                            subscribeStream(guestPlatformId);
                        } else if (!guestPlatformId.equals(x.getPlatformId())) {
                            guestPlatformId = x.getPlatformId();
                            subscribeStream(guestPlatformId);
                        }
                    }
                    if (x.getViewers() >= 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_watchers.setText(String.valueOf(x.getViewers()));
                            }
                        });

                    }
                    if (x.getGuestId() == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                BaseUtils.setFullScreen(PublisherActivity.this, customView, width, height);
//                                ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
//                                params.width = width;
//                                params.height = height;
//                                surfaceView.setLayoutParams(params);
//                                guestVideoView.setVisibility(View.GONE);
                                cardKick.setVisibility(View.GONE);
                                inviteFollowers.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }
            }
        });
    }

    private void subscribeStream(String guestPlatformId) {
        try {
            webRTCClientGuest = new WebRTCClient(this, this);
            webRTCGuestMode = IWebRTCClient.MODE_PLAY;
            this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            this.getIntent().putExtra(EXTRA_DATA_CHANNEL_ENABLED, true);
            guestRenderer.setMirror(true);
            webRTCClientGuest.setVideoRenderers(null, guestRenderer);
            webRTCClientGuest.init(serverURL, guestPlatformId, webRTCGuestMode, tokenId, this.getIntent());
            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
                if (this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (!webRTCClientGuest.isStreaming()) {
                webRTCClientGuest.startStream();
            }
        } catch (Exception ex) {
            Toast.makeText(PublisherActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void flyEmoji(final int like_selected) {
        Handler mHandler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                ZeroGravityAnimation animation = new ZeroGravityAnimation();
                animation.setCount(1);

                animation.setScalingFactor(0.6f);
                animation.setOriginationDirection(Direction.BOTTOM);
                animation.setDestinationDirection(Direction.TOP);
                animation.setImage(like_selected);
                animation.setAnimationListener(new Animation.AnimationListener() {
                                                   @Override
                                                   public void onAnimationStart(Animation animation) {
//                                                       Toast.makeText(LiveStreamActivity.this, "like in Subscriber", Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onAnimationEnd(Animation animation) {

                                                   }

                                                   @Override
                                                   public void onAnimationRepeat(Animation animation) {

                                                   }
                                               }
                );

                animation.play(PublisherActivity.this, heart_view);

            }
        };

        mHandler.postDelayed(runnable, 1);


    }

    void getChatFromFirebase() {
        firebaseChatListener = db.collection(fireStoreStreamChatUrl + hostPlatformId).orderBy("sendAt", ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<StreamChatModel> x = value.toObjects(StreamChatModel.class);
                if (x.size() > 0) {
                    streamChatModelList.clear();
                    streamChatModelList.addAll(value.toObjects(StreamChatModel.class));
                    adapter.setList(streamChatModelList);
                    rv_chat.smoothScrollToPosition(streamChatModelList.size() - 1);
                    Log.d("FirebaseChat", "new data is" + streamChatModelList.get(streamChatModelList.size() - 1));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    preview();
                }
        }
    }

    @Override
    protected void onDestroy() {

        Toast.makeText(PublisherActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
        if (firebaseChatListener != null)
            firebaseChatListener.remove();
        webRTCClient.disableVideo();
        webRTCClient.disableAudio();
        webRTCClient = null;
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (hostPlatformId != null) {
//            BaseUtils.showAlertDialog("Alert!", "Do you want to end this stream?", this, new DialogBtnClickInterface() {
//                @Override
//                public void onClick(boolean positive) {
//                    if (positive) {
//                        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().completeStream(hostPlatformId),
//                                PublisherActivity.this, new ApiResponseHandlerWithFailure<StreamModel>() {
//                            @Override
//                            public void onSuccess(Response<ApiResponse<StreamModel>> data) {
//                                if (webRTCClient.isStreaming()) {
//                                    webRTCClient.stopStream();
//
//                                }
//                                if (webRTCClientGuest != null) {
//                                    webRTCClient.stopStream();
//                                }
//                                isCompleted = true;
//                                if (streamEndedListener != null){
//                                    streamEndedListener.onStreamEnded(true,hostPlatformId,title);
//                                }else {
//                                    Log.e("listener", "onSuccess: "  );
//                                }
//
//                                db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Completed");
//                                finish();
//
//                            }
//
//                            @Override
//                            public void onFailure(String failureCause) {
//                                BaseUtils.showLottieDialog(PublisherActivity.this, failureCause, R.raw.invalid, positive -> {
//                                    //appFollowingData(data.body().getData());
//                                    //wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
//                                });
//                            }
//                        });
//                    }
//
//                }
//            });

            new SaveStreamDialog(new SaveStreamListener() {
                @Override
                public void onYesButtonClickListener(boolean yes) {
                    if (yes) {

                        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface()
                                        .completeStream(hostPlatformId),
                                PublisherActivity.this, new ApiResponseHandlerWithFailure<StreamModel>() {
                                    @Override
                                    public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                                        if (webRTCClient.isStreaming()) {
                                            webRTCClient.stopStream();
                                        }
                                        if (webRTCClientGuest != null) {
                                            webRTCClient.stopStream();
                                        }
                                        isCompleted = true;

                                        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId)
                                                .update("state", "completed");

                                        // duration =  BaseUtils.getVideoDuration(PublisherActivity.this,Uri.parse(path));
                                        String extension = BaseUtils.getMimeType(PublisherActivity.this, Uri.parse(path));
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                                retriever.release();


                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        duration = time;
                                                    }
                                                });
                                            }
                                        });


                                        postModel = new CreatePostModel(title, userModel.getId());
                                        postModel.setExtension(extension);
                                        postModel.setProfile_id(userModel.getId());

                                        postModel.setThumbnail(path);


                                        Log.e("path", "onSuccess: " + path);
                                        arrayListAttachments.add(new AttachmentParams(path, extension
                                                , duration));
                                        postModel.setAttachments(arrayListAttachments);
                                        createPost(postModel);

                                        finish();

                                    }

                                    @Override
                                    public void onFailure(String failureCause) {

                                        Log.e("failure", "onFailure: " + failureCause);

                                    }
                                });


                    }
                }

                @Override
                public void onNoButtonClickListener(boolean no) {
                    Log.e("no", "onYesButtonClickListener: ");
                    if (no) {
                        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface()
                                        .completeStream(hostPlatformId),
                                PublisherActivity.this, new ApiResponseHandlerWithFailure<StreamModel>() {
                                    @Override
                                    public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                                        if (webRTCClient.isStreaming()) {
                                            webRTCClient.stopStream();
                                        }
                                        if (webRTCClientGuest != null) {
                                            webRTCClient.stopStream();
                                        }
                                        isCompleted = true;

                                        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId)
                                                .update("state", "completed");
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String failureCause) {

                                        Log.e("failure", "onFailure: " + failureCause);

                                    }
                                });
                    }


                }
            }).show(getSupportFragmentManager(), "Hello");
        }
    }

    private void createPost(CreatePostModel postModel) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().createPost(postModel), PublisherActivity.this, new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
                Log.e("TAG", "onSuccess: " + data.isSuccessful());
            }

            @Override
            public void onFailure(String failureCause) {
                Log.e("failure reason", "onFailure: " + failureCause);
            }
        });
    }

    void updateGuestId(int guestId) {
        db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("guestId", guestId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InviteToStreamEvent event) {
        updateGuestId(event.getFollowModel().getId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCompleted) {
            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Completed");
        }
        Toast.makeText(PublisherActivity.this, "Stopped", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDisconnected(String streamId) {
        if (streamId.equals(hostPlatformId)) {
            if (webRTCClient != null) {
                webRTCClient.stopStream();
                initStream();
            }
        } else if (streamId.equals(guestPlatformId)) {
            if (webRTCClientGuest != null) {
                webRTCClientGuest.stopStream();
            }
        }
        Log.w(getClass().getSimpleName(), "disconnected");
//        Toast.makeText(PublisherActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPublishFinished(String streamId) {
        Log.w(getClass().getSimpleName(), "onPublishFinished");
//        Toast.makeText(PublisherActivity.this, "Publish finished", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlayFinished(String streamId) {
        if (streamId.equals(guestPlatformId)) {
            ll_guest.setVisibility(View.GONE);
            guestRenderer.setVisibility(View.GONE);
            cardKick.setVisibility(View.GONE);
            inviteFollowers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPublishStarted(String streamId) {
        addStreamInfo(new StreamInfoModel(System.currentTimeMillis(), "", 0, "started", 0));
        if (!isEvent) {
            if (!isStreaming)
                startStream();
        }
        isStreamStarted = true;
        if (isConnected) {
            db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Connected").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.collection(fireStoreStreamInfoUrl).document(hostPlatformId).update("state", "Started");
                }
            });
        }
        Toast.makeText(PublisherActivity.this, "Published", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayStarted(String streamId) {
        if (streamId.equals(guestPlatformId)) {
            ll_guest.setVisibility(View.VISIBLE);
            guestRenderer.setVisibility(View.VISIBLE);
            cardKick.setVisibility(View.VISIBLE);
            inviteFollowers.setVisibility(View.GONE);
        } else {
            ll_guest.setVisibility(View.GONE);
            guestRenderer.setVisibility(View.GONE);
            cardKick.setVisibility(View.GONE);
            inviteFollowers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void noStreamExistsToPlay(String streamId) {
        Log.w(getClass().getSimpleName(), "noStreamExistsToPlay");
//        Toast.makeText(PublisherActivity.this, "No stream exist to play", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String description, String streamId) {
        Toast.makeText(PublisherActivity.this, "Error: " + description, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String streamId) {
        Toast.makeText(PublisherActivity.this, "Signal channel closed with code " + code, Toast.LENGTH_LONG).show();
    }

    @Override
    public void streamIdInUse(String streamId) {
        Log.w(getClass().getSimpleName(), "streamIdInUse");
        Toast.makeText(PublisherActivity.this, "Stream id is already in use.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onIceConnected(String streamId) {

    }

    @Override
    public void onIceDisconnected(String streamId) {

    }

    @Override
    public void onTrackList(String[] tracks) {

    }

    @Override
    public void onBitrateMeasurement(String streamId, int targetBitrate, int videoBitrate, int audioBitrate) {

    }

    @Override
    public void onStreamInfoList(String streamId, ArrayList<StreamInfo> streamInfoList) {

    }
}