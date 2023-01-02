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
import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class MediaControl extends AppCompatActivity {

    NeumorphImageButton btnPlay;
    Context context;
    MediaControlService mediaControlService;
    boolean isServiceBound;
    ServiceConnection serviceConnection;
    Intent serviceIntent;
    Intent intentBroadcast;
    int lastPosition;
    String lastSong;


    public MediaControl(Context context) {
        this.lastSong = null;
        this.isServiceBound = false;
        this.btnPlay = ((MainActivity)context).findViewById(R.id.playButtonBar);
        this.context = context;
        serviceIntent = new Intent(context, MediaControlService.class);
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
                        MetadataGetterSetter.setBarMetadata(context,path);
                        if(mediaControlService.isMediaPlaying()){
                            btnPlay.setImageResource(R.drawable.ic_action_pause);
                        }else{
                            //StyleSetter.setInitBackground(context);
                        }
                    }else{
                        if(lastSong!="" && lastPosition != -1 && lastSong != null){
                            mediaControlService.playMedia(lastSong);
                            mediaControlService.mediaSeekTo(lastPosition);
                            mediaControlService.pauseMedia();
                            btnPlay.setImageResource(R.drawable.ic_action_play);
                            MetadataGetterSetter.setBarMetadata(context,lastSong);
                        }
                    }
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isServiceBound=false;
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

    public void play(int resid) { //Play music from app resource
        mediaControlService.playMedia(resid);
        btnPlay.setImageResource(R.drawable.ic_action_pause);
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
        return mediaControlService.musicPath;
    }

    public int getRemaining(){
        return mediaControlService.mediaRemaining();
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
}
