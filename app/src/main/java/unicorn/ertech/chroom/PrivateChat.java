package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Timur on 04.01.2015.
 */
public class PrivateChat extends FragmentActivity {
    static final String TAG = "myLogs";
    final String SAVED_COLOR = "color";
    com.kpbird.triangletabs.PagerSlidingTabStrip tabStrip;
    long time1, time2;
    //static final int PAGE_COUNT = 4;

    /** идентификатор первого фрагмента Личный чат. */
    public static final int FRAGMENT_ONE = 0;
    /** идентификатор третего фрагмента Избранные. */
    public static final int FRAGMENT_THREE = 2;
    /** идентификатор второго фрагмета ЧС. */
    public static final int FRAGMENT_TWO = 1;
    /** количество фрагментов. */
    public static final int FRAGMENTS = 2;
    /** адаптер фрагментов. */
    private FragmentPagerAdapter _fragmentPagerAdapter;
    /** список фрагментов для отображения. */
    private final List<Fragment> _fragments = new ArrayList<Fragment>();
    /** сам ViewPager который будет все это отображать. */
    private ViewPager _viewPager;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_private);

        // создаем фрагменты.
        _fragments.add(FRAGMENT_ONE, new ConversationsFragment());
        _fragments.add(FRAGMENT_TWO, new FavoritesFragment());

        pager = (ViewPager) findViewById(R.id.pager_priv);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabStrip = (com.kpbird.triangletabs.PagerSlidingTabStrip)findViewById(R.id.pagerTabStrip_priv);
        tabStrip.setViewPager(pager);
        /*SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tabStrip.setTabBackground(R.color.blue);
            } else if (col == 0) {
                tabStrip.setTabBackground(R.color.green);
            } else if (col == 2) {
                tabStrip.setTabBackground(R.color.orange);
            } else if (col == 4) {
                tabStrip.setTabBackground(R.color.purple);
            }
        }*/
        /*pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
                case 0:
                    return "Сообщения";
                case 1:
                    return "Избранные";
            }
            return "Title " + position;
        }

    }

    @Override
    public  void onResume(){
        super.onResume();
        time1=0; time2=0;
        tabStrip.setTextColor(getResources().getColor(R.color.white));
        tabStrip.notifyDataSetChanged();
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                //tabs.setTabBackground(R.color.blue);
                tabStrip.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tabStrip.setBackgroundResource(R.color.green);
                //tabs.setTabBackground(R.color.green);
            } else if (col == 2) {
                //tabs.setTabBackground(R.color.orange);
                tabStrip.setBackgroundResource(R.color.orange);
            } else if (col == 3) {
                //tabs.setTabBackground(R.color.purple);
                tabStrip.setBackgroundResource(R.color.purple);
            }
            tabStrip.setIndicatorColorResource(R.color.white);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
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
        //openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                PrivateChat.this);
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
