package com.app.livewave.BottomDialogSheets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.adapters.WaveItemCommentListAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.CommentRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CommentModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.grpc.internal.SharedResourceHolder;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;


public class WavesItemComment extends BottomSheetDialogFragment implements View.OnClickListener {

    GridView gridView;
    TextView noComments;
    BottomSheetDialog dialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView image_post;
    int postId;
    private int currentPageNumber = 1;
    private List<CommentModel> commentModelList = new ArrayList<>();
    TextInputEditText textInputEditText;
    private CommentRequestModel commentRequestModel = new CommentRequestModel();

    public WavesItemComment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waves_item_comment, container, false);
        getBundleData();
        initViews(view);
        initClickListners();
        return view;
    }

    private void initClickListners() {
        image_post.setOnClickListener(this);
    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
            postId = bundle.getInt("PostId");
            System.out.println("userIduserId");
            System.out.println(postId);

        getPostComments(postId);
    }

    private void getPostComments(int postId) {
        if (currentPageNumber <= 1) {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().getCommentByPostId(postId, currentPageNumber), getActivity(), new ApiResponseHandler<GenericDataModel<CommentModel>>() {
                @Override
                public void onSuccess(Response<ApiResponse<GenericDataModel<CommentModel>>> data) {
                    if (data.body().getData().getData().size() > 0) {
                        noComments.setVisibility(View.GONE);
                        currentPageNumber = data.body().getData().getCurrentPage();
                        if (currentPageNumber == 1) {
                            commentModelList = new ArrayList<>();
                            commentModelList = data.body().getData().getData();
                        } else {
                            commentModelList.addAll(data.body().getData().getData());
                        }
                        WaveItemCommentListAdapter waveItemCommentListAdapter = new WaveItemCommentListAdapter(getContext(),commentModelList);
                        gridView.setAdapter(waveItemCommentListAdapter);
//                        commentModelList.clear();
//                        setCommentData(data.body().getData().getData());

                    } else {
                        noComments.setVisibility(View.VISIBLE);

                    }
                }
            });
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout coordinatorLayout = dialog.findViewById(R.id.bottomSheetId);
        assert coordinatorLayout != null;
        coordinatorLayout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED ) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }




    private void initViews(View view) {
        gridView = view.findViewById(R.id.waves_item_comments_list);
        noComments = view.findViewById(R.id.tv_no_comments_waves);
        textInputEditText = view.findViewById(R.id.et_comment_wave);
        image_post = view.findViewById(R.id.post_comment_wave);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.post_comment_wave) {
            fieldValidation();
        }
    }

    private void fieldValidation() {
        String comment = Objects.requireNonNull(textInputEditText.getText()).toString();
        if (comment.isEmpty()) {
            textInputEditText.setError(getString(R.string.field_cant_be_empty));
            textInputEditText.setFocusable(true);
            return;
        } else {
            commentRequestModel.setPost_id(postId);
            commentRequestModel.setComment(textInputEditText.getText().toString());
            addComment(commentRequestModel);
        }
    }

    private void addComment(CommentRequestModel comment) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().postComment(comment), getActivity(), new ApiResponseHandlerWithFailure<CommentModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<CommentModel>> data) {
                textInputEditText.setText("");
                assert data.body() != null;
                addPost(data.body().getData());
            }

            private void addPost(CommentModel data) {
                noComments.setVisibility(View.GONE);
                commentModelList.add(commentModelList.size(),data);
                WaveItemCommentListAdapter waveItemCommentListAdapter = new WaveItemCommentListAdapter(getContext(),commentModelList);
                gridView.setAdapter(waveItemCommentListAdapter);
            }

            @Override
            public void onFailure(String failureCause) {
                textInputEditText.setText("");

            }
        });

    }
}