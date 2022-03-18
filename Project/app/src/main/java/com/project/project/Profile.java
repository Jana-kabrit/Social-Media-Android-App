package com.project.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Profile extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Users Updated.");
                d = dataSnapshot;
                SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
                String username = shared.getString("username", "null");
                TextView n = (TextView) findViewById(R.id.profilename);
                TextView u = (TextView) findViewById(R.id.profileusername);
                String name = d.child("user").child(username).child("name").getValue(String.class);
                n.setText("Name: " + name);
                u.setText("Username: " + username);

                TextView t1 = (TextView) findViewById(R.id.secretCode);
                TextView t2 = (TextView) findViewById(R.id.codeinfo);
                ImageButton ib = (ImageButton) findViewById(R.id.codebutton);
                LinearLayout l = (LinearLayout) findViewById(R.id.codelayout);
                if(d.child("user").child(username).child("type").getValue().toString().equals("client")){
                    l.setVisibility(View.VISIBLE);
                    ib.setVisibility(View.GONE);
                    t1.setText("Enter a Secret Code");
                    t2.setText("Contact an admin for a code to become one!");

                }
                else{
                    l.setVisibility(View.GONE);
                    ib.setVisibility(View.VISIBLE);
                    t1.setText("Secret Code: " + d.child("code").getValue(String.class));
                    t2.setText("* You are advised to change the code after sharing");
                }

                ImageView i = (ImageView)findViewById(R.id.proavatar);
                String avatarid = d.child("user").child(username).child("avatar").getValue(String.class);
                if(avatarid.equals("1"))
                    i.setImageResource(R.drawable.avatar2);
                else if(avatarid.equals("2"))
                    i.setImageResource(R.drawable.avatar3);
                else if(avatarid.equals("3"))
                    i.setImageResource(R.drawable.avatar4);
                else if(avatarid.equals("4"))
                    i.setImageResource(R.drawable.avatar5);
                else if(avatarid.equals("5"))
                    i.setImageResource(R.drawable.avatar6);
                else if(avatarid.equals("6"))
                    i.setImageResource(R.drawable.avatar7);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value.");
            }
        });
    }
    public void changePass (View view){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        changepassfrag fragment = new changepassfrag();
        transaction.replace(R.id.changepasslayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
    public void changeName(View view){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        changenamefrag fragment = new changenamefrag();
        transaction.replace(R.id.changenamelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void changeAvatar(View view){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        changeavatarfrag fragment = new changeavatarfrag();
        transaction.replace(R.id.changeavatarlayout, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
    public void notificationSettings (View view){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        notifsetfrag fragment = new notifsetfrag();
        transaction.replace(R.id.notifsetlayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void generate (View v){
        int min = 1000, max = 9999;
        Random random = new Random();
        int randomNumber = random.nextInt(max + 1 - min) + min;
        ref.child("code").setValue(randomNumber + "");
        TextView t = (TextView) findViewById(R.id.secretCode);
        t.setText("Secret Code: " + d.child("code").getValue(String.class));
    }

    public void becomeAdmin (View v){
        SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = shared.getString("username", "null");
        EditText e = (EditText) findViewById(R.id.editcode);
        if(TextUtils.isEmpty(e.getText().toString()))
            Toast.makeText(this, "Please enter a code!", Toast.LENGTH_SHORT).show();
        else if(d.child("code").getValue(String.class).equals(e.getText().toString())){
            ref.child("user").child(username).child("type").setValue("admin");
            Toast.makeText(this, "You're an admin now!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Wrong code!", Toast.LENGTH_SHORT).show();
    }

}