package com.app.livewave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.adapters.ImageSliderAdapterForLoginSignUp;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ParameterModels.AuthModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextInputEditText et_email, et_password;
    private FirebaseAuth mAuth;
    KProgressHUD dialog;

    ViewPager viewPager;
    int images[] = {R.drawable.is1,R.drawable.is2,R.drawable.is3,R.drawable.is4};
    int currentPageCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e(TAG, "onCreate: " );
//
//        viewPager = findViewById(R.id.slider_view_pager_login);
//        viewPager.setAdapter(new ImageSliderAdapterForLoginSignUp(images,LoginActivity.this));
//
//        final Handler handler = new Handler();
//        final Runnable update = new Runnable() {
//            @Override
//            public void run() {
//
//                if (currentPageCounter == images.length) {
//                    currentPageCounter = 0;
//                }
//                viewPager.setCurrentItem(currentPageCounter++,true);
//
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//                           @Override
//                           public void run() {
//                               handler.post(update);
//                           }
//                       } ,3500,3500
//        );

        initViews();
        initListener();

    }

    private void initListener() {
        findViewById(R.id.txt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        findViewById(R.id.txt_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
            et_email.setError("Invalid email!");
            et_email.requestFocus();
            return;
        } else if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
            et_password.setError("Password is required!");
            et_password.requestFocus();
            return;
        } else if (et_password.getText().toString().length() < 6) {
            et_password.setError("Password length must be greater or equal to 6!");
            et_password.requestFocus();
            return;
        }
        dialog.show();
        loginToServer();
    }

    private void loginToServer() {
        AuthModel authModel = new AuthModel(et_email.getText().toString(), et_password.getText().toString());
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().loginUser(authModel), LoginActivity.this, new ApiResponseHandlerWithFailure<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                signInAnonymouslyOnFirebaseAndIntentToHome(data);
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(LoginActivity.this, failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            }
        });
    }

    private void signInAnonymouslyOnFirebaseAndIntentToHome(Response<ApiResponse<UserModel>> data) {
        mAuth.signInAnonymously().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    UserModel userModel = data.body().getData();
                    Paper.book().write(Constants.token, data.body().getToken());
                    Paper.book().write(Constants.currentUser, userModel);
                    Paper.book().write("CurrentUserId",userModel.getId());
                    Paper.book().write(Constants.isLogin, true);
                    Intent intent = new Intent(LoginActivity.this,
                            HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseUtils.showLottieDialog(LoginActivity.this, "Login Successfully!", R.raw.check, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
                            startActivity(intent);
                            finish();
                        }
                    });


                } else {
                    dialog.dismiss();
                    BaseUtils.showLottieDialog(LoginActivity.this, "Authentication failed please try again!", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        dialog = BaseUtils.progressDialog(LoginActivity.this);
        Paper.init(this);
        et_email = findViewById(R.id.et_title);
        et_password = findViewById(R.id.et_password);
    }
}