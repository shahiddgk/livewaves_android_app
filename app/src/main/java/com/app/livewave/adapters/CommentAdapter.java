package com.app.livewave.adapters;

import static android.view.View.GONE;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.POST_MODEL;
import static com.app.livewave.utils.Constants.USER_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.ReactionDetailFragment;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.EditCommentInterface;
import com.app.livewave.interfaces.EditCommentReply;
import com.app.livewave.interfaces.ReplyButton;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CommentModel;
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
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holder> {
    private Context context;
    private List<CommentModel> commentModelList = new ArrayList<>();
    private List<ReplyModel> commentReplyModelList = new ArrayList<>();
    private UserModel userModel;
    private PostModel postModel;
    private EditCommentInterface editCommentInterface;
    private ReplyButton replyButton;
    private LinearLayoutManager linearLayoutManager;
    private CommentReplyAdapter commentReplyAdapter;
    private boolean isReplies;
    private EditCommentReply editCommentReply;

    public void setInterface(EditCommentInterface editCommentInterface) {
        this.editCommentInterface = editCommentInterface;
    }

    public void setCommentReplyInterface(EditCommentReply editCommentReply) {
        this.editCommentReply = editCommentReply;
    }

    public CommentAdapter(Context context) {
        this.context = context;
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
    }

    public void setData(List<CommentModel> commentModelList, PostModel mPostModel) {
        this.commentModelList = new ArrayList<>();
        this.commentModelList = commentModelList;
        this.postModel = mPostModel;
        isReplies = false;
        notifyDataSetChanged();
    }

    public void setReplies(ReplyButton replyButton) {
        this.replyButton = replyButton;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CommentModel commentModel = commentModelList.get(position);

        System.out.println("NAME Issue");
        System.out.println(commentModel.getUser().getName());
        holder.tv_name.setText(commentModel.getUser().getName());
        holder.tv_comment_text.setText(commentModel.getComment());
        String imageurl = "" + commentModel.getAttachment();
        System.out.println(imageurl);
        Glide.with(context).load(BaseUtils.getUrlforPicture(commentModel.getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.civ_user_img);
        if (!commentModelList.get(position).getUser().getId().equals(userModel.getId())) {
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HomeActivity) context).openUserProfile(commentModelList.get(position).getUser().getId().toString());
                }
            });
        }
        if (commentModel.getAttachment() == null || commentModel.getAttachment().equals(null)) {
            holder.img_preview_image.setVisibility(GONE);

        } else {

            holder.img_preview_image.setVisibility(View.VISIBLE);
            Glide.with(MyApplication.getAppContext()).load(commentModel.getAttachment()).into(holder.img_preview_image);
        }

        initReactionsListener(position, holder);

        if (commentModelList.get(position).getTotalReactions() > 0) {
            System.out.println("TOTALREACTION");
            System.out.println(commentModelList.get(position).getTotalReactions());
            holder.txt_reaction.setVisibility(View.VISIBLE);
            if (commentModelList.get(position).getMyReaction() != 0) {
                System.out.println("MYREACTION");
                System.out.println(commentModelList.get(position).getMyReaction());
                int totalReactions = commentModelList.get(position).getTotalReactions() - 1;
                if (totalReactions > 0)
                    holder.txt_reaction.setText("You and " + totalReactions + " other reacted to this comment");
                else
                    holder.txt_reaction.setText("You have reacted to this comment");
            } else {

                holder.txt_reaction.setText(commentModelList.get(position).getTotalReactions() + " person reacted to this comment");
            }
        } else {
            holder.txt_reaction.setVisibility(GONE);
        }

        holder.txt_reaction.setOnClickListener(v -> {
            ReactionDetailFragment reactionDetailFragment = new ReactionDetailFragment("0", String.valueOf(commentModelList.get(position).getId()));
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            reactionDetailFragment.show(fragmentManager, "Reactions");
        });

//        commentReplyAdapter = new CommentReplyAdapter(context);
//        commentReplyAdapter.setData(commentReplyModelList,postModel);
//
//        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
//        holder.comment_replies_view.setLayoutManager(linearLayoutManager);
//        holder.comment_replies_view.setAdapter(commentReplyAdapter);

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
            holder.iv_edit.setVisibility(View.VISIBLE);

        } else if (postModel != null) {
            if (userModel.getId().equals(postModel.getUserId())) {
                holder.iv_remove.setVisibility(View.VISIBLE);
            } else {
                holder.iv_remove.setVisibility(GONE);
            }
            holder.iv_edit.setVisibility(GONE);
            holder.iv_remove.setVisibility(GONE);
        } else {

            holder.iv_remove.setVisibility(GONE);
            holder.iv_edit.setVisibility(GONE);

        }

