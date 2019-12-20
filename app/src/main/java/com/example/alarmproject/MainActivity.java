package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements AlarmListener {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    Button btn_save;
    Button btn_remove;
    Spinner spinner;

    // firebase 에 데이터를 읽고 쓰기 위해서는 DatabaseReference를 사용해야 함. 파이어 베이스와 연결
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    
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