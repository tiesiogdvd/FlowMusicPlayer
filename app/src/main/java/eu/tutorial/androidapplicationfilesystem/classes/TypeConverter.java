package eu.tutorial.androidapplicationfilesystem.classes;

//Static class to be used for converting dates, paths and etc.

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TypeConverter {
    public static String convertPath(String filePath){
        String fileName = filePath.substring(filePath.lastIndexOf("/")).replace("/","");
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".png";
        return fileName;
    }

    public static String removeExtras(String filePath){
        String fileName = filePath.substring(filePath.lastIndexOf("/")).replace("/","");
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    public static String formatDuration(int duration) {
        int minutes = (int) TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        int seconds = (int) (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES));

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String getFilePath(String file){
        return new File(file).getParent();
    }

}
