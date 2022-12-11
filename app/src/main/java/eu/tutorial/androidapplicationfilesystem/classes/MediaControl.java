//This class is like glue for the other classes related to controlling media playback
/*
It creates an instance of MediaPlayer
Adds control to MediaPlayer instance with the ability to change user interface
Sets the OnSeekBarChangeListener for the seekbar
Implements the MetadataGetterSetter class to set the title, artist and bitmap image in the UI
TODO: implement saving feature
 */

package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.util.concurrent.TimeUnit;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class MediaControl {
    Handler handler;
    MediaPlayer mp;
    NeumorphImageButton btnPlay;
    SeekBar seekBar;
    TextView totalText,remainingText;
    Context context;
    Runnable runnable;

    public MediaControl(Context context) {
        this.btnPlay = ((MainActivity)context).findViewById(R.id.playButton);
        this.seekBar = ((MainActivity)context).findViewById(R.id.musicProgress);
        this.totalText = ((MainActivity)context).findViewById(R.id.musicTotalText);
        this.remainingText = ((MainActivity)context).findViewById(R.id.musicRemainingText);
        this.handler = new Handler();
        this.mp = new MediaPlayer();
        this.context = context;

        //This runnable moves seekbar at a selected interval
        this.runnable = new Runnable() {
            @Override
            public void run() {
                //Sets progress of seekBar;
                seekBar.setProgress(mp.getCurrentPosition());
                //Handler post delay 0.5s
                handler.postDelayed(this,100);
            }
        };
        setListeners();
    }

    public void play(String path) {//Play music from local storage
        try{
            if(path.equals(null)){
                throw new Exception();
            }else{
                handler.removeCallbacks(runnable);
                MetadataGetterSetter metadataGetterSetter = new MetadataGetterSetter(); //MetadataGetterSetter class is called to receive metadata with the help of MusicData class and then put it into activity_main Views
                metadataGetterSetter.retrieveMetadata(path, context); //For it to work it is required the audio file path and MainActivity context
                mp.release(); //Releases the previous audio file to not create multiple instances
                mp = MediaPlayer.create(context, Uri.parse(path));
                mp.setLooping(false);
                mp.start();
                btnPlay.setImageResource(R.drawable.ic_action_pause);
                String mpDuration = convertTimeFormat(mp.getDuration());
                totalText.setText(mpDuration);
                seekBar.setMax(mp.getDuration());
                handler.postDelayed(runnable, 0);
                btnPlay.setBackgroundResource(R.drawable.ic_action_pause);
            }
        }catch(Exception e){
            Toast.makeText(context, "Path is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void play(int resid) { //Play music from app resource
        handler.postDelayed(runnable, 0);
        mp.release();
        mp = MediaPlayer.create(context, resid);
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_pause);
    }
    public void pause(){
        mp.pause();
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }
    public void unpause(){
        handler.postDelayed(runnable, 0);
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }
    public Boolean isPlaying(){
         return mp.isPlaying();
    }
    public void start(){
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_pause);
    }

    private String convertTimeFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    private void setListeners(){
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b){ //If from user
                    mp.seekTo(progress);
                }
                remainingText.setText(convertTimeFormat(mp.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        this.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnPlay.setImageResource(R.drawable.ic_action_play);
                mp.seekTo(0);
            }
        });
    }






}
