package com.app.livewave.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.MusicNotificationManager;
import com.app.livewave.wavesplayer.playback.MusicService;
import com.app.livewave.wavesplayer.playback.PlaybackInfoListener;
import com.app.livewave.wavesplayer.playback.PlayerAdapter;
import com.app.livewave.wavesplayer.playback.PlayerState;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.app.livewave.widgets.CircularSeekBar;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WavesPlayerBaseActivity extends AppCompatActivity implements View.OnClickListener, CircularSeekBar.OnCircularSeekBarChangeListener {
    public CircularSeekBar seekBar = null;
    public CircularSeekBar seekBarFullScreen = null;
    public ImageButton playPause = null;
    public ImageButton next = null;
    public ImageButton previous = null;
    public ImageButton playPauseFullScreen = null;
    public ImageButton nextSongFullScreen = null;
    public ImageButton previousSongFullSong = null;
    public ImageButton closePlayer = null;
    public TextView songTitle = null;
    public TextView songArtist = null;
    public TextView songTitleFullScreen = null;
    public TextView songArtistFullScreen = null;
    public MusicService mMusicService = null;
    public boolean mIsBound = false;
    public PlayerAdapter mPlayerAdapter = null;
    public boolean mUserIsSeeking = false;
    public PlaybackListener mPlaybackListener = null;
    KProgressHUD kProgressdialog;
    public static ArrayList<Track> trackList = new ArrayList<>();
    View global_wavesplayer;
    TextView timerText,timerTextFullScreen;
    PlayerStateListener playerStateListener;
    public MusicNotificationManager mMusicNotificationManager = null;
    int userSelectedPosition = 0;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicService = ((MusicService.LocalBinder) service).getInstance();
            mPlayerAdapter = mMusicService.mediaPlayerHolder;
            mMusicNotificationManager = mMusicService.musicNotificationManager;

            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
                mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
            }
            if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {

               // restorePlayerStatus();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kProgressdialog = BaseUtils.progressDialog(this);
        doBindService();

    }

    private void updatePlayingInfo(boolean restore, boolean startPlay) {
        if (startPlay) {
            mPlayerAdapter.getMediaPlayer().start();
            new Handler().postDelayed(() -> mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                    mMusicNotificationManager.createNotification()), 200);
        }
        Track selectedSong = mPlayerAdapter.getCurrentSong();

        songTitle.setText(selectedSong.getTitle());
        System.out.println("songTitle");
        System.out.println(selectedSong.getTitle());
        songTitleFullScreen.setText(selectedSong.getTitle());
        songArtist.setText(selectedSong.getArtist_name());
        System.out.println(selectedSong.getArtist_name());
        songArtistFullScreen.setText(selectedSong.getArtist_name());

        int duration = mPlayerAdapter.getMediaPlayer().getDuration();
        seekBar.setMax(duration);
        seekBarFullScreen.setMax(duration);
        ImageView imageViewControl = findViewById(R.id.imageViewControl);
        ImageView imageViewControlFullScreen = findViewById(R.id.image_full_screen_view);

        try {
            Glide.with(this).load(selectedSong.getAttachment()).placeholder(R.drawable.profile_place_holder).into(imageViewControl);
            //Glide.with(this).load(selectedSong.getAttachment()).placeholder(R.drawable.profile_place_holder).into(imageViewControlFullScreen);
        } catch (Exception e) {

        }
