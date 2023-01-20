package eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryAllSongs;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryFavorites;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryFolders;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylistLibrary;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryPlaylists;
import eu.tutorial.androidapplicationfilesystem.classes.FastScrollRecyclerView.FastScrollRecyclerView;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassSelectionStatus;


public class FragmentLibrarySongs extends Fragment implements PassSelectionStatus {

    AdapterLibraryAllSongs adapterLibraryAllSongs;
    AdapterLibraryFavorites adapterLibraryFavorites;
    AdapterLibraryFolders adapterLibraryFolders;
    AdapterPlaylistLibrary adapterPlaylistLibrary;

    AdapterLibraryPlaylists adapterLibraryPlaylists;

    FastScrollRecyclerView recyclerView;

    FragmentManager fragmentManager;
    Playlist requestedPlaylist;

    ArrayList<String> allSongsPaths;
    ArrayList<Playlist> playlists;

    Playlist playlist;
    String action;
    String playlistName;
    EditText searchBar;
    ViewModelMain viewModelMain;
    TextView libraryText;

    Boolean selectionMode;
    TextView selectionNumber;
    LinearLayout selectionAddToPlaylist;
    LinearLayout selectionRemove;
    LinearLayout selectionSetAlbumInfo;

    ImageView libraryImage;

    ArrayList<Integer> selectionList;

    PassMusicStatus passMusicStatus;
    LinearLayout selectionBar;


    public FragmentLibrarySongs() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
        selectionList = new ArrayList<>();
        selectionMode = false;

        if(requireActivity() instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passMusicStatus = (PassMusicStatus) requireActivity();
        } else {
            throw new RuntimeException(requireActivity().toString() + " must implement PassDataaaa");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library_songs, container, false);

        action = getArguments().getString("action");

        if (action.equals("playlist")){
            playlistName = getArguments().getString("playlist");
        }

        //view.onkey

        initViews(view);
        initViewModel();

