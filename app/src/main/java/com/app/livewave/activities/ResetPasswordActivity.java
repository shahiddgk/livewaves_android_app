package com.app.livewave.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;

import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    TextInputEditText et_otp, et_new_password;
    String email;
    KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        email = getIntent().getStringExtra("Email");
        initViews();
        initClickListener();
    }

    private void initViews() {
        et_otp = findViewById(R.id.et_otp);
        et_new_password = findViewById(R.id.et_new_password);
        dialog = BaseUtils.progressDialog(ResetPasswordActivity.this);
    }

    private void initClickListener() {
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void validateFields() {
        if (TextUtils.isEmpty(et_otp.getText().toString().trim())) {
            et_otp.setError("OTP is required!");
            et_otp.requestFocus();
            return;
        } else if (TextUtils.isEmpty(et_new_password.getText().toString().trim())) {
            et_new_password.setError("New Password is required!");
            et_new_password.requestFocus();
            return;
        } else if (et_new_password.getText().toString().length() < 6) {
            et_new_password.setError("Password must be greater than 6!");
            et_new_password.requestFocus();
            return;
        }

        if (email != null) {
            resetPassword(email, et_otp.getText().toString(), et_new_password.getText().toString());
        }
    }

    private void resetPassword(String email, String otp, String password) {
        dialog.show();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("email", email);
        hashMap.put("otp", otp);
        hashMap.put("password", password);
        hashMap.put("password_confirmation", password);
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().resetPassword(hashMap), ResetPasswordActivity.this, new ApiResponseHandlerWithFailure<String>() {
            @Override
            public void onSuccess(Response<ApiResponse<String>> data) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(ResetPasswordActivity.this, "Password Updated Successfully!", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(ResetPasswordActivity.this, failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dialog != null)
            dialog.dismiss();
        super.onDestroy();
    }
}