//        imageViewControl.setImageBitmap(Utils.songArt(selectedSong.getAttachment(), this));

        if (restore) {
            seekBar.setProgress(mPlayerAdapter.getPlayerPosition());
            seekBarFullScreen.setProgress(mPlayerAdapter.getPlayerPosition());
            updatePlayingStatus();
            new Handler().postDelayed(() -> {
                //stop foreground if coming from pause state
                if (mMusicService.isRestoredFromPause()) {
                    mMusicService.stopForeground(false);
                    mMusicService.musicNotificationManager.notificationManager
                            .notify(MusicNotificationManager.NOTIFICATION_ID,
                                    mMusicService.musicNotificationManager.notificationBuilder.build());
                    mMusicService.setRestoredFromPause(false);
                }
            }, 200);
        }

        findViewById(R.id.imageViewControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_close_full_screen_player).setVisibility(View.VISIBLE);
                findViewById(R.id.fullscreen_audio_view).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.floatPlayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.btn_close_full_screen_player).setVisibility(View.VISIBLE);
                findViewById(R.id.fullscreen_audio_view).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.image_full_screen_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_close_full_screen_player).setOnClickListener(new View.OnClickListener() {
                                                                               @Override
                                                                               public void onClick(View view) {
                                                                                   findViewById(R.id.btn_close_full_screen_player).setVisibility(View.GONE);
                                                                                   findViewById(R.id.fullscreen_audio_view).setVisibility(View.GONE);
                                                                               }
                                                                           }
        );

    }


    private void updatePlayingStatus() {
        if (playerStateListener != null) playerStateListener.updatePlayerState();
        int drawable;
        if (mPlayerAdapter.getState() == PlayerState.PREPARING.ordinal()) {
            drawable = R.drawable.ic_baseline_sync_24;
        } else if (mPlayerAdapter.getState() != PlayerState.PAUSED.ordinal()) {
            drawable = R.drawable.ic_pause_filled;
        } else {
            drawable = R.drawable.ic_play_filled;
        }
        playPauseFullScreen.post(() -> {
            playPauseFullScreen.setImageResource(drawable);
        });
        playPause.post(() -> {
            playPause.setImageResource(drawable);
        });
    }

    private void restorePlayerStatus() {
        seekBar.setEnabled(mPlayerAdapter.isMediaPlayer());
        seekBarFullScreen.setEnabled(mPlayerAdapter.isMediaPlayer());

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter.isMediaPlayer()) {
            mPlayerAdapter.onResumeActivity();
            updatePlayingInfo(true, false);
        }
    }

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Intent startNotStickyIntent = new Intent(this, MusicService.class);
        startService(startNotStickyIntent);
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void onSongSelected(Track song, ArrayList<Track> songs) {
        if (!seekBarFullScreen.isEnabled()) {
            seekBar.setEnabled(true);
            seekBarFullScreen.setEnabled(true);
        }
        try {
            mPlayerAdapter.setCurrentSong(song, songs);
            mPlayerAdapter.initMediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter.instantReset();
        }
    }

    public void resumeOrPause() {
        if (checkIsPlayer()) {
            mPlayerAdapter.resumeOrPause();
        } else {
            if (trackList.size() > 0) {
                onSongSelected(trackList.get(0), trackList);
            }
        }
    }

    public void closePlayer() {
        if (checkIsPlayer()) {
            mPlayerAdapter.closePlayer();
        }
        if (global_wavesplayer.isShown()) {
            global_wavesplayer.setVisibility(View.GONE);
        }
    }

    public void playOrResumeSong(ArrayList<Track> currentList, Track song) {
        trackList.clear();
        trackList.addAll(currentList);

        if (!global_wavesplayer.isShown()) {
            global_wavesplayer.setVisibility(View.VISIBLE);
        }
        if (mPlayerAdapter.getCurrentSong() != null) {
            if (mPlayerAdapter.getCurrentSong().getId() == song.getId() && checkIsPlayer()) {
                mPlayerAdapter.resumeOrPause();
            } else {
                onSongSelected(song, trackList);
            }
        } else {
            onSongSelected(song, trackList);
        }

    }

    public Track getCurrentPlayingSong() {
        return mPlayerAdapter.getCurrentSong();
    }

    public void showProgressDialog() {
        if (!kProgressdialog.isShowing()) {
            kProgressdialog.show();
        }
    }

    public void hideProgressDialog() {
        if (kProgressdialog == null) {

        } else {
            if (kProgressdialog.isShowing()) {
                kProgressdialog.dismiss();
            }
        }
    }

    public void skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter.skip(true);
        }
    }

    private boolean checkIsPlayer() {
        return mPlayerAdapter.isMediaPlayer();
    }

    public void initializeSeekBar() {


        findViewById(R.id.btn_close_full_screen_player).setVisibility(View.GONE);
        findViewById(R.id.fullscreen_audio_view).setVisibility(View.GONE);

        if (timerText != null) {
            timerText.setText("00:00");
            timerTextFullScreen.setText("00.00");
        }

        seekBar.setOnSeekBarChangeListener(this);
        seekBarFullScreen.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
        if (fromUser) {
            userSelectedPosition = progress;

            mPlayerAdapter.seekTo(userSelectedPosition);
        }
        if (timerText != null && timerTextFullScreen != null) {
            runOnUiThread(() -> timerText.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(progress)),
                    TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(progress)) -
                            TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(progress))))));

            runOnUiThread(() -> timerTextFullScreen.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(progress)),
                    TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(progress)) -
                            TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(progress))))));
        }
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        seekBar.getProgress();
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {

        if (mUserIsSeeking) {
            mPlayerAdapter.seekTo(userSelectedPosition);
        } else {
            mUserIsSeeking = false;
            mPlayerAdapter.seekTo(userSelectedPosition);
        }
    }

    class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                seekBar.setProgress(position);
                seekBarFullScreen.setProgress(position);
            }
            if (timerText != null && timerTextFullScreen!= null) {
                runOnUiThread(() -> timerText.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(position)),
                        TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(position)) -
                                TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(position))))));

                runOnUiThread(() -> timerTextFullScreen.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(position)),
                        TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(position)) -
                                TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(position))))));
            }
        }


        @Override
        public void onStateChanged(int state) {
            updatePlayingStatus();
            if (mPlayerAdapter.getState() != PlayerState.PAUSED.ordinal()
                    && mPlayerAdapter.getState() != PlayerState.PAUSED.ordinal()) {
                updatePlayingInfo(false, true);
            }
        }

        @Override
        public void onPlaybackCompleted() {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
        if (mPlayerAdapter != null && mPlayerAdapter.isMediaPlayer()) {
            mPlayerAdapter.onPauseActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
        if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
            restorePlayerStatus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlayPause:
                resumeOrPause();
                break;
            case R.id.play_current_song:
                resumeOrPause();
                break;
            case R.id.buttonNext:
                skipNext();
            case R.id.play_next:
                skipNext();
                break;
            case R.id.buttonPrevious:
                skipPrev();
                break;
            case R.id.play_privious:
                skipPrev();
                break;
            case R.id.btn_close_player:
                closePlayer();
                break;
        }
    }
}
