package com.example.alarmproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MediaPlayActivity extends AppCompatActivity {

    ImageView iv;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplay);
        iv = (ImageView)findViewById(R.id.imageView);

        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
        }
        Intent intent = getIntent();
        String str = intent.getStringExtra("mediaSelect");
        Log.d("Mediaplay Spin number", str);
        if("은하".equals(str)){
            mp = MediaPlayer.create(MediaPlayActivity.this, R.raw.eunha);
            iv.setImageResource(R.drawable.eunha);
        }else if("시완".equals(str)){
            iv.setImageResource(R.drawable.siwan);
            mp = MediaPlayer.create(MediaPlayActivity.this, R.raw.siwan);
        }
        mp.start();

        Button btn_stop = findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                Intent intent = new Intent(MediaPlayActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}