package com.app.livewave.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;
import com.app.livewave.utils.Constants;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.EventListener;

public class VideoPlayerActivity extends AppCompatActivity implements EventListener {

    PlayerView exoplayerView;
    SimpleExoPlayer absPlayerInternal;
    int appNameStringRes = R.string.app_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();
        String url = getIntent().getStringExtra(Constants.URL);
        System.out.println(url);
        absPlayerInternal = new SimpleExoPlayer.Builder(this).build();
        exoplayerView.setPlayer(absPlayerInternal);
        MediaItem mediaItem = MediaItem.fromUri(url);
        absPlayerInternal.setMediaItem(mediaItem);
        absPlayerInternal.prepare();
        absPlayerInternal.play();

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        exoplayerView = findViewById(R.id.exoplayerView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    private void stopPlayer() {
        exoplayerView.setPlayer(null);
        if (absPlayerInternal != null)
            absPlayerInternal.release();
        absPlayerInternal = null;
    }
}