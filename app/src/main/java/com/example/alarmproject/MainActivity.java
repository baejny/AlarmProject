package com.example.alarmproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity implements AlarmListener {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    EditText editText;
    Button btn_save;
    Button btn_remove;
    Button btn_removeAll;
    Button btn_save_list;
    Button btn_open_list;
    Spinner spinner;
    Spinner list_spinner;
    Spinner TimeSpinner;

    SharedPreferences sharedPreferences;
    AlarmMethod am;

    @Override
    public void onList(String msg) {
        ((TextView)findViewById(R.id.textView)).setText(msg);
    }

    private DatabaseReference mDatabase;

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
        list_spinner = (Spinner) findViewById(R.id.open_spinner);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        btn_save = (Button) findViewById(R.id.button_save);
        btn_remove = (Button) findViewById(R.id.button_remove);
        btn_removeAll = (Button) findViewById(R.id.button_removeAll);
        btn_save_list = (Button) findViewById(R.id.btn_save_list);
        btn_open_list = (Button) findViewById(R.id.btn_open_list);

        // 스피너 초기화
        makeSpinnerList();
        makeSpinnerTimeList();
        open_namelist();

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

        // 리스트 저장
        btn_save_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                am.save_list(editText.getText().toString());
            }
        });

        // 리스트 열기
        btn_open_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                am.open_list(list_spinner.getSelectedItem().toString());
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

    void makeSpinnerNameList(List<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                list);

        list_spinner.setAdapter(adapter);
        list_spinner.setSelection(0);
    }

    void open_namelist() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> nameList = new ArrayList<>();
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                while (child.hasNext()) {
                    nameList.add(child.next().getKey());
                }
                makeSpinnerNameList(nameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.textView)).setText(am.make_list());
    }
}