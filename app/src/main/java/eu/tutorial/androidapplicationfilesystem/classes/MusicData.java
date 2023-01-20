package eu.tutorial.androidapplicationfilesystem.classes;


import android.content.AsyncQueryHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import androidx.loader.content.AsyncTaskLoader;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


//This class implements storing music data and creates methods to retrieve Metadata from it

public class MusicData{
    String path;
    String title;
    String artist;
    String album;
    int lengthInt;
    Bitmap bmap;
    String date;
    boolean triedToRetrieveBitmap = false;


    public void initConstructorData(String path){
        this.path = path;
        MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
        musicDataMetadata.setAllData(path);
        this.title = musicDataMetadata.title;
        this.artist = musicDataMetadata.artist;
        this.album = musicDataMetadata.album;
        this.bmap = musicDataMetadata.bitmap;
        this.lengthInt = musicDataMetadata.length;
        triedToRetrieveBitmap = true;
    }

    public MusicData(){
        this.date = setInitialDate();
    }


    //Mainly reserved for use with recurcive implementation on music load
    //Usually on first application laod or when MetaData needs retrieving with only path name
    public MusicData(String path, Context context){
        this.date = setInitialDate();
        initConstructorData(path);
        //Checks if imageCacheOnLoad setting is enabled
        if(Settings.getSaveImageCache() && bmap!=null) {
            //getBitmap(context);
            setBmapCache(context, bmap);
            bmap=null;
        }
    }

    public static Comparator<MusicData> titleComparator = new Comparator<MusicData>() {
        @Override
        public int compare(MusicData o1, MusicData o2) {
            String title1 = o1.getTitle().toLowerCase();
            String title2 = o2.getTitle().toLowerCase();
            //ascending order
            return title1.compareTo(title2);

            //descending order
            //return title2.compareTo(title1);
        }
    };

    public static Comparator<MusicData> artistComparator = new Comparator<MusicData>() {
        @Override
        public int compare(MusicData o1, MusicData o2) {
            String artist1 = o1.getArtist().toLowerCase();
            String artist2 = o2.getArtist().toLowerCase();
            //ascending order
            return artist1.compareTo(artist2);

            //descending order
            //return title2.compareTo(title1);
        }
    };

    public static Comparator<MusicData> addedComparator = new Comparator<MusicData>() {
        @Override
        public int compare(MusicData o1, MusicData o2) {
            String date1 = o1.getDate().toLowerCase();
            String date2 = o2.getDate().toLowerCase();
            //ascending order
            return date1.compareTo(date2);

            //descending order
            //return title2.compareTo(title1);
        }
    };

    public static Comparator<MusicData> lengthComparator = new Comparator<MusicData>() {
        @Override
        public int compare(MusicData o1, MusicData o2) {
            int length1 = o1.getLength();
            int length2 = o2.getLength();
            //ascending order
            return Integer.compare(length1, length2);

            //descending order
            //return title2.compareTo(title1);
        }
    };

    /*public Bitmap getBitmap(Context context){
        //this.bmap = MusicDataMetadata.getBitmap(new File(path));
        if(bmap!=null){
            return bmap;
        }

        if(!triedToRetrieveBitmap){
            if(Settings.getSaveImageCache()){
                bmap = loadBmap(context);
                if(bmap == null){
                    if(!new ImageSaver(context).setFileName(path).setDirectory("ImageCache").cacheExists()){
                        bmap = getBmap();
                        setBmapCache(context);
                    }

                    //getBmap();
                    //setBmapCache(context);
                    return bmap;
                }
            }
            if(!Settings.getSaveImageCache()){
              return getBmap();
            }
            triedToRetrieveBitmap=true;
        }
        return bmap;
    }*/

    public Bitmap getBitmap(Context context){
        //this.bmap = MusicDataMetadata.getBitmap(new File(path));
        if(bmap!=null){
            return bmap;
        }
        if(Settings.getSaveImageCache()){
            bmap=null;
            triedToRetrieveBitmap=false;
                if(!new ImageSaver(context).setFileName(path).setDirectory("ImageCache").cacheExists()){
                    setBmapCache(context,getBmap());
                }else{
                    return loadBmap(context);
                }
                return bmap;
        }

        if(!triedToRetrieveBitmap) {
            //triedToRetrieveBitmap = true;
            bmap = getBmap();
            return getBmap();
        }

        return bmap;
    }

    public Bitmap getBitmapQuick(Context context){
        return MusicDataMetadata.getBitmap(path);
    }



    public void setBmapCache(Context context){
        ImageSaver imageSaver = new ImageSaver(context);
        imageSaver.setFileName(path).setDirectory("ImageCache").save(bmap);
    }

    public void setBmapCache(Context context, Bitmap bitmap){
        ImageSaver imageSaver = new ImageSaver(context);
        imageSaver.setFileName(path).setDirectory("ImageCache").save(bitmap);
    }

    /*public Bitmap loadBmap(Context context){
        ImageSaver imageSaver = new ImageSaver(context);
        if(bmap==null && !triedToRetrieveBitmap) {
            bmap = imageSaver.setFileName(path).setDirectory("ImageCache").load();
            return bmap;
        }
        return bmap;
    }*/

    public Bitmap loadBmap(Context context){
        ImageSaver imageSaver = new ImageSaver(context);
        if(bmap==null && !triedToRetrieveBitmap) {
            return imageSaver.setFileName(path).setDirectory("ImageCache").load();
        }
        return bmap;
    }

    /*public Bitmap getBmap(){
        if(!triedToRetrieveBitmap && bmap==null){
            System.out.println("Tried to retrieve");
            bmap = MusicDataMetadata.getBitmap(path);
            this.triedToRetrieveBitmap = true;
            return bmap;
        }else{
            return bmap;
        }
    }*/

    public Bitmap getBmap(){
        System.out.println("Did not try to retrieve");
        if(!triedToRetrieveBitmap && bmap==null){
            System.out.println("Tried to retrieve");
            if(!Settings.getSaveImageCache()){
                this.triedToRetrieveBitmap = true;
            }
            return MusicDataMetadata.getBitmap(path);
        }else{
            return bmap;
        }
    }

    public MusicData(String path){
        initConstructorData(path);
        this.date = setInitialDate();
    }

    public MusicData(String path, String date){
        initConstructorData(path);
        this.date = date;
    }

    public MusicData(String path, String title, String artist, String album, int lengthInt, String date, Context context){  //String hasBitmap
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.lengthInt = lengthInt;
        this.date = date;
        if(Settings.getImageCacheOnLoad() && Settings.getSaveImageCache()){
            getBitmap(context);
        }
    }

    public MusicData(String path, String title, String artist, String album, int lengthInt, Context context){
        this.triedToRetrieveBitmap = false;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.lengthInt = lengthInt;
        this.date = setInitialDate();
        if(Settings.getImageCacheOnLoad() && Settings.getSaveImageCache()){
            getBitmap(context);
        }
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

    public String getLengthString(){
        return TypeConverter.formatDuration(lengthInt);
    }
}
