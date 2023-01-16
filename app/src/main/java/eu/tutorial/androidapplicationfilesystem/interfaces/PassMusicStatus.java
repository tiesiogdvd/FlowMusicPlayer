package eu.tutorial.androidapplicationfilesystem.interfaces;

import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;

public interface PassMusicStatus {
    void onDataReceived(Boolean isPlaying);
    void onSongRequest(String songPath, Playlist playlist, int index);
    void onMediaReady(Boolean isReady);
    void onLibraryStoragePressed();
    void onRequestNavbar(Boolean request, FragmentLibrarySongs fragmentLibrarySongs);
}
