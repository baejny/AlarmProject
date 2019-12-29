package com.example.alarmproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class DatabaseActivity extends AppCompatActivity implements AlarmListener {

    FButton btn_save_list;
    FButton btn_open_list;
    FButton btn_del_list;
    Spinner list_spinner;
    EditText editText;

    SharedPreferences sharedPreferences;
    AlarmMethod am;
    int alarmListCount;

    private DatabaseReference mDatabase;

    @Override
    public void onList(String msg) {
        ((TextView) findViewById(R.id.textView_alarmList_database)).setText(msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        list_spinner = (Spinner) findViewById(R.id.open_spinner);
        editText = (EditText) findViewById(R.id.editText);
        btn_save_list = (FButton) findViewById(R.id.btn_save_list);
        btn_save_list.setButtonColor(getColor(R.color.custom1));
        btn_open_list = (FButton) findViewById(R.id.btn_open_list);
        btn_open_list.setButtonColor(getColor(R.color.custom1));
        btn_del_list = (FButton) findViewById(R.id.btn_delete_list);
        btn_del_list.setButtonColor(getColor(R.color.custom1));


        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        // AlarmMethod 초기화
        am = new AlarmMethod(this, sharedPreferences);
        // 리스너 초기화
        am.setListener(this);

        open_namelist();
        getListCount();

        // 리스트 저장
        btn_save_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String getEdit = editText.getText().toString();
                getEdit = getEdit.trim();
                if(getEdit.getBytes().length>0){
                    am.save_list(editText.getText().toString(),alarmListCount);
                    Toast.makeText(getApplicationContext(),"DB에 저장되었습니다!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"리스트 제목을 적어주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 리스트 열기
        btn_open_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (list_spinner.getSelectedItem() != null) {
                    am.open_list(list_spinner.getSelectedItem().toString());
                    Toast.makeText(getApplicationContext(),"DB에서 가져옵니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "가져올 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 리스트 삭제
        btn_del_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(list_spinner.getSelectedItem()!=null){
                    am.remove_list(list_spinner.getSelectedItem().toString(),alarmListCount);
                    Toast.makeText(getApplicationContext(),"DB에서 삭제합니다!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"삭제할 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.textView_alarmList_database)).setText(am.make_list());
    }

    void makeSpinnerNameList (List< String > list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                list);

        list_spinner.setAdapter(adapter);
        list_spinner.setSelection(0);
    }

    void open_namelist () {
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
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });
    }

    void getListCount(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                int result = 0;
                while (child.hasNext()) {
                    result++;
                    child.next().getKey();
                }
                //ListCount(result);
                alarmListCount = result;
                Log.d("Listcount2", String.valueOf(alarmListCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });
    }
}
