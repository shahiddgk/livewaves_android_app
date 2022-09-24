package com.app.livewave.fragments.newsfeed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.BottomDialogSheets.GiffImageGridListView;
import com.app.livewave.BottomDialogSheets.ReactionDetailFragment;
import com.app.livewave.BottomDialogSheets.SelectMediaForCommentSection;
import com.app.livewave.BottomDialogSheets.SelectMediaForPostDialogFragment;
import com.app.livewave.R;
import com.app.livewave.activities.GiffImageGridView;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.activities.VideoPlayerActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.adapters.CommentAdapter;
import com.app.livewave.adapters.CommentReplyAdapter;
import com.app.livewave.adapters.ImageSliderAdapter;
import com.app.livewave.adapters.UserTagAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.EditCommentInterface;
import com.app.livewave.interfaces.EditCommentReply;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.ReplyButton;
import com.app.livewave.models.CommentReplyRequestModel;
import com.app.livewave.models.CommentRequestModel;
import com.app.livewave.models.IdsAndTagsListModel;
import com.app.livewave.models.ParameterModels.AttachmentParams;
import com.app.livewave.models.ParameterModels.CreatePostModel;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CommentModel;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.ReactionModel;
import com.app.livewave.models.ResponseModels.ReplyModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.utils.FirebaseUtils;
import com.app.livewave.utils.MyApplication;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.checkExtentionValidation;
import static com.app.livewave.utils.BaseUtils.getMimeType;
import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.BaseUtils.updateETwithTags;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.POST_ID;
import static com.app.livewave.utils.Constants.POST_MODEL;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.REQUEST_IMAGE_CAPTURE;
import static com.app.livewave.utils.Constants.URL;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.VIDEO_SHARE_COUNT;
import static com.app.livewave.utils.Constants.VIDEO_VIEW_COUNT;
import static com.app.livewave.utils.Constants.currentUser;
import static com.app.livewave.utils.setDescriptionsDataUtils.commentModel;
import static com.app.livewave.utils.setDescriptionsDataUtils.extractUrls;
import static com.app.livewave.utils.setDescriptionsDataUtils.isContainLink;

public class PostDetailFragment extends Fragment implements View.OnClickListener, PlayerStateListener {
    private TextView txt_name, txt_date, txt_reaction, txt_profile_name, tv_pay_to_unlock,
            tv_comment_text, tv_no_comments, txt_preview_title, txt_preview_des, txt_share,
            txt_shared_name, txt_shared_profile_name, txt_shared_preview_title, txt_shared_preview_des, txt_shared_content, tv_shared_pay_to_unlock;
    private CircleImageView img_profile, civ_user_img, img_shared_profile;
    private ImageView img_reaction, img_share, tv_post_option, iv_play_video, img_preview_image, iv_paid, img_upload,
            img_shared_preview_image, iv_shared_play_video;
    private LinearLayout profile, shared_profile, ll_privacy_item;
    ViewPager image_shared_slider, image_slider;
    private int REQUEST_IMAGE = 1;
    private int REQUEST_GIFF = 2;
    SocialTextView txt_des, txt_shared_des;
    private TextInputEditText et_comment;
    private RecyclerView rv_comments, rv_comments_replies;
    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter;
    private CommentReplyAdapter commentReplyAdapter;
    private List<CommentModel> commentModelList = new ArrayList<>();
    private List<ReplyModel> commentReplyModelList = new ArrayList<>();
    MaterialCardView card_shared;
    RelativeLayout rl_shared_blur_paid_view, rl_blur_paid_view;
    private Toolbar toolbar;

    private PostModel postModel, sharedPostModel;
    private int postId;
    private int userId;
    private ReactionPopup popup;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;
    private SwipeRefreshLayout swipe_to_refresh;
    private RecyclerView rv_tags;
    //    private AppBarLayout app_bar;
    boolean editedComment, editedCommentReply, commentReply, isImage = false, isVideo = false;
    private String commentId;
    private String commentIdReply;
    private int editCommentPosition = -1;
    private int editCommentReplyPosition = -1;
    private KProgressHUD dialog, loadingDialog;
    private NestedScrollView nested_scroll_view;
    private ProgressBar progress_bar;
    private MaterialCardView card_for_tags;
    private ArrayList<FollowModel> addFollowersFiltering = new ArrayList<>();
    private List<FollowModel> followModelList = new ArrayList<>();
    private UserTagAdapter userTagAdapter;
    private List<String> ids = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    LinearLayout layout_preview, layout_shared_preview;
    private UserModel userModel;
    private CommentRequestModel commentRequestModel = new CommentRequestModel();
    private CommentReplyRequestModel commentReplyRequestModel = new CommentReplyRequestModel();
    ImageSliderAdapter imageAdapter, imageSharedAdapter;
    TabLayout image_shared_slider_tabDots, image_slider_tabDots;
    RelativeLayout rl_image;

    private Uri uploadAttachment;
    private String uploadAttachmentPath;
    private String uploadAttachmentExtension;
    private Uri uri;
    private ImageView img_uri, img_cancel;
    RelativeLayout imgevieid;
    private FirebaseUtils utils;
    int count = 0;
    String oldImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        initComponents(view);
        setUpNestedScrollView();
        setUpReactionsClick();
        initClickListeners();
        getIntentData();
        setOctetChangeListener();
        setUpRecycleView(view);
        setEditPostInterface();
        setEditCommentReplyInterface();
        setReplyButtonInterface();
        setHasOptionsMenu(true);

//        et_comment.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                commentRequestModel.setAttachment(" ");
                commentReplyRequestModel.setAttachment(null);
                imgevieid.setVisibility(View.GONE);
            }
        });

        txt_des.setOnHashtagClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                Intent intent = new Intent(PostDetailActivity.this, HashtagFragment.class);
//                intent.putExtra(Constants.HASH_TAG, text.toString());
//                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HASH_TAG, text.toString());
                bundle.putString(HEADER_TITLE, text.toString());
                ((HomeActivity) getActivity()).loadFragment(R.string.tag_hashtag, bundle);
            }
        });
        txt_des.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                if (text.toString().contains("pl/post")) {
                    String postId = text.toString().substring(text.toString().lastIndexOf("/") + 1);
                    getPostFromApiId(postId);

                } else if (text.toString().contains("http")) {
                    Uri uri = Uri.parse(text.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Uri uri = Uri.parse("https://" + text.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        txt_des.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                ((HomeActivity) getActivity()).openUserProfile(text.toString());
//                BaseUtils.openUserProfile(text.toString(), getActivity());
//                for (int i = 0; i < postModel.getTagsData().size(); i++){
//                    if (postModel.getTagsData().get(i).getName().equals(text.toString())){
//                        BaseUtils.openUserProfile(Integer.parseInt(postModel.getTagsData().get(i).getId()), PostDetailActivity.this);
//                    }
//                }
            }
        });
        return view;
    }

    private void uploadMultipleFilesToServer() {
        dialog.show();
        if (editedComment) {
            if (uploadAttachment != null) {
                File file = new File(BaseUtils.getPath(getContext(), uploadAttachment));
                String unique_name = UUID.randomUUID().toString();
                System.out.println("unique_name");
                System.out.println(unique_name);
                System.out.println(uploadAttachment.getPath());
                FirebaseApp.initializeApp(getContext());
                String ext = getMimeType(getContext(), uploadAttachment);
                int validMediaFormat = checkExtentionValidation(ext);
                if (validMediaFormat == 0) {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
                    StorageReference storageReference = FirebaseUtils.getFireBaseStorageRefereneceForSingleImage(ext, profilePath);
                    storageReference.putFile(uploadAttachment).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadAttachmentPath = uri.toString();
                                    uploadAttachmentExtension = ext;
                                    System.out.println("uploadAttachmentPath");
                                    System.out.println(uploadAttachmentPath);
                                    commentRequestModel.setAttachment(uploadAttachmentPath);
                                    System.out.println(uploadAttachmentExtension);
                                    dialog.dismiss();

                                    editComment(commentRequestModel);

                                }
                            });
                        }
                    });
                }
            }
        } else if (commentReply) {

            if (uploadAttachment != null) {
                File file = new File(BaseUtils.getPath(getContext(), uploadAttachment));
                String unique_name = UUID.randomUUID().toString();
                System.out.println("unique_name");
                System.out.println(unique_name);
                System.out.println(uploadAttachment.getPath());
                FirebaseApp.initializeApp(getContext());
                String ext = getMimeType(getContext(), uploadAttachment);
                int validMediaFormat = checkExtentionValidation(ext);
                if (validMediaFormat == 0) {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
                    StorageReference storageReference = FirebaseUtils.getFireBaseStorageRefereneceForSingleImage(ext, profilePath);
                    storageReference.putFile(uploadAttachment).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadAttachmentPath = uri.toString();
                                    uploadAttachmentExtension = ext;
                                    commentRequestModel.setAttachment(uploadAttachmentPath);
                                    System.out.println("uploadAttachmentPath");
                                    System.out.println(uploadAttachmentPath);
                                    System.out.println(uploadAttachmentExtension);
                                    dialog.dismiss();

                                    commentReplyRequestModel.setAttachment(uploadAttachmentPath);
                                    addCommentReply(commentReplyRequestModel);


                                }
                            });
                        }
                    });
                }
            }

        } else {

            if (uploadAttachment != null) {
                File file = new File(BaseUtils.getPath(getContext(), uploadAttachment));
                String unique_name = UUID.randomUUID().toString();
                System.out.println("unique_name");
                System.out.println(unique_name);
                System.out.println(uploadAttachment.getPath());
                FirebaseApp.initializeApp(getContext());
                String ext = getMimeType(getContext(), uploadAttachment);
                System.out.println("EXTENSION");
                System.out.println(ext);
                int validMediaFormat = checkExtentionValidation(ext);
                if (validMediaFormat == 0) {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
                    System.out.println(profilePath);
                    StorageReference storageReference = FirebaseUtils.getFireBaseStorageRefereneceForSingleImage(ext, profilePath);
                    storageReference.putFile(uploadAttachment).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadAttachmentPath = uri.toString();
                                    uploadAttachmentExtension = ext;
                                    System.out.println("uploadAttachmentPath");
                                    System.out.println(uploadAttachmentPath);
                                    System.out.println(uploadAttachmentExtension);
                                    commentRequestModel.setAttachment(uploadAttachmentPath);
                                    dialog.dismiss();

                                    addComment(commentRequestModel);

                                }
                            });
                        }
                    });
                }
            }

        }

    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_detail);
