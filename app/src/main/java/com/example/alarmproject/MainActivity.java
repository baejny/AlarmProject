package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TimePicker picker;
    TextView textView;
    Button btn_save;
    Button btn_remove;
    Spinner spinner;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

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

                showAlarmList();
            }
        });

        // 삭제
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AM.alarm_delete(spinner.getSelectedItemPosition());
                showAlarmList();
            }
        });
    }


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

}