package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.TimeLinePostDialogueOptions;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.CheckPermissionOnJointStream;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ParameterModels.StreamInfoModel;
import com.app.livewave.models.ResponseModels.AlertModelNew;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.EVENT_ID;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.POST_ID;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;
import static com.app.livewave.utils.Constants.URL;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.token;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder> {

    List<AlertModelNew> alertList;
    Context context;
    UserModel currentUser;
    KProgressHUD dialog;
    private CheckPermissionOnJointStream checkPermissionOnJointStream;

    public AlertAdapter(Context context) {
        this.context = context;
        this.alertList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlertAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlertAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.alert_item, parent, false));
    }
    public void setUpPermissionOnJointStream(CheckPermissionOnJointStream checkPermissionOnJointStream) {
        this.checkPermissionOnJointStream = checkPermissionOnJointStream;
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull AlertAdapter.MyViewHolder holder, int position) {
        Paper.init(context);
        currentUser = Paper.book().read(Constants.currentUser);
        holder.txt_title.setText(alertList.get(position).getSubject().trim());
//        BaseUtils.setVerifiedAccount(alertList.get(position).getu,holder.txt_title);
        holder.txt_des.setText(alertList.get(position).getBody().trim());
//        holder.txt_date.setText(DateUtils.getRelativeTimeSpanString(BaseUtils.getDate(alertList.get(position).getCreatedAt())));
        holder.txt_date.setText(BaseUtils.convertFromUTCTime(alertList.get(position).getCreatedAt()));
        Glide.with(context).load(BaseUtils.getUrlforPicture(alertList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_picture);
        holder.iv_delete_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification(position);
            }
        });
        holder.img_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertList.get(position).getSenderId() != currentUser.getId())
                    ((HomeActivity)context).openUserProfile(String.valueOf(alertList.get(position).getSenderId()));
