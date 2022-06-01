package com.app.livewave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.transition.Slide;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.DialogSheets.PrivacyPolicyDialogueSheet;
import com.app.livewave.DialogSheets.wavesplayer.AddToPlaylistDialog;
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
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Response;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText et_name, et_username, et_email, et_password, et_confirm_password;
    TextView txt_privacypolicy;
    MaterialCheckBox check_terms;
    KProgressHUD dialog;

    ViewPager viewPager;
    int images[] = {R.drawable.is1,R.drawable.is2,R.drawable.is3,R.drawable.is4};
    int currentPageCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        viewPager = findViewById(R.id.slider_view_pager);
//        viewPager.setAdapter(new ImageSliderAdapterForLoginSignUp(images,RegisterActivity.this));

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
//            @Override
//            public void run() {
//                handler.post(update);
//            }
//        } ,3500,3500
//        );

        initViews();
        findViewById(R.id.txt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        txt_privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyPolicyDialogueSheet privacyPolicyDialogueSheet = new PrivacyPolicyDialogueSheet();
                FragmentManager fm = getSupportFragmentManager();
                privacyPolicyDialogueSheet.show(fm, "privacyAndPolicy");
            }
        });
    }

    private void validateFields() {
        if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
            et_name.setError("Name is required!");
            et_name.requestFocus();
            return;
        } else if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            et_username.setError("Username is required!");
            et_username.requestFocus();
            return;
        } else if (!et_username.getText().toString().matches("^((?!@).)*$")) {
            et_username.setError("Username should not contain special characters!");
            et_username.requestFocus();
            return;
        } else if (et_username.getText().toString().length() < 3) {
            et_username.setError("Username must be 3 characters long!");
            et_username.requestFocus();
            return;
        } else if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
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
        } else if (TextUtils.isEmpty(et_confirm_password.getText().toString().trim())) {
            et_confirm_password.setError("Confirm password is required!");
            et_confirm_password.requestFocus();
            return;
        } else if (!et_confirm_password.getText().toString().equals(et_password.getText().toString())) {
            et_confirm_password.setError("Password should be same!");
            et_confirm_password.requestFocus();
            return;
        } else if (!check_terms.isChecked()) {
            BaseUtils.showLottieDialog(RegisterActivity.this,this.getResources().getString(R.string.accept_terms_and_conditions), R.raw.invalid, new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {

                }
            });
            return;
        }
        dialog.show();
        registerUserToServer();
    }

    private void registerUserToServer() {
        AuthModel registerUser = new AuthModel(et_email.getText().toString(), et_password.getText().toString(),
                et_name.getText().toString(), et_username.getText().toString(), et_confirm_password.getText().toString());
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().registerUser(registerUser), RegisterActivity.this, new ApiResponseHandlerWithFailure<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {
//                UserModel userModel = data.body().getData();
//                Paper.book().write(Constants.currentUser, userModel);
//                Paper.book().write(Constants.isLogin, true);

                if (data.body().getMessage().equals("The email has already been taken."))
                {
                    BaseUtils.showLottieDialog(RegisterActivity.this, "The email has already been taken.", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
                    dialog.dismiss();
                }else if(data.body().getMessage().equals("The username has already been taken."))
                {
                    BaseUtils.showLottieDialog(RegisterActivity.this, "The username has already been taken.", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
                    dialog.dismiss();
                }else {

                    dialog.dismiss();
                    BaseUtils.showLottieDialog(RegisterActivity.this, "Registered Successfully!", R.raw.check, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivityWithWavesFeature.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }

            }
            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(RegisterActivity.this,failureCause,R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
            }
        });
    }
    private void initViews() {
        dialog = BaseUtils.progressDialog(RegisterActivity.this);
        et_name = findViewById(R.id.et_name);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_title);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        check_terms = findViewById(R.id.check_terms);
        txt_privacypolicy = findViewById(R.id.txt_privacypolicy);
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}