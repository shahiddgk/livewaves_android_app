package com.app.livewave.fragments;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.OTHER_PERSON_PROFILE;
import static com.app.livewave.utils.Constants.POST_CREATE_DIALOG;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;
import static com.app.livewave.utils.Constants.USER_PROFILE;
import static com.app.livewave.utils.Constants.currentUser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.BottomDialogSheets.CreatePostDialogSheet;
import com.app.livewave.BottomDialogSheets.EnterAmountDialogSheet;
import com.app.livewave.BottomDialogSheets.OptionsDialogSheet;
import com.app.livewave.BottomDialogSheets.ReportDialogSheet;
import com.app.livewave.BottomDialogSheets.ReportOptionsDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.FullScreenActivity;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.SubscriberActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.MessageInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.ParameterModels.OnRefreshPost;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.MyApplication;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

public class UserProfileFragment extends Fragment implements PlayerStateListener {

    private RecyclerView rv_user_profile;
    private PostAdapter adapter;
    private ImageView img_cover, iv_follow_status;
    private ProgressBar progress_bar;
    private CircleImageView img_profile, img_status;
    private TextView txt_name, txt_followers, txt_following, txt_bio, tv_account_status;
    private List<PostModel> posts = new ArrayList<>();
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;
    private NestedScrollView nested_scroll_view;
    private SwipeRefreshLayout swipe_to_refresh;
    private CreatePostDialogSheet bottomSheetDialog;
    private String userId;
    private LinearLayout ll_what_on_mind, layout_following, layout_followers;
    private MaterialCardView chat_card, profile_follow_card, events_card, live_card, card_subscriptions_user,card_report_user;
    private UserModel userModel, otherUserModel;
    private KProgressHUD dialog;
    final Handler handler = new Handler();
    Timer timer = new Timer();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("ON CREATED VIEW CALL 1");
        initViews(view);
        initListeners(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        System.out.println("ON CREATED VIEW CALL 2");
        if (getArguments().containsKey(SPECIFIC_USER_ID)) {

            userId = getArguments().getString(SPECIFIC_USER_ID);
            System.out.println("SPECIFIC_USER_ID");
            System.out.println(userId);
        }
        loadProfile();
//        initViews(view);
//        initListeners(view);
        return view;
    }


    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_profile);
//        userId = getIntent().getStringExtra(Constants.SPECIFIC_USER_ID);
//        initViews();
//        initListeners();
//    }

    private void initListeners(View view) {
        view.findViewById(R.id.txt_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new CreatePostDialogSheet(POST_CREATE_DIALOG, Integer.parseInt(userId));
                FragmentManager fm = getChildFragmentManager();
                bottomSheetDialog.show(fm, "myDialog");

            }
        });
        view.findViewById(R.id.chat_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String myJson = gson.toJson(userModel);
//                Intent intent = new Intent(UserProfileActivity.this, ChatFragment.class);
//                intent.putExtra("friendModel", myJson);
//                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString("friendModel", myJson);
                bundle.putString(HEADER_TITLE, userModel.getName());
                ((HomeActivity) getActivity()).loadFragment(R.string.tag_chat, bundle);

//                Intent intent = new Intent(UserProfileActivity.this, WebviewActivity.class);
//                intent.putExtra("intent_type", "4");
//                intent.putExtra("userId", userId);
//                startActivity(intent);
            }
        });


        view.findViewById(R.id.card_subscriptions_user).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(SPECIFIC_USER_ID, userId);
            bundle.putString(HEADER_TITLE, "Subscription Plans");
            System.out.println("userIduserId");
            System.out.println(userId);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_artist_subscriptions, bundle);
        });

        view.findViewById(R.id.card_player).setOnClickListener(v -> ((HomeActivity) getActivity()).loadFragment(R.string.tag_user_wp_store, null));

