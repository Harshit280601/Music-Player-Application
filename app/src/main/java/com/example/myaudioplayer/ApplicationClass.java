package com.example.myaudioplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1= "channel1";
    public static final String CHANNEL_ID_2= "channel2";
    public static final String ACTION_PREVIOUS= "actionprevious";
    public static final String ACTION_NEXT= "actionnext";
    public static final String ACTION_PLAY= "actionplay";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O);  // checking for android version greater than oreo(.O)
        {
            NotificationChannel channel1=
                    new NotificationChannel(CHANNEL_ID_1
                            ,"Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc... ");

            NotificationChannel channel2=
                    new NotificationChannel(CHANNEL_ID_2
                            ,"Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc... ");

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }

}
