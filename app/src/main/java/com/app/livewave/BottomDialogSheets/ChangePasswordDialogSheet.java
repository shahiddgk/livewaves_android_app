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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ParameterModels.ChangePasswordRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import retrofit2.Response;

public class ChangePasswordDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextInputLayout til_current_password, til_new_password, til_retype_password;
    private TextInputEditText et_current_password, et_new_password, et_retype_password;
    private MaterialButton btn_save_password, btn_cancel;
    KProgressHUD dialog;

    public ChangePasswordDialogSheet() {
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_dialog, container, false);
        initViews(view);
        initClickListeners();


        return view;
    }

    private void initClickListeners() {
        btn_save_password.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
        til_current_password = view.findViewById(R.id.til_current_password);
        til_new_password = view.findViewById(R.id.til_new_password);
        til_retype_password = view.findViewById(R.id.til_retype_password);

        et_current_password = view.findViewById(R.id.et_current_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        et_retype_password = view.findViewById(R.id.et_retype_password);

        btn_save_password = view.findViewById(R.id.btn_save_password);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        dialog = BaseUtils.progressDialog(getContext());

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

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save_password) {
            validationCheck();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        }
    }

    private void validationCheck() {
        String oldPass, newPass, confirmPass;
        oldPass = et_current_password.getText().toString().trim();
        newPass = et_new_password.getText().toString().trim();
        confirmPass = et_retype_password.getText().toString().trim();

        til_current_password.setError(null);
        til_new_password.setError(null);
        til_retype_password.setError(null);

        if (oldPass.isEmpty()) {
            til_current_password.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (newPass.isEmpty()) {
            til_new_password.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (confirmPass.isEmpty()) {
            til_retype_password.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (!newPass.equals(confirmPass)) {
            til_retype_password.setError(getString(R.string.confirm_password_dont_match_with_new_passowrd));
            return;

        } else if (newPass.equals(oldPass)) {
            til_new_password.setError(getString(R.string.same_password));
            return;

        } else if (newPass.length() < 6) {
            til_new_password.setError(getString(R.string.password_sould_be_grater_then_6_chr));
            return;

        }
        dialog.show();
        changePassword(oldPass, newPass, confirmPass);


    }

    private void changePassword(String oldPass, String newPass, String confirmPass) {
        ChangePasswordRequestModel changePasswordRequestModel = new ChangePasswordRequestModel(oldPass, newPass, confirmPass);
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().changePasswordApi(changePasswordRequestModel), getContext(), new ApiResponseHandlerWithFailure<String>() {
            @Override
            public void onSuccess(Response<ApiResponse<String>> data) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(getContext(), getString(R.string.password_change_successfully), R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        getDialog().dismiss();
                    }
                });
//                BaseUtils.showToast(getContext(), getString(R.string.password_change_successfully));

            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }
}
