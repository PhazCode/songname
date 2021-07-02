package com.example.songname;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import static com.example.songname.App.notificationChannelID;

public class MySpeakService extends JobIntentService {
    private TextToSpeech mySpeakTextToSpeech = null;
    private boolean isSafeToDestroy = false;

    private final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, notificationChannelID)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Is running")
                        .setOngoing(true);
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        return START_STICKY;
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MySpeakService.class, 1, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String message = intent.getStringExtra("MESSAGE");

        mySpeakTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mySpeakTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, null);
                } else {
                    mySpeakTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
                }

                while (mySpeakTextToSpeech.isSpeaking()) {
                    // do nothing
                }
                isSafeToDestroy = true;
            }
        });
    }

    @Override
    public void onDestroy() {
        if (isSafeToDestroy) {
            if (mySpeakTextToSpeech != null) {
                mySpeakTextToSpeech.shutdown();
            }
            super.onDestroy();
        }
    }
}
