package unicorn.ertech.chroom;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Timur on 14.01.2015.
 */
public class CutImage implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        return transformImage(source);
    }

    public static Bitmap transformImage(Bitmap source) {
        //int targetWidth = source.getWidth();
        int targetWidth = Profile.iHolder.imageWidth;

        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
        int targetHeight = (int) (targetWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
        }
        return result;
    }

    @Override public String key() {
        return "transformation" + " desiredWidth";
    }
}
