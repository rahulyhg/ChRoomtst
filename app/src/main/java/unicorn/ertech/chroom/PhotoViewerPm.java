package unicorn.ertech.chroom;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 01.02.2015.
 */
public class PhotoViewerPm extends Activity{
    TouchImageView imgview;
    int position = 0;
    String photoURLs;
    Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);
        //imgview = (TouchImageView)findViewById(R.id.ivPhotoViewer);
        mPicasso = Picasso.with(getApplicationContext());
        photoURLs = getIntent().getStringExtra("photos");

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<View>();
        View page;
        if(!photoURLs.equals("http://im.topufa.org/")) {
            page = inflater.inflate(R.layout.photo_fragment, null);
            TouchImageView imgview = (TouchImageView) page.findViewById(R.id.ivPhotoViewer);
            /*final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loading_anim);
            imgview.startAnimation(myAnim);*/
            mPicasso.load(photoURLs).into(imgview);
            pages.add(page);
        }

        photoPagerAdapter pagerAdapter = new photoPagerAdapter(pages);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager_photo);
        viewPager.setAdapter(pagerAdapter);
    }
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
}