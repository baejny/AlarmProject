package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int alarmCount;
    int alarmPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimePicker picker=(TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        final SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        alarmCount = sharedPreferences.getInt("alarmCount", 0);
        Log.d("test", String.valueOf(alarmCount));

        final TextView textView = (TextView)findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.button);
        Button DeleteBtn= (Button) findViewById(R.id.button2);
        Button listBtn = (Button) findViewById(R.id.button3);

        // 등록
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int hour_24, minute;

                if (Build.VERSION.SDK_INT >= 23) {
                    hour_24 = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour_24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }

                // 현재 지정된 시간으로 알람 시간 설정
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                //  AlarmManager 등록 및 Preference에 설정한 값 저장
                if (alarmCount < 5) {
                    for (int i = 0; i < 5; i++) {
                        if (sharedPreferences.getLong(String.valueOf(i), 0) == 0) {
                            alarmPointer = i;
                            break;
                        }
                    }

                    // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DATE, 1);
                    }

                    Date currentDateTime = calendar.getTime();
                    String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EEE a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                    Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                    editor.putLong(String.valueOf(alarmPointer), (long) calendar.getTimeInMillis());
                    editor.putInt("alarmCount", alarmCount);
                    editor.apply();
                    alarmCount++;
                    diaryNotification(calendar, alarmPointer);

                }else{
                    Toast.makeText(getApplicationContext(), "알람이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 삭제
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(alarmCount > 0){
                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();

                    int i;
                    for (i = 0;i < 5; i++) {
                        Log.d("int i", String.valueOf(i));
                        Log.d("SP", String.valueOf(sharedPreferences.getLong(String.valueOf(i),0)));
                        Log.d("SSPP", String.valueOf((long)calendar.getTimeInMillis()));
                        if (sharedPreferences.getLong(String.valueOf(i), 0) == (long)calendar.getTimeInMillis()) {
                            editor.remove(String.valueOf(i));
                            editor.apply();

                            alarmCount--;
                            cancelAlarm(calendar, i);

                            Date currentDateTime = calendar.getTime();
                            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EEE a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                            Toast.makeText(getApplicationContext(), date_text + "의 알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "삭제할 알람이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 리스트 뷰
        listBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                // 분은 잘 나옴. 시간이 다름
                String hour_t, min_t;
                long dayint = 24*60*60*1000;
                long hourint = 60*60*1000;
                long minuteint = 60*1000;
                long day, hours, mins, save;

                textView.setText("저장된 시간"+"\n");
                for (int i = 0;i < 5; i++) {
                    if (sharedPreferences.getLong(String.valueOf(i), 0) != 0) {
                        save = sharedPreferences.getLong(String.valueOf(i), 0);
                        day = save/dayint; save %=dayint;
                        hours = save/hourint; save %=hourint;
                        hour_t = String.valueOf(hours);
                        textView.append("\n"+hour_t);
                        mins = save/minuteint; save %=minuteint;
                        min_t = String.valueOf(mins);
                        textView.append("시 "+min_t+"분");
                    }
                }
            }
        });
    }
    void cancelAlarm(Calendar calendar, int requestCode)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        //ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
    }

    void diaryNotification(Calendar calendar, int requestCode)
    {
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("requestCode", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }
}