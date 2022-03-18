package com.project.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    int newscount = 0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot d = dataSnapshot;

                int updatednewscount = (int) d.child("news").getChildrenCount();
                if(updatednewscount > newscount){
                    newscount = updatednewscount;
                    int index = updatednewscount - 1;

                    if(d.child("news").child("" + index).child("title").getValue(String.class) != null){
                        String title = "New Update: " + d.child("news").child("" + index).child("title").getValue(String.class);

                        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        PendingIntent intents = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id)
                                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                                .setContentTitle("FoxHub")
                                .setContentText(title)
                                .setAutoCancel(true)
                                .setContentIntent(intents);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;

                        int importance = NotificationManager.IMPORTANCE_HIGH ;
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID) ;
                        assert mNotificationManager != null;
                        mNotificationManager.createNotificationChannel(notificationChannel) ;
                        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Failed to read value.");
            }
        });

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}