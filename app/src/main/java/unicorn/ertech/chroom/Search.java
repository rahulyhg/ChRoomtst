package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Timur on 04.01.2015.
 */
public class Search extends FragmentActivity {

    final String SAVED_COLOR = "color";

    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENTS = 2;
    private final List<Fragment> _fragments = new ArrayList<Fragment>();

    ViewPager pager;
    PagerAdapter pagerAdapter;
    com.kpbird.triangletabs.PagerSlidingTabStrip tabs;
    long time1, time2;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;



    @Override
    public void onBackPressed() {

        Calendar calend = Calendar.getInstance();
        if(time1==0){
            time1=calend.getTimeInMillis();
            Toast.makeText(getApplicationContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
        }else{
            time2=calend.getTimeInMillis();
            if(time2-time1<=2000){
                finish();
            }else{
                time1=time2;
                Toast.makeText(getApplicationContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("searchCreate",)
        setContentView(R.layout.search_main);

//        Analytics

        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker(R.xml.tracker_config);
        tracker.enableAdvertisingIdCollection(true);

//        Analytics

        _fragments.add(FRAGMENT_ONE, new ShareNetworks());
        _fragments.add(FRAGMENT_TWO, new SearchParam());

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        // Bind the tabs to the ViewPager
        tabs = (com.kpbird.triangletabs.PagerSlidingTabStrip) findViewById(R.id.pagerTabStrip);
        tabs.setViewPager(pager);
        pager.setCurrentItem(1);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return _fragments.get(position);
        }

        @Override
        public int getCount() {
            return FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "Поделиться в соцсетях";
                case 1:
                    return "Поиск";
            }
            return "Title " + position;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onResume(){
        super.onResume();
        tabs.setTextColor(getResources().getColor(R.color.white));
        tabs.setTabPaddingLeftRight(0);
        tabs.setBackgroundResource(R.color.izum_blue);
        time1=0; time2=0;
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
