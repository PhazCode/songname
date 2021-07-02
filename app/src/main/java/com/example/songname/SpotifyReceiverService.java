package com.example.songname;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import static com.example.songname.App.notificationChannelID;

public class SpotifyReceiverService extends Service {
    static final String SPOTIFY_PACKAGE = "com.spotify.music";
    static final String ACTION = SPOTIFY_PACKAGE + ".metadatachanged";
    static final String EXTRA_MESSAGE = "com.example.songname.MESSAGE";
    private static final String ACTION_STOP_SERVICE = "com.example.songname.ACTION_STOP_SERVICE";
    private final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver receiver;
    private String currentTrack;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(ACTION)) {
                    String trackName = intent.getStringExtra("track");
                    String artistName = intent.getStringExtra("artist");

                    if (!trackName.equals(currentTrack)) {
                        currentTrack = trackName;

                        boolean artist = prefs.getBoolean("artist", false);
                        if (artist)
                            speak(String.format(getString(R.string.next_song_by), trackName, artistName));
                        else
                            speak(String.format(getString(R.string.next_song), trackName));
                    }
                }
            }
        };
        this.registerReceiver(this.receiver, filter);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, notificationChannelID)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_information))
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setOngoing(true);

        Intent stopSelf = new Intent(this, SpotifyReceiverService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground, getString(R.string.action_stop), pStopSelf);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NOTIFICATION_ID, builder.build());
        else
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            mNotificationManager.cancel(NOTIFICATION_ID);
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(this.receiver);
        super.onDestroy();
    }

    private void speak(String message) {
        Intent speechIntent = new Intent();
        speechIntent.putExtra(EXTRA_MESSAGE, message);
        TTSService.enqueueWork(this, speechIntent);
    }
}