//        view.findViewById(R.id.card_player).setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putString(SPECIFIC_USER_ID, userId);
//            bundle.putString(HEADER_TITLE, userModel.getName() + " 's Playlist");
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_artist_playlist, bundle);
//        });

        view.findViewById(R.id.send_money).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            EnterAmountDialogSheet enterAmountDialogSheet = new EnterAmountDialogSheet(new MessageInterface() {
                @Override
                public void IAmountEnter(String mAmount) {
                    if (mAmount != null) {
                        String amount = mAmount;

                        Intent intent = new Intent(((HomeActivity) getActivity()).getBaseContext(), WebviewActivity.class);
                        intent.putExtra("type", "donation");
                        intent.putExtra("amount", amount);
                        intent.putExtra("id", userId);
                        intent.putExtra("intent_type", "8");
                        startActivity(intent);

                    } else {

                    }
                }
            });
            FragmentManager fm = getChildFragmentManager();
            enterAmountDialogSheet.show(fm, "amount");

//            Bundle bundle = new Bundle();
//            bundle.putString(SPECIFIC_USER_ID, userId);
//            bundle.putString(HEADER_TITLE, userModel.getName() + " 's Playlist");
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_artist_playlist, bundle);

        });

        view.findViewById(R.id.layout_following).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UserProfileActivity.this, FollowFragment.class);
//                intent.putExtra("Id", "0");
//                intent.putExtra("userId", userId);
//                startActivity(intent);

                ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber).cancel();
                ApiClient.getInstance().getInterface().getProfile(userId).cancel();


                System.out.println("SPECIFIC_USER_ID");
                System.out.println(userId);
                System.out.println(R.string.tag_follow);
                String tagId = R.string.tag_follow + userId;

                Bundle bundle = new Bundle();
                bundle.putString("Id", "0");
                bundle.putString("userId", userId);
                bundle.putString(HEADER_TITLE, "Following");
                ((HomeActivity) getActivity()).performFragmentTransaction(tagId,R.string.tag_follow,bundle);
            }
        });
        view.findViewById(R.id.layout_followers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UserProfileActivity.this, FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userId);
//                startActivity(intent);

                ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber).cancel();
                ApiClient.getInstance().getInterface().getProfile(userId).cancel();

                System.out.println("SPECIFIC_USER_ID");
                System.out.println(userId);

                System.out.println("SPECIFIC_USER_ID");
                System.out.println(userId);
                System.out.println(R.string.tag_follow);
                String tagId = R.string.tag_follow + userId;

                Bundle bundle = new Bundle();
                bundle.putString("Id", "1");
                bundle.putString("userId", userId);
                bundle.putString(HEADER_TITLE, "Followers");
                ((HomeActivity) getActivity()).performFragmentTransaction(tagId,R.string.tag_follow,bundle);
            }
        });
        view.findViewById(R.id.followers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UserProfileActivity.this, FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userId);
//                startActivity(intent);
                System.out.println("SPECIFIC_USER_ID");
                System.out.println(userId);
                System.out.println(R.string.tag_follow);
                String tagId = R.string.tag_follow + userId;

                Bundle bundle = new Bundle();
                bundle.putString("Id", "1");
                bundle.putString("userId", userId);
                bundle.putString(HEADER_TITLE, "Followers");
                ((HomeActivity) getActivity()).performFragmentTransaction(tagId,R.string.tag_follow,bundle);
            }
        });
        txt_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UserProfileActivity.this, FollowFragment.class);
