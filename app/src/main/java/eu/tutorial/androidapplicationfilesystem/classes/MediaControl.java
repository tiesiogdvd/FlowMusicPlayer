//This class is like glue for the other classes related to controlling media playback
/*
It creates an instance of MediaPlayer
Adds control to MediaPlayer instance with the ability to change user interface
Sets the OnSeekBarChangeListener for the seekbar
Implements the MetadataGetterSetter class to set the title, artist and bitmap image in the UI
 */

package eu.tutorial.androidapplicationfilesystem.classes;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class MediaControl extends AppCompatActivity {

    NeumorphImageButton btnPlay, btnPrevious, btnNext;
    final Handler handler;
    SeekBar seekBar;
    TextView totalText,remainingText;
    Context context;
    Runnable runnable;

    BroadcastReceiver receiver;
    MediaControlService mediaControlService;
    boolean isServiceBound;
    ServiceConnection serviceConnection;
    Intent serviceIntent;


    public MediaControl(Context context) {
        this.isServiceBound = false;
        this.handler =  new Handler();
        this.seekBar = null;
        this.btnPrevious = ((MainActivity)context).findViewById(R.id.lastButton);
        this.btnNext = ((MainActivity)context).findViewById(R.id.nextButton);
        this.btnPlay = ((MainActivity)context).findViewById(R.id.playButton);
        this.seekBar = ((MainActivity)context).findViewById(R.id.musicProgress);
        this.totalText = ((MainActivity)context).findViewById(R.id.musicTotalText);
        this.remainingText = ((MainActivity)context).findViewById(R.id.musicRemainingText);

        this.context = context;
        serviceIntent = new Intent(context, MediaControlService.class);
        //startService();
        bindService();
        setListeners();
    }

    public void setRunnable(){//This runnable moves seekbar at a selected interval
        setOnSeekbarListener(); //Actives seekbar listener method
        if (mediaControlService.isMediaReady()){ //Only runs the code if it detects that the service is playing audio
            System.out.println(mediaControlService.mediaRemaining());
            totalText.setText(formatDuration(mediaControlService.mediaLastDuration()));
            remainingText.setText(formatDuration(mediaControlService.mediaLastPosition()));
            seekBar.setMax(mediaControlService.mediaLastDuration());
            seekBar.setProgress(mediaControlService.mediaLastPosition());
            if(mediaControlService.isMediaPlaying()) {

                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mediaControlService.mediaRemaining()); //Requests the service position of MediaPlayer every 0.5s
                        System.out.println("Runnable instance " + handler.toString());
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(runnable, 0);
            }
        }
    }

    public void startService(){

        //startService(serviceIntent);
    }

    public void play(String path) {
        try{
            if(path.equals(null)){
                throw new Exception();
            }else{
                setMetadata(path);
                mediaControlService.playMedia(path);
                btnPlay.setImageResource(R.drawable.ic_action_pause);
                setRunnable();
                //isFinished();
            }
        }catch(Exception e){
            Toast.makeText(context, "Path is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void setMetadata(String path){
        MetadataGetterSetter metadataGetterSetter = new MetadataGetterSetter();
        //MetadataGetterSetter class is called to receive metadata with the help of MusicData class and then put it into activity_main Views
        metadataGetterSetter.retrieveMetadata(path, context);
        //For it to work it is required the audio file path and MainActivity context
    }


    /*public void initService(){
        System.out.println("bleeed");
        serviceIntent = new Intent(context, MediaControlService.class);
        serviceIntent.setAction("create");
        ContextCompat.startForegroundService(context, serviceIntent);
    }*/


    public void bindService(){

        serviceIntent.setAction("create");
        ContextCompat.startForegroundService(context, serviceIntent);

        if(serviceConnection==null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MediaControlService.MyServiceBinder myServiceBinder = (MediaControlService.MyServiceBinder)iBinder;
                    mediaControlService = myServiceBinder.getService();
                    isServiceBound = true;

                    //bindService method is not instant
                    //For that reason code for runnable is activated only after the service is binded
                    //Otherwise methods might be called before the bind and cause crashes

                    if(!mediaControlService.musicPath.equals("none")){ //checks if there was music played before
                        String path = mediaControlService.musicPath; //important on activity changes like rotation
                        setMetadata(path); //sets metadata to last played song
                        if(mediaControlService.isMediaPlaying()){
                            btnPlay.setImageResource(R.drawable.ic_action_pause);
                            setRunnable();
                        }else{


                        }
                    }


                }
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isServiceBound=false;
                    handler.removeCallbacksAndMessages(null);
                }
            };
        }
        context.bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
    }


    public void unbindService(){
        if(isServiceBound && serviceConnection!=null){
            context.unbindService(serviceConnection);
            isServiceBound=false;
        }
    }



    public void play(){
        mediaControlService.playMedia();
        setRunnable();
        btnPlay.setImageResource(R.drawable.ic_action_pause);

    }

    public void pause(){
        mediaControlService.pauseMedia();
        handler.removeCallbacks(runnable);
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }

    public void stopService(){
        Intent serviceIntent = new Intent(context, MediaControlService.class);
        stopService(serviceIntent);
    }



    public void play(int resid) { //Play music from app resource
        handler.postDelayed(runnable, 0);
        mediaControlService.playMedia(resid);
        btnPlay.setImageResource(R.drawable.ic_action_pause);
    }

    public boolean isPlaying(){
        return mediaControlService.isMediaPlaying();
    }


    @SuppressLint("DefaultLocale")
    private String convertTimeFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        (TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    private String formatDuration(int duration) {
        int minutes = (int) TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        int seconds = (int) (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES));

        return String.format("%02d:%02d", minutes, seconds);
    }


    public void isFinished(){
        unregisterReceiver(receiver);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean isFinished = intent.getExtras().getBoolean("isFinished");
                System.out.println(isFinished);
            }
        };
        IntentFilter filt = new IntentFilter("ANSWER");
        context.registerReceiver(receiver,filt);
    }

     /*  if(BroadcastReceiverMediaPlayer.isFinished(context)){
        mediaControlService.pauseMedia();
        System.out.println("IS FINISHED");
        mediaControlService.playMedia();
    }*/

    public void unsetOnSeekbarListener(){
        if(seekBar!=null) {
            seekBar.setOnSeekBarChangeListener(null);
        }
    }

    public void handlerRemoveCallbacks(){
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void setOnSeekbarListener(){
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b){ //If from user
                    mediaControlService.mediaSeekTo(progress);
                }
                if(isServiceBound){
                    remainingText.setText(convertTimeFormat(mediaControlService.mediaRemaining()));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });
    }

    private void setListeners(){
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //savePlaylistData();
                if(mediaControlService!=null){
                    if(!mediaControlService.isMediaPlaying()) {
                        play();
                    } else {
                        pause();
                    }
                } else{
                    Toast.makeText(context, "No music resource loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isServiceBound) {
                    System.out.println(mediaControlService.isMediaPlaying());
                    mediaControlService.playMedia();
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }
}
