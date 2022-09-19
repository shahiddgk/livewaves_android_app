package com.app.livewave.adapters;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.POST_MODEL;
import static com.app.livewave.utils.Constants.USER_ID;

import android.Manifest;
import android.annotation.SuppressLint;
//import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
import android.widget.TextView;

//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.GiffImageGridListView;
import com.app.livewave.BottomDialogSheets.ReactionDetailFragment;
import com.app.livewave.BottomDialogSheets.SelectMediaForCommentSection;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.EditCommentReply;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.CommentReplyRequestModel;
import com.app.livewave.models.CommentRequestModel;
import com.app.livewave.models.IdsAndTagsListModel;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.ReplyModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.MyApplication;
import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.Holder> {
    private static final String TAG = "CommentReplyAdapter";
    private Context context;
    private List<ReplyModel> commentModelList = new ArrayList<>();
    private UserModel userModel;
    private PostModel postModel;
    private ReplyModel data = null;
    private EditCommentReply editCommentInterface;

    public void setInterface(EditCommentReply editCommentInterface) {
        this.editCommentInterface = editCommentInterface;
    }

    public CommentReplyAdapter(Context context) {
        this.context = context;
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
    }

    public void CommentReplyAdapterAddItem(ReplyModel commentRequestModel) {
        System.out.println("NEW DATA ITEM ADDED");
        System.out.println(commentRequestModel.getComment());
        commentModelList.add(0, commentRequestModel);
        notifyDataSetChanged();
    }

    public void setData(List<ReplyModel> commentModelList, PostModel mPostModel) {
        this.commentModelList = new ArrayList<>();
        this.commentModelList = commentModelList;
        this.postModel = mPostModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.comment_reply_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ReplyModel commentModel = commentModelList.get(position);

        System.out.println("Reply Name");
        System.out.println(commentModel.getUser().getName());
        String imageurl = "" + commentModel.getAttachment();
        System.out.println(imageurl);

        holder.tv_name.setText(commentModel.getUser().getName());
        holder.tv_comment_text.setText(commentModel.getComment());
        Glide.with(context).load(BaseUtils.getUrlforPicture(commentModel.getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.civ_user_img);

        initReactionsListener(position, holder);
        if (!commentModelList.get(position).getUser().getId().equals(userModel.getId())) {
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HomeActivity) context).openUserProfile(commentModelList.get(position).getUser().getId().toString());
                }
            });

        }
        if (commentModelList.get(position).getTotalReactions() > 0) {
            holder.txt_reaction.setVisibility(View.VISIBLE);
            if (commentModelList.get(position).getMyReaction() != 0) {
                int totalReactions = commentModelList.get(position).getTotalReactions() - 1;
                if (totalReactions > 0)
                    holder.txt_reaction.setText("You and " + totalReactions + " other reacted to this comment");
                else
                    holder.txt_reaction.setText("You have reacted to this comment");
            } else {
                holder.txt_reaction.setText(commentModelList.get(position).getTotalReactions() + " person reacted to this comment");
            }
        } else {
            holder.txt_reaction.setVisibility(View.GONE);
        }

        holder.txt_reaction.setOnClickListener(v -> {
            ReactionDetailFragment reactionDetailFragment = new ReactionDetailFragment("0", String.valueOf(commentModelList.get(position).getId()));
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            reactionDetailFragment.show(fragmentManager, "Reactions");
        });
        holder.edit_comment_reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " + "clicked" );
                Log.e(TAG, "onClick: " + editCommentInterface );
                if (editCommentInterface != null) {
                    editCommentInterface.editCommentReply(commentModelList.get(position),position);
                }else {
                    Log.e(TAG, "onClick: " + "null" );
                }
            }
        });


