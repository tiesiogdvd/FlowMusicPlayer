package eu.tutorial.androidapplicationfilesystem.activities.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylist;
import eu.tutorial.androidapplicationfilesystem.classes.MediaControlFragment;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.PlaylistDatabaseHelper;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import soup.neumorphism.NeumorphImageButton;


public class FragmentMusicPlayer extends Fragment{

    PlaylistDatabaseHelper myDB;
    ArrayList<Playlist> playlists;
    NeumorphImageButton btnPlaylist;
    DialogPlaylist dialog;
    File musicFile;
    MediaControlFragment mc;
    PassMusicStatus passDataInterface;
    private ViewModelMain viewModelMain;

    public FragmentMusicPlayer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
        //setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
       // setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransitionManager(R.transition.image_transition, getView()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        playlists = new ArrayList<>();
        viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class);
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(),playlistsViewModel -> {
            if(playlistsViewModel!=null){
                playlists.clear();
                playlists.addAll(playlistsViewModel);
            }
        });

        /*if (getArguments() != null){
            playlists = (ArrayList<Playlist>)getArguments().getSerializable("playlists");
            System.out.println("hahahahahah");
            musicFile = (File) getArguments().getSerializable("musicFile");
        } else {

            myDB = new PlaylistDatabaseHelper(getContext());
            playlists = new ArrayList<>();
            getPlaylists();
        }*/

        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //StyleSetter.setInitBackground(getContext());
        initViews(view);
        initListeners();
        //SwipeListener swipeListener = new SwipeListener(this,c)
        //if mc.is
    }




    public void initViews(View view){
        mc = new MediaControlFragment(getContext());
        mc.setPassDataInterface(passDataInterface);
        System.out.println("Shit happens");
        dialog = new DialogPlaylist();
        //musicFile = new File("/storage/emulated/0/MuzikaTest/Rammstein - Rammstein (2019) [320]/08 DIAMANT.mp3"); //keep temporarily to keep playlists working until data retrieved from mainactivity
        //myDB = new PlaylistDatabaseHelper(getContext());
        //playlists = new ArrayList<>();
        //getPlaylists();
        btnPlaylist = view.findViewById(R.id.playlistButton);

        mc.bindService();

        NeumorphImageButton favButton = view.findViewById(R.id.favoriteButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passDataInterface.onDataReceived(mc.isPlaying());
            }
        });
    }


    public void initListeners(){
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.createDialog(getContext(),playlists,musicFile);
            }
        });

    }

    public void lastPlayed(File file){
        musicFile = file;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof PassMusicStatus){
            passDataInterface = (PassMusicStatus) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PassData");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        passDataInterface=null;
        mc.unsetPassDataInterface();
        mc.handlerRemoveCallbacks();
    }

}