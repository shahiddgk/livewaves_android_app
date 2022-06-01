package com.app.livewave.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.CreatePlaylistDialog;
import com.app.livewave.DialogSheets.wavesplayer.PlaylistOptionsDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerPlaylistAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

public class ArtistPublicPlaylistFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {


    // Add RecyclerView member
    private RecyclerView recyclerView;
    public ArrayList<PlayListModel> playLists = new ArrayList<>();
    WavesPlayerPlaylistAdapter wavesPlayerPlaylistAdapter;

    String userId = Paper.book().read("UserID");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_playlist, container, false);

        setHasOptionsMenu(true);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.yourplaylists_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        wavesPlayerPlaylistAdapter = new WavesPlayerPlaylistAdapter(this, playLists, getActivity(), R.layout.item_waves_player_artist_playlist, false);
        recyclerView.setAdapter(wavesPlayerPlaylistAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            playlistCreateDialogSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void playlistCreateDialogSheet() {
        CreatePlaylistDialog createPlaylistDialog = new CreatePlaylistDialog(this);
        FragmentManager fm = getChildFragmentManager();
        createPlaylistDialog.show(fm, "createPlaylistDialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getUserPlaylist(userId).cancel();
    }

    public void getPlaylist(String userId) {
        ((HomeActivity) getActivity()).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getUserPlaylist(userId), getActivity(), new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                ((HomeActivity) getActivity()).hideProgressDialog();
                appData(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) getActivity()).hideProgressDialog();
//                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
//                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPlaylist(userId);
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
    public void updatePlayerState() {
        wavesPlayerPlaylistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {

    }

    @Override
    public void onTrackListUpdateEvent() {

    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {
        getPlaylist(userId);
    }

    @Override
    public void onPlaylistSelectEvent(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("playlist", new Gson().toJson(playLists.get(position)));
        bundle.putString(HEADER_TITLE, playLists.get(position).getTitle());
        ((HomeActivity) getActivity()).loadFragment(R.string.tag_playlist_detail, bundle);
    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {
        PlaylistOptionsDialogSheet playlistOptionsDialogSheet = new PlaylistOptionsDialogSheet(this, playListModel);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        playlistOptionsDialogSheet.show(fm, "playlistOptionsDialogSheet");
    }

}
