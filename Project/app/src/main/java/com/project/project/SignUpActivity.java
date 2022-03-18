package com.project.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Users Updated.");
                d = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value.");
            }
        });
    }

    public void signUpPress (View v){
        EditText u = (EditText) findViewById(R.id.username);
        EditText p = (EditText) findViewById(R.id.password);
        EditText n = (EditText) findViewById(R.id.fullname);
        if(TextUtils.isEmpty(u.getText()) | TextUtils.isEmpty(p.getText()) | TextUtils.isEmpty(n.getText())){
            Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
        }
        else{
            String username = u.getText().toString();
            String password = p.getText().toString();
            String name = n.getText().toString();
            u.setText("");
            p.setText("");
            n.setText("");
            signup(username, password, name);
        }
    }

    public void signup(String username, String password, String name) {
        String type = "client";
        String avatar = "1";
        if (d.child("user").child(username).child("password").getValue(String.class) == null){
            ref.child("user").child(username).child("name").setValue(name);
            ref.child("user").child(username).child("password").setValue(password);
            ref.child("user").child(username).child("avatar").setValue(avatar);
            ref.child("user").child(username).child("type").setValue(type);
            Toast.makeText(this, "User successfully created!", Toast.LENGTH_SHORT).show();
            finish();
            //Intent i = new Intent(this, MainActivity.class);
            //startActivity(i);
        }
        else{
            Toast.makeText(this, "Username is already taken!", Toast.LENGTH_SHORT).show();
        }
    }

    public void backtoLog(View v){
        finish();
    }
}