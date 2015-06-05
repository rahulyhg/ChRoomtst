package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class PrivateMessaging extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView lvChat;
    Context context;
    //ProgressBar progressBar;
    //net.heybird.utils.CircleProgressBar progressBar;
    CircleProgressBar progressBar;
    EditText txtSend;
    ImageButton butSend;
    ImageButton butLists;
    ImageButton butSmile, butFile;
    String URL = "http://im.topufa.org/index.php", attached_ID="";
    TextView nick, tvCancelAttach;
    ImageView avatar, attachedPhoto;
    int msgCount = 0;
    int scrolling = 0;
    int lastBlock = 0, pic_width=100;
    String picUrl, myID;
    Date dateTime;
    String shake, attached_link="";
    RelativeLayout topRow, rlAttach;
    SharedPreferences userData, savedStrings;
    final String USER = "user";
    String fromDIALOGS;
    String lastID4;
    boolean fromShake;
    SwipeRefreshLayout swipeLayout;
    String pathToUserPhoto = new String();
    static final int GALLERY_REQUEST = 1;
    final Object nullObject = null;

    SharedPreferences Notif;
    SharedPreferences.Editor ed2;
    final String SAVED_NOTIF="notif";
    final String SAVED_SOUND="sound";
    final String SAVED_VIBRO="vibro";
    final String SAVED_INDICATOR="indicator";
    final String SAVED_COLOR = "color";
    final String SAVED_LASTID="lastid";

    conversationsMsg p3;
    List<pmChatMessage> messages = new ArrayList<pmChatMessage>();
    pmChatAdapter adapter;
    Timer myTimer;
    ImageButton butBack;
    TableRow BB;

    String token, sendTo, userProfile, favorite;
    boolean firstTime = true;
    boolean NotOut = true;
    String userId, msgNum, lastId, outMsg, mID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        context=getApplicationContext();
        userData = getSharedPreferences("user", MODE_PRIVATE);

        myID = Integer.toString(userData.getInt(USER,0));

        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        if((Notif.contains(SAVED_LASTID))){
            lastID4=Notif.getString(SAVED_LASTID, "0");
        }
        butSend=(ImageButton)findViewById(R.id.buttonSend);
        butSmile=(ImageButton)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butLists=(ImageButton)findViewById(R.id.ibStar);
        butFile=(ImageButton)findViewById(R.id.butFile);
        attachedPhoto=(ImageView)findViewById(R.id.ivAttachedPhoto);
        BB=(TableRow)findViewById(R.id.big_button);
        tvCancelAttach=(TextView)findViewById(R.id.tvCancelAttach);
        final TableLayout smileTable = (TableLayout)findViewById(R.id.smileTablePm);
