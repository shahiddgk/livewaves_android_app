package com.app.livewave.fragments.home;

import static com.app.livewave.utils.BaseUtils.getMimeType;
import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.Constants.DATE_TIME;
import static com.app.livewave.utils.Constants.FCM_TOKEN;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.MY_PROFILE;
import static com.app.livewave.utils.Constants.POST_CREATE_DIALOG;
import static com.app.livewave.utils.Constants.POST_EDIT_DIALOG;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;
import static com.app.livewave.utils.Constants.currentUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.BottomDialogSheets.CreatePostDialogSheet;
import com.app.livewave.BottomDialogSheets.SelectMediaForPostDialogFragment;
import com.app.livewave.R;
import com.app.livewave.activities.FullScreenActivity;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.adapters.InboxAdapter;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.fragments.chat.InboxFragment;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.UploadingProgressInterface;
import com.app.livewave.interfaces.onClickInterfaceForEditPost;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.ParameterModels.OnRefreshPost;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ReadUnreadMessagesNotification;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.FirebaseUtils;
import com.app.livewave.utils.MyApplication;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;


public class HomeFragment extends Fragment implements onClickInterfaceForEditPost, PlayerStateListener {

    private static final String TAG = "HomeFragment";

    private RecyclerView rv_home;
    private PostAdapter adapter;
    private ImageView img_cover, editProfile, img_edit_event_cover;
    private ProgressBar progress_bar;
    private CircleImageView img_profile, img_status;
    private TextView txt_name, txt_followers, txt_following, txt_bio, ic_message_count, ic_message_notification_count;
    private UserModel userModel;
    private List<PostModel> posts = new ArrayList<>();
    private NestedScrollView nested_scroll_view;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    private int totalPages = 0;
    private SwipeRefreshLayout swipe_to_refresh;
    private CreatePostDialogSheet bottomSheetDialog;
    private int noOfFriendRequests;
    public MaterialCardView card_events, card_edit_profile, card_subscriptions;
    boolean isProfile = false;
    private int REQUEST_IMAGE = 1;
    private ArrayList<InboxModel> inboxModelList = new ArrayList<>();
    private String profilePath;
    KProgressHUD loadingDialog;
    LinearLayout layout_following, layout_followers;
    String currentFcmToken;
    ScrollView scrollView;
    KProgressHUD dialog;
    InboxFragment inboxFragment;
    FirebaseFirestore rootRef;
    private int notificationCounter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inboxFragment = new InboxFragment();
        rootRef = FirebaseFirestore.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(root);
        initClickListeners(root);
        return root;
    }


    private void initClickListeners(View root) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.e("!@#!@#", token);

                    }
                });

        root.findViewById(R.id.txt_post).setOnClickListener(v -> {
            if (userModel.getId() != null) {
                bottomSheetDialog = new CreatePostDialogSheet(POST_CREATE_DIALOG, userModel.getId());
                FragmentManager fm = getActivity().getSupportFragmentManager();
                bottomSheetDialog.show(fm, "myDialog");
            }
        });

        root.findViewById(R.id.chat_card).setOnClickListener(v -> {
//                startActivity(new Intent(getActivity(), InboxActivity.class));

            readMessagesApiCall();


            ((HomeActivity) getActivity()).loadFragment(R.string.tag_inbox, null);

            //Was already commented
//                Intent intent = new Intent(getActivity(), WebviewActivity.class);
//                intent.putExtra("intent_type", "4");
//                getActivity().startActivity(intent);
        });

//        root.findViewById(R.id.card_subscriptions).setOnClickListener(v -> {
////                startActivity(new Intent(getActivity(), InboxActivity.class));
//
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_purchased_subscriptions, null);
//
//            //Was already commented
////                Intent intent = new Intent(getActivity(), WebviewActivity.class);
////                intent.putExtra("intent_type", "4");
////                getActivity().startActivity(intent);
//        });

        layout_following.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), FollowFragment.class);
//                intent.putExtra("Id", "0");
//                intent.putExtra("userId", userModel.getId().toString());
//                requireContext().startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString("Id", "0");
            bundle.putString("userId", userModel.getId().toString());
            bundle.putString(HEADER_TITLE, "Followings");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_follow, bundle);
        });
        layout_followers.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userModel.getId().toString());
//                intent.putExtra("noOfFriendRequests", noOfFriendRequests);
//                requireContext().startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString("Id", "1");
            bundle.putString("userId", userModel.getId().toString());
            bundle.putInt("noOfFriendRequests", noOfFriendRequests);
            bundle.putString(HEADER_TITLE, "Followers");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_follow, bundle);
        });
        root.findViewById(R.id.followers).setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userModel.getId().toString());
