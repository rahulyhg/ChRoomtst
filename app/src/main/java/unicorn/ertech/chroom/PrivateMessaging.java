package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 22.01.2015.
 */
public class PrivateMessaging extends Activity {
    ListView lvChat;
    EditText txtSend;
    Button butSend;
    Button butSmile;
    String URL = "http://im.topufa.org/index.php";
    TextView nick;
    ImageView avatar;
    int msgCount = 0;
    int scrolling = 0;
    String lastBlock;
    String picUrl;
    Date dateTime;

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

        butSend=(Button)findViewById(R.id.buttonSend);
        butSmile=(Button)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        dateTime = new Date();

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

        new getEarlierMessages().execute();

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
            jParser.setParam("action", "pm_send");
            //jParser.setParam("userid", myID);
            jParser.setParam("sendto", userId);
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
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0");
                    messages.add(msgCount,p);
                    Log.e("privatesend","666");
                    Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                    conversationsMsg p2 = new conversationsMsg(userId,nick.getText().toString(), outMsg,picUrl, "0",fake,c.get(c.YEAR)+"-"+month+ "-"+c.get(c.DAY_OF_MONTH)+"%"+c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+":"+c.get(c.SECOND));
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

    private class getEarlierMessages extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "pm_get");
            if(fake.equals("true")) {
                jParser.setParam("fakeid", userId);
            }
            else
            {
                jParser.setParam("userid", userId);
            }
            jParser.setParam("lastid", lastBlock);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                boolean flag = false;
                String lastid = null;
                JSONArray arr = null;
                String s = null;
                JSONObject messag = null;
                try {
                    msgNum = json.getString("total");
                    Log.e("msgNum",msgNum);
                    Log.e("msgNumJson",json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    flag = true;
                    s = json.getString("data");
                    arr = new JSONArray(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (flag && Integer.parseInt(msgNum) !=0) {
                    try {
                        lastBlock = json.getString("lastid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < Integer.parseInt(msgNum); i++) {

                        try {
                            messag = new JSONObject(arr.get(i).toString());

                            pmChatMessage p = new pmChatMessage(messag.getString("uid"), messag.getString("message"), messag.getString("direct"));
                            Log.e("addingMessage", messag.getString("message"));
                            messages.add(0, p);
                            msgCount++;

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
                        firstTime = false;
                    }
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
            jParser.setParam("action", "pm_get");
            jParser.setParam("new", "true");
            if(fake.equals("true")) {
                jParser.setParam("fakeid", userId);
            }
            else
            {
                jParser.setParam("userid", userId);
            }
            //jParser.setParam("lastid", lastBlock);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                boolean flag = false;
                String lastid = null;
                JSONArray arr = null;
                String s = null;
                JSONObject messag = null;
                try {
                    msgNum = json.getString("total");
                    Log.e("msgNum",msgNum);
                    Log.e("msgNumJson",json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    flag = true;
                    s = json.getString("data");
                    arr = new JSONArray(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (flag && Integer.parseInt(msgNum) !=0) {
                    try {
                        lastBlock = json.getString("lastid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < Integer.parseInt(msgNum); i++) {

                        try {
                            messag = new JSONObject(arr.get(i).toString());

                            pmChatMessage p = new pmChatMessage(messag.getString("uid"), messag.getString("message"), "1");
                            Calendar c=Calendar.getInstance();int month = c.get(c.MONTH)+1;
                            conversationsMsg p2 = new conversationsMsg(userId,nick.getText().toString(), messag.getString("message"),picUrl, "1",fake,c.get(c.YEAR)+"-"+month+"-"+c.get(c.DAY_OF_MONTH)+"%"+c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+":"+c.get(c.SECOND));
                            Log.e("addingMessage", messag.getString("message"));
                            messages.add(msgCount, p);
                            ConversationsFragment.newMsg(p2);
                            msgCount++;

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
        myTimer.cancel();
        Log.e("json", "destroy");
        super.onDestroy();
    }


}