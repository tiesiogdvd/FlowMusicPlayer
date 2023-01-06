package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragments.FragmentLibrary;
import eu.tutorial.androidapplicationfilesystem.activities.fragments.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.activities.fragments.FragmentMusicPlayer;
import eu.tutorial.androidapplicationfilesystem.classes.PermissionsRetriever;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControl;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.SwipeListener;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.classes.SongDataGetMusicInfo;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity implements PassMusicStatus {

    NeumorphImageButton btnStorage;
    NeumorphImageButton btnPlay;
    NeumorphImageButton openFragment1,openFragment2;
    String receivedPath;
    File musicFile;
    String lastPlayed;
    Playlist allMusic;
    TextView remainingText, totalText;
    MediaControl mc;
    SwipeListener swipeListener;
    FragmentManager fragmentManager;
    LinearLayout musicBar, navBar;
    FragmentMusicPlayer fragmentMusicPlayer;
    String mpFragmentTag = "mp";
    FragmentLibrary fragmentLibrary;
    String libraryFragmentTag = "library";
    FragmentLibrarySongs fragmentLibrarySongs;
    String libraryFragmentSongsTag = "songs";
    String lastFragment;

    private ViewModelMain viewModelMain;


    private void initiateMethods(Bundle savedInstanceState){
        System.out.println("WTWFWFWFF");
        btnStorage = findViewById(R.id.btnStorage);
        btnPlay = findViewById(R.id.playButtonBar);
        //playlists = new ArrayList<>();
        //musicFile = new File("/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANT.mp3");
        allMusic = new Playlist("All music");
        remainingText = findViewById(R.id.musicRemainingText);
        totalText = findViewById(R.id.musicTotalText);
        lastPlayed = null;
        mc = new MediaControl(this);
        lastFragment=null;
        navBar = findViewById(R.id.bottomNavbar);
        fragmentManager = getSupportFragmentManager();
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);
        swipeListener = new SwipeListener(fragmentContainerView, this);
        viewModelMain = new ViewModelProvider(this).get(ViewModelMain.class);

        openFragment1 = findViewById(R.id.openFragment1);
        openFragment2 = findViewById(R.id.openFragment2);
        musicBar = findViewById(R.id.musicBar);

        //Prevents recreation of fragment during changes
        if(savedInstanceState!=null){
            fragmentMusicPlayer = (FragmentMusicPlayer) fragmentManager.findFragmentByTag(mpFragmentTag);
            fragmentLibrary = (FragmentLibrary) fragmentManager.findFragmentByTag(libraryFragmentTag);
            fragmentLibrarySongs = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentSongsTag);
            if(fragmentMusicPlayer==null){fragmentMusicPlayer = new FragmentMusicPlayer();}
            if(fragmentLibrary==null){fragmentLibrary = new FragmentLibrary();}
            if(fragmentLibrarySongs==null){fragmentLibrarySongs = new FragmentLibrarySongs();}
        }else{
            fragmentMusicPlayer = new FragmentMusicPlayer();
            fragmentLibrary = new FragmentLibrary();
            fragmentLibrarySongs = new FragmentLibrarySongs();
            //openFragmentMusicPlayer();
        }



        SharedPreferences sh = getSharedPreferences("lastMusic",MODE_PRIVATE);

        boolean appRunFirstTime = sh.getBoolean("firstRun",true);
        if(appRunFirstTime){
            System.out.println("First time");
            this.deleteDatabase("PlaylistLibrary.db");
            sh.edit().putBoolean("firstRun", false).apply();
            viewModelMain.loadDataAndCache();
        }else{
            if(!viewModelMain.isLoaded()) {
                System.out.println("Not first time");
                viewModelMain.setPlaylists1();
            }
            //viewModelMain.setPlaylists1();
        }

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
                            fragmentMusicPlayer.lastPlayed(musicFile);
                            openFragmentMusicPlayer();
                        }
                    }
                }
            }
    );


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);

        if (PermissionsRetriever.checkPermissions(MainActivity.this) == 1 || PermissionsRetriever.checkPermissions(MainActivity.this)==2) {

            initiateMethods(savedInstanceState);
            setOnClickListeners();

            if (viewModelMain.getLastFragment().equals(mpFragmentTag)) {
                musicBar.setVisibility(View.GONE);
            } else {
                musicBar.setVisibility(View.VISIBLE);
            }
            if (viewModelMain.getBarStatus().equals(true)) {
                navBar.setVisibility(View.VISIBLE);
            } else {
                navBar.setVisibility(View.GONE);
            }
        }
        SongDataGetMusicInfo.getMusicInfos(this);
        //openFragmentMusicPlayer();

    }


    @Override
    protected void onDestroy() {
        mc.handlerRemoveCallbacks();
        mc.unbindService();

        super.onDestroy();
    }

    @Override
    protected void onStop() { //Called after leaving activity, after onPause method then calls onRetart

        mc.handlerRemoveCallbacks();
        mc.unbindService();
        super.onStop();
    }

    @Override
    protected void onRestart() { //Called after onStop then calls onStart
        // mc.startService();
        mc.handlerRemoveCallbacks();
        mc.bindService(); //Service needs to be rebinded after activity changes and etc.
        super.onRestart();
    }

    @Override
    protected void onStart() {
        SharedPreferences sh = getSharedPreferences("lastMusic",MODE_PRIVATE);




        if(sh!=null) {
            String lastSongPath = sh.getString("lastSongPath", "");
            int lastSongPosition = sh.getInt("lastSongRemaining", -1);
            lastFragment = sh.getString("lastFragment","");
            musicFile = new File(lastSongPath);
            fragmentMusicPlayer.lastPlayed(musicFile);
            if (!lastSongPath.equals("") && lastSongPosition != -1) {
                mc.setLastSong(lastSongPath);
                mc.setLastPosition(lastSongPosition);
                //mc.bindService();
                //openFragmentMusicPlayer();
            }
        }
        super.onStart();
    }

    @Override
    protected void onPause() {

        SharedPreferences sh = getSharedPreferences("lastMusic", MODE_PRIVATE);
        SharedPreferences.Editor myPreferences = sh.edit();
        myPreferences.putString("lastSongPath", mc.getPath());
        myPreferences.putInt("lastSongRemaining", mc.getRemaining());
        myPreferences.putString("lastFragment", lastFragment);
        myPreferences.apply();

        super.onPause();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //mc.setMetadata();
        //MetadataGetterSetter.setBarMetadata(this,musicFile.getAbsolutePath());
        super.onConfigurationChanged(newConfig);
        System.out.println("configChange CALLED");
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
       openFragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentMusicPlayer();
            }
        });

        openFragment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.animate().setDuration(300);
                musicBar.setVisibility(View.VISIBLE);
                openFragmentLibrary();

            }
        });

        musicBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentMusicPlayer();
            }
        });


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
    }



    private void openFragmentMusicPlayer(){
    fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.pop_in, R.anim.fade_out)
            .replace(R.id.fragmentContainerView, fragmentMusicPlayer, mpFragmentTag)
            .commit();
    //fragmentManager.popBackStack();
        musicBar.setVisibility(View.GONE);
        viewModelMain.setLastFragment(mpFragmentTag);

    }



    private void openFragmentLibrary(){
        viewModelMain.setLastFragment(libraryFragmentTag);
        final ImageView imageView = (ImageView) this.findViewById(R.id.musicImageBar);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.fade_out)
                .replace(R.id.fragmentContainerView, fragmentLibrary, libraryFragmentTag)
                .addSharedElement(imageView ,"toMusicPlayer")
                .commit();
    }

    @Override
    public void onDataReceived(Boolean isPlaying) {
        if(isPlaying){
            btnPlay.setImageResource(R.drawable.ic_action_pause);
        }else{
            btnPlay.setImageResource(R.drawable.ic_action_play);
        }
    }
}