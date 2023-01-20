package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class StyleSetter {

    static TextView title, artist, remainingText, totalText;
    static NeumorphImageButton btnMusicImage;
    static ImageView imageView;
    static ImageButton btnPlay;
    static ImageButton btnPrev;
    static ImageButton btnNext;
    static ImageButton btnPlaylist;
    static ImageButton btnFav;
    //static ShapeableImageView btnStorage;
    static Bitmap m_bitmap;
    static Bitmap m_drawableBitmap;
    static TextView barText;
    static ShapeableImageView barImage;


    public static void setStyles(Context context, Bitmap bitmap){
        System.out.println("Setting styles");
        m_bitmap = bitmap;
        initViews(context);
        setBackground(context);
    }

    public static void setStyles(Context context){
        initViews(context);
        setBackground(context);
    }


    private static void initViews(Context context){
        btnMusicImage = ((MainActivity)context).findViewById(R.id.musicImage);
        imageView  = ((MainActivity)context).findViewById(R.id.layoutBackground);
        btnPlay = ((MainActivity)context).findViewById(R.id.playButton);
        btnPrev = ((MainActivity) context).findViewById(R.id.lastButton);
        btnNext = ((MainActivity) context).findViewById(R.id.nextButton);
        btnPlaylist = ((MainActivity) context).findViewById(R.id.playlistButton);
        btnFav = ((MainActivity) context).findViewById(R.id.favoriteButton);
        //btnStorage = ((MainActivity) context).findViewById(R.id.btnStorage);
        title = ((MainActivity) context).findViewById(R.id.musicSongName);
        artist = ((MainActivity) context).findViewById(R.id.musicSongArtist);
        remainingText = ((MainActivity) context).findViewById(R.id.musicRemainingText);
        totalText = ((MainActivity) context).findViewById(R.id.musicTotalText);

        barText = ((MainActivity) context).findViewById(R.id.musicSongNameBar);
        barImage = ((MainActivity) context).findViewById(R.id.musicImageBar);
    }

    private static void setBackground(Context context){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inJustDecodeBounds = false;

        if(m_bitmap!=null){
            BitmapDrawable drawable;
            Bitmap blurredBitmap = BlurBuilder.blur(context,m_bitmap);
            drawable = new BitmapDrawable(context.getResources(), blurredBitmap);

            setImageDrawableWithAnimation(imageView, drawable, 200);

            //imageView.setBackground(drawable);
            //imageView.animate().setStartDelay(100).setDuration(200);
            setFill(true);
        }else {
            int UIMode = context.getResources().getConfiguration().uiMode;
            System.out.println(UIMode);

            if(UIMode == 33){ //33 is the night mode
                m_drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_9, options);
                Bitmap blurredBitmap = BlurBuilder.blur(context,m_drawableBitmap);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);

                setImageDrawableWithAnimation(imageView, drawable, 200);

                //imageView.setBackground(drawable);
                //imageView.animate().setStartDelay(100).setDuration(200);
                //imageView.getBackground().setAlpha(40);
                //imageView.getDrawable().setAlpha(0);
                //imageView.setImageAlpha(0);
                //imageView.getDrawable().mutate().setAlpha(0);

            }
            if(UIMode == 17){ //17 for the light mode
                m_drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_7, options);
                Bitmap blurredBitmap = BlurBuilder.blur(context,m_drawableBitmap);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
                //drawable.setAlpha(0);

                setImageDrawableWithAnimation(imageView, drawable, 200);

                //imageView.setBackground(drawable);
                //imageView.animate().setStartDelay(100).setDuration(200);
                //imageView.getBackground().setAlpha(20);
                //imageView.getDrawable().setAlpha(20);
                //imageView.getDrawable().mutate().setAlpha(0);
                //imageView.setImageAlpha(0);
            }
            //imageView.setBackground(drawable);
            //imageView.getBackground().setAlpha(80);
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
        title.setTextColor(color);
        artist.setTextColor(color);
        remainingText.setTextColor(color);
        totalText.setTextColor(color);
    }

    public static void setInitBackground(Context context){
        ImageView imageView = ((MainActivity)context).findViewById(R.id.layoutBackground);
        Bitmap drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_8);
        m_drawableBitmap = drawableBitmap;
        Bitmap blurredBitmap = BlurBuilder.blur(context,drawableBitmap);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
        setImageDrawableWithAnimation(imageView, drawable, 200);
        imageView.getBackground().setAlpha(0);
    }

    public static void setImageDrawableWithAnimation(ImageView imageView, Drawable drawable, int duration) {
        Drawable currentDrawable = imageView.getDrawable();
        if (currentDrawable == null) {
            imageView.setImageDrawable(drawable);
            return;
        }
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {currentDrawable, drawable});
        transitionDrawable.setCrossFadeEnabled(true);
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }
}