//                    BaseUtils.openUserProfile(String.valueOf(alertList.get(position).getSenderId()), context);
            }
        });
        holder.txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertList.get(position).getSenderId() != currentUser.getId())
                    ((HomeActivity)context).openUserProfile(String.valueOf(alertList.get(position).getSenderId()));
                   // BaseUtils.openUserProfile(String.valueOf(alertList.get(position).getSenderId()), context);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertList.get(position).getType() != null) {

                    Intent intent1 = new Intent(context, HomeActivity.class);

                    if (alertList.get(position).getType().equalsIgnoreCase("follow")) {
//                        Intent intent = new Intent(context, UserProfileFragment.class);
//                        intent.putExtra(SPECIFIC_USER_ID, String.valueOf(alertList.get(position).getSenderId()));
//                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(SPECIFIC_USER_ID, String.valueOf(alertList.get(position).getSenderId()));
                        bundle.putBoolean(HIDE_HEADER,false);
                        ((HomeActivity) context).loadFragment(R.string.tag_user_profile, bundle);

                    } else if (alertList.get(position).getType().equalsIgnoreCase("post")) {
                        System.out.println("MESSAGE BODY");
                        System.out.println(alertList.get(position).getBody());
                        if (alertList.get(position).getBody().equalsIgnoreCase("has posted on your profile")) {
                            TimeLinePostDialogueOptions timeLinePostDialogueOptions = new TimeLinePostDialogueOptions(alertList.get(position).getContentID());
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            timeLinePostDialogueOptions.show(fragmentManager, "Time Line Post Option");
                            timeLinePostDialogueOptions.addListener(pressedButton -> {
                                if (pressedButton.equals(context.getString(R.string.delete))) {
                                    BaseUtils.showAlertDialog("Alert", "Are you sure want to delete this post?", context, positive -> {
                                        if (positive) {
                                            deletePost(alertList.get(position).getChildID(),position);
                                        }
                                    });
                                } else if (pressedButton.equals(context.getString(R.string.add))) {

                                    addPost(alertList.get(position).getChildID(),position);

                                }  //add option if any

                                timeLinePostDialogueOptions.dismiss();

                            });
                        } else {
                            if (alertList.get(position).getChildID() == null) {

                                context.startActivity(intent1);

                            } else {
                                System.out.println("SIMPLE POST");
                                System.out.println(alertList.get(position));
                                Bundle bundle = new Bundle();
                                bundle.putInt(USER_ID, alertList.get(position).getSenderId());
                                bundle.putString(POST_ID, String.valueOf(alertList.get(position).getChildID()));
                                bundle.putString(HEADER_TITLE, " ");
                                ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);

                            }
                        }

                    } else if (alertList.get(position).getType().equalsIgnoreCase("comment")
                            || alertList.get(position).getType().equalsIgnoreCase("post")
                            || alertList.get(position).getType().equalsIgnoreCase("comment-tag")
                            || alertList.get(position).getType().equalsIgnoreCase("reaction")
                            || alertList.get(position).getType().equalsIgnoreCase("tag")) {

                        Intent intent = new Intent(context, HomeActivity.class);


                        if ( alertList.get(position).getType().equalsIgnoreCase("tag") || alertList.get(position).getType().equalsIgnoreCase("reaction")) {
//                            intent = new Intent(context, PostDetailFragment.class);
//                            intent.putExtra(USER_ID, alertList.get(position).getSenderId());
//                            intent.putExtra(POST_ID, String.valueOf(alertList.get(position).getChildID()));
                            System.out.println("SIMPLE POST");
                            System.out.println(alertList.get(position).getChildID().toString());
                            Bundle bundle = new Bundle();
                            bundle.putInt(USER_ID, alertList.get(position).getSenderId());
                            bundle.putString(POST_ID, String.valueOf(alertList.get(position).getChildID()));
                            bundle.putString(HEADER_TITLE, " ");
                            String tagId = R.string.tag_post_detail + alertList.get(position).getUserId().toString();
                            ((HomeActivity) context).performFragmentTransaction(tagId,R.string.tag_post_detail, bundle);

                        } else if(alertList.get(position).getType().equalsIgnoreCase("post")) {

                            if (alertList.get(position).getChildID().equals(null)){
                                context.startActivity(intent);
                            }else {
                                System.out.println("SIMPLE POST");
                                System.out.println(alertList.get(position));
                                Bundle bundle = new Bundle();
                                bundle.putInt(USER_ID, alertList.get(position).getSenderId());
                                bundle.putString(POST_ID, String.valueOf(alertList.get(position).getChildID()));
                                bundle.putString(HEADER_TITLE, " ");
                                ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
                            }
                        } else if (alertList.get(position).getType().equalsIgnoreCase("comment-tag") || alertList.get(position).getType().equalsIgnoreCase("comment")) {
//                            intent = new Intent(context, PostDetailFragment.class);
//                            intent.putExtra(USER_ID, alertList.get(position).getSenderId());
//                            intent.putExtra(POST_ID, alertList.get(position).getContentID().toString());

                            Bundle bundle = new Bundle();
                            bundle.putInt(USER_ID, alertList.get(position).getSenderId());
                            bundle.putString(POST_ID, alertList.get(position).getContentID().toString());
                            bundle.putString(HEADER_TITLE, " ");
                            ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
                        }
                       // context.startActivity(intent);
                    } else if (alertList.get(position).getType().equalsIgnoreCase("stream")) {

//                        Intent intent = new Intent(context, UserProfileFragment.class);
//                        intent.putExtra(SPECIFIC_USER_ID, alertList.get(position).getSenderId());
//                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putInt(SPECIFIC_USER_ID, alertList.get(position).getSenderId());
                        bundle.putBoolean(HIDE_HEADER,false);
                        ((HomeActivity) context).loadFragment(R.string.tag_user_profile, bundle);

                    } else if (alertList.get(position).getType().equalsIgnoreCase("event")) {
//                        Intent intent = new Intent(context, EventDetailFragment.class);
//                        intent.putExtra(EVENT_ID, alertList.get(position).getContentID());
//                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(EVENT_ID, alertList.get(position).getChildID() +"");
                        ((HomeActivity) context).loadFragment(R.string.tag_events_detail, bundle);

                    } else if (alertList.get(position).getType().equalsIgnoreCase("message")) {

                        String url = "https://livewaves.app/api/chat/" + alertList.get(position).getChildID() + "?token=" + Paper.book().read(token);
                        Intent intent = new Intent(context, WebviewActivity.class);
                        intent.putExtra("intent_type", "5");
                        intent.putExtra(URL, url);
                        context.startActivity(intent);

                    } else if (alertList.get(position).getType().equalsIgnoreCase("stream-invite") || alertList.get(position).getType().equalsIgnoreCase("event-invite")) {
                        String streamId = String.valueOf(alertList.get(position).getContentID());
                        String type;
                        if (alertList.get(position).getType().equalsIgnoreCase("stream-invite")) {
                            // api/stream/join
                            type = "stream";
                        } else {
                            // api/stream/join?type=event
                            type = "event";
                        }
                        dialog = BaseUtils.progressDialog(context);
                        dialog.show();
                        getStreamById(streamId, type);
//                        Intent intent = new Intent(context, GuestPublisherActivity.class);
//                        intent.putExtra("StreamId", streamId);
//                        intent.putExtra("StreamType", type);
//                        context.startActivity(intent);
                    }
                }
            }

            private void deletePost(Integer contentID , int notificationID) {
                System.out.println("Delete Clicked");

                dialog = BaseUtils.progressDialog(context);
                dialog.show();

                ApiManager.apiCall(ApiClient.getInstance().getInterface().deletePostById(contentID.toString()), context, new ApiResponseHandler<PostModel>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<PostModel>> data) {
                        dialog.dismiss();
                        System.out.println(data);
                        deleteNotification(notificationID);
                    }

                });
                
            }

            private void addPost(int contentID, int notificationID) {
                System.out.println("ADD POST FROM TIME LINE");
                dialog = BaseUtils.progressDialog(context);
                dialog.show();
                ApiManager.apiCall(ApiClient.getInstance().getInterface().AddPostToTimeLine(contentID), context, new ApiResponseHandler<Object>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<Object>> data) {
                        System.out.println(data);
                        dialog.dismiss();
                        deleteNotification(notificationID);
                    }
                });

            }

        });
    }

    private void getStreamById(String streamId, String type) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getStream(streamId), context, new ApiResponseHandler<StreamModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                if (data.body().getData() != null){
                    String hostPlatformId = data.body().getData().getPlatformID();
                    checkFirebase(hostPlatformId, streamId, type);
                }else {
                    dialog.dismiss();
                }
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
                        if (guestId != currentUser.getId()) {
                            dialog.dismiss();
                            System.out.println("Stream is not available");
                            BaseUtils.showLottieDialog(context, "Stream is not available", R.raw.invalid, new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {

                                }
                            });
                        } else {

                            joinStream(streamId, type);
                        }
                    } else {
                        dialog.dismiss();
                        BaseUtils.showLottieDialog(context, "Stream is not available", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
                    }
                }
            }
        });
    }

    private void joinStream(String streamId, String type) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().joinStream(streamId, type), context, new ApiResponseHandler<JoinStreamModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<JoinStreamModel>> data) {
                JoinStreamModel joinStreamModel = data.body().getData();
                dialog.dismiss();
                if (checkPermissionOnJointStream != null) {
                    checkPermissionOnJointStream.onJoinStream(joinStreamModel);
                }
//                Intent intent = new Intent(context, GuestPublisherActivity.class);
//                intent.putExtra("Title", data.body().getData().getTitle());
//                intent.putExtra("StreamId", data.body().getData().getStreamId());
//                intent.putExtra("HostPlatformId", data.body().getData().getParentPlatformID());
//                intent.putExtra("PlatformId", data.body().getData().getPlatformID());
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public void setList(List<AlertModelNew> alertModelList) {
        this.alertList = new ArrayList<>();
        this.alertList = alertModelList;
        notifyDataSetChanged();
    }

    private  void  deleteNotification(int position) {
        System.out.println("NOTIFICAION DELETE");
        dialog = BaseUtils.progressDialog(context);
        dialog.show();

        AlertModelNew tempPostData = alertList.get(position);
        alertList.remove(position);
        notifyDataSetChanged();

        ApiManager.apiCall(ApiClient.getInstance().getInterface().deleteNotificationById(tempPostData.getId().toString()),context,new ApiResponseHandler<Object>(){
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println(data);

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title, txt_date, txt_des;
        CircleImageView img_picture;
        ImageView iv_delete_alert;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_des = itemView.findViewById(R.id.txt_des);
            img_picture = itemView.findViewById(R.id.img_picture);
            iv_delete_alert = itemView.findViewById(R.id.iv_delete_alert);
        }
    }
}