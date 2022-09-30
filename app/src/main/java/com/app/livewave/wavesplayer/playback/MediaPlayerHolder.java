package com.app.livewave.wavesplayer.playback;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import com.app.livewave.models.ResponseModels.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaPlayerHolder implements PlayerAdapter,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private float AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private float AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private float AUDIO_FOCUSED = 2;

    private Context mContext;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer = null;
    private PlaybackInfoListener mPlaybackInfoListener = null;
    private ScheduledExecutorService mExecutor = null;
    private Runnable mSeekBarPositionUpdateTask = null;
    private Track mSelectedSong = null;
    private List<Track> mSongs = null;
    private boolean sReplaySong = false;

    private int mState = 0;
    private NotificationReceiver mNotificationActionsReceiver = null;
    private MusicNotificationManager mMusicNotificationManager = null;
    private float mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private boolean mPlayOnFocusGain = false;
    MusicService mMusicServicekt;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;

    public MediaPlayerHolder(MusicService mMusicServicekt) {
        this.mMusicServicekt = mMusicServicekt;
        mContext = mMusicServicekt.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    mCurrentAudioFocusState = AUDIO_FOCUSED;
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Lost audio focus, but will gain it back (shortly), so note whether
                    // playback should resume
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                    mPlayOnFocusGain = isMediaPlayer() && mState == PlayerState.PLAYING.ordinal() || mState == PlayerState.RESUMED.ordinal();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Lost audio focus, probably "permanently"
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                }
                if (mMediaPlayer != null) {
                    // Update the player state based on the change
                    configurePlayerState();
                }
            }
        };
    }

    private void registerActionsReceiver() {
        mNotificationActionsReceiver = new NotificationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicNotificationManager.PREV_ACTION);
        intentFilter.addAction(MusicNotificationManager.PLAY_PAUSE_ACTION);
        intentFilter.addAction(MusicNotificationManager.NEXT_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mMusicServicekt.registerReceiver(mNotificationActionsReceiver, intentFilter);
    }

    private void unregisterActionsReceiver() {
        if (mMusicServicekt != null && mNotificationActionsReceiver != null) {
            try {
                mMusicServicekt.unregisterReceiver(mNotificationActionsReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void registerNotificationActionsReceiver(boolean isReceiver) {
        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    @Override
    public Track getCurrentSong() {
        return mSelectedSong;
    }

    @Override
    public void setCurrentSong(Track song, ArrayList<Track> songs) {
        mSelectedSong = song;
        mSongs = songs;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PlayerState.COMPLETED.ordinal());
            mPlaybackInfoListener.onPlaybackCompleted();
        }
        if (sReplaySong) {
            if (isMediaPlayer()) {
                resetSong();
            }
            sReplaySong = false;
        } else {
            skip(true);
        }
    }

    @Override
    public void onResumeActivity() {
        startUpdatingCallbackWithPosition();
    }

    @Override
    public void onPauseActivity() {
        stopUpdatingCallbackWithPosition();
    }

    private void tryToGetAudioFocus() {

        int result = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    @Override
    public void setPlaybackInfoListener(PlaybackInfoListener playbackInfoListener) {
        mPlaybackInfoListener = playbackInfoListener;
    }

    private void setStatus(int state) {
        mState = state;
        mSelectedSong.setPlaying(state);
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(state);
        }
    }

    private void resumeMediaPlayer() {
        if (!isPlaying()) {
            mMediaPlayer.start();
            setStatus(PlayerState.RESUMED.ordinal());
            mMusicServicekt.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        }
    }

    private void pauseMediaPlayer() {
        setStatus(PlayerState.PAUSED.ordinal());
        mMediaPlayer.pause();
        mMusicServicekt.stopForeground(false);
        mMusicNotificationManager.notificationManager.notify(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
    }

    private void closeMediaPlayer() {
        setStatus(PlayerState.PAUSED.ordinal());
        mMediaPlayer.stop();
        mMusicServicekt.stopForeground(false);
        //mMusicNotificationManager.notificationManager.notify(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        mMusicNotificationManager.notificationManager.cancel(MusicNotificationManager.NOTIFICATION_ID);
    }

    private void resetSong() {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
        setStatus(PlayerState.PLAYING.ordinal());
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekBarPositionUpdateTask = null;
        }
    }

    private void updateProgressCallbackTask() {
        if (isMediaPlayer() && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void instantReset() {
        if (isMediaPlayer()) {
            if (mMediaPlayer.getCurrentPosition() < 5000) {
                skip(false);
            } else {
                resetSong();
            }
        }
    }

    /**
     * Once the [MediaPlayer] is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the [MainActivity] the [MediaPlayer] is
     * released. Then in the onStart() of the [MainActivity] a new [MediaPlayer]
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */

    @Override
    public void initMediaPlayer() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = new MediaPlayer();
                setStatus(PlayerState.PREPARING.ordinal());
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mMusicNotificationManager = mMusicServicekt.musicNotificationManager;
            }
            tryToGetAudioFocus();
            mMediaPlayer.setDataSource(mSelectedSong.getTrackPath());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            skip(true);
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mMediaPlayer.setOnCompletionListener(this);
        startUpdatingCallbackWithPosition();
        setStatus(PlayerState.PLAYING.ordinal());
    }

    @Override
    public void release() {
        if (isMediaPlayer()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            giveUpAudioFocus();
            unregisterActionsReceiver();
        }
    }

    @Override
    public boolean isPlaying() {
        return isMediaPlayer() && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isReset() {
        return false;
    }

    @Override
    public void resumeOrPause() {
        if (isPlaying()) {
            pauseMediaPlayer();
        } else {
            resumeMediaPlayer();
        }
    }

    @Override
    public void closePlayer() {
        if (isPlaying()) {
            closeMediaPlayer();
            release();

        }
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public boolean isMediaPlayer() {
        return mMediaPlayer != null;
    }

    @Override
    public void reset() {
        sReplaySong = !sReplaySong;
    }

    public boolean issReplaySong() {
        return sReplaySong;
    }

    @Override
    public void skip(boolean isNext) {
        getSkipSong(isNext);
    }

    private void getSkipSong(boolean isNext) {
        int currentIndex = mSongs.indexOf(mSelectedSong);
        int index;
        try {
            if (isNext) index = currentIndex + 1;
            else index = currentIndex - 1;
            mSelectedSong = mSongs.get(index);
        } catch (IndexOutOfBoundsException e) {
            if (currentIndex != 0) mSelectedSong = mSongs.get(0);
            else mSongs.get(mSongs.size() - 1);
            e.printStackTrace();
        }
        initMediaPlayer();
    }

    @Override
    public void seekTo(int position) {
        if (isMediaPlayer()) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getPlayerPosition() {
        return mMediaPlayer.getCurrentPosition();
    }


    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the MediaPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pauseMediaPlayer();
        } else {

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                resumeMediaPlayer();
                mPlayOnFocusGain = false;
            }
        }
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action != null) {

                if (action == MusicNotificationManager.PREV_ACTION) {
                    instantReset();
                } else if (action == MusicNotificationManager.PLAY_PAUSE_ACTION) {
                    resumeOrPause();
                } else if (action == MusicNotificationManager.NEXT_ACTION) {
                    skip(true);
                } else if (action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
                    if (mSelectedSong != null) {
                        pauseMediaPlayer();
                    }
                } else if (action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                    if (mSelectedSong != null && !isPlaying()) {
                        resumeMediaPlayer();
                    }
                } else if (action == Intent.ACTION_HEADSET_PLUG) {
                    if (mSelectedSong != null) {
                        if (intent.getIntExtra("state", -1) == 0) {
                            pauseMediaPlayer();
                        } else if (intent.getIntExtra("state", -1) == 0) {
                            if (!isPlaying()) {
                                resumeMediaPlayer();
                            }
                        }
                    }
                } else if (action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                    if (isPlaying()) {
                        pauseMediaPlayer();
                    }
                }

            }
        }
    }
}
