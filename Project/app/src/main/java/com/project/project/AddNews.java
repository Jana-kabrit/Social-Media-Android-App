package com.project.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddNews extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;
    int postCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        DatePicker date = (DatePicker) findViewById(R.id.eventDate);
        TimePicker time = (TimePicker) findViewById(R.id.eventTime);
        LinearLayout l = (LinearLayout) findViewById(R.id.durationlayout);
        l.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        time.setVisibility(View.GONE);

        RadioGroup r = (RadioGroup)findViewById(R.id.radiogroup);
        r.check(R.id.radio_news);
        RadioButton radioNews = (RadioButton) findViewById(R.id.radio_news);
        RadioButton radioEvent = (RadioButton) findViewById(R.id.radio_event);
        radioNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker date = (DatePicker) findViewById(R.id.eventDate);
                TimePicker time = (TimePicker) findViewById(R.id.eventTime);
                LinearLayout l = (LinearLayout) findViewById(R.id.durationlayout);
                l.setVisibility(View.GONE);
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
            }
        });
        radioEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker date = (DatePicker) findViewById(R.id.eventDate);
                TimePicker time = (TimePicker) findViewById(R.id.eventTime);
                LinearLayout l = (LinearLayout) findViewById(R.id.durationlayout);
                l.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Database Updated.");
                d = dataSnapshot;
                postCount = (int) d.child("news").getChildrenCount();

                SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
                String username = shared.getString("username", "null");
                ImageView i = (ImageView)findViewById(R.id.avataranews);
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

    public void send(View v){
        EditText t1 = (EditText) findViewById(R.id.postTitle);
        EditText t2 = (EditText) findViewById(R.id.postText);
        Spinner s = (Spinner) findViewById(R.id.categories);
        RadioGroup r = (RadioGroup)findViewById(R.id.radiogroup);
        EditText t3 = (EditText) findViewById(R.id.eventDuration);
        DatePicker date = (DatePicker) findViewById(R.id.eventDate);
        TimePicker time = (TimePicker) findViewById(R.id.eventTime);
        int id = r.getCheckedRadioButtonId();
        RadioButton b = (RadioButton) findViewById(id);
        String type = b.getText().toString();
        System.out.println(type);
        if(type.equals("News")){
            if(TextUtils.isEmpty(t1.getText()) | TextUtils.isEmpty(t2.getText())){
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
            }
            else{
                String title = t1.getText().toString();
                String content = t2.getText().toString();
                String category = s.getSelectedItem().toString();
                String sDate = "0";
                String sDuration = "0";
                addpost(title, content, category, type, sDate, sDuration);
            }
        }
        else{
            if(TextUtils.isEmpty(t1.getText()) | TextUtils.isEmpty(t2.getText()) | TextUtils.isEmpty(t3.getText())){
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
            }
            else{
                String title = t1.getText().toString();
                String content = t2.getText().toString();
                String category = s.getSelectedItem().toString();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
                cal.set(Calendar.MONTH, date.getMonth());
                cal.set(Calendar.YEAR, date.getYear());
                cal.set(Calendar.HOUR, time.getHour());
                cal.set(Calendar.MINUTE, time.getMinute());
                String sDate = "" + cal.getTimeInMillis();

                String sDuration = t3.getText().toString();
                addpost(title, content, category, type, sDate, sDuration);
            }

        }

    }

    public void addpost(String title, String content, String category, String type, String date, String duration) {
        SharedPreferences shared = this.getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = shared.getString("username", "null");
        ref.child("news").child("" + postCount).child("username").setValue(username);
        ref.child("news").child("" + postCount).child("title").setValue(title);
        ref.child("news").child("" + postCount).child("content").setValue(content);
        ref.child("news").child("" + postCount).child("category").setValue(category);
        ref.child("news").child("" + postCount).child("type").setValue(type);
        ref.child("news").child("" + postCount).child("date").setValue(date);
        ref.child("news").child("" + postCount).child("duration").setValue(duration);
        Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show();
        //finish();
    }
}