//
//        initComponents();
//        setUpNestedScrollView();
//        setUpReactionsClick();
//        initClickListeners();
//        getIntentData();
//        setOctetChangeListener();
//        setUpRecycleView();
//        setEditPostInterface();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");
//
//        txt_des.setOnHashtagClickListener(new SocialView.OnClickListener() {
//            @Override
//            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
////                Intent intent = new Intent(PostDetailActivity.this, HashtagFragment.class);
////                intent.putExtra(Constants.HASH_TAG, text.toString());
////                startActivity(intent);
//
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.HASH_TAG, text.toString());
//                ((HomeActivity) getActivity()).loadFragment(R.string.tag_hashtag, bundle);
//            }
//        });
//        txt_des.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
//            @Override
//            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                if (text.toString().contains("pl/post")) {
//                    String postId = text.toString().substring(text.toString().lastIndexOf("/") + 1);
//                    getPostFromApiId(postId);
//
//                } else if (text.toString().contains("http")) {
//                    Uri uri = Uri.parse(text.toString());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                } else {
//                    Uri uri = Uri.parse("https://" + text.toString());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                }
//            }
//        });
//        txt_des.setOnMentionClickListener(new SocialView.OnClickListener() {
//            @Override
//            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                BaseUtils.openUserProfile(text.toString(), PostDetailActivity.this);
////                for (int i = 0; i < postModel.getTagsData().size(); i++){
////                    if (postModel.getTagsData().get(i).getName().equals(text.toString())){
////                        BaseUtils.openUserProfile(Integer.parseInt(postModel.getTagsData().get(i).getId()), PostDetailActivity.this);
////                    }
////                }
//            }
//        });
//    }

    private void setYoutubePreview(PostModel postModel) throws MalformedURLException, UnsupportedEncodingException {
        if (postModel == null)
            return;
        if (postModel.getDescription() != null && !postModel.getDescription().equals("")) {
            if (isContainLink(postModel.getDescription())) {
                layout_preview.setVisibility(View.VISIBLE);
                String url = extractUrls(postModel.getDescription());
                if (!url.equalsIgnoreCase("")) {
                    RichPreview richPreview = new RichPreview(new ResponseListener() {
                        @Override
                        public void onData(MetaData metaData) {
                            Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(metaData.getImageurl())).into(img_preview_image);
                            txt_preview_title.setText(metaData.getTitle());
                            txt_preview_des.setText(metaData.getUrl());
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
                    richPreview.getPreview(url);
                    //make sure it's youtube
//                    if (url.contains("youtube") || url.contains("youtu")) {
//                        URL url1 = new URL(url);
//                        String a = splitQuery(url1);
//                        if (a == null) {
//                            a = url1.toString().substring(url1.toString().lastIndexOf("/") + 1);
//                        }
//                        String youtube;
//                        youtube = "https://img.youtube.com/vi/" + a + "/hqdefault.jpg";
//                        if (postModel.getAttachments().size() > 0)
//                            postModel.getAttachments().get(0).setThumbnail(youtube);
//                        else {
//                            img_picture.setVisibility(View.VISIBLE);
//                            Glide.with(PostDetailActivity.this).load(youtube).into(img_picture);
//                        }
//                    }
                }
            } else {
                layout_preview.setVisibility(View.GONE);
            }
        }
    }

    private void setUpNestedScrollView() {
        nested_scroll_view.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()))
                            && scrollY > oldScrollY) {
                        currentItems = linearLayoutManager.getChildCount();
                        totalItems = linearLayoutManager.getItemCount();
                        scrollOutItems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if ((currentItems + scrollOutItems >= totalItems)) {
                            if (nextPageUrl != null) {
                                currentPageNumber++;
                                getPostComments();
                            }
                        }
                    }
                }
            }
        });
    }

    private void setOctetChangeListener() {
        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    editedComment = false;
                    editedCommentReply = false;
                    commentReply = false;
                    commentId = "";
                    tv_comment_text.setText(R.string.post);
                    editCommentPosition = -1;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                Pattern p = Pattern.compile("[@][a-zA-Z0-9-.]+");
                Matcher m = p.matcher(text);

                int cursorPosition = et_comment.getSelectionStart();
                if (s.toString().contains("@")) {
                    card_for_tags.setVisibility(View.VISIBLE);
                } else {
                    card_for_tags.setVisibility(View.GONE);
                }
                while (m.find()) {
                    if (cursorPosition >= m.start() && cursorPosition <= m.end()) {
                        final int s2 = m.start(); // add 1 to ommit the "@" tag
                        final int e = m.end();
                        card_for_tags.setVisibility(View.VISIBLE);
                        //add post
                        filterTagData(text.substring(s2, e));
                        //filterTagData(text);
                        break;
                    } else {

                        card_for_tags.setVisibility(View.GONE);

                    }
                }
            }
        });
    }

    private void filterTagData(String substring) {
        progress_bar.setVisibility(View.VISIBLE);
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowers(substring), getActivity(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                progress_bar.setVisibility(View.GONE);

                followModelList.clear();
                followModelList.addAll(data.body().getData().getData());
                userTagAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setEditPostInterface() {
        commentAdapter.setInterface(new EditCommentInterface() {
            @Override
            public void editComment(CommentModel commentModel, int position) {
                et_comment.setText(commentModel.getComment());
                editedComment = true;
                System.out.println("AFTER EDITED");
                System.out.println(tv_comment_text.getText().toString().trim());
                commentId = commentModel.getId().toString();
                System.out.println("AFTER EDITED CommentID");
                System.out.println(commentId);
                commentRequestModel.setComment_id(commentId);
                tv_comment_text.setText(R.string.edit_comment);
                editCommentPosition = position;

                if (commentModel.getAttachment() != null) {
                    String attachment = "" + commentModel.getAttachment();
                    uploadAttachmentPath = attachment;
                    Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(attachment)).into(img_uri);
                    System.out.println("attachmentattachment");
                    System.out.println(attachment);
                    imgevieid.setVisibility(View.VISIBLE);
                } else {
                    oldImageUrl = null;
                    uploadAttachment = null;
                    uploadAttachmentExtension = null;
                    uploadAttachmentPath = null;
                    imgevieid.setVisibility(View.GONE);
                    commentRequestModel.setAttachment(" ");
                }
            }

        });
    }

    private void setEditCommentReplyInterface() {
        commentAdapter.setCommentReplyInterface(new EditCommentReply() {

            @Override
            public void editCommentReply(ReplyModel commentModel, int position) {
                Log.e("comment checking ", "editCommentReply: " + commentModel.getComment());
                Log.e("position from RV", "editCommentReply: " + position);
                et_comment.setText(commentModel.getComment());
                editedCommentReply = true;
                commentReply = false;
                System.out.println("AFTER EDITED");
                System.out.println(tv_comment_text.getText().toString().trim());
                commentIdReply = commentModel.getId().toString();
                System.out.println("AFTER EDITED CommentID");
                System.out.println(commentIdReply);
                tv_comment_text.setText(R.string.edit_reply_comment);
                editCommentReplyPosition = position;
            }

        });
    }

    private void setReplyButtonInterface() {
        commentAdapter.setReplies(new ReplyButton() {
            @Override
            public void setReplyButton(boolean replyButton, int position) {
                System.out.println(replyButton);
                if (replyButton) {
                    int commentId = 0;
                    editedComment = false;

                    et_comment.setText(commentModelList.get(position).getUser().getName());
                    uploadAttachment = null;
                    uploadAttachmentExtension = null;
                    uploadAttachmentPath = null;
                    commentRequestModel.setAttachment(" ");
                    commentReplyRequestModel.setAttachment(null);
                    imgevieid.setVisibility(View.GONE);

                    commentReply = true;
                    System.out.println("replyButton");
                    System.out.println(commentReply);
                    editedCommentReply = false;
                    commentId = commentModelList.get(position).getId();
                    System.out.println(commentId);
                    System.out.println("COMMENT_ID");
                    commentReplyRequestModel.setParent_id(commentId);
                    editCommentPosition = -1;
                    tv_comment_text.setText("Reply");
                } else {

                    editedComment = false;
                    commentReply = false;
                    commentReplyRequestModel.setParent_id(0);
                    System.out.println("commentReply");
                    System.out.println(commentReply);
                    editedCommentReply = false;
                    commentId = "";
                    et_comment.setText("");
                    tv_comment_text.setText(R.string.post);
                    editCommentPosition = -1;

                }
            }
        });
    }

    private void setUpRecycleView(View view) {

        rv_comments = view.findViewById(R.id.rv_comments);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        commentAdapter = new CommentAdapter(getActivity());
        rv_comments.setLayoutManager(linearLayoutManager);
        rv_comments.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();

        rv_comments_replies = view.findViewById(R.id.rv_comments_replies);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        commentReplyAdapter = new CommentReplyAdapter(getActivity());
        rv_comments_replies.setLayoutManager(linearLayoutManager);
        rv_comments_replies.setAdapter(commentReplyAdapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initClickListeners() {
        img_reaction.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventBus.getDefault().postSticky(new OnTouch(true));
                popup.onTouch(v, event);
                return true;
            }
        });

        img_share.setOnClickListener(v -> {

            addCount(postModel.getSharingID());

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Post");
            String app_url;
            app_url = Constants.BASE_URL + "pl/post/" + postModel.getSharingID();
            shareIntent.putExtra(Intent.EXTRA_TEXT, app_url);
            getContext().startActivity(Intent.createChooser(shareIntent, "Share Post"));
        });
//
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                commentModelList.clear();
                commentReplyModelList.clear();
//                getIntent().removeExtra(POST_MODEL);
                getArguments().remove(POST_MODEL);
                getIntentData();


            }
        });
