package eu.tutorial.androidapplicationfilesystem.classes;

//Static class to be used for converting dates, paths and etc.

public class TypeConverter {
    public static String convertPath(String filePath){
        String fileName = filePath.substring(filePath.lastIndexOf("/")).replace("/","");
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".png";
        return fileName;
    }
}
