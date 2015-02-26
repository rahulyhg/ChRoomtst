package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class PrivateMessaging extends Activity {
    ListView lvChat;
    EditText txtSend;
    ImageButton butSend;
    ImageButton butLists;
    Button butSmile;
    String URL = "http://im.topufa.org/index.php";
    TextView nick;
    ImageView avatar;
    int msgCount = 0;
    int scrolling = 0;
    String lastBlock;
    String picUrl, myID;
    Date dateTime;
    SharedPreferences userData;
    final String USER = "user";
    String fromDIALOGS;

    List<pmChatMessage> messages = new ArrayList<pmChatMessage>();
    pmChatAdapter adapter;
    Timer myTimer;

    String token, sendTo;
    boolean firstTime = true;
    String userId, msgNum, lastId, outMsg, mID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);

        userData = getSharedPreferences("user", MODE_PRIVATE);

        myID = Integer.toString(userData.getInt(USER,0));

        butSend=(ImageButton)findViewById(R.id.buttonSend);
        butSmile=(Button)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butLists=(ImageButton)findViewById(R.id.ibStar);
        dateTime = new Date();

        butLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }});
        final Button  butBack=(Button)findViewById(R.id.butNewsBack);
        nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                Intent i = new Intent(context,Profile2.class);
                i.putExtra("userId",userId);
                i.putExtra("token",token);
                i.putExtra("nick",nick.getText());
                i.putExtra("avatar",picUrl);
                startActivity(i);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butBack.callOnClick();
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
        lastBlock = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        mID = i.getStringExtra("mID");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        fromDIALOGS = i.getStringExtra("fromDialogs");
        if(fromDIALOGS.equals("false"))
        {
            sendTo = i.getStringExtra("userId");
        }
        Picasso.with(getApplicationContext()).load(picUrl).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new PicassoRoundTransformation()).fit().into(avatar);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(isNetworkAvailable()) {
                    outMsg = txtSend.getText().toString();
                    Log.e("privatesend", "111");
                    new OutMsg().execute();
                    imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);

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
                // TODO Auto-generated method stub
                initiatePopupWindow();
            }
        });

        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    scrolling = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0 && scrolling > firstVisibleItem) {
                    new getEarlierMessages().execute();
                }
                scrolling = firstVisibleItem;
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
            jParser.setParam("action", "dialogs_send");
            if(fromDIALOGS.equals("false"))
            {
                jParser.setParam("sendto", sendTo);
            }
            else {
                jParser.setParam("dialogid", userId);
            }
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            Log.e("privatesend","333");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("receivedjson", "2222");
            Log.e("privatesend","444");
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
                    conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND));
                    ConversationsFragment.newMsg(p2);



                    msgCount++;
                    Log.e("privatesend","777");
                    adapter.notifyDataSetChanged();
                    Log.e("privatesend","888");
                    lvChat.setSelection(adapter.getCount());
                    Log.e("privatesend","999");
                    txtSend.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class getEarlierMessages extends AsyncTask<String, String, JSONObject> {/////////нужен свой ID
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
            jParser.setParam("firstid", lastBlock);
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
                        lastBlock = json.getString("firstid");
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
                    for (int i = 0; i < Integer.parseInt(realNum); i++) {
                        try {
                            messag = new JSONObject(real.get(i).toString());
                            msgID = messag.getString("userid");
                            if(msgID.equals(myID))
                            {
                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "0");
                                messages.add(0,p);
                                msgCount++;
                            }
                            else {
                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1");
                                messages.add(0,p);
                                msgCount++;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(adapter.getCount());
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
            jParser.setParam("lastid", lastId);
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
                    lvChat.setSelection(adapter.getCount());
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



    private PopupWindow pwindo;
    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private void initiatePopupWindow() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View view = inflater.inflate(R.layout.fragment_blank, container, false);
            View layout = inflater.inflate(R.layout.smile_popup,  null, false);
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int window_width = metricsB.widthPixels;
            int window_height = metricsB.heightPixels;
            pwindo = new PopupWindow(layout, window_width, window_height, true);
            pwindo.showAsDropDown(butSmile,0,-window_height);
            Button smileCancel = (Button) layout.findViewById(R.id.button3);
            smileCancel.setOnClickListener(smile_click_listener);
            layout.setOnClickListener(smile_click_listener);
            s01 = (ImageView) layout.findViewById(R.id.s01);
            s01.setOnClickListener(smile_click_listener);
            s02 = (ImageView) layout.findViewById(R.id.s02);
            s02.setOnClickListener(smile_click_listener);
            s03 = (ImageView) layout.findViewById(R.id.s03);
            s03.setOnClickListener(smile_click_listener);
            s04 = (ImageView) layout.findViewById(R.id.s04);
            s04.setOnClickListener(smile_click_listener);
            s05 = (ImageView) layout.findViewById(R.id.s05);
            s05.setOnClickListener(smile_click_listener);
            s06 = (ImageView) layout.findViewById(R.id.s06);
            s06.setOnClickListener(smile_click_listener);
            s07 = (ImageView) layout.findViewById(R.id.s07);
            s07.setOnClickListener(smile_click_listener);
            s08 = (ImageView) layout.findViewById(R.id.s08);
            s08.setOnClickListener(smile_click_listener);
            s09 = (ImageView) layout.findViewById(R.id.s09);
            s09.setOnClickListener(smile_click_listener);
            s10 = (ImageView) layout.findViewById(R.id.s10);
            s10.setOnClickListener(smile_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener smile_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.s01:
                    txtSend.append(":)");
                    break;
                case R.id.s02:
                    txtSend.append(":D");
                    break;
                case R.id.s03:
                    txtSend.append(":O");
                    break;
                case R.id.s04:
                    txtSend.append(":(");
                    break;
                case R.id.s05:
                    txtSend.append("*05*");
                    break;
                case R.id.s06:
                    txtSend.append("Z)");
                    break;
                case R.id.s07:
                    txtSend.append("*07*");
                    break;
                case R.id.s08:
                    txtSend.append("*08*");
                    break;
                case R.id.s09:
                    txtSend.append("*09*");
                    break;
                case R.id.s10:
                    txtSend.append("*love*");
                    break;
                default:
                    pwindo.dismiss();
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
                    FavoritesFragment.updateList();
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
            jParser.setParam("addid", userId);
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
                    FavoritesFragment.updateList();
                    Toast.makeText(getApplicationContext(), "Пользователь успешно добавлен в черный список!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(getApplicationContext(),String.valueOf(item.getItemId()), Toast.LENGTH_LONG).show();
                        if(item.getItemId() == R.id.menu_favorites) {
                            new setFavorite().execute();
                        }
                         if(item.getItemId() == R.id.menu_blacklist) {
                             new setBlackList().execute();
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
}