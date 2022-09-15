package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.BottomDialogSheets.PostOptionsDialogFragment;
import com.app.livewave.BottomDialogSheets.ReactionDetailFragment;
import com.app.livewave.BottomDialogSheets.ReportDialogSheet;
import com.app.livewave.BottomDialogSheets.ReportOptionsDialogSheet;
import com.app.livewave.BottomDialogSheets.ShareBottomSheet;
import com.app.livewave.DialogSheets.ExitDialogueSheet;
import com.app.livewave.DialogSheets.report_content_list;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.VideoPlayerActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.MessageInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.onClickInterfaceForEditPost;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.utils.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import org.greenrobot.eventbus.EventBus;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.NEWS_FEED;
import static com.app.livewave.utils.Constants.POST_MODEL;
import static com.app.livewave.utils.Constants.URL;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.VIDEO_SHARE_COUNT;
import static com.app.livewave.utils.Constants.VIDEO_VIEW_COUNT;
import static com.app.livewave.utils.setDescriptionsDataUtils.extractUrls;
import static com.app.livewave.utils.setDescriptionsDataUtils.isContainLink;

//import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.constants.ScaleTypes;
//import com.denzcoskun.imageslider.interfaces.ItemClickListener;
//import com.denzcoskun.imageslider.models.SlideModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    List<PostModel> postList = new ArrayList<>();
    Context context;
    UserModel userModel;
    RecyclerView recyclerView;
    private String from;
    private int userId;
    final int Type_Text = 0, Type_Attachment = 1, Type_Shared = 2, Type_Video = 3;
    private onClickInterfaceForEditPost onClickInterfaceForEditPost;
    Player previousPlayer = null;
    int previousVisibleItem = -1;
    int count;

    ImageSliderAdapter imageAdapter;

    public PostAdapter(Context context, int userId, String mFrom, RecyclerView recycler) {
        this.context = context;
        this.postList = new ArrayList<>();
        this.userId = userId;
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
        this.from = mFrom;
        this.recyclerView = recycler;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Type_Text) {
            return new PostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_text, parent, false));
        } else if (viewType == Type_Attachment) {
            return new PostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_attachment, parent, false));
        } else if (viewType == Type_Video) {
            return new PostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_video, parent, false));
        } else {
            return new PostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_shared, parent, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {

        if (postList.get(position).getUserId() == postList.get(position).getProfileId())
            holder.profile.setVisibility(View.GONE);
        else
            holder.profile.setVisibility(View.VISIBLE);

        holder.txt_name.setText(postList.get(position).getUser().getName());
        BaseUtils.setVerifiedAccount(postList.get(position).getUser().getVerified(), holder.txt_name);
        holder.txt_profile_name.setText(postList.get(position).getProfile().getName());
        BaseUtils.setVerifiedAccount(postList.get(position).getProfile().getVerified(), holder.txt_profile_name);
        Glide.with(context).load(getUrlforPicture(postList.get(position).getUser().getPhoto())).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        if (postList.get(position).getTotalReactions() > 0) {
            holder.txt_reaction.setVisibility(View.VISIBLE);
            if (postList.get(position).getMyReaction() != 0) {
                int totalReactions = postList.get(position).getTotalReactions() - 1;
                if (totalReactions > 0)
                    holder.txt_reaction.setText("You and " + totalReactions + " other reacted to this post");
                else
                    holder.txt_reaction.setText("You have reacted to this post");
            } else {
                holder.txt_reaction.setText(postList.get(position).getTotalReactions() + " person reacted to this post");
            }
        } else {
            holder.txt_reaction.setVisibility(View.GONE);
        }
        if (postList.get(position).getTotalComments() > 0) {
            holder.txt_comments.setVisibility(View.VISIBLE);
            holder.txt_comments.setText(postList.get(position).getTotalComments() + " comments..");
        } else {
            holder.txt_comments.setVisibility(View.GONE);
        }

        //        holder.txt_date.setText(DateUtils.getRelativeTimeSpanString(BaseUtils.getDate(postList.get(position).getCreatedAt())));
        holder.txt_date.setText(BaseUtils.convertFromUTCTime(postList.get(position).getCreatedAt()));
        if (postList.get(position).getUserId() != userModel.getId() && (userModel.getId() != userId || from.equals(NEWS_FEED)))
            holder.iv_post_option.setVisibility(View.VISIBLE);
        else
            holder.iv_post_option.setVisibility(View.VISIBLE);

        if (postList.get(position).getDescription() != null) {
            holder.txt_des.setVisibility(View.VISIBLE);
            holder.txt_des.setText(postList.get(position).getDescription());
        } else
            holder.txt_des.setVisibility(View.GONE);
        initReactionsListener(position, holder);
        switch (holder.getItemViewType()) {

            case Type_Text:
                setImagePreview(context, postList.get(position), holder, false);
                holder.layout_preview.setOnClickListener(v -> {
                    String link = Arrays.toString(extractLinks(postList.get(position).getDescription())).replace("[", "").replace("]", "");
                    if (link.contains("pl/post")) {
                        String postId = link.substring(link.lastIndexOf("/") + 1);
                        getPostFromApiId(postId);
                    } else if (link.contains("http")) {
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } else {
                        Uri uri = Uri.parse("https://" + link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                break;
            case Type_Shared:
                holder.iv_re_share.setVisibility(View.GONE);
                PostModel sharedPostModel = postList.get(position).getSharedPost();
                if (sharedPostModel.getUserId() == sharedPostModel.getProfileId())
                    holder.shared_profile.setVisibility(View.GONE);
                else
                    holder.shared_profile.setVisibility(View.VISIBLE);
                if(sharedPostModel.getUser() !=null){
                    holder.txt_shared_name.setText(sharedPostModel.getUser().getName());
                    BaseUtils.setVerifiedAccount(sharedPostModel.getUser().getVerified(), holder.txt_shared_name);

                    Glide.with(context).load(getUrlforPicture(sharedPostModel.getUser().getPhoto())).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile_place_holder).into(holder.img_shared_profile);
                }
                if(sharedPostModel.getProfile() !=null){
                    holder.txt_shared_profile_name.setText(sharedPostModel.getProfile().getName());
                    BaseUtils.setVerifiedAccount(sharedPostModel.getProfile().getVerified(), holder.txt_shared_profile_name);
                }
                holder.txt_shared_des.setText(sharedPostModel.getDescription());
                if (sharedPostModel.getAttachments().size() == 0)
                    setImagePreview(context, sharedPostModel, holder, true);
                holder.layout_shared_preview.setOnClickListener(v -> {
                    String link = Arrays.toString(extractLinks(sharedPostModel.getDescription())).replace("[", "").replace("]", "");
                    if (link.contains("pl/post")) {
                        String postId = link.substring(link.lastIndexOf("/") + 1);
                        getPostFromApiId(postId);
                    } else if (link.contains("http")) {
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } else {
                        Uri uri = Uri.parse("https://" + link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
                setAttachment(sharedPostModel, holder, true);
                checkPostPrivacy(holder, sharedPostModel, true);
                holder.rl_shared_blur_paid_view.setOnClickListener(v -> {
                    Intent intent = new Intent(context, WebviewActivity.class);
                    intent.putExtra("id", "post_id=" + sharedPostModel.getId());
                    intent.putExtra("type", "post");
                    intent.putExtra("intent_type", "6");
                    context.startActivity(intent);

//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", "post_id=" + sharedPostModel.getId());
//                    bundle.putString("type", "post");
//                    bundle.putString("intent_type", "6");
//                    ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
                });
                holder.iv_shared_play_video.setOnClickListener(v -> {

                    addViewCount(postList.get(position).getId()+"",position);

                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    String share_Count = postList.get(position).getTotalShares() + "";
                    intent.putExtra(VIDEO_SHARE_COUNT,share_Count);
                    String view_Count = postList.get(position).getTotalViews() + "";
                    intent.putExtra(VIDEO_VIEW_COUNT,view_Count);
                    intent.putExtra(URL, sharedPostModel.getAttachments().get(0).getPath());
                    context.startActivity(intent);
                });
                holder.img_shared_profile.setOnClickListener(v -> {
                    if (postList.get(position).getSharedPost().getUserId() != userId)
                        ((HomeActivity)context).openUserProfile(postList.get(position).getSharedPost().getUser().getId().toString());
//                        BaseUtils.openUserProfile(postList.get(position).getSharedPost().getUser().getId().toString(), context);
                });
                holder.txt_shared_profile_name.setOnClickListener(v -> {
                    if (postList.get(position).getSharedPost().getProfileId() != userId)
                        ((HomeActivity)context).openUserProfile(postList.get(position).getSharedPost().getUser().getId().toString());
//                        BaseUtils.openUserProfile(postList.get(position).getSharedPost().getProfile().getId().toString(), context);
                });
                holder.txt_shared_name.setOnClickListener(v -> {
                    if (postList.get(position).getSharedPost().getUserId() != userId)
                        ((HomeActivity)context).openUserProfile(postList.get(position).getSharedPost().getUser().getId().toString());
//                        BaseUtils.openUserProfile(postList.get(position).getSharedPost().getUser().getId().toString(), context);
                });
//                holder.img_shared_picture.setOnClickListener(v -> {
//                    if (sharedPostModel.getAttachments().size() > 0) {
//                        if (sharedPostModel.getAttachments().get(0).getType().equals("video")) {
//                            Intent intent = new Intent(context, VideoPlayerActivity.class);
//                            intent.putExtra(URL, sharedPostModel.getAttachments().get(0).getPath());
//                            context.startActivity(intent);
//                        } else if (sharedPostModel.getAttachments().get(0).getType().equals("image")) {
//                            ArrayList<String> uriString = new ArrayList<>();
//                            uriString.add(getUrlforPicture(sharedPostModel.getAttachments().get(0).getPath()));
//                            new ImageViewer.Builder(context, uriString)
//                                    .setStartPosition(0)
//                                    .show();
//                        }
//                    } else {
////                    String link = Arrays.toString(extractLinks(holder.txt_des.getText().toString())).replace("[", "").replace("]", "");
//                        String link = Arrays.toString(extractLinks(sharedPostModel.getDescription())).replace("[", "").replace("]", "");
//                        if (link.contains("pl/post")) {
//                            String postId = link.substring(link.lastIndexOf("/") + 1);
//                            getPostFromApiId(postId);
//                        } else {
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(link));
//                            context.startActivity(i);
//                        }
//                    }
//
//                });
                if (Constants.APPENV == ENV.review) {
                    holder.rl_shared_blur_paid_view.setVisibility(View.GONE);
                    holder.iv_paid.setVisibility(View.GONE);
                }
                break;
            case Type_Attachment:

                setAttachment(postList.get(position), holder, false);
                checkPostPrivacy(holder, postList.get(position), false);
                holder.rl_blur_paid_view.setOnClickListener(v -> {
                    Intent intent = new Intent(context, WebviewActivity.class);
                    intent.putExtra("id", "post_id=" + postList.get(position).getId());
                    intent.putExtra("type", "post");
                    intent.putExtra("intent_type", "6");
                    context.startActivity(intent);

//                    Bundle bundle = new Bundle();
//                    bundle.putString("id", "post_id=" + postList.get(position).getId());
//                    bundle.putString("type", "post");
//                    bundle.putString("intent_type", "6");
//                    ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
                });
                holder.iv_play_video.setOnClickListener(v -> {

                    addViewCount(postList.get(position).getId()+"",position);

                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    System.out.println(postList.get(position).getTotalShares());
                    String share_Count = postList.get(position).getTotalShares() + "";
                    intent.putExtra(VIDEO_SHARE_COUNT,share_Count);
                    String view_Count = postList.get(position).getTotalViews() + "";
                    intent.putExtra(VIDEO_VIEW_COUNT,view_Count);
                    intent.putExtra(URL, postList.get(position).getAttachments().get(0).getPath());
                    context.startActivity(intent);
                });
//                holder.img_picture.setOnClickListener(v -> {
//                    if (postList.get(position).getAttachments().size() > 0) {
//                        if (postList.get(position).getAttachments().get(0).getType().equals("video")) {
//                            Intent intent = new Intent(context, VideoPlayerActivity.class);
//                            intent.putExtra(URL, postList.get(position).getAttachments().get(0).getPath());
//                            context.startActivity(intent);
//                        } else if (postList.get(position).getAttachments().get(0).getType().equals("image")) {
//                    /*Intent fullImageIntent = new Intent(context, FullScreenImageViewActivity.class);
//                    ArrayList<String> uriString = new ArrayList<>();
//
//                    uriString.add(getUrlforPicture(postList.get(position).getAttachments().get(0).getPath()));
//                    fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString);
//                    fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, position);
//                    context.startActivity(fullImageIntent);*/
//
//                            ArrayList<String> uriString = new ArrayList<>();
//                            uriString.add(getUrlforPicture(postList.get(position).getAttachments().get(0).getPath()));
//                            new ImageViewer.Builder(context, uriString)
//                                    .setStartPosition(0)
//                                    .show();
//                        }
//                    } else {
////                    String link = Arrays.toString(extractLinks(holder.txt_des.getText().toString())).replace("[", "").replace("]", "");
//                        String link = Arrays.toString(extractLinks(postList.get(position).getDescription())).replace("[", "").replace("]", "");
//                        if (link.contains("pl/post")) {
//                            String postId = link.substring(link.lastIndexOf("/") + 1);
//                            getPostFromApiId(postId);
//                        } else {
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(link));
//                            context.startActivity(i);
//                        }
//
//                    }
//
//                });
                if (Constants.APPENV == ENV.review) {
                    holder.rl_blur_paid_view.setVisibility(View.GONE);
                    holder.iv_paid.setVisibility(View.GONE);
                }
                break;

            case Type_Video: {
                setVideo(postList.get(position), holder, position);
                break;
            }
//            case Type_Image:
//                checkPostPrivacy(holder, postList.get(position), false);
//                holder.rl_blur_paid_view.setOnClickListener(v -> {
//                    Intent intent = new Intent(context, WebviewActivity.class);
//                    intent.putExtra("id", "post_id=" + postList.get(position).getId());
//                    intent.putExtra("type", "post");
//                    intent.putExtra("intent_type", "6");
//                    context.startActivity(intent);
//                });
////                ArrayList<Bitmap> imageList = new ArrayList();
//                for (int i = 0; i <postList.get(position).getAttachments().size(); i++){
//                    try {
//                        java.net.URL url = new URL(getUrlforPicture(postList.get(position).getAttachments().get(i).getPath()));
//                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
////                        imageList.add(image);
//                        holder.img_collection_view.addImage(image);
//                    } catch(IOException e) {
//                        System.out.println(e);
//                    }
//                }
////                holder.img_collection_view.addImage(imageList);
//                break;
        }
        holder.iv_share.setOnClickListener(v -> {

            addCount(postList.get(position).getSharingID(),holder.txt_share);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Post");
            String app_url;
            app_url = Constants.BASE_URL + "pl/post/" + postList.get(position).getSharingID();
            shareIntent.putExtra(Intent.EXTRA_TEXT, app_url);
            context.startActivity(Intent.createChooser(shareIntent, "Share Post"));

        });
        count = postList.get(position).getTotalShares();
        if (count > 0) {
            holder.txt_share.setVisibility(View.VISIBLE);

            holder.txt_share.setText(count + " shares..");
        } else {
            holder.txt_share.setVisibility(View.GONE);
        }

        holder.img_profile.setOnClickListener(v -> {
            if (postList.get(position).getUserId() != userId)
                ((HomeActivity)context).openUserProfile(postList.get(position).getUser().getId().toString());
//                BaseUtils.openUserProfile(postList.get(position).getUser().getId().toString(), context);
        });
        holder.txt_profile_name.setOnClickListener(v -> {
            if (postList.get(position).getProfileId() != userId)
                ((HomeActivity)context).openUserProfile(postList.get(position).getProfile().getId().toString());
//                BaseUtils.openUserProfile(postList.get(position).getProfile().getId().toString(), context);
        });
        holder.txt_name.setOnClickListener(v -> {
            if (postList.get(position).getUserId() != userId)
                ((HomeActivity)context).openUserProfile(postList.get(position).getUser().getId().toString());
//                BaseUtils.openUserProfile(postList.get(position).getUser().getId().toString(), context);
        });

        holder.iv_coment.setOnClickListener(v -> {
//                PostDetailFragment.addIntent(context, postList.get(position), userId)
                    Bundle bundle = new Bundle();
                    bundle.putString(POST_MODEL, new Gson().toJson(postList.get(position)));
                    bundle.putInt(USER_ID, userId);
                    bundle.putString(HEADER_TITLE, " ");
                    ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
                }
        );
        holder.itemView.setOnClickListener(v ->
                {
//               PostDetailFragment.addIntent(context, postList.get(position), userId)
                    Bundle bundle = new Bundle();
                    bundle.putString(POST_MODEL, new Gson().toJson(postList.get(position)));
                    bundle.putInt(USER_ID, userId);
                    bundle.putString(HEADER_TITLE, " ");
                    ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
                }
        );
        holder.iv_post_option.setOnClickListener(v -> {
            if (postList.get(position).getUserId() != userModel.getId() && (userModel.getId() != userId || from.equals(NEWS_FEED))) {
                System.out.println("Options Clicked on Other Posts");

                ReportOptionsDialogSheet dialogSheet = new ReportOptionsDialogSheet(userModel, userId, NEWS_FEED);
                dialogSheet.addListener(new PostOptionInterface() {
                    @Override
                    public void pressed(String pressedButton) {
                        if (pressedButton.equals("Report")) {
                            BaseUtils.showAlertDialog("Report", "Are you sure, you want to report this post ?", context, new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {
                                    if (positive) {
                                        reportDialog();
                                    }
                                }
                            });


                        }
                        dialogSheet.dismiss();

                    }

                    private void reportDialog() {

                        report_content_list report_content_list = new report_content_list();
                        FragmentManager fmm = ((FragmentActivity) context).getSupportFragmentManager();
                        report_content_list.show(fmm, "report content");

                        report_content_list.addListener(new PostOptionInterface() {
                            @Override
                            public void pressed(String PressedButton) {
                                if (PressedButton == "Report") {
                                    HashMap<String, Object> hashMap = new HashMap();
                                    hashMap.put("reported_id", postList.get(position).getProfile().getId());
                                    hashMap.put("message", "Content report");
                                    hashMap.put("title", "Report");
                                    ApiManager.apiCall(ApiClient.getInstance().getInterface().report(hashMap), context, new ApiResponseHandler<UserModel>() {
                                        @Override
                                        public void onSuccess(Response<ApiResponse<UserModel>> data) {
//                            BaseUtils.showToast(UserProfileActivity.this, "Reported!");
                                            report_content_list.dismiss();
                                            postList.remove(position);
                                            notifyDataSetChanged();
                                            BaseUtils.showLottieDialog(context, "Reported!", R.raw.check, new DialogBtnClickInterface() {
                                                @Override
                                                public void onClick(boolean positive) {

                                                }
                                            });
                                        }
                                    });
                                } else {
                                    report_content_list.dismiss();
                                }
                            }
                        });

//                        ReportDialogSheet enterAmountDialogSheet = new ReportDialogSheet(new MessageInterface() {
//                            @Override
//                            public void IAmountEnter(String mAmount) {
//                                if (mAmount != null) {
//                                    HashMap<String, Object> hashMap = new HashMap();
//                                    hashMap.put("reported_id", postList.get(position).getProfile().getId());
//                                    hashMap.put("message", mAmount);
//                                    hashMap.put("title", "Report");
//                                    ApiManager.apiCall(ApiClient.getInstance().getInterface().report(hashMap), context, new ApiResponseHandler<UserModel>() {
//                                        @Override
//                                        public void onSuccess(Response<ApiResponse<UserModel>> data) {
////                            BaseUtils.showToast(UserProfileActivity.this, "Reported!");
//                                            BaseUtils.showLottieDialog(context, "Reported!", R.raw.check, new DialogBtnClickInterface() {
//                                                @Override
//                                                public void onClick(boolean positive) {
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                } else {
//
//                                }
//                            }
//                        });
//                        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
//                        enterAmountDialogSheet.show(fm, "amount");
                    }
                });
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                dialogSheet.show(fm, "myDialog");

            } else {

                PostOptionsDialogFragment postOptionsDialogFragment = new PostOptionsDialogFragment(postList.get(position), userModel.getId(), userId);
                postOptionsDialogFragment.addListener(pressedButton -> {
                    if (pressedButton.equals(context.getString(R.string.delete))) {
                        BaseUtils.showAlertDialog("Alert", "Are you sure want to delete this post?", context, positive -> {
                            if (positive) {
                                deletePost(position);
                            }
                        });
                    } else if (pressedButton.equals(context.getString(R.string.edit))) {
                        if (onClickInterfaceForEditPost != null) {
                            onClickInterfaceForEditPost.onClickEdit(postList.get(position));
                        }
                    }  //add option if any

                    postOptionsDialogFragment.dismiss();

                });
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                postOptionsDialogFragment.show(fragmentManager, "Post Option");
            }
        });
        holder.txt_reaction.setOnClickListener(v -> {
            ReactionDetailFragment reactionDetailFragment = new ReactionDetailFragment(String.valueOf(postList.get(position).getId()),"0");
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            reactionDetailFragment.show(fragmentManager, "Reactions");
        });
        holder.iv_re_share.setOnClickListener(v -> {
            ShareBottomSheet shareBottomSheet = new ShareBottomSheet(String.valueOf(postList.get(position).getId()));
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            shareBottomSheet.show(fragmentManager, "Share");

        });
        holder.txt_des.setOnHashtagClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                Intent intent = new Intent(context, HashtagFragment.class);
//                intent.putExtra(Constants.HASH_TAG, text.toString());
//                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HASH_TAG, text.toString());
                bundle.putString(HEADER_TITLE, text.toString());
                ((HomeActivity) context).loadFragment(R.string.tag_hashtag, bundle);
            }
        });
        holder.txt_des.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                if (text.toString().contains("pl/post")) {
                    String postId = text.toString().substring(text.toString().lastIndexOf("/") + 1);
                    getPostFromApiId(postId);

                } else if (text.toString().contains("http")) {
                    Uri uri = Uri.parse(text.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } else {
                    Uri uri = Uri.parse("https://" + text.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            }
        });
        holder.txt_des.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                ((HomeActivity) context).openUserProfile(text.toString());
//                BaseUtils.openUserProfile(text.toString(), context);
//                for (int i = 0; i < postList.get(position).getTagsData().size(); i++){
//                    if (postList.get(position).getTagsData().get(i).getName().equals(text.toString())){
//                        BaseUtils.openUserProfile(Integer.parseInt(postList.get(position).getTagsData().get(i).getId()), context);
//                    }
//                }
            }
        });
    }

    private void addViewCount(String trackId,int position) {
        System.out.println(trackId);
        System.out.println(userId);

        ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToVideoPostForViews(userId,trackId), context, new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println("Post View count added");
                System.out.println(data.body().getMessage());
                if (data.body().getMessage().equals("waves count updated Successfully")) {
                    postList.get(position).setTotalViews(postList.get(position).getTotalViews() + 1);
                }
            }
        });

    }

    private void addCount(String trackId, TextView txt_share) {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToPostForShare(userId,trackId), context, new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println("Post Share count added");
                System.out.println(data.body().getMessage());
                if (data.body().getMessage().equals("post count updated Successfully")) {
                    count = count + 1;
                }
            }
        });
    }


    private void RemoveItem(int position) {
        PostModel tempPostData = postList.get(position);
        postList.remove(position);
    }

    private void setImagePreview(Context context, PostModel postModel, MyViewHolder holder, boolean isShared) {
        if (postModel == null || context == null)
            return;
        if (postModel.getDescription() != null && !postModel.getDescription().equals("")) {
            if (isContainLink(postModel.getDescription().toLowerCase())) {
                if (isShared)
                    holder.layout_shared_preview.setVisibility(View.VISIBLE);
                else
                    holder.layout_preview.setVisibility(View.VISIBLE);

                try {
                    String url = extractUrls(postModel.getDescription());
                    if (!url.equalsIgnoreCase("")) {
                        if (!url.contains("@")) {
                            RichPreview richPreview = new RichPreview(new ResponseListener() {
                                @Override
                                public void onData(MetaData metaData) {
                                    if (isShared) {
                                        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(metaData.getImageurl())).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img_shared_preview_image);
                                        holder.txt_shared_preview_title.setText(metaData.getTitle());
                                        holder.txt_shared_preview_des.setText(metaData.getUrl());
                                    } else {
                                        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(metaData.getImageurl())).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img_preview_image);
                                        holder.txt_preview_title.setText(metaData.getTitle());
                                        holder.txt_preview_des.setText(metaData.getUrl());
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                }
                            });
                            if (url.contains("http"))
                                richPreview.getPreview(url);
                            else
                                richPreview.getPreview("https://" + url);
                        } else {
                            if (isShared)
                                holder.layout_shared_preview.setVisibility(View.GONE);
                            else
                                holder.layout_preview.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception exception) {

                }
            } else {
                if (isShared)
                    holder.layout_shared_preview.setVisibility(View.GONE);
                else
                    holder.layout_preview.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setList(List<PostModel> postList) {
        this.postList = new ArrayList<>();
        this.postList = postList;
        notifyDataSetChanged();
    }

    public void setUpOnclickInterfaceForEditPost(onClickInterfaceForEditPost onClickInterfaceForEditPost) {
        this.onClickInterfaceForEditPost = onClickInterfaceForEditPost;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_shared_name, txt_shared_profile_name, txt_shared_preview_title, txt_shared_preview_des, txt_shared_content, tv_shared_pay_to_unlock, txt_date, txt_comments,txt_share, txt_reaction, txt_profile_name, tv_pay_to_unlock, txt_preview_title, txt_preview_des;
        CircleImageView img_profile, img_shared_profile;
        ImageView iv_paid, iv_play_video, img_shared_picture, img_shared_preview_image, iv_shared_play_video, img_reaction, iv_coment, iv_post_option, iv_share, img_preview_image, iv_re_share;
        LinearLayout profile, ll_privacy_item, shared_profile;
        RelativeLayout rl_blur_paid_view, rl_shared_blur_paid_view, rl_image;
        SocialTextView txt_des, txt_shared_des;
        LinearLayout layout_preview, layout_shared_preview,layout_id_postitem;
        PlayerView playerView;
        //        MultiImageView img_collection_view;
        //        RecyclerView rv_images;
        ViewPager image_slider;
        TabLayout tabLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_shared_name = itemView.findViewById(R.id.txt_shared_name);
            layout_id_postitem = itemView.findViewById(R.id.layout_id_postitem);
            txt_shared_profile_name = itemView.findViewById(R.id.txt_shared_profile_name);
            txt_shared_content = itemView.findViewById(R.id.txt_shared_content);
            tv_shared_pay_to_unlock = itemView.findViewById(R.id.tv_shared_pay_to_unlock);
            img_shared_picture = itemView.findViewById(R.id.img_shared_picture);
            iv_shared_play_video = itemView.findViewById(R.id.iv_shared_play_video);
            img_shared_profile = itemView.findViewById(R.id.img_shared_profile);
            shared_profile = itemView.findViewById(R.id.shared_profile);
            rl_shared_blur_paid_view = itemView.findViewById(R.id.rl_shared_blur_paid_view);
            txt_shared_des = itemView.findViewById(R.id.txt_shared_des);
            txt_shared_preview_title = itemView.findViewById(R.id.txt_shared_preview_title);
            txt_shared_preview_des = itemView.findViewById(R.id.txt_shared_preview_des);
            img_shared_preview_image = itemView.findViewById(R.id.img_shared_preview_image);
            layout_shared_preview = itemView.findViewById(R.id.layout_shared_preview);
            image_slider = itemView.findViewById(R.id.image_slider);

            profile = itemView.findViewById(R.id.profile);
            rl_image = itemView.findViewById(R.id.rl_image);
            layout_preview = itemView.findViewById(R.id.layout_preview);
            txt_preview_title = itemView.findViewById(R.id.txt_preview_title);
            txt_preview_des = itemView.findViewById(R.id.txt_preview_des);
            img_preview_image = itemView.findViewById(R.id.img_preview_image);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_profile_name = itemView.findViewById(R.id.txt_profile_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_des = itemView.findViewById(R.id.txt_des);
            txt_comments = itemView.findViewById(R.id.txt_comments);
            txt_share = itemView.findViewById(R.id.txt_share);
            txt_reaction = itemView.findViewById(R.id.txt_reaction);
            img_profile = itemView.findViewById(R.id.img_profile);
//            img_picture = itemView.findViewById(R.id.img_picture);
            img_reaction = itemView.findViewById(R.id.img_reaction);
            iv_coment = itemView.findViewById(R.id.iv_coment);
            iv_post_option = itemView.findViewById(R.id.iv_post_option);
            ll_privacy_item = itemView.findViewById(R.id.ll_privacy_item);
            rl_blur_paid_view = itemView.findViewById(R.id.rl_blur_paid_view);
            tv_pay_to_unlock = itemView.findViewById(R.id.tv_pay_to_unlock);
            iv_paid = itemView.findViewById(R.id.iv_paid);
            iv_share = itemView.findViewById(R.id.iv_share);
            iv_play_video = itemView.findViewById(R.id.iv_play_video);
            iv_re_share = itemView.findViewById(R.id.iv_re_share);
            tabLayout = itemView.findViewById(R.id.tabDots);
            playerView = itemView.findViewById(R.id.exoplayerView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (postList.get(position).getAttachments().size() > 0) {
//            if(postList.get(position).getAttachments().get(0).getType().equals("image")){
            return Type_Attachment;
//            }else{
//                return Type_Video;
//            }
        } else if (postList.get(position).getShared_id() != null) {
            return Type_Shared;
        } else {
            return Type_Text;
        }
    }

    private void checkPostPrivacy(MyViewHolder holder, PostModel postModel, boolean isShared) {
        if (postModel.getPaid().equals("1") && postModel.getIsPaid() == 0 && postModel.getUserId() != userModel.getId()) {
            if (isShared) {
                holder.rl_shared_blur_paid_view.setVisibility(View.VISIBLE);
                holder.tv_shared_pay_to_unlock.setText(context.getString(R.string.pay__to_view_this_post, String.valueOf((int) postModel.getAmount())));
            } else {
                holder.rl_blur_paid_view.setVisibility(View.VISIBLE);
                holder.tv_pay_to_unlock.setText(context.getString(R.string.pay__to_view_this_post, String.valueOf((int) postModel.getAmount())));
            }
            holder.ll_privacy_item.setVisibility(View.GONE);

        } else {
            if (isShared) {
                holder.rl_shared_blur_paid_view.setVisibility(View.GONE);
            } else {
                holder.rl_blur_paid_view.setVisibility(View.GONE);
            }
            holder.ll_privacy_item.setVisibility(View.VISIBLE);
        }
        if (postModel.getPaid().equals("1")) {
            holder.iv_paid.setVisibility(View.VISIBLE);
        } else
            holder.iv_paid.setVisibility(View.GONE);
    }

    private void setVideo(PostModel postModel, MyViewHolder holder, int position) {
        String uri = postModel.getAttachments().get(0).getPath();
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
        holder.playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(String.valueOf(uri));
        player.setMediaItem(mediaItem);
        player.prepare();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastElementPosition = lManager.findLastCompletelyVisibleItemPosition();
                if (position == lastElementPosition) {
                    if (previousPlayer == null) {
                        player.prepare();
                        player.play();
                        previousPlayer = player;
                        previousVisibleItem = lastElementPosition;
                    } else {
                        if (previousVisibleItem != lastElementPosition) {
                            previousPlayer.stop();
                            player.prepare();
                            player.play();
                            previousPlayer = player;
                            previousVisibleItem = lastElementPosition;
                        }
                    }
                }

                if (lastElementPosition != previousVisibleItem) {
                    if (previousPlayer != null) {
                        previousPlayer.stop();
                        previousPlayer = null;
                        previousVisibleItem = -1;
                    }
                }
            }
        });

    }

    private void setAttachment(PostModel postModel, MyViewHolder holder, boolean isShared) {
        holder.image_slider.setVisibility(View.GONE);
        if (isShared)
            holder.rl_image.setVisibility(View.GONE);
        if (postModel.getAttachments().size() > 0) {
            holder.image_slider.setVisibility(View.VISIBLE);
            if (isShared)
                holder.rl_image.setVisibility(View.VISIBLE);
//            List<SlideModel> slideModelList = new ArrayList<>();
            ArrayList<String> slideModelList = new ArrayList<>();
            for (int i = 0; i < postModel.getAttachments().size(); i++) {
                if (postModel.getAttachments().get(i).getDuration() == 0.0) {
                    slideModelList.add(BaseUtils.getUrlforPicture(postModel.getAttachments().get(i).getPath()));
                }else {
                    System.out.println("DURATIONDURATION");
                    System.out.println(postModel.getAttachments().get(i).getDuration());
                    System.out.println(postModel.getAttachments().get(i).getPath());
                    System.out.println(postModel.getAttachments().get(i).getExtension());
                }

            }
            imageAdapter = new ImageSliderAdapter(context);
            holder.image_slider.setAdapter(imageAdapter);
            if (slideModelList.size() > 1)
                holder.tabLayout.setupWithViewPager(holder.image_slider, true);
            imageAdapter.setImages(slideModelList, true);
        }
        //TODO
        holder.image_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (postModel.getAttachments().get(i).getType().equals("image")){
//                    ArrayList<String> uriString = new ArrayList<>();
//                    uriString.add(getUrlforPicture(postModel.getAttachments().get(i).getPath()));
//
//                }
            }
        });