//        img_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (postModel.getAttachments().size() > 0) {
//                    if (postModel.getAttachments().get(0).getType().equals("video")) {
//                        Intent intent = new Intent(PostDetailActivity.this, VideoPlayerActivity.class);
//                        intent.putExtra(URL, postModel.getAttachments().get(0).getPath());
//                        startActivity(intent);
//                    } else if (postModel.getAttachments().get(0).getType().equals("image")) {
//                        /*Intent fullImageIntent = new Intent(PostDetailActivity.this, FullScreenImageViewActivity.class);
//                        ArrayList<String> uriString = new ArrayList<>();
//                        uriString.add(postModel.getAttachments().get(0).getPath());
//                        fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString);
//                        fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, 0);
//                        startActivity(fullImageIntent);*/
//
//                        ArrayList<String> uriString = new ArrayList<>();
//                        uriString.add(postModel.getAttachments().get(0).getPath());
//                        new ImageViewer.Builder(PostDetailActivity.this, uriString)
//                                .setStartPosition(0)
//                                .show();
//                    }
//                } else {
////                    String link = Arrays.toString(extractLinks(txt_des.getText().toString())).replace("[", "").replace("]", "");
////                    Intent i = new Intent(Intent.ACTION_VIEW);
////                    i.setData(Uri.parse(link));
////                    startActivity(i);
//                }
//            }
//        });
        layout_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = Arrays.toString(extractLinks(txt_des.getText().toString())).replace("[", "").replace("]", "");
                if (link.contains("pl/post")) {
                    String postId = link.substring(link.lastIndexOf("/") + 1);
                    getPostFromApiId(postId);
                } else if (link.contains("http")) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Uri uri = Uri.parse("https://" + link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        img_profile.setOnClickListener(this);
        txt_profile_name.setOnClickListener(this);
        tv_comment_text.setOnClickListener(this);
        txt_name.setOnClickListener(this);

        rl_shared_blur_paid_view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("id", "post_id=" + sharedPostModel.getId());
            intent.putExtra("type", "post");
            intent.putExtra("intent_type", "6");
            startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putString("id", "post_id=" + sharedPostModel.getId());
//            bundle.putString("type", "post");
//            bundle.putString("intent_type", "6");
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_webview, bundle);
        });
        rl_blur_paid_view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("id", "post_id=" + postModel.getId());
            intent.putExtra("type", "post");
            intent.putExtra("intent_type", "6");
            startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putString("id", "post_id=" + postModel.getId());
//            bundle.putString("type", "post");
//            bundle.putString("intent_type", "6");
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_webview, bundle);
        });


        iv_shared_play_video.setOnClickListener(v -> {
            addViewCount(postModel.getId() + "");
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            String share_Count = sharedPostModel.getTotalShares() + "";
            intent.putExtra(VIDEO_SHARE_COUNT, share_Count);
            String view_Count = sharedPostModel.getTotalViews() + "";
            intent.putExtra(VIDEO_VIEW_COUNT, view_Count);
            intent.putExtra(URL, sharedPostModel.getAttachments().get(0).getPath());
            startActivity(intent);
        });

        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getContext()).withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }
        });

