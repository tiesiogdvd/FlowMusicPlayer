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
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.masoudss.lib.SeekBarOnProgressChanged;
import com.masoudss.lib.WaveformSeekBar;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentMusicPlayer;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import linc.com.amplituda.Amplituda;
import linc.com.amplituda.AmplitudaProgressListener;
import linc.com.amplituda.Cache;
import linc.com.amplituda.Compress;
import linc.com.amplituda.ProgressOperation;
import linc.com.amplituda.exceptions.io.AmplitudaIOException;


public class MediaControlFragment extends AppCompatActivity {

    boolean receiversRegistered;

    ImageButton btnPlay;

    MetadataGetterSetter metadataGetterSetter;
    ImageButton btnPrevious;
    ImageButton btnNext;
    //NeumorphImageButton btnBottomBar;
    final Handler handler;
    SeekBar seekBar;
    WaveformSeekBar playerVisualizer;
    TextView totalText,remainingText;

    Context context;
    FragmentMusicPlayer fragmentMusicPlayer;

    Runnable runnable;

    BroadcastReceiver receiverFinished;
    BroadcastReceiver receiverChange;
    BroadcastReceiver receiverPlayState;

    MediaControlService mediaControlService;
    boolean isServiceBound;
    ServiceConnection serviceConnection;
    Intent serviceIntent;

    PassMusicStatus passToActivity;

    Playlist source;
    Integer index;

    ViewModelMain viewModelMain;

    Amplituda amplituda;
    AmplitudaProgressListener amplitudaProgressListener;

    public MediaControlFragment(FragmentMusicPlayer fragmentMusicPlayer) {

        this.isServiceBound = false;
        this.handler =  new Handler();
        this.seekBar = null;


        this.context = fragmentMusicPlayer.getActivity();
        this.fragmentMusicPlayer = fragmentMusicPlayer;
        amplituda = new Amplituda(context);
        this.btnPrevious = fragmentMusicPlayer.requireView().findViewById(R.id.lastButton);
        this.btnNext = fragmentMusicPlayer.requireView().findViewById(R.id.nextButton);
        this.btnPlay =  fragmentMusicPlayer.requireView().findViewById(R.id.playButton);
        this.seekBar = fragmentMusicPlayer.requireView().findViewById(R.id.musicProgress);
        this.totalText =  fragmentMusicPlayer.requireView().findViewById(R.id.musicTotalText);
        this.remainingText = fragmentMusicPlayer.requireView().findViewById(R.id.musicRemainingText);
        this.playerVisualizer = fragmentMusicPlayer.requireView().findViewById(R.id.visualizer);
        serviceIntent = new Intent(context, MediaControlService.class);
        metadataGetterSetter = new MetadataGetterSetter();
        bindService();
        setListeners();

        amplitudaProgressListener = new AmplitudaProgressListener() {
            @Override
            public void onProgress(ProgressOperation operation, int progress) {
            }
            @Override
            public void onStopProgress() {
                super.onStopProgress();
                runOnUiThread(() -> {
                    playerVisualizer.animate().setDuration(2000).setStartDelay(1000).translationY(0).alpha(1.0f).setInterpolator(new AccelerateDecelerateInterpolator());
                    playerVisualizer.setVisibility(View.VISIBLE);
                    // todo: update your ui / view in activity
                });
            }
        };

    }

    public void setViewModel(ViewModelMain viewModelMain){
        this.viewModelMain = viewModelMain;
    }

