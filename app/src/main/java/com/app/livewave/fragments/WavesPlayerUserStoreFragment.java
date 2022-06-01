package com.app.livewave.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.AddToPlaylistDialog;
import com.app.livewave.DialogSheets.wavesplayer.PlaylistOptionsDialogSheet;
import com.app.livewave.DialogSheets.wavesplayer.TrackOptionsDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerFollowingsAdapter;
import com.app.livewave.adapters.wavesplayer.WavesPlayerMySubscriptionAdapter;
import com.app.livewave.adapters.wavesplayer.WavesPlayerPlaylistAdapter;
import com.app.livewave.adapters.wavesplayer.WavesPlayerStoreAdapter;
import com.app.livewave.adapters.wavesplayer.WavesPlayerUSerPlayStoreAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;

public class WavesPlayerUserStoreFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {

    // Add RecyclerView member
    //TextView yoursubscriptionitems_seeall;
    TextView yourstoreitems_seeall;
    TextView yourplaylists_seeall;
   // TextView yourfollowing_seeall;

   // String userId = getArguments().getString(Constants.SPECIFIC_USER_ID);;;
        String userId = Paper.book().read("UserID");
    RecyclerView subscription_recycler;
    WavesPlayerMySubscriptionAdapter wavesSubscriptionAdapter;

    RecyclerView store_recycler;
    WavesPlayerUSerPlayStoreAdapter wavesPlayerStoreAdapter;
//    RecyclerView following_recycler;
//    WavesPlayerFollowingsAdapter wavesPlayerFollowingsAdapter;
    RecyclerView playlist_recycler;
    WavesPlayerPlaylistAdapter wavesPlayerPlaylistAdapter;
    KProgressHUD dialog;

    public ArrayList<Track> trackList = new ArrayList<>();
    public ArrayList<PlayListModel> playLists = new ArrayList<>();
    public ArrayList<Track> followingList = new ArrayList<>();

    TrackOptionsDialogSheet trackOptionsDialogSheet;
    PlaylistOptionsDialogSheet playlistOptionsDialogSheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wavesplayer_user_store, container, false);
        // Add the following lines to create RecyclerView
        setupViews(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTracks(userId);
    }

    void setupViews(View view) {
      // yoursubscriptionitems_seeall = view.findViewById(R.id.yoursubscriptionitems_seeall);
        yourstoreitems_seeall = view.findViewById(R.id.artiststoreitems_seeall);
        yourplaylists_seeall = view.findViewById(R.id.artistplaylists_seeall);
        dialog = BaseUtils.progressDialog(getContext());
     //   yourfollowing_seeall = view.findViewById(R.id.yourlikes_seeall);

//        yoursubscriptionitems_seeall.setOnClickListener(v ->
//                ((HomeActivity) getActivity()).loadFragment(R.string.tag_my_store, null));
//
        yourstoreitems_seeall.setOnClickListener(v ->
        {
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_Artist_store, null);
        });
//

        yourplaylists_seeall.setOnClickListener(v -> {
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_artist_playlist, null);
        });

//        yourfollowing_seeall.setOnClickListener(v ->
//        {
//            Bundle bundle = new Bundle();
//            bundle.putString("tracks", new Gson().toJson(followingList));
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_your_followings, bundle);
//        });


//        wavesSubscriptionAdapter = new WavesPlayerMySubscriptionAdapter(new ArrayList<>(), getActivity(), false);
//        subscription_recycler = view.findViewById(R.id.subscription_recycler);
//        RecyclerView.LayoutManager subscriptionlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        subscription_recycler.setLayoutManager(subscriptionlayoutManager);
//        subscription_recycler.setAdapter(wavesSubscriptionAdapter);
//
//        wavesPlayerFollowingsAdapter = new WavesPlayerFollowingsAdapter(this, followingList, getActivity(), R.layout.item_wavesplayer_followings, true);
//        following_recycler = view.findViewById(R.id.liked_recycler);
//        RecyclerView.LayoutManager likedlayoutManager = new LinearLayoutManager(getActivity());
//        following_recycler.setLayoutManager(likedlayoutManager);
//        following_recycler.setAdapter(wavesPlayerFollowingsAdapter);

        wavesPlayerPlaylistAdapter = new WavesPlayerPlaylistAdapter(this, playLists, getActivity(), R.layout.item_waves_player_artist_playlist, true);
        playlist_recycler = view.findViewById(R.id.playlists_recycler);
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        playlist_recycler.setLayoutManager(layoutManagerHorizontal);
        playlist_recycler.setAdapter(wavesPlayerPlaylistAdapter);

        wavesPlayerStoreAdapter = new WavesPlayerUSerPlayStoreAdapter(trackList, getActivity(), true);
        store_recycler = view.findViewById(R.id.store_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        store_recycler.setLayoutManager(layoutManager);
        store_recycler.setAdapter(wavesPlayerStoreAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getUserTracks(userId).cancel();
        ApiClient.getInstance().getInterface().getUserPlaylist(userId).cancel();
    }

    public void getTracks(String userData) {
        dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getUserTracks(userData), getActivity(), new ApiResponseHandler<List<Track>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<Track>>> data) {
                appTrackData(data.body().getData());
            }
        });
    }

    public void getPlaylist() {
        dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getUserPlaylist(userId), getActivity(), new ApiResponseHandlerWithFailure<List<PlayListModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<PlayListModel>>> data) {
                dialog.dismiss();
                playlistData(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
//                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
//                });
            }
        });
    }

//    public void getFollowinglist() {
//        ((HomeActivity) getActivity()).showProgressDialog();
//        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowingTracks(), getActivity(), data -> appFollowingData(data.body().getData()));
//    }

    private void appTrackData(List<Track> data) {
        trackList.clear();
        for (int i = 0; i < data.size(); i++) {
            trackList.add(data.get(i));
        }
        wavesPlayerStoreAdapter.notifyDataSetChanged();
        dialog.dismiss();
        getPlaylist();
    }

    private void playlistData(List<PlayListModel> data) {
        playLists.clear();
        for (int i = 0; i < data.size(); i++) {
            playLists.add(data.get(i));
        }
        wavesPlayerPlaylistAdapter.notifyDataSetChanged();
        dialog.dismiss();
       // getFollowinglist();
    }

//    private void appFollowingData(List<Track> data) {
//        followingList.clear();
//        for (int i = 0; i < data.size(); i++) {
//            followingList.add(data.get(i));
//        }
//        wavesPlayerFollowingsAdapter.notifyDataSetChanged();
//        ((HomeActivity) getActivity()).hideProgressDialog();
//    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {
        if (isOwner) {
            trackOptionsDialogSheet = new TrackOptionsDialogSheet(this, track);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            trackOptionsDialogSheet.show(fm, "trackoptionsDialogSheet");
        } else {
            AddToPlaylistDialog addToPlaylistDialog = new AddToPlaylistDialog(this, track);
            FragmentManager fm = getChildFragmentManager();
            addToPlaylistDialog.show(fm, "addToPlaylistSheet");
        }
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
        if (playlistOptionsDialogSheet != null) {
            playlistOptionsDialogSheet.dismiss();
        }
        getPlaylist();
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
        playlistOptionsDialogSheet = new PlaylistOptionsDialogSheet(this, playListModel);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        playlistOptionsDialogSheet.show(fm, "playlistOptionsDialogSheet");
    }

}
