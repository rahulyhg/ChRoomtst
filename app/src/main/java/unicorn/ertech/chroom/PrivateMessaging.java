package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import com.giljulio.imagepicker.ui.ImagePickerActivity;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import me.tedyin.circleprogressbarlib.CircleProgressBar;

/**
 * Created by Timur on 22.01.2015.
 */
public class PrivateMessaging extends FragmentActivity /*implements SwipeRefreshLayout.OnRefreshListener*/{

    private Context context;
    private LinearLayout topRow;
    private TableRow BB;

    private ImageButton butLists, butFile, butBack;
    private ImageView avatar;
    private TextView nick;
    private String lastID4;

    private String token, sendTo, userProfile, favorite,  picUrl;
    private boolean fromShake;
    private String userId, lastId, outMsg, shake, myID, fromDIALOGS;

    private SharedPreferences Notif, userData, savedStrings;
    private SharedPreferences.Editor ed2;

    final String URL = "http://im.topufa.org/index.php";
    final String USER = "user";
    final String SAVED_LASTID="lastid";
    final String SAVED_NOTIF="notif";

    private PrivateMessageFragment privateMessageFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);

        userData = getSharedPreferences("user", MODE_PRIVATE);


        myID = Integer.toString(userData.getInt(USER,0));

        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        if((Notif.contains(SAVED_LASTID))){
            lastID4=Notif.getString(SAVED_LASTID, "0");
        }


        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        context=getApplicationContext();

        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butLists=(ImageButton)findViewById(R.id.ibStar);
        butFile=(ImageButton)findViewById(R.id.butFile);
        topRow=(LinearLayout)findViewById(R.id.topRowChat);
        BB=(TableRow)findViewById(R.id.big_button);
        butBack=(ImageButton)findViewById(R.id.butNewsBack);


        butLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }});

        BB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Profile2.class);
                i.putExtra("userId",userProfile);
                i.putExtra("token",token);
                i.putExtra("nick",nick.getText().toString());
                i.putExtra("avatar",picUrl);
                i.putExtra("userPROFILE", userProfile);
                i.putExtra("fromMessaging", true);
                startActivity(i);
            }
        });

        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lastId = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        favorite = i.getStringExtra("favorite");
        //mID = i.getStringExtra("mID");
        userProfile = i.getStringExtra("userPROFILE");
        sendTo = i.getStringExtra("userPROFILE");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        fromShake=i.getBooleanExtra("fromShake", false);
        shake = i.getStringExtra("shake");
        fromDIALOGS = i.getStringExtra("fromDialogs");

        if(shake.equals("true")){
            butLists.setVisibility(View.INVISIBLE);
        }

        if(fromShake){
            ConversationsFragment.update();
        }
        Picasso.with(getApplicationContext())
                .load(picUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(80,0)
                .transform(new PicassoRoundTransformation())
                .into(avatar);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("myID", myID);
        map.put("lastID4", lastID4);
        map.put("nick", nick.getText().toString());
        map.put("picUrl", picUrl);
        map.put("token", token);
        map.put("userId", userId);
        map.put("shake", shake);
        map.put("fromDIALOGS", fromDIALOGS);
        map.put("favorite", favorite);
        map.put("sendTo", sendTo);
        ConnectPrivate cp = new ConnectPrivate(map);
        Bundle bundle = new Bundle();
        bundle.putParcelable("key", cp);


        privateMessageFragment = new PrivateMessageFragment();
        privateMessageFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.container_for_fragment, privateMessageFragment)
                .commit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setColor();
    }

    private void setColor(){
        topRow.setBackgroundResource(R.color.izum_blue);
        butBack.setBackgroundResource(R.drawable.but_blue);
        butLists.setBackgroundResource(R.drawable.but_blue);
        BB.setBackgroundResource(R.drawable.but_blue);
        butFile.setBackgroundResource(R.drawable.but_blue);
    }

    private class remove extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_delete");
            jParser.setParam("list", "1");
            jParser.setParam("deleteid",userId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String status = null;
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if("false".equals(status))
                {
                    favorite = "false";
                    FavoritesFragment.findNremove(userId);
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "Пользователь успешно удален из списка друзей!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при удалении из списка!", Toast.LENGTH_LONG).show();
                }

            }

        }
    }//конец asyncTask

    private class setFavorite extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_add");
            jParser.setParam("addid", userId);
            jParser.setParam("list", "1");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ("false".equals(status)) {
                    favorite = "true";
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "Собеседник успешно добавлен в друзья!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class setBlackList extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_add");
            jParser.setParam("addid", userProfile);
            jParser.setParam("list", "2");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ("false".equals(status)) {

                    Toast.makeText(getApplicationContext(), "Пользователь успешно добавлен в черный список!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class clearHistory extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(PrivateMessaging.this);
            pDialog.setMessage("Удаление переписки ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_swipe");
            jParser.setParam("dialogid", userId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ("false".equals(status)) {
                    privateMessageFragment.clearHistory();
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "История сообщений успешно удалена!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при очистке!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.add_list_menu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_favorites) {

                    if (favorite.equals("true")) {
                        Toast.makeText(getApplicationContext(), "Пользователь уже находится у Вас в друзьях!", Toast.LENGTH_LONG).show();
                    } else {
                        new setFavorite().execute();
                    }
                }
                if (item.getItemId() == R.id.menu_blacklist) {
                    new setBlackList().execute();
                }
                if (item.getItemId() == R.id.menu_clear_history) {
                    if (shake.equals("false")) {
                        new clearHistory().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Нельзя удалять историю переписки со службой поддержки!", Toast.LENGTH_LONG).show();
                    }
                }
                if (item.getItemId() == R.id.menu_out_from_favorites) {
                    if (favorite.equals("true")) {
                        new remove().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Пользователь и так не находится у Вас в друзьях!", Toast.LENGTH_LONG).show();
                    }
                }


                return false;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }
//
    @Override
    public void onPause(){
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        ed2 = Notif.edit();
        if(Notif.contains(SAVED_NOTIF))
        {
            if(Notif.getString(SAVED_NOTIF,"").equals("true"))
            {
                ed2.putString(SAVED_LASTID,lastID4);
                ed2.commit();
                Intent srvs = new Intent(this, notif.class);
                startService(srvs);
            }
        }

        savedStrings=getPreferences(MODE_PRIVATE);
        ed2=savedStrings.edit();
        ed2.putString(userId, ((TextView)privateMessageFragment.getView().findViewById(R.id.editText1)).getText().toString());
        ed2.commit();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        savedStrings=getPreferences(MODE_PRIVATE);
        if(savedStrings.contains(userId)){
            ((TextView)privateMessageFragment.getView().findViewById(R.id.editText1)).setText(savedStrings.getString(userId,""));
        }
    }

    @Override
    public void onBackPressed() {
        if (!privateMessageFragment.windowDismiss()){
            privateMessageFragment.clearText();
            finish();
        }
    }
}