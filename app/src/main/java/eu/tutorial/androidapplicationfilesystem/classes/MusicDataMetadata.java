package eu.tutorial.androidapplicationfilesystem.classes;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//This class implements storing music data and creates methods to retrieve Metadata from it

public class MusicDataMetadata {

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

}