//        final SmileManager sMgr = new SmileManager(this, this, getCurrentFocus());
        topRow=(RelativeLayout)findViewById(R.id.topRowChat);
        rlAttach=(RelativeLayout)findViewById(R.id.rlAttach);
        progressBar=(CircleProgressBar)findViewById(R.id.pbPhoto);

        dateTime = new Date();

        lvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        butLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }});
        butBack=(ImageButton)findViewById(R.id.butNewsBack);

        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width=(int)(100*metricsB.density);

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

        tvCancelAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attached_ID="";
                rlAttach.setVisibility(View.GONE);
                attachedPhoto.setVisibility(View.GONE);
                tvCancelAttach.setVisibility(View.GONE);
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        butFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        adapter = new pmChatAdapter(messages,getApplicationContext());
        lvChat.setAdapter(adapter);
        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String photo = adapter.getItem(position).attach;
                if(!photo.equals("false")) {
                    Intent i = new Intent(context, PhotoViewerPm.class);
                    i.putExtra("photos", photo);
                    startActivity(i);
                }
            }
        });
        lastId = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        favorite = i.getStringExtra("favorite");
        if(favorite.equals("false")){
            if(ConversationsFragment.favorites.contains(Integer.parseInt(userId))){
                favorite="true";
            }
        }
        //mID = i.getStringExtra("mID");
        userProfile = i.getStringExtra("userPROFILE");
        sendTo = i.getStringExtra("userPROFILE");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        fromShake=i.getBooleanExtra("fromShake", false);
        fromDIALOGS = i.getStringExtra("fromDialogs");
        if(fromDIALOGS.equals("false"))
        {
            //sendTo = i.getStringExtra("userId");
        }

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container_private);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.green, R.color.blue, R.color.orange, R.color.purple);
        swipeLayout.setDistanceToTriggerSync(20);

        shake = i.getStringExtra("shake");
        if(shake.equals("true")){
            butLists.setVisibility(View.INVISIBLE);
            pmChatMessage p = new pmChatMessage("0","Здравствуйте! Опишите Вашу проблему максимально подробно, наши агенты свяжутся с Вами в ближайшее время.", "1", "");
            messages.add(0, p);
            msgCount++;
            adapter.notifyDataSetChanged();
        }
        if(fromShake){
            ConversationsFragment.update();
        }
        Picasso.with(getApplicationContext()).load(picUrl).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).resize(80,0).transform(new PicassoRoundTransformation()).into(avatar);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(isNetworkAvailable()) {
                    outMsg = txtSend.getText().toString();
                    new OutMsg().execute();
                    //imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
                }
            }
        });

        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int savedPosition = lvChat.getFirstVisiblePosition();
                lvChat.setSelectionFromTop(savedPosition,60);
                //lvChat.setSelection
