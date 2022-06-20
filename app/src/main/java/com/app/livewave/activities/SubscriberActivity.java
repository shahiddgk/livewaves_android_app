package com.app.livewave.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.contentcapture.ContentCaptureSession;
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

import com.app.livewave.BottomDialogSheets.EnterAmountDialogSheet;
import com.app.livewave.R;
import com.app.livewave.adapters.LiveChatAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.Direction;
import com.app.livewave.interfaces.MessageInterface;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ParameterModels.StreamChatModel;
import com.app.livewave.models.ParameterModels.StreamInfoModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ZeroGravityAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFrame;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import io.antmedia.webrtcandroidframework.IWebRTCClient;
import io.antmedia.webrtcandroidframework.IWebRTCListener;
import io.antmedia.webrtcandroidframework.StreamInfo;
import io.antmedia.webrtcandroidframework.WebRTCClient;
import io.antmedia.webrtcandroidframework.apprtc.CallActivity;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.LIKE;
import static com.app.livewave.utils.Constants.VIEWS;
import static com.app.livewave.utils.Constants.screenHeight;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;

public class SubscriberActivity extends AppCompatActivity implements IWebRTCListener {

    SurfaceViewRenderer renderer_2, renderer_1;
    LinearLayout ll_dual,surface_view_render,first_stream_layout;
    private String tokenId = Constants.tokenId;
    private String serverURL = Constants.serverURL;
    private WebRTCClient webRTCClient, webRTCClientSub;
    private String webRTCMode;
    String platform_id, stream_title, stream_id, react, guestPlatformId, myPlatformId;
    int width, height;
    RecyclerView rv_chat;
    LiveChatAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<StreamChatModel> streamChatModelList = new ArrayList<>();
    String fireStoreStreamChatUrl = "chats/streams/";
    String fireStoreStreamInfoUrl = "streams/";
    ImageView img_send, img_like, img_tip;
    TextInputEditText et_comment;
    ListenerRegistration firebaseChatListener, firebaseStreamEvent;
    UserModel userModel;
    RelativeLayout heart_view, rl_paused, rl_disconnect;
    TextView tv_watchers, tv_title, txt_paused;
    private Long firebase_like;
    private KProgressHUD dialog;
    String STREAM_ID_TYPE;
    boolean isSecondStream = false;
    int counter = 0;
    MaterialCardView card_join, card_live;
    MaterialButton btn_reconnect;
    Boolean isStreaming = false;
    Boolean isGuest = false;
    Boolean isJoined = false;
    
    Boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getIntentData();
        initViews();
        initClickListeners();
        tv_title.setText(stream_title);
    }

    private void initClickListeners() {
        findViewById(R.id.img_back).setOnClickListener(v -> onBackPressed());
        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_comment.getText().toString())) {
                    et_comment.setError("Empty!");
                    et_comment.requestFocus();
                } else {
                    sendMessageToFirebase(et_comment.getText().toString());
                }
            }
        });
        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                react = String.valueOf(System.currentTimeMillis());
                updateSteamInfoValue(LIKE);
            }
        });
        img_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTipDialog();
            }
        });
