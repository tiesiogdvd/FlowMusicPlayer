package eu.tutorial.androidapplicationfilesystem.classes;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//This class implements storing music data and creates methods to retrieve Metadata from it

public class MusicData {
    String path;
    String cachedImagePath;
    String title;
    String artist;
    String album;
    int lengthInt;
    Bitmap bmap;
    File file;
    String date;


    public void initConstructorData(File file){
        this.file = file;
        this.path = file.getAbsolutePath();
        MusicDataMetadata.setAllData(path);
        this.title = MusicDataMetadata.title;
        this.artist = MusicDataMetadata.artist;
        this.album = MusicDataMetadata.album;
        this.bmap = MusicDataMetadata.bitmap;
        this.lengthInt = MusicDataMetadata.length;
        MusicDataMetadata.title = null;
        MusicDataMetadata.artist = null;
        MusicDataMetadata.album = null;
        MusicDataMetadata.bitmap = null;
        MusicDataMetadata.length = -1;
        MusicDataMetadata.lengthString = null;
        //MusicDataMetadata.mr.release();
    }

    public void initConstructorData(String path){
        this.path = path;
        MusicDataMetadata.setAllData(path);
        this.title = MusicDataMetadata.title;
        this.artist = MusicDataMetadata.artist;
        this.album = MusicDataMetadata.album;
        if(this.bmap==null) {
            this.bmap = MusicDataMetadata.bitmap;
        }
        this.lengthInt = MusicDataMetadata.length;
        MusicDataMetadata.title = null;
        MusicDataMetadata.artist = null;
        MusicDataMetadata.album = null;
        MusicDataMetadata.bitmap = null;
        MusicDataMetadata.length=-1;
        MusicDataMetadata.lengthString = null;
    }

    public MusicData(){
        this.date = setInitialDate();
    }

    public MusicData(File file, Context context){
        initConstructorData(file.getAbsolutePath());
        this.date = setInitialDate();
        ImageSaver imageSaver = new ImageSaver(context);
        imageSaver.setFileName(file.getAbsolutePath()).setDirectory("ImageCache").save(bmap);
        initConstructorData(file);
        this.date = setInitialDate();
    }

    public MusicData(String path, Context context){
        //File file = new File(path);
        this.date = setInitialDate();
        ImageSaver imageSaver = new ImageSaver(context);
        this.bmap = imageSaver.setFileName(path).setDirectory("ImageCache").load();
        initConstructorData(path);

        this.cachedImagePath = imageSaver.setFileName(path).setDirectory("ImageCache").save(bmap);
    }

    public Bitmap loadBmap(Context context){
        ImageSaver imageSaver = new ImageSaver(context);
        return imageSaver.setFileName(path).setDirectory("ImageCache").load();
    }

    public String getCachedImagePath() {
        return cachedImagePath;
    }

    public void setCachedImagePath(String cachedImagePath) {
        this.cachedImagePath = cachedImagePath;
    }

    public MusicData(File file, String date){
        initConstructorData(file);
        this.date = date;
    }

    public MusicData(String path, String date){
        initConstructorData(path);
        this.date = date;
    }

    public MusicData(String path, String title, String artist, String album, int lengthInt, String date){
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.lengthInt = lengthInt;
        this.date = date;
    }

    public MusicData(String path, String title, String artist, String album, int lengthInt){
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.lengthInt = lengthInt;
        this.date = setInitialDate();
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
        //this.bmap = MusicDataMetadata.getBitmap(new File(path));
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

    public int getLength() {
        return lengthInt;
    }
}
