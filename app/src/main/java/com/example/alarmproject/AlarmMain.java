package com.example.alarmproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class AlarmMain extends AppCompatActivity {

    private EditText editTextID;
    private EditText editTextCode;
    Button login_btn, reg_btn, chk_btn;

    String id, code; // editText 입력된 값
    String dbid;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextCode = (EditText) findViewById(R.id.editTextCode);
        login_btn = (Button) findViewById(R.id.button);
        reg_btn = (Button) findViewById(R.id.registerbtn);
        chk_btn = (Button) findViewById(R.id.buttonchk);

        chk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                code = editTextCode.getText().toString();

                getFirebasecode();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                id = editTextID.getText().toString();

                if(dbid == id){
                    // MainActivity로 이동
                    Intent temp = new Intent(AlarmMain.this, MainActivity.class);
                    temp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AlarmMain.this.startActivity(temp);
                    Toast.makeText(AlarmMain.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AlarmMain.this, "아이디나 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent temp = new Intent(AlarmMain.this, Register.class);
                temp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AlarmMain.this.startActivity(temp);
            }
        });
    }

    public void getFirebasecode(){
        databaseReference.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    AlarmMain tmp = postSnapshot.getValue(AlarmMain.class);
                    dbid = tmp.id;
                    Log.d("Firebase",dbid);
                }

//                Log.d("test id",dbid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AlarmMain.this, "No number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
