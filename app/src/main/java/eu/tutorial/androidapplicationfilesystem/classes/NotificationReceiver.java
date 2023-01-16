package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_PREVIOUS = "PREVIOUS";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PLAY = "PLAY";


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("ahujelasjhd");

    }
}