//        if (holder.iv_remove.getVisibility() == View.GONE && holder.iv_edit.getVisibility() == View.GONE)
//        {
//
//            RelativeLayout.LayoutParams ReplyButton = new
//                    RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE);
//            ReplyButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
//            ReplyButton.setMargins(20,20,0,0);
//            holder.tv_reply_button.setLayoutParams(ReplyButton);
//
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

        if (commentModel.getChildren().size() > 0) {
            List<ReplyModel> commentReplyModelList = new ArrayList<>();
            commentReplyModelList = commentModelList.get(position).getChildren();

            commentReplyAdapter = new CommentReplyAdapter(context);
            commentReplyAdapter.setData(commentReplyModelList, postModel);

            linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            holder.comment_replies_view.setLayoutManager(linearLayoutManager);
            holder.comment_replies_view.setAdapter(commentReplyAdapter);
            // initReactionsListener(position, holder);

            commentReplyAdapter.notifyDataSetChanged();

            holder.comment_replies_view.setHasFixedSize(false);
            holder.comment_replies_view.setAdapter(commentReplyAdapter);
            commentReplyAdapter.setInterface(new EditCommentReply() {
                @Override
                public void editCommentReply(ReplyModel replyModel, int position) {
                    Log.e("TAG", "editCommentReply: ");
                    if (editCommentReply != null) {
                        try {
                            Log.e("TAG", "editCommentReply: " + commentModelList.get(position).getChildren().get(position).getComment());
                            editCommentReply.editCommentReply(replyModel, position);
                        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                            Log.e("index out of ", "editCommentReply: " + indexOutOfBoundsException.getMessage() );
                        }

                    }
                }
            });


            if (replyButton != null) {
                if (holder.comment_replies_view.getVisibility() == View.GONE) {
                    holder.comment_replies_view.setVisibility(View.VISIBLE);


                } else {
                    replyButton.setReplyButton(false, position);
                    holder.comment_replies_view.setVisibility(GONE);

                }
            }


        }

        holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReplies = !isReplies;
                replyButton.setReplyButton(true, position);

            }
        });

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

    private void reactOnComment(CommentModel commentModel, int reaction, int reactorId, int position) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().reactionOnComment(commentModel.getId(), reaction, postModel.getId()), context, data -> {
            assert data.body() != null;
            if (data.body().getMessage().equals("Reaction Removed Successfully")) {
                System.out.println("Removed Successfully");
                commentModelList.get(position).setTotalReactions(commentModelList.get(position).getTotalReactions() - 1);
                //        if (data.body().getData().getReactorId() == userModel.getId())
                commentModelList.get(position).setMyReaction(0);
            } else {
                if (commentModelList.get(position).getMyReaction() == 0) {
                    commentModelList.get(position).setTotalReactions(commentModelList.get(position).getTotalReactions() + 1);
                    commentModelList.get(position).setMyReaction(reaction);
                    System.out.println(data.body().getData().getReactorId());
                    System.out.println(userModel.getId());
                    System.out.println(commentModelList.get(position).getMyReaction());
//                    if (data.body().getData().getReactorId() == userModel.getId()){}
                    //  commentModelList.get(position).setMyReaction(1);
                }
                System.out.println("REACTOR ID Reaction Checking");
                System.out.println(commentModelList.get(position).getMyReaction());
                System.out.println(commentModelList.get(position).getTotalReactions());
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

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("EDIT CLICKED");
                System.out.println(editCommentInterface);
                if (editCommentInterface != null) {
                    try {

                        editCommentInterface.editComment(commentModelList.get(position), position);
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        Log.e("Index Out ", "onClick: " + indexOutOfBoundsException.getMessage());
                    }
                }
            }
        });
    }

    private void removeComment(CommentModel commentModel, int position) {
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
        private CircleImageView civ_user_img;
        private TextView tv_name, tv_reply_button, txt_reaction;
        SocialTextView tv_comment_text;
        private ImageView iv_remove, iv_edit, img_reaction, img_preview_image;
        RecyclerView comment_replies_view;

        public Holder(@NonNull View itemView) {
            super(itemView);
            civ_user_img = itemView.findViewById(R.id.civ_user_img_comment_bar);
            //civ_user_img_reply = itemView.findViewById(R.id.civ_user_img_reply_comment_bar);
            tv_comment_text = itemView.findViewById(R.id.tv_comment_text);
            //tv_reply_comment_text = itemView.findViewById(R.id.tv_reply_comment_text);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_reply_button = itemView.findViewById(R.id.iv_reply_button);
            // tv_reply_name = itemView.findViewById(R.id.tv_reply_name);
            iv_remove = itemView.findViewById(R.id.iv_remove);
            //iv_reply_remove = itemView.findViewById(R.id.iv_reply_remove);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            comment_replies_view = itemView.findViewById(R.id.rv_comments_replies);
            // iv_reply_edit = itemView.findViewById(R.id.iv_reply_edit);
            img_reaction = itemView.findViewById(R.id.iv_reaction);
            txt_reaction = itemView.findViewById(R.id.txt_reaction);
            img_preview_image = itemView.findViewById(R.id.img_Comment_preview_image);
            // iv_reply_reaction = itemView.findViewById(R.id.iv_reply_reaction);
            // list_view = itemView.findViewById(R.id.list_view);
        }
    }
}
