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

public class MainActivity extends AppCompatActivity implements AlarmListener {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    Button btn_save;
    Button btn_remove;
    Spinner spinner;

    @Override
    public void onList(String msg) {
        ((TextView)findViewById(R.id.textView)).setText(msg);
    }

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

        // 스피너 초기화
        makeSpinnerList();
        // 리스너 초기화
        AM.setListener(this);

        // 등록
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AM.alarm_insert(picker.getCurrentHour(), picker.getCurrentMinute());
            }
        });

        // 삭제
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AM.alarm_delete(spinner.getSelectedItemPosition());
            }
        });
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
}