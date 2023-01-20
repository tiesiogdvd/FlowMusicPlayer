package eu.tutorial.androidapplicationfilesystem.classes;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ServiceNotification extends Application {

    public static final String CHANNEL_ID_1 = "mediaPlayerChannel1";
    public static final String CHANNEL_ID_2 = "mediaPlayerChannel2";
    public static final String ACTION_PREVIOUS = "PREVIOUS";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PLAY = "PLAY";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel(){

        NotificationChannel serviceChannel1 = new NotificationChannel(CHANNEL_ID_1, "MP Channel 1", NotificationManager.IMPORTANCE_HIGH);
        serviceChannel1.setDescription("MP Channel 1 description");
        serviceChannel1.setSound(null,null);
        serviceChannel1.setVibrationPattern(null);
        serviceChannel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationChannel serviceChannel2 = new NotificationChannel(CHANNEL_ID_2, "MP Channel 2", NotificationManager.IMPORTANCE_DEFAULT);
        serviceChannel2.setDescription("MP Channel 2 description");
        serviceChannel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        serviceChannel2.setSound(null,null);
        serviceChannel2.setVibrationPattern(null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel1);
        manager.createNotificationChannel(serviceChannel2);
    }
}
