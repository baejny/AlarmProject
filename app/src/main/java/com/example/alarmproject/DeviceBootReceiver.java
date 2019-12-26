package com.example.alarmproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DeviceBootReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    AlarmManager alarmManager;
    Intent alarmIntent;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            // on device boot complete, reset the alarm
            sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE);
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int count = 0;
            for (int i = 0; i < 5; i++) {
                Long millis = sharedPreferences.getLong(String.valueOf(i), 0);
                if (millis != 0) {
                    count++;
                    alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("alarmPointer", i);
                    alarmIntent.putExtra("mediaSelect", sharedPreferences.getString(String.valueOf(i+10), null));
                    pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (alarmManager != null) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                }
            }
            Toast.makeText(context, "[재부팅] " + String.valueOf(count) +"개의 알람이 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}