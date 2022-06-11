package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.UserTagAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.IdsAndTagsListModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.BaseUtils.updateETwithTags;

public class ShareBottomSheet extends BottomSheetDialogFragment{
    private CircleImageView img_profile;
    private TextView txt_name, tv_post_text;
    private TextInputEditText et_post;
    private ImageView img_cancel;
    private MaterialCardView card_post;
    private UserModel userModel;
    private RecyclerView rv_tags;

    private ArrayList<FollowModel> addFollowersFiltering = new ArrayList<>();
    private List<FollowModel> followModelList = new ArrayList<>();
    private UserTagAdapter userTagAdapter;
    private List<String> ids = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    String postId;
    private ProgressBar progress_bar;
    private MaterialCardView card_for_tags;

    public ShareBottomSheet(String s) {
        this.postId = s;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_bottom_sheet_dialog, container, false);
        initViews(view);
        initCLickListeners();
        return view;
    }
    private void initCLickListeners() {
        card_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePost();
            }
        });
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void sharePost() {
        HashMap hashMap = new HashMap();
        hashMap.put("id", postId);
        hashMap.put("description", et_post.getText().toString());
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().sharePost(hashMap), getContext(), new ApiResponseHandlerWithFailure<PostModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<PostModel>> data) {
                dismiss();
            }

            @Override
            public void onFailure(String failureCause) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);
        progress_bar = view.findViewById(R.id.progress_bar);
        card_for_tags = view.findViewById(R.id.card_tags);
        tv_post_text = view.findViewById(R.id.tv_post_text);
        img_profile = view.findViewById(R.id.img_profile);
        txt_name = view.findViewById(R.id.txt_name);
        et_post = view.findViewById(R.id.et_post);
        img_cancel = view.findViewById(R.id.img_cancel);
        card_post = view.findViewById(R.id.card_post);

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
                        filterTagData(text);
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
//                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

//    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
//        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
//        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
//        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
//
//        int windowHeight = getWindowHeight();
//        if (layoutParams != null) {
//            layoutParams.height = windowHeight;
//        }
//        bottomSheet.setLayoutParams(layoutParams);
//        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//    }

//    private int getWindowHeight() {
//        // Calculate window height for fullscreen use
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.heightPixels;
//    }
}