//                intent.putExtra("Id", "1");
//                intent.putExtra("userId", userId);
//                startActivity(intent);

                System.out.println("SPECIFIC_USER_ID");
                System.out.println(userId);
                System.out.println(R.string.tag_follow);
                String tagId = R.string.tag_follow + userId;

                Bundle bundle = new Bundle();
                bundle.putString("Id", "1");
                bundle.putString("userId", userId);
                bundle.putString(HEADER_TITLE, "Followers");
                ((HomeActivity) getActivity()).performFragmentTransaction(tagId,R.string.tag_follow,bundle);
            }
        });
        profile_follow_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsDialogSheet dialogSheet = new OptionsDialogSheet(otherUserModel, Integer.parseInt(userId));
                dialogSheet.addListener(new PostOptionInterface() {
                    @Override
                    public void pressed(String pressedButton) {
                        if (pressedButton.equals("Follow")) {
                            if (userModel.getFollow().equals(1)) {
                                BaseUtils.showAlertDialog("Unfollow", "Are you sure want to unfollow " + userModel.getName() + " ?", getActivity(), new DialogBtnClickInterface() {
                                    @Override
                                    public void onClick(boolean positive) {
                                        if (positive) {
                                            followUnFollowUser();
                                        }
                                    }
                                });
                            } else {
                                followUnFollowUser();

                            }
                        } else if (pressedButton.equals("Report")) {
                            BaseUtils.showAlertDialog("Report", "Are you sure, you want to report " + userModel.getName() + " ?", getActivity(), new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {
                                    if (positive) {
                                        reportDialog();
                                    }
                                }
                            });


                        } else if (pressedButton.equals("Block")) {
                            if (userModel.getIsBlocked().equals(0)) {
                                BaseUtils.showAlertDialog("Block", "Are you sure, you want to block " + userModel.getName() + " ?", getActivity(), new DialogBtnClickInterface() {
                                    @Override
                                    public void onClick(boolean positive) {
                                        if (positive) {
                                            blockUser();
                                        }
                                    }
                                });
                            } else {
                                blockUser();
                            }
                        }
                        dialogSheet.dismiss();

                    }
                });
                FragmentManager fm = getChildFragmentManager();
                dialogSheet.show(fm, "myDialog");

            }
        });

        card_report_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportOptionsDialogSheet dialogSheet = new ReportOptionsDialogSheet(otherUserModel, Integer.parseInt(userId),USER_PROFILE);
                dialogSheet.addListener(new PostOptionInterface() {
                    @Override
                    public void pressed(String pressedButton) {
                        if (pressedButton.equals("Report")) {
                            BaseUtils.showAlertDialog("Report", "Are you sure, you want to report " + userModel.getName() + " ?", getActivity(), new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {
                                    if (positive) {
                                        reportDialog();
                                    }
                                }
                            });


                        } else if (pressedButton.equals("Block")) {
                            if (userModel.getIsBlocked().equals(0)) {
                                BaseUtils.showAlertDialog("Block", "Are you sure, you want to block " + userModel.getName() + " ?", getActivity(), new DialogBtnClickInterface() {
                                    @Override
                                    public void onClick(boolean positive) {
                                        if (positive) {
                                            blockUser();
                                        }
                                    }
                                });
                            } else {
                                blockUser();
                            }
                        }
                        dialogSheet.dismiss();

                    }
                });
                FragmentManager fm = getChildFragmentManager();
                dialogSheet.show(fm, "myDialog");
            }
        });


        events_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Intent intent = new Intent(getActivity(), OtherPersonAndTimeSpecificEventsFragment.class);
