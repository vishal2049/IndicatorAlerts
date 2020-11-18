package com.TheTechBeing.indicatorAlerts;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import java.util.ArrayList;

public class notificationChannel extends Application {

        public static final String CHANNEL_FOREGROUND_ID="channel_one";
        public static final String CHANNEL_NOTIFICATION_ID="channel_two";

        @Override
        public void onCreate() {
            super.onCreate();

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)//app sdk ver & device ver
            {
                NotificationChannel channel1 = new NotificationChannel(CHANNEL_FOREGROUND_ID,"Foreground channel", NotificationManager.IMPORTANCE_LOW);
                channel1.setDescription("standard channel");

                NotificationChannel channel2 = new NotificationChannel(CHANNEL_NOTIFICATION_ID,"Alerts channel", NotificationManager.IMPORTANCE_HIGH);
                channel2.setDescription("Alerts channel");

                ArrayList<NotificationChannel> channels=new ArrayList();
                channels.add(channel1);
                channels.add(channel2);

                NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannels(channels);
            }
        }
    }
