package eu.tutorial.androidapplicationfilesystem.classes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class SongDataTest {
    public long songId;
    public String songTitle;
    public String songArtist;
    public String path;
    public short genre;
    public int duration;
    public String album;
    public Bitmap albumArt;



    //TODO: implement isBitmapLoaded

    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("songId: %d, Title: %s, Artist: %s, Path: %s, Genere: %d, Duration %s",
                songId, songTitle, songArtist, path, genre, duration);
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public short getGenre() {
        return genre;
    }

    public void setGenre(short genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }
}
