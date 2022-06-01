package com.app.livewave.wavesplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kotlin.jvm.internal.Intrinsics;

public class Utils {

    public static Bitmap songArt(String path, Context context) {
        Bitmap bmp = null;
        try {
            InputStream in = new URL(path).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp == null ? getLargeIcon(context) : bmp;
    }

    public static Bitmap songArtLocal(String path, Context context) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        InputStream inputStream;
        retriever.setDataSource(path);
        if (retriever.getEmbeddedPicture() != null) {
            inputStream = new ByteArrayInputStream(retriever.getEmbeddedPicture());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            retriever.release();
            return bitmap;
        } else {
            return getLargeIcon(context);
        }
    }

    private static Bitmap getLargeIcon(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wavesplayer);
    }

    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    String formatDuration(int duration) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(duration)),
                TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(duration)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(duration))));
    }

    int formatTrack(int trackNumber) {
        int formatted = trackNumber;
        if (trackNumber >= 1000) {
            formatted = trackNumber % 1000;
        }
        return formatted;
    }

    public static void delete(final AppCompatActivity activity, final File imageFile) {
        Handler handler = new Handler();
        handler.postDelayed((Runnable) (new Runnable() {
            public final void run() {
                String[] projection = new String[]{"_id"};
                String selection = "_data = ?";
                String[] var10000 = new String[1];
                String var10003 = imageFile.getAbsolutePath();
                Intrinsics.checkExpressionValueIsNotNull(var10003, "imageFile.absolutePath");
                var10000[0] = var10003;
                String[] selectionArgs = var10000;

                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = activity.getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, (String) null);
                if (c == null) {
                    Intrinsics.throwNpe();
                }
                if (c.moveToFirst()) {
                    long id = c.getLong(c.getColumnIndexOrThrow("_id"));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                    contentResolver.delete(deleteUri, (String) null, (String[]) null);
                } else {
                    Log.w("Media ", "Media not found!!");
                }

                c.close();
            }
        }), 70L);
    }
}
