package eu.tutorial.androidapplicationfilesystem.activities.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControlFragment;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import soup.neumorphism.NeumorphImageButton;


public class FragmentMusicPlayer extends Fragment{

    ArrayList<Playlist> playlists;
    NeumorphImageButton btnPlaylist;
    NeumorphImageButton favButton;
    DialogPlaylist dialog; //dialog for adding songs to playlists and removing them
    File musicFile; //file that is currently playing
    MediaControlFragment mc; //MediaControl class binds application to service for MediaPlayer
    PassMusicStatus passDataInterface; //Gets passed to MediaControl class to create communication between MainActivity and fragment about music status

    public FragmentMusicPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passDataInterface = (PassMusicStatus) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PassData");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        mc = new MediaControlFragment(getContext());
        mc.setPassDataInterface(passDataInterface);
        mc.bindService();
        dialog = new DialogPlaylist();
        btnPlaylist = view.findViewById(R.id.playlistButton);
        favButton = view.findViewById(R.id.favoriteButton);
    }

    private void initViewModel(){
        ViewModelMain viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class);
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(), playlistsViewModel -> { //Created observable checks for playlist changes in ViewModel playlists LiveData
            if(playlistsViewModel!=null){
                playlists.clear(); //Upon change of playlists in ViewModel it resets the playlists data in this fragment.
                playlists.addAll(playlistsViewModel);
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

            }
        });

    }

    @Override
    public void onDestroy() {
        System.out.println("Destroyed");
        mc.handlerRemoveCallbacks(); //!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
        mc.unbindService();
        super.onDestroy();
    }

    @Override
    public void onStop() { //Called after leaving activity, after onPause method then calls onRetart
        //mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK
        mc.handlerRemoveCallbacks();
        mc.unbindService();
        super.onStop();
    }

    @Override
    public void onResume() {
        mc.bindService(); //Service needs to be rebinded after activity changes and etc.
        mc.handlerRemoveCallbacks();//!IMPORTANT TO NOT CAUSE RUNNABLE MEMORY LEAK || On screen rotation || On notification clicked || and etc
        super.onResume();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        passDataInterface=null;
        mc.unsetPassDataInterface();
        mc.handlerRemoveCallbacks();
    }


    public void lastPlayed(File file){
        musicFile = file;
    }


}