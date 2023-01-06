package eu.tutorial.androidapplicationfilesystem.classes;

import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

//Class created for scraping all of the storage in a recursive manner
//Used to get all paths for all audio files
//Does not fetch any metadata
//Can be filtered by length as seen below selectedFile.length(1024/1024)

public class StorageScraper {
    public static void searchStorage(String path, ArrayList<String> allSongsPaths){
        ArrayList<String> audioFileTypes;
        audioFileTypes = new ArrayList<>();
        //Collections.addAll(audioFileTypes,".3gp",".mp4",".m4a",".aac",".ts",".amr",".flac",".mp4",".mid",".midi",".xmf",".mxmf",".rtttl",".rtx",".ota",".mp3",".mkv",".ogg",".wav");
        Collections.addAll(audioFileTypes,".m4a",".aac",".flac",".midi",".rtttl",".ota",".mp3",".mkv",".ogg",".wav");
        File[] filesAndFolders;
        File root = new File(path);
        filesAndFolders = root.listFiles();
                for(File selectedFile:filesAndFolders){
                    if(!selectedFile.getAbsolutePath().equals("/storage/emulated/0/Android")){ //excluded folder due to it not being accessible on limited scope
                        if (selectedFile.isDirectory()) {
                            searchStorage(selectedFile.getAbsolutePath(),allSongsPaths); //method calls itself with the folders' path to fetch all songs
                        } else if (!selectedFile.equals(null)) {
                            String fileType = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".")); //checks if file type is valid
                            if (audioFileTypes.contains(fileType) && Integer.parseInt(String.valueOf(selectedFile.length()/1024/1024))>1.5) { //only adds to array if file is specified size
                                allSongsPaths.add(selectedFile.getAbsolutePath());
                            } else {
                                //System.out.println("Not an audio type");
                            }
                        }
                    }
                }
    }
}
