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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmMethod{
    Context context;
    SharedPreferences sharedPreferences;
    AlarmManager alarmManager;

    int alarmCount;
    int alarmPointer;

    private AlarmListener mListener;
    AlarmMethod(Context context, SharedPreferences sharedPreferences){
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PackageManager pm = this.context.getPackageManager();
        ComponentName receiver = new ComponentName(this.context, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    //리스너 부착
    public void setListener(AlarmListener listener){
        mListener = listener;
        mListener.onList(make_list());
    }

    //알람 등록
    void alarm_insert(int hour, int minute, String mediaNum){
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

            if (calendar.before(Calendar.getInstance()))  {
                calendar.add(Calendar.DATE, 1);
                Toast.makeText(context,"다음날 같은 시간으로 설정합니다!", Toast.LENGTH_SHORT).show();
            }

            Date currentDateTime = calendar.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context, date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(String.valueOf(alarmPointer), (long)calendar.getTimeInMillis());
            editor.putString(String.valueOf(alarmPointer+10), mediaNum);
            editor.apply();

            Long millis = calendar.getTimeInMillis();
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("alarmPointer", alarmPointer);
            alarmIntent.putExtra("mediaSelect", mediaNum);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmPointer, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY, pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                }
            }
        }else{
            Toast.makeText(context, "알람이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //알란 변경 (알람 실행 후 내일 같은 시간으로 설정)
    void alarm_change(int alarmPointer){
        Calendar nextNotifyTime = Calendar.getInstance();
        nextNotifyTime.setTimeInMillis(System.currentTimeMillis());
        nextNotifyTime.set(Calendar.SECOND, 0);
        nextNotifyTime.set(Calendar.MILLISECOND, 0);

        //하루 증가
        nextNotifyTime.add(Calendar.DATE, 1);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String mediaNum = sharedPreferences.getString(String.valueOf(alarmPointer+10), null);
        Long Millis = nextNotifyTime.getTimeInMillis();
        editor.putLong(String.valueOf(alarmPointer), Millis);
        editor.putString(String.valueOf(alarmPointer+10), mediaNum);
        editor.apply();

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("alarmPointer", alarmPointer);
        alarmIntent.putExtra("mediaSelect", mediaNum);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmPointer, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Millis, AlarmManager.INTERVAL_DAY, pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Millis, pendingIntent);
            }
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //알람 삭제
    void alarm_delete(int SelectedItemPosition){
        alarmCount = getAlarmCount();
        if(alarmCount > 0){
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("alarmPointer", SelectedItemPosition);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SelectedItemPosition, alarmIntent, 0);
            Log.d("Delete Spinner number", String.valueOf(SelectedItemPosition));
            if (PendingIntent.getBroadcast(context, SelectedItemPosition, alarmIntent, 0) != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(String.valueOf(SelectedItemPosition), 0);
                editor.putString(String.valueOf(SelectedItemPosition+10), null);
                editor.apply();
            }
        }else{
            Toast.makeText(context, "저장된 알람이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //알람 삭제
    void alarm_deleteAll(){
        alarmCount = getAlarmCount();
        if(alarmCount > 0){
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            for(int i=0; i<5; i++){
                alarmIntent.putExtra("alarmPointer", i);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, 0);
                if (PendingIntent.getBroadcast(context, i, alarmIntent, 0) != null && alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(String.valueOf(i), 0);
                    editor.putString(String.valueOf(i+10), null);
                    editor.apply();
                }
            }
        }else{
            Toast.makeText(context, "저장된 알람이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //디바이스 부팅시 알람 초기화
    void alarm_boot(){
        int count = 0;
        for (int i = 0; i < 5; i++) {
            Long millis = sharedPreferences.getLong(String.valueOf(i), 0);
            if (millis != 0) {
                Log.d("test", "boot test = " + i);
                count++;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(sharedPreferences.getLong(String.valueOf(i),0));
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.putExtra("alarmPointer", i);
                alarmIntent.putExtra("mediaSelect", sharedPreferences.getString(String.valueOf(i+10), null));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (alarmManager != null) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY, pendingIntent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                    }
                }
            }
        }
        Toast.makeText(context, "[재부팅] " + String.valueOf(count) +"개의 알람이 있습니다.", Toast.LENGTH_SHORT).show();
    }

    //sharedPreferences에 값 변동시 Main의 List 재설정
    String make_list(){
        String msg = "";
        for (int i = 0;i < 5; i++) {
            Long timeMillis = sharedPreferences.getLong(String.valueOf(i), 0);
            if (timeMillis != 0) {
                String pattern = "MM월dd일 HH시mm분";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String date = (String)formatter.format(new Timestamp(timeMillis));
                msg += (i+1 + " : " + date);
            }
            if(i!=4){
                msg += ("\n");
            }
        }
        return msg;
    }

    //sharedPreferences에 저장된 알람 갯수 계산
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
