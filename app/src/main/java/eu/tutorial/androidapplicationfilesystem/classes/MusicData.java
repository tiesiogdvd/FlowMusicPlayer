package eu.tutorial.androidapplicationfilesystem.classes;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.io.File;
import java.util.Date;


//This class implements storing music data and creates methods to retrieve Metadata from it

public class MusicData {
    String path;
    String artist;
    String title;
    String album;
    Bitmap bmap;
    File file;
    String date;


    public void initConstructorData(File file){
        this.file = file;
        this.path = file.getAbsolutePath();
        this.title = MusicDataMetadata.getTitle(file);
        this.artist = MusicDataMetadata.getArtist(file);
        this.album = MusicDataMetadata.getAlbum(file);
    }

    public void initConstructorData(String path){
        this.path = path;
        this.title = MusicDataMetadata.getTitle(path);
        this.artist = MusicDataMetadata.getArtist(path);
        this.album = MusicDataMetadata.getAlbum(path);
    }

    public MusicData(){
    }

    public MusicData(File file){
        initConstructorData(file);
        this.date = setInitialDate();
    }

    public MusicData(String path){
        //File file = new File(path);
        initConstructorData(path);
        this.date = setInitialDate();
    }

    public MusicData(File file, String date){
        initConstructorData(file);
        this.date = date;
    }

    public MusicData(String path, String date){

        //File file = new File(path);
        //initConstructorData(file);
        initConstructorData(path);
        this.date = date;
    }

    public String getPath() {
        System.out.println(path);
        return path;
    }


    //This method is only used if MusicData(File file) constructor has been called
    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum(){
        return album;
    }

    public Bitmap getBitmap(){
        this.bmap = MusicDataMetadata.getBitmap(new File(path));
        return bmap;
    }

    public String getDate(){
        return date;
    }

    private String setInitialDate(){
        Date date = new Date();
        String dateFormat = "dd/MM/Y hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDate = sdf.format(date);
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
