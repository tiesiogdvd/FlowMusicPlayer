package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;

public class StyleSetter {

    static TextView title, artist, remainingText, totalText;
    static NeumorphImageButton btnMusicImage;
    static ImageView imageView;
    static NeumorphImageButton btnPlay;
    static NeumorphImageButton btnPrev;
    static NeumorphImageButton btnNext;
    static NeumorphImageButton btnPlaylist;
    static NeumorphImageButton btnFav;
    static NeumorphImageButton btnStorage;
    static Bitmap m_bitmap;
    static Bitmap m_drawableBitmap;



    public static void setStyles(Context context, Bitmap bitmap){
        m_bitmap = bitmap;
        initViews(context);
        setBackground(context);
    }

    private static void initViews(Context context){
        btnMusicImage = ((MainActivity)context).findViewById(R.id.musicImage);
        imageView  = ((MainActivity)context).findViewById(R.id.layoutBackground);
        btnPlay = ((MainActivity) context).findViewById(R.id.playButton);
        btnPrev = ((MainActivity) context).findViewById(R.id.lastButton);
        btnNext = ((MainActivity) context).findViewById(R.id.nextButton);
        btnPlaylist = ((MainActivity) context).findViewById(R.id.playlistButton);
        btnFav = ((MainActivity) context).findViewById(R.id.favoriteButton);
        btnStorage = ((MainActivity) context).findViewById(R.id.btnStorage);
        title = ((MainActivity) context).findViewById(R.id.musicSongName);
        artist = ((MainActivity) context).findViewById(R.id.musicSongArtist);
        remainingText = ((MainActivity) context).findViewById(R.id.musicRemainingText);
        totalText = ((MainActivity) context).findViewById(R.id.musicTotalText);
    }

    private static void setBackground(Context context){

        if(m_bitmap!=null){
            BitmapDrawable drawable;
            Bitmap blurredBitmap = BlurBuilder.blur(context,m_bitmap);
            drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
            imageView.setBackground(drawable);
            setFill(true);
        }else {
            m_drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_3);
            Bitmap blurredBitmap = BlurBuilder.blur(context,m_drawableBitmap);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
            imageView.setBackground(drawable);
            setFill(false);
        }

    }


    private static void setFill(Boolean hasImage){
        int brightness;
        if(hasImage){
            brightness = 255-BrightnessCalculator.calculateBrightness(m_bitmap);
        }else{
            brightness = 255-BrightnessCalculator.calculateBrightness(m_drawableBitmap);
        }
        if(brightness>=123){brightness=255;}
        if(brightness<=123){brightness=0;}
        String hex = String.format("#%02X%02X%02X", brightness, brightness, brightness); //3 brightness each stands for R,G,B
        int color = Color.parseColor(hex);

        btnPlay.setColorFilter(color);
        btnPrev.setColorFilter(color);
        btnNext.setColorFilter(color);
        btnPlaylist.setColorFilter(color);
        btnFav.setColorFilter(color);
        btnStorage.setColorFilter(color);
        title.setTextColor(color);
        artist.setTextColor(color);
        remainingText.setTextColor(color);
        totalText.setTextColor(color);
    }

    public static void setInitBackground(Context context){

        initViews(context);
        Bitmap drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_3);
        m_drawableBitmap = drawableBitmap;
        Bitmap blurredBitmap = BlurBuilder.blur(context,drawableBitmap);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
        imageView.setBackground(drawable);
        setFill(false);
    }
}
