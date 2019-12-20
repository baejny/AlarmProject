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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.Activity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    Button btn_save;
    Button btn_remove;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        final AlarmMethod AM = new AlarmMethod(this, sharedPreferences);

        picker=(TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        spinner = (Spinner) findViewById(R.id.spinner);
        textView = (TextView)findViewById(R.id.textView);
        btn_save = (Button) findViewById(R.id.button_save);
        btn_remove = (Button) findViewById(R.id.button_remove);

        // 리스트 초기화
        showAlarmList();
        // 스피너 초기화
        makeSpinnerList();


        // 등록
        btn_save.setOnClickListener(new View.OnClickListener() {
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
                AM.alarm_insert(hour_24,minute);

//                // 현재 지정된 시간으로 알람 시간 설정
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
//                calendar.set(Calendar.MINUTE, minute);
//                calendar.set(Calendar.SECOND, 0);
//                calendar.set(Calendar.MILLISECOND, 0);
//
//                //  AlarmManager 등록 및 Preference에 설정한 값 저장
//                alarmCount = getAlarmCount();
//                if (alarmCount < 5) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
//
//                    for (int i = 0; i < 5; i++) {
//                        if (sharedPreferences.getLong(String.valueOf(i), 0) == 0) {
//                            alarmPointer = i;
//                            break;
//                        }
//                    }
//
//                    // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
//                    if (calendar.before(Calendar.getInstance())) {
//                        calendar.add(Calendar.DATE, 1);
//                        //Toast.makeText(getApplicationContext(),"다음날 같은 시간으로 설정합니다!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    Date currentDateTime = calendar.getTime();
//                    String date_text = new SimpleDateFormat("yyyy년MM월dd일 hh시mm분 ", Locale.getDefault()).format(currentDateTime);
//                    Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
//
//                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//                    editor.putLong(String.valueOf(alarmPointer), (long)calendar.getTimeInMillis());
//                    editor.apply();
//                    diaryNotification((long)calendar.getTimeInMillis(), alarmPointer, true);
//
//                }else{
//                    Toast.makeText(getApplicationContext(), "알람이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
//                }
                showAlarmList();
            }
        });

        // 삭제
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AM.alarm_delete(spinner.getSelectedItemPosition());
//                alarmCount = getAlarmCount();
//                if (alarmCount > 0) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
//                    int position = spinner.getSelectedItemPosition();
//                    String selected = String.valueOf(position);
//
//                    diaryNotification(sharedPreferences.getLong(selected,0),position,false );
//                    SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//                    editor.putLong(selected, 0);
//                    editor.apply();
//                }else{
//                    Toast.makeText(getApplicationContext(), "삭제할 알람이 없습니다.", Toast.LENGTH_SHORT).show();
//                }
                showAlarmList();
            }
        });
    }

//    int getAlarmCount(){
//        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
//        int result = 0;
//        for(int i=0; i<5; i++){
//            if(sharedPreferences.getLong(String.valueOf(i),0) != 0){
//                result++;
//            }
//        }
//        return result;
//    }

    void showAlarmList(){
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        textView.setText("");
        for (int i = 0;i < 5; i++) {
            Long timeMillis = sharedPreferences.getLong(String.valueOf(i), 0);
            if (timeMillis != 0) {
                String pattern = "MM월dd일 HH시mm분";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String date = (String)formatter.format(new Timestamp(timeMillis));
                textView.append(i+1 + " : " + date);
            }
            if(i!=4){
                textView.append("\n");
            }
        }
    }

    void makeSpinnerList(){
        String[] facilityList = {"1","2","3","4","5"};
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                facilityList);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

//    void diaryNotification(Long TimeInMillis, int requestCode, boolean CheckNotify)
//    {
//        PackageManager pm = this.getPackageManager();
//        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
//        Intent alarmIntent = new Intent(this, AlarmReceiver.class); // alarmReceiver 주는 인텐트
//        alarmIntent.putExtra("requestCode", requestCode);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        // 사용자가 매일 알람을 허용했다면
//        if (CheckNotify) {
//            if (alarmManager != null) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, TimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TimeInMillis, pendingIntent);
//                }
//            }
//
//            // 부팅 후 실행되는 리시버 사용가능하게 설정
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//
//        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
//    }
}