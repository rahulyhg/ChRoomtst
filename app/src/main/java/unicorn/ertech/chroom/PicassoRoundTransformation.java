package unicorn.ertech.chroom;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by Timur on 14.01.2015.
 */
public class PicassoRoundTransformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        return convertToCircle(source, false);
    }
    @Override
    public String key() {
        return "circle";
    }

    public static Bitmap convertToCircle(Bitmap source, boolean recycleSource) {

        int x, y;
        int size = Math.min(source.getWidth(), source.getHeight());
        /*if(source.getWidth()==source.getHeight()){
            x=source.getWidth()/2;
            y=x;
        }else{
            x = (source.getWidth() - size) / 2;
            y = (source.getHeight() - size) / 2;
        }*/
        x = (source.getWidth() - size) / 2;
        y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap!=source){
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);
        squaredBitmap.recycle();
        return bitmap;
    }
}