//        img_full_screen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (surface_view_render.getOrientation() == LinearLayout.VERTICAL) {
//
//                    surface_view_render.setOrientation(LinearLayout.HORIZONTAL);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) first_stream_layout.getLayoutParams();
//                    params.gravity = Gravity.CENTER;
//                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//                    first_stream_layout.setLayoutParams(params);
//
//                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ll_dual.getLayoutParams();
//                    params1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params1.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params1.gravity = Gravity.CENTER;
//
//                    ll_dual.setLayoutParams(params1);
//
//                    if(isGuest) {
//                        webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//                    }
//                }else {
//
//                    surface_view_render.setOrientation(LinearLayout.VERTICAL);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) first_stream_layout.getLayoutParams();
//                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params.gravity = Gravity.CENTER;
//
//                    first_stream_layout.setLayoutParams(params);
//
//                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ll_dual.getLayoutParams();
//                    params1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params1.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    params1.gravity = Gravity.CENTER;
//
//                    ll_dual.setLayoutParams(params1);
//
//                    if(isGuest) {
//                        webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
//                    }
//                  //  webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
//                }
//
//            }
//        });
        card_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().joinStream(stream_id, STREAM_ID_TYPE), SubscriberActivity.this, new ApiResponseHandlerWithFailure<JoinStreamModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Response<ApiResponse<JoinStreamModel>> data) {
                        JoinStreamModel joinStreamModel = data.body().getData();
                        if (ActivityCompat.checkSelfPermission(SubscriberActivity.this,
                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(SubscriberActivity.this,
                                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.RECORD_AUDIO},
                                    1);
                        } else {
                            if (webRTCClientSub != null) {
                                if (webRTCClientSub.isStreaming())
                                    webRTCClientSub.stopStream();
                            }
                            webRTCClientSub = null;

                            publishStream(joinStreamModel.getPlatformID());
//                            onBackPressed();
//                            Intent intent = new Intent(SubscriberActivity.this, GuestPublisherActivity.class);
//                            intent.putExtra("Title", joinStreamModel.getTitle());
//                            intent.putExtra("StreamId", joinStreamModel.getStreamId());
//                            intent.putExtra("HostPlatformId", joinStreamModel.getParentPlatformID());
//                            intent.putExtra("PlatformId", joinStreamModel.getPlatformID());
//                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        if (webRTCClientSub != null) {
                            if (webRTCClientSub.isStreaming())
                                webRTCClientSub.stopStream();
                        }
                        webRTCClientSub = null;
                        if (myPlatformId != null) {
                            publishStream(myPlatformId);
                        }
                    }
                });
            }
        });
        btn_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_reconnect.setVisibility(View.GONE);
                if (webRTCClient.isStreaming())
                    webRTCClient.stopStream();
                if (webRTCClientSub != null) {
                    if (webRTCClientSub.isStreaming())
                        webRTCClientSub.stopStream();
                }
                webRTCClient = null;
                webRTCClientSub = null;
                rl_disconnect.setVisibility(View.GONE);
                startAntMediaStream();
            }
        });
    }

    private void getIntentData() {
        if (getIntent().hasExtra("Subscriber")) {
            platform_id = getIntent().getStringExtra("PLATFORM_ID");
            stream_title = getIntent().getStringExtra("TITLE");
            stream_id = getIntent().getStringExtra("ID");
            STREAM_ID_TYPE = getIntent().getStringExtra("STREAM_ID_TYPE");
            isGuest = false;
        } else {
            stream_title = getIntent().getStringExtra("Title");
            platform_id = getIntent().getStringExtra("HostPlatformId");
            myPlatformId = getIntent().getStringExtra("PlatformId");
            isGuest = true;
        }

    }

    private void startAntMediaStream() {
        try {
            webRTCClient = new WebRTCClient(this, this);
            this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            webRTCMode = IWebRTCClient.MODE_PLAY;
            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
                if (this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initStream();
            if (!webRTCClient.isStreaming()) {
                webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                webRTCClient.startStream();

                renderer_1.init(webRTCClient.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                    @Override
                    public void onFirstFrameRendered() {

                    }

                    @Override
                    public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
                        System.out.println("Vedio height Width");
                        System.out.println(videoHeight);
                        System.out.println(videoWidth);
                        System.out.println("Simple height Width");
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        height = displayMetrics.heightPixels;
                        width = displayMetrics.widthPixels;
                        System.out.println(height);
                        System.out.println(width);


                    }
                });
            }
        } catch (Exception ex) {
            Toast.makeText(SubscriberActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initStream() {
        renderer_1.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        System.out.println("STREAM INFO");
        renderer_1.setEnableHardwareScaler(true);

       webRTCClient.setVideoRenderers(null, renderer_1);
        webRTCClient.init(serverURL, platform_id, webRTCMode, tokenId, this.getIntent());

    }

    private void initSecondStream() {

        renderer_2.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        renderer_2.setEnableHardwareScaler(true);
        webRTCClientSub.setVideoRenderers(null, renderer_2);
        webRTCClientSub.init(serverURL, guestPlatformId, webRTCMode, tokenId, this.getIntent());
    }

    private void showTipDialog() {
        EnterAmountDialogSheet enterAmountDialogSheet = new EnterAmountDialogSheet(new MessageInterface() {
            @Override
            public void IAmountEnter(String mAmount) {
                if (mAmount != null) {
                    String amount = mAmount;
                    Intent intent = new Intent(SubscriberActivity.this, WebviewActivity.class);
                    intent.putExtra("id", STREAM_ID_TYPE + "=" + stream_id);
                    intent.putExtra("type", "tip");
                    intent.putExtra("amount", amount);
                    intent.putExtra("intent_type", "7");
                    startActivity(intent);
                } else {

                }
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        enterAmountDialogSheet.show(fm, "amount");
    }

    private void sendMessageToFirebase(String message) {
        if (message.replace(" ", "").length() > 256) {
            BaseUtils.showLottieDialog(SubscriberActivity.this, "Message is too long to send!", R.raw.invalid, new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {

                }
            });
            return;
        }
        StreamChatModel streamChatModel = new StreamChatModel(userModel.getPhoto(), message, userModel.getUsername(), System.currentTimeMillis());
        db.collection(fireStoreStreamChatUrl + platform_id)
                .add(streamChatModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                et_comment.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("!@#", e.getMessage());
                BaseUtils.showLottieDialog(SubscriberActivity.this, "Message sending failed!", R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            }
        });
    }

    private void updateSteamInfoValue(String type) {
        switch (type) {
            case LIKE:
                db.collection(fireStoreStreamInfoUrl).document(platform_id).update("animation", System.currentTimeMillis());
                break;
            case VIEWS:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        db.collection(fireStoreStreamInfoUrl).document(platform_id).update("viewers", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscriberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;

        }
    }

    private void flyEmoji() {
        Handler mHandler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                ZeroGravityAnimation animation = new ZeroGravityAnimation();
                animation.setCount(1);

                animation.setScalingFactor(0.6f);
                animation.setOriginationDirection(Direction.BOTTOM);
                animation.setDestinationDirection(Direction.TOP);
                animation.setImage(R.drawable.livewaves_transparent_heart);
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

                animation.play(SubscriberActivity.this, heart_view);

            }
        };

        mHandler.postDelayed(runnable, 1);
    }

    private void initViews() {
        Paper.init(this);
        dialog = BaseUtils.progressDialog(this);
        dialog.setCancellable(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });

        userModel = Paper.book().read(Constants.currentUser);
        db = FirebaseFirestore.getInstance();
        tv_watchers = findViewById(R.id.tv_watchers);
        card_join = findViewById(R.id.card_join);
        card_live = findViewById(R.id.card_live);
        tv_title = findViewById(R.id.tv_title);
        heart_view = findViewById(R.id.heart_view);
        rl_paused = findViewById(R.id.rl_paused);
        rl_disconnect = findViewById(R.id.rl_disconnect);
        btn_reconnect = findViewById(R.id.btn_reconnect);
        txt_paused = findViewById(R.id.txt_paused);
        et_comment = findViewById(R.id.et_comment);
        img_send = findViewById(R.id.img_send);
        img_like = findViewById(R.id.img_like);
    //    img_full_screen = findViewById(R.id.img_full_screen);
        img_tip = findViewById(R.id.img_tip);
        ll_dual = findViewById(R.id.ll_dual);
        first_stream_layout = findViewById(R.id.first_stream_layout);
        renderer_2 = findViewById(R.id.second_stream);
        renderer_1 = findViewById(R.id.first_stream);
        surface_view_render = findViewById(R.id.surface_view_render);
        BaseUtils.blink(card_live);
        rv_chat = findViewById(R.id.rv_chat);
        rv_chat.setHasFixedSize(true);
        rv_chat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LiveChatAdapter(this);
        rv_chat.setAdapter(adapter);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        getChatFromFirebase();
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
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
        startAntMediaStream();
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
            if (isStreaming) {
                if (webRTCClient != null) {
                    if (!webRTCClient.isStreaming()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                                webRTCClient.startStream();
                            }
                        });

                    }
                }
                if (webRTCClientSub != null) {
                    if (!webRTCClientSub.isStreaming()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                                webRTCClientSub.startStream();
                            }
                        });

                    }
                }
            }

            Toast.makeText(SubscriberActivity.this, "Available", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_paused.setText("No Internet Connection!");
                    rl_paused.setVisibility(View.VISIBLE);
                }
            });
            if (webRTCClient != null) {
                if (webRTCClient.isStreaming()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webRTCClient.stopStream();
                        }
                    });

                }
            }
            if (webRTCClientSub != null) {
                if (webRTCClientSub.isStreaming()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webRTCClientSub.stopStream();
                        }
                    });
                }
            }
            Toast.makeText(SubscriberActivity.this, "Lost", Toast.LENGTH_SHORT).show();
        }
    };

    void getChatFromFirebase() {
        firebaseChatListener = db.collection(fireStoreStreamChatUrl + platform_id).orderBy("sendAt", ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void getStreamInfo() {
        firebaseStreamEvent = db.collection(fireStoreStreamInfoUrl).document(platform_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                StreamInfoModel x = (StreamInfoModel) value.toObject(StreamInfoModel.class);
                if (x != null) {
                    if (x.getAnimation() != null) {
                        if (firebase_like == null) {
                            firebase_like = x.getAnimation();
                        } else if (!firebase_like.equals(x.getAnimation())) {
                            firebase_like = x.getAnimation();
                            flyEmoji();
                        }
                    }
                    if (x.getGuestId() == userModel.getId()) {
                        if (isGuest) {

                            if (!isJoined) {
                                System.out.println("JOINED");
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) first_stream_layout.getLayoutParams();
                                params.weight = 0.5f;

                                first_stream_layout.setLayoutParams(params);

                                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ll_dual.getLayoutParams();
                                params1.weight = 0.5f;

                                ll_dual.setLayoutParams(params1);

                                publishStream(myPlatformId);
                            }
                            card_join.setVisibility(View.GONE);
                            ll_dual.setVisibility(View.VISIBLE);

                            renderer_2.setVisibility(View.VISIBLE);
                        } else {
                            if (!isJoined) {
                                card_join.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        isGuest = false;
                        card_join.setVisibility(View.GONE);
                        ll_dual.setVisibility(View.GONE);
                        renderer_2.setVisibility(View.GONE);
                    }
                    if (x.getPlatformId() != null) {
                        if (!x.getPlatformId().equals("")) {
                            if (guestPlatformId == null) {
                                guestPlatformId = x.getPlatformId();
                            } else if (!guestPlatformId.equals(x.getPlatformId())) {
                                guestPlatformId = x.getPlatformId();
                            }
                            if (x.getGuestId() != userModel.getId())
                                subscribeSecondStream();
                            ll_dual.setVisibility(View.VISIBLE);
                            renderer_2.setVisibility(View.VISIBLE);
                        } else {
//                            isGuest = false;
                            isJoined = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (webRTCClientSub != null) {
                                        if (webRTCClientSub.isStreaming()) {
                                            webRTCClientSub.stopStream();
                                        }
                                    }
                                    webRTCClientSub = null;
                                }
                            });
                            ll_dual.setVisibility(View.GONE);
                            renderer_2.setVisibility(View.GONE);
                            isSecondStream = false;
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
                    if (x.getState().toLowerCase().equals("paused")) {
                        txt_paused.setText("Host has paused the stream!");
                        rl_paused.setVisibility(View.VISIBLE);
                        Toast.makeText(SubscriberActivity.this, "Host Paused!", Toast.LENGTH_SHORT).show();
                    }
                    if (x.getState().toLowerCase().equals("resumed")) {
                        rl_paused.setVisibility(View.GONE);
                        Toast.makeText(SubscriberActivity.this, "Host resumed!", Toast.LENGTH_SHORT).show();
                        webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                        webRTCClient.startStream();
                    }
                    if (x.getState().toLowerCase().equals("connected")) {
                        btn_reconnect.setVisibility(View.VISIBLE);
                        if (webRTCClient != null) {
                            if (webRTCClient.isStreaming()) {
                                webRTCClient.stopStream();
                            }
                        }
                    }
                    if (x.getState().toLowerCase().equals("completed")) {
                        if (webRTCClient != null) {
                            if (webRTCClient.isStreaming()) {
                                webRTCClient.stopStream();
                            }
                        }
                        if (webRTCClientSub != null) {
                            if (webRTCClientSub.isStreaming()) {
                                webRTCClientSub.stopStream();
                            }
                        }
                        finish();
                    }
                    if (value.get("guestId") != null) {
                        int a = Integer.parseInt(value.get("guestId").toString());
                        int b = userModel.getId();
                        Log.d("!@#idsstream", a + " : " + b);
                        if (Integer.parseInt(value.get("guestId").toString()) != userModel.getId()) {
//                            if (firebaseStreamEvent != null) {
//                                firebaseStreamEvent.remove();
//                            }
                        }
                    }
                }
            }
        });
    }

    private void publishStream(String myPlatformId) {
        try {
            this.myPlatformId = myPlatformId;
            isJoined = true;
            webRTCClientSub = new WebRTCClient(this, this);
            this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            webRTCMode = IWebRTCClient.MODE_PUBLISH;
            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
                if (SubscriberActivity.this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SubscriberActivity.this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            webRTCClientSub.setVideoRenderers(null, renderer_2);
            webRTCClientSub.init(serverURL, myPlatformId, webRTCMode, tokenId, this.getIntent());
            if (!webRTCClientSub.isStreaming()) {
                webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                webRTCClientSub.startStream();
            }
        } catch (Exception ex) {
            Toast.makeText(SubscriberActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribeSecondStream() {
        try {
            webRTCClientSub = new WebRTCClient(this, this);
            this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            webRTCMode = IWebRTCClient.MODE_PLAY;
            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
                if (SubscriberActivity.this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SubscriberActivity.this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initSecondStream();
            if (!webRTCClientSub.isStreaming()) {
                webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                webRTCClientSub.startStream();
            }
        } catch (Exception ex) {
            Toast.makeText(SubscriberActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDisconnected(String streamId) {
        if (streamId.equals(platform_id))
            rl_disconnect.setVisibility(View.GONE);
        Log.w(getClass().getSimpleName(), "disconnected");
       // Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
        if (streamId.equals(platform_id)) {
            if (webRTCClient != null) {
                webRTCClient.stopStream();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (webRTCClient!=null &&  !webRTCClient.isStreaming()) {
                            webRTCClient.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                            webRTCClient.startStream();
                        }
                    }
                }, 10000);
            }
        }
        if (guestPlatformId != null) {
            if (streamId.equals(guestPlatformId)) {
                if (webRTCClientSub != null) {

                    webRTCClientSub.stopStream();
//                    initSecondStream();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!webRTCClientSub.isStreaming()) {
                                webRTCClientSub.onVideoScalingSwitch(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                                webRTCClientSub.startStream();
                            }
                        }
                    }, 15000);
                }
            }
        }
    }

    @Override
    public void onPublishStarted(String streamId) {
        Toast.makeText(SubscriberActivity.this, "Publish Started", Toast.LENGTH_SHORT).show();
        card_join.setVisibility(View.GONE);
        db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", myPlatformId);
    }

    @Override
    public void onPublishFinished(String streamId) {
        Toast.makeText(SubscriberActivity.this, "Publish Finished", Toast.LENGTH_SHORT).show();

        if (webRTCClientSub != null) {
            if (webRTCClientSub.isStreaming()) {
                webRTCClientSub.stopStream();
                webRTCClientSub.toggleMic();
            }
        }
        webRTCClientSub = null;
    }

    @Override
    public void onPlayStarted(String streamId) {
        if (counter == 0) {
            counter++;
            updateSteamInfoValue(VIEWS);
            sendMessageToFirebase("Joined");
            isStreaming = true;
            getStreamInfo();
        }
        Log.w(getClass().getSimpleName(), "onPlayStarted");
        Toast.makeText(this, "Play started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlayFinished(String streamId) {
        isStreaming = false;

        Toast.makeText(SubscriberActivity.this, "Play Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noStreamExistsToPlay(String streamId) {
        Log.w(getClass().getSimpleName(), "noStreamExistsToPlay");

     //   Toast.makeText(this, "No stream exist to play", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String description, String streamId) {
        Toast.makeText(this, "Error: " + description, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String streamId) {
        Toast.makeText(this, "Signal channel closed with code " + code, Toast.LENGTH_LONG).show();
    }

    @Override
    public void streamIdInUse(String streamId) {
        Log.w(getClass().getSimpleName(), "streamIdInUse");
        Toast.makeText(this, "Stream id is already in use.", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (webRTCClient != null) {
            if (webRTCClient.isStreaming()) {
                webRTCClient.enableVideo();
                webRTCClient.enableAudio();
            }
        }
        if (webRTCClientSub != null) {
            if (webRTCClientSub.isStreaming()) {
                webRTCClientSub.enableVideo();
                webRTCClientSub.enableAudio();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webRTCClient != null) {
            if (webRTCClient.isStreaming()) {
                webRTCClient.disableVideo();
                webRTCClient.disableAudio();
            }
        }
        if (webRTCClientSub != null) {
            if (webRTCClientSub.isStreaming()) {
                webRTCClientSub.disableAudio();
                webRTCClientSub.disableVideo();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(SubscriberActivity.this, "Stopped", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        if (isGuest || isJoined) {
            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", "", "guestId", 0);
        }
        sendMessageToFirebase("Left");
        if (firebaseChatListener != null)
            firebaseChatListener.remove();
        if (firebaseStreamEvent != null)
            firebaseStreamEvent.remove();
        if (Integer.parseInt(tv_watchers.getText().toString()) > 0)
            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("viewers", FieldValue. increment(-1));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isGuest || isJoined) {
            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", "", "guestId", 0);
        }
        if (webRTCClient != null) {
            if (webRTCClient.isStreaming())
                webRTCClient.stopStream();
        }
        if (webRTCClientSub != null) {
            if (webRTCClientSub.isStreaming())
                webRTCClientSub.stopStream();
        }

        webRTCClient = null;
        webRTCClientSub = null;
    }
}


//package com.app.livewave.activities;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.ConnectivityManager;
//import android.net.Network;
//import android.net.NetworkCapabilities;
//import android.net.NetworkRequest;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.app.livewave.BottomDialogSheets.EnterAmountDialogSheet;
//import com.app.livewave.R;
//import com.app.livewave.adapters.LiveChatAdapter;
//import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
//import com.app.livewave.interfaces.DialogBtnClickInterface;
//import com.app.livewave.interfaces.Direction;
//import com.app.livewave.interfaces.MessageInterface;
//import com.app.livewave.models.JoinStreamModel;
//import com.app.livewave.models.ParameterModels.StreamChatModel;
//import com.app.livewave.models.ParameterModels.StreamInfoModel;
//import com.app.livewave.models.ResponseModels.ApiResponse;
//import com.app.livewave.models.ResponseModels.UserModel;
//import com.app.livewave.retrofit.ApiClient;
//import com.app.livewave.utils.ApiManager;
//import com.app.livewave.utils.BaseUtils;
//import com.app.livewave.utils.Constants;
//import com.app.livewave.utils.ZeroGravityAnimation;
//import com.app.livewave.wavesplayer.playback.PlayerStateListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.card.MaterialCardView;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.ListenerRegistration;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.kaopiz.kprogresshud.KProgressHUD;
//
//import org.webrtc.SurfaceViewRenderer;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.tavendo.autobahn.WebSocket;
//import io.antmedia.webrtcandroidframework.IWebRTCClient;
//import io.antmedia.webrtcandroidframework.IWebRTCListener;
//import io.antmedia.webrtcandroidframework.StreamInfo;
//import io.antmedia.webrtcandroidframework.WebRTCClient;
//import io.antmedia.webrtcandroidframework.apprtc.CallActivity;
//import io.paperdb.Paper;
//import retrofit2.Response;
//
//import static com.app.livewave.utils.Constants.LIKE;
//import static com.app.livewave.utils.Constants.VIEWS;
//import static com.google.firebase.firestore.Query.Direction.ASCENDING;
//import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;
//
//public class SubscriberFragment extends Fragment implements IWebRTCListener, PlayerStateListener {
//
//    SurfaceViewRenderer renderer_2, renderer_1;
//    LinearLayout ll_dual;
//    private String tokenId = Constants.tokenId;
//    private String serverURL = Constants.serverURL;
//    private WebRTCClient webRTCClient, webRTCClientSub;
//    private String webRTCMode;
//    String platform_id, stream_title, stream_id, react, guestPlatformId, myPlatformId;
//    int width, height;
//    RecyclerView rv_chat;
//    LiveChatAdapter adapter;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    List<StreamChatModel> streamChatModelList = new ArrayList<>();
//    String fireStoreStreamChatUrl = "chats/streams/";
//    String fireStoreStreamInfoUrl = "streams/";
//    ImageView img_send, img_like, img_tip;
//    TextInputEditText et_comment;
//    ListenerRegistration firebaseChatListener, firebaseStreamEvent;
//    UserModel userModel;
//    RelativeLayout heart_view, rl_paused, rl_disconnect;
//    TextView tv_watchers, tv_title, txt_paused;
//    private Long firebase_like;
//    private KProgressHUD dialog, dialog1;
//    String STREAM_ID_TYPE;
//    boolean isSecondStream = false;
//    int counter = 0;
//    MaterialCardView card_join, card_live;
//    MaterialButton btn_reconnect;
//    Boolean isStreaming = false;
//    Boolean isGuest = false;
//    Boolean isJoined = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_subscriber, container, false);
//
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getIntentData();
//        initViews(view);
//        initClickListeners(view);
//        tv_title.setText(stream_title);
//
//        return view;
//    }
//
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_subscriber);
////
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
////        getIntentData();
////        initViews();
////        initClickListeners();
////        tv_title.setText(stream_title);
////    }
//
//    private void initClickListeners(View view) {
//        view.findViewById(R.id.img_back).setOnClickListener(v -> getActivity().onBackPressed());
//        img_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(et_comment.getText().toString())) {
//                    et_comment.setError("Empty!");
//                    et_comment.requestFocus();
//                } else {
//                    sendMessageToFirebase(et_comment.getText().toString());
//                }
//            }
//        });
//        img_like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                react = String.valueOf(System.currentTimeMillis());
//                updateSteamInfoValue(LIKE);
//            }
//        });
//        img_tip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showTipDialog();
//            }
//        });
//        card_join.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//                ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().joinStream(stream_id, STREAM_ID_TYPE), getActivity(), new ApiResponseHandlerWithFailure<JoinStreamModel>() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onSuccess(Response<ApiResponse<JoinStreamModel>> data) {
////
//                        ((HomeActivity)getActivity()).hideProgressDialog();
//                        JoinStreamModel joinStreamModel = data.body().getData();
//                        if (ActivityCompat.checkSelfPermission(getActivity(),
//                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                                ActivityCompat.checkSelfPermission(getActivity(),
//                                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                            requestPermissions(
//                                    new String[]{android.Manifest.permission.CAMERA,
//                                            android.Manifest.permission.RECORD_AUDIO},
//                                    1);
//                        } else {
//                            if (webRTCClientSub != null) {
//                                if (webRTCClientSub.isStreaming())
//                                    webRTCClientSub.stopStream();
//                            }
//                            webRTCClientSub = null;
//                            publishStream(joinStreamModel.getPlatformID());
////                            onBackPressed();
////                            Intent intent = new Intent(SubscriberActivity.this, GuestPublisherActivity.class);
////                            intent.putExtra("Title", joinStreamModel.getTitle());
////                            intent.putExtra("StreamId", joinStreamModel.getStreamId());
////                            intent.putExtra("HostPlatformId", joinStreamModel.getParentPlatformID());
////                            intent.putExtra("PlatformId", joinStreamModel.getPlatformID());
////                            startActivity(intent);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String failureCause) {
//
//                        if (webRTCClientSub != null) {
//                            if (webRTCClientSub.isStreaming())
//                                webRTCClientSub.stopStream();
//                        }
//                        webRTCClientSub = null;
//                        if (myPlatformId != null) {
//                            publishStream(myPlatformId);
//                        }
//                    }
//                });
//            }
//        });
//        btn_reconnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_reconnect.setVisibility(View.GONE);
//                if (webRTCClient.isStreaming())
//                    webRTCClient.stopStream();
//                if (webRTCClientSub != null) {
//                    if (webRTCClientSub.isStreaming())
//                        webRTCClientSub.stopStream();
//                }
//                webRTCClient = null;
//                webRTCClientSub = null;
//                rl_disconnect.setVisibility(View.GONE);
//                startAntMediaStream();
//            }
//        });
//    }
//
//    private void getIntentData() {
//        if (getActivity().getIntent().hasExtra("Subscriber")) {
//            platform_id = getActivity().getIntent().getStringExtra("PLATFORM_ID");
//            stream_title = getActivity().getIntent().getStringExtra("TITLE");
//            stream_id = getActivity().getIntent().getStringExtra("ID");
//            STREAM_ID_TYPE = getActivity().getIntent().getStringExtra("STREAM_ID_TYPE");
//            isGuest = false;
//        } else {
//            stream_title = getActivity().getIntent().getStringExtra("Title");
//            platform_id = getActivity().getIntent().getStringExtra("HostPlatformId");
//            myPlatformId = getActivity().getIntent().getStringExtra("PlatformId");
//            isGuest = true;
//        }
//
//    }
//
//    private void startAntMediaStream() {
//        try {
//            webRTCClient = new WebRTCClient(this, getActivity());
//            getActivity().getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//            webRTCMode = IWebRTCClient.MODE_PLAY;
//            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
//                if (getActivity().checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getActivity(), "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            initStream();
//            if (!webRTCClient.isStreaming()) {
//                webRTCClient.startStream();
//            }
//        } catch (Exception ex) {
//            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void initStream() {
//        webRTCClient.setVideoRenderers(null, renderer_1);
//        webRTCClient.init(serverURL, platform_id, webRTCMode, tokenId, getActivity().getIntent());
//    }
//
//    private void initSecondStream() {
//        webRTCClientSub.setVideoRenderers(null, renderer_2);
//        webRTCClientSub.init(serverURL, guestPlatformId, webRTCMode, tokenId, getActivity().getIntent());
//    }
//
//    private void showTipDialog() {
//        EnterAmountDialogSheet enterAmountDialogSheet = new EnterAmountDialogSheet(new MessageInterface() {
//            @Override
//            public void IAmountEnter(String mAmount) {
//                if (mAmount != null) {
//                    String amount = mAmount;
////                    Intent intent = new Intent(SubscriberActivity.this, WebviewFragment.class);
////                    intent.putExtra("id", STREAM_ID_TYPE + "=" + stream_id);
////                    intent.putExtra("type", "tip");
////                    intent.putExtra("amount", amount);
////                    intent.putExtra("intent_type", "7");
////                    startActivity(intent);
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", STREAM_ID_TYPE + "=" + stream_id);
//                    bundle.putString("type", "tip");
//                    bundle.putString("amount", amount);
//                    bundle.putString("intent_type", "7");
//                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_webview, bundle);
//                } else {
//
//                }
//            }
//        });
//        FragmentManager fm = getChildFragmentManager();
//        enterAmountDialogSheet.show(fm, "amount");
//    }
//
//    private void sendMessageToFirebase(String message) {
//        if (message.replace(" ", "").length() > 256) {
//            BaseUtils.showLottieDialog(getActivity(), "Message is too long to send!", R.raw.invalid, new DialogBtnClickInterface() {
//                @Override
//                public void onClick(boolean positive) {
//
//                }
//            });
//            return;
//        }
//        StreamChatModel streamChatModel = new StreamChatModel(userModel.getPhoto(), message, userModel.getUsername(), System.currentTimeMillis());
//        db.collection(fireStoreStreamChatUrl + platform_id)
//                .add(streamChatModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                et_comment.setText("");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("!@#", e.getMessage());
//                BaseUtils.showLottieDialog(getActivity(), "Message sending failed!", R.raw.invalid, new DialogBtnClickInterface() {
//                    @Override
//                    public void onClick(boolean positive) {
//
//                    }
//                });
//            }
//        });
//    }
//
//    private void updateSteamInfoValue(String type) {
//        switch (type) {
//            case LIKE:
//                db.collection(fireStoreStreamInfoUrl).document(platform_id).update("animation", System.currentTimeMillis());
//                break;
//            case VIEWS:
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        db.collection(fireStoreStreamInfoUrl).document(platform_id).update("viewers", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                break;
//
//        }
//    }
//
//    private void flyEmoji() {
//        Handler mHandler = new Handler();
//        Runnable runnable = new Runnable() {
//            public void run() {
//                ZeroGravityAnimation animation = new ZeroGravityAnimation();
//                animation.setCount(1);
//
//                animation.setScalingFactor(0.6f);
//                animation.setOriginationDirection(Direction.BOTTOM);
//                animation.setDestinationDirection(Direction.TOP);
//                animation.setImage(R.drawable.livewaves_transparent_heart);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                                                   @Override
//                                                   public void onAnimationStart(Animation animation) {
////                                                       Toast.makeText(LiveStreamActivity.this, "like in Subscriber", Toast.LENGTH_SHORT).show();
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
//                animation.play(getActivity(), heart_view);
//
//            }
//        };
//
//        mHandler.postDelayed(runnable, 1);
//    }
//
//    private void initViews(View view) {
//        Paper.init(getActivity());
//        dialog = BaseUtils.progressDialog(getActivity());
//        dialog.setCancellable(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                getActivity().onBackPressed();
//            }
//        });
//        dialog1 = BaseUtils.progressDialog(getActivity());
//        dialog1.setCancellable(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                getActivity().onBackPressed();
//            }
//        });
//        userModel = Paper.book().read(Constants.currentUser);
//        db = FirebaseFirestore.getInstance();
//        tv_watchers = view.findViewById(R.id.tv_watchers);
//        card_join = view.findViewById(R.id.card_join);
//        card_live = view.findViewById(R.id.card_live);
//        tv_title = view.findViewById(R.id.tv_title);
//        heart_view = view.findViewById(R.id.heart_view);
//        rl_paused = view.findViewById(R.id.rl_paused);
//        rl_disconnect = view.findViewById(R.id.rl_disconnect);
//        btn_reconnect = view.findViewById(R.id.btn_reconnect);
//        txt_paused = view.findViewById(R.id.txt_paused);
//        et_comment = view.findViewById(R.id.et_comment);
//        img_send = view.findViewById(R.id.img_send);
//        img_like = view.findViewById(R.id.img_like);
//        img_tip = view.findViewById(R.id.img_tip);
//        ll_dual = view.findViewById(R.id.ll_dual);
//        renderer_2 = view.findViewById(R.id.second_stream);
//        renderer_1 = view.findViewById(R.id.first_stream);
//        BaseUtils.blink(card_live);
//        rv_chat = view.findViewById(R.id.rv_chat);
//        rv_chat.setHasFixedSize(true);
//        rv_chat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        adapter = new LiveChatAdapter(getActivity());
//        rv_chat.setAdapter(adapter);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        height = displayMetrics.heightPixels;
//        width = displayMetrics.widthPixels;
//        getChatFromFirebase();
//        et_comment.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
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
//        ConnectivityManager connectivityManager =
//                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            connectivityManager.registerDefaultNetworkCallback(networkCallback);
//        } else {
//            NetworkRequest request = new NetworkRequest.Builder()
//                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
//            connectivityManager.registerNetworkCallback(request, networkCallback);
//        }
//        startAntMediaStream();
//    }
//
//    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//        @Override
//        public void onAvailable(@NonNull Network network) {
//            super.onAvailable(network);
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    rl_paused.setVisibility(View.GONE);
//                }
//            });
//            if (isStreaming) {
//                if (webRTCClient != null) {
//                    if (!webRTCClient.isStreaming()) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                webRTCClient.startStream();
//                            }
//                        });
//
//                    }
//                }
//                if (webRTCClientSub != null) {
//                    if (!webRTCClientSub.isStreaming()) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                webRTCClientSub.startStream();
//                            }
//                        });
//
//                    }
//                }
//            }
//
//            Toast.makeText(getActivity(), "Available", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onLost(@NonNull Network network) {
//            super.onLost(network);
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    txt_paused.setText("No Internet Connection!");
//                    rl_paused.setVisibility(View.VISIBLE);
//                }
//            });
//            if (webRTCClient != null) {
//                if (webRTCClient.isStreaming()) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            webRTCClient.stopStream();
//                        }
//                    });
//
//                }
//            }
//            if (webRTCClientSub != null) {
//                if (webRTCClientSub.isStreaming()) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            webRTCClientSub.stopStream();
//                        }
//                    });
//                }
//            }
//            Toast.makeText(getActivity(), "Lost", Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    void getChatFromFirebase() {
//        firebaseChatListener = db.collection(fireStoreStreamChatUrl + platform_id).orderBy("sendAt", ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
//    private void getStreamInfo() {
//        firebaseStreamEvent = db.collection(fireStoreStreamInfoUrl).document(platform_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                StreamInfoModel x = (StreamInfoModel) value.toObject(StreamInfoModel.class);
//                if (x != null) {
//                    if (x.getAnimation() != null) {
//                        if (firebase_like == null) {
//                            firebase_like = x.getAnimation();
//                        } else if (!firebase_like.equals(x.getAnimation())) {
//                            firebase_like = x.getAnimation();
//                            flyEmoji();
//                        }
//                    }
//                    if (x.getGuestId() == userModel.getId()) {
//                        if (isGuest) {
//                            if (!isJoined) {
//                                publishStream(myPlatformId);
//                            }
//                            card_join.setVisibility(View.GONE);
//                            ll_dual.setVisibility(View.VISIBLE);
//                            renderer_2.setVisibility(View.VISIBLE);
//                        } else {
//                            if (!isJoined) {
//                                card_join.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    } else {
//                        isGuest = false;
//                        card_join.setVisibility(View.GONE);
//                        ll_dual.setVisibility(View.GONE);
//                        renderer_2.setVisibility(View.GONE);
//                    }
//                    if (x.getPlatformId() != null) {
//                        if (!x.getPlatformId().equals("")) {
//                            if (guestPlatformId == null) {
//                                guestPlatformId = x.getPlatformId();
//                            } else if (!guestPlatformId.equals(x.getPlatformId())) {
//                                guestPlatformId = x.getPlatformId();
//                            }
//                            if (x.getGuestId() != userModel.getId())
//                                subscribeSecondStream();
//                            ll_dual.setVisibility(View.VISIBLE);
//                            renderer_2.setVisibility(View.VISIBLE);
//                        } else {
////                            isGuest = false;
//                            isJoined = false;
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (webRTCClientSub != null) {
//                                        if (webRTCClientSub.isStreaming()) {
//                                            webRTCClientSub.stopStream();
//                                        }
//                                    }
//                                    webRTCClientSub = null;
//                                }
//                            });
//                            ll_dual.setVisibility(View.GONE);
//                            renderer_2.setVisibility(View.GONE);
//
//                            isSecondStream = false;
//                        }
//                    }
//                    if (x.getViewers() >= 0) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tv_watchers.setText(String.valueOf(x.getViewers()));
//
//                            }
//                        });
//                    }
//                    if (x.getState().toLowerCase().equals("paused")) {
//                        txt_paused.setText("Host has paused the stream!");
//                        rl_paused.setVisibility(View.VISIBLE);
//                        Toast.makeText(getActivity(), "Host Paused!", Toast.LENGTH_SHORT).show();
//                    }
//                    if (x.getState().toLowerCase().equals("resumed")) {
//                        rl_paused.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "Host resumed!", Toast.LENGTH_SHORT).show();
//                        webRTCClient.startStream();
//                    }
//                    if (x.getState().toLowerCase().equals("connected")) {
//                        btn_reconnect.setVisibility(View.VISIBLE);
//                        if (webRTCClient != null) {
//                            if (webRTCClient.isStreaming()) {
//                                webRTCClient.stopStream();
//                            }
//                        }
//                    }
//                    if (x.getState().toLowerCase().equals("completed")) {
//                        if (webRTCClient != null) {
//                            if (webRTCClient.isStreaming()) {
//                                webRTCClient.stopStream();
//                            }
//                        }
//                        if (webRTCClientSub != null) {
//                            if (webRTCClientSub.isStreaming()) {
//                                webRTCClientSub.stopStream();
//                            }
//                        }
////                        finish();
//                        getActivity().onBackPressed();
//                    }
//
//                    if (value.get("guestId") != null) {
//                        int a = Integer.parseInt(value.get("guestId").toString());
//                        int b = userModel.getId();
//                        Log.d("!@#idsstream", a + " : " + b);
//                        if (Integer.parseInt(value.get("guestId").toString()) != userModel.getId()) {
////                            if (firebaseStreamEvent != null) {
////                                firebaseStreamEvent.remove();
////                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    private void publishStream(String myPlatformId) {
//        try {
//            this.myPlatformId = myPlatformId;
//            isJoined = true;
//            webRTCClientSub = new WebRTCClient(this, getActivity());
//            getActivity().getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//            webRTCMode = IWebRTCClient.MODE_PUBLISH;
//            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
//                if (SubscriberFragment.this.getActivity().checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getActivity(), "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            webRTCClientSub.setVideoRenderers(null, renderer_2);
//            webRTCClientSub.init(serverURL, myPlatformId, webRTCMode, tokenId, getActivity().getIntent());
//            if (!webRTCClientSub.isStreaming()) {
//                webRTCClientSub.startStream();
//            }
//        } catch (Exception ex) {
//            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void subscribeSecondStream() {
//        try {
//            webRTCClientSub = new WebRTCClient(this, getActivity());
//            getActivity().getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//            webRTCMode = IWebRTCClient.MODE_PLAY;
//            for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
//                if (SubscriberFragment.this.getActivity().checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getActivity(), "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            initSecondStream();
//            if (!webRTCClientSub.isStreaming()) {
//                webRTCClientSub.startStream();
//            }
//        } catch (Exception ex) {
//            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    @Override
//    public void onDisconnected(String streamId) {
//        if (streamId.equals(platform_id))
//            rl_disconnect.setVisibility(View.GONE);
//        Log.w(getClass().getSimpleName(), "disconnected");
//        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_LONG).show();
//        if (streamId.equals(platform_id)) {
//            if (webRTCClient != null) {
//                dialog1.show();
//                webRTCClient.stopStream();
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!webRTCClient.isStreaming()) {
//                            webRTCClient.startStream();
//                        }
//                        dialog1.dismiss();
//                    }
//                }, 10000);
//            }
//        }
//        if (guestPlatformId != null) {
//            if (streamId.equals(guestPlatformId)) {
//                if (webRTCClientSub != null) {
//                    dialog1.show();
//                    webRTCClientSub.stopStream();
////                    initSecondStream();
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!webRTCClientSub.isStreaming()) {
//                                webRTCClientSub.startStream();
//                            }
//                            dialog1.dismiss();
//                        }
//                    }, 15000);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onPublishStarted(String streamId) {
//        Toast.makeText(getActivity(), "Publish Started", Toast.LENGTH_SHORT).show();
//        card_join.setVisibility(View.GONE);
//        db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", myPlatformId);
//    }
//
//    @Override
//    public void onPublishFinished(String streamId) {
//        Toast.makeText(getActivity(), "Publish Finished", Toast.LENGTH_SHORT).show();
//        if (webRTCClientSub != null) {
//            if (webRTCClientSub.isStreaming()) {
//                webRTCClientSub.stopStream();
//                webRTCClientSub.toggleMic();
//            }
//        }
//        webRTCClientSub = null;
//    }
//
//    @Override
//    public void onPlayStarted(String streamId) {
//        if (counter == 0) {
//            counter++;
//            updateSteamInfoValue(VIEWS);
//            sendMessageToFirebase("Joined");
//            isStreaming = true;
//            getStreamInfo();
//        }
//        Log.w(getClass().getSimpleName(), "onPlayStarted");
//        Toast.makeText(getActivity(), "Play started", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPlayFinished(String streamId) {
//        isStreaming = false;
//        Toast.makeText(getActivity(), "Play Finished", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void noStreamExistsToPlay(String streamId) {
//        Log.w(getClass().getSimpleName(), "noStreamExistsToPlay");
//        Toast.makeText(getActivity(), "No stream exist to play", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onError(String description, String streamId) {
//        Toast.makeText(getActivity(), "Error: " + description, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String streamId) {
//        Toast.makeText(getActivity(), "Signal channel closed with code " + code, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void streamIdInUse(String streamId) {
//        Log.w(getClass().getSimpleName(), "streamIdInUse");
//        Toast.makeText(getActivity(), "Stream id is already in use.", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onIceConnected(String streamId) {
//    }
//
//    @Override
//    public void onIceDisconnected(String streamId) {
//    }
//
//    @Override
//    public void onTrackList(String[] tracks) {
//
//    }
//
//    @Override
//    public void onBitrateMeasurement(String streamId, int targetBitrate, int videoBitrate, int audioBitrate) {
//
//    }
//
//    @Override
//    public void onStreamInfoList(String streamId, ArrayList<StreamInfo> streamInfoList) {
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (webRTCClient != null) {
//            if (webRTCClient.isStreaming()) {
//                webRTCClient.enableVideo();
//                webRTCClient.enableAudio();
//            }
//        }
//        if (webRTCClientSub != null) {
//            if (webRTCClientSub.isStreaming()) {
//                webRTCClientSub.enableVideo();
//                webRTCClientSub.enableAudio();
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (webRTCClient != null) {
//            if (webRTCClient.isStreaming()) {
//                webRTCClient.disableVideo();
//                webRTCClient.disableAudio();
//            }
//        }
//        if (webRTCClientSub != null) {
//            if (webRTCClientSub.isStreaming()) {
//                webRTCClientSub.disableAudio();
//                webRTCClientSub.disableVideo();
//            }
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Toast.makeText(getActivity(), "Stopped", Toast.LENGTH_SHORT).show();
//    }
//
//
//    @Override
//    public void onDestroy() {
//        if (isGuest || isJoined) {
//            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", "", "guestId", 0);
//        }
//        sendMessageToFirebase("Left");
//        if (firebaseChatListener != null)
//            firebaseChatListener.remove();
//        if (firebaseStreamEvent != null)
//            firebaseStreamEvent.remove();
//        if (Integer.parseInt(tv_watchers.getText().toString()) > 0)
//            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("viewers", FieldValue. increment(-1));
//
//        super.onDestroy();
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                if (isGuest || isJoined) {
//                    db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", "", "guestId", 0);
//                }
//                if (webRTCClient != null) {
//                    if (webRTCClient.isStreaming())
//                        webRTCClient.stopStream();
//                }
//                if (webRTCClientSub != null) {
//                    if (webRTCClientSub.isStreaming())
//                        webRTCClientSub.stopStream();
//                }
//
//                webRTCClient = null;
//                webRTCClientSub = null;
//            }
//        };
//
//        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
//    }
//
////    @Override
////    public void onBackPressed() {
////        super.onBackPressed();
////
////        if (isGuest || isJoined) {
////            db.collection(fireStoreStreamInfoUrl).document(platform_id).update("platformId", "", "guestId", 0);
////        }
////        if (webRTCClient != null) {
////            if (webRTCClient.isStreaming())
////                webRTCClient.stopStream();
////        }
////        if (webRTCClientSub != null) {
////            if (webRTCClientSub.isStreaming())
////                webRTCClientSub.stopStream();
////        }
////
////        webRTCClient = null;
////        webRTCClientSub = null;
////    }
//
//    @Override
//    public void updatePlayerState() {
//
//    }
//}