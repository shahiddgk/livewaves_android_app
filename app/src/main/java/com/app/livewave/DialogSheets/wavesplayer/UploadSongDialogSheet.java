package com.app.livewave.DialogSheets.wavesplayer;

import static android.app.Activity.RESULT_OK;
import static com.app.livewave.utils.BaseUtils.getMimeType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.RequestModels.TrackUploadModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import retrofit2.Response;

public class UploadSongDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private TextInputLayout til_song_title, til_artist_name, til_song_description, til_category, til_song_amount;
    private TextInputEditText et_song_title, et_artist_name, et_song_description, et_category_select, et_song_thumbnail, et_song_audio, et_song_price;
    private MaterialButton btn_upload_song, btn_save_song, btn_cancel, btn_upload_thumbnail;
    private TabLayout tl_free_paid, tl_privacy;
    private TextView title_upload_song;
    private String categoryId, category, subCategoryId;
    KProgressHUD dialog;
    private static final int AUDIO_REQUEST_CODE = 1;
    private static final int IMAGE_REQUEST_CODE = 2;
    private FirebaseUtils utils;
    String firebaseUploadTrackUrl = "uploads/track/audios/";
    String firebaseUploadImageUrl = "uploads/track/images/";
    String timeStamp;
    TrackUploadModel trackUploadModel;
    String trackFirebaseUri;
    String imageFirebaseUri;
    Track track;
    boolean isEdit = false;
    WPAdapterOptionsListener wpAdapterOptionsListener;

    List<String> Extension = new ArrayList<>(
            Arrays.asList("MP3", ".MP3","mp3",".mp3","AAC",".AAC",".aac","aac","OGA",".OGA",".oga","oga","FLAC","flac",".FLAC",".flac", "WAV","wav",".WAV",".wav","PCM","pcm",".PCM",".pcm","AIFF","aiff",".AIFF",".aiff",".m4a",".M4A","m4a","M4A",".mpeg",".MPEG","MPEG","mpeg")
    );

    public UploadSongDialogSheet() {
    }

    public UploadSongDialogSheet(WPAdapterOptionsListener wpAdapterOptionsListener) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    public UploadSongDialogSheet(WPAdapterOptionsListener wpAdapterOptionsListener, Track track, boolean isEdit) {
        this.track = track;
        this.isEdit = isEdit;
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_song_dialog, container, false);
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        utils = new FirebaseUtils(getActivity());
        trackUploadModel = new TrackUploadModel();
        initViews(view);
        populateTrackData();
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        btn_upload_song.setOnClickListener(this);
        btn_upload_thumbnail.setOnClickListener(this);
        btn_save_song.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        et_category_select.setOnClickListener(this);

        tl_free_paid.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        til_song_amount.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                        til_song_amount.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initViews(View view) {
        title_upload_song = view.findViewById(R.id.title_upload_song);
        btn_upload_song = view.findViewById(R.id.btn_upload_song);
        btn_upload_thumbnail = view.findViewById(R.id.btn_upload_thumbnail);
        til_song_title = view.findViewById(R.id.til_song_title);
        til_artist_name = view.findViewById(R.id.til_artist_name);
        til_song_description = view.findViewById(R.id.til_song_description);
        til_category = view.findViewById(R.id.til_category);
        til_song_amount = view.findViewById(R.id.til_song_amount);

        et_song_audio = view.findViewById(R.id.et_song_audio);
        et_song_title = view.findViewById(R.id.et_song_title);
        et_artist_name = view.findViewById(R.id.et_artist_name);
        et_song_thumbnail = view.findViewById(R.id.et_song_thumbnail);
        et_song_description = view.findViewById(R.id.et_song_description);
        et_category_select = view.findViewById(R.id.et_category_select);
        et_song_price = view.findViewById(R.id.et_song_price);
        tl_free_paid = view.findViewById(R.id.tl_free_paid);
        tl_privacy = view.findViewById(R.id.tl_public_private);

        btn_save_song = view.findViewById(R.id.btn_save_password);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        dialog = BaseUtils.progressDialog(getContext());

    }

    public void populateTrackData() {
        if (track != null) {
            et_song_title.setText(track.getTitle());
            et_song_description.setText(track.getDescription());
            et_song_audio.setText(track.getTrackPath());
            et_song_thumbnail.setText(track.getAttachment());
            et_artist_name.setText(track.getArtist_name());
            if(track.getIsPublic().equals("0")){
                tl_privacy.getTabAt(0).select();
            }else{
                tl_privacy.getTabAt(1).select();
            }
            if (track.getPaid().equals("0")) {
                tl_free_paid.getTabAt(0).select();
                til_song_amount.setVisibility(View.GONE);
            } else {
                tl_free_paid.getTabAt(1).select();
                til_song_amount.setVisibility(View.VISIBLE);
                et_song_price.setText(track.getAmount().toString());
            }

            List<CategoryModel> categoryModelList = Paper.book().read(Constants.categoryList);

            for (int i = 0; i < categoryModelList.size(); i++) {
                if (categoryModelList.get(i).getId().equals(track.getCategoryId())) {
                    category = categoryModelList.get(i).getTitle();
                    categoryId = categoryModelList.get(i).getId() + "";
                    subCategoryId = categoryModelList.get(i).getId() + "";
                    break;
                }
            }

            et_category_select.setText(category);
            if (isEdit) title_upload_song.setText(getText(R.string.edit_song_details));
        }
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
        } else if (v.getId() == R.id.et_category_select) {
            showCategoryDialog();
        } else if (v.getId() == R.id.btn_upload_song) {
            openGalleryForAudio();
        } else if (v.getId() == R.id.btn_upload_thumbnail) {
            openGalleryForImage();
        }
    }

    void openGalleryForImage() {
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        et_song_thumbnail.setText(uri.toString());
                        uploadTrackImageToFirebase();
                        Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();
                    }
                }
            });


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AUDIO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String ext = getMimeType(getContext(), uri);
                    System.out.println("Extension Path");
                    System.out.println(ext);

                    if(Extension.contains(ext))
                    {
                        et_song_audio.setText(uri.toString());
                        uploadTrackToFirebase();

                    } else {

                        System.out.println("SONG EXTENTION");
                        System.out.println(uri.toString().substring(uri.toString().lastIndexOf(".")));
                        et_song_audio.setText(null);
                        Toast.makeText(getContext(), "Cannot upload this file", Toast.LENGTH_LONG).show();

                    }

                }
                break;
