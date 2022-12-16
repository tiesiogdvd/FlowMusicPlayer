package eu.tutorial.androidapplicationfilesystem.classes.replaced;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


//Partly successful attempt to create communication with the MediaControlService, but it
//was replaced by binding methods in the MediaControlService service and MediaControl class.

//Limit of these methods is that the return method might get called before the broadcast has been received.

//How these methods work is they send broadcasts with action ASK which are received in MediaControlService.
//These broadcasts are captured and depending on their payload the MediaControlService
//creates broadcasts with action ANSWER providing the data requested.

//It works, but needs implementations to wait for the receiver output,
//which makes it not optimal and prone to bugs and missed requests.

public class BroadcastReceiverMediaPlayer extends Activity {

    static String path;
    static Boolean request;
    static int mpDuration;
    static int mpRemaining;
    static BroadcastReceiver receiver;
    static Boolean isFinished;

    public static void Playing(Context context) {
        unregisterReceiver(context); //Receiver gets unregistered to prevent memory leaks
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                request = intent.getExtras().getBoolean("isPlaying");
                System.out.println(request + "in isServicePlaying");
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");  //Filters the broadcast messages to see only ones with action ANSWER

        context.registerReceiver(receiver,filt); //Receiver needs to be registered before it can work.

        broadcast("check","isPlaying", context); //Broadcast created after initiating the receiver with the request
        //In this case it asks the MediaControlService if the MediaPlayer is playing and a reply is expected in the receiver.
    }

    public static Boolean isServicePlaying(Context context){
        Playing(context);
        return request;
    }

    public static String servicePath(Context context){
        unregisterReceiver(context);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                path = intent.getExtras().getString("path");
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");
        context.registerReceiver(receiver,filt);
        broadcast("check","path", context);
        return path;
    }



    public static int serviceDuration(Context context){
        unregisterReceiver(context);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mpDuration = intent.getExtras().getInt("mpDuration");
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");
        context.registerReceiver(receiver,filt);
        broadcast("check","mpDuration", context);
        return mpDuration;
    }



    public static void Remaining(Context context){
        unregisterReceiver(context);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mpRemaining = intent.getExtras().getInt("mpRemaining");
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");
        context.registerReceiver(receiver,filt);
        broadcast("check","mpRemaining", context);
    }

    public static int serviceRemaining(Context context){
        Remaining(context);
        return mpRemaining;
    }

    public static Boolean isFinished(Context context){
        unregisterReceiver(context);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isFinished = intent.getExtras().getBoolean("isFinished");
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");
        context.registerReceiver(receiver,filt);
        return isFinished;
    }


    private static void broadcast(String key, String payload, Context context){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction("ASK");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    public static void unregisterReceiver(Context context){

        try{
        if(receiver != null){
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        }catch(Exception e){}
    }


}
