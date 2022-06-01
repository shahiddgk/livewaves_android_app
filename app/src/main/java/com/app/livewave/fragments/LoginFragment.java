package com.app.livewave.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.livewave.R;
import com.app.livewave.activities.ForgotPasswordActivity;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.LoginActivity;
import com.app.livewave.activities.RegisterActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ParameterModels.AuthModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
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

import io.paperdb.Paper;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    TextInputEditText et_email, et_password;
    private FirebaseAuth mAuth;
    KProgressHUD dialog;

    ViewPager viewPager;
    int images[] = {R.drawable.is1,R.drawable.is2,R.drawable.is3,R.drawable.is4};
    int currentPageCounter = 0;

    public LoginFragment() {
        // Required empty public constructor
    }

//    public static LoginFragment newInstance(String param1, String param2) {
//        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews(view);
        initListener(view);

        return view;
    }

    private void initListener(View view) {
        view.findViewById(R.id.txt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });
        view.findViewById(R.id.txt_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
            }
        });
        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
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
                ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().loginUser(authModel), getContext(), new ApiResponseHandlerWithFailure<UserModel>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<UserModel>> data) {
                        signInAnonymouslyOnFirebaseAndIntentToHome(data);
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        dialog.dismiss();
                        BaseUtils.showLottieDialog(getContext(), failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
                    }
                });
            }

            private void signInAnonymouslyOnFirebaseAndIntentToHome(Response<ApiResponse<UserModel>> data) {
                mAuth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            UserModel userModel = data.body().getData();
                            Paper.book().write(Constants.token, data.body().getToken());
                            Paper.book().write(Constants.currentUser, userModel);
                            Paper.book().write("CurrentUserId",userModel.getId());
                            Paper.book().write(Constants.isLogin, true);
                            Intent intent = new Intent(getContext(),
                                    HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            BaseUtils.showLottieDialog(getContext(), "Login Successfully!", R.raw.check, new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });


                        } else {
                            dialog.dismiss();
                            BaseUtils.showLottieDialog(getContext(), "Authentication failed please try again!", R.raw.invalid, new DialogBtnClickInterface() {
                                @Override
                                public void onClick(boolean positive) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void initViews(View view) {
        mAuth = FirebaseAuth.getInstance();
        dialog = BaseUtils.progressDialog(getContext());
        Paper.init(getContext());
        et_email = view.findViewById(R.id.et_title);
        et_password = view.findViewById(R.id.et_password);
    }
}