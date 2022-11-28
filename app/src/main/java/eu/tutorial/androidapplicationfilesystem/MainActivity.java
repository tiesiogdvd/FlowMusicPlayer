package eu.tutorial.androidapplicationfilesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import java.io.File;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar

        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.music);
        setOnClickListeners();



    }

    private void setOnClickListeners(){

        btnStorage = findViewById(R.id.btnStorage);
        btnPlay = findViewById(R.id.playButton);
        btnPrevious = findViewById(R.id.lastButton);
        btnNext = findViewById(R.id.nextButton);
        btnPlaylist = findViewById(R.id.playlistButton);
        btnFavorite = findViewById(R.id.favoriteButton);
        btnMusicImage = findViewById(R.id.musicImage);

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
                File [] filesAndFolders = root.listFiles();
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
                    startActivity(intent);
                }else{
                    //permission not allowed
                    requestPermission();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.release();
                mp = MediaPlayer.create(MainActivity.this, R.raw.music1);
                mp.start();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/MuzikaTest/Complicated.mp3";
                mp.release();
                //mp = MediaPlayer.create(MainActivity.this, R.raw.music1);
                mp = MediaPlayer.create(MainActivity.this, Uri.parse(path));
                mp.start();
            }
        });
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