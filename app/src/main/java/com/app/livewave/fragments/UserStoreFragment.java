package com.app.livewave.fragments;

import android.content.SharedPreferences;
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

import com.app.livewave.DialogSheets.wavesplayer.TrackOptionsDialogSheet;
import com.app.livewave.DialogSheets.wavesplayer.UploadSongDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerStoreAdapter;
import com.app.livewave.adapters.wavesplayer.WavesPlayerUSerPlayStoreAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;

public class UserStoreFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {

    private RecyclerView recyclerView;
    public ArrayList<Track> trackList = new ArrayList<>();
    WavesPlayerUSerPlayStoreAdapter wavesPlayerStoreAdapter;
    TrackOptionsDialogSheet trackOptionsDialogSheet;

    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_store, container, false);
        // Add the following lines to create RecyclerView
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.yourstoreitems_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        wavesPlayerStoreAdapter = new WavesPlayerUSerPlayStoreAdapter(trackList, getActivity(), false);
        recyclerView.setAdapter(wavesPlayerStoreAdapter);
        userId = Paper.book().read("UserID");;
        System.out.println("USER ID:");
        System.out.println(userId);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume store fragment", "Resuming");
        getTracks(userId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            uploadNewSong();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTracks(String userId) {
        ((HomeActivity) getActivity()).showProgressDialog();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getUserTracks(userId), getActivity(), new ApiResponseHandler<List<Track>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<Track>>> data) {
                appTrackData(data.body().getData());
            }
        });
    }

    private void appTrackData(List<Track> data) {
        trackList.clear();
        for (int i = 0; i < data.size(); i++) {
            trackList.add(data.get(i));
        }
        wavesPlayerStoreAdapter.notifyDataSetChanged();
        ((HomeActivity) getActivity()).hideProgressDialog();
    }

    void uploadNewSong() {
        UploadSongDialogSheet uploadSongDialogSheet = new UploadSongDialogSheet(this);
        FragmentManager fm = getChildFragmentManager();
        uploadSongDialogSheet.show(fm, "uploadSongDialogSheet");
    }

    @Override
    public void updatePlayerState() {
        wavesPlayerStoreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {
        trackOptionsDialogSheet = new TrackOptionsDialogSheet(this, track);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        trackOptionsDialogSheet.show(fm, "trackoptionsDialogSheet");
    }

    @Override
    public void onTrackListUpdateEvent() {
        if (trackOptionsDialogSheet != null) {
            trackOptionsDialogSheet.dismiss();
        }
        getTracks(userId);
    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {
        if (trackOptionsDialogSheet != null) {
            trackOptionsDialogSheet.dismiss();
        }
    }

    @Override
    public void onPlaylistSelectEvent(int position) {

    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }

}
