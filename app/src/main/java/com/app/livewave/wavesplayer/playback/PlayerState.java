package com.app.livewave.wavesplayer.playback;

public enum PlayerState {
    INVALID(-1),
    PLAYING (0),
    PAUSED (1),
    COMPLETED (2),
    RESUMED (3),
    PREPARING (4);

    private final int stateValue;
    PlayerState(int i) {
        this.stateValue = i;
    }
}
