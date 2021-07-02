package com.example.songname;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.Locale;

public class TTSService extends JobIntentService {
    private TextToSpeech mTextToSpeech = null;
    private boolean isSafeToDestroy = false;
    private AudioManager am;
    private final AudioManager.OnAudioFocusChangeListener afl = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO React to audio-focus changes here!
        }
    };


    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, TTSService.class, 1, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String message = intent.getStringExtra(SpotifyReceiverService.EXTRA_MESSAGE);

        mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            mTextToSpeech.setLanguage(Locale.US);
            mTextToSpeech.setSpeechRate(0.8f);

            am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

            int focus_res = am.requestAudioFocus(
                    afl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            );

            // speak and wait for finishing


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, null);
            } else {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
            }

            while (mTextToSpeech.isSpeaking()) {
                // wait for speaking to finish
            }
            am.abandonAudioFocus(afl);
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
