package eu.tutorial.androidapplicationfilesystem.classes;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Playlist {
    String playlistName;
    ArrayList <MusicData> songs;
    String date;

    public Playlist(String playlistName, ArrayList<MusicData> songs) {
        songs = new ArrayList<>();
        this.playlistName = playlistName;
        this.songs = songs;
        this.date = setInitialDate();
    }
    public Playlist (String playlistName) {
        songs = new ArrayList<>();
        this.playlistName = playlistName;
        this.date = setInitialDate();
    }
    public Playlist (String playlistName, String date) {
        songs = new ArrayList<>();
        this.playlistName = playlistName;
        this.date = date;
    }


    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getDate(){
        return date.toString();
    }

    public String getSongDate(String path){
        for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                return s.getDate();
            }
        }
        return "";
    }

    public ArrayList<MusicData> getSongsArray() {
        return songs;
    }

    public void printSongsArray(){
        for(MusicData s: songs){
            System.out.println(s.getPath());
        }
    }

    public void setSongsArray(ArrayList <MusicData> songs){
        this.songs = songs;
    }

    public String getSongPath(int position){
        return songs.get(position).getPath();
    }

    public int getLength(){
        return songs.size();
    }

    public boolean inPlaylist(String path) {
        boolean matches = false;
        for(MusicData s: songs){
            //System.out.println(s.getPath()+" "+path);
            if (path.equals(s.getPath())) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    public boolean inPlaylist(File file) {
        String path = file.getAbsolutePath();
        boolean matches = false;
        for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    public void addSong(File file){
        songs.add(new MusicData(file));
    }

    public void addSong(File file, String date){
        songs.add(new MusicData(file, date));
    }
    public void addSong(String path, String date){
        songs.add(new MusicData(path, date));
    }
    public void addSong(String path){
        songs.add(new MusicData(path));
    }

    public boolean removeSong(String path) {
        boolean deleted = false;
        for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                deleted = true;
                songs.remove(s);
                break;
            }
        }
        return deleted;
    }

    public boolean removeSong(File file) {
        String path = file.getAbsolutePath();
        boolean deleted = false;
        for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                songs.remove(s);
                deleted = true;
                for(int i=0; i<5; i++){System.out.println(deleted);}
                break;
            }
        }
        return deleted;
    }

    private String setInitialDate(){
        Date date = new Date();
        String dateFormat = "dd/MM/Y hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDate = sdf.format(date);
        System.out.println(setDateJavaUtil(formattedDate));
        return formattedDate;
    }

    private Date setDateJavaUtil(String dateString){

        String dateFormat = "dd/MM/Y hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