//                intent.putExtra("noOfFriendRequests", noOfFriendRequests);
//                requireContext().startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString("Id", "1");
            bundle.putString("userId", userModel.getId().toString());
            bundle.putInt("noOfFriendRequests", noOfFriendRequests);
            bundle.putString(HEADER_TITLE, "Followers");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_follow, bundle);
        });
        txt_followers.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userModel.getId().toString());
//                intent.putExtra("noOfFriendRequests", noOfFriendRequests);
//                requireContext().startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString("Id", "1");
            bundle.putString("userId", userModel.getId().toString());
            bundle.putInt("noOfFriendRequests", noOfFriendRequests);
            bundle.putString(HEADER_TITLE, "Followers");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_follow, bundle);
        });
        card_events.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), EventsActivity.class);
//                intent.putExtra(USER_ID, userModel.getId().toString());
//                intent.putExtra(USER_NAME, userModel.getName());
//                intent.putExtra(DATE_TIME, "");
//                startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString(USER_ID, userModel.getId().toString());
            bundle.putString(USER_NAME, userModel.getName());
            bundle.putString(DATE_TIME, "");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_events_list, bundle);
        });

        card_subscriptions.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), EventsActivity.class);
//                intent.putExtra(USER_ID, userModel.getId().toString());
//                intent.putExtra(USER_NAME, userModel.getName());
//                intent.putExtra(DATE_TIME, "");
//                startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString(USER_ID, userModel.getId().toString());
            bundle.putString(USER_NAME, userModel.getName());
            bundle.putString(DATE_TIME, "");
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_purchased_subscriptions, bundle);
        });
        card_edit_profile.setOnClickListener(v -> {
//                startActivity(new Intent(getActivity(), EditProfileFragment.class));
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_edit_profile, bundle);
        });
        root.findViewById(R.id.img_waves_item).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_waves_Features, bundle);
        });
        root.findViewById(R.id.img_search).setOnClickListener(v -> {
//                startActivity(new Intent(getActivity(), SearchFragment.class));

            ((HomeActivity) getActivity()).loadFragment(R.string.tag_search, null);

        });

        root.findViewById(R.id.card_player).setOnClickListener(v -> ((HomeActivity) getActivity()).loadFragment(R.string.tag_wp_store, null));

        editProfile.setOnClickListener(v -> {
            isProfile = true;
            showImagePickerOptions();
        });
        img_edit_event_cover.setOnClickListener(v -> {
            isProfile = false;
            showImagePickerOptions();
        });
        img_profile.setOnClickListener(v -> {
            ArrayList<String> images = new ArrayList<>();
            images.add(getUrlforPicture(userModel.getPhoto()));
            Intent intent = new Intent(getActivity(), FullScreenActivity.class);
            intent.putStringArrayListExtra("Images", images);
            startActivity(intent);
        });
        img_cover.setOnClickListener(v -> {
            ArrayList<String> images = new ArrayList<>();
            images.add(getUrlforPicture(userModel.getCoverPhoto()));
            Intent intent = new Intent(getActivity(), FullScreenActivity.class);
            intent.putStringArrayListExtra("Images", images);
            startActivity(intent);
        });
    }

    private void readMessagesApiCall() {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().readMessages(userModel.getId()), getContext(), new ApiResponseHandler<List<ReadUnreadMessagesNotification>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<ReadUnreadMessagesNotification>>> data) {
                System.out.println("Message Read Api Called");
                System.out.println(data.body().getMessage());
                if (data != null) {
                    ic_message_count.setText("0");
                    notificationCounter = 0;
                    Paper.book().delete("notificationCounts");
                    ic_message_notification_count.setVisibility(View.GONE);
                    ic_message_count.setVisibility(View.GONE);
                }
            }
        });


    }

    private void getLatestMessages() {
        Query query = rootRef.collection(Constants.firebaseDatabaseRoot).whereArrayContains("members", userModel.getId()).orderBy("sentAt", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("ErrorFetchingData", error.toString());
                }
                inboxModelList.clear();
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Log.e("TAG", "onEvent: " + dc.getType());
                        switch (dc.getType()) {
                            case ADDED:
                                InboxModel chatRoot = dc.getDocument().toObject(InboxModel.class);
                                inboxModelList.add(chatRoot);
                                Log.e("added", "onEvent: " + inboxModelList.size());
                                Log.e("added", "onEvent: " + inboxModelList.size());
                                int pos = -1;
                                for (int i = inboxModelList.size() - 1; i > -1; i--) {
                                    for (int j = 0; j < inboxModelList.get(i).membersInfo.size(); j++) {
                                        if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType() != null) {
                                            if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType().equals("delete")) {
                                                pos = i;
                                            }
                                        }
                                    }
                                    if (pos != -1) {
                                        inboxModelList.remove(pos);
                                    }
                                    pos = -1;
                                }
                                // adapter.setList(inboxModelList);

                                break;
                            case MODIFIED:
                                int check = 0;
                                InboxModel modifiedMessage = dc.getDocument().toObject(InboxModel.class);
                                for (int i = 0; i < inboxModelList.size(); i++) {
                                    if (inboxModelList.get(i).getId().equals(modifiedMessage.id)) {
                                        inboxModelList.get(i).senderName = modifiedMessage.senderName;
                                        inboxModelList.get(i).senderId = modifiedMessage.senderId;
                                        inboxModelList.get(i).lastMessage = modifiedMessage.lastMessage;
                                        inboxModelList.get(i).sentAt = modifiedMessage.sentAt;
                                        inboxModelList.get(i).lastMessageId = modifiedMessage.lastMessageId;
                                        inboxModelList.get(i).membersInfo = modifiedMessage.membersInfo;
                                        check = 1;
                                    }
                                }

                                Log.e("modified", "onEvent: " + check + "  " + modifiedMessage.members);

                                if (check == 0) {
                                    inboxModelList.add(modifiedMessage);
                                    Log.e("size", "onEvent: " + inboxModelList.size());
                                }
                                int pos1 = -1;
                                for (int i = inboxModelList.size() - 1; i > -1; i--) {
                                    for (int j = 0; j < inboxModelList.get(i).membersInfo.size(); j++) {
                                        if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType() != null) {
                                            if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType().equals("delete")) {
                                                pos1 = i;
                                            }
                                        }
                                    }
                                    if (pos1 != -1) {
                                        inboxModelList.remove(pos1);
                                    }
                                    pos1 = -1;
                                }
                                Log.e(TAG, "onEvent: " + modifiedMessage + " " + userModel.getName());
                                if (modifiedMessage.senderId == userModel.getId()) {
                                    Log.e(TAG, "onEvent: " + "in if");
                                    Log.e(TAG, "onEvent: " + "you send");
                                } else if (modifiedMessage.senderId != userModel.getId()  && inboxModelList.size() > 0) {
                                    Log.e(TAG, "onEvent: " + "in else");
                                    notificationCounter++;
                                    if (notificationCounter > 0 ) {
                                        Paper.book().write("notificationCounts", notificationCounter);
                                        ic_message_notification_count.setVisibility(View.VISIBLE);
                                        ic_message_notification_count.setText("" + notificationCounter);
                                    }
                                }

