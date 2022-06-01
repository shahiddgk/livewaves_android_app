package com.app.livewave.services;

import static com.app.livewave.utils.Constants.HAS_EXTRA;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.BottomDialogSheets.TimeLinePostDialogueOptions;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.SplashActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import io.paperdb.Paper;
import retrofit2.Response;

public class FirebaseFCMService extends FirebaseMessagingService {


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Paper.init(this);

        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNotification(RemoteMessage message) {
        Intent intent = new Intent();
        if (message.getData() == null || message.getData().get("type") == null) {
            Log.e("errorNotification", "message is empty :(");
            return;
        }

        if (message.getData().get("type").equalsIgnoreCase("follow")) {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(HAS_EXTRA, R.string.tag_user_profile);
            intent.putExtra(Constants.SPECIFIC_USER_ID, message.getData().get("senderID"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
        } else if (message.getData().get("type").equalsIgnoreCase("post")) {
            System.out.println("MESSAGE BODY");
            System.out.println(message.getData().get("body"));

            if (message.getData().get("body").equalsIgnoreCase("has posted on your profile")) {
                TimeLinePostDialogueOptions timeLinePostDialogueOptions = new TimeLinePostDialogueOptions(Integer.parseInt(message.getData().get("contentID")));
                FragmentManager fragmentManager = ((FragmentActivity) getBaseContext()).getSupportFragmentManager();
                timeLinePostDialogueOptions.show(fragmentManager, "Time Line Post Option");
                timeLinePostDialogueOptions.addListener(pressedButton -> {
                    if (pressedButton.equals(getBaseContext().getString(R.string.delete))) {
                        BaseUtils.showAlertDialog("Alert", "Are you sure want to delete this post?", getApplicationContext(), positive -> {
                            if (positive) {
                                deletePost(message.getData().get("contentID"));
                            }
                        });
                    } else if (pressedButton.equals(getApplicationContext().getString(R.string.add))) {

                        addPost(Integer.parseInt(message.getData().get("contentID")));

                    }  //add option if any

                    timeLinePostDialogueOptions.dismiss();

                });
            } else {
                intent.putExtra(Constants.SPECIFIC_USER_ID, message.getData().getOrDefault("contentID", ""));
                intent.putExtra(Constants.SPECIFIC_POST_ID, message.getData().getOrDefault("childID", ""));
            }

        }else if (message.getData().get("type").equalsIgnoreCase("reaction")
                || message.getData().get("type").equalsIgnoreCase("comment")
                || message.getData().get("type").equalsIgnoreCase("comment-tag")
                || message.getData().get("type").equalsIgnoreCase("tag")) {


            intent = new Intent(this, SplashActivity.class);
            intent.putExtra("type", message.getData().get("type"));
            if (message.getData().get("type").equalsIgnoreCase("comment")
                    || message.getData().get("type").equalsIgnoreCase("comment-tag")) {
                intent.putExtra(Constants.SPECIFIC_USER_ID, message.getData().getOrDefault("contentID", ""));
                intent.putExtra(Constants.SPECIFIC_POST_ID, message.getData().getOrDefault("childID", ""));

            } else if ( message.getData().get("type").equalsIgnoreCase("reaction") || message.getData().get("type").equalsIgnoreCase("tag")) {
                intent.putExtra(Constants.SPECIFIC_USER_ID, message.getData().getOrDefault("contentID", ""));
                intent.putExtra(Constants.SPECIFIC_POST_ID, message.getData().getOrDefault("childID", ""));
            }
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);

        } else if (message.getData().get("type").equalsIgnoreCase("stream")) {

            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(HAS_EXTRA, R.string.tag_user_profile);
            Log.e("!@#!@#!@#", message.getData().getOrDefault("senderID", ""));
            intent.putExtra(Constants.SPECIFIC_USER_ID, message.getData().getOrDefault("senderID", ""));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);

        } else if (message.getData().get("type").equalsIgnoreCase("event")) {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(HAS_EXTRA, R.string.tag_events_detail);
            intent.putExtra(Constants.EVENT_ID, message.getData().get("contentID"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
        } else if (message.getData().get("type").equalsIgnoreCase("stream-invite") || message.getData().get("type").equalsIgnoreCase("event-invite")) {
            intent = new Intent(this, SplashActivity.class);
            intent.putExtra("type", message.getData().get("type"));
            intent.putExtra("stream_id", message.getData().get("contentID"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);

        } else if (Objects.requireNonNull(message.getData().get("type")).equalsIgnoreCase("message")) {
            intent = new Intent(this, SplashActivity.class);
            intent.putExtra("type", message.getData().get("type"));
            intent.putExtra("contentID", message.getData().get("contentID"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
        } else {
            intent = new Intent(this, SplashActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "LiveWaves";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(message.getNotification().getTitle())
                        .setContentText(message.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void deletePost(String contentID) {

    }

    private void addPost(int contentID) {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().AddPostToTimeLine(contentID), getApplicationContext(), new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println(data);
            }
        });

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }
}
