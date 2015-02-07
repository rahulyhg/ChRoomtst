package unicorn.ertech.chroom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

/**
 * Created by Timur on 01.02.2015.
 */
public class PhotoViewer extends Activity implements ViewSwitcher.ViewFactory {
    ImageSwitcher imgswitcher;
    ImageView imgview;
    int position = 0;

    //private Integer[] mImageIds = { R.drawable.card1, R.drawable.card10,
    //        R.drawable.card11, R.drawable.card12, R.drawable.card13,
    //        R.drawable.card14, R.drawable.card15, }; //тут будет массив ссылок на фотки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        imgswitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
        //imgswitcher.setFactory(this);
        imgview = (ImageView)findViewById(R.id.ivPhotoViewer);

        String picUri = getIntent().getStringExtra("photo");
        Log.i("photo", picUri);
        if((picUri!=null)&&(!picUri.equals(""))) {
            Picasso.with(getApplicationContext()).load(picUri).into(imgview);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonForward:
                setPositionNext();
                //imgswitcher.setImageResource(mImageIds[position]);
                break;
            case R.id.ButtonPrev:
                setPositionPrev();
                //imgswitcher.setImageResource(mImageIds[position]);
                break;

            default:
                break;
        }
    }

    public void setPositionNext() {
        position++;
        if (position > 4 - 1) {
            position = 0;
        }
    }

    public void setPositionPrev() {
        position--;
        if (position < 0) {
            position = 4 - 1;
        }
    }

    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        ImageView imgview = new ImageView(this);
        imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgview.setLayoutParams(new
                ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imgview.setBackgroundColor(0xFF000000);
        return imgview;
    }
}
