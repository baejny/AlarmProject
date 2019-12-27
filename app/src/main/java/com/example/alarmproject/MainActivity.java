package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.sql.Timestamp;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity implements AlarmListener {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    FButton btn_save;
    FButton btn_remove;
    FButton btn_removeAll;
    Spinner spinner;
    Spinner TimeSpinner;

    SharedPreferences sharedPreferences;
    AlarmMethod am;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onList(String msg) {
        ((TextView)findViewById(R.id.textView)).setText(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        am = new AlarmMethod(this, sharedPreferences);

        picker=(TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        spinner = (Spinner) findViewById(R.id.spinner);
        TimeSpinner = (Spinner) findViewById(R.id.spinner2);
        textView = (TextView)findViewById(R.id.textView);
        btn_save = (FButton) findViewById(R.id.button_save);
        btn_save.setButtonColor(getColor(R.color.fbutton_color_peter_river));
        btn_remove = (FButton) findViewById(R.id.button_remove);
        btn_remove.setButtonColor(getColor(R.color.fbutton_color_orange));
        btn_removeAll = (FButton) findViewById(R.id.button_removeAll);
        btn_removeAll.setButtonColor(getColor(R.color.fbutton_color_pomegranate));

        // 스피너 초기화
        makeSpinnerList();
        makeSpinnerTimeList();
        // 리스너 초기화
        am.setListener(this);

        // 등록
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                am.alarm_insert(picker.getCurrentHour(), picker.getCurrentMinute(), TimeSpinner.getSelectedItem().toString());
            }
        });

        // 삭제
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                am.alarm_delete(spinner.getSelectedItemPosition());
            }
        });

        // 전체 초기화
        btn_removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                am.alarm_deleteAll();
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

    void makeSpinnerTimeList(){
        String[] facilityList = {"은하","시완"};
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                facilityList);
        TimeSpinner.setAdapter(adapter);
        TimeSpinner.setSelection(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.textView)).setText(am.make_list());
    }
}