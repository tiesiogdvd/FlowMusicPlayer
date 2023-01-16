package eu.tutorial.androidapplicationfilesystem.classes;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class ViewModelMain extends AndroidViewModel{


    private MutableLiveData<ArrayList<Playlist>> playlists;
    private Playlist allSongs;
    String rootPath = "storage/emulated/0";
    private final ArrayList<String> allSongsPaths;
    private String lastSongPath;
    private String lastFragment;
    private Boolean barStatus;
    private Boolean isLoaded;
    private boolean firstLoad = true;
    String allSongPlaylistTag;
    PlaylistRepository myDB;

    MutableLiveData <Playlist> currentSource;
    MutableLiveData <Integer> currentIndex;

    public ViewModelMain(@NonNull Application application) {
        super(application);
        myDB = new PlaylistRepository(getApplication().getBaseContext());
        playlists = new MutableLiveData<>();
        allSongsPaths = new ArrayList<>();
        allSongPlaylistTag = "All Songs";
        isLoaded = false;
        lastFragment = "";
        barStatus = true;
        currentSource = new MutableLiveData<>();
        currentIndex = new MutableLiveData<>();
        SharedPreferences sh = application.getSharedPreferences("lastMusic",MODE_PRIVATE);
    }

    public void setPlaylists1(){
        playlists = myDB.getFullPlaylistsData();
        playlists.setValue(playlists.getValue());
        isLoaded = true;

        String lastSongSource = Settings.getLastSongSource();
        Integer lastSongIndex = Settings.getLastSongIndex();
        Playlist playlist = getPlaylist(lastSongSource);

        currentSource.setValue(playlist);
        currentIndex.setValue(lastSongIndex);

    }

    public boolean isLoaded(){
        return isLoaded;
    }

    public void getSources(){

    }

    public void addPlaylist(String playlistName, String date){
        myDB.addPlaylist(playlistName, date);
        ArrayList <Playlist> playlistsAdd = playlists.getValue();
        if (playlistsAdd==null){playlistsAdd = new ArrayList<>();}
        playlistsAdd.add(new Playlist(playlistName,date));
        playlists.setValue(playlistsAdd);
    }

    public void addPlaylist(Playlist playlist){
        myDB.addPlaylist(playlist.getPlaylistName(),playlist.getDate());
        ArrayList <Playlist> playlistsAdd = playlists.getValue();
        if (playlistsAdd==null){playlistsAdd = new ArrayList<>();}
        playlistsAdd.add(playlist);
        //System.out.println("B:LEDFED:E:");
        playlists.setValue(playlistsAdd);
    }

    public void addSong(String path, String playlistName, String date){
        ArrayList <Playlist> playlistsAddSong = playlists.getValue();
        for(Playlist playlist: playlistsAddSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.addSong(path,date,getApplication().getBaseContext());
                MusicData song = playlist.getSong(path);
                myDB.addSong(playlistName,path,song.getTitle(),song.getArtist(),song.getAlbum(),song.getLength(),date);
                break;
            }
        }
        playlists.postValue(playlistsAddSong);
    }

    public void addSong(String path, String playlistName, Context context){
        ArrayList <Playlist> playlistsAddSong = playlists.getValue();
        for(Playlist playlist: playlistsAddSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.addSong(path,context);
                MusicData song = playlist.getSong(path);
                myDB.addSong(playlistName,path,song.getTitle(),song.getArtist(),song.getAlbum(),song.getLength(),song.getDate());
                break;
            }
        }
        playlists.postValue(playlistsAddSong);
    }



    public void addSong(SongDataTest song, String playlistName){
        ArrayList <Playlist> playlistsAddSong = playlists.getValue();
        for(Playlist playlist: playlistsAddSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.addSong(song.getPath(),song.getSongTitle(),song.getSongArtist(),song.getAlbum(),song.getDuration(),getApplication().getBaseContext());
                myDB.addSong(playlistName,song.getPath(),song.getSongTitle(),song.getSongArtist(),song.getAlbum(),song.getDuration(),new Date().toString());
                break;
            }
        }
        playlists.postValue(playlistsAddSong);
    }



    public void removeSong(String path, String playlistName){
        myDB.removeSong(path, playlistName);
        ArrayList <Playlist> playlistsRemoveSong = playlists.getValue();
        for(Playlist playlist: playlistsRemoveSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.removeSong(path, getApplication().getBaseContext());
                break;
            }
        }
        playlists.postValue(playlistsRemoveSong);
    }


    private void setPlaylists(){
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                //playlists = myDB.getFullPlaylistsData();
                playlists = myDB.getFullPlaylistsData();
            }
        };
        Thread thread = new Thread(runnable1);
        System.out.println(thread.getName());
        thread.start();
        thread.interrupt();
    }


    public ArrayList<String> getAllSongsPaths(){
        return allSongsPaths;
    }

    private void setAllSongsPaths(){
        StorageScraper.searchStorage(rootPath,allSongsPaths);
    }

    private void setAllSongsMetadata(Context context){
        setAllSongsPaths();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (String path:allSongsPaths) {
                    //System.out.println(path);
                    //allSongs.addSong(path, context);
                    addSong(path,allSongPlaylistTag,context);
                }
                ArrayList<Playlist>tempPlaylist = playlists.getValue();
                tempPlaylist.add(allSongs);
                playlists.postValue(tempPlaylist);
                Settings.setLastLoadingFinished(true);
            }
        };
        Thread thread = new Thread(runnable);
        //System.out.println(thread.getName());
        thread.start();
        thread.interrupt();

    }

    public Playlist getAllSongsMetadata(){
        return allSongs;
    }

    public Playlist getPlaylist(String requestedPlaylist) {
        ArrayList<Playlist> tempPlaylists = playlists.getValue();
        if (tempPlaylists != null) {
            for (Playlist playlist : tempPlaylists) {
                if (playlist.getPlaylistName().equals(requestedPlaylist)) {
                    playlist.getSongsArray().sort(MusicData.titleComparator);
                    return playlist;
                }
            }
        }
        return null;
    }

    public LiveData<ArrayList<Playlist>> getPlaylists(){
        return playlists;
    }

    public void setLastFragment(String lastFragment){
        this.lastFragment = lastFragment;
    }

    public String getLastFragment(){
        return  lastFragment;
    }

    public void setBarStatus(Boolean barStatus){
        this.barStatus = barStatus;
    }

    public Boolean getBarStatus(){
        return barStatus;
    }

    public void loadDataAndCache() {
        Settings.setLastLoadingFinished(false);
        Date date = new Date();
        addPlaylist(allSongPlaylistTag, date.toString());
        setAllSongsMetadata(getApplication().getBaseContext());
    }

    public void loadDataInit(){

        Settings.setLastLoadingFinished(false);
        addPlaylist(allSongPlaylistTag, new Date().toString());
        ArrayList<SongDataTest> musicInfo = SongDataGetMusicInfo.getMusicInfo(getApplication().getBaseContext());

        for (SongDataTest song: musicInfo) {
           addSong(song, allSongPlaylistTag);
        }

        Settings.setLastLoadingFinished(true);
    }

    public Boolean getFirstLoad() {
        return firstLoad;
    }

    public void setFirstLoad(Boolean firstLoad) {
        this.firstLoad = firstLoad;
    }




    public MutableLiveData<Playlist> getCurrentSource() {
        return currentSource;
    }

    public MutableLiveData<Integer> getCurrentIndex() {
        return currentIndex;
    }


    public void setCurrentSource(Playlist currentSource, Integer index) {
        this.currentSource.setValue(currentSource);
        this.currentIndex.setValue(index);
        Settings.setLastSongIndex(index);
        Settings.setLastSongSource(currentSource.getPlaylistName());
    }

    public void setCurrentSourcePlaylist(Playlist currentSource) {
        this.currentSource.setValue(currentSource);
        Settings.setLastSongSource(currentSource.getPlaylistName());
    }

    public void setCurrentSourceInteger(Integer index) {
        if (index != null) {
            System.out.println("YEAAA");
            this.currentIndex.setValue(index);
            Settings.setLastSongIndex(index);
        }
    }

}
