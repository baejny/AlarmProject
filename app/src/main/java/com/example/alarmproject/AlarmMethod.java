package com.example.alarmproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmMethod{
    Context context;
    SharedPreferences sharedPreferences;

    Intent alarmIntent;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    int alarmCount;
    int alarmPointer;

    AlarmMethod(Context context, SharedPreferences sharedPreferences){
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.alarmIntent = new Intent(context, AlarmReceiver.class);
        this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("test", "AlarmMethod Constructed");
    }

    void alarm_insert(int hour, int minute){
        alarmCount = getAlarmCount();
        if (alarmCount < 5) {
            for (int i = 0; i < 5; i++) {
                if (sharedPreferences.getLong(String.valueOf(i), 0) == 0) {
                    alarmPointer = i;
                    break;
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
                Toast.makeText(context,"다음날 같은 시간으로 설정합니다!", Toast.LENGTH_SHORT).show();
            }

            Date currentDateTime = calendar.getTime();
            String date_text = new SimpleDateFormat("yyyy년MM월dd일 hh시mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context, date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(String.valueOf(alarmPointer), (long)calendar.getTimeInMillis());
            editor.apply();

            Long millis = calendar.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(context, alarmPointer, alarmIntent, 0);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                }
            }
        }else{
            Toast.makeText(context, "알람이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    void alarm_delete(int SelectedItemPosition){
        Log.d("test","delete Test");
        alarmCount = getAlarmCount();
        if(alarmCount > 0){
            pendingIntent = PendingIntent.getBroadcast(context, SelectedItemPosition, alarmIntent, 0);
            if (PendingIntent.getBroadcast(this.context, SelectedItemPosition, alarmIntent, 0) != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(String.valueOf(SelectedItemPosition), 0);
                editor.apply();
            }
        }else{
            Toast.makeText(context, "알람이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    int getAlarmCount(){
        int result = 0;
        for(int i=0; i<5; i++){
            if(sharedPreferences.getLong(String.valueOf(i),0) != 0){
                result++;
            }
        }
        return result;
    }

}
