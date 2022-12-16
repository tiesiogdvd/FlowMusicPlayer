package eu.tutorial.androidapplicationfilesystem.classes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ServiceNotification extends Application {
    public static final String CHANNEL_ID_1 = "mediaPlayerChannel";
    public static final String CHANNEL_ID_2 = "mediaPlayerChannel";
    public static final String ACTION_PREVIOUS = "actionPrevious";
    public static final String ACTION_NEXT = "actionNext";
    public static final String ACTION_PLAY = "actionPlay";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel(){


        NotificationChannel serviceChannel1 = new NotificationChannel(CHANNEL_ID_1, "Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel serviceChannel2 = new NotificationChannel(CHANNEL_ID_2, "Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel1);
        manager.createNotificationChannel(serviceChannel2);
    }
}
