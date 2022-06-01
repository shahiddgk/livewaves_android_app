package com.app.livewave.wavesplayer.playback;

import android.media.MediaPlayer;

import com.app.livewave.models.ResponseModels.Track;

import java.util.ArrayList;

public interface PlayerAdapter {

    boolean isMediaPlayer();

    boolean isPlaying();

    boolean isReset();

    Track getCurrentSong();

    int getState();

    int getPlayerPosition();

    MediaPlayer getMediaPlayer();

    void initMediaPlayer();

    void release();

    void resumeOrPause();

    void closePlayer();

    void reset();

    void instantReset();

    void skip(boolean isNext);

    void seekTo(int position);

    void setPlaybackInfoListener(PlaybackInfoListener playbackInfoListener);

    void registerNotificationActionsReceiver(boolean isRegister);


    void setCurrentSong(Track song, ArrayList<Track> songs);

    void onPauseActivity();

    void onResumeActivity();
}