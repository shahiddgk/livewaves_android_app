package com.app.livewave.wavesplayer.playback;

public abstract class PlaybackInfoListener {
    public abstract void onPositionChanged(int position);
    public abstract void onStateChanged(int state);
    public abstract void onPlaybackCompleted();
}