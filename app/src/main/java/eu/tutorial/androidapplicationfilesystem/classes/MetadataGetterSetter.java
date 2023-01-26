package eu.tutorial.androidapplicationfilesystem.classes;


//Used to set metadata to music player fragment views
//Makes use of MusicData class to get the Metadata

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import linc.com.amplituda.Cache;
import linc.com.amplituda.Compress;
import linc.com.amplituda.exceptions.io.AmplitudaIOException;
import soup.neumorphism.NeumorphImageButton;

public class MetadataGetterSetter {
    TextView songName;
    TextView barText;
    TextView barArtist;
    TextView artistName;
    NeumorphImageButton btnMusicImage;
    ShapeableImageView barImage;
    File musicFile;
    MusicData musicData;

    public void retrieveMetadata(String path, Context context) {  //Song path and mainactivity context
        songName = ((MainActivity) context).findViewById(R.id.musicSongName);
        artistName = ((MainActivity) context).findViewById(R.id.musicSongArtist);
        btnMusicImage = ((MainActivity) context).findViewById(R.id.musicImage);
        musicFile = new File(path);

        barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        barArtist = ((MainActivity) context).findViewById(R.id.musicSongArtistBar);
        barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);

        if (musicFile.exists()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                // todo: background tasks
                MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
                musicDataMetadata.setAllData(musicFile.getAbsolutePath());

                ((MainActivity) context).runOnUiThread(()->{
                    String title = musicDataMetadata.title;
                    String album = musicDataMetadata.album;
                    String artist = musicDataMetadata.artist;
                    Bitmap bitmap = musicDataMetadata.bitmap;
                    //Implementing the changing of views in activity
                    songName.setText("Title: " + title);
                    barText.setText(title);
                    if (artist != null) {
                        artistName.setVisibility(View.VISIBLE);
                        artistName.setText("Artist: " + artist);
                        barArtist.setVisibility(View.VISIBLE);
                        barArtist.setText(artist);
                    } else {
                        artistName.setVisibility(View.INVISIBLE);
                        barArtist.setVisibility(View.GONE);
                    }

                    if(!Settings.getBackgroundMode()){ //Background mode square image
                        if (bitmap != null) {
                            btnMusicImage.setImageBitmap(bitmap);
                            barImage.setImageBitmap(bitmap);
                            StyleSetter.setStyles(context, bitmap);
                        } else {
                            btnMusicImage.setImageResource(R.drawable.ic_action_note);
                            barImage.setImageResource(R.drawable.ic_group_23_image_6);
                            StyleSetter.setStyles(context, null);
                        }
                    }else{
                        if (bitmap != null) {  //Background mode gradient image half screen
                            barImage.setImageBitmap(bitmap);
                            StyleSetter.setStyles(context, bitmap);
                        } else {
                            barImage.setImageResource(R.drawable.ic_group_23_image_6);
                            StyleSetter.setStyles(context, null);
                        }
                    }
                });
            });

        }else{
            barImage.setImageResource(R.drawable.ic_group_23_image_6);
            btnMusicImage.setImageResource(R.drawable.ic_action_note);
            songName.setText("File no longer found");
            artistName.setVisibility(View.GONE);
        }
    }




    public static void setBarMetadata (Context context, String path) {
        TextView barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        ShapeableImageView barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);
        File musicFile = new File(path);

        if (musicFile.exists()) {
            MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
            musicDataMetadata.setAllData(musicFile.getAbsolutePath());
            String title = musicDataMetadata.title;
            Bitmap bitmap = musicDataMetadata.bitmap;
            barText.setText(title);
            if (bitmap != null) {
                barImage.setImageBitmap(bitmap);
            } else {
                barImage.setImageResource(R.drawable.ic_group_23_image_6);
            }
        }else{
            barText.setText("File no longer found");
            barImage.setImageResource(R.drawable.ic_group_23_image_6);
        }
    }

}