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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.InviteFollowersAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ParameterModels.ChangePasswordRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Response;

public class InviteUserDialogSheet extends BottomSheetDialogFragment {

    SearchView searchView;
    RecyclerView rv_invite_user;
    InviteFollowersAdapter inviteFollowersAdapter;
    int streamId;
    boolean isEvent;

    public InviteUserDialogSheet(int mstreamId, boolean isEvent) {
        this.streamId = mstreamId;
        this.isEvent = isEvent;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_user_dialog, container, false);
        initViews(view);
        initSearchlistiner();
        return view;
    }

    private void initSearchlistiner() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFollowers(newText);
                return false;
            }
        });
    }

    private void searchFollowers(String query) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowers(query), getContext(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                setData(data.body().getData().getData());
            }
        });
    }

    private void setData(List<FollowModel> data) {
        inviteFollowersAdapter.setList(data);
    }

    private void initViews(View view) {
        searchView = view.findViewById(R.id.searchView);
        rv_invite_user = view.findViewById(R.id.rv_invite_user);
        inviteFollowersAdapter = new InviteFollowersAdapter(getContext(),streamId,this, isEvent);
        rv_invite_user.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv_invite_user.setAdapter(inviteFollowersAdapter);
        
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
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
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


//    private void changePassword(String oldPass, String newPass, String confirmPass) {
//        ChangePasswordRequestModel changePasswordRequestModel = new ChangePasswordRequestModel(oldPass, newPass, confirmPass);
//        ApiManager.apiCall(ApiClient.getInstance().getInterface().changePasswordApi(changePasswordRequestModel), getContext(), new ApiResponseHandler<String>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<String>> data) {
//                BaseUtils.showToast(getContext(), getString(R.string.password_change_successfully));
//                getDialog().dismiss();
//            }
//        });
//
//    }
}
