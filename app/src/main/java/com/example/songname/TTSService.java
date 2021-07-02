package com.example.songname;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class TTSService extends JobIntentService {
    private TextToSpeech mySpeakTextToSpeech = null;
    private boolean isSafeToDestroy = false;

    // Unique job ID for this service.
    private static int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, TTSService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String message = intent.getStringExtra("MESSAGE");

        mySpeakTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mySpeakTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, null);
            } else {
                mySpeakTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
            }

            while (mySpeakTextToSpeech.isSpeaking()) {
                // do nothing
            }
            isSafeToDestroy = true;
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
