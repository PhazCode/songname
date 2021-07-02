package com.example.songname;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of our BroadcastReceiver
        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
        IntentFilter providerChanged = new IntentFilter();
        providerChanged.addAction("com.spotify.music.metadatachanged");
        registerReceiver(receiver, providerChanged);
    }
}


