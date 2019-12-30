package com.example.alarmproject;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.hoang8f.widget.FButton;

public class DeleteActivity extends AppCompatActivity implements AlarmListener {

    TextView textView;
    Spinner spinner;
    FButton btn_delete;
    FButton btn_deleteAll;
    FButton btn_move_database;


    SharedPreferences sharedPreferences;
    AlarmMethod am;

    @Override
    public void onList(String msg) {
        ((TextView) findViewById(R.id.textView_alarmList_delete)).setText(msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        textView = (TextView) findViewById(R.id.textView_alarmList_delete);
        spinner = (Spinner) findViewById(R.id.spinner_delete);
        btn_delete = (FButton) findViewById(R.id.button_delete);
        btn_delete.setButtonColor(getColor(R.color.custom1));
        btn_deleteAll = (FButton) findViewById(R.id.button_deleteAll);
        btn_deleteAll.setButtonColor(getColor(R.color.custom1));
        btn_move_database = (FButton) findViewById(R.id.button_database);
        btn_move_database.setButtonColor(getColor(R.color.custom1));

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        // AlarmMethod 초기화
        am = new AlarmMethod(this, sharedPreferences);
        // 스피너 초기화
        makeSpinnerList();
        // 리스너 초기화
        am.setListener(this);

        // 삭제
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (am.getAlarmCount() > 0) {
                    Toast.makeText(getApplicationContext(), String.valueOf(spinner.getSelectedItemPosition() + 1) + "번째 알람이 삭제 되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "삭제할 알람이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                am.alarm_delete(spinner.getSelectedItemPosition());
            }
        });

        // 전체 삭제
        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (am.getAlarmCount() > 0) {
                    Toast.makeText(getApplicationContext(), "전체 알람이 삭제 되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "삭제할 알람이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                am.alarm_deleteAll();
            }
        });

        //데이터베이스 액티비티로 이동
        btn_move_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DatabaseActivity.class);
                ;
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.textView_alarmList_delete)).setText(am.make_list());
    }

    void makeSpinnerList() {
        String[] facilityList = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                facilityList);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }
}