//        img_shared_picture.setOnClickListener(v -> {
//            if (sharedPostModel.getAttachments().size() > 0) {
//                if (sharedPostModel.getAttachments().get(0).getType().equals("video")) {
//                    Intent intent = new Intent(this, VideoPlayerActivity.class);
//                    intent.putExtra(URL, sharedPostModel.getAttachments().get(0).getPath());
//                    startActivity(intent);
//                } else if (sharedPostModel.getAttachments().get(0).getType().equals("image")) {
//                    ArrayList<String> uriString = new ArrayList<>();
//                    uriString.add(getUrlforPicture(sharedPostModel.getAttachments().get(0).getPath()));
//                    new ImageViewer.Builder(this, uriString)
//                            .setStartPosition(0)
//                            .show();
//                }
//            } else {
////                    String link = Arrays.toString(extractLinks(holder.txt_des.getText().toString())).replace("[", "").replace("]", "");
//                String link = Arrays.toString(extractLinks(sharedPostModel.getDescription())).replace("[", "").replace("]", "");
//                if (link.contains("pl/post")) {
//                    String postId = link.substring(link.lastIndexOf("/") + 1);
//                    getPostFromApiId(postId);
//                } else {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(link));
//                    this.startActivity(i);
//                }
//            }
//
//        });
        layout_shared_preview.setOnClickListener(v -> {
            String link = Arrays.toString(extractLinks(sharedPostModel.getDescription())).replace("[", "").replace("]", "");
            if (link.contains("pl/post")) {
                String postId = link.substring(link.lastIndexOf("/") + 1);
                getPostFromApiId(postId);
            } else if (link.contains("http")) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Uri uri = Uri.parse("https://" + link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        txt_reaction.setOnClickListener(v -> {
            ReactionDetailFragment reactionDetailFragment = new ReactionDetailFragment(String.valueOf(postModel.getId()), "0");
            FragmentManager fragmentManager = getChildFragmentManager();
            reactionDetailFragment.show(fragmentManager, "Reactions");
        });
    }

    private void addViewCount(String id) {
        System.out.println(id);
        System.out.println(userId);

        ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToVideoPostForViews(userId, id), getContext(), new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println("Post View count added");
                System.out.println(data.body().getMessage());
                if (data.body().getMessage().equals("waves count updated Successfully")) {
                    postModel.setTotalViews(postModel.getTotalViews() + 1);
                }
            }
        });
    }

    private void addCount(String trackId) {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToPostForShare(userId, trackId), getContext(), new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println("Post Share count added");
                System.out.println(data.body().getMessage());
                if (data.body().getMessage().equals("post count updated Successfully")) {

                }
            }
        });
    }

    public String[] extractLinks(String text) {
        List<String> links = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            if (url.startsWith("http"))
                links.add(url);
        }

        return links.toArray(new String[0]);
    }

    private void showImagePickerOptions() {

        SelectMediaForCommentSection selectMediaForCommentSection = new SelectMediaForCommentSection(2);
        selectMediaForCommentSection.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();
                }
                if (getString(R.string.video_giff).equals(PressedButton)) {
                    System.out.println("PressedButton");
                    System.out.println(PressedButton);
                    launchGiffActivity();
                }
            }
        });

//        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(1);
//        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
//            @Override
//            public void pressed(String PressedButton) {
////                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
////                    launchCameraIntent();
////                }
//                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
//                    launchGalleryIntent();
//
//                }
////                if (getString(R.string.video_gallery).equals(PressedButton)) {
////                    launchVideoGalleryIntent();
////
////                }
//            }
//        });

        FragmentManager fm = getActivity().getSupportFragmentManager();
        selectMediaForCommentSection.show(fm, "Select_Media");

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, REQUEST_IMAGE_CAPTURE);
        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
//        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void launchGalleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.ACTION_PICK, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), REQUEST_IMAGE);
    }

    private void launchGiffActivity() {

        GiffImageGridListView giffImageGridListView = new GiffImageGridListView();
        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
        giffImageGridListView.show(fragmentManager, "Giff image list");
        giffImageGridListView.addListener(pressedButton -> {
            //add option if any
            if (pressedButton.equals(null) || pressedButton.equals("")) {
                System.out.println("Nothing selected");
            } else {
                System.out.println("SELECTED GIFF URL");
                System.out.println(pressedButton);
                uploadAttachmentPath = pressedButton;
                commentRequestModel.setAttachment(uploadAttachmentPath);
                commentReplyRequestModel.setAttachment(uploadAttachmentPath);
                commentReplyRequestModel.setAttachment(uploadAttachmentPath);
                Glide.with(MyApplication.getAppContext()).load(uploadAttachmentPath).into(img_uri);
                imgevieid.setVisibility(View.VISIBLE);
            }
            giffImageGridListView.dismiss();

        });

//        Intent intent = new Intent(getContext(),GiffImageGridView.class);
//        startActivity(intent);
//        startActivityForResult(Intent.createChooser(intent, "Select Giff"), REQUEST_GIFF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {

                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    //     final Uri imageUri = data.getData();
                    count = 1;
                    uploadAttachment = selectedImageUri;
                    imgevieid.setVisibility(View.VISIBLE);
                    img_uri.setImageURI(selectedImageUri);

                    // Glide.with(MyApplication.getAppContext()).load(imageUri).into(img_uri);

                } else {
                    count = 0;
                    imgevieid.setVisibility(View.GONE);
                }
//                else {
//                    uri = data.getData();
//                    if (uri != null) {
//
//                        imageList.add(uri);
//                        newImageList.add(uri);
//                        adapter.setList(imageList);
//                        rv_multiple_images.setVisibility(View.VISIBLE);
//                        img_layout.setVisibility(View.VISIBLE);
//
//                        extention = getMimeType(getContext(), uri);
//
//                    }
//                }
            }
        }
