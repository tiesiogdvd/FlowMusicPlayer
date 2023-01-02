package eu.tutorial.androidapplicationfilesystem.activities.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylistsLibrary;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.PlaylistDatabaseHelper;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;


public class FragmentLibrary extends Fragment {

    RecyclerView recyclerView;
    AdapterPlaylistsLibrary adapter;
    ArrayList<Playlist> playlists;
    private ViewModelMain viewModelMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = v.findViewById(R.id.libraryRecyclerView);

        playlists = new ArrayList<>();
        viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class);
        viewModelMain.getPlaylists().observe(getViewLifecycleOwner(),playlistsViewModel -> {
            if(playlistsViewModel!=null){
                playlists.clear();
                playlists.addAll(playlistsViewModel);
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new AdapterPlaylistsLibrary(getContext(),playlists);



        /*if (getArguments() != null){
            playlists = (ArrayList<Playlist>)getArguments().getSerializable("playlists");
            System.out.println("hahahahahah");
            System.out.println(playlists.get(0).getPlaylistName());
        }*/

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }
}