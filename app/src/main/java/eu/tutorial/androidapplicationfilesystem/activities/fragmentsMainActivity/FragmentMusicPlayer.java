package eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControlFragment;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.Settings;
import eu.tutorial.androidapplicationfilesystem.classes.TypeConverter;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import soup.neumorphism.NeumorphImageButton;


public class FragmentMusicPlayer extends Fragment{

    ArrayList<Playlist> playlists;
    ImageButton btnPlaylist;
    ImageButton favButton;
    DialogPlaylist dialog; //dialog for adding songs to playlists and removing them
    File musicFile; //file that is currently playing
    MediaControlFragment mc; //MediaControl class binds application to service for MediaPlayer
    PassMusicStatus passDataInterface; //Gets passed to MediaControl class to create communication between MainActivity and fragment about music status
    ViewModelMain viewModelMain;
    Playlist favorites;

    public FragmentMusicPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getActivity() instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passDataInterface = (PassMusicStatus) getActivity();
        } else {
            throw new RuntimeException(getContext().toString() + " must implement PassData");
        }

        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initViewModel();
        initListeners();
    }


    private void initViews(View view){
        playlists = new ArrayList<>();
        mc = new MediaControlFragment(this);
        mc.setPassDataInterface(passDataInterface);
        mc.bindService();
        mc.receiverBroadcast();
        dialog = new DialogPlaylist();
        btnPlaylist = view.findViewById(R.id.playlistButton);
        favButton = view.findViewById(R.id.favoriteButton);
    }

    private void initViewModel(){
        viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class);
        mc.setViewModel(viewModelMain);
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(), playlistsViewModel -> { //Created observable checks for playlist changes in ViewModel playlists LiveData
            if(playlistsViewModel!=null){
                playlists.clear(); //Upon change of playlists in ViewModel it resets the playlists data in this fragment.
                playlists.addAll(playlistsViewModel);
                favorites = viewModelMain.getPlaylist("Favorites");
                if(musicFile!=null){
                    if(favorites.inPlaylist(musicFile.getAbsolutePath())){
                        favButton.setImageResource(R.drawable.ic_action_favorite_filled);
                    }
                }
            }
        });

        viewModelMain.getCurrentIndex().observe(getViewLifecycleOwner(), index->{
            if(index!=null && index!=-1 && viewModelMain.getCurrentSource().getValue()!=null && viewModelMain.getCurrentSource().getValue().getLength()!=0){
                System.out.println(index);
                String musicPath = viewModelMain.getCurrentSource().getValue().getSongPath(index);
                if(musicPath!=null) {
                    musicFile = new File(musicPath);
                    if (favorites!=null && favorites.inPlaylist(musicFile.getAbsolutePath())) {
                        favButton.setImageResource(R.drawable.ic_action_favorite_filled);
                    } else {
                        favButton.setImageResource(R.drawable.ic_action_favorite);
                    }
                }
            }else{
                if(index==null){
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("lastMusic",MODE_PRIVATE);
                    Settings.setSettings(sharedPreferences);
                    String musicPath = Settings.getLastSongPath();
                    musicFile = new File(musicPath);
                    if(favorites!=null && favorites.inPlaylist(musicFile.getAbsolutePath())){
                        favButton.setImageResource(R.drawable.ic_action_favorite_filled);
                    }else{
                        favButton.setImageResource(R.drawable.ic_action_favorite);
                    }
                }
            }
        });


    }


    private void initListeners(){
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.createDialog(getContext(),playlists,musicFile);
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favorites != null) {
                    boolean inPlaylist = favorites.inPlaylist(musicFile.getAbsolutePath());
                    if (musicFile != null && !inPlaylist) {
                        System.out.println("Added to playlist");
                        viewModelMain.addSong(musicFile.getAbsolutePath(), "Favorites", getActivity());
                        favButton.animate();
                        favButton.setImageResource(R.drawable.ic_action_favorite_filled);
                    } else {
                        System.out.println("Null or already added");
                        if (musicFile != null) {
                            viewModelMain.removeSong(musicFile.getAbsolutePath(), "Favorites");
                            favButton.setImageResource(R.drawable.ic_action_favorite);
                        }
                    }
                }else{
                    viewModelMain.addPlaylist("Favorites", TypeConverter.getDateString());
                }
            }

        });

    }

    @Override
    public void onDestroy() {
        System.out.println("Destroyed");
        if(mc!=null) {
            mc.unsetReceivers();
            mc.handlerRemoveCallbacks(); //!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
            mc.unbindService();
            mc.unsetPassDataInterface();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() { //Called after leaving activity, after onPause method then calls onRetart
        //mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
        if(mc!=null) {
            mc.unsetReceivers();
            mc.handlerRemoveCallbacks(); //!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
            mc.unbindService();
            mc.unsetPassDataInterface();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        mc.receiverBroadcast();
        mc.bindService(); //Service needs to be rebinded after activity changes and etc.
        mc.setPassDataInterface(passDataInterface);
        mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK || On screen rotation || On notification clicked || and etc
        super.onResume();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if(mc!=null) {
            passDataInterface = null;
            mc.unsetReceivers();
            mc.unsetPassDataInterface();
            mc.handlerRemoveCallbacks();
        }
    }


    public void lastPlayed(File file){
        musicFile = file;
    }

    public void lastPlayed(File file, Playlist playlist, Integer index){
        if(mc!=null) {
            //mc.play(file.getAbsolutePath());
            musicFile = file;
            mc.setSource(playlist, index);
        }
    }

}