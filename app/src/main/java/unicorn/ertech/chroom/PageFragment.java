package unicorn.ertech.chroom;

/**
 * Created by Timur on 04.01.2015.
 */

//
//Вроде вообще не используется, удалить перед релизом
//

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public int pageNumber;
    int backColor;
    int msgCount;
    ListView lvChat;
    ArrayList<String> messagesUfa,messagesRB, messagesRUS, messagesNews,listArr;
    ArrayAdapter<String> adapterUfa, adapterRB, adapterRUS, adapterNews, lstAdptr;
    JSONObject json;
    Timer myTimer;
    boolean firsTime;
    String URL = "http://im.topufa.org/index.php";
    String lastID1, lastID2,lastID3,lastID4, msgNum, room, outMsg, token, myID;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        // messagesNews.add(0,"News");
        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                Log.e("tokenBeforeRequest", token);
                if (isNetworkAvailable()) {
                    switch (pageNumber)
                    {
                        case 0:
                            room = "1";
                            new globalChat1().execute();
                            break;
                        case 1:
                            room = "2";
                            new globalChat2().execute();
                            break;
                        case 2:
                            room = "3";
                            new globalChat3().execute();
                            break;
                        case 3:
                            room = "4";
                            new globalChat4().execute();
                            break;
                    }
                }
                else
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 1000, 2L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, null);
        //final TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
        ImageButton butSend = (ImageButton) view.findViewById(R.id.button2);
        lvChat = (ListView)view.findViewById(R.id.lvChat);
        final EditText txtSend = (EditText) view.findViewById(R.id.sendText);
        firsTime = true;
        //token = Main.str;
        room = "1";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        myID = "1";
        listArr = new ArrayList<String>();

        lstAdptr = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listArr);

        lvChat.setAdapter(lstAdptr);
        //tvPage.setText("Page " + pageNumber);
        //tvPage.setBackgroundColor(backColor);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

                switch (pageNumber)
                {
                    case 0:
                        room = "1";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                        //listArr.add(0,txtSend.getText().toString());
                        break;
                    case 1:
                        room = "2";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                        //listArr.add(0,txtSend.getText().toString());
                        break;
                    case 2:
                        room = "3";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                        //listArr.add(0,txtSend.getText().toString());
                        break;
                    case 3:
                        room = "4";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                        //listArr.add(0,txtSend.getText().toString());
                        break;
                }
                txtSend.setText("");
                //lstAdptr.notifyDataSetChanged();
            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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
            jParser.setParam("action", "globe_send");
            jParser.setParam("userid", myID);
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
            jParser.setParam("deviceid", "");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String status = "";

            try {
                status = json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("false"))
            {
                //Toast.makeText(getActivity().getApplicationContext(),"Сообщение добавлено!",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Ошибка при добавлении сообщения!",Toast.LENGTH_LONG).show();
            }
        }
    }



    private class globalChat1 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры

            jParser.setParam("token", token);
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID1);
            jParser.setParam("room", "1");
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber+"");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID1);
                if(lastID1.equals(lastid))
                {
                    lastID1 = json.getString("lastid");
                }
                else {
                    flag = true;
                    lastID1 = json.getString("lastid");
                    msgNum = json.getString("number");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(flag && Integer.parseInt(msgNum) != 0)
            {
                for (int i = 0; i < Integer.parseInt(msgNum); i++)
                {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messag", messag.toString());
                        if(firsTime) {
                            listArr.add(msgCount, messag.getString("from") + ":    " + messag.getString("message"));
                        }else
                        {
                            listArr.add(0, messag.getString("from") + ":    " + messag.getString("message"));
                        }
                        msgCount++;
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e)
                    {
                        Log.e("NullPointerException", e.toString());
                    }
                }

                lstAdptr.notifyDataSetChanged();

                firsTime = false;
            }

        }
    }

    private class globalChat2 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID2);
            jParser.setParam("room", "2");
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber+"");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID2);
                if(lastID2.equals(lastid))
                {
                    lastID2 = json.getString("lastid");
                }
                else {
                    flag = true;
                    lastID2 = json.getString("lastid");
                    msgNum = json.getString("number");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(flag)
            {
                for (int i = 0; i < Integer.parseInt(msgNum); i++)
                {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messag", messag.toString());
                        if(firsTime) {
                            listArr.add(msgCount, messag.getString("from") + ":    " + messag.getString("message"));
                        }else
                        {
                            listArr.add(0, messag.getString("from") + ":    " + messag.getString("message"));
                        }
                        msgCount++;
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e)
                    {
                        Log.e("NullPointerException", e.toString());
                    }
                }

                lstAdptr.notifyDataSetChanged();

                firsTime = false;
            }

        }
    }

    private class globalChat3 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID3);
            jParser.setParam("room", "3");
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber+"");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID3);
                if(lastID3.equals(lastid))
                {
                    lastID3 = json.getString("lastid");
                }
                else {
                    flag = true;
                    lastID3 = json.getString("lastid");
                    msgNum = json.getString("number");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(flag)
            {
                for (int i = 0; i < Integer.parseInt(msgNum); i++)
                {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messag", messag.toString());
                        if(firsTime) {
                            if(!messag.equals(null)) {
                                listArr.add(msgCount, messag.getString("from") + ":    " + messag.getString("message"));
                            }
                        }else
                        {
                            if(!messag.equals(null)) {
                                listArr.add(0, messag.getString("from") + ":    " + messag.getString("message"));
                            }
                        }
                        msgCount++;
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e)
                    {
                        Log.e("NullPointerException", e.toString());
                    }
                }

                lstAdptr.notifyDataSetChanged();

                firsTime = false;
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
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID4);
            jParser.setParam("room", "4");
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber+"");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID4);
                if(lastID4.equals(lastid))
                {
                    lastID4 = json.getString("lastid");
                }
                else {
                    flag = true;
                    lastID4 = json.getString("lastid");
                    msgNum = json.getString("number");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(flag)
            {
                for (int i = 0; i < Integer.parseInt(msgNum); i++)
                {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messag", messag.toString());
                        if(firsTime) {
                            listArr.add(msgCount, messag.getString("from") + ":    " + messag.getString("message"));
                        }else
                        {
                            listArr.add(0, messag.getString("from") + ":    " + messag.getString("message"));
                        }
                        msgCount++;
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e)
                    {
                        Log.e("NullPointerException", e.toString());
                    }
                }

                lstAdptr.notifyDataSetChanged();

                firsTime = false;
            }

        }
    }

    private void fillArrayList(ArrayList<String> array, JSONArray jsArray)
    {
        JSONObject messag = null;
        for (int i = 0; i < Integer.parseInt(msgNum); i++)
        {
            try {
                messag = new JSONObject(jsArray.get(i).toString());
                Log.e("messag", messag.toString());
                if(firsTime) {
                    array.add(msgCount, messag.getString("from") + ":    " + messag.getString("message"));
                }else
                {
                    array.add(0, messag.getString("from") + ":    " + messag.getString("message"));
                }
                msgCount++;
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }
}
