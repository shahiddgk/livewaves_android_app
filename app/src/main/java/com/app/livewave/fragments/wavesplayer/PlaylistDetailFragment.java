package com.app.livewave.fragments.wavesplayer;

import static com.app.livewave.utils.BaseUtils.showAlertDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.CreatePlaylistDialog;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerStoreAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PlaylistDetailFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {
    private RecyclerView recyclerView;
    public ArrayList<Track> trackList;
    public PlayListModel curPlayList;
    WavesPlayerStoreAdapter wavesPlayerStoreAdapter;
    boolean isArtist = false;

    public PlaylistDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);
        // Add the following lines to create RecyclerView
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle.containsKey("playlist")) {
            String jsonString = bundle.getString("playlist");

            Gson gson = new Gson();
            Type playlist = new TypeToken<PlayListModel>() {
            }.getType();
            curPlayList = gson.fromJson(jsonString, playlist);
            trackList = curPlayList.getTracks();
        }
        if (bundle.containsKey("isArtist")) {
            isArtist = true;
        }
        recyclerView = view.findViewById(R.id.yourstoreitems_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        wavesPlayerStoreAdapter = new WavesPlayerStoreAdapter(this, trackList, getActivity(), false);
        recyclerView.setAdapter(wavesPlayerStoreAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isArtist) inflater.inflate(R.menu.playlist_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume store fragment", "Resuming");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            playlistDetailsEditDialogSheet();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            deletePlaylistApi();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void removeFromPlaylist(Track track) {
        showAlertDialog(getString(R.string.remove_track), getString(R.string.are_you_sure_cant_to_remove_track), getContext(), positive -> {
            if (positive) {
                ((HomeActivity) getActivity()).showProgressDialog();
                ApiManager.apiCall(ApiClient.getInstance().getInterface()
                        .removeTrackFromPlaylist(curPlayList.getPlaylistId(), track.getId()), getContext(), data -> {
                    ((HomeActivity) getActivity()).hideProgressDialog();
                    curPlayList.getTracks().remove(track);
                    trackList.remove(track);
                    wavesPlayerStoreAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    void deletePlaylistApi() {
        showAlertDialog(getString(R.string.delete_playlist), getString(R.string.are_you_sure_cant_to_delete_playlist), getContext(), new DialogBtnClickInterface() {
            @Override
            public void onClick(boolean positive) {
                if (positive) {
                    ((HomeActivity) getActivity()).showProgressDialog();
                    ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().deletePlaylist(curPlayList.getPlaylistId()), getActivity(), new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
                        @Override
                        public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                            ((HomeActivity) getActivity()).hideProgressDialog();
                            BaseUtils.showLottieDialog(getActivity(), data.message(), R.raw.check, positive -> {
                                getActivity().onBackPressed();
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
        CreatePlaylistDialog createPlaylistDialog = new CreatePlaylistDialog(this, curPlayList);
        FragmentManager fm = getChildFragmentManager();
        createPlaylistDialog.show(fm, "createPlaylistDialog");
    }

    @Override
    public void updatePlayerState() {
        wavesPlayerStoreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {
        if (!isArtist) removeFromPlaylist(track);
    }

    @Override
    public void onTrackListUpdateEvent() {

    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {
        ((HomeActivity) getActivity()).setHeaderTitle(playListModel.getTitle());
    }

    @Override
    public void onPlaylistSelectEvent(int position) {

    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }
}