//                if (smileTable.getVisibility() == View.GONE) {
//                    sMgr.setVisibleSmile(true);
//
//                } else {
//                    sMgr.setVisibleSmile(false);
//
//                }
            }
        });

        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //scrolling = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0 && scrolling > firstVisibleItem) {
                    if(lastBlock!=-1) {
                       // new getEarlierMessages().execute();
                    }
                }
                //scrolling = firstVisibleItem;
            }
        });

        if(fromDIALOGS.equals("true")) {
            new getEarlierMessages().execute();
        }

        Log.i("pm", "first");
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                Log.e("tokenBeforeRequest", token);
                if (isNetworkAvailable()) {
                    new getLastMessage().execute();
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 2L * 1000);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setColor();
    }

    private void setColor(){
        SharedPreferences sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                topRow.setBackgroundResource(R.color.blue);
                butBack.setBackgroundResource(R.drawable.but_blue);
                butLists.setBackgroundResource(R.drawable.but_blue);
                BB.setBackgroundResource(R.drawable.but_blue);
                butFile.setBackgroundResource(R.drawable.but_blue);
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                butBack.setBackgroundResource(R.drawable.but_green);
                butLists.setBackgroundResource(R.drawable.but_green);
                BB.setBackgroundResource(R.drawable.but_green);
                butFile.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                butBack.setBackgroundResource(R.drawable.but_orange);
                butLists.setBackgroundResource(R.drawable.but_orange);
                BB.setBackgroundResource(R.drawable.but_orange);
                butFile.setBackgroundResource(R.drawable.but_orange);
            } else if(col == 3){
                topRow.setBackgroundResource(R.color.purple);
                butBack.setBackgroundResource(R.drawable.but_purple);
                butLists.setBackgroundResource(R.drawable.but_purple);
                BB.setBackgroundResource(R.drawable.but_purple);
                butFile.setBackgroundResource(R.drawable.but_purple);;
            }
        }else{
            topRow.setBackgroundResource(R.color.green);
        }
    }
    @Override
    public void onRefresh() {
            swipeLayout.setRefreshing(true);
            new getEarlierMessages().execute();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                    //parser.parseAsync();
                }
            }, 1000);

    }

    private class OutMsg extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
            butSend.setEnabled(false);
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            if (!shake.equals("true")){
                jParser.setParam("action", "dialogs_send");
                if (fromDIALOGS.equals("false")) {
                    jParser.setParam("sendto", sendTo);
                } else {
                    jParser.setParam("dialogid", userId);
            }
            }
            else
            {
                jParser.setParam("action", "get_support");
                jParser.setParam("sendto", sendTo);
                jParser.setParam("type", "2");
            }
            if(!attached_ID.equals("")){
                jParser.setParam("attache", attached_ID);
                if(outMsg.equals("")){
                    outMsg="Изображение";
                }
            }
            jParser.setParam("message", outMsg);
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmOutSend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("pmOut",json.toString());
            butSend.setEnabled(true);
            if (json != null) {
                String status = "";
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ("false".equals(status)) {
                    attached_ID="";
                    rlAttach.setVisibility(View.GONE);
                    attachedPhoto.setVisibility(View.GONE);
                    tvCancelAttach.setVisibility(View.GONE);
                    try {
                        userId = json.getString("dialogid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0", attached_link);
                    messages.add(msgCount,p);
                    attached_link="";
                    Log.e("privatesend","666");
                    Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                    conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                    p3 = p2;
                    if(favorite.equals("true"))
                    {
                        FavoritesFragment.newMsg(p2);
                    }
                    else {
                        ConversationsFragment.newMsg(p2);
                    }

                    msgCount++;
                    Log.e("privatesend","777");

                    Log.e("privatesend","888");
                    lvChat.setSelection(adapter.getCount());
                    Log.e("privatesend","999");
                    txtSend.setText("");
                    if(shake.equals("true")){
                        pmChatMessage p3 = new pmChatMessage("0", "Спасибо, Ваша заявка принята на рассмотрение, Вам ответят в ближайшее время. Ответ Вы сможете увидеть в личных сообщениях", "1", "");
                        messages.add(msgCount, p3);
                        msgCount++;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    try {
                        status = json.getString("error_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("65"))
                    {
                        Toast.makeText(getApplicationContext(), "Нельзя отправлять больше 5 сообщений в день службе поддержки!", Toast.LENGTH_LONG).show();
                    }
                    else if(status.equals("63")){
                        Toast.makeText(getApplicationContext(), "Вы в чёрном списке этого пользователя", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class getEarlierMessages extends AsyncTask<String, String, JSONObject> {/////////нужен свой ID
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PrivateMessaging.this);
            pDialog.setMessage("Загрузка сообщений ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_get");
            jParser.setParam("page", String.valueOf(lastBlock));
            Log.e("lastB", String.valueOf(lastBlock));
            jParser.setParam("dialogid", userId);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmEarlySend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("pmEarly",json.toString());
            if(json!=null) {

                String realNum = "";
                JSONArray real = null;
                String s = null;
                String msgID = "";
                JSONObject messag = null;
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s.equals("false")){
                    try {
                        realNum = json.getString("total");
                        Log.e("num", realNum);
                        lastId = json.getString("lastid");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(!realNum.equals("0")){
                            lastBlock++;
                            scrolling = Integer.parseInt(realNum);
                            s = json.getString("data");
                            real = new JSONArray(s);
                        }
                        else {
                            scrolling = 0;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < Integer.parseInt(realNum); i++) {
                        try {
                            messag = new JSONObject(real.get(i).toString());
                            msgID = messag.getString("userid");
                            if (!messag.getString("message").equals("1/!")){
                                if (msgID.equals(myID)) {
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "0", messag.getString("attache"));
                                    messages.add(0, p);
                                    msgCount++;
                                } else {
                                    userProfile = messag.getString("userid");
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", messag.getString("attache"));
                                    messages.add(0, p);
                                    msgCount++;
                                }
                        }

                            Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                            conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                            p3 = p2;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if(firstTime)
                    {
                        lvChat.setSelection(adapter.getCount());
                        if(Integer.parseInt(realNum)==0)
                        {
                            Toast.makeText(getApplicationContext(), "История переписки пуста!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        lvChat.setSelection(scrolling);
                        if(Integer.parseInt(realNum)==0)
                        {
                            Toast.makeText(getApplicationContext(), "Больше сообщений нет!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                }
            }

        }
    }//конец asyncTask

    private class getLastMessage extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_get");
            jParser.setParam("dialogid", userId);
            jParser.setParam("lastid", "");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmLastSend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            //Log.i("pmLast",json.toString());
            if(json!=null) {
                String realNum = "";
                JSONArray real = null;
                String s = null;
                JSONObject messag = null;
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s.equals("false")){
                    try {
                        realNum = json.getString("total");
                        lastId = json.getString("lastid");
                        if(!lastId.equals(null)){
                            if(Integer.parseInt(lastId)>Integer.parseInt(lastID4)) {
                                lastID4 = lastId;
                                ed2 = Notif.edit();
                                ed2.putString(SAVED_LASTID,lastID4);
                                if(Integer.parseInt(lastId)>Integer.parseInt(ConversationsFragment.lastID4)) {
                                    ConversationsFragment.lastID4 = lastId;}
                                ed2.commit();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(!realNum.equals("0")){
                            s = json.getString("data");
                            real = new JSONArray(s);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(firstTime == false) {
                        for (int i = 0; i < Integer.parseInt(realNum); i++) {
                            try {
                                messag = new JSONObject(real.get(i).toString());
                                Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                                conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), messag.getString("message"), picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                                if (!messag.getString("message").equals("1/!")) {
                                    if (NotOut) {
                                        if (favorite.equals("false")) {
                                            ConversationsFragment.newMsg(p2);
                                        } else {
                                            FavoritesFragment.newMsg(p2);
                                        }
                                    }
                                }

                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", messag.getString("attache"));
                                messages.add(msgCount, p);
                                msgCount++;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                Log.e("NullPointerException", e.toString());
                            }
                        }
                    }
                    else
                    {
                        firstTime = false;
                    }

                    adapter.notifyDataSetChanged();
                    if(!realNum.equals("0")) {
                        lvChat.setSelection(adapter.getCount());
                    }
                }
            }

        }
    }//конец asyncTask

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

                if(status.equals("false"))
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
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

                if (status.equals("false")) {
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

                if (status.equals("false")) {

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

                if (status.equals("false")) {
                    msgCount=0;
                    messages.clear();
                    adapter.notifyDataSetChanged();
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

    //Всплывающее меню
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.add_list_menu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.menu_favorites) {

                            if(favorite.equals("true"))
                            {
                                Toast.makeText(getApplicationContext(), "Пользователь уже находится у Вас в друзьях!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                new setFavorite().execute();
                            }
                        }
                         if(item.getItemId() == R.id.menu_blacklist) {
                            new setBlackList().execute();
                        }
                        if(item.getItemId() == R.id.menu_clear_history) {
                            if(shake.equals("false")) {
                                new clearHistory().execute();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Нельзя удалять историю переписки со службой поддержки!", Toast.LENGTH_LONG).show();
                            }
                        }
                        if(item.getItemId() == R.id.menu_out_from_favorites) {
                            if(favorite.equals("true"))
                            {
                                new remove().execute();
                            }
                            else
                            {
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

    @Override
    public void onPause()
    {
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
        NotOut = false;
        savedStrings=getPreferences(MODE_PRIVATE);
        ed2=savedStrings.edit();
        ed2.putString(userId, txtSend.getText().toString());
        ed2.commit();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        NotOut = true;
        savedStrings=getPreferences(MODE_PRIVATE);
        if(savedStrings.contains(userId)){
            txtSend.setText(savedStrings.getString(userId,""));
        }
    }

    @Override
    public void onDestroy(){
        myTimer.cancel();

        Log.e("json", "destroy");
        super.onDestroy();
    }

    private class sendUserPhoto extends AsyncTask<String, Integer, JSONObject> {
        long totalSent, fileSize;
        int i;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rlAttach.setVisibility(View.VISIBLE);
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            String responseString = new String();
            JSONObject json = null;
            try
            {
                HttpClient client = new DefaultHttpClient();
                File file = new File(pathToUserPhoto);
                HttpPost post = new HttpPost(URL);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addBinaryBody("file", file, ContentType.create("image/jpeg"), file.getName());
                entityBuilder.addTextBody("action", "picture_upload");
                entityBuilder.addTextBody("token", token);
                i=0;
                // add more key/value pairs here as needed
                final HttpEntity entity = entityBuilder.build();
                fileSize=entity.getContentLength();
                //Log.v("result", EntityUtils.toString(httpEntity));
                class ProgressiveEntity implements HttpEntity {
                    @Override
                    public void consumeContent() throws IOException {
                        entity.consumeContent();
                    }
                    @Override
                    public InputStream getContent() throws IOException,
                            IllegalStateException {
                        return entity.getContent();
                    }
                    @Override
                    public Header getContentEncoding() {
                        return entity.getContentEncoding();
                    }
                    @Override
                    public long getContentLength() {
                        return entity.getContentLength();
                    }
                    @Override
                    public Header getContentType() {
                        return entity.getContentType();
                    }
                    @Override
                    public boolean isChunked() {
                        return entity.isChunked();
                    }
                    @Override
                    public boolean isRepeatable() {
                        return entity.isRepeatable();
                    }
                    @Override
                    public boolean isStreaming() {
                        return entity.isStreaming();
                    } // CONSIDER put a _real_ delegator into here!

                    @Override
                    public void writeTo(OutputStream outstream) throws IOException {

                        class ProxyOutputStream extends FilterOutputStream {
                            /**
                             * @author Stephen Colebourne
                             */

                            public ProxyOutputStream(OutputStream proxy) {
                                super(proxy);
                            }
                            public void write(int idx) throws IOException {
                                out.write(idx);
                            }
                            public void write(byte[] bts) throws IOException {
                                out.write(bts);
                            }
                            public void write(byte[] bts, int st, int end) throws IOException {
                                out.write(bts, st, end);
                            }
                            public void flush() throws IOException {
                                out.flush();
                            }
                            public void close() throws IOException {
                                out.close();
                            }
                        } // CONSIDER import this class (and risk more Jar File Hell)

                        class ProgressiveOutputStream extends ProxyOutputStream {
                            public ProgressiveOutputStream(OutputStream proxy) {
                                super(proxy);
                            }
                            public void write(byte[] bts, int st, int end) throws IOException {

                                // FIXME  Put your progress bar stuff here!

                                out.write(bts, st, end);
                                totalSent += end;
                                if(totalSent>(fileSize/10)){
                                    i++;
                                    Log.i("progressI", Integer.toString(i));
                                    publishProgress((int)(10*totalSent/fileSize));
                                    //totalSent=0;
                                }
                            }
                        }

                        entity.writeTo(new ProgressiveOutputStream(outstream));
                    }

                };
                ProgressiveEntity myEntity = new ProgressiveEntity();

                post.setEntity(myEntity);
                HttpResponse response = client.execute(post);
                final HttpEntity httpEntity = response.getEntity();
                responseString = EntityUtils.toString(httpEntity);
                json = new JSONObject(responseString);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progressBar.setVisibility(View.GONE);
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    Log.e("saveToken", e.toString());
                }
                if(status.equals("false"))
                {
                    Toast.makeText(getApplicationContext(), "Изображение успешно загружено на сервер!", Toast.LENGTH_LONG).show();
                    try {
                        attached_ID=json.getString("attached_id");
                        attached_link=json.getString("link");
                        attachedPhoto.setVisibility(View.VISIBLE);
                        tvCancelAttach.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(attached_link).resize(pic_width, 0).noFade().into(attachedPhoto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при отправке изображения!", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("progress", Integer.toString(values[0]));
            //Log.i("progressbar", Integer.toString(progressBar.getProgress()));
            progressBar.setProgress(values[0]*10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData(); //Это путь картинки
                    pathToUserPhoto = getImagePath(selectedImage);
                    Picasso.with(context).load(selectedImage).resize(pic_width, 0).noFade().into(attachedPhoto);
                    attachedPhoto.setVisibility(View.VISIBLE);
                    new sendUserPhoto().execute();
                }
        }
    }

    protected String getImagePath(Uri uri){
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}