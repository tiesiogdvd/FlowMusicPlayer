package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.PermissionsRetriever;
import eu.tutorial.androidapplicationfilesystem.classes.PlaylistDatabaseHelper;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControl;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.StyleSetter;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity {

    NeumorphButton btnStorage;
    NeumorphImageButton btnPlay, btnPrevious, btnNext;
    NeumorphImageButton btnPlaylist, btnFavorite, btnMusicImage;
    String receivedPath;
    File musicFile;
    String lastPlayed;
    ArrayList<Playlist> playlists;
    Playlist allMusic;
    TextView remainingText, totalText;
    MediaControl mc;
    PlaylistDatabaseHelper myDB;


    private void initiateMethods(){
        btnStorage = findViewById(R.id.btnStorage);
        btnPlay = findViewById(R.id.playButton);
        btnPrevious = findViewById(R.id.lastButton);
        btnNext = findViewById(R.id.nextButton);
        btnPlaylist = findViewById(R.id.playlistButton);
        btnFavorite = findViewById(R.id.favoriteButton);
        btnMusicImage = findViewById(R.id.musicImage);
        playlists = new ArrayList<>();
        musicFile = new File("/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANT.mp3");
        allMusic = new Playlist("All music");
        remainingText = findViewById(R.id.musicRemainingText);
        totalText = findViewById(R.id.musicTotalText);
        lastPlayed = null;
        myDB = new PlaylistDatabaseHelper(this);
        mc = new MediaControl(this);
    }

    //ActivityResultLauncher waits for the FileListActivity to finish and retrieves the data provided from that activity
    ActivityResultLauncher <Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==1){
                        //ResultCode 1 means that music data has been received amd executes this part of code
                        Intent intent = result.getData();
                        if(intent!=null){ //confirms that there is actual music data provided in the result
                            receivedPath = intent.getStringExtra("result");
                            musicFile = new File(receivedPath);
                            //mc.play(receivedPath);
                            mc.play(receivedPath);
                            //stopService();
                            //startService(receivedPath);
                        }
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate called");
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);
        initiateMethods();

        setOnClickListeners();
        //lastPlayed = "/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANTA.mp3";
        //mc.play(lastPlayed);
        //mc.pause();

        StyleSetter.setInitBackground(this);

        //String path = Environment.getExternalStorageDirectory().getPath();
        //StorageScraper.searchStorage(path,allMusic);


        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                playlists = myDB.getPlaylists();
            }
        };
        Thread thread = new Thread(runnable1);
        System.out.println(thread.getName());
        thread.start();
        thread.interrupt();
        //savePlaylistData(playlists);

        //BroadcastReceiverMediaPlayer.receiveBroadcast(this);
    }



    @Override
    protected void onDestroy() {
        mc.handlerRemoveCallbacks(); //!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
        mc.unbindService();
        super.onDestroy();
    }

    @Override
    protected void onStop() { //Called after leaving activity, after onPause method then calls onRetart
        //mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
        mc.handlerRemoveCallbacks();
        mc.unbindService();
        super.onStop();
    }

    @Override
    protected void onRestart() { //Called after onStop then calls onStart
       // mc.startService();
        mc.bindService(); //Service needs to be rebinded after activity changes and etc.
        mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK || On screen rotation || On notification clicked || and etc
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //mc.setMetadata();
        super.onConfigurationChanged(newConfig);
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main);
            if(mc.isPlaying()){
                for(int i=0; i<=5; i++){
                    System.out.println("IS PLAYING MADAFAKAAAAAA");
                }
            }
        } else {
            setContentView(R.layout.activity_main);

        }
    }*/


    private void savePlaylistData(ArrayList<Playlist> playlist){

        File internalPath = this.getFilesDir();
        System.out.println(internalPath.getAbsolutePath());
        if(!playlist.equals(null)){
            for (Playlist pl : playlist) {
                System.out.println(pl.getPlaylistName());
                for(int i=0; i<pl.getLength();i++){
                    System.out.println(pl.getSongPath(i));
                }
            }
        }
    }

    private void setOnClickListeners(){
        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionsRetriever.checkPermissions(MainActivity.this) == 1 || PermissionsRetriever.checkPermissions(MainActivity.this)==2){
                    // permission allowed
                    Intent intent = new Intent(MainActivity.this, FileListActivity.class); //Creates an intent from the current Activity to FileListActivity's class
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path); //Includes data which FileListActivity will use, in this case it will be given storage path
                    //startActivity(intent);
                    activityLauncher.launch(intent);
                }
            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        DialogPlaylist dialogPlaylist = new DialogPlaylist();
        dialogPlaylist.createDialog(MainActivity.this,playlists,musicFile);
    }


}