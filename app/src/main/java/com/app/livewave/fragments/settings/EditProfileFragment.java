package com.app.livewave.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.BottomDialogSheets.ChangePasswordDialogSheet;
import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.BottomDialogSheets.SelectMediaForPostDialogFragment;
import com.app.livewave.R;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.UploadingProgressInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.FirebaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.getMimeType;
import static com.app.livewave.utils.BaseUtils.nullCheck;
import static com.app.livewave.utils.Constants.AUTOCOMPLETE_REQUEST_CODE;

public class EditProfileFragment extends Fragment implements View.OnClickListener, PlayerStateListener {
    private ImageView img_cover, img_edit_profile, img_edit_cover;
    CircleImageView img_profile;
    boolean isProfile = false;
    private int REQUEST_IMAGE = 121;
    String profilePath;
    private TextInputEditText et_name, et_username, et_email, et_phone, et_address, et_bio,et_paypal_email,et_category_select;
    private TextInputLayout til_name, tli_username, til_email, til_phone, til_address, til_bio;
    private SwitchMaterial switch_btn_private;
    private MaterialButton btn_change_password, save_profile;
    private UserModel userModel;
    private ChangePasswordDialogSheet changePasswordDialogSheet;
    KProgressHUD dialog, loadingDialog;
//    TextView txt_category;
    Integer categoryId, subCategoryId;
    List<CategoryModel> categoryModelList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initComponents(view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setData();
        initClickListeners(view);

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);
//        initComponents();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        setData();
//        initClickListeners();
//
//
//    }

