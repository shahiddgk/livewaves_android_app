package com.app.livewave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ParameterModels.AuthModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jsoup.Connection;

import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText et_email;
    KProgressHUD dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
        findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });
    }

    private void ValidateFields() {
        if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
            et_email.setError("Invalid Email!");
            et_email.requestFocus();
            return;
        }
        sendEmailToServer();
    }

    private void sendEmailToServer() {
        dialog.show();
        AuthModel authModel = new AuthModel(et_email.getText().toString());
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().forgetPassword(authModel), ForgotPasswordActivity.this, new ApiResponseHandlerWithFailure<String>() {
            @Override
            public void onSuccess(Response<ApiResponse<String>> data) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(ForgotPasswordActivity.this, "Email sent!", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("Email",et_email.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String failureCause) {
                BaseUtils.showLottieDialog(ForgotPasswordActivity.this, failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void initViews() {
        et_email = findViewById(R.id.et_title);
        dialog = BaseUtils.progressDialog(ForgotPasswordActivity.this);
    }
}