//        if (commentModel.getChildrenData().size()>0)
//        {
//            ChildrenModelForReplyComment ChildrenList = commentModel.getChildrenData().get(position);
//            if (commentModel.getChildrenData().size() > 0){
//                System.out.println("REPLAYER");
//                System.out.println(commentModel.getId());
//                System.out.println(ChildrenList.getParentId());
//                System.out.println(ChildrenList.getComment());
//                System.out.println(ChildrenList.getUser().getName());
//
//                if (commentModel.getId().toString().equals(ChildrenList.getParentId().toString()))
//                {
//                    holder.tv_reply_name.setText(ChildrenList.getUser().getName());
//                    holder.tv_reply_comment_text.setText(ChildrenList.getComment());
//                    Glide.with(context).load(BaseUtils.getUrlforPicture(ChildrenList.getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.civ_user_img_reply);
//                }
//            }
//
//        }

        if (userModel.getId().equals(commentModel.getCommenterId())) {
            holder.iv_remove.setVisibility(View.VISIBLE);
            //  holder.iv_edit.setVisibility(View.VISIBLE);

        } else if (postModel != null) {
            if (userModel.getId().equals(postModel.getUserId())) {
                holder.iv_remove.setVisibility(View.VISIBLE);
                //               holder.edit_comment_reply_button.setVisibility(View.VISIBLE);
            } else {
                holder.iv_remove.setVisibility(View.GONE);
                //               holder.edit_comment_reply_button.setVisibility(View.GONE);
            }
            //   holder.iv_edit.setVisibility(View.GONE);
            holder.iv_remove.setVisibility(View.GONE);
            //          holder.edit_comment_reply_button.setVisibility(View.GONE);
        } else {

            holder.iv_remove.setVisibility(View.GONE);
            //         holder.edit_comment_reply_button.setVisibility(View.VISIBLE);
            //         holder.edit_comment_reply_button.setVisibility(View.GONE);

        }

        if (commentModel.getAttachment() == null || commentModel.getAttachment().equals(null)) {

            holder.img_preview_image.setVisibility(View.GONE);

        } else {

            holder.img_preview_image.setVisibility(View.VISIBLE);

//            Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(imageurl)).into(holder.img_preview_image);
            Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(imageurl)).into(holder.img_preview_image);
        }

//        if (holder.iv_remove.getVisibility() == View.GONE && holder.iv_edit.getVisibility() == View.GONE)
//        {
//            RelativeLayout.LayoutParams ReplyButton = new
//                    RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
//            ReplyButton.setMargins(20,20,0,0);
//            holder.tv_reply_button.setLayoutParams(ReplyButton);
//        }

        holder.tv_comment_text.setOnHashtagClickListener(new SocialView.OnClickListener() {
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
        holder.tv_comment_text.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
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
//        holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.comment_replies_view.getVisibility()==View.VISIBLE)
//                {
//                    holder.comment_replies_view.setVisibility(View.GONE);
//                }else {
//                    holder.comment_replies_view.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        holder.tv_comment_text.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                ((HomeActivity) context).openUserProfile(text.toString());
//                BaseUtils.openUserProfile(text.toString(), context);
//                for (int i = 0; i < commentModel.getTagData().size(); i++){
//                    if (commentModel.getTagData().get(i).getName().equals(text.toString())){
//                        BaseUtils.openUserProfile(Integer.parseInt(commentModel.getTagData().get(i).getId()), context);
//                    }
//                }
            }
        });
