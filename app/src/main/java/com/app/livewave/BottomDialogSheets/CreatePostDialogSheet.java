package com.app.livewave.BottomDialogSheets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.adapters.MultipleImageAdapter;
import com.app.livewave.adapters.UserTagAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.MessageInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.RemoveImage;
import com.app.livewave.interfaces.TagInterface;
import com.app.livewave.interfaces.UploadingProgressInterface;
import com.app.livewave.models.IdsAndTagsListModel;
import com.app.livewave.models.ParameterModels.AttachmentParams;
import com.app.livewave.models.ParameterModels.CreatePostModel;
import com.app.livewave.models.ParameterModels.OnRefreshPost;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.utils.FirebaseUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.checkExtentionValidation;
import static com.app.livewave.utils.BaseUtils.getFileExtention;
import static com.app.livewave.utils.BaseUtils.getMimeType;
import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.BaseUtils.updateETwithTags;
import static com.app.livewave.utils.setDescriptionsDataUtils.URL_REGEX;

public class CreatePostDialogSheet extends BottomSheetDialogFragment implements TagInterface {
    private CircleImageView img_profile;
    private TextView txt_name, tv_post_text;
    //    private SocialAutoCompleteTextView et_post;
    private TextInputEditText et_post;
    private ImageView img_cancel, img_upload, img_post, iv_clear_attachment;
    private SwitchMaterial switch_paid;
    private MaterialCardView card_post, card_cancel, single_image_card;
    private UserModel userModel;
    private int userId;
    private ArrayAdapter<UserModel> mentionAdapter;
    private int REQUEST_IMAGE = 1;
    private int REQUEST_IMAGE_CAPTURE = 101;
    private LinearLayout img_layout;
    private Uri uri;
    private String dialogType;
    private PostModel postModelForEditPost;
    private boolean isPostImageSelected;
    private Uri uploadAttachment;
    private String isPaid = "0", amount;
    private FirebaseUtils utils;
    private String videoUrl;
    private CreatePostModel createPostModel;
    private RecyclerView rv_tags, rv_multiple_images;
    private boolean editingPost;
    private String extention, duration;
    ArrayList<AttachmentParams> attachmentList = new ArrayList<>();
    private ArrayList<FollowModel> addFollowersFiltering = new ArrayList<>();
    private List<FollowModel> followModelList = new ArrayList<>();
    private UserTagAdapter userTagAdapter;
    private List<String> ids = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<Uri> newImageList = new ArrayList<>();
    ArrayList<String> deleteImageList = new ArrayList<>();
    ArrayList<AttachmentParams> currentImageList = new ArrayList<>();
    MultipleImageAdapter adapter;
    int count = 0;
    private ProgressBar progress_bar;
    private MaterialCardView card_for_tags;
    KProgressHUD loadingDialog, dialog;
//    String preview_url = null;

    String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";


    //Dialog type is for edit or add post
    public CreatePostDialogSheet(String mDialogType, int id) {
        this.userId = id;
        this.dialogType = mDialogType;
    }

    public CreatePostDialogSheet(String mDialogType, int id, PostModel postModel) {
        this.userId = id;
        this.dialogType = mDialogType;
        this.postModelForEditPost = postModel;
        this.editingPost = true;
    }

    public CreatePostDialogSheet() {
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);
        initViews(view);
        initCLickListeners();
        if (editingPost)
            setEditData();

