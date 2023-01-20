package eu.tutorial.androidapplicationfilesystem.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PathPlaylist {
    String path;
    ArrayList <String> audioFileTypes;

    public PathPlaylist() {
        audioFileTypes = new ArrayList<>();
        Collections.addAll(audioFileTypes,".m4a",".aac",".flac",".midi",".rtttl",".ota",".mp3",".mkv",".ogg",".wav");
    }

    public ArrayList<String> searchPath(String path) {
        File rootPath = new File(path);
        File[] filesAndFolders = rootPath.listFiles();
        ArrayList <String> allSongsPaths = new ArrayList<>();
        if(filesAndFolders!=null && filesAndFolders.length!=0){
            for (File selectedPath : filesAndFolders) {
                if (selectedPath != null) {
                    String fileType = selectedPath.getAbsolutePath().substring(selectedPath.getAbsolutePath().lastIndexOf(".")); //checks if file type is valid
                    if (audioFileTypes.contains(fileType) && Integer.parseInt(String.valueOf(selectedPath.length() / 1024)) > Settings.getLowestFileSize()) { //only adds to array if file is specified size
                        allSongsPaths.add(selectedPath.getAbsolutePath());
                        System.out.println(selectedPath);
                    }
                }
            }
        }
        return allSongsPaths;
    }

    public Playlist createPlaylist(ArrayList<String> paths){
        Playlist tempPlaylist = new Playlist();
        for (String path: paths){
            tempPlaylist.addSong(path);
            System.out.println("song added");
        }
        return tempPlaylist;
    }
}
