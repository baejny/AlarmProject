package com.example.alarmproject;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import info.hoang8f.widget.FButton;

public class DeleteActivity extends AppCompatActivity implements AlarmListener {

    int alarmCount;
    int alarmPointer;

    TextView textView;
    FButton btn_delete;
    FButton btn_deleteAll;
    Spinner spinner;

    SharedPreferences sharedPreferences;
    AlarmMethod am;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onList(String msg) {
        ((TextView)findViewById(R.id.textView_alarmList)).setText(msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        textView = (TextView)findViewById(R.id.textView_alarmList);
        spinner = (Spinner) findViewById(R.id.spinner_delete);
        btn_delete = (FButton) findViewById(R.id.button_delete);
        btn_delete.setButtonColor(getColor(R.color.fbutton_color_orange));
        btn_deleteAll = (FButton) findViewById(R.id.button_deleteAll);
        btn_deleteAll.setButtonColor(getColor(R.color.fbutton_color_pomegranate));

        // 스피너 초기화
        makeSpinnerList();
        // 리스너 초기화
        am.setListener(this);
        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        // AlarmMethod 초기화
        am = new AlarmMethod(this, sharedPreferences);

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
        ((TextView)findViewById(R.id.textView_alarmList)).setText(am.make_list());
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
