package com.example.songname;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.songname.App.notificationChannelID;

public class SpotifyReceiverService extends Service {
    private static final String TAG = SpotifyReceiverService.class.getSimpleName();
    static final String SPOTIFY_PACKAGE = "com.spotify.music";
    static final String ACTION = SPOTIFY_PACKAGE + ".metadatachanged";
    private BroadcastReceiver recevier;
    private final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String currentTrack = "";
    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        this.recevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(ACTION)) {
                    String trackId = intent.getStringExtra("id");
                    String artistName = intent.getStringExtra("artist");
                    String albumName = intent.getStringExtra("album");
                    String trackName = intent.getStringExtra("track");
                    int trackLengthInSec = intent.getIntExtra("length", 0);

                    Log.d("trackname", trackName);

                    if (!trackName.equals(currentTrack)) {
                        currentTrack = trackName;
                        TTS(trackName);
                    }
                }
            }
        };
        this.registerReceiver(this.recevier, filter);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, notificationChannelID)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Is running")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setOngoing(true);

        Intent stopSelf = new Intent(this, SpotifyReceiverService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground, "Stop", pStopSelf);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private static final String ACTION_STOP_SERVICE = "com.example.songname.ACTION_STOP_SERVICE";

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
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.recevier);
    }

    private void TTS(String trackName) {
        Intent speechIntent = new Intent();
        speechIntent.putExtra("MESSAGE", trackName);
        TTSService.enqueueWork(this, speechIntent);
    }
}