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
    MediaMetadataRetriever mr;
    File file;
    String date;

    public MusicData(){
    }

    public MusicData(File file){
        this.file = file;
        this.path = file.getAbsolutePath();
        this.title = getTitle(file);
        this.artist = getArtist(file);
        this.album = getAlbum(file);
        this.bmap = getBitmap(file);
        this.date = setInitialDate();
    }

    public MusicData(String path){
        File file = new File(path);
        this.file = file;
        this.path = file.getAbsolutePath();
        this.title = getTitle(file);
        //System.out.println(title);
        this.artist = getArtist(file);
        this.album = getAlbum(file);
        this.bmap = getBitmap(file);
        this.date = setInitialDate();
    }

    public MusicData(File file, String date){
        this.file = file;
        this.path = file.getAbsolutePath();
        this.title = getTitle(file);
        this.artist = getArtist(file);
        this.album = getAlbum(file);
        this.bmap = getBitmap(file);
        this.date = date;
    }

    public MusicData(String path, String date){

        File file = new File(path);
        this.file = file;
        this.path = file.getAbsolutePath();
        this.title = getTitle(file);
        //System.out.println(title);
        this.artist = getArtist(file);
        this.album = getAlbum(file);
        this.bmap = getBitmap(file);
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    //This method required a file to be provided
    public String getTitle(File file){
        path = file.getAbsolutePath();
        mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            title = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            mr.release();
            if(title==null){throw new Exception();}
            return title;
        }catch(Exception e){
            mr.release();
            title = path.substring(path.lastIndexOf("/")).replace("/","");
            return title;
        }
    }

    //This method is only used if MusicData(File file) constructor has been called
    public String getTitle(){
        return title;
    }

    public String getArtist(File file){
        path = file.getAbsolutePath();
        mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            artist = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            mr.release();
            return artist;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }
    public String getArtist(){
        return artist;
    }

    public String getAlbum(File file){
        path = file.getAbsolutePath();
        mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            album = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            mr.release();
            return album;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }

    public String getAlbum(){
        return album;
    }

    public Bitmap getBitmap(File file){
        path = file.getAbsolutePath();
        mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            byte [] data = mr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mr.release();
            return bitmap;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }

    public Bitmap getBitmap(){
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
