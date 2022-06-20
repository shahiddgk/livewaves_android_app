package com.app.livewave.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.livewave.BottomDialogSheets.WavesItemComment;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.LoginActivityWithWavesFeature;
import com.app.livewave.activities.RegisterActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.ReactionModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.ResponseModels.WavesModelResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.Constants.HIDE_HEADER;


public class TikTokReelsAdapter extends RecyclerView.Adapter<TikTokReelsAdapter.myviewholder>{

    List<WavesModelResponse> wavesVideoItems = new ArrayList<>();
    Context context;
    boolean isLogin;

    public TikTokReelsAdapter(List<WavesModelResponse> wavesVideoItems,Context context,boolean isLogin) {
        this.wavesVideoItems = wavesVideoItems;
        this.context = context;
        this.isLogin = isLogin;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_for_reels,parent,false);

        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.setContext(context);
        holder.setData(wavesVideoItems.get(position),isLogin);
    }

    @Override
    public int getItemCount() {
        return wavesVideoItems.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        VideoView videoView;
        TextView title,description,commentsCount,likesCount,shareCount;
        CircleImageView profile_picture;
        ImageView ic_image_play,ic_image_pause,ic_like_outline,ic_like_bold,ic_comments,ic_share,ic_follow_account;
        ProgressBar progressBar;
        LinearLayout video_play_pause_click,comments_layout,likes_layout,share_layout;
        Animation fade_out,fade_in;
        Context context;
        boolean isLogin;
        UserModel userModel;
        String likesCountNumber,commentsCountNumber,shareCountNumber;
        Integer id;
        int count;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.videoTitle);
            description = itemView.findViewById(R.id.videoDescription);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
            ic_image_play = itemView.findViewById(R.id.ic_image_play);
            ic_image_pause = itemView.findViewById(R.id.ic_image_pause);
            video_play_pause_click = itemView.findViewById(R.id.video_play_pause_click);
            ic_like_outline = itemView.findViewById(R.id.iv_like_waves_reels);
            ic_like_bold = itemView.findViewById(R.id.iv_like_waves_reels_filled);
            ic_comments = itemView.findViewById(R.id.ic_comments_waves_reels);
            ic_share = itemView.findViewById(R.id.iv_share_waves_reels);
            profile_picture = itemView.findViewById(R.id.img_profile_waves_item);
            ic_follow_account = itemView.findViewById(R.id.ic_follow_account);
            commentsCount = itemView.findViewById(R.id.tv_comment_count);
            likesCount = itemView.findViewById(R.id.tv_likes_count);
            shareCount = itemView.findViewById(R.id.tv_share_count);
            comments_layout = itemView.findViewById(R.id.comments_layout);
            likes_layout = itemView.findViewById(R.id.likes_layout);
            share_layout = itemView.findViewById(R.id.share_layout);
        }

        void setContext(Context context) {
            this.context = context;
        }

        void setData(WavesModelResponse obj,boolean Login) {
            videoView.setVideoURI(Uri.parse(obj.getPath()));
            id = Paper.book().read("CurrentUserId");
            isLogin = Login;
            //userID = Paper.book().read("CurrentUserId");
            System.out.println("CHECKING LOGIN");
            title.setText(obj.getName());
            description.setText(obj.getDescription());
           // addItemCount(obj.getPost_id());

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isLogin == false) {

                        Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } else {

                        userProfile(obj.getUser_id()) ;
                    }
                }
            });

            if (obj.getIs_following().equals("Yes")) {
                ic_follow_account.setVisibility(View.GONE);
            } else {
                ic_follow_account.setVisibility(View.VISIBLE);
            }

                if (ic_follow_account.getVisibility() == View.VISIBLE) {
                    profile_picture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (isLogin == false) {

                                Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                            } else {
                                followUnFollowUser(obj.getUser_id());
                                ic_follow_account.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {

                        profile_picture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (isLogin == false) {

                                    Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                } else {

                                    followUnFollowUser(obj.getUser_id());
                                    ic_follow_account.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                }

                if (obj.getTotal_comments() == 0 ) {
                    comments_layout.setVisibility(View.GONE);
                } else {
                    commentsCountNumber = String.valueOf(obj.getTotal_comments());
                    commentsCount.setText(commentsCountNumber);
                    comments_layout.setVisibility(View.VISIBLE);
                }

            if (obj.getTotal_reactions() == 0 ) {
                likes_layout.setVisibility(View.GONE);
            } else {
                likesCountNumber = String.valueOf(obj.getTotal_reactions());
                likesCount.setText(likesCountNumber);
                likes_layout.setVisibility(View.VISIBLE);
            }

            if (obj.getTotal_shares() == 0 ) {
                share_layout.setVisibility(View.GONE);
            } else {
                shareCountNumber = String.valueOf(obj.getTotal_shares());
                shareCount.setText(shareCountNumber);
                share_layout.setVisibility(View.VISIBLE);
            }

                if (obj.getMy_reaction() == "No" || obj.getMy_reaction().equals("No")) {
                    ic_like_bold.setVisibility(View.GONE);
                    ic_like_outline.setVisibility(View.VISIBLE);
                } else {
                    ic_like_bold.setVisibility(View.VISIBLE);
                    ic_like_outline.setVisibility(View.GONE);
                }

            ic_like_outline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isLogin == false) {

                            Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else {
                            reactOnWaves(obj,1);
                            fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                            fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            ic_like_bold.setVisibility(View.VISIBLE);
                            ic_like_bold.setAnimation(fade_in);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ic_like_outline.setVisibility(View.GONE);
                                }
                            }, 500);

                        }
                    }

            });

            ic_like_bold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if ( isLogin == false ) {

                        Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } else {
                        reactOnWaves(obj,1);
                    fade_out = AnimationUtils.loadAnimation(context,R.anim.fade_out);
                    fade_in = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                    ic_like_outline.setVisibility(View.VISIBLE);
                    ic_like_outline.setAnimation(fade_in);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ic_like_bold.setVisibility(View.GONE);
                            }
                        },500);
                    }
                }
            });

            video_play_pause_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fade_out = AnimationUtils.loadAnimation(context,R.anim.fade_out);
                    fade_in = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                    Handler handler=new Handler();
                    if (videoView.isPlaying()){
                        ic_image_pause.setVisibility(View.VISIBLE);
                        ic_image_pause.startAnimation(fade_out);
                        videoView.pause();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("PAUSE PAUSE");
                                ic_image_pause.setVisibility(View.GONE);
                            }
                        },500);
                    } else {

                        ic_image_play.setVisibility(View.VISIBLE);
                        ic_image_play.startAnimation(fade_out);
                        videoView.start();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("PLAY PLAY");

                                ic_image_play.setVisibility(View.GONE);
                            }
                        },500);
                    }
                }
            });

                ic_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isLogin == false) {

                            Intent intent = new Intent(context, LoginActivityWithWavesFeature.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else {

                            WavesItemComment wavesItemComment = new WavesItemComment();
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            Bundle bundle = new Bundle();
                            bundle.putInt("PostId", obj.getPost_id());
                            wavesItemComment.setArguments(bundle);
                            wavesItemComment.show(fragmentManager, "Comment list");
                        }
                    }
                });

            if (!obj.getPhoto().equals(null) || !obj.getPhoto().equals(" ") || obj.getPhoto() != null || obj.getPhoto() != " ") {

                if (obj.getPhoto().contains("https") || obj.getPhoto().contains("http") ) {

                    System.out.println("Profile picture PATh");
                    System.out.println(obj.getPhoto());
                    String profilePhoto = obj.getPhoto();
                    Glide.with(context).load(getUrlforPicture(profilePhoto)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_place_holder).into(profile_picture);

                }
            }
            ic_share.setOnClickListener(v -> {
                if (isLogin == false){

                } else {
                    addCount(obj.getSharingID(), obj.getTotal_shares());
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share Post");
                String app_url;
                app_url = Constants.BASE_URL + "pl/post/" + obj.getSharingID();
                intent.putExtra(Intent.EXTRA_TEXT, app_url);
                context.startActivity(Intent.createChooser(intent, "Share Post"));

            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }

        private void addCount(String postId, int total_shares) {

            ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToPostForShare(id,postId), context, new ApiResponseHandler<Object>() {
                @Override
                public void onSuccess(Response<ApiResponse<Object>> data) {
                    System.out.println("Waves count added");
                    System.out.println(data.body().getMessage());
                    if (data.body().getMessage().equals("post count updated Successfully")) {
                        shareCountNumber = String.valueOf(total_shares+1);
                        shareCount.setText(shareCountNumber);
                        share_layout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        private void reactOnWaves(WavesModelResponse mPostModel, int id) {
            if (mPostModel != null) {
                ApiManager.apiCall(ApiClient.getInstance().getInterface().react(mPostModel.getId(), id), context, new ApiResponseHandler<ReactionModel>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<ReactionModel>> data) {
                        if (data.body().getMessage().equals("Reaction Removed Successfully")) {

                            mPostModel.setTotal_reactions(mPostModel.getTotal_reactions()-1);
                            if (mPostModel.getTotal_reactions() == 0) {
                                likes_layout.setVisibility(View.GONE);
                            } else {
                                likes_layout.setVisibility(View.VISIBLE);
                                likesCountNumber = String.valueOf(mPostModel.getTotal_reactions());
                                likesCount.setText(likesCountNumber);
                            }
                        } else {
                            mPostModel.setTotal_reactions(mPostModel.getTotal_reactions() + 1);
                            likesCountNumber = String.valueOf(mPostModel.getTotal_reactions());
                            likesCount.setText(likesCountNumber);
                            likes_layout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }

        private void userProfile(int user_id) {
            System.out.println("Profile Clicked");
            String userId = String.valueOf(user_id);
            ((HomeActivity) context).openUserProfile(userId);
        }

        private void followUnFollowUser(int userId) {
//        dialog.show();
            System.out.println("Followed Clicked");

            ApiManager.apiCallWithFailure(new ApiClient().getInterface().followUnfollowUser(userId), context, new ApiResponseHandlerWithFailure<String>() {
                @Override
                public void onSuccess(Response<ApiResponse<String>> data) {
//                dialog.dismiss();

                    if (data.body().getMessage().equals("Followed Successfully")) {
                       // userModel.setFollow(1);
                        ic_follow_account.setVisibility(View.GONE);
                    } else if (data.body().getMessage().equals("unfollowed Successfully")) {
                     //   userModel.setFollow(0);
                        ic_follow_account.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(String failureCause) {
//                dialog.dismiss();

//                BaseUtils.showToast(UserProfileActivity.this, failureCause);
                    BaseUtils.showLottieDialog(context, failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
                }
            });

        }
    }


}
