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
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryPlaylist;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryFolders;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryPlaylists;
import eu.tutorial.androidapplicationfilesystem.classes.DialogPlaylistLibrary;
import eu.tutorial.androidapplicationfilesystem.classes.FastScrollRecyclerView.FastScrollRecyclerView;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
public class FragmentLibrarySongs extends Fragment{

    AdapterLibraryPlaylist adapterLibraryAllSongs, adapterLibraryFavorites, adapterPlaylistLibrary;
    AdapterLibraryFolders adapterLibraryFolders;
    AdapterLibraryPlaylists adapterLibraryPlaylists;

    FastScrollRecyclerView recyclerView;

    FragmentManager fragmentManager;
    Playlist requestedPlaylist;

    ArrayList<String> allSongsPaths;
    ArrayList<Playlist> playlists;

    String action;
    String playlistName;
    EditText searchBar;
    ViewModelMain viewModelMain;
    TextView libraryText;

    Boolean selectionMode;
    TextView selectionNumber;


    LinearLayout selectionAddToPlaylist;
    LinearLayout selectionDelete;
    LinearLayout selectionSetAlbumInfo;
    LinearLayout selectionRemoveFromPlaylist;
    LinearLayout selectionSetPlaylistCover;
    LinearLayout selectionRemovePlaylist;

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

        initViews(view);
        initViewModel();
        initListeners();