//            case IMAGE_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    Uri uri = data.getData();
//                    et_song_thumbnail.setText(uri.toString());
//                    uploadTrackImageToFirebase();
//                    Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
    }

    public void openGalleryForAudio() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");

        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, AUDIO_REQUEST_CODE);
//        Intent audioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(audioIntent, AUDIO_REQUEST_CODE);
    }

    private void showCategoryDialog() {
        SelectCategoryDialog selectCategoryDialog = new SelectCategoryDialog(new OnCategorySelect() {
            @Override
            public void onSelect(SubcategoryModel subcategoryModel) {
                category = subcategoryModel.getTitle();
                categoryId = subcategoryModel.getCategoryId().toString();
                subCategoryId = subcategoryModel.getId().toString();
                til_category.setError(null);
                et_category_select.setText(category);
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectCategoryDialog.show(fm, "myDialog");
    }

    private void trackUpload(TrackUploadModel trackUploadModel) {
        if (!dialog.isShowing()) {
            dialog.show();
        }

        if (isEdit) {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().editTrack(trackUploadModel), getContext(), new ApiResponseHandler<List<Track>>() {
                @Override
                public void onSuccess(Response<ApiResponse<List<Track>>> data) {
                    Toast.makeText(getContext(), "Song Details Updated Successfully", Toast.LENGTH_SHORT).show();
                    wpAdapterOptionsListener.onTrackListUpdateEvent();
                    dismiss();
                }
            });
        } else {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().uploadTrack(trackUploadModel), getContext(), new ApiResponseHandler<List<Track>>() {
                @Override
                public void onSuccess(Response<ApiResponse<List<Track>>> data) {
                    Toast.makeText(getContext(), "Song Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    wpAdapterOptionsListener.onTrackListUpdateEvent();
                    dismiss();
                }
            });
        }
    }

    public void uploadTrackToFirebase() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        FirebaseApp.initializeApp(getContext());
        String ext = getMimeType(getContext(), Uri.parse(et_song_audio.getText().toString()));
        String profilePath = firebaseUploadTrackUrl + timeStamp + et_song_title.getText().toString();
        StorageReference storageReference = utils.getFireBaseStorageReferenece(ext, profilePath);
        storageReference.putFile(Uri.parse(et_song_audio.getText().toString())).addOnSuccessListener(taskSnapshot -> {
            Log.d("Uri getUploadSessionUri", "" + taskSnapshot.getUploadSessionUri());
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                trackFirebaseUri = uri.toString();
                dialog.dismiss();
            });
        });
    }

    public void uploadTrackImageToFirebase() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        FirebaseApp.initializeApp(getContext());
        String ext = getMimeType(getContext(), Uri.parse(et_song_thumbnail.getText().toString()));
        String profilePath = firebaseUploadImageUrl + timeStamp + et_song_title.getText().toString();
        StorageReference storageReference = utils.getFireBaseStorageReferenece(ext, profilePath);
        storageReference.putFile(Uri.parse(et_song_thumbnail.getText().toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Uri Image", "" + taskSnapshot.getUploadSessionUri());
                    imageFirebaseUri = uri.toString();
                    dialog.dismiss();
                });
            }
        });
    }

    private void validationCheck() {
        String songTitle, songDescription, songPrice, artistName;
        songTitle = et_song_title.getText().toString().trim();
        artistName = et_artist_name.getText().toString().trim();
        songDescription = et_song_description.getText().toString().trim();
        songPrice = et_song_price.getText().toString().trim();

        til_song_title.setError(null);
        til_artist_name.setError(null);
        til_song_description.setError(null);
        if (til_song_amount.getVisibility() == View.GONE) {
            til_song_amount.setError(null);
        }

        if (songTitle.isEmpty()) {
            til_song_title.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (artistName.isEmpty()) {
            til_artist_name.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (songDescription.isEmpty()) {
            til_song_description.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (category == null) {
            til_category.requestFocus();
            til_category.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (til_song_amount.getVisibility() == View.VISIBLE) {
            if (songPrice.isEmpty()) {
                til_song_amount.setError(getString(R.string.field_cant_be_empty));
            }
        }

        dialog.show();

        if (track != null) trackUploadModel.setTrack_id(track.getId());
        trackUploadModel.setTitle(et_song_title.getText().toString());
        trackUploadModel.setArtist_name(et_artist_name.getText().toString());
        trackUploadModel.setCategory_id(categoryId);
        trackUploadModel.setThumbnail(imageFirebaseUri);
        trackUploadModel.setTrack(trackFirebaseUri);
        trackUploadModel.setPrivacy(tl_privacy.getSelectedTabPosition() == 0 ? "0" : "1");
        trackUploadModel.setDescription(et_song_description.getText().toString());
        trackUploadModel.setPaid(tl_free_paid.getSelectedTabPosition() == 0 ? "0" : "1");
        if (tl_free_paid.getSelectedTabPosition() == 1) {
            til_song_amount.setVisibility(View.VISIBLE);
            trackUploadModel.setAmount(et_song_price.getText().toString());
        }


        trackUpload(trackUploadModel);
    }
}