//                                InboxModel inboxModel = inboxModelList.get(InboxAdapter.clickedChatPosition);
//                                inboxModelList.set(InboxAdapter.clickedChatPosition,inboxModelList.get(0));
//                                inboxModelList.set(0,inboxModel);
//                                adapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                } else {
                    Log.e("else ", "onEvent: " + "nulll");
                }

            }
        });
    }


    public void showImagePickerOptions() {
        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(2);
        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
                    launchCameraIntent();
                }
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();
                }

            }
        });
        FragmentManager fm = getActivity().getSupportFragmentManager();
        selectMediaForPostDialogFragment.show(fm, "Select Media");

    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//        if (isProfile) {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//        } else {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
//        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//        if (isProfile) {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//        } else {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
//        }

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                if (isProfile)
                    Glide.with(getActivity()).load(uri).placeholder(R.drawable.profile_place_holder).into(img_profile);
                else
                    Glide.with(getActivity()).load(uri).placeholder(R.drawable.cover_place_holder).into(img_cover);
                if (isProfile) {
                    userModel.setPhoto(uri.toString());
//                    Glide.with(getActivity()).load(uri).into(img_profile);
                } else {
                    userModel.setCoverPhoto(uri.toString());

//                    Glide.with(getActivity()).load(uri).into(img_cover);
                }
                loadingDialog.show();
                File file = new File(uri.getPath());
                String unique_name = UUID.randomUUID().toString();
                if (isProfile)
                    profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/user/profile";
                else
                    profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/user/cover";
                FirebaseApp.initializeApp(getActivity());
                FirebaseUtils utils = new FirebaseUtils(getActivity());
                String ext = getMimeType(getActivity(), uri);
                utils.uploadVideoFileToFirebase(ext, profilePath, file, new UploadingProgressInterface() {
                    @Override
                    public void progressChange(int progress) {
                        Log.d("!@#", progress + "");
                        loadingDialog.setProgress(progress);
                    }

                    @Override
                    public void onFailureUpload(String message) {
                        Log.d("!@#fail", message + "");
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onSuccessfulUpload(Uri fileUri) {
                        Call call;
                        if (isProfile)
                            call = ApiClient.getInstance().getInterface().updateProfile(fileUri.toString());
                        else
                            call = ApiClient.getInstance().getInterface().updateCover(fileUri.toString());
                        ApiManager.apiCall(call, getActivity(), new ApiResponseHandler<UserModel>() {
                            @Override
                            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                                loadProfile();
                                currentPageNumber = 1;
                                loadPost();
                            }
                        });

                    }
                });
