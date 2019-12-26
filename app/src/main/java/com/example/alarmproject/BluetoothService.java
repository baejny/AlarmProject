package com.example.alarmproject;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;

public class BluetoothService extends Service {

    private static final int REQUEST_ENABLE_BT = 1;
    public static String SERVICE_STRING = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static UUID UUID_TDCS_SERVICE= UUID.fromString(SERVICE_STRING);

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;

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
                Intent temp = new Intent(BluetoothService.this, MainActivity.class);
                temp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                temp.putExtra("bluetoothFind", 1);
                startActivity(temp);
            }else if(_new_state == BluetoothProfile.STATE_DISCONNECTED){
            }
        }
    };

    private ScanCallback mBLEScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String device = result.getDevice().getName();
            BluetoothDevice dev = result.getDevice();
            Log.d("test", "Scanning...: "+ device);
            if("HUSTAR_05".equals(device)){
                Log.d("test", "Connecting DEVICE"+device);
                mBluetoothLeScanner.stopScan(mBLEScanCallback);
                dev.connectGatt(BluetoothService.this, true, GattClientCallback); // 자동연결
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("test","onScanFailed errorCode = "+errorCode);
        }
    };

    private void startScan(){
        // 위와 같은 서비스를 제공하는 장치만 스캔하도록 scanFilter 설정
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filterBuilder = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_TDCS_SERVICE)).build();
        filters.add(filterBuilder);
        // 저전력 모드로 스캔하도록 설정
        ScanSettings settingBuilder = new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_POWER).build();
        mBluetoothLeScanner.startScan(filters, settingBuilder, mBLEScanCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initializes Bluetooth adapter.
        // BLE manager 설정, BLE manager를 이용하여 BLE adapter 설정
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        startScan();
    }
}
