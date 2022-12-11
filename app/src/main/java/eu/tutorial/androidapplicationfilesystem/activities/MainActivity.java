package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.PlaylistDatabaseHelper;
import eu.tutorial.androidapplicationfilesystem.classes.StorageScraper;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControl;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity {

    NeumorphButton btnStorage;
    NeumorphImageButton btnPlay;
    NeumorphImageButton btnPrevious;
    NeumorphImageButton btnNext;
    NeumorphImageButton btnPlaylist;
    NeumorphImageButton btnFavorite;
    NeumorphImageButton btnMusicImage;
    String receivedPath;
    MediaMetadataRetriever metadataRetriever;
    SeekBar seekBar;
    File musicFile;
    String lastPlayed;
    RecyclerView recyclerView;
    ArrayList<Playlist> playlists;
    Playlist allMusic;
    Handler handler;
    TextView remainingText;
    TextView totalText;
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
        metadataRetriever = new MediaMetadataRetriever();
        seekBar = findViewById(R.id.musicProgress);
        recyclerView = findViewById(R.id.playlistRecyclerView);
        playlists = new ArrayList<>();
        musicFile = new File("/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANT.mp3");
        allMusic = new Playlist("All music");
        remainingText = findViewById(R.id.musicRemainingText);
        totalText = findViewById(R.id.musicTotalText);
        handler = new Handler();
        mc = new MediaControl(this);
        lastPlayed = null;
        myDB = new PlaylistDatabaseHelper(this);
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
                            mc.play(receivedPath);
                        }
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);


        initiateMethods();
        setOnClickListeners();
        lastPlayed = "/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANT.mp3";
        mc.play(lastPlayed);
        mc.pause();

        if(checkPermissionRecordAudio()){
        }else{
            requestPermissionRecordAudio();
        }


         Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getPath();
                StorageScraper.searchStorage(path,allMusic);
                allMusic.printSongsArray();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        playlists = myDB.getPlaylists();
        savePlaylistData(playlists);

    }


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

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //savePlaylistData();
                if(mc!=null){
                    if(!mc.isPlaying()) {
                        Toast.makeText(MainActivity.this, "Music should start playing", Toast.LENGTH_SHORT).show();
                        mc.start();
                    } else {
                        mc.pause();
                    }
                } else{
                    Toast.makeText(MainActivity.this, "No music resource loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnMusicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissionStorage()){
                    // permission allowed
                    Intent intent = new Intent(MainActivity.this, FileListActivity.class); //Creates an intent from the current Activity to FileListActivity's class
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path); //Includes data which FileListActivity will use, in this case it will be given storage path
                    //startActivity(intent);
                    activityLauncher.launch(intent);
                }else{
                    //permission not allowed
                    requestPermissionStorage();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mc.play(R.raw.music1);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/MuzikaTest/Complicated.mp3";
                mc.play(path);
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

    private boolean checkPermissionStorage(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermissionStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){ //if permission for storage is not given, it will make a Toast popup
            Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
        } else
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111); //Request for storage permission, has to also be added in AndroidManifest.xml
    }

    private boolean checkPermissionRecordAudio(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermissionRecordAudio(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.RECORD_AUDIO)){ //if permission for storage is not given, it will make a Toast popup
            Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
        } else
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},112); //Request for storage permission, has to also be added in AndroidManifest.xml
    }

}