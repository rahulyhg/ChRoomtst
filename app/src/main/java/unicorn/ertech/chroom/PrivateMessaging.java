package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timur on 22.01.2015.
 */
public class PrivateMessaging extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView lvChat;
    EditText txtSend;
    ImageButton butSend;
    ImageButton butLists;
    ImageButton butSmile;
    String URL = "http://im.topufa.org/index.php";
    TextView nick;
    ImageView avatar;
    int msgCount = 0;
    int scrolling = 0;
    int lastBlock = 0;
    String picUrl, myID;
    Date dateTime;
    String shake;
    RelativeLayout topRow;
    SharedPreferences userData;
    final String USER = "user";
    String fromDIALOGS;
    String lastID4;
    SwipeRefreshLayout swipeLayout;

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
        userData = getSharedPreferences("user", MODE_PRIVATE);

        myID = Integer.toString(userData.getInt(USER,0));
        SharedPreferences userD = getSharedPreferences("notifications", Context.MODE_PRIVATE);
        if((userD.contains("notif"))){
            if(!userD.getString("lastid", "0").equals("0")){
                lastID4=userD.getString("lastid", "");
            }
        }
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        butSend=(ImageButton)findViewById(R.id.buttonSend);
        butSmile=(ImageButton)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butLists=(ImageButton)findViewById(R.id.ibStar);
        BB=(TableRow)findViewById(R.id.big_button);
        final TableLayout smileTable = (TableLayout)findViewById(R.id.smileTablePm);
        topRow=(RelativeLayout)findViewById(R.id.topRowChat);
        dateTime = new Date();

        butLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }});
        butBack=(ImageButton)findViewById(R.id.butNewsBack);
        findSmiles();

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

        adapter = new pmChatAdapter(messages,getApplicationContext());
        lvChat.setAdapter(adapter);
        lastId = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        favorite = i.getStringExtra("favorite");
        //mID = i.getStringExtra("mID");
        userProfile = i.getStringExtra("userPROFILE");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        fromDIALOGS = i.getStringExtra("fromDialogs");
        if(fromDIALOGS.equals("false"))
        {
            sendTo = i.getStringExtra("userId");
        }

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container_private);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.green, R.color.blue, R.color.orange, R.color.purple);
        swipeLayout.setDistanceToTriggerSync(20);

        shake = i.getStringExtra("shake");
        if(shake.equals("true")){
            butLists.setVisibility(View.INVISIBLE);
        }
        Picasso.with(getApplicationContext()).load(picUrl).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new PicassoRoundTransformation()).fit().into(avatar);

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
                int savedPosition = lvChat.getLastVisiblePosition();
                lvChat.setSelection(savedPosition);
                //lvChat.setSelection
                /*if (smileTable.getVisibility() == View.GONE) {
                    smileTable.setVisibility(View.VISIBLE);
                } else {
                    smileTable.setVisibility(View.GONE);
                }*/
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
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                butBack.setBackgroundResource(R.drawable.but_green);
                butLists.setBackgroundResource(R.drawable.but_green);
                BB.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                butBack.setBackgroundResource(R.drawable.but_orange);
                butLists.setBackgroundResource(R.drawable.but_orange);
                BB.setBackgroundResource(R.drawable.but_orange);
            } else if(col == 3){
                topRow.setBackgroundResource(R.color.purple);
                butBack.setBackgroundResource(R.drawable.but_purple);
                butLists.setBackgroundResource(R.drawable.but_purple);
                BB.setBackgroundResource(R.drawable.but_purple);
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
                jParser.setParam("type", "1");
            }
            jParser.setParam("message", outMsg);
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    try {
                        userId = json.getString("dialogid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0");
                    messages.add(msgCount,p);
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
                    adapter.notifyDataSetChanged();
                    Log.e("privatesend","888");
                    lvChat.setSelection(adapter.getCount());
                    Log.e("privatesend","999");
                    txtSend.setText("");
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
                    else {
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
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
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
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "0");
                                    messages.add(0, p);
                                    msgCount++;
                                } else {
                                    userProfile = messag.getString("userid");
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1");
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
                            Toast.makeText(getApplicationContext(), "Истоия переписки пуста!", Toast.LENGTH_SHORT).show();
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
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
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
                        if(Integer.parseInt(lastId)>Integer.parseInt(lastID4)) {
                            lastID4 = lastId;
                        }
                        ed2 = Notif.edit();
                        if(Notif.contains(SAVED_NOTIF))
                        {
                            if(Notif.getString(SAVED_NOTIF,"").equals("true"))
                            {
                                ed2.putString(SAVED_LASTID,lastID4);
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

                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1");
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
                    //ConversationsFragment.update();
                    Toast.makeText(getApplicationContext(), "Пользователь успешно удален из избранного!", Toast.LENGTH_LONG).show();
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



    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private void findSmiles(){
        s01 = (ImageView) findViewById(R.id.s0p1);
        s01.setOnClickListener(smile_click_listener);
        s02 = (ImageView) findViewById(R.id.s0p2);
        s02.setOnClickListener(smile_click_listener);
        s03 = (ImageView) findViewById(R.id.s0p3);
        s03.setOnClickListener(smile_click_listener);
        s04 = (ImageView) findViewById(R.id.s0p4);
        s04.setOnClickListener(smile_click_listener);
        s05 = (ImageView) findViewById(R.id.s0p5);
        s05.setOnClickListener(smile_click_listener);
        s06 = (ImageView) findViewById(R.id.s0p6);
        s06.setOnClickListener(smile_click_listener);
        s07 = (ImageView) findViewById(R.id.s0p7);
        s07.setOnClickListener(smile_click_listener);
        s08 = (ImageView) findViewById(R.id.s0p8);
        s08.setOnClickListener(smile_click_listener);
        s09 = (ImageView) findViewById(R.id.s0p9);
        s09.setOnClickListener(smile_click_listener);
        s10 = (ImageView) findViewById(R.id.s0p0);
        s10.setOnClickListener(smile_click_listener);
    }
    private View.OnClickListener smile_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.s0p1:
                    txtSend.append(":)");
                    break;
                case R.id.s0p2:
                    txtSend.append(":D");
                    break;
                case R.id.s0p3:
                    txtSend.append(":O");
                    break;
                case R.id.s0p4:
                    txtSend.append(":(");
                    break;
                case R.id.s0p5:
                    txtSend.append("*05*");
                    break;
                case R.id.s0p6:
                    txtSend.append("Z)");
                    break;
                case R.id.s0p7:
                    txtSend.append("*07*");
                    break;
                case R.id.s0p8:
                    txtSend.append("*08*");
                    break;
                case R.id.s0p9:
                    txtSend.append("*09*");
                    break;
                case R.id.s0p0:
                    txtSend.append("*love*");
                    break;
                default:
                    break;
            }
            txtSend.setText(getSmiledText(getApplicationContext(),txtSend.getText()));
        }
    };
    //
    //Ниже часть, связанная с отображением смайлов в edittext
    //

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ":)", R.drawable.s01);
        addPattern(emoticons, ":D", R.drawable.s02);
        addPattern(emoticons, ":O", R.drawable.s03);
        addPattern(emoticons, ":(", R.drawable.s04);
        addPattern(emoticons, "*05*", R.drawable.s05);
        addPattern(emoticons, "Z)", R.drawable.s06);
        addPattern(emoticons, "*07*", R.drawable.s07);
        addPattern(emoticons, "*08*", R.drawable.s08);
        addPattern(emoticons, "*09*", R.drawable.s09);
        addPattern(emoticons, "*love*", R.drawable.s10);
        // ...
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
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
                    //ConversationsFragment.update();
                    Toast.makeText(getApplicationContext(), "Собеседник успешно добавлен в избранное!", Toast.LENGTH_SHORT).show();
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
                    //ConversationsFragment.update();
                    Toast.makeText(getApplicationContext(), "Истоия сообщений успешно удалена!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Пользователь и так находится у Вас в избранном!", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getApplicationContext(), "Пользователь и так не находится у Вас в избранном!", Toast.LENGTH_LONG).show();
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
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        NotOut = true;
    }

    @Override
    public void onDestroy(){
        myTimer.cancel();

        Log.e("json", "destroy");
        super.onDestroy();
    }
}