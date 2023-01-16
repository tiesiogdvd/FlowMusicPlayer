package eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylistsLibrary;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;


public class FragmentLibrary extends Fragment {

    RecyclerView recyclerView;
    AdapterPlaylistsLibrary adapter;
    ArrayList<Playlist> playlists;
    FragmentManager fragmentManager;
    LinearLayout btnAllSongs, btnPlaylists, btnFavorites, btnFolders, btnStorage;
    PassMusicStatus passMusicStatus;

    String libraryFragmentSongsTag = "songs";
    String libraryFragmentPlaylistsTag = "playlists";
    String libraryFragmentFavoritesTag = "favorites";
    String libraryFragmentFoldersTag = "folders";
    String libraryFragmentStorageTag = "storage";
    FragmentLibrary fragmentLibrary;
    FragmentLibrarySongs fragmentLibrarySongs, fragmentLibraryPlaylists, fragmentLibraryFavorite, fragmentLibraryFolders, fragmentLibraryStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        if(savedInstanceState!=null){
            fragmentLibrarySongs = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentSongsTag);
            if(fragmentLibrarySongs==null){fragmentLibrarySongs = new FragmentLibrarySongs();}

            fragmentLibraryPlaylists = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentPlaylistsTag);
            if(fragmentLibraryPlaylists==null){fragmentLibraryPlaylists = new FragmentLibrarySongs();}

            fragmentLibraryFavorite = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentFavoritesTag);
            if(fragmentLibraryFavorite==null){fragmentLibraryFavorite = new FragmentLibrarySongs();}

            fragmentLibraryFolders = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentFoldersTag);
            if(fragmentLibraryFolders==null){fragmentLibraryFolders = new FragmentLibrarySongs();}

            fragmentLibraryStorage = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentStorageTag);
            if(fragmentLibraryStorage==null){fragmentLibraryStorage = new FragmentLibrarySongs();}

        }else{
            fragmentLibrarySongs = new FragmentLibrarySongs();
            fragmentLibraryPlaylists = new FragmentLibrarySongs();
            fragmentLibraryFavorite = new FragmentLibrarySongs();
            fragmentLibraryFolders = new FragmentLibrarySongs();
            fragmentLibraryStorage = new FragmentLibrarySongs();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        initViews(view); //Initialises all links to views and inside calls initViewModel method
        initViewModel(); //Method for initializing ViewModel and its observable
        initListeners();
        return view;
    }

    private void initViews(View view){
        fragmentLibrary = this;
        btnAllSongs = view.findViewById(R.id.btnLibraryAllSongs);
        btnPlaylists = view.findViewById(R.id.btnLibraryPlaylists);
        btnFavorites = view.findViewById(R.id.btnLibraryFavorites);
        btnFolders = view.findViewById(R.id.btnLibraryFolders);
        btnStorage = view.findViewById(R.id.btnLibraryStorage);

        recyclerView = view.findViewById(R.id.libraryRecyclerView);
        playlists = new ArrayList<>();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    private void initViewModel(){
        ViewModelMain viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class); //Initiated ViewModel
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(), playlistsViewModel -> { //Initiated observable for retrieving LiveData of playlists in ViewModel
            if(playlistsViewModel!=null){
                playlists.clear();
                playlists.addAll(playlistsViewModel);
                //adapter.notifyDataSetChanged(); //Adapter will reconstruct the views on playlist LiveData changes in ViewModel
            }
        });
    }

    private void initListeners(){
        btnAllSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action","allSongs");
                bundle.putString("playlist","All Songs");
                fragmentLibrarySongs.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        //.addToBackStack(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .replace(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .addToBackStack(libraryFragmentSongsTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
            }
        });

        btnPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action","playlists");
                //bundle.putString("playlist","playlistname");
                fragmentLibraryPlaylists.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        //.addToBackStack(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .replace(R.id.fragmentContainerView, fragmentLibraryPlaylists, libraryFragmentPlaylistsTag)
                        .addToBackStack(libraryFragmentPlaylistsTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action","favorites");
                bundle.putString("playlist","Favorites");
                fragmentLibraryFavorite.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        //.addToBackStack(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .replace(R.id.fragmentContainerView, fragmentLibraryFavorite, libraryFragmentFavoritesTag)
                        .addToBackStack(libraryFragmentFavoritesTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
            }
        });

        btnFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action","folders");
                //bundle.putString("playlist","playlistname");
                fragmentLibraryFolders.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        //.addToBackStack(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .replace(R.id.fragmentContainerView, fragmentLibraryFolders, libraryFragmentFoldersTag)
                        .addToBackStack(libraryFragmentFoldersTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
            }
        });

        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getContext() instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
                    passMusicStatus = (PassMusicStatus) getActivity();
                } else {
                    throw new RuntimeException(getContext().toString() + " must implement PassData");
                }

                passMusicStatus.onLibraryStoragePressed();

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(); //Initializes recyclerView for displaying all playlists
    }





    private void initRecyclerView(){
        adapter = new AdapterPlaylistsLibrary(getContext(),playlists);
        recyclerView.setHasFixedSize(true);
        int orientation = getContext().getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));}
        if(orientation == Configuration.ORIENTATION_PORTRAIT){recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));}

        recyclerView.setAdapter(adapter);
    }

}
