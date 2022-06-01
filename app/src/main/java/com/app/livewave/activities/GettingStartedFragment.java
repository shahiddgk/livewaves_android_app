package com.app.livewave.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.GettingStartedAdapter;
import com.app.livewave.models.VideoModel;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class GettingStartedFragment extends Fragment implements PlayerStateListener {
    RecyclerView rv_getting_started;
    GettingStartedAdapter adapter;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_getting_started, container, false);
        setHasOptionsMenu(true);
        initViews(view);

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_getting_started);
//        initViews();
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Getting Started");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//    }

    private void initViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        rv_getting_started = view.findViewById(R.id.rv_getting_started);
        rv_getting_started.setHasFixedSize(false);
        rv_getting_started.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new GettingStartedAdapter(getActivity());
        rv_getting_started.setAdapter(adapter);
        getList();
    }

    private void getList() {
        List<VideoModel> videoModelList = new ArrayList<>();
        videoModelList.add(new VideoModel("How to get started", "https://firebasestorage.googleapis.com/v0/b/live-waves.appspot.com/o/Tutorials%2FTutorial%20for%20all%20users.mp4?alt=media&token=51a229ce-5e1d-4c31-8b0f-32a26b1e4179", "https://firebasestorage.googleapis.com/v0/b/live-waves.appspot.com/o/Tutorials%2Fvideo_for_everone.png?alt=media&token=712e955d-11ae-4cbe-9ce8-d3fab2134504"));
        videoModelList.add(new VideoModel("How to get started as Content Creator", "https://firebasestorage.googleapis.com/v0/b/live-waves.appspot.com/o/Tutorials%2FTutorial%20for%20Conten%20Creators%20New.mp4?alt=media&token=c71d6013-c366-43be-a97d-727449fc6e71", "https://firebasestorage.googleapis.com/v0/b/live-waves.appspot.com/o/Tutorials%2Fvideo%20for%20content.png?alt=media&token=53d141e4-603e-479a-b0bc-e3b4f8047a4a"));
        adapter.setList(videoModelList);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePlayerState() {

    }
}