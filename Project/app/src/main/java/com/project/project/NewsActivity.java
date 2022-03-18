package com.project.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;
    int newsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences shared = getApplication().getSharedPreferences("notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = shared.edit();
        String notif = shared.getString("notif", "null");
        if(notif.equals("true"))
            startService(new Intent(this, NotificationService.class));
        else
            stopService(new Intent(this, NotificationService.class));

        NewsActivity.compute c = new NewsActivity.compute();
        c.start();

        Spinner s = (Spinner) findViewById(R.id.categorysearch);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profileSettings) {
            Intent i = new Intent("android.intent.action.profile");
            startActivity(i);
        }
        else if(id == R.id.Logout){
            SharedPreferences shared = this.getSharedPreferences("username", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = shared.edit();
            e.putString("username", "null");
            e.commit();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class compute extends Thread{
        public void run(){
            SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
            String username = shared.getString("username", "null");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("Database Updated.");
                    d = dataSnapshot;
                    newsCount = (int) d.child("news").getChildrenCount();

                    FloatingActionButton fab = findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent("android.intent.action.addnews");
                            startActivity(i);
                        }
                    });

                    if(d.child("user").child(username).child("type").getValue(String.class).equals("admin"))
                        fab.show();
                    else
                        fab.hide();

                    reload();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    System.out.println("Failed to read value.");
                }
            });
        }
    }

    public void reload(){
        Spinner s = (Spinner) findViewById(R.id.categorysearch);
        LinearLayout allNews = (LinearLayout) findViewById(R.id.allnews);
        allNews.removeAllViews();
        String selected = s.getSelectedItem().toString();

        for(int i = newsCount - 1; i >= 0; i--){
            if(d.child("news").child("" + i).child("username").getValue().toString() == null ||
                    d.child("news").child("" + i).child("type").getValue().toString() == null ||
                    d.child("news").child("" + i).child("category").getValue().toString() == null ||
                    d.child("news").child("" + i).child("content").getValue().toString() == null ||
                    d.child("news").child("" + i).child("title").getValue().toString() == null) {
                break;
            }
            else{
                TextView admin = new TextView(NewsActivity.this);
                TextView title = new TextView(NewsActivity.this);
                TextView content = new TextView(NewsActivity.this);

                admin.setTextSize(15);
                title.setTextSize(30);
                content.setTextSize(20);

                String user = "" + d.child("news").child("" + i).child("username").getValue().toString();
                admin.setText("\n " + d.child("user").child(user).child("name").getValue().toString() + " posted to " +
                        d.child("news").child("" + i).child("type").getValue().toString() + " / " +
                        d.child("news").child("" + i).child("category").getValue().toString());

                title.setText(" " + d.child("news").child("" + i).child("title").getValue().toString());;

                String c = "" + d.child("news").child("" + i).child("content").getValue().toString();
                String con;
                if (c.length() > 100)
                    con = c.substring(0, 100) + "...";
                else
                    con = c;

                content.setText(" " + con);

                Button b = new Button(NewsActivity.this);
                b.setText("Read more");
                b.setId(i);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = v.getId();
                        SharedPreferences shared = getApplication().getSharedPreferences("index", Context.MODE_PRIVATE);
                        SharedPreferences.Editor e = shared.edit();
                        e.putString("index", index + "");
                        e.commit();
                        Intent intent = new Intent("android.intent.action.comments");
                        startActivity(intent);
                    }
                });

                if(selected.equals("All")){
                    allNews.addView(admin);
                    allNews.addView(title);
                    allNews.addView(content);
                    allNews.addView(b);
                }
                else{
                    if(d.child("news").child("" + i).child("category").getValue().toString().equals(selected)) {
                        allNews.addView(admin);
                        allNews.addView(title);
                        allNews.addView(content);
                        allNews.addView(b);
                    }
                }
            }


        }
    }
    @Override
    protected void onStop() {
        SharedPreferences shared = getApplication().getSharedPreferences("notif", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = shared.edit();
        String notif = shared.getString("notif", "null");
        if(notif.equals("true")) {
            Intent r = new Intent(getApplicationContext(), NotificationService.class);
            r.setPackage(getPackageName());
            startService(r);
        }

        super.onStop();
    }
}