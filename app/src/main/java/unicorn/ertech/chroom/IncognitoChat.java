package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 11.01.2015.
 */
public class IncognitoChat extends Fragment implements InterfaceSet{
    private Context context;
    public int pageNumber;
    int state;
    int msgCount;
    ListView lvChat;
    ArrayList<String> messagesUfa,messagesRB, messagesRUS, messagesNews,listArr;
    ArrayAdapter<String> adapterUfa, adapterRB, adapterRUS, adapterNews, lstAdptr;
    JSONObject json;
    Timer myTimer;
    boolean firsTime;
    String URL = "http://im.topufa.org/index.php";
    String lastID1, lastID2,lastID3,lastID4, msgNum, room, outMsg, token, myID;
    List<anonChat> messages = new ArrayList<anonChat>();
    private ArrayList<HashMap<String, Object>> citiList;
    private static final String TITLE = "message_author"; // Верхний текст
    private static final String DESCRIPTION = "message_body"; // ниже главного
    private static final String ICON = "avatar";  // будущая картинка
    anonChatAdapter adapter;
    EditText txtSend;
    boolean stopTImer = true ;
    globalChat chatTask;
    SmileManager sMgr;
    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        // messagesNews.add(0,"News");
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    String str = "Incognito = " + stopTImer;
                    str += " Position " + state;
                    if (!stopTImer) Log.e("StopTimer",str);
                    if(!stopTImer) {
                        chatTask = new globalChat();
                        chatTask.execute();
                    }
                }
                else
                {
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

        sMgr = new SmileManager(getActivity(), context, view);
        view = sMgr.setView();

        firsTime = true;
        //token = Main.str;
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        //myID = "1";
        //listArr = new ArrayList<String>();

        ///lstAdptr = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listArr);
        adapter = new anonChatAdapter(messages,getActivity().getApplicationContext());
        lvChat.setAdapter(adapter);
        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                outMsg = txtSend.getText().toString();
                new OutMsg().execute();
                imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);

                txtSend.setText("");
                //lstAdptr.notifyDataSetChanged();
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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

    @Override
    public boolean windowDismiss() {
        return false;
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
            jParser.setParam("action", "anonimus_send");
            //jParser.setParam("userid", myID);
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
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
                Toast.makeText(getActivity().getApplicationContext(),"Сообщение добавлено!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class globalChat extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры

            jParser.setParam("token", token);
            jParser.setParam("action", "anonimus_get");
            jParser.setParam("lastid", lastID1);
            //jParser.setParam("device_id", "dsfadfg");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    //Log.e("pageNumber", pageNumber + "");
                    lastid = json.getString("lastid");
                    //Log.e("lastid", lastid);
                    //Log.e("lastID", lastID1);
                    if (lastID1.equals(lastid)) {
                        lastID1 = json.getString("lastid");
                    } else {
                        flag = true;
                        lastID1 = json.getString("lastid");
                        msgNum = json.getString("total");
                        s = json.getString("data");
                        arr = new JSONArray(s);
                        s = arr.get(0).toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (flag && Integer.parseInt(msgNum) != 0) {
                    for (int i = 0; i < Integer.parseInt(msgNum); i++) {
                        try {
                            messag = new JSONObject(arr.get(i).toString());
                            if (firsTime) {
                                messages.add(msgCount, new anonChat(messag.getString("id"),messag.getString("nick"), messag.getString("sex"), messag.getString("message")));
                            } else {
                                messages.add(0, new anonChat(messag.getString("id"),messag.getString("nick"), messag.getString("sex"), messag.getString("message")));
                            }
                            msgCount++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    adapter.notifyDataSetChanged();

                    firsTime = false;
                }
            }
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
        stopTImer=false;
    }

    @Override
    public void onPause(){
        super.onPause();
        stopTImer=true;
    }
}
