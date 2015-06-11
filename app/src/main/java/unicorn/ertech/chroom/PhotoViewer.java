package unicorn.ertech.chroom;
import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 01.02.2015.
 */
public class PhotoViewer extends Activity{
    TouchImageView imgview;
    int position = 0;
    String[] photoURLs;
    Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        mPicasso = Picasso.with(getApplicationContext());
        photoURLs = getIntent().getStringArrayExtra("photos");

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<View>();
        View page;
        if((photoURLs[9]!=null)&&(!photoURLs[9].equals("http://im.topufa.org/"))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            mPicasso.load(photoURLs[9]).into(imgview,new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
            pages.add(page);
        }

        if((photoURLs[5]!=null)&&(!photoURLs[5].equals("http://im.topufa.org/"))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            mPicasso.load(photoURLs[5]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
            pages.add(page);
        }

        if((photoURLs[6]!=null)&&(!photoURLs[6].equals("http://im.topufa.org/"))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            mPicasso.load(photoURLs[6]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
            pages.add(page);
        }

        if((photoURLs[7]!=null)&&(!photoURLs[7].equals("http://im.topufa.org/"))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            mPicasso.load(photoURLs[7]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
            pages.add(page);
        }

        if(((photoURLs[8]!=null)&&!photoURLs[8].equals("http://im.topufa.org/"))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            mPicasso.load(photoURLs[8]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
            pages.add(page);
        }


        photoPagerAdapter pagerAdapter = new photoPagerAdapter(pages);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager_photo);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("id", 0));
        viewPager.setOffscreenPageLimit(1);
//matrix=new Matrix();
//SGD=new ScaleGestureDetector(this, new ScaleListener());
    }
    public void onClick(View v) {
        switch (v.getId()) {
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
/*@Override
public View makeView() {
// TODO Auto-generated method stub
ImageView imgview = new ImageView(this);
imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
imgview.setLayoutParams(new
ImageSwitcher.LayoutParams(
ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
imgview.setBackgroundColor(0xFF000000);
return imgview;
}*/
/*@Override
public boolean onTouchEvent(MotionEvent event) {
SGD.onTouchEvent(event);
return true;
}
private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
@Override
public boolean onScale(ScaleGestureDetector detector){
float SF=detector.getScaleFactor();
SF=Math.max(0.1f, Math.min(SF, 0.5f));
matrix.setScale(SF,SF);
return true;
}
}*/
}