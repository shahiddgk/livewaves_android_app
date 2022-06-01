package com.app.livewave.wavesplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.app.livewave.wavesplayer.models.Track;

import java.util.ArrayList;

public class SongProvider {

    private static SongProvider songProvider;

    public static SongProvider getInstance() {
        if (songProvider == null) {
            songProvider = new SongProvider();
        }
        return songProvider;
    }

    private int TITLE = 0;
    private int TRACK = 1;
    private int YEAR = 2;
    private int DURATION = 3;
    private int PATH = 4;
    private int ALBUM = 5;
    private int ARTIST_ID = 6;
    private int ARTIST = 7;

    private String[] BASE_PROJECTION = {MediaStore.Audio.AudioColumns.TITLE, // 0
            MediaStore.Audio.AudioColumns.TRACK, // 1
            MediaStore.Audio.AudioColumns.YEAR, // 2
            MediaStore.Audio.AudioColumns.DURATION, // 3
            MediaStore.Audio.AudioColumns.DATA, // 4
            MediaStore.Audio.AudioColumns.ALBUM, // 5
            MediaStore.Audio.AudioColumns.ARTIST_ID, // 6
            MediaStore.Audio.AudioColumns.ARTIST};// 7

    private ArrayList<Track> mAllDeviceSongs = new ArrayList<>();

    public ArrayList<Track> getAllDeviceSongs(Context context) {
        Cursor cursor = makeSongCursor(context);
        return getSongs(cursor);
    }


    private ArrayList<Track> getSongs(Cursor cursor) {
        ArrayList<Track> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Track song = getSongFromCursorImpl(cursor);
                if (song.getDuration() >= 30000) {
                    songs.add(song);
                    mAllDeviceSongs.add(song);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }


    private Track getSongFromCursorImpl(Cursor cursor) {
        String title = cursor.getString(TITLE);
        int trackNumber = cursor.getInt(TRACK);
        int year = cursor.getInt(YEAR);
        int duration = cursor.getInt(DURATION);
        String uri = cursor.getString(PATH);
        String albumName = cursor.getString(ALBUM);
        int artistId = cursor.getInt(ARTIST_ID);
        String artistName = cursor.getString(ARTIST);

        return new Track(title, trackNumber, year, duration, uri, albumName, artistId, artistName);
    }

    private Cursor makeSongCursor(Context context) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, null, null, null);
        } catch (SecurityException e) {
            return null;
        }

    }
}
