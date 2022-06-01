package com.app.livewave.DialogSheets.wavesplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerPlaylistAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.RequestModels.AddTrackToPlaylistModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class AddToPlaylistDialog extends DialogFragment implements
        View.OnClickListener, WPAdapterOptionsListener {

    private MaterialButton btn_save_song;
    private ImageView btn_cancel;
    KProgressHUD dialog;
    RecyclerView playlist_recycler;
    RelativeLayout btn_create_new;
    WavesPlayerPlaylistAdapter wavesPlayerPlaylistAdapter;
    ArrayList<PlayListModel> playLists = new ArrayList<>();
    Track track;
    WPAdapterOptionsListener wpAdapterOptionsListener;
    Context context;

    public AddToPlaylistDialog(WPAdapterOptionsListener wpAdapterOptionsListener, Track track) {
        this.track = track;
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_to_playlist_dialog, container, false);
        initViews(view);
        initClickListeners();
        getPlaylist();
        context = getActivity();
        return view;
    }

    private void initClickListeners() {
        btn_create_new.setOnClickListener(this);
        btn_save_song.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
        btn_save_song = view.findViewById(R.id.btn_save_to_playlist);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_create_new = view.findViewById(R.id.btn_create_new);
        dialog = BaseUtils.progressDialog(getContext());

        wavesPlayerPlaylistAdapter = new WavesPlayerPlaylistAdapter(this, playLists, getActivity(), R.layout.item_playlist, true);
        playlist_recycler = view.findViewById(R.id.recycler_playlist);
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        playlist_recycler.setLayoutManager(layoutManagerHorizontal);
        playlist_recycler.setAdapter(wavesPlayerPlaylistAdapter);
    }

    public void getPlaylist() {
        ((HomeActivity) getActivity()).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getMyPlaylist(), context, new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                if (!data.body().getData().isEmpty()){
                    ((HomeActivity) context).hideProgressDialog();
                    appData(data.body().getData());
                }else{
                    BaseUtils.showLottieDialog(context, data.body().getMessage(), R.raw.invalid, positive -> {
                    });
                    ((HomeActivity) context).hideProgressDialog();
                }
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) context).hideProgressDialog();
//                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
//                });
            }
        });
    }

    public void addTrackToPlaylist(int id) {

            ((HomeActivity) getActivity()).showProgressDialog();
            ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().addTrackToPlaylist(new AddTrackToPlaylistModel(id, track.getId())), getActivity(), new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
                @Override
                public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                    ((HomeActivity) getActivity()).hideProgressDialog();
                    BaseUtils.showLottieDialog(getActivity(), data.body().getMessage(), R.raw.check, positive -> {
                    });
                    wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                    dismiss();
                }

                @Override
                public void onFailure(String failureCause) {
                    ((HomeActivity) getActivity()).hideProgressDialog();
                    BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
                    });
                }
            });
    }

    private void appData(List<PlayListModel> data) {
        playLists.clear();
        for (int i = 0; i < data.size(); i++) {
            playLists.add(data.get(i));
        }
        wavesPlayerPlaylistAdapter.notifyDataSetChanged();
        ((HomeActivity) getActivity()).hideProgressDialog();
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
        if (id == R.id.btn_save_to_playlist) {
            this.dismiss();

        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.btn_create_new) {
            CreatePlaylistDialog createPlaylistDialog = new CreatePlaylistDialog(this);
            FragmentManager fm = getChildFragmentManager();
            createPlaylistDialog.show(fm, "createPlaylistDialog");
        }
    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {

    }

    @Override
    public void onTrackListUpdateEvent() {

    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {
        getPlaylist();
    }

    @Override
    public void onPlaylistSelectEvent(int position) {
        System.out.println("USERID");

        UserModel User= Paper.book().read(Constants.currentUser);

        String TrackUserId = track.getUserId().toString();
        String CurrentUserId = User.getId().toString();

        if(track.getAmount().equals("0") || TrackUserId.equals(CurrentUserId)) {

            System.out.println(track.getAmount());

            addTrackToPlaylist(playLists.get(position).getPlaylistId());
        } else {

            System.out.println(track.getAmount());

            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("id", "tracks_id=" + track.getId());
            intent.putExtra("type", "track");
            intent.putExtra("amount", track.getAmount());
            intent.putExtra("playlist_id", playLists.get(position).getPlaylistId());
            intent.putExtra("intent_type", "7");
            intent.putExtra("paid",1);
            context.startActivity(intent);

        }
    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }
}
