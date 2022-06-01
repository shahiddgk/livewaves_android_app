package com.app.livewave.DialogSheets.wavesplayer;

import static com.app.livewave.utils.BaseUtils.showAlertDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import retrofit2.Response;

public class TrackOptionsDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    ImageView btn_cancel;
    private LinearLayout edit, delete, addToPlaylist;
    KProgressHUD dialog;
    Track track;
    ImageView trackImage;
    TextView trackTitle, trackDesc;
    WPAdapterOptionsListener wpAdapterOptionsListener;

    public TrackOptionsDialogSheet(WPAdapterOptionsListener wpAdapterOptionsListener, Track track) {
        this.track = track;
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_options_dialog, container, false);
        initViews(view);
        initClickListeners();

        return view;
    }

    private void initClickListeners() {
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        addToPlaylist.setOnClickListener(this);
    }

    private void initViews(View view) {
        btn_cancel = view.findViewById(R.id.btn_cancel);
        edit = view.findViewById(R.id.edit_iv);
        delete = view.findViewById(R.id.delete_iv);
        addToPlaylist = view.findViewById(R.id.add_to_playlist_iv);
        dialog = BaseUtils.progressDialog(getContext());

        trackImage = view.findViewById(R.id.track_options_im);
        trackTitle = view.findViewById(R.id.track_options_songtitle);
        trackDesc = view.findViewById(R.id.track_options_singer);

        if (track != null) {
            Glide.with(this).
                    load(track.getAttachment()).
                    placeholder(R.drawable.profile_place_holder).into(trackImage);
            trackTitle.setText(track.getTitle());
            trackDesc.setText(track.getDescription());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.add_to_playlist_iv) {
            addPlaylistDialog();
        } else if (id == R.id.edit_iv) {
            trackDetailsEditDialogSheet(getActivity());
        } else if (id == R.id.delete_iv) {
            deleteTrackApi();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        }
    }

    void deleteTrackApi() {
        showAlertDialog(getString(R.string.delete_track), getString(R.string.are_you_sure_cant_to_delete_track), getContext(), new DialogBtnClickInterface() {
            @Override
            public void onClick(boolean positive) {
                if (positive) {
                    ((HomeActivity) getActivity()).showProgressDialog();
                    ApiManager.apiCall(ApiClient.getInstance().getInterface().deleteTrack(track.getId()), getContext(), new ApiResponseHandler<List<Track>>() {
                        @Override
                        public void onSuccess(Response<ApiResponse<List<Track>>> data) {
                            ((HomeActivity) getActivity()).hideProgressDialog();
                            wpAdapterOptionsListener.onTrackListUpdateEvent();
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    void addPlaylistDialog() {
        AddToPlaylistDialog addToPlaylistDialog = new AddToPlaylistDialog(wpAdapterOptionsListener, track);
        FragmentManager fm = getChildFragmentManager();
        addToPlaylistDialog.show(fm, "addToPlaylistSheet");
    }

    void trackDetailsEditDialogSheet(Activity context) {
        UploadSongDialogSheet uploadSongDialogSheet = new UploadSongDialogSheet(wpAdapterOptionsListener,track, true);
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        uploadSongDialogSheet.show(fm, "uploadSongDialogSheet");
    }
}
