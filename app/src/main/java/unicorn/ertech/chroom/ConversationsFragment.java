package unicorn.ertech.chroom;

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
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
    String token, realNum, fakeNum, lastID4;
    conversationsMsg agent;
    String URL = "http://im.topufa.org/index.php";
    newJsonParser jps = new newJsonParser();
    boolean stopTImer = false ;
    List<Integer> favorites = new ArrayList<Integer>();

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
        token = Main.str;
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
                i.putExtra("mID",fake);
                i.putExtra("fromDialogs","true");
                i.putExtra("avatar",adapter.getItem(position).picURL);
                adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });

        messages.clear();
        favorites.clear();
        new getLists().execute();
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    if(!stopTImer) {
                        new globalChat4().execute();
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

    public static void findNremove(String id)//удаляет чела с id из сиписка диалогов
    {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(id))
            {
                messages.remove(i);
                adapter.notifyDataSetChanged();
                WriteDialogsToFile();
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

                            if(!favorites.contains(Integer.parseInt(s)))
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), "false", messag.getString("id"), messag.getString("time"));
                                messages.add(0, p);
                            }
                            else
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), "false", messag.getString("id"), messag.getString("time"));
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

        }
    }//конец asyncTask


    private class getLists extends AsyncTask<String, String, JSONObject> {
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
                            s = messag.getString("bookmarked");

                            if(s.equals("false"))
                            {
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), messag.getString("read"), messag.getString("lastid"), messag.getString("time"));
                                messages.add(0, p);
                            }
                            else
                            {
                                favorites.add(Integer.parseInt(messag.getString("dialog_id")));
                                conversationsMsg p = new conversationsMsg(messag.getString("dialog_id"), messag.getString("name"), messag.getString("message"), messag.getString("avatar"), messag.getString("read"), messag.getString("lastid"), messag.getString("time"));
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



   public boolean checkInList(conversationsMsg msg) {
        boolean flag = false;
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(msg.uid))
            {
                messages.get(i).message = msg.message;
                messages.get(i).time = msg.time;
                conversationsMsg m = messages.get(i);
                m.direction = "1";
                messages.remove(i);
                messages.add(0,m);
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
                conversationsMsg m = new conversationsMsg(p.uid,messages.get(i).from,p.message,messages.get(i).picURL,p.direction,messages.get(i).msgId,p.time);
                messages.remove(i);
                messages.add(0,m);
                adapter.notifyDataSetChanged();
            }
        }
        if(flag)
        {
            conversationsMsg m = new conversationsMsg(p.uid,p.from,p.message,p.picURL,p.direction,p.msgId,p.time);
            messages.add(0,m);
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

    private static void WriteDialogsToFile()
    {
        String filePath = context.getFilesDir().getPath().toString() + FILENAME;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(messages);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        //myTimer.cancel();
        WriteDialogsToFile();
        stopTImer=true;
        super.onPause();
    }

    @Override
    public void onResume() {
        stopTImer=false;
        super.onResume();
    }
}
