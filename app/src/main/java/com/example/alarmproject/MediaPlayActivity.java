package com.example.alarmproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MediaPlayActivity extends AppCompatActivity {

    MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("test", "MediaPlayActivity Test");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplay);

        mp = MediaPlayer.create(MediaPlayActivity.this, R.raw.alarm);
        mp.start();

        Button btn_stop = findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Intent intent = new Intent(MediaPlayActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }
}
