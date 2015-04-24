package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timur on 08.01.2015.
 */
public class AdsFragment extends Fragment {
        private Context context;
        public int pageNumber;
        ImageButton butSmile;
        EditText txtSend;
        int msgCount;
        ListView lvChat;
        JSONObject json;
        Timer myTimer;
        boolean firsTime;
        String URL = "http://im.topufa.org/index.php";
        String lastID1, lastID2,lastID3,lastID4, msgNum, room, outMsg, token, deleted_total;
        List<chatMessage> messages = new ArrayList<chatMessage>();
        private ArrayList<HashMap<String, Object>> citiList;
        final String SAVED_CITY = "city";
        chatAdapter adapter;
        boolean stopTImer = false ;

        /** Handle the results from the voice recognition activity. */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        pageNumber=0;
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        //token = Main.str;
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("citySrv")){
            room=Integer.toString(sPref.getInt("citySrv",3345));
        }else{
            room="3345";
        }
        //room="3345";
        // messagesNews.add(0,"News");
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    if(!stopTImer) {
                        new globalChat1().execute();
                    }
                }
                else
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 4L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_blank, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        final ImageButton butSend = (ImageButton) view.findViewById(R.id.button2);
        butSmile = (ImageButton) view.findViewById(R.id.butSmile);
        lvChat = (ListView)view.findViewById(R.id.lvChat);
        txtSend = (EditText) view.findViewById(R.id.editText1);
        final TableLayout smileTable = (TableLayout)view.findViewById(R.id.smileTable1);
        firsTime = true;

        final smileManager sMgr = new smileManager(getActivity());
        sMgr.initSmiles(smileTable, txtSend);
        //token = Main.str;
        //room = "11";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        deleted_total = "0";
        //myID = "1";
        //listArr = new ArrayList<String>();
        citiList = new ArrayList<HashMap<String, Object>>();

        ///lstAdptr = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listArr);
        adapter = new chatAdapter(messages,getActivity().getApplicationContext());

        HashMap<String, Object> hm;

        lvChat.setAdapter(adapter);
        //tvPage.setText("Page " + pageNumber);
        //tvPage.setBackgroundColor(backColor);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {

                String UserId = adapter.getItem(position).uid;
                String nick = adapter.getItem(position).from;
                Intent i = new Intent(getActivity(),Profile2.class);
                i.putExtra("userId",UserId);
                i.putExtra("token",token);
                i.putExtra("nick",nick);
                i.putExtra("avatar",adapter.getItem(position).picURL);
                i.putExtra("userPROFILE", UserId);
                //messages.remove(position);
                //adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        //room = "11";
                        outMsg = txtSend.getText().toString();
                        new OutMsg().execute();
                        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);

                txtSend.setText("");
                //lstAdptr.notifyDataSetChanged();
            }
        });

        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(smileTable.getVisibility()==View.GONE){
                    sMgr.setVisibleSmile(true);
                }else{
                    sMgr.setVisibleSmile(false);
                }
                butSend.refreshDrawableState();
                butSmile.refreshDrawableState();
                txtSend.refreshDrawableState();
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
            //jParser.setParam("userid", myID);
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("receivedjson", "2222");
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
                Toast.makeText(getActivity().getApplicationContext(),"Сообщение успешно добавлено!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
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
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("sendjson", "2222");
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {

            JSONArray deleted = null;
            boolean flag = false;
            String lastid = null;
            JSONArray arr = null;
            String s = null;
            JSONObject messag = null;
            Log.e("room", room);
            try {
                msgNum = json.getString("total");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.e("pageNumber", pageNumber + "");
                lastid = json.getString("lastid");
                Log.e("lastid", lastid);
                Log.e("lastID", lastID1);
                if (lastID1.equals(lastid)) {
                    lastID1 = json.getString("lastid");
                } else {
                    flag = true;
                    lastID1 = json.getString("lastid");
                    msgNum = json.getString("total");
                    Log.e("msgNum", msgNum);
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    deleted_total = json.getString("total-deleted");
                    s = arr.get(0).toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (flag && (!msgNum.equals("0")) || (!deleted_total.equals("0"))) {
                Log.e("sendjson", "3333loop");
                if(!deleted_total.equals("0"))
                {
                    try {
                        s = json.getString("deleted");
                        deleted = new JSONArray(s);
                        if(!deleted_total.equals("")) {
                            for (int i = 0; i < Integer.parseInt(deleted_total); i++) {
                                checkInList(deleted.get(i).toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < Integer.parseInt(msgNum); i++) {
                    try {
                        messag = new JSONObject(arr.get(i).toString());
                        Log.e("messagads", messag.toString());
                        if (firsTime) {
                            messages.add(msgCount, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));

                        } else {
                            messages.add(0, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));
                        }
                        msgCount++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("NullPointerException", e.toString());
                    }
                }
                Log.e("sendjson", "loop4");


                firsTime = false;

                adapter.notifyDataSetChanged();
            }

        }

        }
    }

    @Override
    public void onDestroy(){
        myTimer.cancel();
        Log.e("json", "destroy");
        super.onDestroy();
    }

    public void onResume(){
        super.onResume();
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("citySrv")){
            String tmp = Integer.toString(sPref.getInt("citySrv",3345));
            if(!room.equals(tmp)){
                room=tmp;
                firsTime=true;
                msgCount=0;
                messages.clear();
                lastID1="0";
                adapter.notifyDataSetChanged();
            }
        }
        stopTImer=false;
    }

    public void checkInList(String ID) {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).messageID.equals(ID))
            {
                messages.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onPause(){
        stopTImer=true;
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
        super.onPause();
    }
}