        if (Constants.APPENV == ENV.review) {
            switch_paid.setVisibility(View.GONE);
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setEditData() {
        et_post.setText(postModelForEditPost.getDescription());
        BaseUtils.highlightTags(getContext(), et_post, postModelForEditPost.getDescription());

        ids.clear();
        tags.clear();
        ids.add(postModelForEditPost.getIds());
        tags.add(postModelForEditPost.getTags());


        if (postModelForEditPost.getAttachments() != null && postModelForEditPost.getAttachments().size() > 0) {
            imageList = new ArrayList<>();
            currentImageList = new ArrayList<>();
            for (int i = 0; i < postModelForEditPost.getAttachments().size(); i++) {
                extention = postModelForEditPost.getAttachments().get(i).getExtension();
                duration = String.valueOf(postModelForEditPost.getAttachments().get(i).getDuration());
                if (postModelForEditPost.getPaid().equals("1")) {
                    switch_paid.setOnCheckedChangeListener(null);
                    switch_paid.setChecked(true);
                    initCLickListeners();
                    isPaid = String.valueOf(postModelForEditPost.getIsPaid());
                    amount = String.valueOf(postModelForEditPost.getAmount());
                    switch_paid.setText(getString(R.string.paid_post_amount, amount));
                }
                img_layout.setVisibility(View.VISIBLE);
                if (postModelForEditPost.getAttachments().get(i).getThumbnail() == null) {
                    imageList.add(Uri.parse(postModelForEditPost.getAttachments().get(i).getPath()));
                } else {
                    imageList.add(Uri.parse(postModelForEditPost.getAttachments().get(i).getThumbnail()));
                }
                currentImageList.add(new AttachmentParams(postModelForEditPost.getAttachments().get(i).getPath(), postModelForEditPost.getAttachments().get(i).getExtension(), String.valueOf(postModelForEditPost.getAttachments().get(i).getDuration())));
            }
            adapter.setList(imageList);
            rv_multiple_images.setVisibility(View.VISIBLE);
        }
//        if (postModelForEditPost.getAttachments() != null && postModelForEditPost.getAttachments().size() > 0) {
//            if (postModelForEditPost.getPaid().equals("1")) {
//                switch_paid.setOnCheckedChangeListener(null);
//                switch_paid.setChecked(true);
//                initCLickListeners();
//                isPaid = String.valueOf(postModelForEditPost.getIsPaid());
//                amount = String.valueOf(postModelForEditPost.getAmount());
//                switch_paid.setText(getString(R.string.paid_post_amount, amount));
//            }
//            img_layout.setVisibility(View.VISIBLE);
//            if (postModelForEditPost.getAttachments().size() < 2)
//                single_image_card.setVisibility(View.VISIBLE);
//            if (postModelForEditPost.getAttachments().get(0).getThumbnail() == null) {
//                Glide.with(MyApplication.getAppContext()).load(postModelForEditPost.getAttachments().get(0).getPath()).placeholder(R.drawable.cover_place_holder).into(img_post);
//            } else {
//                Glide.with(MyApplication.getAppContext()).load(postModelForEditPost.getAttachments().get(0).getThumbnail()).placeholder(R.drawable.cover_place_holder).into(img_post);
//            }
//        }
        tv_post_text.setText("Edit");
    }

    private void initCLickListeners() {

        card_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_post.getText().toString())) {
                    et_post.setError("Field is required!");
                    et_post.requestFocus();
                } else {
//                    if (uri != null) {
//                        uploadFileToServer(uri);
//                    } else
                    if (editingPost) {
                        if (newImageList.size() > 0) {
                            uploadMultipleFilesToServer();
                        } else {
                            createPostModel = new CreatePostModel(et_post.getText().toString(), userId);
                            IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
                            createPostModel.setTags(idsAndTagsListModel.getTags());
                            createPostModel.setIds(idsAndTagsListModel.getIds());
                            createPostModel.setAttachments(currentImageList);
                            if (editingPost) {
                                if (currentImageList.size() == 0 && newImageList.size() == 0) {
                                    createPostModel.setAction(Constants.REMOVED);
                                }
//                                    if (postModelForEditPost.getAttachments().size() > 0) {
//
//                                    } else {
//                                        createPostModel.setAction(Constants.REMOVED);
//                                    }
                                createPostModel.setId(postModelForEditPost.getId());
                            }
                            writePost(createPostModel);
                        }
                    } else {
                        if (imageList.size() > 0) {
                            uploadMultipleFilesToServer();
                        } else {
                            createPostModel = new CreatePostModel(et_post.getText().toString(), userId);
                            IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
                            createPostModel.setTags(idsAndTagsListModel.getTags());
//                        createPostModel.setPreview_url(preview_url);
                            createPostModel.setIds(idsAndTagsListModel.getIds());
                            if (editingPost) {
                                if (postModelForEditPost.getAttachments().size() > 0) {

                                } else {
                                    createPostModel.setAction(Constants.REMOVED);
                                }
                                createPostModel.setId(postModelForEditPost.getId());
                            }
                            writePost(createPostModel);
                        }
                    }
                }
            }
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

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        iv_clear_attachment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uri = null;
//                img_layout.setVisibility(View.GONE);
//                if (postModelForEditPost != null) {
//                    if (postModelForEditPost.getAttachments().size() > 0) {
//                        postModelForEditPost.setAttachments(new ArrayList<>());
//                    }
//                }
//            }
//        });

        switch_paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EnterAmountDialogSheet enterAmountDialogSheet = new EnterAmountDialogSheet(new MessageInterface() {
                        @Override
                        public void IAmountEnter(String mAmount) {
                            if (mAmount != null) {
                                isPaid = "1";
                                amount = mAmount;
                                switch_paid.setText(getString(R.string.paid_post_amount, mAmount));
                            } else {
                                switch_paid.setChecked(false);
                            }
                        }
                    });
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    enterAmountDialogSheet.show(fm, "amount");
                } else {
                    isPaid = "0";
                    amount = "0";
                    switch_paid.setText(getString(R.string.paid_post_amount, ""));

                }
            }
        });

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

    private void uploadMultipleFilesToServer() {
//        dialog.show();
        loadingDialog.show();
        attachmentList = new ArrayList<>();
        if (editingPost) {
            for (int i = 0; i < newImageList.size(); i++) {
                File file = new File(BaseUtils.getPath(getContext(), newImageList.get(i)));
                String unique_name = UUID.randomUUID().toString();
                FirebaseApp.initializeApp(getContext());
                String ext = getMimeType(getContext(), newImageList.get(i));
                int validMediaFormat = checkExtentionValidation(ext);
                if (validMediaFormat == 0) {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
                    StorageReference storageReference = utils.getFireBaseStorageReferenece(ext, profilePath);
                    storageReference.putFile(newImageList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    attachmentList.add(new AttachmentParams(uri.toString(), ext, "0"));
                                    count++;
                                    int percent;
                                    if (newImageList.size() - count != 0)
                                        percent = 100 / (newImageList.size() - count);
                                    else
                                        percent = 100;
                                    loadingDialog.setProgress(percent);
                                    if (count == newImageList.size()) {
                                        count = 0;
                                        createPostModel = new CreatePostModel();
                                        attachmentList.addAll(currentImageList);
                                        createPostModel.setAttachments(attachmentList);
                                        createPostModel.setDescription(et_post.getText().toString());
                                        IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
                                        createPostModel.setTags(idsAndTagsListModel.getTags());
                                        createPostModel.setIds(idsAndTagsListModel.getIds());
                                        createPostModel.setProfile_id(userId);
                                        createPostModel.setExtension(extention);
                                        if (isPaid.equals("1")) {
                                            createPostModel.setPaid("1");
                                            createPostModel.setAmount(amount);
                                        } else {
                                            createPostModel.setPaid("0");
                                        }
                                        if (editingPost) {
                                            createPostModel.setId(postModelForEditPost.getId());
                                        }
                                        loadingDialog.dismiss();
                                        writePost(createPostModel);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/videos/" + unique_name;
                    if (file != null)
                        uploadVideo(ext, file, profilePath);
                }
            }
        } else {
            for (int i = 0; i < imageList.size(); i++) {
                File file = new File(BaseUtils.getPath(getContext(), imageList.get(i)));
                String unique_name = UUID.randomUUID().toString();
                FirebaseApp.initializeApp(getContext());
//            String ext = getFileExtention(file.getName());
                String ext = getMimeType(getContext(), imageList.get(i));
                int validMediaFormat = checkExtentionValidation(ext);
                if (validMediaFormat == 0) {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
                    StorageReference storageReference = utils.getFireBaseStorageReferenece(ext, profilePath);
                    storageReference.putFile(imageList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    attachmentList.add(new AttachmentParams(uri.toString(), ext, "0"));
                                    count++;
                                    int percent;
                                    if (imageList.size() - count != 0)
                                        percent = 100 / (imageList.size() - count);
                                    else
                                        percent = 100;
                                    loadingDialog.setProgress(percent);
                                    if (count == imageList.size()) {
                                        count = 0;
                                        createPostModel = new CreatePostModel();
                                        createPostModel.setAttachments(attachmentList);
                                        createPostModel.setDescription(et_post.getText().toString());
                                        IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
                                        createPostModel.setTags(idsAndTagsListModel.getTags());
                                        createPostModel.setIds(idsAndTagsListModel.getIds());
                                        createPostModel.setProfile_id(userId);
                                        createPostModel.setExtension(extention);
                                        if (isPaid.equals("1")) {
                                            createPostModel.setPaid("1");
                                            createPostModel.setAmount(amount);
                                        } else {
                                            createPostModel.setPaid("0");
                                        }
                                        if (editingPost) {
                                            createPostModel.setId(postModelForEditPost.getId());
                                        }
                                        loadingDialog.dismiss();
                                        writePost(createPostModel);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/videos/" + unique_name;
                    if (file != null)
                        uploadVideo(ext, file, profilePath);
                }
            }
        }

    }

//    private void uploadFileToServer(Uri uri) {
//        loadingDialog.show();
////        try {
//        File file = new File(BaseUtils.getPath(getContext(), uri));
//        String unique_name = UUID.randomUUID().toString();
//        FirebaseApp.initializeApp(getContext());
//
////        String ext = getFileExtention(file.getName());
//        String ext = getMimeType(getContext(), uri);
//        int validMediaFormate = checkExtentionValidation(ext);
//        if (validMediaFormate == 0) {
//            String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name;
//            uploadPhoto(ext, uri, profilePath);
//        } else {
//            String profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/videos/" + unique_name;
//            if (file != null)
//                uploadVideo(ext, file, profilePath);
//        }
////        }catch (Exception e){
////
////        }
//    }

//    private void uploadPhoto(String ext, Uri file, String profilePath) {
//        utils.uploadFileToFirebase(ext, profilePath, file, new UploadingProgressInterface() {
//            @Override
//            public void progressChange(int progress) {
//                Log.d("!@#", progress + "");
//                loadingDialog.setProgress(progress);
//            }
//
//            @Override
//            public void onFailureUpload(String message) {
//                loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onSuccessfulUpload(Uri fileUri) {
//
//                createPostModel = new CreatePostModel();
//                ArrayList<AttachmentParams> attachmentParams = new ArrayList<>();
//                attachmentParams.add(new AttachmentParams(fileUri.toString(), extention, "0"));
//                createPostModel.setAttachments(attachmentParams);
//                createPostModel.setDescription(et_post.getText().toString());
//                IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
//                createPostModel.setTags(idsAndTagsListModel.getTags());
//                createPostModel.setIds(idsAndTagsListModel.getIds());
//                createPostModel.setProfile_id(userId);
//                createPostModel.setExtension(extention);
////                createPostModel.setPreview_url(preview_url);
//                if (isPaid.equals("1")) {
//                    createPostModel.setPaid("1");
//                    createPostModel.setAmount(amount);
//                } else {
//                    createPostModel.setPaid("0");
//                }
//                if (editingPost)
//                    createPostModel.setId(postModelForEditPost.getId());
//                writePost(createPostModel);
//            }
//        });
//    }


    private void uploadVideo(String ext, File file, String profilePath) {
        System.out.println("Video Extension");
        System.out.println(ext);
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
//                Toast.makeText(getContext(), "Video Complete", Toast.LENGTH_SHORT).show();
                videoUrl = fileUri.toString();
                String unique_name = UUID.randomUUID().toString();
                String ext = getFileExtention(file.getName());
                String firebasePath = Constants.FirestoreBaseDir + userModel.getId() + "/posts/images/" + unique_name + "." + ext;

                utils.uploadVideoThumbnail(firebasePath, file, new UploadingProgressInterface() {
                    @Override
                    public void progressChange(int progress) {
                        loadingDialog.setProgress(progress);
                    }

                    @Override
                    public void onFailureUpload(String message) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onSuccessfulUpload(Uri fileUri) {
//                        Toast.makeText(getContext(), "thumbnail Complete", Toast.LENGTH_SHORT).show();

                        createPostModel = new CreatePostModel();
                        createPostModel.setProfile_id(userId);
                        createPostModel.setDescription(et_post.getText().toString());
                        IdsAndTagsListModel idsAndTagsListModel = BaseUtils.checkETTagsInEditText(et_post, addFollowersFiltering, ids, tags);
                        createPostModel.setTags(idsAndTagsListModel.getTags());
                        createPostModel.setIds(idsAndTagsListModel.getIds());

                        attachmentList.add(new AttachmentParams(videoUrl, extention, duration));
                        createPostModel.setAttachments(attachmentList);
                        createPostModel.setThumbnail(fileUri.toString());
                        createPostModel.setExtension(extention);
                        createPostModel.setDuration(duration);
//                        createPostModel.setPreview_url(preview_url);
                        if (isPaid.equals("1")) {
                            createPostModel.setPaid("1");
                            createPostModel.setAmount(amount);
                        } else {
                            createPostModel.setPaid("0");
                        }
                        if (editingPost)
                            createPostModel.setId(postModelForEditPost.getId());
                        writePost(createPostModel);
                    }
                });

            }
        });
    }

    private void showImagePickerOptions() {

        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(3);
        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
                    launchCameraIntent();
                }
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();

                }
                if (getString(R.string.video_gallery).equals(PressedButton)) {
                    launchVideoGalleryIntent();

                }
            }
        });

        FragmentManager fm = getActivity().getSupportFragmentManager();
        selectMediaForPostDialogFragment.show(fm, "Select_Media");

    }

    private void launchVideoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video"), Constants.REQUEST_GALLERY_VIDEO);
    }

    private void launchGalleryIntent() {
//        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
//        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
//        // setting aspect ratio
//        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
////        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
////        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
//        startActivityForResult(intent, REQUEST_IMAGE);

        Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_IMAGE_CAPTURE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                uri = null;
                if (data.getClipData() != null) {
//                    if (!editingPost)
//                        imageList = new ArrayList<>();
//                    newImageList = new ArrayList<>();

//                    single_image_card.setVisibility(View.GONE);

                    //picked multiple images
                    //get number of picked images
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUrl = data.getClipData().getItemAt(i).getUri();
                        imageList.add(imageUrl);
                        System.out.println("images Path");
                        System.out.println(uri.getPath());
                        newImageList.add(imageUrl);
                    }
                    adapter.setList(imageList);
                    rv_multiple_images.setVisibility(View.VISIBLE);
                    img_layout.setVisibility(View.VISIBLE);
                } else {
                    uri = data.getData();
                    if (uri != null) {
//                        if (!editingPost)
//                            imageList = new ArrayList<>();
//                        newImageList = new ArrayList<>();
                        imageList.add(uri);
                        System.out.println("Image Path");
                        System.out.println(uri.getPath());
                        newImageList.add(uri);
                        adapter.setList(imageList);
                        rv_multiple_images.setVisibility(View.VISIBLE);
                        img_layout.setVisibility(View.VISIBLE);
//                    rv_multiple_images.setVisibility(View.GONE);
                        //picked single image
                        extention = getMimeType(getContext(), uri);
//                    if (uri != null) {
//                        img_layout.setVisibility(View.VISIBLE);
//                        single_image_card.setVisibility(View.VISIBLE);
//                        Glide.with(getContext()).load(uri).into(img_post);
//                    } else {
//                        img_layout.setVisibility(View.GONE);
//                        single_image_card.setVisibility(View.GONE);
//                    }
                    }
                }
            }
        }
        if (requestCode == Constants.REQUEST_GALLERY_VIDEO && resultCode == Activity.RESULT_OK) {
            uri = data != null ? data.getData() : null;
            if (uri != null) {
                newImageList.clear();
                imageList.clear();
                duration = BaseUtils.getVideoDuration(getContext(), uri);
                extention = getMimeType(getContext(), uri);
                uploadAttachment = uri;
                isPostImageSelected = true;
                System.out.println("Video Path");
                System.out.println(uri.getPath());
 //               if (!editingPost)
//                    imageList = new ArrayList<>();
//                newImageList = new ArrayList<>();
                newImageList.add(uri);
                imageList.add(uri);
                adapter.setList(imageList);
                rv_multiple_images.setVisibility(View.VISIBLE);
                img_layout.setVisibility(View.VISIBLE);
//                img_layout.setVisibility(View.VISIBLE);
//                single_image_card.setVisibility(View.VISIBLE);
//                Glide.with(getContext()).load(uri).placeholder(R.drawable.cover_place_holder).into(img_post);
            } else {
                BaseUtils.showLottieDialog(getContext(), "Path not found!", R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
//                BaseUtils.showToast(getContext(), "Path not found");
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            uri = (Uri) data.getExtras().get("path");
            if (uri != null) {
                System.out.println("Capture Image Path");
                System.out.println(uri.getPath());
//                if (!editingPost)
//                    imageList = new ArrayList<>();
//                newImageList = new ArrayList<>();
                imageList.add(uri);
                newImageList.add(uri);
                adapter.setList(imageList);
                rv_multiple_images.setVisibility(View.VISIBLE);
                img_layout.setVisibility(View.VISIBLE);
//                    rv_multiple_images.setVisibility(View.GONE);
                //picked single image
                extention = getMimeType(getContext(), uri);
            }
        }
    }

    private void writePost(CreatePostModel createPostModel) {
        Call<ApiResponse<PostModel>> request = null;
        if (editingPost) {
                request = ApiClient.getInstance().getInterface().editPost(createPostModel);

        } else {

                request = ApiClient.getInstance().getInterface().createPost(createPostModel);

        }

        ApiManager.apiCallWithFailure(request, getContext(), new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
                PostModel postModel = data.body().getData();
                if (getDialog() != null)
                    getDialog().dismiss();
                EventBus.getDefault().postSticky(new OnRefreshPost(true, editingPost, postModel));
                ApiManager.apiCall(ApiClient.getInstance().getInterface().postProcess(data.body().getData().getId()), getContext(), new ApiResponseHandler<String>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<String>> data) {
                        loadingDialog.dismiss();
                        deleteImageFromFirebase();
                    }
                });
            }

            @Override
            public void onFailure(String failureCause) {
                loadingDialog.dismiss();
            }
        });
    }

    private void deleteImageFromFirebase() {
        if (deleteImageList.size() > 0) {
            for (int i = 0; i < deleteImageList.size(); i++) {
                utils.deleteFileFromStorage(deleteImageList.get(i));
            }
        }
    }

    public boolean isContainLink(String comment) {
        Pattern p = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(comment);//replace with string to compare
        if (m.find()) {
            return true;
        }
        return false;
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);
        utils = new FirebaseUtils(getContext());
        loadingDialog = BaseUtils.showProgressDialog(getContext());
