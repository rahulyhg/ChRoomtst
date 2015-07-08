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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
 * Created by Timur on 08.01.2015.
 */
public class AdsFragment extends Fragment implements InterfaceSet{
        private Context context;
        public int pageNumber;
        EditText txtSend;
        int msgCount;
        ListView lvChat;
        JSONObject json;
        Timer myTimer;
        boolean firsTime;
        String URL = "http://im.topufa.org/index.php";
        String lastID1, lastID2,lastID3,lastID4, msgNum, room, outMsg, token, deleted_total;
        List<chatMessage> messages = new ArrayList<chatMessage>();
        final String SAVED_CITY = "city";
        chatAdapter adapter;
        boolean stopTImer = true;
        int state;
        globalChat1 chatTask;
        SmileManager sMgr;


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
                    String str = "City = " + stopTImer;
                    str += " Position " + state;
                    if (!stopTImer) Log.e("StopTimer",str);
                    if(!stopTImer) {
                        chatTask = new globalChat1();
                        chatTask.execute();
                    }
                }
                else
                {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 0, 2L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        final ImageButton butSend = (ImageButton) view.findViewById(R.id.button2);
//        butSmile = (ImageButton) view.findViewById(R.id.butSmile);
        lvChat = (ListView)view.findViewById(R.id.lvChat);
        txtSend=(EditText)view.findViewById(R.id.editText1);
        firsTime = true;

        sMgr = new SmileManager(getActivity(), context, view);
        view = sMgr.setView();
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

        ///lstAdptr = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listArr);
        adapter = new chatAdapter(messages,getActivity().getApplicationContext());

        HashMap<String, Object> hm;

        lvChat.setAdapter(adapter);
        //tvPage.setText("Page " + pageNumber);
        //tvPage.setBackgroundColor(backColor);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                sMgr.windowDismiss();

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
                int izumCount = DataClass.getIzumCount();
                Log.e("Izum","IzumCount = " + izumCount);

                Context context = getActivity().getApplicationContext();
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        //room = "11";
                outMsg = txtSend.getText().toString();
                sMgr.windowDismiss();
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

    @Override
    public boolean windowDismiss() {

        if(sMgr == null) return false;

        if(sMgr.windowIsShowing()){
            return sMgr.windowDismiss();
        }
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
            jParser.setParam("action", "globe_send");
            //jParser.setParam("userid", myID);
            jParser.setParam("room", room);
            jParser.setParam("message", outMsg);
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            Log.e("City_sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("City_receivedjson", "2222");
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

            if("false".equals(status))
            {
                Toast.makeText(context,"Сообщение успешно добавлено!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    status=json.getString("error_code");
                    if(status.equals("60")){
                        Toast.makeText(getActivity().getApplicationContext(), "Нельзя отправить пустое сообщение!", Toast.LENGTH_LONG).show();
                    }else if(status.equals("12")){
                        Toast.makeText(getActivity().getApplicationContext(), "Вы забанены за нарушение правил чата!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private class globalChat1 extends AsyncTask<String, chatMessage, Void> {
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
            jParser.setParam("lastid", lastID1);
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            Log.e("City_sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("City_sendjson", "2222");

            if(json!=null) {

                JSONArray deleted = null;
                boolean flag = false;
                String lastid = null;
                JSONArray arr = null;
                String s = null;
                JSONObject messag = null;
                Log.e("City_room", room);
                try {
                    msgNum = json.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.e("City_pageNumber", pageNumber + "");
                    lastid = json.getString("lastid");
                    Log.e("City_lastid", lastid);
                    Log.e("City_lastID", lastID1);
                    if (lastID1.equals(lastid)) {
                        lastID1 = json.getString("lastid");
                    } else {
                        flag = true;
                        lastID1 = json.getString("lastid");
                        msgNum = json.getString("total");
                        Log.e("City_msgNum", msgNum);
                        s = json.getString("data");
                        arr = new JSONArray(s);
                        deleted_total = json.getString("total-deleted");
                        s = arr.get(0).toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (flag && (!msgNum.equals("0")) || (!deleted_total.equals("0"))) {
                    Log.e("City_sendjson", "3333loop");
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
                            Log.e("City_messagads", messag.toString());
                            publishProgress(new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"|"+messag.getString("age"), messag.getString("message"), messag.getString("avatar"),messag.getString("id")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                        }
                    }
                    Log.e("City_sendjson", "loop4");


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
        Log.e("City_json", "destroy");
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
        super.onPause();
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
