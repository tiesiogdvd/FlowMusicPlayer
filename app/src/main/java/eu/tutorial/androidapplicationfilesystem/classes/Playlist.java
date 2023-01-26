package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Playlist implements Parcelable{

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    String playlistName;
    ArrayList <MusicData> songs;
    String date;
    Bitmap bitmap;

    public Playlist(String playlistName, ArrayList<MusicData> songs, Context context) {
        songs = new ArrayList<>();
        this.playlistName = playlistName;
        this.songs = songs;
        this.date = setInitialDate();
        setPlaylistBitmap(context);
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

    public Playlist () {
        songs = new ArrayList<>();
        this.date = setInitialDate();
    }

    protected Playlist(Parcel in) {
        playlistName = in.readString();
        date = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }


    public static Comparator<Playlist> titleComparator = new Comparator<Playlist>() {
        @Override
        public int compare(Playlist o1, Playlist o2) {
            String title1 = o1.getPlaylistName().toLowerCase();
            String title2 = o2.getPlaylistName().toLowerCase();
            return title1.compareTo(title2);
        }
    };

    public void setSong (int index){
        if(songs.get(index)==null){
            songs.add(index,null);
        }
    }

    public void resetPlaylistBitmap(Context context){
        bitmap = null;
        retrieveBitmap(context);
    }

    public void setPlaylistBitmap(Context context){
        if(bitmap == null) {
            retrieveBitmap(context);
        }
    }



    public Bitmap getPlaylistBitmap(Context context){
        setPlaylistBitmap(context);
        return bitmap;
    }

    public void retrieveBitmap(Context context){
        for (MusicData s : songs) {
            bitmap = s.getBitmapQuick(context);
            if (bitmap != null) {
                break;
            }
        }
    }


    private int getSongPosition(String path){
        for(int i=0; i<songs.size(); i++){
            if(songs.get(i).getPath().equals(path)){
                return i;
            }
        }
        return -1;
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
        int index = getSongPosition(path);
        return songs.get(index).getDate();
        /*for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                return s.getDate();
            }
        }
        return "";*/
    }

    public ArrayList<MusicData> getSongsArray() {
        return songs;
    }

    public void setSongsArray(ArrayList <MusicData> songs){
        this.songs = songs;
    }

    public String getSongPath(int position){
        if(songs!=null && position!=songs.size()){
            return songs.get(position).getPath();
        }else{
            return null;
        }
    }

    public int getLength(){
        if(songs!=null){return songs.size();}
        else{return 0;}
    }

    public int getIndex(String path){
        for (MusicData song:songs) {
            for(int i=0; i<songs.size(); i++){
                if(songs.get(i).getPath().equals(path)){
                    return i;
                }
            }
        }
        return -1;
    }

    public MusicData getSong(int index){
        System.out.println(index);
        System.out.println(songs.size());
        if (!(songs.size()<=index)) {
            return songs.get(index);
        }else{
            return null;
        }
    }

    public MusicData getSong(String path){
        int index = getSongPosition(path);
        if(index!=-1){
            return songs.get(index);
        }else{
            return null;
        }
    }


    public boolean inPlaylist(String path) {
        boolean matches = false;
        int index = getSongPosition(path);
        return index != -1;
    }


    public void addSong(String path, String date, Context context){
        songs.add(new MusicData(path, date));
        setPlaylistBitmap(context);
    }
    public void addSong(String path, Context context){
        songs.add(new MusicData(path, context));
        setPlaylistBitmap(context);
    }


    public void addSong(String path, String title, String artist, String album, int lengthInt, Context context){
        System.out.println(path);
        songs.add(new MusicData(path, title, artist, album,lengthInt,context));
        //setPlaylistBitmap(context);
    }

    public void addSong(String path){
        songs.add(new MusicData(path));
    }

    public void addSong(MusicData song){
        songs.add(song);
    }


    public boolean removeSong(String path, Context context) {
        boolean deleted = false;
        for(MusicData s: songs){
            if (path.equals(s.getPath())) {
                deleted = true;
                songs.remove(s);
                break;
            }
        }
        resetPlaylistBitmap(context);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playlistName);
        dest.writeString(date);
        dest.writeParcelable(bitmap, flags);
    }
}
