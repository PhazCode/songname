package com.phazcode.songname;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "Song.App";
    static String notificationChannelID = "SongChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Creating Song.App");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannels();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannels() {
        try {
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel =
                    new NotificationChannel(notificationChannelID,
                            getString(R.string.listening),
                            NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