//        holder.image_slider.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onItemSelected(int i) {
//                if (postModel.getAttachments().get(i).getType().equals("image")){
//                    ArrayList<String> uriString = new ArrayList<>();
//                    uriString.add(getUrlforPicture(postModel.getAttachments().get(i).getPath()));
//                    new ImageViewer.Builder(context, uriString)
//                            .setStartPosition(0)
//                            .show();
//                }
//
//            }
//        });

//        if (postModel.getAttachments().size() == 1) {
//            gridLayoutManager = new GridLayoutManager(context, 1);
//        } else {
//            gridLayoutManager = new GridLayoutManager(context, 2);
//        }
//        holder.rv_images.setLayoutManager(gridLayoutManager);
//        imageAdapter = new ImageAdapter(context);
//        holder.rv_images.setAdapter(imageAdapter);
//        imageAdapter.setList(postModel.getAttachments());

        if (isShared) {
            holder.iv_shared_play_video.setVisibility(View.GONE);
//            holder.img_shared_picture.setVisibility(View.GONE);
        } else {
            holder.iv_play_video.setVisibility(View.GONE);
//            holder.img_picture.setVisibility(View.GONE);
        }
        if (postModel.getAttachments().size() > 0) {
//            if (isShared)
//                holder.img_shared_picture.setVisibility(View.VISIBLE);
//            else
//                holder.img_picture.setVisibility(View.VISIBLE);
            if (postModel.getAttachments().get(0).getType().equals("image") && postModel.getAttachments().get(0).getThumbnail().equals(null)) {
                System.out.println("Images Test");
                System.out.println(postModel.getAttachments().get(0).getPath());
//                if (isShared)
//                    Glide.with(context).load(getUrlforPicture(postModel.getAttachments().get(0).getPath())).placeholder(R.drawable.cover_place_holder).into(holder.img_shared_picture);
//                else
//                    Glide.with(context).load(getUrlforPicture(postModel.getAttachments().get(0).getPath())).placeholder(R.drawable.cover_place_holder).into(holder.img_picture);
            } else if (postModel.getAttachments().get(0).getType().equals("video")) {
                String thumbnail;
                if (postModel.getThumbnail() == null) {
                    thumbnail = getUrlforPicture(postModel.getAttachments().get(0).getThumbnail());
                } else {
                    thumbnail = getUrlforPicture(postModel.getAttachments().get(0).getThumbnail());
                }
                if (isShared) {
                    holder.iv_shared_play_video.setVisibility(View.VISIBLE);
//                    List<SlideModel> slideModelList = new ArrayList<>();
                    ArrayList<String> slideModelList = new ArrayList<>();
                    slideModelList.add(BaseUtils.getUrlforPicture(thumbnail));

                    imageAdapter = new ImageSliderAdapter(context);
                    holder.image_slider.setAdapter(imageAdapter);
                    if (slideModelList.size() > 1)
                        holder.tabLayout.setupWithViewPager(holder.image_slider, true);
                    imageAdapter.setImages(slideModelList, false);
//                    Glide.with(context).load(thumbnail).placeholder(R.drawable.cover_place_holder).into(holder.img_shared_picture);
                } else {
                    holder.iv_play_video.setVisibility(View.VISIBLE);
                    ArrayList<String> slideModelList = new ArrayList<>();
                    slideModelList.add(BaseUtils.getUrlforPicture(thumbnail));
                    imageAdapter = new ImageSliderAdapter(context);
                    holder.image_slider.setAdapter(imageAdapter);
                    if (slideModelList.size() > 1)
                        holder.tabLayout.setupWithViewPager(holder.image_slider, true);
                    imageAdapter.setImages(slideModelList, false);
//                    Glide.with(context).load(thumbnail).placeholder(R.drawable.cover_place_holder).into(holder.img_picture);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initReactionsListener(int position, MyViewHolder holder) {
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(new int[]{
                        R.drawable.ic_happy_reply,
                        R.drawable.ic_normal_reply,
                        R.drawable.ic_sad_bold,
                        R.drawable.ic_wow_bold,
                        R.drawable.ic_anygry_bold,
                        R.drawable.ic_laughing
                }).withPopupGravity(PopupGravity.SCREEN_LEFT)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (p) -> {
            switch (p) {
                case 0:
                    reactPost(postList.get(position), 1, position);
                    break;
                case 1:
                    reactPost(postList.get(position), 2, position);
                    break;
                case 2:
                    reactPost(postList.get(position), 3, position);
                    break;
                case 3:
                    reactPost(postList.get(position), 4, position);
                    break;
                case 4:
                    reactPost(postList.get(position), 5, position);
                    break;
                case 5:
                    reactPost(postList.get(position), 6, position);
                    break;
                default:
                    break;

            }

            return true;
        });
        holder.img_reaction.setOnTouchListener((v, event) -> {
            EventBus.getDefault().postSticky(new OnTouch(true));
            popup.onTouch(v, event);
            return true;
        });
    }

    public String[] extractLinks(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            if (url.startsWith("http"))
                links.add(url);
        }

        return links.toArray(new String[0]);
    }

    private void getPostFromApiId(String postId) {

        ApiManager.apiCallWithFailure(new ApiClient().getInterface().getPostById(postId), context, new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
                assert data.body() != null;
//                PostDetailFragment.addIntent(context, data.body().getData(), userId);

                Bundle bundle = new Bundle();
                bundle.putString(POST_MODEL, new Gson().toJson(data.body().getData()));
                bundle.putInt(USER_ID, userId);
                bundle.putString(HEADER_TITLE, " ");
                ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
            }

            @Override
            public void onFailure(String failureCause) {

            }
        });
    }

    private void reactPost(PostModel postModel, int id, int position) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().react(postModel.getId(), id), context, data -> {
            assert data.body() != null;
            if (data.body().getMessage().equals("Reaction Removed Successfully")) {
                postList.get(position).setTotalReactions(postList.get(position).getTotalReactions() - 1);
                if (data.body().getData().getReactorId() == userModel.getId())
                    postList.get(position).setMyReaction(0);
            } else {
                if (postList.get(position).getMyReaction() == 0) {
                    postList.get(position).setTotalReactions(postList.get(position).getTotalReactions() + 1);
                    if (data.body().getData().getReactorId() == userModel.getId())
                        postList.get(position).setMyReaction(1);
                }

            }
            notifyDataSetChanged();
        });

    }

    private void deletePost(int position) {
        PostModel tempPostData = postList.get(position);
        postList.remove(position);
        notifyDataSetChanged();

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().deletePostById(String.valueOf(tempPostData.getId())), context, new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
            }

            @Override
            public void onFailure(String failureCause) {
                BaseUtils.showLottieDialog(context, failureCause, R.raw.invalid, positive -> {
                });
                postList.set(position, tempPostData);
                notifyDataSetChanged();
            }
        });
    }
}
