package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import java.util.Random;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText editTextID;
    Button btn;

    String id, code;
    Random rnd;
    StringBuffer buf = new StringBuffer();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        editTextID = (EditText) findViewById(R.id.editTextID);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                long seed = System.currentTimeMillis();
                rnd = new Random(seed);
                rnd.setSeed(System.currentTimeMillis());

                for(int i=0;i<5;i++){
                    if(rnd.nextBoolean()){
                        buf.append((char)((long)(rnd.nextInt(26))+65));
                    }else{
                        buf.append((rnd.nextInt(10)));
                    }
                }

                Log.d("test",String.valueOf(buf));
                code = String.valueOf(buf);
                ((TextView)findViewById(R.id.codeView)).setText(code);

                id = editTextID.getText().toString();

                databaseReference.child(code).child("ID").setValue(id);
            }
        });
    }

}