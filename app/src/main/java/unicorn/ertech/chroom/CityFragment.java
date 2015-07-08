package unicorn.ertech.chroom;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 08.01.2015.
 */
public class CityFragment extends android.support.v4.app.Fragment implements InterfaceSet{
    private Context context;
    EditText txtSend;
    public int pageNumber;
    int state;
    int msgCount;
    ListView lvChat;
    JSONObject json;
    Timer myTimer;
    boolean firsTime;
    boolean stopTImer = true ;
    List<chatMessage> messages = new ArrayList<chatMessage>();
    String URL = "http://im.topufa.org/index.php";
    String lastID1, lastID2,lastID3,lastID4, msgNum="0", room, outMsg, token, myID,deleted_total="0" ;


    SmileManager sMgr;
    private ArrayList<HashMap<String, Object>> regionList;
    private static final String TITLE = "message_author"; // Верхний текст
    private static final String DESCRIPTION = "message_body"; // ниже главного
    private static final String ICON = "avatar";  // будущая картинка
    chatAdapter adapter;
    globalChat2 chatTask;

    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        pageNumber=1;
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        // messagesNews.add(0,"News");
        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("regSrv")){
            room=Integer.toString(sPref.getInt("regSrv",3296));
        }else{
            room="3296";
        }
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    String str = "Oblast = " + stopTImer;
                    str += " Position " + state;
                    if (!stopTImer) Log.e("StopTimer",str);
//                    //room = "3296";
                    if(!stopTImer) {
                        chatTask = new globalChat2();
                        chatTask.execute();
                    }
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        },  0, 2L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        final ImageButton butSend = (ImageButton) view.findViewById(R.id.button2);
        lvChat = (ListView)view.findViewById(R.id.lvChat);
        txtSend = (EditText) view.findViewById(R.id.editText1);
        firsTime = true;
        //token = Main.str;
        //room = "10";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        myID = "18";deleted_total = "";

        regionList = new ArrayList<HashMap<String, Object>>();

        adapter = new chatAdapter(messages,getActivity().getApplicationContext());
        HashMap<String, Object> hm;
        lvChat.setAdapter(adapter);
        sMgr = new SmileManager(getActivity(), context, view);
        view = sMgr.setView();

        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_animation);
        //rellay=(RelativeLayout)view.findViewById(R.id.relLay1);
        //Animation anim2 = AnimationUtils.loadAnimation(getActivity(), )
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
                //messages.remove(position);
                //adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });
        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int izumCount = DataClass.getIzumCount();
                Log.e("Izum","IzumCount = " + izumCount);

                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                outMsg = txtSend.getText().toString();
                if (izumCount > 0){
                    new OutMsg().execute();
                    DataClass.setIzumCount(izumCount - 1);
                } else {
                    showDialog();
                }
                imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                txtSend.setText("");
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

    @Override
    public void Start(int state) {
        stopTImer = false;
        this.state = state;
    }

    @Override
    public void Stop() {
        stopTImer = true;
        if (chatTask != null && !chatTask.isCancelled()){
            chatTask.cancel(stopTImer);
        }
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
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
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
                Toast.makeText(getActivity().getApplicationContext(),"Сообщение успешно добавлено!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class globalChat2 extends AsyncTask<String, chatMessage, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "globe_get");
            jParser.setParam("lastid", lastID2);
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);

            if(json!=null) {

                JSONArray deleted = null;
                boolean flag = false;
                String lastid = null;
                JSONArray arr = null;
                String s = null;
                JSONObject messag = null;
                Log.e("Region_room", room);
                try {
                    msgNum = json.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.e("Region_pageNumber", pageNumber + "");
                    lastid = json.getString("lastid");
                    Log.e("Region_lastid", lastid);
                    Log.e("Region_lastID", lastID2);
                    if (lastID2.equals(lastid)) {
                        lastID2 = json.getString("lastid");
                    } else {
                        flag = true;
                        lastID2 = json.getString("lastid");
                        msgNum = json.getString("total");
                        Log.e("Region_msgNum", msgNum);
                        s = json.getString("data");
                        arr = new JSONArray(s);
                        deleted_total = json.getString("total-deleted");
                        s = arr.get(0).toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (flag && (!msgNum.equals("0")) || (!deleted_total.equals("0"))) {
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
                            Log.e("Region_messagcity", messag.toString());

                            publishProgress(new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                        }
                    }

                    firsTime = false;
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(chatMessage... values) {
            super.onProgressUpdate(values);
            if (firsTime) {
                messages.add(msgCount, values[0]);

            } else {
                messages.add(0, values[0]);
            }
            msgCount++;
        }

        @Override
        protected void onPostExecute(Void result) {

            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        myTimer.cancel();
    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences sPref = getActivity().getSharedPreferences("saved_chats", Context.MODE_PRIVATE);
        if(sPref.contains("regSrv")){
            String tmp = Integer.toString(sPref.getInt("regSrv",3296));
            if(!room.equals(tmp)){
                room=tmp;
                firsTime=true;
                msgCount=0;
                messages.clear();
                lastID2="0";
                adapter.notifyDataSetChanged();
            }
        }
        stopTImer=false;
    }

    @Override
    public void onPause(){
        super.onPause();
        stopTImer=true;
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
    public boolean windowDismiss() {
        if(sMgr == null) return false;
        if(sMgr.windowIsShowing()){
            return sMgr.windowDismiss();
        }
        return false;
    }



    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Пополнить счёт");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(getActivity(), Billing.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
