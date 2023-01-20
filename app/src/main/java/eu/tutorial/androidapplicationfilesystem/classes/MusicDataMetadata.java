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
            lengthString = TypeConverter.formatDuration(length);
        }catch(Exception e){

        }
        mr.release();
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
}