//                    intent.putExtra(Constants.USER_ID, otherUserModel.getId().toString());
//                    intent.putExtra(Constants.USER_NAME, otherUserModel.getName());
//                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.USER_ID, otherUserModel.getId().toString());
                    bundle.putString(Constants.USER_NAME, otherUserModel.getName());
                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_other_person_and_time_specific_events, bundle);
                } catch (Exception exception) {
                    Log.e("EXCEPTION", exception.getMessage());
                }

            }
        });
        live_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if stream is paid
                if (otherUserModel.getStream().getAmount() > 0 && otherUserModel.getStream().getIsPaid() == 0) {

                    System.out.println("Paid Stream");

                    Intent intent = new Intent(getContext(), WebviewActivity.class);
                    intent.putExtra("id", "stream_id=" + otherUserModel.getStream().getId());
                    intent.putExtra("type", "stream");
                    intent.putExtra("intent_type", "6");
                    startActivity(intent);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", "stream_id=" + otherUserModel.getStream().getId());
//                    bundle.putString("type", "stream");
//                    bundle.putString("intent_type", "6");
//                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_webview, bundle);
                }
                //if stream is free or the person already paid of the stream
                else {
                    System.out.println("Stream Testing");
                    System.out.println(otherUserModel.getStream().getId());
                    System.out.println(otherUserModel.getStream().getTitle());
                    System.out.println(otherUserModel.getStream().getPlatformID());

                    if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                        Intent intent = new Intent(getContext(), SubscriberActivity.class);
                        intent.putExtra("ID", otherUserModel.getStream().getId().toString());
                        intent.putExtra("TITLE", otherUserModel.getStream().getTitle());
                        intent.putExtra("PLATFORM_ID", otherUserModel.getStream().getPlatformID());
                        intent.putExtra("STREAM_ID_TYPE", "stream_id");
                        intent.putExtra("Subscriber", "Subscriber");
                        startActivity(intent);

                    } else {
                        Toast.makeText(getContext(), "Permission Denied -> Go to Setting -> Allow Permission", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.RECORD_AUDIO},
                                1);
                    }

//                    intent = new Intent(getContext(), SubscriberActivity.class);
//                    intent.putExtra("ID", otherUserModel.getStream().getId());
//                    intent.putExtra("TITLE", otherUserModel.getStream().getTitle());
//                    intent.putExtra("PLATFORM_ID", otherUserModel.getStream().getPlatformID());
//                    intent.putExtra("STREAM_ID_TYPE", "stream_id");
//                    intent.putExtra("Subscriber", "Subscriber");

