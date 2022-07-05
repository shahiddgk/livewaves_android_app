package com.app.livewave.fragments.live;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.PublisherActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.models.ParameterModels.CreateStreamModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CreateStreamResponseModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.changeViewVisibilityWithAnimation;

public class CreateStreamFragment extends Fragment implements View.OnClickListener, PlayerStateListener {

    private TextInputEditText et_stream_title, et_stream_price;
    private TextInputLayout text_input_layout;
    private RelativeLayout rl_select_category;
    private TabLayout tab_free_paid;
    private SwitchMaterial switch_btn_location;
    private MaterialButton btn_create_stream;
    private String streamAmount;
    private TextView tv_category_type;
    private String streamTitle, category, category_id, sub_category, sub_category_id;
    private String isPaid, locationEnabled = "0";
    private Long lat = 0L, lng = 0L;
    CreateStreamResponseModel createStreamResponseModel;
    KProgressHUD dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_stream, container, false);
        setHasOptionsMenu(true);
        initComponents(view);
        setClickListeners();

        if (Constants.APPENV == ENV.review) {
            tab_free_paid.setVisibility(View.GONE);
        }

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_stream);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        initComponents();
//        setClickListeners();
//
//        if (Constants.APPENV == ENV.review) {
//            tab_free_paid.setVisibility(View.GONE);
//        }
//    }

    private void setClickListeners() {
        rl_select_category.setOnClickListener(this);
        btn_create_stream.setOnClickListener(this);

        tab_free_paid.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    changeViewVisibilityWithAnimation(text_input_layout, 0);
                    isPaid = "0";
                    et_stream_price.setText("");
                } else {
                    isPaid = "1";
                    changeViewVisibilityWithAnimation(text_input_layout, 1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        switch_btn_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    locationEnabled = "1";
                } else {
                    locationEnabled = "0";

                }

            }
        });
    }

    private void initComponents(View view) {
        et_stream_title = view.findViewById(R.id.et_stream_title);
        et_stream_price = view.findViewById(R.id.et_stream_price);
        text_input_layout = view.findViewById(R.id.text_input_layout);
        dialog = BaseUtils.progressDialog(getActivity());
        rl_select_category = view.findViewById(R.id.rl_select_category);
        tab_free_paid = view.findViewById(R.id.tl_going_myevents);
        switch_btn_location = view.findViewById(R.id.switch_btn_private);
        btn_create_stream = view.findViewById(R.id.btn_create_event);
        tv_category_type = view.findViewById(R.id.tv_category_type);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_select_category) {
            showCategoryDialog();
//            Intent intent = new Intent(this, SelectCategoryActivity.class);
//            startActivityForResult(intent, CATEGORY_SELECTION_REQUEST_CODE);

        } else if (v.getId() == R.id.btn_create_event) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                validationCheck();
            } else {
                Toast.makeText(getActivity(), "Permission Denied -> Go to Setting -> Allow Permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.CAMERA,
                                android.Manifest.permission.RECORD_AUDIO},
                        1);
            }
        }

    }


    private void showCategoryDialog() {
        SelectCategoryDialog selectCategoryDialog = new SelectCategoryDialog(new OnCategorySelect() {
            @Override
            public void onSelect(SubcategoryModel subcategoryModel) {
                category = subcategoryModel.getTitle();
                sub_category_id = subcategoryModel.getId().toString();
                category_id = subcategoryModel.getCategoryId().toString();
                tv_category_type.setError(null);
                tv_category_type.setText(category);
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectCategoryDialog.show(fm, "myDialog");
    }

    private void validationCheck() {
        boolean error = false;
        streamTitle = et_stream_title.getText().toString().trim();
        if (streamTitle.equals(getString(R.string.empty_string))) {
            et_stream_title.setFocusable(true);
            et_stream_title.setError(getString(R.string.please_enter_title));
            error = true;
        } else if(streamTitle.length()<2) {
            et_stream_title.setFocusable(true);
            et_stream_title.setError(getString(R.string.stream_title_length));
            error = true;
        } else if (streamTitle.length() > 100) {
            et_stream_title.setFocusable(true);
            et_stream_title.setError(getString(R.string.title_should_be_less_then_100_char));
            error = true;

        }
        if (tab_free_paid.getSelectedTabPosition() == 1) {

            streamAmount = et_stream_price.getText().toString().trim();
            if (streamAmount.equals(getString(R.string.empty_string))) {
                et_stream_price.setFocusable(true);
                et_stream_price.setError(getString(R.string.paid_is_selected_please_enter_amount));
                error = true;

            }
            if (streamAmount.equals("0")) {
                et_stream_price.setFocusable(true);
                et_stream_price.setError(getString(R.string.amount_cant_be_zero));
                error = true;

            }
        }
        if (tv_category_type.getText().toString().equals("")) {
            tv_category_type.setFocusable(true);
            tv_category_type.setError(getString(R.string.please_select_a_category));
            error = true;
        }
        if (error)
            return;

        sendCreateStreamApiRequest();
    }

    private void sendCreateStreamApiRequest() {
        dialog.show();
        CreateStreamModel createStreamModel = new CreateStreamModel(streamAmount, streamTitle, isPaid, locationEnabled, category_id, lat, lng);
        ApiManager.apiCall(ApiClient.getInstance().getInterface().createStream(createStreamModel), getActivity(), new ApiResponseHandler<CreateStreamResponseModel>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(Response<ApiResponse<CreateStreamResponseModel>> data) {
                dialog.dismiss();
                createStreamResponseModel = data.body().getData();
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.RECORD_AUDIO},
                            1);
                } else {
                    Intent intent = new Intent(getActivity(), PublisherActivity.class);
                    intent.putExtra("ID", createStreamResponseModel.getId());
                    intent.putExtra("TITLE", createStreamResponseModel.getTitle());
                    intent.putExtra("PLATFORM_ID", createStreamResponseModel.getPlatformID());
                    intent.putExtra("Event", false);
                    startActivity(intent);

//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ID", createStreamResponseModel.getId());
//                    bundle.putString("TITLE", createStreamResponseModel.getTitle());
//                    bundle.putString("PLATFORM_ID", createStreamResponseModel.getPlatformID());
//                    bundle.putBoolean("Event", false);
//                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_publisher, bundle);
//
                    getActivity().onBackPressed();
//                    finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (createStreamResponseModel != null) {
                        Intent intent = new Intent(getActivity(), PublisherActivity.class);
                        intent.putExtra("ID", createStreamResponseModel.getId());
                        intent.putExtra("TITLE", createStreamResponseModel.getTitle());
                        intent.putExtra("PLATFORM_ID", createStreamResponseModel.getPlatformID());
                        intent.putExtra("Event", false);
                        startActivity(intent);

//                        Bundle bundle = new Bundle();
//                        bundle.putInt("ID", createStreamResponseModel.getId());
//                        bundle.putString("TITLE", createStreamResponseModel.getTitle());
//                        bundle.putString("PLATFORM_ID", createStreamResponseModel.getPlatformID());
//                        bundle.putBoolean("Event", false);
//                        ((HomeActivity) getActivity()).loadFragment(R.string.tag_publisher, bundle);
                        getActivity().onBackPressed();
//                        finish();
                    }
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
//        dialog.dismiss();

        ((HomeActivity) getActivity()).hideProgressDialog();
        super.onDestroy();
    }

    @Override
    public void updatePlayerState() {

    }
    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (CATEGORY_SELECTION_REQUEST_CODE): {
//                if (resultCode == Activity.RESULT_OK) {
//                    category = data.getStringExtra(CATEGORY);
//                    category_id = data.getStringExtra(CATEGORY_ID);
////                    sub_category = data.getStringExtra(SUB_CATEGORY);
////                    sub_category_id = data.getStringExtra(SUB_CATEGORY_ID);
//
//                    tv_category_type.setError(null);
//                    tv_category_type.setText(category);
//                }
//                break;
//            }
//        }
//    }
}