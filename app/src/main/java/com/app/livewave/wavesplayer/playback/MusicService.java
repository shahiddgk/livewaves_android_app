package com.app.livewave.wavesplayer.playback;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import org.jetbrains.annotations.NotNull;

public class MusicService extends Service {
    private final LocalBinder mIBinder = new LocalBinder();

    public MediaPlayerHolder mediaPlayerHolder;
    public MusicNotificationManager musicNotificationManager;
    private boolean isRestoredFromPause = false;

    public final boolean isRestoredFromPause() {
        return this.isRestoredFromPause;
    }

    public final void setRestoredFromPause(boolean var1) {
        this.isRestoredFromPause = var1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayerHolder.registerNotificationActionsReceiver(false);
        musicNotificationManager = null;
        mediaPlayerHolder.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mediaPlayerHolder == null) {
            mediaPlayerHolder = new MediaPlayerHolder(this);
            musicNotificationManager = new MusicNotificationManager(this);
            mediaPlayerHolder.registerNotificationActionsReceiver(true);
        }
        return mIBinder;
    }

    public final class LocalBinder extends Binder {
        @NotNull
        public final MusicService getInstance() {
            return MusicService.this;
        }
    }
}