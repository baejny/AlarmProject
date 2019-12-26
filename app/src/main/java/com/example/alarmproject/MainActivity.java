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

public class MainActivity extends AppCompatActivity implements AlarmListener {
    int alarmCount;
    int alarmPointer;

    TimePicker picker;
    TextView textView;
    Button btn_save;
    Button btn_remove;
    Button btn_removeAll;
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

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /* Scan 조건 체크 */
        // REQUEST BLE ENABLE
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        // REQUEST FINE LOCATION PERMISSION
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1003);
        }
        Intent intent = getIntent();
        if(intent.getIntExtra("bluetoothFind", 0) != 1){
            Intent bluetoothIntent = new Intent(this, BluetoothService.class);
            startService(bluetoothIntent);
            //finish();
        }
        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        am = new AlarmMethod(this, sharedPreferences);

        picker=(TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        spinner = (Spinner) findViewById(R.id.spinner);
        TimeSpinner = (Spinner) findViewById(R.id.spinner2);
        textView = (TextView)findViewById(R.id.textView);
        btn_save = (Button) findViewById(R.id.button_save);
        btn_remove = (Button) findViewById(R.id.button_remove);
        btn_removeAll = (Button) findViewById(R.id.button_removeAll);

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