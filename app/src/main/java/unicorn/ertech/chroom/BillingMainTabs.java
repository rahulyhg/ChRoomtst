package unicorn.ertech.chroom;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Timur on 10.07.2015.
 */
public class BillingMainTabs extends FragmentActivity {
    ImageButton butBack;
    RelativeLayout topRow;
    Context context;
    static String URL = "http://im.topufa.org/index.php";
    String token="";
    TextView count;
    BillingFragmentsAdapter pagerAdapter;

    /** идентификатор фрагмента */
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    /** количество фрагментов. */
    public static final int FRAGMENTS = 2;
    /** список фрагментов для отображения. */
    private final ArrayList<Fragment> _fragments = new ArrayList<Fragment>();

    com.kpbird.triangletabs.PagerSlidingTabStrip tabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_maintabs);
        context = this;

        butBack=(ImageButton)findViewById(R.id.butBackWal);
        count = (TextView) findViewById(R.id.count);

        token=DataClass.getToken();

        _fragments.add(FRAGMENT_ONE, new Billing());
        _fragments.add(FRAGMENT_TWO, new sms());


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerBilling);
        pagerAdapter = new BillingFragmentsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // Bind the tabs to the ViewPager
        tabs = (com.kpbird.triangletabs.PagerSlidingTabStrip) findViewById(R.id.pagerTabStripBilling);
        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.white));

        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        new getBalance().execute();
    }

    public void closeMe(){
        this.finish();
    }

    private  class getBalance extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //?????? ?????? ??? ?????????
            jParser.setParam("token", token);
            jParser.setParam("action", "balance_get");
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String s="";
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if("false".equals(s)){
                    try {
                        //String s1=new String("??????: " + json.getString("balance")+" ???????? ", "");
                        count.setText(json.getString("balance")+" ");
                        count.append(getResources().getString(R.string.Wallet2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }else{
                    if(s.equals("13")){
                        Toast.makeText(getApplicationContext(), "???????? ?????", Toast.LENGTH_SHORT);
                    }else if(s.equals("22")){
                        Toast.makeText(getApplicationContext(),"?? ???????????????? ? ????????", Toast.LENGTH_SHORT);
                    }
                }
            }

        }
    }//????? asyncTask

    private class BillingFragmentsAdapter extends FragmentPagerAdapter {

        public BillingFragmentsAdapter(FragmentManager fm) {
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
                    return "Google Play";
                case 1:
                    return "SMS";
            }
            return "Title " + position;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(_fragments.get(0).getTag());
        if (fragment != null)
        {
            ((Billing)fragment).onActivityResult(requestCode, resultCode,data);
        }
    }

}
