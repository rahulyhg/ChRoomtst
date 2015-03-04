package unicorn.ertech.chroom;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

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
    public static String str;
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

    public static Intent srvs;


    ImageButton butProfile;
    ImageButton butSupport;
    ImageButton butSettings;
    public  static String URL = "http://im.topufa.org/index.php";

    final String SAVED_COLOR = "color";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabHost = getTabHost();
        Intent i = getIntent();
        str = i.getStringExtra("Token");
        topRow=(RelativeLayout)findViewById(R.id.topRow);
        butSettings = (ImageButton) findViewById(R.id.settingsButton);
        butSupport = (ImageButton) findViewById(R.id.imageButton);
        butProfile = (ImageButton) findViewById(R.id.usrPic);

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
        globaltab = tabHost.newTabSpec("Private");
        //View tabView2;
        //tabView2 = createTabView(tabHost.getContext(), "", R.drawable.icon2s);
        //globaltab.setIndicator(tabView2);
        Intent globalIntent = new Intent(this, Search.class);
        globaltab.setContent(globalIntent);


        // Вкладка
        newstab = tabHost.newTabSpec("Global");
        /*View tabView4;
        tabView4 = createTabView(tabHost.getContext(), "", R.drawable.icon4s);
        newstab.setIndicator(tabView4);*/
        Intent newsIntent = new Intent(this, GlobalChat.class);
        newstab.setContent(newsIntent);

        // Вкладка
        searchtab = tabHost.newTabSpec("News");
        //View tabView3;
        //tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3s);
        //searchtab.setIndicator(tabView3);
        Intent searchIntent = new Intent(this, NewsContainer.class);
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
            ed2.putString(SAVED_LASTID,ConversationsFragment.lastID4);
            ed2.commit();

            startService(srvs);
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

    public String readToken()//чтение токена из файла
    {
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

    public static void checkConnection() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.color_scheme_pick, menu);
        return super.onCreateOptionsMenu(menu);
    }


    protected  void setColor(){
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tabsSetColor(1);
                topRow.setBackgroundResource(R.color.blue);
                butSupport.setBackgroundResource(R.color.blue);
                butProfile.setBackgroundResource(R.color.blue);
                butSettings.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tabsSetColor(0);
                topRow.setBackgroundResource(R.color.green);
                butSupport.setBackgroundResource(R.color.green);
                butProfile.setBackgroundResource(R.color.green);
                butSettings.setBackgroundResource(R.color.green);
                tabHost.refreshDrawableState();
            } else if (col == 2) {
                tabsSetColor(2);
                topRow.setBackgroundResource(R.color.orange);
                butSupport.setBackgroundResource(R.color.orange);
                butProfile.setBackgroundResource(R.color.orange);
                butSettings.setBackgroundResource(R.color.orange);
                tabHost.refreshDrawableState();
            } else if(col == 3){
                tabsSetColor(3);
                topRow.setBackgroundResource(R.color.purple);
                butSupport.setBackgroundResource(R.color.purple);
                butProfile.setBackgroundResource(R.color.purple);
                butSettings.setBackgroundResource(R.color.purple);
                tabHost.refreshDrawableState();
            }
                curColor=col;
        }else{
            tabsSetColor(0);
            topRow.setBackgroundResource(R.color.green);
            butSupport.setBackgroundResource(R.color.green);
            butProfile.setBackgroundResource(R.color.green);
            butSettings.setBackgroundResource(R.color.green);
            tabHost.refreshDrawableState();
            curColor=0;
        }
    }

    protected void tabsSetColor(int color){
        View tabView3;
        switch (color){
            case 0:
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3s);
                searchtab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon1s);
                privatetab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon2s);
                globaltab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon4s);
                newstab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon5s);
                incognitotab.setIndicator(tabView3);
                tabHost.clearAllTabs();
                tabHost.addTab(privatetab);
                tabHost.addTab(globaltab);
                tabHost.addTab(newstab);
                tabHost.addTab(searchtab);
                tabHost.addTab(incognitotab);
                break;
            case 1:
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3sb);
                searchtab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon1sb);
                privatetab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon2sb);
                globaltab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon4sb);
                newstab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon5sb);
                incognitotab.setIndicator(tabView3);
                tabHost.clearAllTabs();
                tabHost.addTab(privatetab);
                tabHost.addTab(globaltab);
                tabHost.addTab(searchtab);
                tabHost.addTab(newstab);
                tabHost.addTab(incognitotab);
                break;
            case 2:
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3so);
                searchtab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon1so);
                privatetab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon2so);
                globaltab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon4so);
                newstab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon5so);
                incognitotab.setIndicator(tabView3);
                tabHost.clearAllTabs();
                tabHost.addTab(privatetab);
                tabHost.addTab(globaltab);
                tabHost.addTab(searchtab);
                tabHost.addTab(newstab);
                tabHost.addTab(incognitotab);
                break;
            case 3:
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon3sp);
                searchtab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon1sp);
                privatetab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon2sp);
                globaltab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon4sp);
                newstab.setIndicator(tabView3);
                tabView3 = createTabView(tabHost.getContext(), "", R.drawable.icon5sp);
                incognitotab.setIndicator(tabView3);
                tabHost.clearAllTabs();
                tabHost.addTab(privatetab);
                tabHost.addTab(globaltab);
                tabHost.addTab(searchtab);
                tabHost.addTab(newstab);
                tabHost.addTab(incognitotab);
                break;
            default:
                break;
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
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if(col!=curColor){
                setColor();
            }
        }
        //tabHost.setCurrentTab(curTab);
    }

    @Override
    public void onPause(){

        super.onPause();
    }

    /*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tabHost.setCurrentTab(savedInstanceState.getInt("mCurrentTab"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mCurrentTab", tabHost.getCurrentTab());
        super.onSaveInstanceState(outState);
    }*/
}

