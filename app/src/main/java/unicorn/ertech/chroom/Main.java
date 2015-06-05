package unicorn.ertech.chroom;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Timur on 04.01.2015.
 */

public class Main extends TabActivity {
    /**
     * Called when the activity is first created.
     */
    String str;
    SharedPreferences sPref;
    Integer curColor;

    TabSpec searchtab;
    TabSpec globaltab;
    TabSpec privatetab;
    TabSpec incognitotab;
    TabSpec newstab;
    TabHost tabHost;
    RelativeLayout topRow;
    SharedPreferences Notif;
    SharedPreferences.Editor ed2;
    final String SAVED_NOTIF="notif";
    final String SAVED_SOUND="sound";
    final String SAVED_VIBRO="vibro";
    final String SAVED_INDICATOR="indicator";
    final String SAVED_LASTID="lastid";
    int pic_width2;
    String lastID4="";

    Intent srvs;


    ImageButton butProfile;
    ImageButton butSupport;
    ImageButton butSettings;
    public  static String URL = "http://im.topufa.org/index.php";

    final String SAVED_COLOR = "color";
    final String SAVE_TAB = "tab";

    View tabViewTMP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabHost = getTabHost();
        Intent i = getIntent();
        str = i.getStringExtra("Token");

        topRow=(RelativeLayout)findViewById(R.id.topRowAbout);
        butSettings = (ImageButton) findViewById(R.id.settingsButton);
        butSupport = (ImageButton) findViewById(R.id.imageButton);
        butProfile = (ImageButton) findViewById(R.id.usrPic);

        SharedPreferences Notif2 = getSharedPreferences("notifications",Context.MODE_PRIVATE);
        if(Notif2.contains(SAVED_LASTID))
        {
               lastID4=Notif2.getString(SAVED_LASTID,"");
        }

        // Вкладка
        privatetab = tabHost.newTabSpec("Private");
        // устанавливаем заголовок и иконку
        /*View tabView;
        tabView = createTabView(tabHost.getContext(), "", R.drawable.icon1s);
        privatetab.setIndicator(tabView);*/
        // устанавливаем окно, которая будет показываться во вкладке
        Intent privateIntent;
        privateIntent = new Intent(this, PrivateChat.class);
        privatetab.setContent(privateIntent);

        // Вкладка
        globaltab = tabHost.newTabSpec("Global");
        //View tabView2;
        //tabView2 = createTabView(tabHost.getContext(), "", R.drawable.icon2s);
        //globaltab.setIndicator(tabView2);
        Intent globalIntent = new Intent(this, Search.class);
        globaltab.setContent(globalIntent);


        // Вкладка
        newstab = tabHost.newTabSpec("News");
        /*View tabView4;
        tabView4 = createTabView(tabHost.getContext(), "", R.drawable.icon4s);
        newstab.setIndicator(tabView4);*/
        Intent newsIntent = new Intent(this, GlobalChat.class);
        newstab.setContent(newsIntent);

        // Вкладка
        searchtab = tabHost.newTabSpec("Search");
        //View tabView3;
        //tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3s);
        //searchtab.setIndicator(tabView3);
        Intent searchIntent = new Intent(this, NewPeoples.class);
        searchtab.setContent(searchIntent);

        // Вкладка
        incognitotab = tabHost.newTabSpec("Incognito");
        /*View tabView5;
        tabView5 = createTabView(tabHost.getContext(), "", R.drawable.icon5s);
        incognitotab.setIndicator(tabView5);*/
        Intent incognitoIntent = new Intent(this, IncognitoTab.class);
        incognitotab.setContent(incognitoIntent);

        setColor();

         /*tabHost.addTab(privatetab);
        tabHost.addTab(globaltab);
        tabHost.addTab(searchtab);
        tabHost.addTab(newstab);
        tabHost.addTab(incognitotab);*/


        butSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), SetActivity.class);
                startActivity(in);
            }
        });

        butSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent(getApplicationContext(), Support.class);
                startActivity(in);*/
                Intent i = new Intent(getApplicationContext(), PrivateMessaging.class);
                i.putExtra("userId","0");
                i.putExtra("userPROFILE", "0");
                i.putExtra("token",str);
                i.putExtra("nick","Служба поддержки");
                i.putExtra("favorite","false");
                i.putExtra("shake", "true");//значит служба поддержки
                i.putExtra("fromDialogs","false");
                startActivity(i);
            }
        });

        butProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), Profile.class);
                startActivity(in);
            }
        });
        srvs = new Intent(this, notif.class);
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        ed2 = Notif.edit();
        if(Notif.contains(SAVED_NOTIF))
        {

        }
        else
        {
            ed2.putString(SAVED_NOTIF,"true");
            ed2.putString(SAVED_SOUND,"true");
            ed2.putString(SAVED_VIBRO,"true");
            ed2.putString(SAVED_INDICATOR,"true");
            ed2.putString(SAVED_LASTID,lastID4);
            ed2.commit();

            startService(srvs);
        }

        Picasso mPicasso = Picasso.with(getApplicationContext());
        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width2=(int)(50*metricsB.density);
        SharedPreferences userData;
        userData = getSharedPreferences("user", MODE_PRIVATE);
        if(userData.contains("avatar_link")){
            String url = userData.getString("avatar_link", "");
            if((!url.equals(""))&&(!url.equals("http://im.topufa.org/"))){
                mPicasso.load(url).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(butProfile);
            }
        }
        if(!userData.contains("density")){
            SharedPreferences.Editor ed = userData.edit();
            ed.putFloat("density", metricsB.density);
            ed.commit();
        }
    }

    private static View createTabView(final Context context, final String text, int id) {

        View view = LayoutInflater.from(context).inflate(R.layout.tabs_lay, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView2);
        ImageView left = (ImageView) view.findViewById(R.id.imageView3);
        ImageView right = (ImageView) view.findViewById(R.id.imageView4);
        //TextView tv = (TextView) view.findViewById(R.id.tabsText);
        //tv.setText(text);
        iv.setImageResource(id);
        left.setImageResource(R.drawable.gradients);
        right.setImageResource(R.drawable.gradients2);
        return view;
    }

    public String readToken(){ //чтение токена из файла
        FileInputStream fIn = null;
        try {
            fIn = openFileInput("token.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fIn);
        char[] inputBuffer = new char[40];
        try {
            isr.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String readString = new String(inputBuffer);

        return readString;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    protected void setColor(){
        topRow.setBackgroundResource(R.color.izum_blue);
        butSupport.setBackgroundResource(R.color.izum_blue);
        butProfile.setBackgroundResource(R.color.izum_blue);
        butSettings.setBackgroundResource(R.color.izum_blue);

        View tabView1;
        tabView1 = createTabView(tabHost.getContext(), "", R.drawable.icon3sb);
        searchtab.setIndicator(tabView1);
        tabView1 = createTabView(tabHost.getContext(), "", R.drawable.icon1sb);
        privatetab.setIndicator(tabView1);
        tabView1 = createTabView(tabHost.getContext(), "", R.drawable.icon2sb);
        globaltab.setIndicator(tabView1);
        tabView1 = createTabView(tabHost.getContext(), "", R.drawable.icon4sb);
        newstab.setIndicator(tabView1);
        tabView1 = createTabView(tabHost.getContext(), "", R.drawable.icon5sb);
        incognitotab.setIndicator(tabView1);
        tabHost.clearAllTabs();
        tabHost.addTab(privatetab); //личка
        tabHost.addTab(globaltab); //поиск
        tabHost.addTab(newstab); //общий чат
        tabHost.addTab(searchtab); //новости
        tabHost.addTab(incognitotab); //инкогнито

        sPref = getPreferences(MODE_PRIVATE);
        String tabID = sPref.getString(SAVE_TAB, "Private");
        tabHost.setCurrentTabByTag(tabID);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                Main.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("да!", new DialogInterface.OnClickListener() {
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

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        setColor();
    }

    @Override
    public void onPause(){
        super.onPause();
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putString(SAVE_TAB, tabHost.getCurrentTabTag());
        ed.commit();
    }

    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences userData;
        userData = getSharedPreferences("user", MODE_PRIVATE);
        if(userData.contains("avatar_link")){
            Picasso mPicasso = Picasso.with(getApplicationContext());
            Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            pic_width2=(int)(50*metricsB.density);
            String url = userData.getString("avatar_link", "");
            if((!url.equals(""))&&(!url.equals("http://im.topufa.org/"))){
                mPicasso.load(url).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(butProfile);
            }
        }
    }
}

