package unicorn.ertech.chroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 04.01.2015.
 */


public class GlobalChat extends FragmentActivity{
    static final String TAG = "myLogs";
    final String SAVED_COLOR = "color";
    PagerTabStrip tabStrip;
    //static final int PAGE_COUNT = 4;

    /** идентификатор фрагмента города. */
    public static final int FRAGMENT_ONE = 1;
    /** идентификатор фрагмента страны. */
    public static final int FRAGMENT_THREE = 3;
    /** идентификатор фрагмета региона. */
    public static final int FRAGMENT_TWO = 2;
    /** идентификатор фрагмета объявлений. */
    public static final int FRAGMENT_FOUR = 0;
    /** количество фрагментов. */
    public static final int FRAGMENTS = 4;
    /** адаптер фрагментов. */
    private FragmentPagerAdapter _fragmentPagerAdapter;
    /** список фрагментов для отображения. */
    private final List<Fragment> _fragments = new ArrayList<Fragment>();
    /** сам ViewPager который будет все это отображать. */
    private ViewPager _viewPager;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    com.kpbird.triangletabs.PagerSlidingTabStrip tabs;
    final String SAVED_CITY = "city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_global);

        // создаем фрагменты.
        _fragments.add(FRAGMENT_FOUR, new CountryFragment()); //Объявления
        _fragments.add(FRAGMENT_ONE, new AdsFragment()); //Город
        _fragments.add(FRAGMENT_TWO, new CityFragment()); //Регион
        _fragments.add(FRAGMENT_THREE, new RegionFragment()); //Страна


        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        // Bind the tabs to the ViewPager
        tabs = (com.kpbird.triangletabs.PagerSlidingTabStrip) findViewById(R.id.pagerTabStrip);
        tabs.setViewPager(pager);
        //tabStrip = (PagerTabStrip)findViewById(R.id.pagerTabStrip);
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tabs.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tabs.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                tabs.setBackgroundResource(R.color.orange);
            } else if (col == 4) {
                tabs.setBackgroundResource(R.color.purple);
            }
        }
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(1);
        /*pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //PageFragment.pageNumber = position;
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return _fragments.get(position);
            //return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 1:
                    String[] city = getResources().getStringArray(R.array.cities);
                    SharedPreferences sPref = getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
                    if(sPref.contains(SAVED_CITY)){
                        return city[sPref.getInt(SAVED_CITY,11)-11];
                    }
                    return "Уфа";
                case 2:
                    return "Башкортостан";
                case 3:
                    return "Россия";
                case 0:
                    return "Объявления";
            }
            return "Title " + position;
        }

    }

    @Override
    public  void onResume(){
        super.onResume();
        tabs.setTextColor(getResources().getColor(R.color.white));
        //tabs.setBackgroundResource(R.color.green);
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tabs.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tabs.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                tabs.setBackgroundResource(R.color.orange);
            } else if (col == 4) {
                tabs.setBackgroundResource(R.color.purple);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                GlobalChat.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }
}
