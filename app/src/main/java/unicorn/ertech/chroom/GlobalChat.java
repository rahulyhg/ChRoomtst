package unicorn.ertech.chroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Timur on 04.01.2015.
 */


public class GlobalChat extends FragmentActivity{
    static final String TAG = "myLogs";
    final String SAVED_COLOR = "color";
    //static final int PAGE_COUNT = 4;

    /** идентификатор фрагмента города. */
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    public static final int FRAGMENT_FOUR = 3;
    /** количество фрагментов. */
    public static final int FRAGMENTS = 4;
    /** список фрагментов для отображения. */
    private final ArrayList<Fragment> _fragments = new ArrayList<Fragment>();
    /** сам ViewPager который будет все это отображать. */

    com.kpbird.triangletabs.PagerSlidingTabStrip tabs;
    final String SAVED_CITY = "city";
    long time1, time2;
    int lastFragment = 0;
    PagerAdapter pagerAdapter;

    int activeFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_global);
        // создаем фрагменты.
        _fragments.add(FRAGMENT_ONE, new IncognitoChat());
        _fragments.add(FRAGMENT_TWO, new AdsFragment());
        _fragments.add(FRAGMENT_THREE, new CityFragment());
        _fragments.add(FRAGMENT_FOUR, new RegionFragment());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerGCH);
        pagerAdapter = new GlobalChatAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // Bind the tabs to the ViewPager
        tabs = (com.kpbird.triangletabs.PagerSlidingTabStrip) findViewById(R.id.pagerTabStrip);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                activeFragment = position;
                InterfaceSet fragOld = (InterfaceSet) _fragments.get(lastFragment);
                fragOld.Stop();
                hideKeyBoard();
                fragOld.windowDismiss();
                InterfaceSet fragNew = (InterfaceSet) _fragments.get(position);
                fragNew.Start(position);
                lastFragment = position;
                Log.e(TAG, "Position = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs.setTextColor(getResources().getColor(R.color.white));
        tabs.setBackgroundResource(R.color.izum_blue);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(activeFragment);
    }

    private class GlobalChatAdapter extends FragmentPagerAdapter {

        public GlobalChatAdapter(FragmentManager fm) {
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
                    return "Анономный чат";
                case 1:
                    String[] city = getResources().getStringArray(R.array.cities);
                    SharedPreferences sPref = getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
                    Log.e("myLog", sPref.getString("cityStr", "Уфа"));
                    Log.e("myLog", city[sPref.getInt(SAVED_CITY, 9)]);
                    if(sPref.contains("cityStr")){
                        return sPref.getString("cityStr", "Уфа");
                    }else {
                        if (sPref.contains(SAVED_CITY)) {
                            return city[sPref.getInt(SAVED_CITY, 9)];
                        }
                    }
                    return "Уфа";
                case 2:
                    String[] region = getResources().getStringArray(R.array.regions);
                    SharedPreferences sPref2 = getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
                    if(sPref2.contains("region")){
                        return region[sPref2.getInt("region",0)];
                    }
                    return "Башкортостан";
                case 3:
                    return "Россия";
            }
            return "Title " + position;
        }
    }

    private void hideKeyBoard(){
        ((InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    @Override
    public  void onResume(){
        super.onResume();
        tabs.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<FRAGMENTS; i++){
                    InterfaceSet frag = (InterfaceSet) _fragments.get(i);
                    frag.Stop();
                }
                InterfaceSet frag = (InterfaceSet) _fragments.get(1);
                frag.Start(1);
            }
        }, 1000);
        //Log.i("globalresume","glogalresume");
        time1=0; time2=0;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        InterfaceSet fragment = (InterfaceSet)_fragments.get(activeFragment);
        if(!fragment.windowDismiss()){

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((InterfaceSet)_fragments.get(lastFragment)).windowDismiss();
        hideKeyBoard();
    }
}
