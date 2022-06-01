package com.app.livewave.DialogSheets.wavesplayer;

import static com.app.livewave.utils.BaseUtils.showAlertDialog;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.FragmentManager;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Response;

public class PlaylistOptionsDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    ImageView btn_cancel;
    private LinearLayout edit, delete;
    PlayListModel playListModel;
    TextView playlistTitle;
    WPAdapterOptionsListener wpAdapterOptionsListener;

    public PlaylistOptionsDialogSheet(WPAdapterOptionsListener wpAdapterOptionsListener, PlayListModel playListModel) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
        this.playListModel = playListModel;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_options_dialog, container, false);
        initViews(view);
        initClickListeners();

        return view;
    }

    private void initClickListeners() {
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
        btn_cancel = view.findViewById(R.id.btn_cancel);
        edit = view.findViewById(R.id.edit_iv);
        delete = view.findViewById(R.id.delete_iv);

        playlistTitle = view.findViewById(R.id.playlist_title);
        if (playListModel != null) {
            playlistTitle.setText(playListModel.getTitle());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.edit_iv) {
            playlistDetailsEditDialogSheet();
        } else if (id == R.id.delete_iv) {
            deletePlaylistApi();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        }
    }

    void deletePlaylistApi() {
        showAlertDialog(getString(R.string.delete_playlist), getString(R.string.are_you_sure_cant_to_delete_playlist), getContext(), new DialogBtnClickInterface() {
            @Override
            public void onClick(boolean positive) {
                if (positive) {
                    ((HomeActivity) getActivity()).showProgressDialog();
                    ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().deletePlaylist(playListModel.getPlaylistId()), getActivity(), new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
                        @Override
                        public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                            ((HomeActivity) getActivity()).hideProgressDialog();
                            BaseUtils.showLottieDialog(getActivity(), data.message(), R.raw.check, positive -> {
                                wpAdapterOptionsListener.onPlaylistUpdateEvent(playListModel);
                            });
                        }

                        @Override
                        public void onFailure(String failureCause) {
                            ((HomeActivity) getActivity()).hideProgressDialog();
                            BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
                            });
                        }
                    });
                }
            }
        });
    }

    void playlistDetailsEditDialogSheet() {
        CreatePlaylistDialog createPlaylistDialog = new CreatePlaylistDialog(wpAdapterOptionsListener, playListModel);
        FragmentManager fm = getChildFragmentManager();
        createPlaylistDialog.show(fm, "createPlaylistDialog");
    }
}
