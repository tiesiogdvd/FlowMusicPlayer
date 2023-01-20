package eu.tutorial.androidapplicationfilesystem.classes;


//Used to set metadata to music player fragment views
//Makes use of MusicData class to get the Metadata

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterLibraryAllSongs;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

public class MetadataGetterSetter {
    TextView songName;
    TextView artistName;
    NeumorphImageButton btnMusicImage;
    File musicFile;
    MusicData musicData;

    public static void retrieveMetadata(String path, Context context) {  //Song path and mainactivity context
        System.out.println("Retrieving");
        TextView songName = ((MainActivity) context).findViewById(R.id.musicSongName);
        TextView artistName = ((MainActivity) context).findViewById(R.id.musicSongArtist);
        NeumorphImageButton btnMusicImage = ((MainActivity) context).findViewById(R.id.musicImage);
        File musicFile = new File(path);

        TextView barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        ShapeableImageView barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);

        if (musicFile.exists()) {
            MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
            musicDataMetadata.setAllData(musicFile.getAbsolutePath());
            class retrievingMetadata extends AsyncTask <Void, Void, MusicDataMetadata>{
                String m_path;
                public retrievingMetadata(String path){
                    this.m_path = path;
                }

                @Override
                protected MusicDataMetadata doInBackground(Void... voids) {
                    MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
                    musicDataMetadata.setAllData(m_path);
                    return musicDataMetadata;
                }

                @Override
                protected void onPostExecute(MusicDataMetadata musicDataMetadata) {
                    super.onPostExecute(musicDataMetadata);
                    String title = musicDataMetadata.title;
                    System.out.println(title + " SONG NAME REQUEST");
                    String album = musicDataMetadata.album;
                    String artist = musicDataMetadata.artist;
                    Bitmap bitmap = musicDataMetadata.bitmap;
                    //Implementing the changing of views in MainActivity
                    songName.setText("Title: " + title);
                    barText.setText(title);
                    if (artist != null) {
                        artistName.setVisibility(View.VISIBLE);
                        artistName.setText("Artist: " + artist);
                    } else {
                        artistName.setVisibility(View.INVISIBLE);
                    }
                    if (bitmap != null) {
                        btnMusicImage.setImageBitmap(bitmap);
                        barImage.setImageBitmap(bitmap);
                        StyleSetter.setStyles(context, bitmap);

                    } else {
                        barImage.setImageResource(R.drawable.ic_group_23_image_6);
                        btnMusicImage.setImageResource(R.drawable.ic_action_note);
                        StyleSetter.setStyles(context, null);
                    }

                }
            }
            new retrievingMetadata(musicFile.getAbsolutePath()).execute();

        }else{
            barImage.setImageResource(R.drawable.ic_group_23_image_6);
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