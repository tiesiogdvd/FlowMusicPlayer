package eu.tutorial.androidapplicationfilesystem.classes;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


//This class implements storing music data and creates methods to retrieve Metadata from it

public class MusicDataMetadata {
    public String title;
    public String artist;
    public String album;
    public Bitmap bitmap;
    public int length;
    public String lengthString;
    public MediaMetadataRetriever mr;

    public MusicDataMetadata(){
        mr = new MediaMetadataRetriever();
    }

    public void setAllData(String path){
        //MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr = new MediaMetadataRetriever();
        mr.setDataSource(path);

        try{
            title = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if(title==null){throw new Exception();}
        }catch(Exception e){
            title = path.substring(path.lastIndexOf("/")).replace("/","");
        }

        try{
            artist = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }catch(Exception e){

        }

        try{
            album = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        }catch(Exception e){

        }

        try{
            byte [] data = mr.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }catch(Exception e){

        }

        try{
            length = Integer.parseInt(mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            lengthString = convertTimeFormat(length);
        }catch(Exception e){

        }
        mr.release();
    }



    //This method required a file to be provided
    public static String getTitle(File file){
        String path = file.getAbsolutePath();
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String title = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            mr.release();
            if(title==null){throw new Exception();}
            return title;
        }catch(Exception e){
            mr.release();
            String title = path.substring(path.lastIndexOf("/")).replace("/","");
            return title;
        }
    }

    public static String getTitle(String path){
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String title = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            mr.release();
            if(title==null){throw new Exception();}
            return title;
        }catch(Exception e){
            mr.release();
            String title = path.substring(path.lastIndexOf("/")).replace("/","");
            return title;
        }
    }


    public static String getArtist(File file){
        String path = file.getAbsolutePath();
        MediaStore.Audio.Albums.getContentUri(file.getAbsolutePath());
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String artist = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            mr.release();
            return artist;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }

    public static String getArtist(String path){
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String artist = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            mr.release();
            return artist;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }

    public static String getAlbum(File file){
        String path = file.getAbsolutePath();
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String album = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            mr.release();
            return album;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }

    public static String getAlbum(String path){
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            String album = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            mr.release();
            return album;
        }catch(Exception e){
            mr.release();
            return null;
        }
    }


    public static Bitmap getBitmap(String path){
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
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

    public static Bitmap getBitmap(File file){
        String path = file.getAbsolutePath();
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
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

    public static String getLengthString(String path) {
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            int length = Integer.parseInt(mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mr.release();
            String lengthString = convertTimeFormat(length);
            return lengthString;
        }catch(Exception e){
            mr.release();
            return "";
        }
    }

    public static String getLengthString(File file) {
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            int length = Integer.parseInt(mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mr.release();
            String lengthString = convertTimeFormat(length);
            return lengthString;
        }catch(Exception e){
            mr.release();
            return "";
        }
    }

    @SuppressLint("DefaultLocale")
    private static String convertTimeFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        (TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    public static int getLengthInt(String path) {
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(path);
        try{
            int length = Integer.parseInt(mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mr.release();
            return length;
        }catch(Exception e){
            mr.release();
            return 0;
        }
    }

    public static int getLengthInt(File file) {
        MediaMetadataRetriever mr = new MediaMetadataRetriever();
        mr.setDataSource(file.getAbsolutePath());
        try{
            int length = Integer.parseInt(mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mr.release();
            return length;
        }catch(Exception e){
            mr.release();
            return 0;
        }
    }



}
