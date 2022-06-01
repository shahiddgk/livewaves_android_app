package com.app.livewave.DialogSheets.wavesplayer;

import static com.app.livewave.utils.BaseUtils.getMimeType;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.livewave.R;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.RequestModels.PlaylistCreateModel;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.concurrent.TimeUnit;

public class CreatePlaylistDialog extends DialogFragment implements View.OnClickListener {
    private TextView create_playlist;
    private TextInputLayout til_playlist_title;
    private TextInputEditText et_playlist_title, et_playlist_thumbnail;
    private MaterialButton btn_save_playlist, btn_upload_playlist_thumbnail;
    private ImageView btn_cancel;
    private TabLayout tl_public_private;
    KProgressHUD dialog;
    WPAdapterOptionsListener wpAdapterOptionsListener;
    PlayListModel playListModel;
    String firebaseUploadImageUrl = "uploads/track/images/";
    String imageFirebaseUri;
    private static final int IMAGE_REQUEST_CODE = 100;
    String timeStamp;
    private FirebaseUtils utils;
    
    public CreatePlaylistDialog(WPAdapterOptionsListener wpAdapterOptionsListener) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    public CreatePlaylistDialog(WPAdapterOptionsListener wpAdapterOptionsListener, PlayListModel playListModel) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
        this.playListModel = playListModel;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_playlist_dailog, container, false);
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        utils = new FirebaseUtils(getActivity());
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        btn_save_playlist.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_upload_playlist_thumbnail.setOnClickListener(this);
    }

    private void initViews(View view) {
        create_playlist = view.findViewById(R.id.create_playlist);
        btn_save_playlist = view.findViewById(R.id.btn_save_playlist);
        btn_upload_playlist_thumbnail = view.findViewById(R.id.btn_upload_playlist_thumbnail);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        til_playlist_title = view.findViewById(R.id.til_playlist_title);
        et_playlist_title = view.findViewById(R.id.et_playlist_title);
        et_playlist_thumbnail = view.findViewById(R.id.et_playlist_thumbnail);
        tl_public_private = view.findViewById(R.id.tl_public_private);
        dialog = BaseUtils.progressDialog(getContext());
        if (playListModel != null) {
            et_playlist_title.setText(playListModel.getTitle());
            create_playlist.setText("Edit Playlist");
            et_playlist_thumbnail.setText(playListModel.getPlaylistThumbnail());
            tl_public_private.getTabAt(Integer.parseInt(playListModel.getIsPublic())).select();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.RelativeDialog);
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save_playlist) {
            validationCheck();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        }else if (v.getId() == R.id.btn_upload_playlist_thumbnail) {
            openGalleryForImage();
        }
    }

    void openGalleryForImage(){
                mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null){
                        et_playlist_thumbnail.setText(uri.toString());
                        uploadPlaylistImageToFirebase();
                        Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public void uploadPlaylistImageToFirebase() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        FirebaseApp.initializeApp(getContext());
        String ext = getMimeType(getContext(), Uri.parse(et_playlist_thumbnail.getText().toString()));
        String profilePath = firebaseUploadImageUrl + timeStamp + et_playlist_title.getText().toString();
        StorageReference storageReference = utils.getFireBaseStorageReferenece(ext, profilePath);
        storageReference.putFile(Uri.parse(et_playlist_thumbnail.getText().toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        String playlistTitle;
        String playlistThumbnail;
        playlistTitle = et_playlist_title.getText().toString().trim();
        playlistThumbnail = et_playlist_thumbnail.getText().toString().trim();

        til_playlist_title.setError(null);
        et_playlist_thumbnail.setError(null);

        if (playlistTitle.isEmpty()) {
            til_playlist_title.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (playlistThumbnail.isEmpty()) {
            et_playlist_thumbnail.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        PlaylistCreateModel playlistCreateModel = new PlaylistCreateModel();
        playlistCreateModel.setTitle(playlistTitle);
        playlistCreateModel.setIsPublic(tl_public_private.getSelectedTabPosition() + "");
        playlistCreateModel.setPlaylistThumbnail(imageFirebaseUri);
        if (playListModel != null) {
            playlistCreateModel.setId(playListModel.getPlaylistId());
            playListModel.setTitle(playlistTitle);
            playListModel.setIsPublic(tl_public_private.getSelectedTabPosition() + "");
            playListModel.setPlaylistThumbnail(imageFirebaseUri);


        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        if (playListModel != null) {
            System.out.println("testst:" + tl_public_private.getSelectedTabPosition());
            ApiManager.apiCall(ApiClient.getInstance().getInterface().updatePlaylist(playlistCreateModel), getContext(), data -> {
                Toast.makeText(getContext(), data.body().getMessage(), Toast.LENGTH_SHORT).show();
                wpAdapterOptionsListener.onPlaylistUpdateEvent(playListModel);
                dismiss();
            });
        } else {
            ApiManager.apiCall(ApiClient.getInstance().getInterface().createNewPlaylist(playlistCreateModel), getContext(), data -> {
                Toast.makeText(getContext(), data.body().getMessage(), Toast.LENGTH_SHORT).show();
                wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                dismiss();
            });
        }
    }
}