//        dialog = BaseUtils.progressDialog(getContext());
        img_layout = view.findViewById(R.id.img_layout);
        progress_bar = view.findViewById(R.id.progress_bar);
        card_for_tags = view.findViewById(R.id.card_tags);
        tv_post_text = view.findViewById(R.id.tv_post_text);
//        iv_clear_attachment = view.findViewById(R.id.iv_clear_attachment);
        img_profile = view.findViewById(R.id.img_profile);
        txt_name = view.findViewById(R.id.txt_name);
        et_post = view.findViewById(R.id.et_post);
        img_cancel = view.findViewById(R.id.img_cancel);
        img_upload = view.findViewById(R.id.img_upload);
//        img_post = view.findViewById(R.id.img_post);
        switch_paid = view.findViewById(R.id.switch_paid);
        card_post = view.findViewById(R.id.card_post);
//        card_cancel = view.findViewById(R.id.card_cancel);
//        single_image_card = view.findViewById(R.id.single_image_card);

        rv_multiple_images = view.findViewById(R.id.rv_multiple_images);
        RecyclerView.LayoutManager imageLayoutManager = new GridLayoutManager(getContext(), 3);
        adapter = new MultipleImageAdapter(getContext(), new RemoveImage() {
            @Override
            public void removedUri(Uri uri) {
                if (currentImageList.size() > 0) {

                    for (int i = 0; i < currentImageList.size(); i++) {
                        if (Uri.parse(currentImageList.get(i).getPath()).equals(uri)) {
                            deleteImageList.add(currentImageList.get(i).getPath());
                            currentImageList.remove(i);
                        }
                    }
                }
                if (newImageList.size() > 0) {
                    for (int i = 0; i < newImageList.size(); i++) {
                        if (newImageList.get(i).equals(uri)) {
                            newImageList.remove(i);
                        }
                    }
                }
            }
        });
        rv_multiple_images.setLayoutManager(imageLayoutManager);
        rv_multiple_images.setAdapter(adapter);


        rv_tags = view.findViewById(R.id.rv_tags);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        userTagAdapter = new UserTagAdapter(getContext(), followModelList);
        rv_tags.setLayoutManager(layoutManager);
        rv_tags.setAdapter(userTagAdapter);
        userTagAdapter.initInterface(new UserTagAdapter.IuserSelected() {
            @Override
            public void selected(FollowModel follower) {
                String username = follower.getFollowingUsername();
                if (!et_post.getText().toString().contains(username)
                        && addFollowersFiltering.contains(follower)) {
                    addFollowersFiltering.remove(follower);
                    ids.remove(follower.getFollowingId());
                    tags.remove(username);
                }
                if (!addFollowersFiltering.contains(follower)) {
                   /* if(et_comment.getText().toString().length()==1)
                    et_comment.setText("@"+username);*/
                    IdsAndTagsListModel idsAndTagsListModel = updateETwithTags(follower, et_post, addFollowersFiltering);
                    ids.add(idsAndTagsListModel.getIds());
                    tags.add(idsAndTagsListModel.getTags());

                }
                card_for_tags.setVisibility(View.GONE);
            }

        });


        Glide.with(getContext()).load(getUrlforPicture(userModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_profile);
        txt_name.setText(userModel.getName());

        et_post.setEnabled(true);

        if (userModel.getId() != userId)
            switch_paid.setVisibility(View.GONE);

        et_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                Pattern p = Pattern.compile("[@][a-zA-Z0-9-.]+");
                Matcher m = p.matcher(text);

                int cursorPosition = et_post.getSelectionStart();
                if (s.toString().contains("@")) {
                    card_for_tags.setVisibility(View.VISIBLE);
                } else {
                    card_for_tags.setVisibility(View.GONE);
                }
                while (m.find()) {
                    if (cursorPosition >= m.start() && cursorPosition <= m.end()) {
                        final int s2 = m.start() + 1; // add 1 to ommit the "@" tag
                        final int e = m.end();
                        card_for_tags.setVisibility(View.VISIBLE);
                        //add post
                        //filterData(text.substring(s2, e));
                        filterTagData(text.substring(s2, e));
                        break;
                    } else {
                        card_for_tags.setVisibility(View.GONE);
                    }
                }
            }
        });
//        iv_clear_attachment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearAttachment();
//            }
//        });
    }

    @Override
    public void onDestroy() {
        loadingDialog.dismiss();
        super.onDestroy();
    }

    private void filterTagData(String substring) {
        progress_bar.setVisibility(View.VISIBLE);
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowers(substring), getContext(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                progress_bar.setVisibility(View.GONE);

                followModelList.clear();
                followModelList.addAll(data.body().getData().getData());
                userTagAdapter.notifyDataSetChanged();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        ((View) getView().getParent()).setBackgroundTintMode( PorterDuff.Mode.CLEAR);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
//        ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onCallback(ArrayAdapter<UserModel> mentionAdapter) {
        mentionAdapter.notifyDataSetChanged();
    }

    public void clearAttachment() {
//        img_post.setImageResource(0);
        img_layout.setVisibility(View.GONE);
        isPaid = "0";
        amount = "0";
        uploadAttachment = null;
    }
}
