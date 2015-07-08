package unicorn.ertech.chroom;

import android.app.NotificationManager;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 18.01.2015.
 */
public class ConversationsFragment extends Fragment {
    static Context context;

    ListView dialogs;
    final static String FILENAME = "dialogs";
    static conversationsAdapter adapter;
    static List<conversationsMsg> messages = new ArrayList<conversationsMsg>();
    Timer myTimer;
    static public  String token, realNum, fakeNum;
    public static String lastID4;
    conversationsMsg agent;
    static String URL = "http://im.topufa.org/index.php";
    newJsonParser jps = new newJsonParser();
    boolean stopTImer = false;
    static boolean firstTime;
    static List<Integer> favorites = new ArrayList<Integer>();
    static SharedPreferences Notif;
    static SharedPreferences.Editor ed2;
    final static String SAVED_NOTIF="notif";
    final String SAVED_SOUND="sound";
    final String SAVED_VIBRO="vibro";
    final String SAVED_INDICATOR="indicator";
    final static String SAVED_LASTID="lastid";
    static int currentPosition=0;
    static int requests=0;
    String serverTime;
    static int deltaMin;

    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_priv1, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        dialogs = (ListView)view.findViewById(R.id.lvConversations);

        adapter = new conversationsAdapter(messages,context);
        dialogs.setAdapter(adapter);
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        lastID4 = "";

        dialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {

                String UserId = adapter.getItem(position).uid;
                String nick = adapter.getItem(position).from;
                String fake = adapter.getItem(position).msgId;
                adapter.getItem(position).direction = "true";
                Intent i = new Intent(getActivity(),PrivateMessaging.class);
                i.putExtra("userId",UserId);
                i.putExtra("token",token);
                i.putExtra("nick",nick);
                i.putExtra("favorite","false");
                i.putExtra("mID",fake);
                i.putExtra("shake", "false");//если true - значит служба поддержки
                i.putExtra("userPROFILE",adapter.getItem(position).userid);
                i.putExtra("fromDialogs","true");
                i.putExtra("avatar",adapter.getItem(position).picURL);
                adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });

        messages.clear();
        favorites.clear();
        firstTime=true;
        new getLists().execute();
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    if(!stopTImer) {
                        new globalChat4().execute();
                        Log.d("timer","____");
                    }
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 3L * 1000);

        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static void update()
    {
        messages.clear();
        favorites.clear();
        FavoritesFragment.messages.clear();
        FavoritesFragment.adapter.notifyDataSetChanged();
        new getLists().execute();
    }

    public static void findNremove(String id)//удаляет чела с id из сиписка диалогов
    {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(id))
            {
                messages.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private class globalChat4 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("firstid", lastID4);

            jParser.setParam("action", "dialogs_get");
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                Log.e("lastID4", json.toString());
                boolean flag = false;
                String lastid = null;
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
                        lastID4 = Integer.toString(json.getInt("lastid"));
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
                            s = messag.getString("dialog_id");
                            Log.e("messag", messag.toString());
                            if(!favorites.contains(Integer.parseInt(s)))
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), "false", messag.getString("id"), messag.getString("time"),messag.getString("id"));
                                if(!checkInList(p)) {
                                    //messages.add(messages.size(), p);
                                    messages.add(0, p);
                                }
                            }
                            else
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), "false", messag.getString("id"), messag.getString("time"),messag.getString("id"));
                                FavoritesFragment.addList(p);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }
                    if(!realNum.equals("0")) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            requests++;
            if(requests>15){
                requests=0;
                if(messages.size()>0) {
                    String s = messages.get(0).userid;
                    new getOnline().execute(s);
                }
            }
        }
    }//конец asyncTask


    public static class getLists extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            FavoritesFragment.messages.clear();
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_list");
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                boolean flag = false;
                String lastid = null;
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
                    lastID4 = Integer.toString(json.getInt("lastid"));
                    //serverTime = json.getString("servertime");
                    Notif = context.getSharedPreferences("notifications",context.MODE_PRIVATE);
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
                            Log.e("getList", s);
                            real = new JSONArray(s);
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    for (int i = 0; i < Integer.parseInt(realNum); i++) {
                        try {
                            messag = new JSONObject(real.get(i).toString());
                            s = messag.getString("bookmarked");
                            if(s.equals("false"))
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), messag.getString("read"), messag.getString("lastid"), messag.getString("time"),messag.getString("userid"));
                                //messages.add(messages.size(), p);
                                messages.add(0, p);
                            }
                            else
                            {
                                favorites.add(Integer.parseInt(messag.getString("dialog_id")));
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), messag.getString("read"), messag.getString("lastid"), messag.getString("time"),messag.getString("userid"));
                                FavoritesFragment.addList(p);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }
            requests++;
            if((requests>15)||(firstTime)){
                requests=0;
                firstTime=false;
                if(messages.size()>0) {
                    String s = messages.get(0).userid;
                    new getOnline().execute(s);
                }
            }
        }
    }//конец asyncTask

    boolean findAgent(String UID)
    {
        boolean flag = false;
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(UID))
            {
                return true;
            }
        }

        return  flag;
    }

    void setAgentFirst(String UID)
    {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(UID))
            {
                conversationsMsg Myagent = messages.get(i);
                messages.remove(i);
                messages.add(0,Myagent);
                return;
            }
        }
    }



   public static boolean checkInList(conversationsMsg msg) {
        boolean flag = false;
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(msg.uid))
            {
                messages.get(i).message = msg.message;
                messages.get(i).time = msg.time;
                messages.get(i).direction = "false";
                messages.get(i).from=msg.from;
                conversationsMsg m = messages.get(i);
                messages.remove(i);
                //messages.add(messages.size(), m);
                messages.add(0, m);
                adapter.notifyDataSetChanged();
                flag = true;
            }
        }
        return  flag;
   }

    public static void newMsg(conversationsMsg p)
    {
        boolean flag = true;
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(p.uid))
            {
                flag = false;
                conversationsMsg m = new conversationsMsg(p.uid,messages.get(i).from,p.message,messages.get(i).picURL,p.direction,messages.get(i).msgId,p.time,p.userid);
                messages.remove(i);
                //messages.add(messages.size(), m);
                messages.add(0,m);
                adapter.notifyDataSetChanged();
            }
        }
        if(flag)
        {
            conversationsMsg m = new conversationsMsg(p.uid,p.from,p.message,p.picURL,p.direction,p.msgId,p.time, p.userid);
            messages.add(0,m);
            //messages.add(messages.size(), m);
        }
    }

    private void ReadDialogsFromFile()
    {
        String filePath = context.getFilesDir().getPath().toString() + FILENAME;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                try {
                    ObjectInputStream oin = new ObjectInputStream(fis);
                    try {
                        messages = (List<conversationsMsg>) oin.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    public void onDestroy(){
        myTimer.cancel();
        stopTImer=true;
        Log.e("json", "destroy");

        super.onDestroy();
    }
    @Override
    public void onPause(){
        //stopTImer=true;
        Notif = context.getSharedPreferences("notifications",context.MODE_PRIVATE);
        ed2 = Notif.edit();
        if(Notif.contains(SAVED_NOTIF))
        {
            if(Notif.getString(SAVED_NOTIF,"").equals("true"))
            {
                ed2.putString(SAVED_LASTID,lastID4);
                ed2.commit();

            }
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        stopTImer=false;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        super.onResume();
    }

    public static class getOnline extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "online_get");
            jParser.setParam("userid", args[0]);
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String s=null;
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s == null) s = "";

                if("false".equals(s)){
                    try{
                        s=json.getString("online");
                        if(messages.size()>0){
                            if(s.equals("true")){
                                messages.get(currentPosition).online="| online";
                            }else{
                                messages.get(currentPosition).online="";
                            }
                        }
                        currentPosition++;
                        if(currentPosition<messages.size()){
                            new getOnline().execute(messages.get(currentPosition).userid);
                        }else{
                            adapter.notifyDataSetChanged();
                            currentPosition=0;
                            FavoritesFragment.startOnlineCheck();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }
            }

        }
    }//конец asyncTask

    private void computeDeltaTime(String serverTime){
        int phoneTime;
        Calendar c=Calendar.getInstance();
        phoneTime=c.get(c.HOUR_OF_DAY)*60+c.get(c.MINUTE);
        String[] dateTime = serverTime.split("%");
        String[] time = dateTime[1].split(":");
        //String[] date = dateTime[0].split("-");

    }

}