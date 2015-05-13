package unicorn.ertech.chroom;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 01.02.2015.
 */
public class PhotoViewerPm extends Activity{
    TouchImageView imgview;
    int position = 0;
    String[] photoURLs;
    Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        //imgview = (TouchImageView)findViewById(R.id.ivPhotoViewer);
        mPicasso = Picasso.with(getApplicationContext());
        photoURLs = getIntent().getStringArrayExtra("photos");

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<View>();
        View page;
        if((photoURLs[0]!=null)&&(!photoURLs[0].equals("http://im.topufa.org/"))&&(!photoURLs[0].equals(""))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs[0]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview, new Callback() {
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
        if ((photoURLs[1] != null) && (!photoURLs[1].equals("http://im.topufa.org/"))&&(!photoURLs[1].equals(""))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs[1]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
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
        if ((photoURLs[2] != null) && (!photoURLs[2].equals("http://im.topufa.org/"))&&(!photoURLs[2].equals(""))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs[2]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
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
        if ((photoURLs[3] != null) && (!photoURLs[3].equals("http://im.topufa.org/"))&&(!photoURLs[3].equals(""))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs[3]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
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
        if ((photoURLs[4] != null) && (!photoURLs[4].equals("http://im.topufa.org/"))&&(!photoURLs[4].equals(""))) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            final ProgressBar pb=(ProgressBar)page.findViewById(R.id.progressBar2);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs[4]).memoryPolicy(MemoryPolicy.NO_STORE).into(imgview,new Callback() {
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
    }
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
}