//        setDescriptionsDataUtils.setTagsForComments(context, commentModel, holder.tv_comment_text, position);
        setUpOnclickListeners(holder, position);

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initReactionsListener(int position, Holder holder) {
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(new int[]{
                        R.drawable.smile,
                        R.drawable.meh,
                        R.drawable.sad,
                        R.drawable.wow,
                        R.drawable.angry,
                        R.drawable.laughing,
                        R.drawable.crying
                }).withPopupGravity(PopupGravity.SCREEN_LEFT)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (p) -> {
            switch (p) {
                case 0:
                    reactOnComment(commentModelList.get(position), 1, userModel.getId(), position);
                    break;
                case 1:
                    reactOnComment(commentModelList.get(position), 2, userModel.getId(), position);
                    break;
                case 2:
                    reactOnComment(commentModelList.get(position), 3, userModel.getId(), position);
                    break;
                case 3:
                    reactOnComment(commentModelList.get(position), 4, userModel.getId(), position);
                    break;
                case 4:
                    reactOnComment(commentModelList.get(position), 5, userModel.getId(), position);
                    break;
                case 5:
                    reactOnComment(commentModelList.get(position), 6, userModel.getId(), position);
                    break;
                case 6:
                    reactOnComment(commentModelList.get(position), 7, userModel.getId(), position);
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

    private void reactOnComment(ReplyModel commentModel, int reaction, int reactorId, int position) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().reactionOnComment(commentModel.getId(), reaction, postModel.getId()), context, data -> {
            assert data.body() != null;
            if (data.body().getMessage().equals("Reaction Removed Successfully")) {
                System.out.println("Removed Successfully");
                commentModelList.get(position).setTotalReactions(commentModelList.get(position).getTotalReactions() - 1);
                if (data.body().getData().getReactorId() == userModel.getId())
                    commentModelList.get(position).setMyReaction(0);
            } else {
                if (commentModelList.get(position).getMyReaction() == 0) {
                    commentModelList.get(position).setTotalReactions(commentModelList.get(position).getTotalReactions() + 1);
                    if (data.body().getData().getReactorId() == userModel.getId())
                        commentModelList.get(position).setMyReaction(1);
                }
                System.out.println("ADDed Successfully");

            }
            notifyDataSetChanged();

            System.out.println(data);
        });

    }

    private void getPostFromApiId(String postId) {

        ApiManager.apiCallWithFailure(new ApiClient().getInterface().getPostById(postId), context, new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
//                PostDetailFragment.addIntent(context, data.body().getData(), userModel.getId());

                Bundle bundle = new Bundle();
                bundle.putString(POST_MODEL, new Gson().toJson(data.body().getData()));
                bundle.putInt(USER_ID, userModel.getId());
                bundle.putString(HEADER_TITLE, " ");
                ((HomeActivity) context).loadFragment(R.string.tag_post_detail, bundle);
            }

            @Override
            public void onFailure(String failureCause) {

            }
        });
    }

    private void setUpOnclickListeners(Holder holder, int position) {
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.showAlertDialog("Remove Comment", "Are you sure want to remove this comment?", context, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        if (positive) {
                            removeComment(commentModelList.get(position), position);
                        }
                    }
                });
            }
        });


//        holder.edit_comment_reply_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("EDIT CLICKED");
//            }
//        });

