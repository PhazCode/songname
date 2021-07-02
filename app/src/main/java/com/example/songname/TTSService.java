package com.example.songname;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.Locale;

public class TTSService extends JobIntentService {
    private TextToSpeech mTextToSpeech = null;
    private boolean isSafeToDestroy = false;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, TTSService.class, 1, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String message = intent.getStringExtra(SpotifyReceiverService.EXTRA_MESSAGE);

        mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            mTextToSpeech.setLanguage(Locale.US);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, null);
            } else {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
            }

            while (mTextToSpeech.isSpeaking()) {
                // wait for speaking to finish
            }
            isSafeToDestroy = true;
        });
    }

    @Override
    public void onDestroy() {
        if (isSafeToDestroy) {
            if (mTextToSpeech != null) {
                mTextToSpeech.shutdown();
            }
            super.onDestroy();
        }
    }
}
