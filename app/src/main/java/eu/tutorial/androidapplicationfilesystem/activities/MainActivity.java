package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Fade;
import androidx.transition.Transition;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrary;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentMusicPlayer;
import eu.tutorial.androidapplicationfilesystem.classes.PathPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.PermissionsRetriever;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControl;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.Settings;
import eu.tutorial.androidapplicationfilesystem.classes.SwipeListener;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;

public class MainActivity extends AppCompatActivity implements PassMusicStatus {

    ShapeableImageView btnPlay, openSettings, openLibrary;
    String receivedPath;
    File musicFile;

    MediaControl mc;
    SwipeListener swipeListener;
    FragmentManager fragmentManager;
    LinearLayout musicBar, navBar, fullBar;
    FragmentMusicPlayer fragmentMusicPlayer;
    String mpFragmentTag = "mp";
    FragmentLibrary fragmentLibrary;
    String libraryFragmentTag = "library";
    FragmentLibrarySongs fragmentLibrarySongs;
    private ViewModelMain viewModelMain;
    ConstraintLayout background;

    Boolean selectEnabled;


    private void initiateMethods() {

        mc = new MediaControl(this);
        viewModelMain = new ViewModelProvider(this).get(ViewModelMain.class);
        btnPlay = findViewById(R.id.playButtonBar);
        openSettings = findViewById(R.id.openSettings);
        openLibrary = findViewById(R.id.openLibrary);
        musicBar = findViewById(R.id.musicBar);
        navBar = findViewById(R.id.bottomNavbar);
        background = findViewById(R.id.layoutID);
        fullBar = findViewById(R.id.navbars);

        selectEnabled = false;


        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);
        swipeListener = new SwipeListener(fragmentContainerView, this);
    }


    public void initFragments(){
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        //Prevents recreation of fragment during changes
        fragmentMusicPlayer = (FragmentMusicPlayer) fragmentManager.findFragmentByTag(mpFragmentTag);
        fragmentLibrary = (FragmentLibrary) fragmentManager.findFragmentByTag(libraryFragmentTag);
        if (fragmentMusicPlayer == null) {
            fragmentMusicPlayer = new FragmentMusicPlayer();
        }
        if (fragmentLibrary == null) {
            fragmentLibrary = new FragmentLibrary();
        }
    }




    public void setSettings(){
        SharedPreferences sh = getSharedPreferences("lastMusic", MODE_PRIVATE);
        Settings.setSettings(sh);

        //Settings.setUseMediastore(true);
        //Settings.setSaveImageCache(true);
        //Settings.setGetImageCacheOnLoad(true);
        //Settings.loadOnlyFromCache();

        if (Settings.isFirstRun() || !Settings.getLastLoadingFinished()) {
            if (Settings.isFirstRun()) {
                System.out.println("First time");
                Playlist playlist = new Playlist();
                playlist.setPlaylistName("Favorites");
                viewModelMain.addPlaylist(playlist);
            }
            if (Settings.getUseMediastore()) {
                viewModelMain.loadDataInit();
            } else {
                viewModelMain.loadDataAndCache();
            }
        } else {
            if (!viewModelMain.isLoaded()) {
                System.out.println("Not first time");
                viewModelMain.setPlaylists1();
            }
        }
    }

    public void setAnimation(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable animationDrawable = (AnimationDrawable) background.getBackground();
                animationDrawable.setEnterFadeDuration(2500);
                animationDrawable.setExitFadeDuration(5000);
                animationDrawable.start();
            }
        });
    }



    //ActivityResultLauncher waits for the FileListActivity to finish and retrieves the data provided from that activity
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1) {
                        //ResultCode 1 means that music data has been received amd executes this part of code
                        Intent intent = result.getData();
                        if (intent != null) { //confirms that there is actual music data provided in the result
                            receivedPath = intent.getStringExtra("result");
                            playMusic(receivedPath);
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
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getSupportActionBar().hide(); //to hide top Application Bar
        setContentView(R.layout.activity_main);


        if (PermissionsRetriever.checkPermissions(MainActivity.this) == 1 || PermissionsRetriever.checkPermissions(MainActivity.this) == 2) {
            initiateMethods();
            initFragments();
            setSettings();
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
        mc.handlerRemoveCallbacks();
        mc.bindService(); //Service needs to be rebinded after activity changes and etc.
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAnimation();
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sh = getSharedPreferences("lastMusic", MODE_PRIVATE);
        Settings.setSettings(sh);
        String lastSongPath = Settings.getLastSongPath();
        int lastSongPosition = Settings.getLastSongRemaining();
        String lastSongSource = Settings.getLastSongSource();

        Playlist sourcePlaylist;
        Integer index;

        if (!mc.isServiceBound() && lastSongPath != null && new File(lastSongPath).exists()) {

            mc.setLastSong(lastSongPath);
            if (lastSongPosition != -1) {
                mc.setLastPosition(lastSongPosition);
            }

            if (lastSongSource != null) {
                sourcePlaylist = viewModelMain.getCurrentSource().getValue();
                System.out.println(sourcePlaylist.getPlaylistName());
                viewModelMain.getCurrentSource().observe(this, source -> {
                    if (source != null) {
                        mc.setSource(sourcePlaylist,viewModelMain.getCurrentIndex().getValue());
                    }
                });

                index = viewModelMain.getCurrentIndex().getValue();
                mc.setSource(sourcePlaylist,index);
                fragmentMusicPlayer.lastPlayed(new File(lastSongPath), sourcePlaylist, index);
            } else {
                fragmentMusicPlayer.lastPlayed(new File(lastSongPath));
                PathPlaylist pathPlaylist = new PathPlaylist();
                String path = new File(lastSongPath).getParent();
                //ArrayList<String> paths = pathPlaylist.searchPath(path);
                //mc.setPaths(paths);
            }
            mc.bindService();
        }


    }


    @Override
    protected void onPause() {
        if (mc != null) {
            Settings.setLastSongRemaining(mc.getRemaining());
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("configChange CALLED");
    }

    private void setOnClickListeners() {
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class); //Creates an intent from the current Main Activity to Settings Activity
                startActivity(intent);
            }
        });

        openLibrary.setOnClickListener(new View.OnClickListener() {
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

    }

    public void openLibrary(){
        if (PermissionsRetriever.checkPermissions(MainActivity.this) == 1 || PermissionsRetriever.checkPermissions(MainActivity.this) == 2) {
            // permission allowed
            Intent intent = new Intent(MainActivity.this, FileListActivity.class); //Creates an intent from the current Activity to FileListActivity's class
            String path = Environment.getExternalStorageDirectory().getPath();
            intent.putExtra("path", path); //Includes data which FileListActivity will use, in this case it will be given storage path
            activityLauncher.launch(intent);
            overridePendingTransition(R.anim.pop_in,R.anim.pop_out);
        }
    }


    private void openFragmentMusicPlayer() {

        Transition transition = new Fade();
        fragmentMusicPlayer.setSharedElementEnterTransition(transition);
        fragmentMusicPlayer.setSharedElementReturnTransition(transition);


        musicBar.setVisibility(View.GONE);
        viewModelMain.setLastFragment(mpFragmentTag);
        fragmentManager.popBackStackImmediate();
        ImageView imageView = this.findViewById(R.id.musicImageBar);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                .replace(R.id.fragmentContainerView, fragmentMusicPlayer, mpFragmentTag)
                .addSharedElement(imageView ,"toMusicPlayer")
                .commitNowAllowingStateLoss();
    }

    private void openFragmentLibrary() {
        viewModelMain.setLastFragment(libraryFragmentTag);
        //final ImageView imageView = (ImageView) this.findViewById(R.id.musicImageBar);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                .replace(R.id.fragmentContainerView, fragmentLibrary, libraryFragmentTag)
                .addToBackStack(libraryFragmentTag)
                //.addSharedElement(imageView ,"toMusicPlayer")
                .commit();

    }

    private void playMusic(String songPath) {
        musicFile = new File(songPath);
        mc.play(songPath);
        fragmentMusicPlayer.lastPlayed(musicFile);
        openFragmentMusicPlayer();
    }

    private void playMusic(String songPath, Playlist playlist, int index) {
        musicFile = new File(songPath);
        //mc.play(songPath);
        mc.play(songPath,playlist,index);
        fragmentMusicPlayer.lastPlayed(musicFile);
        //fragmentMusicPlayer.lastPlayed(musicFile, playlist, index);
        openFragmentMusicPlayer();

    }

    @Override
    public void onBackPressed() {
        if(!selectEnabled) {
            super.onBackPressed();
            if (fragmentMusicPlayer != null) {
                if (fragmentMusicPlayer.isVisible()) {
                    musicBar.setVisibility(View.GONE);
                }
            }
        }else{
            selectEnabled = false;
            fragmentLibrarySongs.backPressed();
            fullBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDataReceived(Boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setImageResource(R.drawable.ic_action_pause);
        } else {
            btnPlay.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void onSongRequest(String songPath, Playlist playlist, int index) {
        fullBar.setVisibility(View.VISIBLE);
        fragmentManager.popBackStackImmediate();
        viewModelMain.setCurrentSource(playlist, index);
        playMusic(songPath, playlist, index);
    }

    @Override
    public void onMediaReady(Boolean isReady) {
        if (viewModelMain.getFirstLoad()) {
            openFragmentMusicPlayer();
            viewModelMain.setFirstLoad(false);
        }
    }

    @Override
    public void onLibraryStoragePressed() {
        openLibrary();
    }

    @Override
    public void onRequestNavbar(Boolean request, FragmentLibrarySongs fragmentLibrarySongs) {
        this.fragmentLibrarySongs = fragmentLibrarySongs;
        selectEnabled = !request;
        if(request){
            fullBar.setVisibility(View.VISIBLE);
        }
        else{
            fullBar.setVisibility(View.GONE);
        }
    }

}