//        holder.edit_comment_reply_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommentReplyRequestModel commentReplyRequestModel = new CommentReplyRequestModel();
//
//                if (holder.edit_comment_reply_layout.getVisibility()==View.GONE) {
//
//                    holder.edit_comment_reply_layout.setVisibility(View.VISIBLE);
//                    Glide.with(context).load(BaseUtils.getUrlforPicture(commentModelList.get(position).getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.civ_user_img_reply_edit);
//
//                    holder.et_comment.setText(commentModelList.get(position).getComment());
//
//                    holder.img_upload.setOnClickListener(new View.OnClickListener() {
//
//                        String uploadAttachmentPath;
//                        @Override
//                        public void onClick(View view) {
//                            System.out.println("IMAGE UPLOAD CLICKED");
//                            Dexter.withContext(context).withPermissions(Manifest.permission.CAMERA,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
//                                @Override
//                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                        showImagePickerOptions();
//                                    } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                        showSettingsDialog();
//                                    }
//                                }
//
//                                private void showSettingsDialog() {
//
//                                    SelectMediaForCommentSection selectMediaForCommentSection = new SelectMediaForCommentSection(2);
//                                    selectMediaForCommentSection.addClickListener(new PostOptionInterface() {
//                                        @Override
//                                        public void pressed(String PressedButton) {
//                                            if ("Select Picture Form Gallery".equals(PressedButton)) {
//                                                launchGalleryIntent();
//                                            }
//                                            if ("Select giff".equals(PressedButton)) {
//                                                System.out.println("PressedButton");
//                                                System.out.println(PressedButton);
//                                                launchGiffActivity();
//                                            }
//                                        }
//
//                                        private void launchGiffActivity() {
//
//                                            GiffImageGridListView giffImageGridListView = new GiffImageGridListView();
//                                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//                                            giffImageGridListView.show(fragmentManager, "Giff image list");
//                                            giffImageGridListView.addListener(pressedButton -> {
//                                                //add option if any
//                                                if (pressedButton.equals(null) || pressedButton.equals("")){
//                                                    System.out.println("Nothing selected");
//                                                } else {
//                                                    System.out.println("SELECTED GIFF URL");
//                                                    System.out.println(pressedButton);
////                                                    uploadAttachmentPath = pressedButton;
////                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
////                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
////                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
////                                                    Glide.with(MyApplication.getAppContext()).load(uploadAttachmentPath).into(img_uri);
////                                                    imgevieid.setVisibility(View.VISIBLE);
//                                                }
//                                                giffImageGridListView.dismiss();
//
//                                            });
//
////        Intent intent = new Intent(getContext(),GiffImageGridView.class);
////        startActivity(intent);
////        startActivityForResult(Intent.createChooser(intent, "Select Giff"), REQUEST_GIFF);
//                                        }
//                                    });
//
//                                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
//                                    selectMediaForCommentSection.show(fm, "Select_Media");
//
//                                }
//
//                                private void showImagePickerOptions() {
//
//                                    SelectMediaForCommentSection selectMediaForCommentSection = new SelectMediaForCommentSection(2);
//                                    selectMediaForCommentSection.addClickListener(new PostOptionInterface() {
//                                        @Override
//                                        public void pressed(String PressedButton) {
//                                            if (PressedButton.equals("Select Picture Form Gallery")) {
//                                                launchGalleryIntent();
//                                            }
//                                            if (PressedButton.equals("Select giff")) {
//                                                System.out.println("PressedButton");
//                                                System.out.println(PressedButton);
//                                                launchGiffActivity();
//                                            }
//                                        }
//                                    });
//
//                                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
//                                    selectMediaForCommentSection.show(fm, "Select_Media");
//
//                                }
//
//                                private void launchGalleryIntent() {
//                                    int REQUEST_IMAGE = 1;
//
//                                    Intent intent = new Intent();
//                                    intent.setType("image/*");
//                                    intent.putExtra(Intent.ACTION_PICK, true);
//                                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                                    ((Activity) context).startActivityForResult(intent,REQUEST_IMAGE);
//                                }
//
//                                private void launchGiffActivity() {
//
//                                    GiffImageGridListView giffImageGridListView = new GiffImageGridListView();
//                                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//                                    giffImageGridListView.show(fragmentManager, "Giff image list");
//                                    giffImageGridListView.addListener(pressedButton -> {
//                                        //add option if any
//                                        if (pressedButton.equals(null) || pressedButton.equals("")){
//                                            System.out.println("Nothing selected");
//                                        } else {
//                                            System.out.println("SELECTED GIFF URL");
//                                            System.out.println(pressedButton);
//                                                    uploadAttachmentPath = pressedButton;
//                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
//                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
//                                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
//                                                    Glide.with(MyApplication.getAppContext()).load(uploadAttachmentPath).into(holder.img_uri);
//                                                    holder.imgevieid.setVisibility(View.VISIBLE);
//                                        }
//                                        giffImageGridListView.dismiss();
//
//                                    });
//
////        Intent intent = new Intent(getContext(),GiffImageGridView.class);
////        startActivity(intent);
////        startActivityForResult(Intent.createChooser(intent, "Select Giff"), REQUEST_GIFF);
//                                }
//
//                                @Override
//                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                                    permissionToken.continuePermissionRequest();
//                                }
//                            }).check();
//
//                        }
//                    });
//
//                    holder.edit_comment_button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String comment = Objects.requireNonNull(holder.et_comment.getText()).toString();
//
//                            if (comment.isEmpty()) {
//                                holder.et_comment.setError("Field Can\'t be Empty");
//                                holder.et_comment.setFocusable(true);
//                                return;
//                            }
//
//                            comment = holder.et_comment.getText().toString();
//                            System.out.println("commentcomment");
//                            System.out.println(comment);
//                            holder.et_comment.setText(comment);
//                            commentReplyRequestModel.setCommenter_id(userModel.getId());
//                            commentReplyRequestModel.setComment(comment);
//                            commentReplyRequestModel.setParent_id(commentModelList.get(position).getParent_id());
//                            commentReplyRequestModel.setPost_id(commentModelList.get(position).getPostId());
//                            commentReplyRequestModel.setId(commentModelList.get(position).getId().toString());
////                            commentReplyRequestModel.setIds(commentModelList.get(position).getIds().toString());
////                            commentReplyRequestModel.setTags(commentModelList.get(position).getTag().toString());
//                            holder.et_comment.setText(comment);
//                            editComment(commentReplyRequestModel, position);
//                        }
//                    });
//
//                } else {
//                    holder.imgevieid.setVisibility(View.GONE);
//                    holder.edit_comment_reply_layout.setVisibility(View.GONE);
//
//                }
//
//            }
//        });

