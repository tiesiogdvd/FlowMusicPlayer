package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.SharedPreferences;

public class Settings {

    //Setting whether to choose MediaStore audio file retrieval or own recursive implementation on music load
    //Default false
    private static Boolean useMediastore;

    //Option to either save audio file images into cache
    //Lower ram usage and processor overhead
    //Uses extra storage
    //Default false
    private static Boolean saveImageCache;

    //Option to load cache in the background on the load of application
    //Works together with saveImageCache option
    //If it is off, the cache will be created only when the media file image is accessed
    private static Boolean imageCacheOnLoad;

    //Sets a parameter for lowest file size when scraping audio data
    //Can be used both by MediaStore and recursive implementation in StorageScraper
    //Default 1536 (1.5MB)
    private static int lowestFileSize;

    //This parameter is also used for scraping audio data
    //Can be used by MediaStore
    //Default 90000 = 90s
    private static int lowestSongLength;

    //Parameter to check if the application is loaded for the first time
    //Default true
    private static Boolean firstRun;

    //Parameter to check if last load of songs finished
    //To be set false before the laod starts and true after it finishes
    //Default false
    private static Boolean lastLoadingFinished;


    private static SharedPreferences sharedPreferences;

    private static String lastSongPath;

    private static int lastSongRemaining;

    private static String lastSongSource;

    private static int lastSongIndex;

    private static boolean backgroundMode;


    public static void setSettings(SharedPreferences sharedPreferences){

        useMediastore = sharedPreferences.getBoolean("useMediaStore",true);
        saveImageCache = sharedPreferences.getBoolean("saveImageCache",false);
        imageCacheOnLoad = sharedPreferences.getBoolean("imageCacheOnLoad", false);

        lowestFileSize = sharedPreferences.getInt("lowestFileSize",1536);
        lowestSongLength = sharedPreferences.getInt("lowestSongLength",90000);

        firstRun = sharedPreferences.getBoolean("firstRun", true);
        lastLoadingFinished = sharedPreferences.getBoolean("lastLoadingFinished", false);

        lastSongPath = sharedPreferences.getString("lastSongPath",null);
        lastSongRemaining = sharedPreferences.getInt("lastSongRemaining", -1);
        lastSongSource = sharedPreferences.getString("lastSongSource", null);
        lastSongIndex = sharedPreferences.getInt("lastSongIndex", -1);
        backgroundMode = sharedPreferences.getBoolean("backgroundMode",false);

        Settings.sharedPreferences = sharedPreferences;
    }

    public static Boolean getUseMediastore() {
        return useMediastore;
    }

    public static Boolean getSaveImageCache() {
        return saveImageCache;
    }

    public static Boolean getImageCacheOnLoad(){
        return imageCacheOnLoad;
    }

    public static int getLowestFileSize() {
        return lowestFileSize;
    }

    public static int getLowestSongLength() {
        return lowestSongLength;
    }

    public static Boolean getLastLoadingFinished() {
        return lastLoadingFinished;
    }

    public static int getLastSongIndex() {
        return lastSongIndex;
    }

    public static String getLastSongPath() {
        return lastSongPath;
    }

    public static int getLastSongRemaining() {
        return lastSongRemaining;
    }

    public static String getLastSongSource() {
        return lastSongSource;
    }

    public static Boolean getBackgroundMode(){
        return backgroundMode;
    }

    public static Boolean isFirstRun(){
        if(!firstRun){
            return false;
        }else{
            sharedPreferences.edit().putBoolean("firstRun",false).apply();
            return true;
        }

    }


    public static void setUseMediastore(Boolean useMediastore) {
        Settings.useMediastore = useMediastore;
        sharedPreferences.edit().putBoolean("useMediaStore", useMediastore).apply();

    }

    public static void setSaveImageCache(Boolean saveImageCache) {
        Settings.saveImageCache = saveImageCache;
        sharedPreferences.edit().putBoolean("saveImageCache", saveImageCache).apply();
    }

    public static void setGetImageCacheOnLoad(Boolean imageCacheOnLoad){
        Settings.imageCacheOnLoad = imageCacheOnLoad;
        sharedPreferences.edit().putBoolean("imageCacheOnLoad", imageCacheOnLoad).apply();
    }

    public static void setLowestFileSize(int lowestFileSize) {
        Settings.lowestFileSize = lowestFileSize;
        sharedPreferences.edit().putInt("lowestFileSize", lowestFileSize).apply();
    }

    public static void setLowestSongLength(int lowestSongLength) {
        Settings.lowestSongLength = lowestSongLength;
        sharedPreferences.edit().putInt("lowestSongLength", lowestSongLength).apply();
    }

    public static void setLastLoadingFinished(Boolean lastLoadingFinished) {
        Settings.lastLoadingFinished = lastLoadingFinished;
        sharedPreferences.edit().putBoolean("lastLoadingFinished", lastLoadingFinished).apply();
    }

    public static void setLastSongPath(String lastSongPath) {
        Settings.lastSongPath = lastSongPath;
        sharedPreferences.edit().putString("lastSongPath",lastSongPath).apply();
    }

    public static void setLastSongRemaining(int lastSongRemaining) {
        Settings.lastSongRemaining = lastSongRemaining;
        sharedPreferences.edit().putInt("lastSongRemaining",lastSongRemaining).apply();
    }

    public static void setLastSongSource(String lastSongSource) {
        Settings.lastSongSource = lastSongSource;
        sharedPreferences.edit().putString("lastSongSource", lastSongSource).apply();
    }

    public static void setLastSongIndex(int lastSongIndex) {
        Settings.lastSongIndex = lastSongIndex;
        sharedPreferences.edit().putInt("lastSongIndex", lastSongIndex).apply();
    }

    public static void setBackgroundMode(boolean backgroundMode) {
        Settings.backgroundMode = backgroundMode;
        sharedPreferences.edit().putBoolean("backgroundMode", backgroundMode).apply();
    }
}
