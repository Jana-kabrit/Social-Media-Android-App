package com.project.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText u = (EditText) findViewById(R.id.username);
        EditText p = (EditText) findViewById(R.id.password);
        u.setText("");
        p.setText("");
        SharedPreferences shared = getApplication().getSharedPreferences("notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = shared.edit();
        String notif = shared.getString("notif", "null");
        if(notif.equals("true"))
            startService(new Intent(this, NotificationService.class));
        else
            stopService(new Intent(this, NotificationService.class));
        /*
        SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = shared.getString("username", "null");
        if(username != "null"){
            Intent i = new Intent("android.intent.action.news");
            startActivity(i);
        }
         */

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

    public void loginPress(View v){
        EditText u = (EditText) findViewById(R.id.username);
        EditText p = (EditText) findViewById(R.id.password);
        if(TextUtils.isEmpty(u.getText()) | TextUtils.isEmpty(p.getText())){
            Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
        }
        else{
            String username = u.getText().toString();
            String password = p.getText().toString();
            login(username, password);
        }
    }

    public void login(String username, String password) {
        if (d.child("user").child(username).child("password").getValue(String.class) == null){
            Toast.makeText(this, "User does not exist!", Toast.LENGTH_SHORT).show();
        }
        else if(d.child("user").child(username).child("password").getValue(String.class).equals(password)){
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            SharedPreferences shared = this.getSharedPreferences("username", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = shared.edit();
            e.putString("username", username);
            e.commit();
            EditText u = (EditText) findViewById(R.id.username);
            EditText p = (EditText) findViewById(R.id.password);
            u.setText("");
            p.setText("");
            Intent i = new Intent("android.intent.action.news");
            startActivity(i);
        }
        else{
            Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUpAct (View v){
        Intent i = new Intent("android.intent.action.signup");
        startActivity(i);
    }

    @Override
    protected void onStop() {
        SharedPreferences shared = getApplication().getSharedPreferences("notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = shared.edit();
        String notif = shared.getString("notif", "null");
        if(notif.equals("true")){
            Intent restartServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
            restartServiceIntent.setPackage(getPackageName());
            startService(restartServiceIntent);
        }

        super.onStop();
    }
}