//                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (userModel != null)
            setData(userModel);
        getLatestMessages();


    }

    @Override
    public void onResume() {
        super.onResume();
        userModel = Paper.book().read(Constants.currentUser);
        Constants.isOnHomeFragment = true;
        if (userModel != null)
            setData(userModel);
        notificationCounter = Paper.book().read("notificationCounts", 0);
        Log.e(TAG, "onResume: " + notificationCounter);
        if (notificationCounter != 0) {
            if (notificationCounter > 0) {

                ic_message_notification_count.setVisibility(View.VISIBLE);
                ic_message_notification_count.setText("" + notificationCounter);
            }
        } else {
            ic_message_notification_count.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Constants.isOnHomeFragment = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        currentFcmToken = Paper.book().read(FCM_TOKEN);
        editProfile = view.findViewById(R.id.editProfile);
        img_edit_event_cover = view.findViewById(R.id.img_edit_event_cover);
        card_subscriptions = view.findViewById(R.id.card_subscriptions_personal);
        card_events = view.findViewById(R.id.card_events);
        card_edit_profile = view.findViewById(R.id.card_edit_profile);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        nested_scroll_view.setNestedScrollingEnabled(true);
        progress_bar = view.findViewById(R.id.progress_bar);
        layout_following = view.findViewById(R.id.layout_following);
        layout_followers = view.findViewById(R.id.layout_followers);
        rv_home = view.findViewById(R.id.rv_home);
        ic_message_notification_count = view.findViewById(R.id.ic_message_notification_count);
//        ViewGroup.LayoutParams params=rv_home.getLayoutParams();
//        params.height= (int) Constants.screenHeight+1;
//        rv_home.setLayoutParams(params);
        rv_home.setHasFixedSize(false);
        rv_home.setNestedScrollingEnabled(false);
        dialog = BaseUtils.progressDialog(getContext());
        nested_scroll_view.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_home.setLayoutManager(layoutManager);
        adapter = new PostAdapter(getActivity(), userModel.getId(), MY_PROFILE, rv_home);
        adapter.setUpOnclickInterfaceForEditPost(this);
        rv_home.setAdapter(adapter);
        img_cover = view.findViewById(R.id.img_cover);
        img_profile = view.findViewById(R.id.img_profile);
        img_status = view.findViewById(R.id.img_status);
        txt_name = view.findViewById(R.id.txt_name);
        ic_message_count = view.findViewById(R.id.ic_message_count);
        txt_followers = view.findViewById(R.id.txt_followers);
        txt_following = view.findViewById(R.id.txt_following);
        txt_bio = view.findViewById(R.id.txt_bio);
//        dialog = BaseUtils.progressDialog(getActivity());
        loadingDialog = BaseUtils.showProgressDialog(getActivity());
        loadUnreadMessages();
        getActionData();
        loadProfile();
        loadPost();

        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
                    if ((currentItems + scrollOutItems) >= totalItems) {
                        if (currentPageNumber != totalPages) {
                            progress_bar.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            loadPost();
                        }
                    }
                }
            }
        });