//        if (requestCode == REQUEST_GIFF) {
//            if (resultCode == AppCompatActivity.RESULT_OK) {
//                String giffurl = data.getDataString();
//                if (giffurl != null || giffurl != "") {
//                    System.out.println("GIFF URL");
//                    Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(giffurl)).into(img_preview_image);
//
//                }
//            }
//        }
//        if (requestCode == Constants.REQUEST_GALLERY_VIDEO && resultCode == Activity.RESULT_OK) {
//            uri = data != null ? data.getData() : null;
//            if (uri != null) {
//                duration = BaseUtils.getVideoDuration(getContext(), uri);
//                extention = getMimeType(getContext(), uri);
//                uploadAttachment = uri;
//                isPostImageSelected = true;
//                if (!editingPost)
//                    imageList = new ArrayList<>();
//                newImageList = new ArrayList<>();
//                newImageList.add(uri);
//                imageList.add(uri);
//                adapter.setList(imageList);
//                rv_multiple_images.setVisibility(View.VISIBLE);
//                img_layout.setVisibility(View.VISIBLE);
////                img_layout.setVisibility(View.VISIBLE);
////                single_image_card.setVisibility(View.VISIBLE);
////                Glide.with(getContext()).load(uri).placeholder(R.drawable.cover_place_holder).into(img_post);
//            } else {
//                BaseUtils.showLottieDialog(getContext(), "Path not found!", R.raw.invalid, new DialogBtnClickInterface() {
//                    @Override
//                    public void onClick(boolean positive) {
//                    }
//                });
////                BaseUtils.showToast(getContext(), "Path not found");
//            }
//
//        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
//        {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            img_uri.setImageBitmap(photo);
//        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Uri selectedImageUri = data.getData();
//            if (selectedImageUri != null) {
////                if (!editingPost)
////                    imageList = new ArrayList<>();
////                newImageList = new ArrayList<>();
//                System.out.println("CAMERA");
//                System.out.println(selectedImageUri.getPath());
//                img_uri.setImageURI(selectedImageUri);
//
////                imageList.add(uri);
////                newImageList.add(uri);
////                adapter.setList(imageList);
////                rv_multiple_images.setVisibility(View.VISIBLE);
////                img_layout.setVisibility(View.VISIBLE);
//////                    rv_multiple_images.setVisibility(View.GONE);
////                //picked single image
////                extention = getMimeType(getContext(), uri);
//            }
//        }
    }

    private void getIntentData() {
        if (getArguments().containsKey(POST_MODEL)) {
            PostModel postModel1 = new Gson().fromJson(getArguments().getString(POST_MODEL), PostModel.class);
            postId = postModel1.getId();
            postModel = postModel1;
            setData(postModel);
        }
        if (getArguments().containsKey(POST_ID)) {
            if (getArguments().getString(POST_ID) != null)
                postId = Integer.parseInt(getArguments().getString(POST_ID));
        }
        if (getArguments().containsKey(USER_ID)) {
            userId = getArguments().getInt(USER_ID, -1);
        }
        if (postModel == null) {
            getPostFromApiId(String.valueOf(postId));
        }
        getPostComments();
        if (postModel.getTotalShares() > 0) {
            txt_share.setVisibility(View.VISIBLE);

            txt_share.setText(postModel.getTotalShares() + " shares..");
        } else {
            txt_share.setVisibility(View.GONE);
        }

    }

    private void getPostComments() {
        if (currentPageNumber <= 1) {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().getCommentByPostId(postId, currentPageNumber), getActivity(), new ApiResponseHandler<GenericDataModel<CommentModel>>() {
                @Override
                public void onSuccess(Response<ApiResponse<GenericDataModel<CommentModel>>> data) {
                    swipe_to_refresh.setRefreshing(false);
                    if (data.body().getData().getData().size() > 0) {
                        rv_comments.setVisibility(View.VISIBLE);
                        currentPageNumber = data.body().getData().getCurrentPage();
                        nextPageUrl = data.body().getData().getNextPageUrl();
                        if (currentPageNumber == 1) {
                            commentModelList = new ArrayList<>();
                            commentReplyModelList = new ArrayList<>();
                            commentModelList = data.body().getData().getData();
                            for (int i = 0; i < commentModelList.size(); i++) {
                                commentReplyModelList.addAll(commentModelList.get(i).getChildren());
                            }
                        } else {
                            commentModelList.addAll(data.body().getData().getData());
                        }
                        commentAdapter.setData(commentModelList, postModel);
                        commentReplyAdapter.setData(commentReplyModelList, postModel);
                        commentAdapter.notifyDataSetChanged();
                        commentReplyAdapter.notifyDataSetChanged();
                        tv_no_comments.setVisibility(View.GONE);
//                        commentModelList.clear();
//                        setCommentData(data.body().getData().getData());

                    } else {
                        tv_no_comments.setVisibility(View.VISIBLE);

                    }
                }
            });
        }

    }

    private void setCommentData(List<CommentModel> data) {
        commentModelList.addAll(data);
        commentAdapter.notifyDataSetChanged();
    }

    private void setData(PostModel postModel) {
        this.postModel = postModel;
        if (postModel.getUserId() == postModel.getProfileId())
            profile.setVisibility(View.GONE);
        else
            profile.setVisibility(View.VISIBLE);
        txt_name.setText(postModel.getUser().getName());
        txt_profile_name.setText(postModel.getProfile().getName());
        txt_des.setText(postModel.getDescription());
        Glide.with(requireActivity()).load(getUrlforPicture(postModel.getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_profile);
        Glide.with(requireActivity()).load(getUrlforPicture(userModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(civ_user_img);
        if (postModel.getTotalReactions() > 0) {
            txt_reaction.setVisibility(View.VISIBLE);
            if (postModel.getMyReaction() != 0) {
                int totalReactions = postModel.getTotalReactions() - 1;
                if (totalReactions > 0)
                    txt_reaction.setText("You and " + totalReactions + " other reacted to this post");
                else
                    txt_reaction.setText("You have reacted to this post");
            } else {
                txt_reaction.setText(postModel.getTotalReactions() + " person reacted to this post");
            }
        } else {
            txt_reaction.setVisibility(View.GONE);
        }
        setAttachment(postModel, false);
//        txt_date.setText(DateUtils.getRelativeTimeSpanString(BaseUtils.getDate(postModel.getCreatedAt())));
        txt_date.setText(BaseUtils.convertFromUTCTime(postModel.getCreatedAt()) + " " + BaseUtils.getTimeFromDate(postModel.getCreatedAt()));
        if (postModel.getAttachments().size() == 0)
            setImagePreview(postModel, false);
        checkPostPrivacy(postModel, false);
//        try {
//            setYoutubePreview(postModel);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if (postModel.getShared_id() != null) {
            card_shared.setVisibility(View.VISIBLE);
            sharedPostModel = postModel.getSharedPost();
            if (sharedPostModel.getUserId() == sharedPostModel.getProfileId())
                shared_profile.setVisibility(View.GONE);
            else
                shared_profile.setVisibility(View.VISIBLE);
            txt_shared_name.setText(sharedPostModel.getUser().getName());
            BaseUtils.setVerifiedAccount(sharedPostModel.getUser().getVerified(), txt_shared_name);
            txt_shared_profile_name.setText(sharedPostModel.getProfile().getName());
            BaseUtils.setVerifiedAccount(sharedPostModel.getProfile().getVerified(), txt_shared_profile_name);
            Glide.with(MyApplication.getAppContext()).load(getUrlforPicture(sharedPostModel.getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_shared_profile);
            txt_shared_des.setText(sharedPostModel.getDescription());
            if (sharedPostModel.getAttachments().size() == 0)
                setImagePreview(sharedPostModel, true);
            checkPostPrivacy(sharedPostModel, true);
            setAttachment(sharedPostModel, true);
        } else {
            card_shared.setVisibility(View.GONE);
        }
        if (Constants.APPENV == ENV.review) {
            rl_blur_paid_view.setVisibility(View.GONE);
            rl_shared_blur_paid_view.setVisibility(View.GONE);
            iv_paid.setVisibility(View.GONE);
        }
    }

    private void checkPostPrivacy(PostModel postModel, boolean isShared) {
        if (postModel.getPaid().equals("1") && postModel.getIsPaid() == 0 && postModel.getUserId() != userModel.getId()) {
            if (isShared) {
                rl_shared_blur_paid_view.setVisibility(View.VISIBLE);
                tv_shared_pay_to_unlock.setText(getString(R.string.pay__to_view_this_post, String.valueOf((int) postModel.getAmount())));
            } else {
                rl_blur_paid_view.setVisibility(View.VISIBLE);
                tv_pay_to_unlock.setText(getString(R.string.pay__to_view_this_post, String.valueOf((int) postModel.getAmount())));
            }
            ll_privacy_item.setVisibility(View.GONE);

        } else {
            if (isShared) {
                rl_shared_blur_paid_view.setVisibility(View.GONE);
            } else {
                rl_blur_paid_view.setVisibility(View.GONE);
            }
            ll_privacy_item.setVisibility(View.VISIBLE);
        }
        if (postModel.getPaid().equals("1")) {
            iv_paid.setVisibility(View.VISIBLE);
        } else
            iv_paid.setVisibility(View.GONE);
    }

    private void setImagePreview(PostModel postModel, boolean isShared) {
        if (postModel == null)
            return;
        if (postModel.getDescription() != null && !postModel.getDescription().equals("")) {
            if (isContainLink(postModel.getDescription())) {
                if (isShared)
                    layout_shared_preview.setVisibility(View.VISIBLE);
                else
                    layout_preview.setVisibility(View.VISIBLE);
                String url = extractUrls(postModel.getDescription());
                if (!url.equalsIgnoreCase("")) {

                    RichPreview richPreview = new RichPreview(new ResponseListener() {
                        @Override
                        public void onData(MetaData metaData) {
                            if (isShared) {
                                Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(metaData.getImageurl())).into(img_shared_preview_image);
                                txt_shared_preview_title.setText(metaData.getTitle());
                                txt_shared_preview_des.setText(metaData.getUrl());
                            } else {
                                Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(metaData.getImageurl())).into(img_preview_image);
                                txt_preview_title.setText(metaData.getTitle());
                                txt_preview_des.setText(metaData.getUrl());
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
                    richPreview.getPreview(url);
                }
            } else {
                if (isShared)
                    layout_shared_preview.setVisibility(View.GONE);
                else
                    layout_preview.setVisibility(View.GONE);
            }
        }
    }

    private void setAttachment(PostModel postModel, boolean isShared) {

        if (postModel.getAttachments().size() > 0) {
            if (isShared) {
                image_shared_slider.setVisibility(View.VISIBLE);
                rl_image.setVisibility(View.VISIBLE);
            } else
                image_slider.setVisibility(View.VISIBLE);

            if (BaseUtils.checkExtentionValidation(postModel.getAttachments().get(0).getPath()) == 0) {
                isImage = true;
                if (isShared) {
                    image_shared_slider.setVisibility(View.GONE);
                    rl_image.setVisibility(View.GONE);
                    if (postModel.getAttachments() != null) {
                        if (postModel.getAttachments().size() > 0) {
                            image_shared_slider.setVisibility(View.VISIBLE);
                            rl_image.setVisibility(View.VISIBLE);
//                        List<SlideModel> slideModelList = new ArrayList<>();
                            ArrayList<String> slideModelList = new ArrayList<>();
                            for (int i = 0; i < postModel.getAttachments().size(); i++) {
//                                if (postModel.getSharedPost() != null)
                                slideModelList.add(BaseUtils.getUrlforPicture(postModel.getAttachments().get(i).getPath()));
                            }
                            if (slideModelList.size() > 1)
                                image_shared_slider_tabDots.setupWithViewPager(image_shared_slider, true);
                            imageSharedAdapter.setImages(slideModelList, true);
                        }
                    }

                }
//                    Glide.with(this).load(getUrlforPicture(postModel.getAttachments().get(0).getPath())).placeholder(R.drawable.cover_place_holder).into(img_shared_picture);
                else {
                    image_slider.setVisibility(View.GONE);
                    if (postModel.getAttachments().size() > 0) {
                        image_slider.setVisibility(View.VISIBLE);
//                        List<SlideModel> slideModelList = new ArrayList<>();
                        ArrayList<String> slideModelList = new ArrayList<>();
                        for (int i = 0; i < postModel.getAttachments().size(); i++) {
                            slideModelList.add(BaseUtils.getUrlforPicture(postModel.getAttachments().get(i).getPath()));
                        }
                        if (slideModelList.size() > 1)
                            image_slider_tabDots.setupWithViewPager(image_slider, true);
                        imageAdapter.setImages(slideModelList, true);
                    }
                }
                //TODO
//                image_slider.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onItemSelected(int i) {
//                        if (postModel.getAttachments().get(i).getType().equals("image")) {
//                            ArrayList<String> uriString = new ArrayList<>();
//                            uriString.add(getUrlforPicture(postModel.getAttachments().get(i).getPath()));
//                            new ImageViewer.Builder(PostDetailActivity.this, uriString)
//                                    .setStartPosition(0)
//                                    .show();
//                        }
//
//                    }
//                });
//                image_shared_slider.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onItemSelected(int i) {
//                        if (sharedPostModel.getAttachments().get(i).getType().equals("image")) {
//                            ArrayList<String> uriString = new ArrayList<>();
//                            uriString.add(getUrlforPicture(sharedPostModel.getAttachments().get(i).getPath()));
//                            new ImageViewer.Builder(PostDetailActivity.this, uriString)
//                                    .setStartPosition(0)
//                                    .show();
//                        }
//
//                    }
//                });
//                    Glide.with(this).load(getUrlforPicture(postModel.getAttachments().get(0).getPath())).placeholder(R.drawable.cover_place_holder).into(img_picture);

            } else if (BaseUtils.checkExtentionValidation(postModel.getAttachments().get(0).getPath()) == 1) {
                isVideo = true;
                String thumbnail;
                if (postModel.getThumbnail() == null) {
                    thumbnail = getUrlforPicture(postModel.getAttachments().get(0).getThumbnail());

                } else {
                    thumbnail = getUrlforPicture(postModel.getThumbnail());

                }
                if (isShared) {
                    iv_shared_play_video.setVisibility(View.VISIBLE);
                    image_shared_slider.setVisibility(View.GONE);
                    rl_image.setVisibility(View.GONE);
                    if (postModel.getAttachments().size() > 0) {
                        image_shared_slider.setVisibility(View.VISIBLE);
                        rl_image.setVisibility(View.VISIBLE);
//                        List<SlideModel> slideModelList = new ArrayList<>();
                        ArrayList<String> slideModelList = new ArrayList<>();
                        slideModelList.add(thumbnail);
                        if (slideModelList.size() > 1)
                            image_shared_slider_tabDots.setupWithViewPager(image_shared_slider, true);
                        imageSharedAdapter.setImages(slideModelList, false);
                    }
//                    Glide.with(this).load(thumbnail).placeholder(R.drawable.cover_place_holder).into(img_shared_picture);
                } else {
                    iv_play_video.setVisibility(View.VISIBLE);
                    image_slider.setVisibility(View.GONE);
                    if (postModel.getAttachments().size() > 0) {
                        image_slider.setVisibility(View.VISIBLE);
//                        List<SlideModel> slideModelList = new ArrayList<>();
                        ArrayList<String> slideModelList = new ArrayList<>();
                        slideModelList.add(thumbnail);
                        if (slideModelList.size() > 1)
                            image_slider_tabDots.setupWithViewPager(image_slider, true);
                        imageAdapter.setImages(slideModelList, false);
                    }
//                    Glide.with(this).load(thumbnail).placeholder(R.drawable.cover_place_holder).into(img_picture);
                }
                iv_play_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addViewCount(postModel.getId() + "");
                        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                        String share_Count = postModel.getTotalShares() + "";
                        intent.putExtra(VIDEO_SHARE_COUNT, share_Count);
                        String view_Count = postModel.getTotalViews() + "";
                        intent.putExtra(VIDEO_VIEW_COUNT, view_Count);
                        intent.putExtra(URL, postModel.getAttachments().get(0).getPath());
                        startActivity(intent);
                    }
                });
                iv_shared_play_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addViewCount(postModel.getId() + "");
                        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                        String share_Count = postModel.getTotalShares() + "";
                        intent.putExtra(VIDEO_SHARE_COUNT, share_Count);
                        String view_Count = postModel.getTotalViews() + "";
                        intent.putExtra(VIDEO_VIEW_COUNT, view_Count);
                        intent.putExtra(URL, postModel.getAttachments().get(0).getPath());
                        startActivity(intent);
                    }
                });

            } else {
                iv_shared_play_video.setVisibility(View.GONE);
                iv_play_video.setVisibility(View.GONE);
            }

        } else {
            iv_play_video.setVisibility(View.GONE);
            iv_shared_play_video.setVisibility(View.GONE);
            image_slider.setVisibility(View.GONE);
            image_shared_slider.setVisibility(View.GONE);
            rl_image.setVisibility(View.GONE);
        }


        isImage = false;
        isVideo = false;
    }

    private void getPostFromApiId(String postId) {
        if (postId != null) {
            dialog.show();
            ApiManager.apiCallWithFailure(new ApiClient().getInterface().getPostById(postId), getActivity(), new ApiResponseHandlerWithFailure<PostModel>() {
                @Override
                public void onSuccess(Response<ApiResponse<PostModel>> data) {
                    setData(data.body().getData());
                    dialog.dismiss();
                }

                @Override
                public void onFailure(String failureCause) {
                    dialog.dismiss();

                    requireActivity().onBackPressed();
                }
            });
        }


//                new ApiResponseHandler<PostModel>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<PostModel>> data) {
//                setData(data.body().getData());
//                dialog.dismiss();
//            }
//        });

    }

    private void setUpReactionsClick() {
        ReactionsConfig config = new ReactionsConfigBuilder(getActivity())
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

        popup = new ReactionPopup(getActivity(), config, (p) -> {
            switch (p) {
                case 0:
                    reactPost(postModel, 1);
                    break;
                case 1:
                    reactPost(postModel, 2);
                    break;
                case 2:
                    reactPost(postModel, 3);
                    break;
                case 3:
                    reactPost(postModel, 4);
                    break;
                case 4:
                    reactPost(postModel, 5);
                    break;
                case 5:
                    reactPost(postModel, 6);
                    break;
                case 6:
                    reactPost(postModel, 7);
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void reactPost(PostModel mPostModel, int id) {
        if (mPostModel != null) {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().react(mPostModel.getId(), id), getActivity(), new ApiResponseHandler<ReactionModel>() {
                @Override
                public void onSuccess(Response<ApiResponse<ReactionModel>> data) {
                    UserModel userModel = Paper.book().read(Constants.currentUser);
                    if (data.body().getMessage().equals("Reaction Removed Successfully")) {
                        postModel.setTotalReactions(postModel.getTotalReactions() - 1);
                        if (data.body().getData().getReactorId() == userModel.getId())
                            postModel.setMyReaction(0);
                    } else {
                        if (postModel.getMyReaction() == 0) {
                            postModel.setTotalReactions(postModel.getTotalReactions() + 1);
                            if (data.body().getData().getReactorId() == userModel.getId())
                                postModel.setMyReaction(1);
                        }
                    }
                    setData(postModel);
                }
            });
        }
    }

    private void initComponents(View view) {
        dialog = BaseUtils.progressDialog(getActivity());

        Paper.init(getActivity());
        userModel = Paper.book().read(currentUser);

        txt_shared_name = view.findViewById(R.id.txt_shared_name);
        img_uri = view.findViewById(R.id.img_uri);
        img_cancel = view.findViewById(R.id.img_comment_cancel);
        imgevieid = view.findViewById(R.id.imgevieid);
        txt_shared_profile_name = view.findViewById(R.id.txt_shared_profile_name);
        txt_shared_content = view.findViewById(R.id.txt_shared_content);
        tv_shared_pay_to_unlock = view.findViewById(R.id.tv_shared_pay_to_unlock);
        image_shared_slider = view.findViewById(R.id.image_shared_slider);
        iv_shared_play_video = view.findViewById(R.id.iv_shared_play_video);
        img_shared_profile = view.findViewById(R.id.img_shared_profile);
        shared_profile = view.findViewById(R.id.shared_profile);
        rl_shared_blur_paid_view = view.findViewById(R.id.rl_shared_blur_paid_view);
        txt_shared_des = view.findViewById(R.id.txt_shared_des);
        txt_shared_preview_title = view.findViewById(R.id.txt_shared_preview_title);
        txt_shared_preview_des = view.findViewById(R.id.txt_shared_preview_des);
        img_shared_preview_image = view.findViewById(R.id.img_shared_preview_image);
        layout_shared_preview = view.findViewById(R.id.layout_shared_preview);
        rl_blur_paid_view = view.findViewById(R.id.rl_blur_paid_view);
        tv_pay_to_unlock = view.findViewById(R.id.tv_pay_to_unlock);
        ll_privacy_item = view.findViewById(R.id.ll_privacy_item);
        iv_paid = view.findViewById(R.id.iv_paid);


        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        tv_post_option = view.findViewById(R.id.iv_post_option);
        iv_play_video = view.findViewById(R.id.iv_play_video);
        tv_no_comments = view.findViewById(R.id.tv_no_comments);
        card_shared = view.findViewById(R.id.card_shared);

        txt_preview_title = view.findViewById(R.id.txt_preview_title);
        txt_preview_des = view.findViewById(R.id.txt_preview_des);
        img_preview_image = view.findViewById(R.id.img_preview_image);
        layout_preview = view.findViewById(R.id.layout_preview);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        app_bar = findViewById(R.id.app_bar);
//        app_bar.addOnOffsetChangedListener(this);
//
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        profile = view.findViewById(R.id.profile);
        et_comment = view.findViewById(R.id.et_comment);
        civ_user_img = view.findViewById(R.id.civ_user_img_comment_bar);
        txt_name = view.findViewById(R.id.txt_name);
        txt_profile_name = view.findViewById(R.id.txt_profile_name);
        img_upload = view.findViewById(R.id.img_upload);
        tv_comment_text = view.findViewById(R.id.tv_comment_text);
        txt_date = view.findViewById(R.id.txt_date);
        txt_des = view.findViewById(R.id.txt_des);
        txt_reaction = view.findViewById(R.id.txt_reaction);
        img_profile = view.findViewById(R.id.img_profile);
        image_slider = view.findViewById(R.id.image_slider);
        img_reaction = view.findViewById(R.id.img_reaction);
        img_share = view.findViewById(R.id.img_share);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);

        progress_bar = view.findViewById(R.id.progress_bar);
        card_for_tags = view.findViewById(R.id.card_tags);
        image_slider_tabDots = view.findViewById(R.id.image_slider_tabDots);
        image_shared_slider_tabDots = view.findViewById(R.id.image_shared_slider_tabDots);
        rl_image = view.findViewById(R.id.rl_image);
        txt_share = view.findViewById(R.id.txt_share);

        imageAdapter = new ImageSliderAdapter(getActivity());
        image_slider.setAdapter(imageAdapter);

        imageSharedAdapter = new ImageSliderAdapter(getActivity());
        image_shared_slider.setAdapter(imageSharedAdapter);

        rv_tags = view.findViewById(R.id.rv_tags);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        userTagAdapter = new UserTagAdapter(getActivity(), followModelList);
        rv_tags.setLayoutManager(layoutManager);
        rv_tags.setAdapter(userTagAdapter);
        userTagAdapter.initInterface(new UserTagAdapter.IuserSelected() {
            @Override
            public void selected(FollowModel follower) {
                String username = follower.getFollowingUsername();
                if (!et_comment.getText().toString().contains(username)
                        && addFollowersFiltering.contains(follower)) {
                    addFollowersFiltering.remove(follower);
                    ids.remove(follower.getFollowingId());
                    tags.remove(username);
                }
                if (!addFollowersFiltering.contains(follower)) {
                   /* if(et_comment.getText().toString().length()==1)
                    et_comment.setText("@"+username);*/
                    IdsAndTagsListModel idsAndTagsListModel = updateETwithTags(follower, et_comment, addFollowersFiltering);
                    ids.add(idsAndTagsListModel.getIds());
                    tags.add(idsAndTagsListModel.getTags());

                }
                card_for_tags.setVisibility(View.GONE);
            }

        });
    }

//    @Override
//    protected void onDestroy() {
//        dialog.dismiss();
//        super.onDestroy();
//    }

//    public static void setTagsForPosts(Context context, PostModel postData, TextView txt_des, ImageView img_picture, int position) throws MalformedURLException, UnsupportedEncodingException {
//        if (postData == null || context == null)
//            return;
//        List<TagModel> tagsData = new ArrayList<>();
//        PostModel postModel = postData;
//        if (postData.getDescription() != null && !postData.getDescription().equals("")) {
//            if (isContainLink(postData.getDescription())) {
//                String url = extractUrls(postData.getDescription());
//                if (!url.equalsIgnoreCase("")) {
//                    //make sure it's youtube
//                    if (url.contains("youtube") || url.contains("youtu")) {
//                        java.net.URL url1 = new URL(url);
//                        String a = splitQuery(url1);
//                        if (a != null) {
//                            String youtube;
//                            youtube = "https://img.youtube.com/vi/" + a + "/hqdefault.jpg";
//                            if (postData.getAttachments().size() > 0)
//                                postData.getAttachments().get(0).setThumbnail(youtube);
//                            else {
//                                img_picture.setVisibility(View.VISIBLE);
//                                Glide.with(context).load(youtube).into(img_picture);
//                            }
//                        } else {
//                            Log.e("SplitUrl", "null");
//                        }
//                    }
//
//                    if (postData.getTagsData().size() > 0) {
//                        Log.e("@#$@#$1", postModel.getDescription() + "");
//
//                        tagsData = postData.getTagsData();
//                        highlightTagsForPost(context, txt_des, postData, position);
//                    } else {
//                        txt_des.setText(postData.getDescription());
//                    }
//                }
//            } else {
//                if (postData.getTagsData().size() > 0) {
//                    Log.e("@#$@#$1", postModel.getDescription() + "");
//
//                    tagsData = postData.getTagsData();
//                    highlightTagsForPost(context, txt_des, postData, position);
//                } else {
//                    txt_des.setText(postData.getDescription());
//                }
//            }
//            if (postData.getDescription().contains("#")) {
//                highlightTagsForPost(context, txt_des, postData, position);
//            }
//        }
//    }

//    public static String splitQuery(java.net.URL url) throws UnsupportedEncodingException {
//        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
//        String value = null;
//        String query = url.getQuery();
//        if (query != null) {
//            String[] pairs = query.split("&");
//            for (String pair : pairs) {
//                int idx = pair.indexOf("=");
//                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
//                if (pair.substring(0, idx).equals("v")) {
//                    value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
//                }
//            }
//        }
//
//        return value;
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_profile) {
            if (postModel.getUserId() != userId)
                ((HomeActivity) getActivity()).openUserProfile(postModel.getUser().getId().toString());
//                BaseUtils.openUserProfile(postModel.getUser().getId().toString(), getActivity());
        } else if (id == R.id.txt_profile_name) {
            if (postModel.getProfileId() != userId)
                ((HomeActivity) getActivity()).openUserProfile(postModel.getProfile().getId().toString());
//                BaseUtils.openUserProfile(postModel.getProfile().getId().toString(), getActivity());
        } else if (id == R.id.txt_name) {
            if (postModel.getUserId() != userId)
                ((HomeActivity) getActivity()).openUserProfile(postModel.getUser().getId().toString());
//                BaseUtils.openUserProfile(postModel.getUser().getId().toString(), getActivity());
        } else if (id == R.id.iv_post_option) {

        } else if (id == R.id.tv_comment_text) {
            String comment = Objects.requireNonNull(et_comment.getText()).toString();
            if (comment.isEmpty()) {
                et_comment.setError(getString(R.string.field_cant_be_empty));
                et_comment.setFocusable(true);
                return;
            }
            IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_comment, addFollowersFiltering, ids, tags);

            commentRequestModel.setComment(comment);
            commentRequestModel.setTags(idsAndTagsListModel.getTags());
            commentRequestModel.setIds(idsAndTagsListModel.getIds());
            commentRequestModel.setPost_id(postId);
            System.out.println("uploadAttachmentPath");
            System.out.println(uploadAttachmentPath);
            if (uploadAttachmentPath == null) {
                commentRequestModel.setAttachment(" ");
            }

            commentReplyRequestModel.setComment(comment);
            commentReplyRequestModel.setTags(idsAndTagsListModel.getTags());
            commentReplyRequestModel.setIds(idsAndTagsListModel.getIds());
            commentReplyRequestModel.setPost_id(postId);
            commentReplyRequestModel.setCommenter_id(userModel.getId());


            if (editedComment && uploadAttachment == null) {
                editComment(commentRequestModel);
            } else if (commentReply && uploadAttachment == null) {
                Log.e("yes got u", "onClick: ");
                addCommentReply(commentReplyRequestModel);
            } else if (commentReply && uploadAttachment != null) {
                uploadMultipleFilesToServer();
            } else if (editedCommentReply) {
                Log.e("on", "onClick: ");
                editCommentReply(commentReplyRequestModel);
            } else if (editedComment && uploadAttachment != null) {
                uploadMultipleFilesToServer();
            } else if (uploadAttachment != null) {
                uploadMultipleFilesToServer();
            } else {
                addComment(commentRequestModel);
            }
        }
    }

    private void editComment(CommentRequestModel commentRequestModel) {

        CommentModel mCommentModel = findCommentObjectWithCommentId(commentId);
        CommentModel tempForFailureCause = mCommentModel;
        mCommentModel.setComment(commentRequestModel.getComment());
        mCommentModel.setIds(commentRequestModel.getIds());
        mCommentModel.setTag(commentRequestModel.getTags());
        System.out.println("UPLOADIMAGEPATH");
        System.out.println(commentId);
        System.out.println(uploadAttachmentPath);
        if (uploadAttachmentPath != null) {
            mCommentModel.setAttachment(uploadAttachmentPath);
        } else {

            commentRequestModel.setAttachment(" ");
            mCommentModel.setAttachment(null);
        }
        commentModelList.set(editCommentPosition, mCommentModel);

        commentAdapter.notifyDataSetChanged();

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().editComment(commentRequestModel), getActivity(), new ApiResponseHandlerWithFailure<CommentModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<CommentModel>> data) {
                et_comment.setText("");

                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(String failureCause) {

                et_comment.setText("");

                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);

                commentModelList.set(editCommentPosition, tempForFailureCause);
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void editCommentReply(CommentReplyRequestModel commentReplyRequestModel) {
        ReplyModel mCommentModel = findCommentReplyObjectWithCommentId(commentIdReply);
        Log.e("ids", "editCommentReply: " + mCommentModel.getId());
        ReplyModel tempForFailureCause = mCommentModel;
        mCommentModel.setComment(commentReplyRequestModel.getComment());
        mCommentModel.setIds(commentReplyRequestModel.getIds());
        mCommentModel.setTag(commentReplyRequestModel.getTags());

        if (uploadAttachmentPath != null) {
            mCommentModel.setAttachment(uploadAttachmentPath);
        } else {

            commentReplyRequestModel.setAttachment(" ");
            commentReplyRequestModel.setAttachment(null);
        }

        commentReplyModelList.set(editCommentReplyPosition, mCommentModel);

        commentReplyAdapter.notifyDataSetChanged();

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().editCommentReply(commentReplyRequestModel), getActivity(), new ApiResponseHandlerWithFailure<ReplyModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<ReplyModel>> data) {
                Log.e("response", "onSuccess: " + data.body().getStatus());

                et_comment.setText("");

                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);
                Log.e("on sucess", "onSuccess: ");
            }

            @Override
            public void onFailure(String failureCause) {

                Log.e("failure response", "onFailure: " + failureCause);

                et_comment.setText("");

                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);

                commentReplyModelList.set(editCommentReplyPosition, tempForFailureCause);
                commentReplyAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addComment(CommentRequestModel comment) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().postComment(comment), getActivity(), new ApiResponseHandlerWithFailure<CommentModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<CommentModel>> data) {
                et_comment.setText("");
                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);

                assert data.body() != null;
                addPost(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                et_comment.setText("");

            }
        });

    }

    private void addCommentReply(CommentReplyRequestModel comment) {

        Log.e("in add", "addCommentReply: ");

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().postCommentReply(comment), getActivity(), new ApiResponseHandlerWithFailure<ReplyModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<ReplyModel>> data) {
                et_comment.setText("");
                uploadAttachment = null;
                uploadAttachmentExtension = null;
                uploadAttachmentPath = null;
                imgevieid.setVisibility(View.GONE);
                System.out.println("currentPageNumber");
                System.out.println(currentPageNumber);
                currentPageNumber = 1;
                getIntentData();

                assert data.body() != null;
                addCommentReply(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                et_comment.setText("");

            }
        });

    }

    private void addCommentReply(ReplyModel data) {
        System.out.println("COMMENTER NAME");
        commentReplyModelList.add(0, data);
        commentReplyAdapter.setData(commentReplyModelList, postModel);
//        commentAdapter.setCommentReplyData(commentReplyModelList, postModel);

        tv_no_comments.setVisibility(View.GONE);
        commentReplyAdapter.notifyDataSetChanged();
        commentAdapter.notifyDataSetChanged();
    }

    private void addPost(CommentModel data) {
        commentModelList.add(0, data);
        commentAdapter.setData(commentModelList, postModel);
        tv_no_comments.setVisibility(View.GONE);
//        commentAdapter.notifyDataSetChanged();
    }


