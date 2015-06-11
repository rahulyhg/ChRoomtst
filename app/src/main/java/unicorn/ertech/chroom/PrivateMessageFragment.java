package unicorn.ertech.chroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ILDAR on 08.06.2015.
 */
public class PrivateMessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private Context context;

    private ListView lvChat;
    private EditText txtSend;
    private ImageButton butSend, butSmile;
    private SwipeRefreshLayout swipeLayout;

    private SharedPreferences Notif;
    private SharedPreferences.Editor ed2;

    private boolean firstTime = true;
    private boolean NotOut = true;

    private int msgCount = 0, sendedPhotos=0, outPhoto=0, scrolling = 0, lastBlock = 0, pic_width=80;
    private String URL = "http://im.topufa.org/index.php";
    private String token, sendTo, userProfile, favorite, fromDIALOGS;
    private String picUrl, nickName, myID, lastID4, shake;
    private String userId, lastId, outMsg, mID;
    String[] attached_link;

    private final String SAVED_LASTID="lastid";

    private List<pmChatMessage> messages = new ArrayList<pmChatMessage>();
    private pmChatAdapter adapter;
    private Timer myTimer;
    private conversationsMsg p3;
    private SmileManager sMgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HashMap<String, String> map = new HashMap<String, String>();
        if(getArguments() != null){
            ConnectPrivate cp = getArguments().getParcelable("key");
            map = cp.get(0);
        }
        Notif = getActivity().getSharedPreferences("notifications",getActivity().MODE_PRIVATE);
        if((Notif.contains(SAVED_LASTID))){
            lastID4=Notif.getString(SAVED_LASTID, "0");
        }

        if (map != null){
            myID = map.get("myID");
            nickName = map.get("nick");
            picUrl = map.get("picUrl");
            userId = map.get("userId");
            token = map.get("token");
            shake = map.get("shake");
            lastID4 = map.get("lastID4");
            fromDIALOGS = map.get("fromDIALOGS");
            favorite = map.get("favorite");
            sendTo = map.get("sendTo");
        }
        if(favorite.equals("false")){
            if("".equals(userId)) return;

            if(ConversationsFragment.favorites.contains(Integer.parseInt(userId))){
                favorite="true";
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.private_chat_list, container, false);

        context = view.getContext();

        butSend=(ImageButton)view.findViewById(R.id.button2);
        butSmile=(ImageButton)view.findViewById(R.id.butSmile);
        lvChat=(ListView)view.findViewById(R.id.lvChat);
        txtSend=(EditText)view.findViewById(R.id.editText1);

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container_private);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.izum_blue);
        swipeLayout.setDistanceToTriggerSync(20);

        adapter = new pmChatAdapter(messages,context);
        lvChat.setAdapter(adapter);
        lvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        if(shake.equals("true")){
            String[] s = new String[5];
            pmChatMessage p = new pmChatMessage("0","Здравствуйте! Опишите Вашу проблему максимально подробно, наши агенты свяжутся с Вами в ближайшее время.", "1", convertTime(), s);
            messages.add(0, p);
            msgCount++;
            adapter.notifyDataSetChanged();
        }


        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isNetworkAvailable()) {
                    outMsg = txtSend.getText().toString();
                    new OutMsg().execute();
                    //imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                } else {
                    Toast.makeText(context, "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(fromDIALOGS.equals("true")) {
            new getEarlierMessages().execute();
        }

        attached_link=new String[5];
        for(int i=0; i<5; i++){
            attached_link[i]="";
        }

        sMgr = new SmileManager(getActivity(), context, view);
        view = sMgr.setView();


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

        return view;
    }

    @Override
    public void onDestroy() {
        myTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        new getEarlierMessages().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                //parser.parseAsync();
            }
        }, 1000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public boolean windowDismiss() {
        if(sMgr.windowIsShowing()){
            return sMgr.windowDismiss();
        }
        return false;
    }

    public void clearText(){
        txtSend.setText("");
    }

    private class OutMsg extends AsyncTask<String, String, JSONObject> {
        //String attachPhoto;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
            butSend.setEnabled(false);
            /*if(outPhoto==0){
                attachPhoto=attached_ID;
            }else if(outPhoto==1){
                attachPhoto=attached_ID2;
            }else if(outPhoto==2){
                attachPhoto=attached_ID3;
            }else if(outPhoto==3){
                attachPhoto=attached_ID4;
            }else if(outPhoto==4){
                attachPhoto=attached_ID5;
            }*/
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            if (!shake.equals("true")){
                jParser.setParam("action", "dialogs_send");
                if (fromDIALOGS.equals("false")) {
                    jParser.setParam("sendto", sendTo);
                } else {
                    jParser.setParam("dialogid", userId);
                }
            }
            else
            {
                jParser.setParam("action", "get_support");
                jParser.setParam("sendto", sendTo);
                jParser.setParam("type", "2");
            }

            String s="";
//            if(!attachedPhotos[0].equals("")){
//                s=attachedPhotos[0];
//            }
//            for(int i=1; i<5; i++){
//                if(!attachedPhotos[i].equals("")){
//                    s=s+","+attachedPhotos[i];
//                }
//            }
            if(!s.equals("")){
                jParser.setParam("attache", s);
                if(outMsg.equals("")){
                    outMsg="Изображение";
                }
            }
            jParser.setParam("message", outMsg);
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmOutSend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("pmOut", json.toString());
            butSend.setEnabled(true);
//            for(int i=0; i<5; i++) {
//                attachedPhotos[i] = "";
//            }
            //outPhoto++;
            if (json != null) {
                String status = "";
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if ("false".equals(status)) {
//                    attached_ID="";
//                    rlAttach.setVisibility(View.GONE);
                    //attachedPhoto.setVisibility(View.GONE);
                    //tvCancelAttach.setVisibility(View.GONE);
                    try {
                        userId = json.getString("dialogid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0", convertTime(), attached_link);
                    messages.add(msgCount, p);
                    msgCount++;

                    Log.e("privatesend","666");
                    Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                    conversationsMsg p2 = new conversationsMsg(userId, nickName, outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                    p3 = p2;
                    if(favorite.equals("true"))
                    {
                        FavoritesFragment.newMsg(p2);
                    }
                    else {
                        ConversationsFragment.newMsg(p2);
                    }


                    Log.e("privatesend","777");

                    Log.e("privatesend","888");
                    lvChat.setSelection(adapter.getCount());
                    Log.e("privatesend","999");
                    txtSend.setText("");

                    if(shake.equals("true")){
                        String[] sArr = new String[5];
                        for(int t=0; t<5; t++){
                            sArr[t]="";
                        }
                        pmChatMessage p3 = new pmChatMessage("0", "Спасибо, Ваша заявка принята на рассмотрение, Вам ответят в ближайшее время. Ответ Вы сможете увидеть в личных сообщениях", "1", convertTime(), sArr);
                        messages.add(msgCount, p3);
                        msgCount++;
                    }
//                    adapter.notifyDataSetChanged();
//                    for(int t=0; t<5; t++){
//                        attached_link[t]="";
//                    }
//                    hidePhotos();
                } else {
                    try {
                        status = json.getString("error_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("65"))
                    {
                        Toast.makeText(context, "Нельзя отправлять больше 5 сообщений в день службе поддержки!", Toast.LENGTH_LONG).show();
                    }
                    else if(status.equals("63")){
                        Toast.makeText(context, "Вы в чёрном списке этого пользователя", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                Toast.makeText(context, "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
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
            jParser.setParam("action", "dialogs_get");
            jParser.setParam("dialogid", userId);
            jParser.setParam("lastid", "");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmLastSend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            //Log.i("pmLast",json.toString());
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
                if("false".equals(s)){
                    try {
                        realNum = json.getString("total");
                        lastId = json.getString("lastid");
                        if((!lastId.equals(null))&&(!lastId.equals(""))){
                            int t;
                            if(!lastID4.equals("")){
                                t=Integer.parseInt(lastID4);
                            }else{
                                t=0;
                            }
                            if(Integer.parseInt(lastId)>t) {
                                lastID4 = lastId;
                                ed2 = Notif.edit();
                                ed2.putString(SAVED_LASTID,lastID4);
                                if(Integer.parseInt(lastId)>Integer.parseInt(ConversationsFragment.lastID4)) {
                                    ConversationsFragment.lastID4 = lastId;}
                                ed2.commit();
                            }
                        }
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
                                Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                                conversationsMsg p2 = new conversationsMsg(userId, nickName, messag.getString("message"), picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                                if (!messag.getString("message").equals("1/!")) {
                                    if (NotOut) {
                                        if (favorite.equals("false")) {
                                            ConversationsFragment.newMsg(p2);
                                        } else {
                                            FavoritesFragment.newMsg(p2);
                                        }
                                    }
                                }

                                String[] sArr = new String[5];
                                for(int t=0; t<5; t++){
                                    sArr[t]="";
                                }
                                int attache_total=messag.getInt("attache_total");
                                if(attache_total>0){
                                    JSONArray attaches= messag.getJSONArray("attache");
                                    for(int t=0; t<attache_total; t++){
                                        sArr[t]=attaches.getString(t);
                                    }
                                }

                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", messag.getString("time"), sArr);
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
                    if(!realNum.equals("0")) {
                        lvChat.setSelection(adapter.getCount());
                    }
                }
            }

        }
    }//конец asyncTask


    private class getEarlierMessages extends AsyncTask<String, String, JSONObject> {/////////нужен свой ID
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Загрузка сообщений ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_get");
            jParser.setParam("page", String.valueOf(lastBlock));
            Log.e("lastB", String.valueOf(lastBlock));
            jParser.setParam("dialogid", userId);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.i("pmEarlySend",jParser.nameValuePairs.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.i("pmEarly",json.toString());
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
                if("false".equals(s)){
                    try {
                        realNum = json.getString("total");
                        Log.e("num", realNum);
                        lastId = json.getString("lastid");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(!realNum.equals("0")){
                            lastBlock++;
                            scrolling = Integer.parseInt(realNum);
                            s = json.getString("data");
                            real = new JSONArray(s);
                        }
                        else {
                            scrolling = 0;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < Integer.parseInt(realNum); i++) {
                        try {
                            messag = new JSONObject(real.get(i).toString());
                            msgID = messag.getString("userid");
                            if (!messag.getString("message").equals("1/!")){
                                if (msgID.equals(myID)) {
                                    String[] sArr = new String[5];
                                    for(int t=0; t<5; t++){
                                        sArr[t]="";
                                    }
                                    //int attache_total=messag.getInt("attache_total");
                                    //if(attache_total>0){
                                    try {
                                        String attacheStatus=messag.getString("attache");
                                        if((!attacheStatus.equals("false"))&&(!"".equals(attacheStatus))){
                                            JSONArray attaches= messag.getJSONArray("attache");
                                            if(!attaches.getString(0).equals("false")){
                                                for(int t=0; t<attaches.length(); t++){
                                                    sArr[t]=attaches.getString(t);
                                                }
                                            }
                                        }
                                    }catch (JSONException e){
                                        if(e.getCause().toString().equals("org.json.JSONException: Value false at attache of type java.lang.String cannot be converted to JSONArray")){
                                            JSONArray attaches= messag.getJSONArray("attache");
                                            if(!attaches.getString(0).equals("false")){
                                                for(int t=0; t<attaches.length(); t++){
                                                    sArr[t]=attaches.getString(t);
                                                }
                                            }
                                        }
                                    }

                                    //}
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "0",
                                            messag.getString("time"), sArr);
                                    messages.add(0, p);
                                    msgCount++;
                                } else {
                                    String[] sArr = new String[5];
                                    for(int t=0; t<5; t++){
                                        sArr[t]="";
                                    }
                                    //int attache_total=messag.getInt("attache_total");
                                    ///if(attache_total>0){
                                    try {
                                        String attacheStatus=messag.getString("attache");
                                        if((!attacheStatus.equals("false"))&&(!"".equals(attacheStatus))){
                                            JSONArray attaches= messag.getJSONArray("attache");
                                            if(!attaches.getString(0).equals("false")){
                                                for(int t=0; t<attaches.length(); t++){
                                                    sArr[t]=attaches.getString(t);
                                                }
                                            }
                                        }
                                    }catch (JSONException e){
                                        if(e.getCause().toString().equals("org.json.JSONException: Value false at attache of type java.lang.String cannot be converted to JSONArray")){
                                            JSONArray attaches= messag.getJSONArray("attache");
                                            if(!attaches.getString(0).equals("false")){
                                                for(int t=0; t<attaches.length(); t++){
                                                    sArr[t]=attaches.getString(t);
                                                }
                                            }
                                        }
                                    }
                                    //}
                                    userProfile = messag.getString("userid");
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", messag.getString("time"), sArr);
                                    messages.add(0, p);
                                    msgCount++;
                                }
                            }

                            Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                            conversationsMsg p2 = new conversationsMsg(userId, nickName, outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
                            p3 = p2;


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
                        if(Integer.parseInt(realNum)==0)
                        {
                            Toast.makeText(context, "История переписки пуста!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        lvChat.setSelection(scrolling);
                        if(Integer.parseInt(realNum)==0)
                        {
                            Toast.makeText(context, "Больше сообщений нет!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                }
            }

        }
    }

    public String convertTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    public void clearHistory(){
        msgCount=0;
        messages.clear();
        adapter.notifyDataSetChanged();
    }
}
