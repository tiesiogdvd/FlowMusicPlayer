package eu.tutorial.androidapplicationfilesystem.classes;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BrightnessCalculator {
    private static int calculateBrightnessEstimate(Bitmap bitmap, int pixelSpacing) {
        int R = 0; int G = 0; int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return (R + B + G) / (n * 3);
    }

    public static int calculateBrightness(android.graphics.Bitmap bitmap) {
        return calculateBrightnessEstimate(bitmap, 1);
    }


}
