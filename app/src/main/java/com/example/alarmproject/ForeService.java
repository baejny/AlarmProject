package com.example.alarmproject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

public class ForeService extends Service {

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // foreground 서비스 시작 시 notification에 등록
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "AlarmService");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            String channelName = "Foreground Alarm Service";
            String description = "AlarmProject is Running";

            NotificationChannel channel = new NotificationChannel("AlarmService", channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        startForeground(1, builder.build());


        return START_STICKY;
    }

    public IBinder onBind(Intent intent){
        return null;
    }
}
