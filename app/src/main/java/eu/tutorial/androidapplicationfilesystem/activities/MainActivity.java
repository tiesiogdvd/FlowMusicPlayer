package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MetadataGetterSetter;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
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
    MediaPlayer mp;
    String receivedPath;
    MediaMetadataRetriever metadataRetriever;
    TextView songName;
    TextView artistName;
    SeekBar seekBar;
    MusicData musicData;
    File[] filesAndFolders;
    File musicFile;
    MetadataGetterSetter metadataGetterSetter;

    private void initiateMethods(){
        btnStorage = findViewById(R.id.btnStorage);
        btnPlay = findViewById(R.id.playButton);
        btnPrevious = findViewById(R.id.lastButton);
        btnNext = findViewById(R.id.nextButton);
        btnPlaylist = findViewById(R.id.playlistButton);
        btnFavorite = findViewById(R.id.favoriteButton);
        btnMusicImage = findViewById(R.id.musicImage);
        metadataRetriever = new MediaMetadataRetriever();
        songName = findViewById(R.id.musicSongName);
        artistName = findViewById(R.id.musicSongArtist);
        seekBar = findViewById(R.id.musicProgress);

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

                            MetadataGetterSetter metadataGetterSetter = new MetadataGetterSetter();
                            //MetadataGetterSetter class is called to receive metadata with the help of MusicData class and then put it into MainActivity Views
                            metadataGetterSetter.retrieveMetadata(receivedPath, MainActivity.this);
                            //For it to work it is required the audio file path and MainActivity context

                            play(receivedPath);
                        }

                    }
                }
            }
    );

    private void play(String path) { //Play music from local storage
        mp.release();
        mp = MediaPlayer.create(MainActivity.this, Uri.parse(path));
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_pause);
    }
    private void play(int resid) { //Play music from app resource
        mp.release();
        mp = MediaPlayer.create(MainActivity.this, resid);
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_pause);
    }
    private void Pause(){
        mp.pause();
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }
    private void Unpause(){
        mp.start();
        btnPlay.setImageResource(R.drawable.ic_action_play);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);
        initiateMethods();
        setOnClickListeners();
        mp = MediaPlayer.create(this, R.raw.music);

        String path1 = Environment.getExternalStorageDirectory().getPath();
        path1+="/MuzikaTest/Rammstein - Rammstein (2019) [320]/05 SEX.mp3";
        File testFile = new File(path1);
        mp = MediaPlayer.create(this, Uri.parse(testFile.getAbsolutePath()));


    }

    private void setOnClickListeners(){

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp!=null){
                    if(!mp.isPlaying()) {
                        Toast.makeText(MainActivity.this, "Music should start playing", Toast.LENGTH_SHORT).show();
                        mp.start();
                        btnPlay.setImageResource(R.drawable.ic_action_pause);
                    } else {btnPlay.setImageResource(R.drawable.ic_action_play);
                        mp.pause();}
                } else{
                    Toast.makeText(MainActivity.this, "No music resource loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPlay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "long press", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnMusicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath();
                path+="/Download";
                System.out.println(path);
                File root = new File(path);
                File [] filesAndFolders = root.listFiles();
                // selectedFile = filesAndFolders[8];
                //filesAndFolders.length;
                System.out.println(filesAndFolders.length);
                //System.out.println(selectedFile.length);


                Toast.makeText(MainActivity.this, "normal press", Toast.LENGTH_SHORT).show();
            }
        });

        btnMusicImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath();
                File root = new File(path);
                filesAndFolders = root.listFiles();
                File selectedFile = filesAndFolders[8];
                //filesAndFolders.length;

                System.out.println(selectedFile.getName());
                Toast.makeText(MainActivity.this, "long press", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    // permission allowed
                    Intent intent = new Intent(MainActivity.this, FileListActivity.class); //Creates an intent from the current Activity to FileListActivity's class
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path); //Includes data which FileListActivity will use, in this case it will be given storage path
                    //startActivity(intent);
                    activityLauncher.launch(intent);
                }else{
                    //permission not allowed
                    requestPermission();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(R.raw.music1);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/MuzikaTest/Complicated.mp3";
                play(path);
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_overlay_layout);
        Button editLayout = dialog.findViewById(R.id.layoutEdit);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){ //if permission for storage is not given, it will make a Toast popup
            Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
        } else
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},111); //Request for storage permission, has to also be added in AndroidManifest.xml

    }
}