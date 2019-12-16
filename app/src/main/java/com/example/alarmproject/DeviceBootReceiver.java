package com.example.alarmproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    int alarmCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("test", "BootReceiver TEST");

        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE);

            for (int i = 0; i < 5; i++) {
                if (sharedPreferences.getLong(String.valueOf(i), 0) != 0) {
                    alarmCount++;
                    // on device boot complete, reset the alarm
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, 0);

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    long millis = sharedPreferences.getLong(String.valueOf(i), Calendar.getInstance().getTimeInMillis());

                    Calendar current_calendar = Calendar.getInstance();
                    Calendar nextNotifyTime = new GregorianCalendar();
                    nextNotifyTime.setTimeInMillis(sharedPreferences.getLong(String.valueOf(i), millis));

                    if (current_calendar.after(nextNotifyTime)) {
                        nextNotifyTime.add(Calendar.DATE, 1);
                    }

                    if (manager != null) {
                        manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                }
            }
            Toast.makeText(context.getApplicationContext(),"[재부팅후] " + alarmCount+ "개의 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}