package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;

public class BluetoothClass extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    public static String SERVICE_STRING = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static UUID UUID_TDCS_SERVICE= UUID.fromString(SERVICE_STRING);

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private final BluetoothGattCallback GattClientCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt _gatt, int _status, int _new_state){
            super.onConnectionStateChange( _gatt, _status, _new_state );
            if(_status == BluetoothGatt.GATT_FAILURE){
                Log.d("test_gatt", "GATT_FAILURE");
                return;
            }else if(_status!=BluetoothGatt.GATT_SUCCESS){
                Log.d("test_gatt", "GATT_SUCCESS");
                return;
            }
            if(_new_state == BluetoothProfile.STATE_CONNECTED){
                Log.d("test_gatt", "STATE_CONNECTED");
                _gatt.discoverServices();
                Intent temp = new Intent(BluetoothClass.this, MainActivity.class);
                temp.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(temp);
                finish();
            }else if(_new_state == BluetoothProfile.STATE_DISCONNECTED){
            }
        }
    };

    private ScanCallback mBLEScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String device = result.getDevice().getName();
            BluetoothDevice dev = result.getDevice();
            Log.d("test", "scanning : "+ device);
            if("HUSTAR_05".equals(device)){
                Log.d("test", "Connecting DEVICE"+device);
                mBluetoothLeScanner.stopScan(mBLEScanCallback);
                dev.connectGatt(BluetoothClass.this, true, GattClientCallback);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("test","scan failed errCode = "+errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
    }

    private void startScan(){
        // 위와 같은 서비스를 제공하는 장치만 스캔하도록 scanFilter 설정
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filterBuilder = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_TDCS_SERVICE)).build();
        filters.add(filterBuilder);
        // 저전력 모드로 스캔하도록 설정
        ScanSettings settingBuilder = new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_POWER).build();
        mBluetoothLeScanner.startScan(filters, settingBuilder, mBLEScanCallback);
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content2);
        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();

        // Initializes Bluetooth adapter.
        // BLE manager 설정, BLE manager를 이용하여 BLE adapter 설정
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /* Scan 조건 체크 */
        // REQUEST BLE ENABLE
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // REQUEST FINE LOCATION PERMISSION
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1003);
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        startScan();
    }
}