//        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editCommentInterface != null) {
//                    System.out.println(editCommentInterface);
//                    editCommentInterface.editCommentReply(commentModelList.get(position),position);
//                }
//            }
//        });
    }

    private void editComment(CommentReplyRequestModel commentModel, int position) {

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().editCommentReply(commentModel), context, new ApiResponseHandlerWithFailure<ReplyModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<ReplyModel>> data) {
                System.out.println("UPDATE SUCCESS FULLY");
                assert data.body() != null;
            }

            @Override
            public void onFailure(String failureCause) {

            }
        });

    }

    private void removeComment(ReplyModel commentModel, int position) {
        commentModelList.remove(position);
        notifyDataSetChanged();

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().removeComment(commentModel.getId().toString()), context, new ApiResponseHandlerWithFailure<GenericDataModel<String>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<String>>> data) {

            }

            @Override
            public void onFailure(String failureCause) {
                commentModelList.add(position, commentModel);
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private CircleImageView civ_user_img, civ_user_img_reply_edit;
        private TextView tv_name, txt_reaction, edit_comment_button;
        SocialTextView tv_comment_text;
        private ImageView iv_remove, img_reaction, img_preview_image, edit_comment_reply_button, img_upload, img_uri;
        LinearLayout edit_comment_reply_layout;
        private TextInputEditText et_comment;
        //       RelativeLayout imgevieid;
        //EditText et_comment_reply;
        //iv_edit;

        public Holder(@NonNull View itemView) {
            super(itemView);
            civ_user_img = itemView.findViewById(R.id.civ_user_img_reply_bar);
            txt_reaction = itemView.findViewById(R.id.txt_reply_reaction);
            tv_comment_text = itemView.findViewById(R.id.tv_reply_comment_text);
            tv_name = itemView.findViewById(R.id.tv_reply_name);
            iv_remove = itemView.findViewById(R.id.iv_reply_remove);
            edit_comment_reply_button = itemView.findViewById(R.id.iv_reply_edit);
            img_reaction = itemView.findViewById(R.id.iv_reaction_reply);
            img_preview_image = itemView.findViewById(R.id.img_Comment_reply_preview_image);
//            edit_comment_reply_layout = itemView.findViewById(R.id.edit_comment_reply_layout);
            civ_user_img_reply_edit = itemView.findViewById(R.id.civ_user_img_comment_bar);
//            et_comment = itemView.findViewById(R.id.et_comment_reply);
//            img_upload = itemView.findViewById(R.id.img_upload_reply);
            edit_comment_button = itemView.findViewById(R.id.tv_comment_text);
//            imgevieid = itemView.findViewById(R.id.imgevieid);
//            img_uri = itemView.findViewById(R.id.img_uri);
//            iv_edit = itemView.findViewById(R.id.iv_reply_edit);

        }
    }
}
