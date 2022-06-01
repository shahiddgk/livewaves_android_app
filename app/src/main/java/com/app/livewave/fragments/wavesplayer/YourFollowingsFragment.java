package com.app.livewave.fragments.wavesplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.AddToPlaylistDialog;
import com.app.livewave.R;
import com.app.livewave.adapters.wavesplayer.WavesPlayerFollowingsAdapter;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class YourFollowingsFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {
    // Add RecyclerView member
    private RecyclerView recyclerView;
    public ArrayList<Track> trackList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_likes, container, false);

        Bundle bundle = getArguments();
        if (bundle.containsKey("tracks")) {
            String jsonString = bundle.getString("tracks");

            Gson gson = new Gson();
            Type playlist = new TypeToken<List<Track>>() {
            }.getType();
            trackList = gson.fromJson(jsonString, playlist);
        }

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.yourlikes_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new WavesPlayerFollowingsAdapter(this, trackList, getActivity(), R.layout.item_wavesplayer_followings, false));

        return view;
    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {
        AddToPlaylistDialog addToPlaylistDialog = new AddToPlaylistDialog(this, track);
        FragmentManager fm = getChildFragmentManager();
        addToPlaylistDialog.show(fm, "addToPlaylistSheet");
    }

    @Override
    public void onTrackListUpdateEvent() {

    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {

    }

    @Override
    public void onPlaylistSelectEvent(int position) {

    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }
}
