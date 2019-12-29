package com.example.alarmproject;

import android.bluetooth.BluetoothAdapter;
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
    FButton btn_delete;
    FButton btn_deleteAll;
    Spinner spinner;

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
        btn_delete.setButtonColor(getColor(R.color.fbutton_color_orange));
        btn_deleteAll = (FButton) findViewById(R.id.button_deleteAll);
        btn_deleteAll.setButtonColor(getColor(R.color.fbutton_color_pomegranate));

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
                am.alarm_delete(spinner.getSelectedItemPosition());
            }
        });

        // 전체 삭제
        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                am.alarm_deleteAll();
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
