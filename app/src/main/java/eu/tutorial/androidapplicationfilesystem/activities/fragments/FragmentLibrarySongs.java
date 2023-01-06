package eu.tutorial.androidapplicationfilesystem.activities.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryItems;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylistsLibrary;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;


public class FragmentLibrarySongs extends Fragment implements FastScrollRecyclerView.SectionedAdapter {
    AdapterLibraryItems adapterLibraryItems;
    FastScrollRecyclerView recyclerView;
    FragmentManager fragmentManager;
    Playlist requestedPlaylist;
    ArrayList<String> allSongsPaths;
    ArrayList<Playlist> playlists;
    Playlist playlist;
    String action;
    String playlistName;

    public FragmentLibrarySongs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library_songs, container, false);

        action = getArguments().getString("action");
        if (action.equals("playlist")){
            playlistName = getArguments().getString("playlistName");
        }

        initViews(view);
        initViewModel();

        return view;
    }

    private void initViews(View view){
        //allSongsMetadata = new Playlist();
        recyclerView = view.findViewById(R.id.libraryRecyclerViewItems);
        playlists = new ArrayList<>();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModel(){

        ViewModelMain viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class); //Initiated ViewModel
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(), playlistsViewModel -> { //Initiated observable for retrieving LiveData of playlists in ViewModel
            if(playlistsViewModel!=null){
                playlists.clear();
                playlists.addAll(playlistsViewModel);
                adapterLibraryItems.notifyDataSetChanged(); //Adapter will reconstruct the views on playlist LiveData changes in ViewModel
            }
        });

        if(action.equals("allSongs")){
            //requestedPlaylist = viewModelMain.getAllSongs();
            allSongsPaths = viewModelMain.getAllSongsPaths();
            playlist = viewModelMain.getPlaylist("All Songs");
            System.out.println(playlist.getSong(0));

        }
        if(action.equals("playlist") && playlistName!=null && !playlistName.equals("")){
            requestedPlaylist = viewModelMain.getPlaylist(playlistName);
        }
        if(action.equals("playlists")){
            //requestedPlaylist = viewModelMain.getAllSongs();
            //allSongs = viewModelMain.getAllSongsPaths();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViews();
    }

    private void initRecyclerViews(){
        adapterLibraryItems = new AdapterLibraryItems(getContext(),allSongsPaths,playlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterLibraryItems);

    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return null;
    }

}