    private void initClickListeners(View view) {
        btn_change_password.setOnClickListener(this);
        save_profile.setOnClickListener(this);
        et_address.setOnClickListener(this);
        img_edit_cover.setOnClickListener(this);
        img_edit_profile.setOnClickListener(this);
        et_category_select.setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setData() {
        userModel = Paper.book().read(Constants.currentUser);
        et_name.setText(nullCheck(userModel.getName()));
        et_username.setText(nullCheck(userModel.getUsername()));
        et_email.setText(nullCheck(userModel.getEmail()));
        et_phone.setText(nullCheck(userModel.getPhone()));
        et_address.setText(nullCheck(userModel.getAddress()));
        et_bio.setText(nullCheck(userModel.getBio()));
        et_paypal_email.setText(nullCheck(userModel.getWithdrawlAccount()));
        switch_btn_private.setChecked(userModel.getPrivate().equals("1"));

        Glide.with(this).load(BaseUtils.getUrlforPicture(userModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(img_profile);
        Glide.with(this).load(BaseUtils.getUrlforPicture(userModel.getCoverPhoto())).placeholder(R.drawable.cover_place_holder).into(img_cover);

        for (int i =0 ; i< categoryModelList.size(); i++){
            if (categoryModelList.get(i).getId().equals(userModel.getCategory_id())){
                for (int j = 0; j< categoryModelList.get(i).getSubcategoryModelList().size(); j++){
                    if (categoryModelList.get(i).getSubcategoryModelList().get(j).getId().equals(userModel.getSubcategory_id())){
                        et_category_select.setText(categoryModelList.get(i).getSubcategoryModelList().get(j).getTitle());
                    }
                }
            }
        }
    }

    private void initComponents(View view) {
        Paper.init(getActivity());

        categoryModelList = Paper.book().read(Constants.categoryList);
        img_edit_profile = view.findViewById(R.id.img_edit_profile);
        img_edit_cover = view.findViewById(R.id.img_edit_cover);

        et_category_select = view.findViewById(R.id.et_category_select);

        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), apiKey);
        }
        dialog = BaseUtils.progressDialog(getActivity());
        loadingDialog = BaseUtils.showProgressDialog(getActivity());
        img_cover = view.findViewById(R.id.img_cover);
        img_profile = view.findViewById(R.id.img_profile);

        et_name = view.findViewById(R.id.et_name);
        et_username = view.findViewById(R.id.et_username);
        et_email = view.findViewById(R.id.et_email);
        et_phone = view.findViewById(R.id.et_phone);
        et_address = view.findViewById(R.id.et_address);
        et_bio = view.findViewById(R.id.et_bio);

        til_name = view.findViewById(R.id.til_name);
        tli_username = view.findViewById(R.id.tli_username);
        til_email = view.findViewById(R.id.til_email);
        til_phone = view.findViewById(R.id.til_phone);
        til_address = view.findViewById(R.id.til_address);
        til_bio = view.findViewById(R.id.til_bio);
        et_paypal_email = view.findViewById(R.id.et_paypal_email);

        switch_btn_private = view.findViewById(R.id.switch_btn_private);

        btn_change_password = view.findViewById(R.id.btn_change_password);
        save_profile = view.findViewById(R.id.save_profile);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.save_profile:
                validationCheck();
                break;
            case R.id.btn_change_password:
                showChangePasswordDialog();
                break;
            case R.id.et_address:
                getAddress();
                break;
            case R.id.img_edit_profile:
                isProfile = true;
                showImagePickerOptions();
                break;
            case R.id.img_edit_cover:
                isProfile = false;
                showImagePickerOptions();
                break;
            case R.id.et_category_select:
                showCategoryDialog();
                break;

        }
    }

    @Override
    public void onDestroy() {
        loadingDialog.dismiss();
        dialog.dismiss();
        super.onDestroy();
    }

    private void showCategoryDialog() {
        SelectCategoryDialog selectCategoryDialog = new SelectCategoryDialog(new OnCategorySelect(){
            @Override
            public void onSelect(SubcategoryModel subcategoryModel) {
                et_category_select.setText(subcategoryModel.getTitle());
                categoryId = subcategoryModel.getCategoryId();
                subCategoryId = subcategoryModel.getId();
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectCategoryDialog.show(fm, "myDialog");
    }

    private void showImagePickerOptions() {
        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(2);
        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
                    launchCameraIntent();
                }
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();
                }
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectMediaForPostDialogFragment.show(fm, "myDialog");
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialogSheet changePasswordDialogSheet = new ChangePasswordDialogSheet();
        FragmentManager fm = getChildFragmentManager();
        changePasswordDialogSheet.show(fm, "myDialog");

    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);

//        Bundle bundle = new Bundle();
//        bundle.putInt(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
//        bundle.putBoolean(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//        ((HomeActivity) getActivity()).loadFragment(R.string.edit_profile, bundle);

//        if (isProfile) {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//        } else {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
//        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
//        if (isProfile) {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//        } else {
//            // setting aspect ratio
//            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
//            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
//        }

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void getAddress() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                et_address.setText(place.getAddress());
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                if (isProfile)
                    Glide.with(this).load(uri).placeholder(R.drawable.profile_place_holder).into(img_profile);
                else
                    Glide.with(this).load(uri).placeholder(R.drawable.cover_place_holder).into(img_cover);

                loadingDialog.show();
                File file = new File(uri.getPath());
                String unique_name = UUID.randomUUID().toString();

                String ext = getMimeType(getActivity(), uri);
                if (isProfile)
                    profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/user/profile";
                else
                    profilePath = Constants.FirestoreBaseDir + userModel.getId() + "/user/cover";
                FirebaseApp.initializeApp(getActivity());
                FirebaseUtils utils = new FirebaseUtils(getActivity());
                utils.uploadVideoFileToFirebase(ext, profilePath, file, new UploadingProgressInterface() {
                    @Override
                    public void progressChange(int progress) {
                        Log.d("!@#", progress + "");
                        loadingDialog.setProgress(progress);
                    }

                    @Override
                    public void onFailureUpload(String message) {
                        Log.d("!@#fail", message + "");
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onSuccessfulUpload(Uri fileUri) {
                        Call call;
                        if (isProfile)
                            call = ApiClient.getInstance().getInterface().updateProfile(fileUri.toString());
                        else
                            call = ApiClient.getInstance().getInterface().updateCover(fileUri.toString());

                        ApiManager.apiCall(call, getActivity(), new ApiResponseHandler<UserModel>() {
                            @Override
                            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                                if (isProfile)
                                    Glide.with(EditProfileFragment.this).load(BaseUtils.getUrlforPicture(data.body().getData().getPhoto())).into(img_profile);
                                else
                                    Glide.with(EditProfileFragment.this).load(BaseUtils.getUrlforPicture(data.body().getData().getCoverPhoto())).into(img_cover);
                            }
                        });

                    }
                });
            }
        }
    }

    private void validationCheck() {
        String name = et_name.getText().toString();
        String userName = et_username.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
        String address = et_address.getText().toString();
        String bio = et_bio.getText().toString();
        String profilePrivate = switch_btn_private.isChecked() ? "1" : "0";

        if (name.isEmpty()) {
            til_name.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (email.isEmpty()) {
            til_email.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (!BaseUtils.isValidEmail(email)) {
            til_email.setError(getString(R.string.invalid_email));
            return;
        }

        userModel.setName(name);
        userModel.setUsername(userName);
        userModel.setEmail(email);
        userModel.setPhone(phone);
        userModel.setAddress(address);
        userModel.setBio(bio);
        userModel.set_private(profilePrivate);
        userModel.setWithdrawlAccount(et_paypal_email.getText().toString());
        userModel.setCategory_id(categoryId);
        userModel.setSubcategory_id(subCategoryId);
        dialog.show();
        updateProfile();
    }

    private void updateProfile() {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().upDateProfile(userModel), getActivity(), new ApiResponseHandlerWithFailure<UserModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<UserModel>> data) {
                dialog.dismiss();
                Paper.book().write(Constants.currentUser, data.body().getData());
                BaseUtils.showLottieDialog(getActivity(), getString(R.string.profile_updated_successfully), R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
//                        getActivity().finish();
                        getActivity().onBackPressed();
                    }
                });
//                BaseUtils.showToast(EditProfileActivity.this, getString(R.string.profile_updated_successfully));


            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void updatePlayerState() {

    }
}