        return view;
    }


    private void hideViewsPlaylist(){
        selectionSetAlbumInfo.setVisibility(View.GONE);
        selectionRemovePlaylist.setVisibility(View.GONE);
    }

    private void hideViewsPlaylists(){
        selectionAddToPlaylist.setVisibility(View.GONE);
        selectionDelete.setVisibility(View.GONE);
        selectionSetAlbumInfo.setVisibility(View.GONE);
        selectionSetPlaylistCover.setVisibility(View.GONE);
        selectionRemoveFromPlaylist.setVisibility(View.GONE);
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.libraryRecyclerViewItems);
        libraryText = view.findViewById(R.id.librarySelectionText);

        libraryImage = view.findViewById(R.id.libraryImage);

        selectionBar =  view.findViewById(R.id.selectionBar);
        selectionBar.setVisibility(View.GONE);
        System.out.println("GONE");

        selectionNumber = view.findViewById(R.id.selectionNumber);

        selectionAddToPlaylist = view.findViewById(R.id.selectionAddToPlaylist);
        selectionDelete = view.findViewById(R.id.selectionDelete);

        selectionSetAlbumInfo = view.findViewById(R.id.selectionSetAlbumInfo);
        selectionRemoveFromPlaylist = view.findViewById(R.id.selectionRemoveFromPlaylist);
        selectionSetPlaylistCover = view.findViewById(R.id.selectionSetPlaylistCover);
        selectionRemovePlaylist = view.findViewById(R.id.selectionRemovePlaylist);

        switch (action){
            case("allSongs"):
                libraryText.setText("All Songs");
                hideViewsPlaylist();
                break;
            case("playlists"):
                libraryText.setText("Playlists");
                hideViewsPlaylists();
                break;
            case("favorites"):
                libraryText.setText("Favorites");
                hideViewsPlaylist();
                break;
            case("playlist"):
                libraryText.setText(playlistName);
                hideViewsPlaylist();
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
                switch (action){
                    case("allSongs"):
                        requestedPlaylist = viewModelMain.getPlaylist("All Songs");
                        adapterLibraryAllSongs.notifyDataSetChanged(); //Adapter will reconstruct the views on playlist LiveData changes in ViewModel
                        break;

                    case("playlists"):
                        playlists.clear();
                        playlists.addAll(playlistsViewModel);
                        adapterLibraryPlaylists.notifyDataSetChanged();
                        break;

                    case("favorites"):
                        requestedPlaylist = viewModelMain.getPlaylist("Favorites");
                        adapterLibraryFavorites.notifyDataSetChanged();
                        break;

                    case("playlist"):
                        requestedPlaylist = viewModelMain.getPlaylist(playlistName);
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

        playlists = viewModelMain.getPlaylists().getValue();
        playlists.sort(Playlist.titleComparator);

        switch (action){
            case("allSongs"):
                allSongsPaths = viewModelMain.getAllSongsPaths();
                requestedPlaylist = viewModelMain.getPlaylist("All Songs");
                libraryImage.setImageBitmap(requestedPlaylist.getPlaylistBitmap(getContext()));
                break;
            case("playlists"):
                libraryImage.setImageBitmap(playlists.get(0).getPlaylistBitmap(getContext()));
                break;
            case("favorites"):
                requestedPlaylist = viewModelMain.getPlaylist("Favorites");
                libraryImage.setImageBitmap(requestedPlaylist.getPlaylistBitmap(getContext()));
                break;
            case("playlist"):
                requestedPlaylist = viewModelMain.getPlaylist(playlistName);
                libraryImage.setImageBitmap(requestedPlaylist.getPlaylistBitmap(getContext()));
                break;
            case("folders"):
                break;
            default:
                break;

        }
    }


    private void initListeners() {
        selectionAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectionList!=null && selectionList.size()!=0){
                    DialogPlaylistLibrary dialogPlaylistLibrary = new DialogPlaylistLibrary();
                    dialogPlaylistLibrary.createDialog(requireActivity(),viewModelMain.getPlaylists().getValue(),createSelectionArray());
                }
            }
        });


        selectionRemoveFromPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModelMain.removeSongList(createSelectionArray(),requestedPlaylist.getPlaylistName());
                switch (action){
                    case("allSongs"):
                        for(Integer index:selectionList){
                            adapterLibraryAllSongs.notifyItemRemoved(index);
                        }
                        break;
                    case("playlists"):
                        break;
                    case("favorites"):
                        for(Integer index:selectionList){
                            adapterLibraryFavorites.notifyItemRemoved(index);
                        }
                        //adapterLibraryFavorites.notifyDataSetChanged();
                        break;
                    case("playlist"):
                        for(Integer index:selectionList){
                            adapterPlaylistLibrary.notifyItemRemoved(index);
                        }
                        //adapterPlaylistLibrary.notifyDataSetChanged();
                        break;
                    case("folders"):

                        break;
                    default:
                        break;
                }
            }
        });

        selectionRemovePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModelMain.removePlaylistList(selectionList,playlists);

                switch (action){
                    case("allSongs"):
                        //adapterLibraryAllSongs.notifyDataSetChanged();
                        break;
                    case("playlists"):
                        for(Integer index:selectionList){
                            adapterLibraryPlaylists.notifyItemRemoved(index);
                        }
                        //adapterLibraryPlaylists.notifyDataSetChanged();
                        break;
                    case("favorites"):
                        //adapterLibraryFavorites.notifyDataSetChanged();
                        break;
                    case("playlist"):
                        //adapterPlaylistLibrary.notifyDataSetChanged();
                        break;
                    case("folders"):

                        break;
                    default:
                        break;
                }

            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViews();
        //selectionBar.setVisibility(View.GONE);
    }

    private void initRecyclerViews(){
        switch (action){
            case("allSongs"):
                adapterLibraryAllSongs = new AdapterLibraryPlaylist(this, allSongsPaths, requestedPlaylist);
                //adapterLibraryAllSongs.setHasStableIds(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryAllSongs);
                break;
            case("playlists"):
                adapterLibraryPlaylists = new AdapterLibraryPlaylists(this,playlists);
                //adapterLibraryPlaylists.setHasStableIds(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryPlaylists);
                break;
            case("favorites"):
                adapterLibraryFavorites = new AdapterLibraryPlaylist(this,null,requestedPlaylist);
                //adapterLibraryFavorites.setHasStableIds(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterLibraryFavorites);
                break;
            case("playlist"):
                adapterPlaylistLibrary = new AdapterLibraryPlaylist(this,null,requestedPlaylist);
                //adapterPlaylistLibrary.setHasStableIds(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterPlaylistLibrary);
                break;
            case("folders"):
                adapterLibraryFolders = new AdapterLibraryFolders(getContext(),allSongsPaths, requestedPlaylist);
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
        selectionList = null;
        selectionList = new ArrayList<>();
        selectionMode = false;
        selectionBar.setVisibility(View.GONE);


        switch (action){
            case("allSongs"):
                adapterLibraryAllSongs.disableSelectMode();
                adapterLibraryAllSongs.notifyDataSetChanged();
                break;
            case("playlists"):
                adapterLibraryPlaylists.disableSelectionMode();
                adapterLibraryPlaylists.notifyDataSetChanged();
                break;
            case("favorites"):
                adapterLibraryFavorites.disableSelectMode();
                adapterLibraryFavorites.notifyDataSetChanged();
                break;
            case("playlist"):
                adapterPlaylistLibrary.disableSelectMode();
                adapterPlaylistLibrary.notifyDataSetChanged();
                break;
            case("folders"):

                break;
            default:
                break;
        }
    }

    public void selectAll(){
        selectionList = new ArrayList<>();
        for(int i = 0; i < requestedPlaylist.getLength(); i++){
            selectionList.add(i);
        }
    }

    public ArrayList<MusicData> createSelectionArray(){
        ArrayList<MusicData> selectedSongs = new ArrayList<>();
        for(Integer index:selectionList){
               selectedSongs.add(requestedPlaylist.getSong(index));
        }
        return selectedSongs;
    }

    public boolean selectedListContains(Integer position){
        System.out.println(selectionList.contains(position) + "Position");
        return selectionList.contains(position);

    }

    @SuppressLint("SetTextI18n")
    public void onChecked(Boolean isChecked, Integer itemChecked) {
        boolean selectionWasEnabled = selectionMode;
        selectionMode = true;
        passMusicStatus.onRequestNavbar(false,this);

        switch (action){
            case("allSongs"):
                if(isChecked){
                    selectionList.add(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                if(!isChecked){
                    selectionList.remove(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                break;
            case("playlists"):
                if(isChecked){
                    selectionList.add(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + playlists.size());
                }
                if(!isChecked){
                    selectionList.remove(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + playlists.size());
                }
                break;
            case("favorites"):
                if(isChecked){
                    selectionList.add(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                if(!isChecked){
                    selectionList.remove(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                break;
            case("playlist"):
                if(isChecked){
                    selectionList.add(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                if(!isChecked){
                    selectionList.remove(itemChecked);
                    selectionNumber.setText("Selected " + selectionList.size() + "/" + requestedPlaylist.getLength());
                }
                break;
            case("folders"):
                break;
            default:
                break;
        }

        if(!selectionWasEnabled){
            selectionBar.setVisibility(View.VISIBLE);
            switch (action){
                case("allSongs"):
                    adapterLibraryAllSongs.notifyDataSetChanged();
                    break;
                case("playlists"):
                    adapterLibraryPlaylists.notifyDataSetChanged();
                    break;
                case("favorites"):
                    adapterLibraryFavorites.notifyDataSetChanged();
                    break;
                case("playlist"):
                    adapterPlaylistLibrary.notifyDataSetChanged();
                    break;
                case("folders"):
                    break;
                default:
                    break;
            }

        }
    }


}