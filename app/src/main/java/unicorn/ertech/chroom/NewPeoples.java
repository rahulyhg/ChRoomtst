package unicorn.ertech.chroom;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Azat on 28.04.15.
 */
public class NewPeoples extends FragmentActivity {
    final String SAVED_COLOR = "color";
    long time1, time2;


    com.kpbird.triangletabs.PagerSlidingTabStrip tabStrip;
    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_peoples);

        pager = (ViewPager) findViewById(R.id.pager_priv);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabStrip = (com.kpbird.triangletabs.PagerSlidingTabStrip)findViewById(R.id.pagerTabStrip_priv);
        tabStrip.setViewPager(pager);

    }

    @Override
    public  void onResume(){
        super.onResume();
        tabStrip.setBackgroundResource(R.color.izum_blue);
        tabStrip.setTextColor(getResources().getColor(R.color.white));
        tabStrip.setTabPaddingLeftRight(0);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private final int FRAGMENTS = 3;
        private final List<Fragment> _fragments = new ArrayList<Fragment>();

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            _fragments.add(0, new NewPeoplesFragment());
            _fragments.add(1, new NewPeopleRatFragment());
            _fragments.add(2, new NewPeopleRatFragment());
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
                    return "Новые люди";
                case 1:
                    return "Рейтинг за сутки";
                case 2:
                    return "Рейтинг за все время";
            }
            return "Title " + position;
        }
    }
}
