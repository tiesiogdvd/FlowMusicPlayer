//This class is like glue for the other classes related to controlling media playback
/*
It creates an instance of MediaPlayer
Adds control to MediaPlayer instance with the ability to change user interface
Sets the OnSeekBarChangeListener for the seekbar
Implements the MetadataGetterSetter class to set the title, artist and bitmap image in the UI
 */

package eu.tutorial.androidapplicationfilesystem.classes;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import soup.neumorphism.NeumorphImageButton;

public class MediaControl extends AppCompatActivity{

    ShapeableImageView btnPlay;
    Context context;
    MediaControlService mediaControlService;
    boolean isServiceBound;
    ServiceConnection serviceConnection;
    Intent serviceIntent;
    Intent intentBroadcast;
    int lastPosition;
    String lastSong;
    PassMusicStatus passToActivity;

    Playlist musicSource;
    ArrayList <String> sourcePaths;
    Integer musicIndex;


    public MediaControl(Context context) {
        this.lastSong = null;
        this.isServiceBound = false;
        this.btnPlay = ((MainActivity)context).findViewById(R.id.playButtonBar);
        this.context = context;
        serviceIntent = new Intent(context, MediaControlService.class);
        passToActivity = (PassMusicStatus) context;

        musicSource = null;
        musicIndex = null;

        bindService();
        setListeners();
    }



    public void play(String path) {
        try{
            if(path.equals(null)){
                throw new Exception();
            }else{
                mediaControlService.playMedia(path);
                btnPlay.setImageResource(R.drawable.ic_action_pause);
                //isFinished();
            }
        }catch(Exception e){
            Toast.makeText(context, "Path is null", Toast.LENGTH_SHORT).show();
        }
    }


    public void play(String path, Playlist playlist, Integer index) {
        try{
            if(path.equals(null)){
                throw new Exception();
            }else{
                mediaControlService.playMedia(path, playlist, index);
                btnPlay.setImageResource(R.drawable.ic_action_pause);
                //isFinished();
            }
        }catch(Exception e){
            Toast.makeText(context, "Path is nulll", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSource(Playlist playlist, Integer index){
        musicSource = playlist;
        musicIndex = index;
        if(isServiceBound){
            mediaControlService.setSources(playlist,index);
        }
    }


    public void bindService() {
            serviceIntent.setAction("create");
            ContextCompat.startForegroundService(context, serviceIntent);

            if (serviceConnection == null) {
                serviceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder iBinder) {
                        MediaControlService.MyServiceBinder myServiceBinder = (MediaControlService.MyServiceBinder) iBinder;
                        mediaControlService = myServiceBinder.getService();
                        isServiceBound = true;
                        //bindService method is not instant
                        //For that reason code for runnable is activated only after the service is binded
                        //Otherwise methods might be called before the bind and cause crashes

                        if (mediaControlService.musicPath!=null && !mediaControlService.musicPath.equals("none")) { //checks if there was music played before
                            String path = mediaControlService.musicPath; //important on activity changes like rotation
                            MetadataGetterSetter.setBarMetadata(context, path);
                            passToActivity.onMediaReady(true);
                            System.out.println("ready");
                            if (mediaControlService.isMediaPlaying()) {
                                btnPlay.setImageResource(R.drawable.ic_action_pause);
                            } else {
                                //StyleSetter.setInitBackground(context);
                            }
                        } else {
                            if (!Objects.equals(lastSong, "") && lastPosition != -1 && lastSong != null && new File(lastSong).exists()) {

                                if(Settings.getLastSongIndex()!=-1 && Settings.getLastSongSource()!=null){
                                    mediaControlService.sourcePlaylist  = musicSource;
                                    mediaControlService.sourceIndex = musicIndex;
                                    mediaControlService.playMedia(lastSong,musicSource,musicIndex);

                                }else{
                                    System.out.println("hahahahahahahahahahahahaha");
                                    mediaControlService.sourcePaths = sourcePaths;
                                    if(sourcePaths!=null && sourcePaths.size()!=0) {
                                        for (int i = 0; i < sourcePaths.size(); i++) {
                                            if (sourcePaths.get(i).equals(lastSong)) {
                                                musicIndex = i;
                                                System.out.println(musicIndex);
                                            }
                                        }
                                    }
                                    mediaControlService.sourceIndex=musicIndex;
                                    mediaControlService.playMedia(lastSong);
                                    System.out.println("huhuhuhuuh");
                                }


                                mediaControlService.mediaSeekTo(lastPosition);
                                mediaControlService.pauseMedia();


                                btnPlay.setImageResource(R.drawable.ic_action_play);
                                MetadataGetterSetter.setBarMetadata(context, lastSong);
                                passToActivity.onMediaReady(true);
                                System.out.println("ready");
                            }
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        isServiceBound = false;
                    }
                };
            }
            context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    public void unbindService(){
        if(isServiceBound && serviceConnection!=null){
            context.unbindService(serviceConnection);
            isServiceBound=false;
        }
    }

    public Boolean isServiceBound(){
        return isServiceBound;
    }



    private void broadcast(String key, String payload){
        intentBroadcast = new Intent();
        intentBroadcast.putExtra(key, payload);
        intentBroadcast.setAction("ASK");
        intentBroadcast.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intentBroadcast);
    }

    public void play(){
        mediaControlService.playMedia();
        btnPlay.setImageResource(R.drawable.ic_action_pause);

    }

    public void pause(){
        mediaControlService.pauseMedia();
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }

    public void stopService(){
        Intent serviceIntent = new Intent(context, MediaControlService.class);
        stopService(serviceIntent);
    }


    public boolean isPlaying(){
        return mediaControlService.isMediaPlaying();
    }

    public void handlerRemoveCallbacks(){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public String getPath(){
        if(mediaControlService!=null) {
            return mediaControlService.musicPath;
        }else{
            return "";
        }
    }

    public int getRemaining(){
        if(mediaControlService!=null) {
            return mediaControlService.mediaRemaining();
        }else{
            return -1;
        }
    }

    public void seekTo(int position){
        mediaControlService.mediaSeekTo(position);
    }

    private void setListeners() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //savePlaylistData();
                if (mediaControlService != null) {
                    if (!mediaControlService.isMediaPlaying()) {
                        play();
                    } else {
                        pause();
                    }
                } else {
                    Toast.makeText(context, "No music resource loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setLastSong(String lastSongPath) {
        this.lastSong = lastSongPath;
    }

    public void setLastPosition(int lastSongPosition) {
        this.lastPosition = lastSongPosition;
    }

    public void setPaths(ArrayList<String> paths) {
        this.sourcePaths = paths;
    }
}