        return view;
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.libraryRecyclerViewItems);
        libraryText = view.findViewById(R.id.librarySelectionText);

        libraryImage = view.findViewById(R.id.libraryImage);

        selectionBar =  view.findViewById(R.id.selectionBar);
        selectionBar.setVisibility(View.GONE);
        selectionNumber = view.findViewById(R.id.selectionNumber);


        switch (action){
            case("allSongs"):
                libraryText.setText("All Songs");
                break;

            case("playlists"):

                libraryText.setText("Playlists");

                break;

            case("favorites"):
                libraryText.setText("Favorites");

                break;

            case("playlist"):
                libraryText.setText(playlistName);

                break;

            case("folders"):
                libraryText.setText("Folders");
                break;

            default:
                break;

        }








        playlists = new ArrayList<>();
        fragmentManager = requireActivity().getSupportFragmentManager();
        searchBar = view.findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                switch (action){
                    case("allSongs"):
                        if(adapterLibraryAllSongs!=null){
                            adapterLibraryAllSongs.filter(searchBar.getText().toString().toLowerCase());
                        }
                        break;

                    case("playlists"):
                        if(adapterLibraryPlaylists!=null){
                            adapterLibraryPlaylists.filter(searchBar.getText().toString().toLowerCase());
                        }
                        break;

                    case("favorites"):
                        if(adapterLibraryFavorites!=null){
                            adapterLibraryFavorites.filter(searchBar.getText().toString().toLowerCase());
                        }
                        break;

                    case("playlist"):
                        if(adapterPlaylistLibrary !=null){
                            adapterPlaylistLibrary.filter(searchBar.getText().toString().toLowerCase());
                        }
                        break;

                    case("folders"):
                        if(adapterLibraryFolders!=null){
                            adapterLibraryFolders.filter(searchBar.getText().toString().toLowerCase());
                        }
                        break;

                    default:
                        break;

                }
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void initViewModel(){
        viewModelMain = new ViewModelProvider(requireActivity()).get(ViewModelMain.class); //Initiated ViewModel
        /*viewModelMain.getPlaylists().observe(getViewLifecycleOwner(), playlistsViewModel -> { //Initiated observable for retrieving LiveData of playlists in ViewModel
            if(playlistsViewModel!=null){
                playlists.clear();
                playlists.addAll(playlistsViewModel);

                switch (action){
                    case("allSongs"):
                        adapterLibraryAllSongs.notifyDataSetChanged(); //Adapter will reconstruct the views on playlist LiveData changes in ViewModel
                        break;

                    case("playlists"):
                        adapterLibraryPlaylists.notifyDataSetChanged();
                        break;

                    case("favorites"):
                        adapterLibraryFavorites.notifyDataSetChanged();
                        break;

                    case("playlist"):
                        adapterPlaylistLibrary.notifyDataSetChanged();
                        //adapterPlaylistsLibrary.notifyDataSetChanged();
                        break;

                    case("folders"):
                        adapterLibraryFolders.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }
        });*/

        //playlists = viewModelMain.getPlaylists().getValue();


        switch (action){
            case("allSongs"):
                allSongsPaths = viewModelMain.getAllSongsPaths();
                playlist = viewModelMain.getPlaylist("All Songs");
                playlist.getSongsArray().sort(MusicData.titleComparator);
                libraryImage.setImageBitmap(playlist.getPlaylistBitmap(getContext()));
                break;

            case("playlists"):
                playlists = viewModelMain.getPlaylists().getValue();
                //playlistsCopy = playlists;
                playlists.sort(Playlist.titleComparator);
                libraryImage.setImageBitmap(playlists.get(0).getPlaylistBitmap(getContext()));
                break;

            case("favorites"):
                requestedPlaylist = viewModelMain.getPlaylist("Favorites");
                requestedPlaylist.getSongsArray().sort(MusicData.titleComparator);
                libraryImage.setImageBitmap(requestedPlaylist.getPlaylistBitmap(getContext()));
                break;

            case("playlist"):
                requestedPlaylist = viewModelMain.getPlaylist(playlistName);
                requestedPlaylist.getSongsArray().sort(MusicData.titleComparator);
                libraryImage.setImageBitmap(requestedPlaylist.getPlaylistBitmap(getContext()));
                break;

            case("folders"):
                break;

            default:
                break;

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViews();
    }

    private void initRecyclerViews(){
        switch (action){
            case("allSongs"):
                adapterLibraryAllSongs = new AdapterLibraryAllSongs(this, allSongsPaths,playlist);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryAllSongs);
                break;
            case("playlists"):
                adapterLibraryPlaylists = new AdapterLibraryPlaylists(requireContext(),playlists);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryPlaylists);
                break;
               /* adapterLibraryPlaylists = new AdapterLibraryPlaylists(getContext(),playlists);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapterLibraryPlaylists);*/

            case("favorites"):
                adapterLibraryFavorites = new AdapterLibraryFavorites(getContext(),requestedPlaylist);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryFavorites);
                break;

            case("playlist"):
                adapterPlaylistLibrary = new AdapterPlaylistLibrary(getContext(),requestedPlaylist);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterPlaylistLibrary);
                break;

            case("folders"):
                adapterLibraryFolders = new AdapterLibraryFolders(getContext(),allSongsPaths,playlist);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), GridLayoutManager.DEFAULT_SPAN_COUNT));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryFolders);
                break;

            default:
                break;

        }
    }

    public void backPressed(){
        //selectionBar =  getView().findViewById(R.id.selectionBar);
        selectionBar.setVisibility(View.GONE);
        selectionList = null;
        selectionList = new ArrayList<>();

        adapterLibraryAllSongs.disableSelectMode();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onChecked(Boolean isChecked, Integer itemChecked) {
        selectionMode = true;
        passMusicStatus.onRequestNavbar(false,this);
        selectionBar.setVisibility(View.VISIBLE);
        if(isChecked){
          selectionList.add(itemChecked);
          selectionNumber.setText("Selected " + selectionList.size() + "/" + playlist.getLength());
        }
        if(!isChecked){
            //selectionList.removeIf(index -> index.equals(itemChecked));
            selectionList.remove(itemChecked);
            selectionNumber.setText("Selected " + selectionList.size() + "/" + playlist.getLength());
        }
    }
}