//        rv_home.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager lManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int lastElementPosition = lManager.findLastCompletelyVisibleItemPosition();
//                if(lastElementPosition == posts.size()-1){
//                    if (currentPageNumber != totalPages) {
//                        progress_bar.setVisibility(View.VISIBLE);
//                        currentPageNumber++;
//                        loadPost();
//                    }
//                }
//            }
//        });


        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                posts.clear();
                loadProfile();
                loadPost();
                swipe_to_refresh.setRefreshing(false);
            }
        });
    }

    private void loadUnreadMessages() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().unReadMessages(), getContext(), new ApiResponseHandler<List<ReadUnreadMessagesNotification>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<ReadUnreadMessagesNotification>>> data) {
                System.out.println("UnRead Messages Api Called");
                System.out.println(data.body().getMessage());
                if (data != null) {
                    setDataForNotification(data.body().getData());
                }
            }

            private void setDataForNotification(List<ReadUnreadMessagesNotification> data) {


                if (data.size() > 0) {

                    ic_message_count.setVisibility(View.VISIBLE);
                    if (data.size() > 99) {
                        ic_message_count.setText(data.size() + "+");
                    } else {
                        ic_message_count.setText(data.size() + "");
                    }
                } else {
                    ic_message_count.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getActionData() {
        String action = " ";
        String path = " ";
        action = Paper.book().read("action");
        if (action == "android.intent.action.SEND" || action.equals("android.intent.action.SEND")) {
            path = Paper.book().read("Picture Path");
            System.out.println("PICTURES PATH IN HOME FRAGEMENT");
            System.out.println(path);
            CreatePostDialogSheet createPostDialogSheet = new CreatePostDialogSheet(Constants.POST_EDIT_DIALOG, userModel.getId());
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            createPostDialogSheet.show(fragmentManager, POST_CREATE_DIALOG);
        }

    }

    private void loadProfile() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getProfile(userModel.getId().toString()), getActivity(), new ApiResponseHandler<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                if (data != null) {
                    UserModel userModel1 = data.body().getData();
                    noOfFriendRequests = userModel1.getTotalRequests();
                    Paper.book().write(Constants.currentUser, userModel1);
                    if (userModel1.getFcmToken() == null || !userModel1.getFcmToken().equals(currentFcmToken))
                        upDateFcmToServer();
                    setData(userModel1);
                }
            }
        });

    }

    private void upDateFcmToServer() {
        if (currentFcmToken != null) {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().updateFCM(currentFcmToken), getActivity(), new ApiResponseHandler<UserModel>() {
                @Override
                public void onSuccess(Response<ApiResponse<UserModel>> data) {
                    Paper.book().write(Constants.currentUser, data.body().getData());
                }
            });
        }

    }

    private void loadPost() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getPost(userModel.getId(), currentPageNumber), getActivity(),
                new ApiResponseHandler<GenericDataModel<PostModel>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<GenericDataModel<PostModel>>> data) {
                        if (data != null) {
                            currentPageNumber = data.body().getData().getCurrentPage();
                            totalPages = data.body().getData().getLastPage();
                            if (currentPageNumber == 1) {
                                posts = new ArrayList<>();
                                posts = data.body().getData().getData();
                            } else {
                                posts.addAll(data.body().getData().getData());
                            }
                            adapter.setList(posts);
                            dialog.dismiss();
                            progress_bar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void setData(UserModel userModel1) {
        Glide.with(MyApplication.getAppContext()).load(getUrlforPicture(userModel1.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_profile);
        Glide.with(MyApplication.getAppContext()).load(getUrlforPicture(userModel1.getCoverPhoto())).placeholder(R.drawable.cover_place_holder).into(img_cover);
        Glide.with(MyApplication.getAppContext()).load(getUrlforPicture(userModel1.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_status);
        txt_bio.setText(userModel1.getBio());
        txt_name.setText(userModel1.getName());
        txt_followers.setText(String.valueOf((int) userModel1.getTotalFollowers()));
        txt_following.setText(String.valueOf((int) userModel1.getTotalFollowing()));
        BaseUtils.setVerifiedAccount(userModel1.getVerified(), txt_name);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        ApiClient.getInstance().getInterface().getPost(userModel.getId(), currentPageNumber).cancel();
        ApiClient.getInstance().getInterface().getProfile(userModel.getId().toString()).cancel();
        dialog.dismiss();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRecyclerItemClick(OnTouch event) {
        if (event.isClick()) {
            rv_home.requestDisallowInterceptTouchEvent(true);
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreatingPost(OnRefreshPost event) {
        if (event.isRefresh() && !event.isEditPost()) {
            posts.add(0, event.getPostModel());
            adapter.setList(posts);
        } else if (event.isRefresh() && event.isEditPost()) {
            int position = BaseUtils.getIndexFromList(posts, event.getPostModel().getId());
            if (position != -1) {
                posts.set(position, event.getPostModel());
                adapter.setList(posts);
            } else {
                BaseUtils.showLottieDialog(getActivity(), "Post Edited successfully Refresh screen", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
//                BaseUtils.showToast(getActivity(), "Post Edited successfully Refresh screen");
            }
        }

    }


    @Override
    public void onClickEdit(PostModel postModel) {
        CreatePostDialogSheet createPostDialogSheet = new CreatePostDialogSheet(Constants.POST_EDIT_DIALOG, userModel.getId(), postModel);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        createPostDialogSheet.show(fragmentManager, POST_EDIT_DIALOG);
    }

    @Override
    public void updatePlayerState() {

    }
}