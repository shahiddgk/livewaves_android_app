package com.app.livewave.wavesplayer.playback;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.wavesplayer.utils.Utils;

public class MusicNotificationManager {
    public static String CHANNEL_ID = "action.CHANNEL_ID";
    public int REQUEST_CODE = 100;
    public NotificationManager notificationManager;
    public NotificationCompat.Builder notificationBuilder;
    public MediaSessionCompat mediaSession;
    public MediaSessionManager mediaSessionManager;
    public MediaControllerCompat.TransportControls transportControls;
    public Context context;
    public MusicService mMusicService;


    public static int NOTIFICATION_ID = 101;
    static String PLAY_PAUSE_ACTION = "action.PLAYPAUSE";
    static String NEXT_ACTION = "action.NEXT";
    static String PREV_ACTION = "action.PREV";

    public MusicNotificationManager(MusicService musicService) {
        this.mMusicService = musicService;
        notificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        context = mMusicService.getBaseContext();
    }


    private PendingIntent playerAction(String action) {

        Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void deleteNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public Notification createNotification() {
        Track song = mMusicService.mediaPlayerHolder.getCurrentSong();

        notificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Intent openPlayerIntent = new Intent(mMusicService, HomeActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE, openPlayerIntent, 0);

        String artist = song.getDescription();
        String songTitle = song.getTitle();

        initMediaSession(song);

        notificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.app_launcher)
                .setLargeIcon(Utils.songArt(song.getAttachment(), mMusicService.getBaseContext()))
                .setColor(ContextCompat.getColor(context, R.color.buttercup))
                .setContentTitle(songTitle)
                .setContentText(artist)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2));
        return notificationBuilder.build();
    }

    private NotificationCompat.Action notificationAction(String action) {
        int icon;
        if (action == PREV_ACTION) {
            icon = R.drawable.ic_previous_filled;
        } else if (action == PLAY_PAUSE_ACTION) {
            if (mMusicService.mediaPlayerHolder.getState() != PlayerState.PAUSED.ordinal()) {
                icon = R.drawable.ic_pause;
            } else {
                icon = R.drawable.ic_play_arrow;
            }
        } else if (action == NEXT_ACTION) {
            icon = R.drawable.ic_next_filled;
        } else {
            icon = R.drawable.ic_previous_filled;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        mMusicService.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription(mMusicService.getString(R.string.app_name));
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void initMediaSession(Track song) {
        mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(context, "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData(song);
    }

    private void updateMetaData(Track song) {
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, Utils.songArt(song.getAttachment(), context))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getDescription())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Empty")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .build());
    }
}