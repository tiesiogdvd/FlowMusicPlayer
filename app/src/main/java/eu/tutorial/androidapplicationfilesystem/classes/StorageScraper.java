package eu.tutorial.androidapplicationfilesystem.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class StorageScraper {
    public static void searchStorage(String path, Playlist allMusic){
        ArrayList<String> audioFileTypes;
        audioFileTypes = new ArrayList<>();
        Collections.addAll(audioFileTypes,".3gp",".mp4",".m4a",".aac",".ts",".amr",".flac",".mp4",".mid",".midi",".xmf",".mxmf",".rtttl",".rtx",".ota",".mp3",".mkv",".ogg",".wav");

        File[] filesAndFolders;
        File root = new File(path);

        filesAndFolders = root.listFiles();
        for(File selectedFile:filesAndFolders){
            if(!selectedFile.getAbsolutePath().equals("/storage/emulated/0/Android")){
                System.out.println(selectedFile.getAbsolutePath());
                if (selectedFile.isDirectory()) {
                    searchStorage(selectedFile.getAbsolutePath(),allMusic);
                } else if (!selectedFile.equals(null)) {
                    String fileType = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf("."));
                    if (audioFileTypes.contains(fileType)) {
                        allMusic.addSong(selectedFile);
                        System.out.println("Name of file: " + selectedFile.getName() + " Location: " + selectedFile.getAbsolutePath());
                    } else {
                        System.out.println("Not an audio type");
                    }
                }
            }
        }
    }
}