    public void setRunnable(){//This runnable moves seekbar at a selected interval

        setOnSeekbarListener(); //Actives seekbar listener method
        if (mediaControlService.isMediaReady()){ //Only runs the code if it detects that the service is playing audio
            System.out.println(mediaControlService.mediaRemaining());
            totalText.setText(TypeConverter.formatDuration(mediaControlService.mediaDuration()));
            remainingText.setText(TypeConverter.formatDuration(mediaControlService.mediaRemaining()));
            seekBar.setMax(mediaControlService.mediaDuration());
            seekBar.setProgress(mediaControlService.mediaRemaining());

            if(mediaControlService.isMediaPlaying()) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playerVisualizer.setProgress(mediaControlService.mediaRemaining());
                        seekBar.setProgress(mediaControlService.mediaRemaining()); //Requests the service position of MediaPlayer every 0.5s
                        System.out.println("Runnable instance " + handler.toString());
                        handler.postDelayed(this, 20);
                    }
                };
                handler.postDelayed(runnable, 0);
            }

            playerVisualizer.setOnProgressChanged(new SeekBarOnProgressChanged() {
                @Override
                public void onProgressChanged(@NonNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                    if(b){
                        mediaControlService.mediaSeekTo((int) v);
                    }
                }
            });

        }
    }

    public int[] convertListToArray(List<Integer> list) {
        return list.stream().mapToInt(i->i).toArray();
    }

    public void setWaveFormSeekbar(){
        playerVisualizer.animate().setDuration(1000).setStartDelay(1000).translationY(200).alpha(0.0f).setInterpolator(new AccelerateDecelerateInterpolator());
        //playerVisualizer.setVisibility(View.INVISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            // todo: background tasks
            amplituda.processAudio(mediaControlService.musicPath,Compress.withParams(Compress.AVERAGE, 1), Cache.withParams(Cache.REUSE),amplitudaProgressListener).get(result -> {
                List<Integer> amplitudesData = result.amplitudesAsList();
                int[] intsArray =  convertListToArray(amplitudesData);
                playerVisualizer.setSampleFrom(intsArray);
                runOnUiThread(() -> {
                    playerVisualizer.setMaxProgress(mediaControlService.mediaDuration());
                    playerVisualizer.setVisibleProgress((float)mediaControlService.mediaDuration()/3);
                    playerVisualizer.clearAnimation();
                    // todo: update your ui / view in activity
                });
            }, exception -> {
                if(exception instanceof AmplitudaIOException) {
                    System.out.println("IO Exception!");
                }
            });
        });

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
            Toast.makeText(context, "Path is nulll", Toast.LENGTH_SHORT).show();
        }
    }

    public void play(String path, Playlist playlist, Integer index) {
        path = playlist.getSong(index).getPath();
        System.out.println(playlist.getSong(index).getPath());
        try{
            if(path.equals(null)){
                throw new Exception();
            }else{
                setMetadata(path);
                mediaControlService.playMedia(path, playlist, index);
                btnPlay.setImageResource(R.drawable.ic_action_pause);
                setRunnable();
            }
        }catch(Exception e){
            Toast.makeText(context, "Path is nulll", Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(){
        return mediaControlService.musicPath;
    }

    public void setMetadata(String path){
        //MetadataGetterSetter class is called to receive metadata with the help of MusicData class and then put it into activity_main Views
        metadataGetterSetter.retrieveMetadata(path, context);
        //For it to work it is required the audio file path and MainActivity context
    }


    public void bindService(){

        //receiverBroadcast();
        serviceIntent.setAction("create");
        ContextCompat.startForegroundService(context, serviceIntent);

        if(serviceConnection==null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MediaControlService.MyServiceBinder myServiceBinder = (MediaControlService.MyServiceBinder)iBinder;
                    mediaControlService = myServiceBinder.getService();
                    isServiceBound = true;
                    //receiverBroadcast();
                    //Sets a receiver to listen for receiving of the end of song
                    //bindService method is not instant
                    //For that reason code for runnable is activated only after the service is binded
                    //Otherwise methods might be called before the bind and cause crashes

                    if(mediaControlService.musicPath!=null && !mediaControlService.musicPath.equals("none")){ //checks if there was music played before
                        String path = mediaControlService.musicPath; //important on activity changes like rotation
                        setMetadata(path); //sets metadata to last played song
                        setWaveFormSeekbar();

                        if(mediaControlService.isMediaPlaying()){
                            btnPlay.setImageResource(R.drawable.ic_action_pause);
                            setRunnable();
                        }else{
                            totalText.setText(TypeConverter.formatDuration(mediaControlService.mediaDuration()));
                            remainingText.setText(TypeConverter.formatDuration(mediaControlService.mediaRemaining()));
                            seekBar.setMax(mediaControlService.mediaDuration());
                            seekBar.setProgress(mediaControlService.mediaRemaining());
                        }

                        //setWaveFormSeekbar();
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



    public void receiverBroadcast(){
        if(receiversRegistered == false) {
            receiverFinished = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Boolean isFinished = intent.getExtras().getBoolean("isFinished");
                    mediaControlService.pauseMedia();
                    handler.removeCallbacks(runnable);
                    btnPlay.setImageResource(R.drawable.ic_action_play);
                    if (passToActivity != null) {
                        passToActivity.onDataReceived(false);
                    }
                }
            };
            context.registerReceiver(receiverFinished, new IntentFilter("STATE"));

            receiverChange = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean isPlaying = intent.getExtras().getBoolean("isPlaying");
                    viewModelMain.setCurrentSourceInteger(mediaControlService.sourceIndex);
                    setWaveFormSeekbar();
                    if (isPlaying) {
                        btnPlay.setImageResource(R.drawable.ic_action_pause);
                        setMetadata(mediaControlService.musicPath);
                        passToActivity.onDataReceived(true);
                        setRunnable();

                    } else {
                        btnPlay.setImageResource(R.drawable.ic_action_play);
                        setMetadata(mediaControlService.musicPath);
                        passToActivity.onDataReceived(false);
                    }


                }
            };
            context.registerReceiver(receiverChange, new IntentFilter("SOURCECHANGED"));


            receiverPlayState = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean isPlaying = intent.getExtras().getBoolean("isPlaying");
                    System.out.println("Next");
                    if (isPlaying) {
                        btnPlay.setImageResource(R.drawable.ic_action_pause);
                        if (passToActivity != null) {
                            passToActivity.onDataReceived(true);
                        }
                    } else {
                        btnPlay.setImageResource(R.drawable.ic_action_play);
                        if (passToActivity != null) {
                            passToActivity.onDataReceived(false);
                        }
                    }
                }
            };
            context.registerReceiver(receiverPlayState, new IntentFilter("PLAYPAUSE"));
            receiversRegistered = true;
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
                    remainingText.setText(TypeConverter.formatDuration(mediaControlService.mediaRemaining()));
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
                if(mediaControlService!=null){
                    if(!mediaControlService.isMediaPlaying()) {
                        passToActivity.onDataReceived(true);
                        play();
                    } else {
                        passToActivity.onDataReceived(false);
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
                playPreviousFromMp();

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextFromMp();
                //setRunnable();
            }
        });
    }

    public void playPreviousFromMp(){
        mediaControlService.playPrev();
        viewModelMain.setCurrentSourceInteger(mediaControlService.sourceIndex);
        //setMetadata(mediaControlService.musicPath);

    }

    public void playNextFromMp(){
        mediaControlService.playNext();
        viewModelMain.setCurrentSourceInteger(mediaControlService.sourceIndex);
        //setMetadata(mediaControlService.musicPath);
    }


    public void setPassDataInterface(PassMusicStatus musicStatus){
        passToActivity = musicStatus;
    }

    public void unsetPassDataInterface(){
        passToActivity = null;
    }

    public void setSource(Playlist playlistSource, Integer indexSource) {
        source = playlistSource;
        index = indexSource;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void unsetReceivers() {

        if(receiversRegistered && receiverFinished!=null && receiverChange!=null){
            context.unregisterReceiver(receiverFinished);
            context.unregisterReceiver(receiverChange);
            context.unregisterReceiver(receiverPlayState);
            receiverFinished = null;
            receiverChange = null;
            receiverPlayState = null;
            receiversRegistered = false;
        }



    }
}
