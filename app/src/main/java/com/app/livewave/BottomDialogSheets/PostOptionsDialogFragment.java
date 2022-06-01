package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.ParameterModels.ChangePasswordRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Response;

public class PostOptionsDialogFragment extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    private TextView tv_delete, tv_edit;
    private boolean myProfile;
    private PostModel postModel;
    private Integer myId;
    private Integer currentProfileUserId;


    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }

    public PostOptionsDialogFragment(PostModel postModel, Integer myId, Integer currentProfileUserId) {
        this.myId = myId;
        this.postModel = postModel;
        this.currentProfileUserId = currentProfileUserId;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_options_dialog, container, false);
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.delete));
            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.edit));

            }
        });
    }

    private void initViews(View view) {
        tv_delete = view.findViewById(R.id.tv_delete);
        tv_edit = view.findViewById(R.id.tv_edit);

        if (!myId.equals(postModel.getUserId()) && !myId.equals(currentProfileUserId)) {
             tv_edit.setVisibility(View.GONE);
            tv_delete.setVisibility(View.GONE);
        } else if (myId.equals(postModel.getUserId())) {
             tv_edit.setVisibility(View.VISIBLE);
            tv_delete.setVisibility(View.VISIBLE);
        } else if (myId.equals(currentProfileUserId)) {
             tv_delete.setVisibility(View.VISIBLE);
            tv_edit.setVisibility(View.GONE);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }


}
