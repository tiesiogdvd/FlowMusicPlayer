package eu.tutorial.androidapplicationfilesystem.classes;

import static eu.tutorial.androidapplicationfilesystem.classes.ServiceNotification.CHANNEL_ID_1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;

public class MediaControlService extends Service{
    MediaPlayer mp = new MediaPlayer();
    String musicPath = "none";
    Boolean mpAvailable = false;

    Integer mpDuration;
    Integer mpLastPosition;



    /*@Override
    public void onCompletion(MediaPlayer mp) {
        finished = true;
        //mp.seekTo(0);
        //broadcast("isFinished",true);
        //mp.pause();
    }*/

    class MyServiceBinder extends Binder{
        public MediaControlService getService(){
            return MediaControlService.this;
        }
    }

    private final IBinder mBinder = new MyServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;  //START_... will change what action is taken when the service is killed  NOT_STICKY means the service will not be restarted
    }

    public void playMedia(String path){
        actionPlay(path);
    }


    public void playMedia(int resid){
        mp.release();
        mp = MediaPlayer.create(this, resid);
        mpAvailable = true;
        mp.start();
    }

    public void playMedia(){
        mp.start();
    }


    public void pauseMedia(){
        mp.pause();
        mpLastPosition = mp.getCurrentPosition();
        System.out.println(mpLastPosition);
    }

    public void mediaSeekTo(int position){
        //finished = false;
        mp.seekTo(position);
    }

    public Boolean isMediaPlaying(){
        if(mp!=null){
            return mp.isPlaying();
        }else{
            return false;
        }
    }

    public Boolean isMediaReady(){
        return mpAvailable;
    }

    public int mediaDuration(){
        if(mpAvailable) {
            System.out.println(mp.getDuration());
            return mp.getDuration();
        }
        return 0;
    }

    public int mediaRemaining(){
        if(mpAvailable) {
            //System.out.println(mp.getCurrentPosition());
        return mp.getCurrentPosition();
        }
        return 0;
    }

    private void actionPlay(String path) {
        //finished = false;
        if (new File(path).exists()) {
            mp.release();
            mp = MediaPlayer.create(this, Uri.parse(path));
            mp.start();
            mpDuration = mp.getDuration();
            mpAvailable = true;
            musicPath = path;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new Notification.Builder(this, CHANNEL_ID_1)
                    .setContentTitle("Song playing")
                    .setContentText(path)
                    .setSmallIcon(R.drawable.ic_action_folder)
                    .setContentIntent(pendingIntent)
                    .build(); //this will start the service
            startForeground(9, notification);//but this is important to keep in running in the foreground
        }
    }

    private void actionNullNotification(){
        Notification notification = new Notification.Builder(this, CHANNEL_ID_1)
                .setContentTitle("Song playing")
                .setSmallIcon(R.drawable.ic_action_folder)
                .build(); //this will start the service
        startForeground(9,notification);
    }


    private void broadcast(String key, String payload){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction("ANSWER");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    private void broadcast(String key, int payload){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction("ANSWER");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    private void broadcast(String key, Boolean payload){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction("ANSWER");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }


    @Override
    public void onCreate() {//Could put the same things as in onStartCommand, but it will only create the notification once
        actionNullNotification();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        mp = null;
        //unregisterReceiver(receiver);
        super.onDestroy();
    }
}
