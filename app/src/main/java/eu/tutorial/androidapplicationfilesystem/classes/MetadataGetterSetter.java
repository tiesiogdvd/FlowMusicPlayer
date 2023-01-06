package eu.tutorial.androidapplicationfilesystem.classes;


//Used to set metadata to music player fragment views
//Makes use of MusicData class to get the Metadata

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

public class MetadataGetterSetter {
    TextView songName;
    TextView artistName;
    NeumorphImageButton btnMusicImage;
    File musicFile;
    MusicData musicData;

    public static void retrieveMetadata(String path, Context context) {  //Song path and mainactivity context
        TextView songName = ((MainActivity) context).findViewById(R.id.musicSongName);
        TextView artistName = ((MainActivity) context).findViewById(R.id.musicSongArtist);
        NeumorphImageButton btnMusicImage = ((MainActivity) context).findViewById(R.id.musicImage);
        File musicFile = new File(path);

        TextView barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        ShapeableImageView barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);

        if (musicFile.exists()) {
            String title = MusicDataMetadata.getTitle(musicFile);
            String album = MusicDataMetadata.getAlbum(musicFile);
            String artist = MusicDataMetadata.getArtist(musicFile);
            Bitmap bitmap = MusicDataMetadata.getBitmap(musicFile);
            //Implementing the changing of views in MainActivity
            songName.setText("Title: " + title);
            barText.setText(title);
            if (artist != null) {
                artistName.setVisibility(View.VISIBLE);
                artistName.setText("Artist: " + artist);
            } else {
                artistName.setVisibility(View.GONE);
            }
            if (bitmap != null) {
                btnMusicImage.setImageBitmap(bitmap);
                barImage.setImageBitmap(bitmap);
                StyleSetter.setStyles(context, bitmap);

            } else {
                barImage.setImageResource(R.drawable.img_bg_2);
                btnMusicImage.setImageResource(R.drawable.ic_action_note);
                StyleSetter.setStyles(context, bitmap);
                //
            }
        }else{
            barImage.setImageResource(R.drawable.img_bg_2);
            btnMusicImage.setImageResource(R.drawable.ic_action_note);
            songName.setText("File no longer found");
            artistName.setVisibility(View.GONE);
            StyleSetter.setInitBackground(context);
            //StyleSetter.setStyles(context, bitmap);
        }
    }

    public static void setBarMetadata (Context context, String path) {
        TextView barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        ShapeableImageView barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);
        File musicFile = new File(path);

        if (musicFile.exists()) {
            String title = MusicDataMetadata.getTitle(musicFile);
            Bitmap bitmap = MusicDataMetadata.getBitmap(musicFile);
            barText.setText(title);
            if (bitmap != null) {
                barImage.setImageBitmap(bitmap);
            } else {
                barImage.setImageResource(R.drawable.img_bg_2);
            }
        }else{
            barText.setText("File no longer found");
            barImage.setImageResource(R.drawable.img_bg_2);
        }
    }

}