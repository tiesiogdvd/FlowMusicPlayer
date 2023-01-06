package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//Class implemented to create a png of bitmap received from music metadata.
//Meant to store image cache for fast loading
//Might be replaced with Glide due to memory cost on very large data sets

public class ImageSaver {
    private String directoryName = "ImageCache";
    private String fileName = "image.png";
    private Context context;
    private File dir;
    private boolean external = false;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public ImageSaver setFileName(String filePath) {
        this.fileName = TypeConverter.convertPath(filePath);
        return this;
    }

    public ImageSaver setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public ImageSaver setDirectory(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public String save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    String path = context.getFilesDir() + "/" + directoryName+"/"+fileName;
                    return path;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @NonNull
    private File createFile() {
        File directory;
        if (external) {
            directory = getAlbumStorageDir(directoryName);
            if (!directory.exists()) {
                directory.mkdir();
            }
        } else {
            directory = new File(context.getFilesDir() + "/" + directoryName);
            if (!directory.exists()) {
                directory.mkdir();
            }
        }

        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean deleteFile() {
        File file = createFile();
        return file.delete();
    }
}