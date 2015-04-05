package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class anonMessaging extends Activity {

    ListView lvChat;
    EditText txtSend;
    ImageButton butSend;
    ImageButton butSmile;
    ImageButton butStar;
    String URL = "http://im.topufa.org/index.php";
    TextView nick;
    ImageView avatar;
    int msgCount = 0;
    int scrolling = 0;
    String lastBlock;
    String picUrl;
    Date dateTime;
    boolean flag=false;

    List<pmChatMessage> messages = new ArrayList<pmChatMessage>();
    pmChatAdapter adapter;
    Timer myTimer;

    String token;
    boolean firstTime = true;
    String userId, msgNum, lastId, outMsg, fake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);

        butSend=(ImageButton)findViewById(R.id.buttonSend);
        butSmile=(ImageButton)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butStar=(ImageButton)findViewById(R.id.ibStar);
        dateTime = new Date();

        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        adapter = new pmChatAdapter(messages,getApplicationContext());
        lvChat.setAdapter(adapter);
        lastId = "0";
        lastBlock = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        fake = i.getStringExtra("fake");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        Picasso.with(getApplicationContext()).load(picUrl).transform(new PicassoRoundTransformation()).fit().into(avatar);

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

        butStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new request().execute();
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
                }
                scrolling = firstVisibleItem;
            }
        });


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
        final TableLayout smileTable = (TableLayout)findViewById(R.id.smileTablePm);
        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int savedPosition = lvChat.getFirstVisiblePosition();
                lvChat.setSelectionFromTop(savedPosition,60);
                //lvChat.setSelection
                if (smileTable.getVisibility() == View.GONE) {
                    smileTable.setVisibility(View.VISIBLE);
                } else {
                    smileTable.setVisibility(View.GONE);
                }
            }
        });

        findSmiles();
    }

    private class OutMsg extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "anonimus_pm_send");
            //jParser.setParam("userid", myID);
            jParser.setParam("fakeid", userId);
            jParser.setParam("message", outMsg);
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("anonDebugSendParams", jParser.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                Log.i("anonDebugSend", json.toString());
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0");
                    messages.add(msgCount,p);
                    msgCount++;
                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(adapter.getCount());
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
            jParser.setParam("action", "anonimus_pm_get");
            jParser.setParam("fakeid", userId);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                JSONArray arr = null;
                String s = null;
                JSONObject messag = null;
                Log.i("anonDebug", json.toString());
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (s.equals("false")){
                    try {
                        msgNum = json.getString("total");
                        s = json.getString("data");
                        arr = new JSONArray(s);
                        lastId = json.getString("lastid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < Integer.parseInt(msgNum); i++) {

                        try {
                            messag = new JSONObject(arr.get(i).toString());
                            s = messag.getString("system");
                            if(s.equals("0")) {
                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1");
                                messages.add(msgCount, p);
                                msgCount++;
                            }

                            if(s.equals("3"))//пришел завпрос на открытие профиля
                            {
                                openQuitDialog();
                            }

                            if(s.equals("7"))//собеседник вышел из переписки
                            {
                                openLeaveDialog();
                                flag=true;
                            }

                            if(s.equals("4"))//профили взаимно открыты
                            {
                                Intent in = new Intent(getApplicationContext(),Profile2.class);
                                in.putExtra("userId",messag.getString("uid"));
                                in.putExtra("token",token);
                                in.putExtra("nick",nick.getText());
                                in.putExtra("avatar",picUrl);
                                startActivity(in);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void onDestroy(){
        if(!flag) {
            new leaving().execute();
        }
        myTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onPause(){
        myTimer.cancel();

        super.onDestroy();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                anonMessaging.this);
        quitDialog.setTitle("Собеседник предлагает открыть друг другу профили");

        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new confirm().execute();
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

    private void openLeaveDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                anonMessaging.this);
        quitDialog.setTitle("Собеседник покинул беседу!");

        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                close();
            }
        });

        quitDialog.show();
    }

    void close()
    {
        this.finish();
    }

    private class confirm extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "anonimus_pm_send");
            jParser.setParam("fakeid", userId);
            jParser.setParam("status", "4");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("anonDebugConfirm", json.toString());
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {

                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class request extends AsyncTask<String, String, JSONObject> {//запрос на открытие профилей

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "anonimus_pm_send");
            jParser.setParam("fakeid", userId);
            jParser.setParam("status", "3");
            Log.i("anonDebugRequestSend", jParser.toString());
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("anonDebugRequest", json.toString());
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    Toast.makeText(getApplicationContext(), "Запрос успешно отправлен!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        status = json.getString("error_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("65"))
                    {
                        Toast.makeText(getApplicationContext(), "Вы исчерпали все попытки!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class leaving extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "anonimus_pm_send");
            jParser.setParam("fakeid", userId);
            jParser.setParam("status", "7");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("anonDebugLeave", json.toString());
            if (json != null) {
                String status = "";
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0");
                    messages.add(msgCount,p);
                    msgCount++;
                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(adapter.getCount());
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
}
