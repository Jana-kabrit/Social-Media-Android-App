package com.project.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Comments extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DataSnapshot d;
    int commentCount;
    String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        compute c = new compute();
        c.start();
    }

    class compute extends Thread{
        public void run(){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("Comments Updated.");
                    d = dataSnapshot;

                    SharedPreferences shared = getApplication().getSharedPreferences("index", Context.MODE_PRIVATE);
                    String id = shared.getString("index", "null");
                    index = id;

                    String user = "" + d.child("news").child("" + id).child("username").getValue().toString();

                    ImageView iv = (ImageView) findViewById(R.id.avatarcomment);
                    String avatarid = d.child("user").child(user).child("avatar").getValue(String.class);
                    if(avatarid.equals("1"))
                        iv.setImageResource(R.drawable.avatar2);
                    else if(avatarid.equals("2"))
                        iv.setImageResource(R.drawable.avatar3);
                    else if(avatarid.equals("3"))
                        iv.setImageResource(R.drawable.avatar4);
                    else if(avatarid.equals("4"))
                        iv.setImageResource(R.drawable.avatar5);
                    else if(avatarid.equals("5"))
                        iv.setImageResource(R.drawable.avatar6);
                    else if(avatarid.equals("6"))
                        iv.setImageResource(R.drawable.avatar7);

                    TextView admin = (TextView) findViewById(R.id.postedby);
                    admin.setText("\n " + d.child("user").child(user).child("name").getValue().toString() + " posted to " +
                            d.child("news").child("" + id).child("type").getValue().toString() + " / " +
                            d.child("news").child("" + id).child("category").getValue().toString());

                    TextView title = (TextView) findViewById(R.id.maintitle);
                    title.setText("" + d.child("news").child("" + id).child("title").getValue().toString());

                    TextView content = (TextView) findViewById(R.id.maincontent);
                    content.setText(" " + d.child("news").child("" + id).child("content").getValue().toString() + "\n");

                    Button cal = (Button) findViewById(R.id.addtocal);
                    if(d.child("news").child("" + id).child("type").getValue().toString().equals("News"))
                        cal.setVisibility(View.GONE);
                    else
                        cal.setVisibility(View.VISIBLE);

                    LinearLayout com = (LinearLayout) findViewById(R.id.commentsLayout);
                    com.removeAllViews();
                    commentCount = (int) d.child("news").child("" + id).child("comments").getChildrenCount();
                    for(int i = 0; i < commentCount; i++){
                        if(d.child("news").child("" + id).child("comments").child("" + i).child("username").getValue(String.class) != null &&
                                d.child("news").child("" + id).child("comments").child("" + i).child("content").getValue(String.class) != null) {
                            TextView t1 = new TextView(Comments.this);
                            t1.setTextSize(15);
                            String commenter = "" + d.child("news").child("" + id).child("comments").child("" + i).child("username").getValue().toString();
                            t1.setText("    " + d.child("user").child(commenter).child("name").getValue().toString() + " commented: ");

                            TextView t2 = new TextView(Comments.this);
                            t2.setTextSize(20);
                            t2.setText("    " + d.child("news").child("" + id).child("comments").child("" + i).child("content").getValue().toString() + "\n");

                            com.addView(t1);
                            com.addView(t2);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    System.out.println("Failed to read value.");
                }
            });
        }
    }

    public void comment (View v){
        SharedPreferences shared = getApplication().getSharedPreferences("username", Context.MODE_PRIVATE);
        String username = shared.getString("username", "null");
        EditText e = (EditText) findViewById(R.id.comment);
        String comment = e.getText().toString();
        ref.child("news").child(index).child("comments").child(commentCount + "").child("username").setValue(username);
        ref.child("news").child(index).child("comments").child(commentCount + "").child("content").setValue(comment);
        e.setText("");
    }

    public void addtocal(View v){
        Intent i = new Intent();
        i.setType("vnd.android.cursor.item/event");
        long duration = Long.parseLong(d.child("news").child("" + index).child("duration").getValue().toString() + "");
        long startTime = Long.parseLong(d.child("news").child("" + index).child("date").getValue().toString() + "");
        String t = d.child("news").child("" + index).child("title").getValue().toString() + "";

        i.putExtra("beginTime", startTime);
        i.putExtra("endTime", startTime + duration * 60 * 60 * 1000);
        i.putExtra("title", t);

        i.setAction(Intent.ACTION_EDIT);
        startActivity(i);
    }
}