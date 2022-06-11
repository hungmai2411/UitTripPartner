package com.uittrippartner;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;


public class MyApplication extends Application {
    public static final String CHANNEL_ID = "24112002";


    @Override
    public void onCreate() {
        super.onCreate();

        createChannelID();
    }


    private void createChannelID() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Push Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
