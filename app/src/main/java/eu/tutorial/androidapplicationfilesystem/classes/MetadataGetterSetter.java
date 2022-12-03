package eu.tutorial.androidapplicationfilesystem.classes;


//Used to set metadata to the main activity views
//Makes use of MusicData class to get the Metadata

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class MetadataGetterSetter {

    public void retrieveMetadata(String path, Context context) {  //Song path and mainactivity context
        TextView songName = ((MainActivity)context).findViewById(R.id.musicSongName);
        TextView artistName = ((MainActivity)context).findViewById(R.id.musicSongArtist);
        NeumorphImageButton btnMusicImage = ((MainActivity)context).findViewById(R.id.musicImage);
        File musicFile = new File(path);
        MusicData musicData = new MusicData(); //Created new object of class MusicData to retrieve metadata info from selected audio

        String title = musicData.getTitle(musicFile);
        String album = musicData.getAlbum(musicFile);
        String artist = musicData.getArtist(musicFile);
        Bitmap bitmap = musicData.getBitmap(musicFile);


        //Implementing the changing of views in MainActivity
        songName.setText("Title: " + title);
        if (artist != null) {
            artistName.setVisibility(View.VISIBLE);
            artistName.setText("Artist: " + artist);
        } else {
            artistName.setVisibility(View.GONE);
        }
        if (bitmap != null) {
            btnMusicImage.setImageBitmap(bitmap);
        } else {
            btnMusicImage.setImageResource(R.drawable.ic_launcher_test_background);
        }
    }

}