//
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ID", otherUserModel.getStream().getId());
//                    bundle.putString("TITLE", otherUserModel.getStream().getTitle());
//                    bundle.putString("PLATFORM_ID", otherUserModel.getStream().getPlatformID());
//                    bundle.putString("STREAM_ID_TYPE", "stream_id");
//                    bundle.putString("Subscriber", "Subscriber");
//                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_subscriber, bundle);
                }
            }
        });
        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();

                System.out.println("ON BACK CALLED");
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherUserModel.getPhoto() != null) {
                    ArrayList<String> images = new ArrayList<>();
                    images.add(getUrlforPicture(otherUserModel.getPhoto()));
                    Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                    intent.putStringArrayListExtra("Images", images);
                    startActivity(intent);
                }

            }
        });
        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherUserModel.getCoverPhoto() != null) {
                    ArrayList<String> images = new ArrayList<>();
                    images.add(getUrlforPicture(otherUserModel.getCoverPhoto()));
                    Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                    intent.putStringArrayListExtra("Images", images);
                    startActivity(intent);
                }

            }
        });

    }

    private void reportDialog() {
        ReportDialogSheet enterAmountDialogSheet = new ReportDialogSheet(new MessageInterface() {
            @Override
            public void IAmountEnter(String mAmount) {
                if (mAmount != null) {
                    HashMap<String, Object> hashMap = new HashMap();
                    hashMap.put("reported_id", userId);
                    hashMap.put("message", mAmount);
                    hashMap.put("title", "Report");
                    ApiManager.apiCall(ApiClient.getInstance().getInterface().report(hashMap), getActivity(), new ApiResponseHandler<UserModel>() {
                        @Override
                        public void onSuccess(Response<ApiResponse<UserModel>> data) {
//                            BaseUtils.showToast(UserProfileActivity.this, "Reported!");
                            BaseUtils.showLottieDialog(getActivity(), "Reported!", R.raw.check, new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {

                                }
                            });
                        }
                    });
                } else {

                }
            }
        });
        FragmentManager fm = getChildFragmentManager();
        enterAmountDialogSheet.show(fm, "amount");
    }

    private void blockUser() {
//        dialog.show();
        ((HomeActivity) getActivity()).showProgressDialog();
        ApiManager.apiCallWithFailure(new ApiClient().getInterface().blockUnblockUser(Integer.parseInt(userId)), getActivity(), new ApiResponseHandlerWithFailure<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {

                if (data.body().getMessage().equals("Blocked Successfully")) {
                    userModel.setIsBlocked(1);
                } else if (data.body().getMessage().equals("Unblocked Successfully")) {
                    userModel.setIsBlocked(0);
                }
                if (userModel.getPrivate().equals("1") && userModel.getFollow().equals(0) && userModel.getRequested() == 1) {
                    followUnFollowUser();
                } else {
//                    dialog.dismiss();
                    ((HomeActivity) getActivity()).hideProgressDialog();
                    setData(userModel);
                }
            }

            @Override
            public void onFailure(String failureCause) {
//                dialog.dismiss();
                ((HomeActivity) getActivity()).hideProgressDialog();
//                BaseUtils.showToast(UserProfileActivity.this, failureCause);
                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            }
        });
    }

    private void followUnFollowUser() {
//        dialog.show();
        ((HomeActivity) getActivity()).hideProgressDialog();

        ApiManager.apiCallWithFailure(new ApiClient().getInterface().followUnfollowUser(Integer.parseInt(userId)), getActivity(), new ApiResponseHandlerWithFailure<String>() {
            @Override
            public void onSuccess(Response<ApiResponse<String>> data) {
//                dialog.dismiss();
                ((HomeActivity) getActivity()).hideProgressDialog();

                if (data.body().getMessage().equals("Followed Successfully")) {
                    userModel.setFollow(1);
                } else if (data.body().getMessage().equals("unfollowed Successfully")) {
                    userModel.setFollow(0);
                } else if (data.body().getMessage().equals("Follow request sent successfully")) {
                    userModel.setRequested(1);
                    userModel.set_private("1");
                    userModel.setFollow(0);
                } else if (data.body().getMessage().equals("Request removed successfully")) {
                    userModel.setRequested(0);
                    userModel.setFollow(0);
                }
                setData(userModel);
            }

            @Override
            public void onFailure(String failureCause) {
//                dialog.dismiss();
                ((HomeActivity) getActivity()).hideProgressDialog();

//                BaseUtils.showToast(UserProfileActivity.this, failureCause);
                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            }
        });

    }

    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(currentUser);

        dialog = BaseUtils.progressDialog(getActivity());
        live_card = view.findViewById(R.id.live_card);
        card_subscriptions_user = view.findViewById(R.id.card_subscriptions_user);
        card_report_user = view.findViewById(R.id.card_report_user);
        tv_account_status = view.findViewById(R.id.tv_account_status);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        layout_followers = view.findViewById(R.id.layout_followers);
        layout_following = view.findViewById(R.id.layout_following);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        chat_card = view.findViewById(R.id.chat_card);
        events_card = view.findViewById(R.id.events_card);
        profile_follow_card = view.findViewById(R.id.profile_follow_card);
        progress_bar = view.findViewById(R.id.progress_bar);
        ll_what_on_mind = view.findViewById(R.id.ll_what_on_mind);
        rv_user_profile = view.findViewById(R.id.rv_user_profile);
        rv_user_profile.setHasFixedSize(false);
        rv_user_profile.setNestedScrollingEnabled(false);
        nested_scroll_view.setNestedScrollingEnabled(true);
