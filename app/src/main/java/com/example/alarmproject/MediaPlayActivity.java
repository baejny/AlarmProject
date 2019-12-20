package com.example.alarmproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class MediaPlayActivity extends AppCompatActivity {

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplay);

        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
        }
        mp = MediaPlayer.create(MediaPlayActivity.this, R.raw.alarm);
        mp.start();

        Button btn_stop = findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                Intent intent = new Intent(MediaPlayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test","MediaplayActivity Stop");
    }
}
