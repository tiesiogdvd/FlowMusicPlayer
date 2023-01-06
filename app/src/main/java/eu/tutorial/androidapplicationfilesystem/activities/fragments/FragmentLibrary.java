package eu.tutorial.androidapplicationfilesystem.activities.fragments;

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
import android.widget.LinearLayout;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylistsLibrary;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;


public class FragmentLibrary extends Fragment {

    RecyclerView recyclerView;
    AdapterPlaylistsLibrary adapter;
    ArrayList<Playlist> playlists;
    FragmentManager fragmentManager;
    LinearLayout btnAllSongs;
    String libraryFragmentTag = "library";
    String libraryFragmentSongsTag = "songs";
    FragmentLibrary fragmentLibrary;
    FragmentLibrarySongs fragmentLibrarySongs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        if(savedInstanceState!=null){
            fragmentLibrarySongs = (FragmentLibrarySongs) fragmentManager.findFragmentByTag(libraryFragmentSongsTag);
            if(fragmentLibrarySongs==null){fragmentLibrarySongs = new FragmentLibrarySongs();}
        }else{
            fragmentLibrarySongs = new FragmentLibrarySongs();
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
                //bundle.putString("playlist","playlistname");
                fragmentLibrarySongs.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out)
                        //.addToBackStack(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .add(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentSongsTag)
                        .addToBackStack(libraryFragmentSongsTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

}