//        ViewGroup.LayoutParams params=rv_user_profile.getLayoutParams();
//        params.height= (int) Constants.screenHeight+1;
//        rv_user_profile.setLayoutParams(params);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_user_profile.setLayoutManager(layoutManager);
        iv_follow_status = view.findViewById(R.id.iv_follow_status);
        img_cover = view.findViewById(R.id.img_cover);
        img_profile = view.findViewById(R.id.img_profile);
        img_status = view.findViewById(R.id.img_status);
        txt_name = view.findViewById(R.id.txt_name);
        txt_followers = view.findViewById(R.id.txt_followers);
        txt_following = view.findViewById(R.id.txt_following);
        txt_bio = view.findViewById(R.id.txt_bio);

        //loadProfile();
        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
                    if ((currentItems + scrollOutItems) >= totalItems) {
                        if (nextPageUrl != null) {
                            progress_bar.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            loadPost();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                posts.clear();
                loadProfile();
                swipe_to_refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        System.out.println("RESUME FUNCTIONS");
        loadPost();

        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("ON DETACH CALLED");
        ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber).cancel();
        ApiClient.getInstance().getInterface().getProfile(userId).cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("ON DESTROY CALLED");

        ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber).cancel();
        ApiClient.getInstance().getInterface().getProfile(userId).cancel();

    }

    private void loadPost() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber), getActivity(),
                new ApiResponseHandler<GenericDataModel<PostModel>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<GenericDataModel<PostModel>>> data) {
                        if (data != null) {
                            if (data.body().getData().getData().size() > 0) {
                                if (userModel.getPrivate().equals("1") && userModel.getFollow().equals(0))
                                    return;

                                tv_account_status.setVisibility(View.GONE);
                                currentPageNumber = data.body().getData().getCurrentPage();
                                nextPageUrl = data.body().getData().getNextPageUrl();
                                if (currentPageNumber == 1) {
                                    posts = new ArrayList<>();
                                    posts = data.body().getData().getData();
                                } else {
                                    posts.addAll(data.body().getData().getData());
                                }
                                Activity activity = getActivity();
                                if (isAdded() && activity != null) {
                                    if (posts.size() > 0) {
                                        handler.postDelayed(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.setList(posts);
                                                        progress_bar.setVisibility(View.GONE);
                                                    }
                                                },500);
                                    }

                                }
                            } else {
                                rv_user_profile.setVisibility(View.GONE);
                                tv_account_status.setVisibility(View.VISIBLE);
                                tv_account_status.setText(getString(R.string.posts_found));
                            }
                        }
                    }
                });

    }

    private void loadProfile() {
//        dialog.show();
        ((HomeActivity) getActivity()).showProgressDialog();

        ApiManager.apiCall(ApiClient.getInstance().getInterface().getProfile(userId), getActivity(), new ApiResponseHandler<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                if (data != null) {
                    if (Objects.requireNonNull(data.body()).getData() != null) {
                        otherUserModel = data.body().getData();
                        userId = otherUserModel.getId().toString();
                        Paper.book().write("UserID", userId);
                        Activity activity = getActivity();
                        if (isAdded() && activity!=null) {
                            adapter = new PostAdapter(getActivity(), Integer.parseInt(userId), OTHER_PERSON_PROFILE, rv_user_profile);
                            rv_user_profile.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            loadPost();
                            setData(otherUserModel);
                        }

                    } else {
                        ApiClient.getInstance().getInterface().getPost(Integer.parseInt(userId), currentPageNumber).cancel();
                        ApiClient.getInstance().getInterface().getProfile(userId).cancel();
                        getActivity().onBackPressed();
                    }

                }
            }
        });
    }

//    x@Override
//    public void onBackPressed() {
//        if (isTaskRoot()) {
//            startActivity(new Intent(getActivity(), HomeActivity.class));
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void setData(UserModel userModel1) {
        userModel = userModel1;
        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(userModel1.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_profile);
        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(userModel1.getCoverPhoto())).placeholder(R.drawable.cover_place_holder).into(img_cover);
        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(userModel1.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_status);
        txt_bio.setText(userModel1.getBio());
        txt_name.setText(userModel1.getName());
        txt_followers.setText(String.valueOf((int) userModel1.getTotalFollowers()));
        txt_following.setText(String.valueOf((int) userModel1.getTotalFollowing()));

        BaseUtils.setVerifiedAccount(userModel1.getVerified(), txt_name);
