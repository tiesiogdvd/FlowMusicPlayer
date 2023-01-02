package eu.tutorial.androidapplicationfilesystem.classes;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class ViewModelMain extends AndroidViewModel{


    private MutableLiveData<ArrayList<Playlist>> playlists;
    private String lastSongPath;
    PlaylistRepository myDB;

    public ViewModelMain(@NonNull Application application) {
        super(application);
        myDB = new PlaylistRepository(getApplication().getBaseContext());
        setPlaylists();
    }

    private void setPlaylists(){
        playlists = myDB.getFullPlaylistsData();
    }

    public void addPlaylist(String playlistName, String date){
        myDB.addPlaylist(playlistName, date);
        ArrayList <Playlist> playlistsAdd = playlists.getValue();
        playlistsAdd.add(new Playlist(playlistName,date));
        playlists.setValue(playlistsAdd);
    }

    public void addPlaylist(Playlist playlist){
        myDB.addPlaylist(playlist.getPlaylistName(),playlist.getDate());
        ArrayList <Playlist> playlistsAdd = playlists.getValue();
        playlistsAdd.add(playlist);
        playlists.setValue(playlistsAdd);
    }

    public void addSong(String path, String playlistName, String date){
        ArrayList <Playlist> playlistsAddSong = playlists.getValue();
        for(Playlist playlist: playlistsAddSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.addSong(path,date);
                myDB.addSong(path, playlistName, date);
                break;
            }
        }
        playlists.setValue(playlistsAddSong);
    }

    public void addSong(String path, String playlistName){
        ArrayList <Playlist> playlistsAddSong = playlists.getValue();
        for(Playlist playlist: playlistsAddSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.addSong(path);
                myDB.addSong(path, playlistName, playlist.getSongDate(path));
                break;
            }
        }
        playlists.setValue(playlistsAddSong);
    }

    public void removeSong(String path, String playlistName){
        myDB.removeSong(path, playlistName);
        ArrayList <Playlist> playlistsRemoveSong = playlists.getValue();
        for(Playlist playlist: playlistsRemoveSong){
            if(playlist.getPlaylistName().equals(playlistName)){
                playlist.removeSong(path);
                break;
            }
        }
        playlists.setValue(playlistsRemoveSong);
    }




    private void setPlaylists1(){
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

    public LiveData<ArrayList<Playlist>> getPlaylists(){
        return playlists;
    }


}
