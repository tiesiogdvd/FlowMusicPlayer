package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.imageview.ShapeableImageView;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;
import soup.neumorphism.NeumorphImageButton;

public class StyleSetter {

    static TextView title, artist, remainingText, totalText;
    static NeumorphImageButton btnMusicImage;
    static ImageView imageView, imageViewGradient;
    static ConstraintLayout imageViewGradientWrapper;
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

        imageViewGradient = ((MainActivity) context).findViewById(R.id.layoutBackgroundGradient);
        imageViewGradientWrapper = ((MainActivity) context).findViewById(R.id.layoutBackgroundGradientWrapper);
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
        if(m_bitmap!=null){

            if(!Settings.getBackgroundMode()){
                Bitmap blurredBitmap = BlurBuilder.blur(context,m_bitmap);
                Glide.with(context)
                        .asBitmap()
                        .load(blurredBitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transition(BitmapTransitionOptions.withCrossFade(500))
                        .centerCrop()
                        .into(imageView);

                imageView.setVisibility(View.VISIBLE);

                imageViewGradientWrapper.setVisibility(View.GONE);
                btnMusicImage.setVisibility(View.VISIBLE);
            }else{
                Glide.with(context)
                        .asBitmap()
                        .load(m_bitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transition(BitmapTransitionOptions.withCrossFade(500))
                        .centerCrop()
                        .into(imageViewGradient);

                imageViewGradientWrapper.setVisibility(View.VISIBLE);
                imageViewGradient.animate();
                imageView.setVisibility(View.GONE);
                btnMusicImage.setVisibility(View.GONE);

            }
            setFill(true, context);
        }else {
            int UIMode = context.getResources().getConfiguration().uiMode;
            if(UIMode == 33){ //33 is the night mode

                if(!Settings.getBackgroundMode()){
                    imageView.setVisibility(View.VISIBLE);

                    imageViewGradientWrapper.setVisibility(View.GONE);
                    btnMusicImage.setVisibility(View.VISIBLE);
                    m_drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_9);
                    Bitmap blurredBitmap = BlurBuilder.blur(context,m_drawableBitmap);
                    Glide.with(context)
                            .asBitmap()
                            .load(blurredBitmap)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transition(BitmapTransitionOptions.withCrossFade(500))
                            .centerCrop()
                            .into(imageView);

                }else{
                    imageViewGradientWrapper.setVisibility(View.VISIBLE);
                    imageViewGradient.animate();
                    imageView.setVisibility(View.GONE);

                    btnMusicImage.setVisibility(View.GONE);
                    Drawable image = context.getResources().getDrawable(R.drawable.img_bg_8, context.getTheme());
                    Glide.with(context)
                            .asDrawable()
                            .load(image)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .centerCrop()
                            .into(imageViewGradient);
                }



            }



            if(UIMode == 17){ //17 for the light mode
                if(!Settings.getBackgroundMode()){
                    imageView.setVisibility(View.VISIBLE);

                    imageViewGradientWrapper.setVisibility(View.GONE);
                    btnMusicImage.setVisibility(View.VISIBLE);
                    m_drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_7);
                    Bitmap blurredBitmap = BlurBuilder.blur(context,m_drawableBitmap);
                    Glide.with(context)
                            .asBitmap()
                            .load(blurredBitmap)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transition(BitmapTransitionOptions.withCrossFade(500))
                            .centerCrop()
                            .into(imageView);

                }else{
                    imageViewGradientWrapper.setVisibility(View.VISIBLE);
                    imageViewGradient.animate();
                    imageView.setVisibility(View.GONE);

                    btnMusicImage.setVisibility(View.GONE);
                    Drawable image = context.getResources().getDrawable(R.drawable.img_bg_8, context.getTheme());
                    Glide.with(context)
                            .asDrawable()
                            .load(image)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .centerCrop()
                            .into(imageViewGradient);
                }
            }

            setFill(false, context);
        }

        if(Settings.getBackgroundMode()){
            View view = ((MainActivity) context).findViewById(R.id.layoutGradient);
            int h = view.getHeight();
            int w = view.getWidth();
            ((MainActivity) context).getRequestedOrientation();
            int orientation = context.getResources().getConfiguration().orientation;
            ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());


            if(m_bitmap!=null){
                Palette palette = Palette.from(m_bitmap).generate();
                int dominantColor = palette.getDominantColor(Color.WHITE);
                if(orientation == Configuration.ORIENTATION_PORTRAIT){
                    mDrawable.getPaint().setShader(new LinearGradient(0,0,0,h,Color.parseColor("#00000000"),dominantColor, Shader.TileMode.CLAMP));
                }else{
                    mDrawable.getPaint().setShader(new LinearGradient(0,0,w,0,Color.parseColor("#00000000"),dominantColor, Shader.TileMode.CLAMP));
                }
                view.setBackground(mDrawable);
                //imageView.setBackground(null);
                imageView.setBackgroundResource(android.R.color.transparent);
                imageViewGradientWrapper.setVisibility(View.VISIBLE);
                imageViewGradient.animate();
                imageView.setVisibility(View.VISIBLE);

                imageView.setBackgroundColor(dominantColor);
            }else{
                if(orientation == Configuration.ORIENTATION_PORTRAIT){
                    mDrawable.getPaint().setShader(new LinearGradient(0,0,0,h,Color.parseColor("#00000000"),context.getColor(R.color.background), Shader.TileMode.CLAMP));
                }else{
                    mDrawable.getPaint().setShader(new LinearGradient(0,0,w,0,Color.parseColor("#00000000"),context.getColor(R.color.background), Shader.TileMode.CLAMP));
                }
                view.setBackground(mDrawable);
                imageView.setBackgroundResource(android.R.color.transparent);
                imageView.setVisibility(View.VISIBLE);
                //imageView1.setVisibility(View.GONE);
                imageView.setBackgroundColor(context.getColor(R.color.background));
            }
        }
    }




    private static void setFill(Boolean hasImage, Context context){
        if(!Settings.getBackgroundMode()){
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
        }else{
            int color = context.getResources().getColor(R.color.text,context.getTheme());
            btnPlay.setColorFilter(color);
            btnPrev.setColorFilter(color);
            btnNext.setColorFilter(color);
            btnPlaylist.setColorFilter(color);
            btnFav.setColorFilter(color);
            title.setTextColor(color);
            artist.setTextColor(color);
            remainingText.setTextColor(color);
            totalText.setTextColor(color);


            //btnPlay.getBackground().setTint(context.getResources().getColor(R.color.background, context.getTheme()));

        }
    }

    public static void setInitBackground(Context context){
        ImageView imageView = ((MainActivity)context).findViewById(R.id.layoutBackground);
        Bitmap drawableBitmap;
        if(!Settings.getBackgroundMode()){
            drawableBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_8);
            m_drawableBitmap = drawableBitmap;
            Bitmap blurredBitmap = BlurBuilder.blur(context,drawableBitmap);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), blurredBitmap);
            setImageDrawableWithAnimation(imageView, drawable, 200);
            imageView.getBackground().setAlpha(0);
        }else{
            int color = context.getResources().getColor(R.color.text,context.getTheme());
            imageView.setBackgroundColor(color);
            imageView.getBackground().setAlpha(255);
        }
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
        imageView.setScaleType(ImageView.ScaleType.FIT_END);
    }

}