//        dialog.dismiss();
        ((HomeActivity) getActivity()).hideProgressDialog();


        if (userModel1.getPrivate().equals("1") && userModel1.getFollow().equals(0)) {
            setAccountPrivate();
        } else {
            setAccount();
        }

        if (userModel1.getFollow().equals(1)) {
            iv_follow_status.setImageResource(R.drawable.ic_tick);
            profile_follow_card.setCardBackgroundColor(getResources().getColor(R.color.spotify_green));
        } else if (userModel1.getPrivate().equals("1") && userModel1.getFollow().equals(0) && userModel1.getRequested() == 1) {
            tv_account_status.setText(getString(R.string.following_request_is_pending));
            tv_account_status.setVisibility(View.VISIBLE);
            iv_follow_status.setImageResource(R.drawable.ic_pending);
            profile_follow_card.setCardBackgroundColor(getResources().getColor(R.color.dark_grey));

        } else if (userModel1.getIsBlocked() == 1) {
            iv_follow_status.setImageResource(R.drawable.ic_baseline_block_24);
            profile_follow_card.setCardBackgroundColor(getResources().getColor(R.color.quantum_googredA700));

        } else {
            iv_follow_status.setImageResource(R.drawable.ic_add);
            profile_follow_card.setCardBackgroundColor(getResources().getColor(R.color.buttercup));

        }
        if (userModel1.getStatus().equals("2"))
            profile_follow_card.setVisibility(View.GONE);
        if (userModel1.getStreaming() == 1) {
            live_card.setVisibility(View.VISIBLE);
        } else {
            live_card.setVisibility(View.GONE);
        }
    }

    private void setAccount() {
        ll_what_on_mind.setVisibility(View.VISIBLE);
        rv_user_profile.setVisibility(View.VISIBLE);
        chat_card.setVisibility(View.VISIBLE);
        events_card.setVisibility(View.VISIBLE);
        profile_follow_card.setVisibility(View.VISIBLE);
        layout_following.setVisibility(View.VISIBLE);
        layout_followers.setVisibility(View.VISIBLE);

    }

    private void setAccountPrivate() {
        ll_what_on_mind.setVisibility(View.GONE);
        rv_user_profile.setVisibility(View.GONE);
        live_card.setVisibility(View.GONE);
        chat_card.setVisibility(View.GONE);
        events_card.setVisibility(View.GONE);
//        layout_following.setVisibility(View.GONE);
//        layout_followers.setVisibility(View.GONE);
        tv_account_status.setVisibility(View.VISIBLE);
        tv_account_status.setText(getString(R.string.this_acount_is_private_but_you_can_send_them_a_request));


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

//    @Override
//    public void onDestroy() {
////        dialog.dismiss();
//        ((HomeActivity)getActivity()).hideProgressDialog();
//
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        EventBus.getDefault().removeAllStickyEvents();
//        super.onDestroy();
//    }

//    @Override
//    public void onDestroy() {
//        dialog.dismiss();
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        EventBus.getDefault().removeAllStickyEvents();
//
//        if (((HomeActivity) getActivity()).isTaskRoot(this)) {
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(HIDE_HEADER, false);
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_dashboard, bundle);
//        }
////        else {
////            getActivity().onBackPressed();
////        }
//        super.onDestroy();
//    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRecyclerItemClick(OnTouch event) {
        if (event.isClick()) {
            rv_user_profile.requestDisallowInterceptTouchEvent(true);
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCreatingPost(OnRefreshPost event) {
        if (event.isRefresh()) {
            posts.add(0, event.getPostModel());
            adapter.setList(posts);
        } else if (event.isRefresh() && event.isEditPost()) {
            int position = BaseUtils.getIndexFromList(posts, event.getPostModel().getId());
            if (position != -1) {
                posts.set(position, event.getPostModel());
                adapter.setList(posts);
            } else {
//                BaseUtils.showToast(this, "Post Edited successfully Refresh screen");
                BaseUtils.showLottieDialog(getActivity(), "Post Edited successfully Refresh screen", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
            }
        }
    }

    @Override
    public void updatePlayerState() {

    }
}