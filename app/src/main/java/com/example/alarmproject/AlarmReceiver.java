package com.example.alarmproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import androidx.core.app.NotificationCompat;
import androidx.core.content.IntentCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    int alarmPointer;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        alarmPointer = intent.getIntExtra("alarmPointer", 0 );
        PendingIntent pendingI = PendingIntent.getActivity(context, alarmPointer, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("출석 체크 알리미!")
                .setContentText(String.valueOf(alarmPointer)+"번째 알람입니다~")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        // notification 동작 후 다음 날 같은 시간으로 저장후 toast
        if (notificationManager != null) {
            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());
            // 다음날 같은 시간으로 설정 및 Preference에 설정한 값 저장
            AlarmMethod am = new AlarmMethod(context, context.getSharedPreferences("daily alarm", MODE_PRIVATE));
            am.alarm_change(alarmPointer);

            Intent temp = new Intent(context, MediaPlayActivity.class);
            temp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            temp.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            temp.putExtra("mediaSelect", intent.getStringExtra("mediaSelect"));
            context.startActivity(temp);
        }
    }
}