//    public static void addIntent(Context context, PostModel post_model, int userId) {
//        Intent intent = new Intent(context, PostDetailFragment.class);
//
//        Pair[] pairs = new Pair[2];
//        pairs[0] = new Pair<View, String>(holder.img_event, context.getResources().getString(R.string.event_image));
//        pairs[1] = new Pair<View, String>(holder.tv_event_title, context.getResources().getString(R.string.event_title));
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
//
//        intent.putExtra(POST_MODEL, post_model);
//        intent.putExtra(USER_ID, userId);
//        context.startActivity(intent);
//    }

//    public static void addIntent(Context context, int postId, int userId) {
//        Intent intent = new Intent(context, PostDetailFragment.class);
//        intent.putExtra(POST_ID, postId);
//        intent.putExtra(USER_ID, userId);
//        context.startActivity(intent);
//    }

//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        swipe_to_refresh.setEnabled(verticalOffset == 0);
//    }

    private CommentModel findCommentObjectWithCommentId(String id) {
        for (CommentModel comment : commentModelList) {
            if (comment.getId().toString().equals(id)) {
                System.out.println("COMMENT WITH ID ");
                System.out.println(comment.getComment());
                return comment;
            }
        }
        return null;
    }

    private ReplyModel findCommentReplyObjectWithCommentId(String id) {
        for (ReplyModel comment : commentReplyModelList) {
            if (comment.getId().toString().equals(id)) {
                return comment;
            }
        }
        return null;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getActivity().onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        if (isTaskRoot()) {
//            startActivity(new Intent(PostDetailActivity.this, HomeActivity.class));
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onDestroy() {

        ApiClient.getInstance().getInterface().getCommentByPostId(postId, currentPageNumber).cancel();

        if (((HomeActivity) getActivity()).isTaskRoot(this)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_dashboard, bundle);
        }
//        else {
//            getActivity().onBackPressed();
//        }
        super.onDestroy();
    }

    @Override
    public void updatePlayerState() {

    }
}