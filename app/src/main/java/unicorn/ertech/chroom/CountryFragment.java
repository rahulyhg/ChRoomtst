package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timur on 08.01.2015.
 */
public class CountryFragment extends Fragment {
    private Context context;
    ImageButton butSmile;
    EditText txtSend;
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
    String lastID1, lastID2,lastID3,lastID4, msgNum, room="3159", outMsg, token, myID,deleted_total="0" ;
    List<chatMessage> messages = new ArrayList<chatMessage>();
    private ArrayList<HashMap<String, Object>> citiList;
    private static final String TITLE = "message_author"; // Верхний текст
    private static final String DESCRIPTION = "message_body"; // ниже главного
    private static final String ICON = "avatar";  // будущая картинка
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
        pageNumber=3;
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
                if (isNetworkAvailable()) {
                    room = "3159";
                    if(!stopTImer) {
                        new globalChat4().execute();
                    }
                } else {
                    //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                }
            }
        }, 1L * 250, 4L * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_blank3, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        final ImageButton butSend = (ImageButton) view.findViewById(R.id.button23);
        butSmile=(ImageButton)view.findViewById(R.id.butSmile3);
        lvChat = (ListView)view.findViewById(R.id.lvChat3);
        txtSend = (EditText) view.findViewById(R.id.editText3);
        final TableLayout smileTable = (TableLayout)view.findViewById(R.id.smileTable3);
        firsTime = true;
        //token = Main.str;
        room = "3159";
        msgCount=0;
        lastID1 = "";
        lastID2 = "";
        lastID3 = "";
        lastID4 = "";
        myID = "1";
        deleted_total = "";
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
                room = "3159";
                outMsg = txtSend.getText().toString();
                new OutMsg().execute();
                imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);

                txtSend.setText("");
            }
        });

        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(smileTable.getVisibility()==View.GONE){
                    smileTable.setVisibility(View.VISIBLE);
                }else{
                    smileTable.setVisibility(View.GONE);
                }
                butSend.refreshDrawableState();
                butSmile.refreshDrawableState();
                txtSend.refreshDrawableState();
            }
        });
        findSmiles(view);
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
            jParser.setParam("room", room);
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                //String deleted_total = "0";
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
                    Log.e("lastID", lastID4);
                    if (lastID4.equals(lastid)) {
                        lastID4 = json.getString("lastid");
                    } else {
                        flag = true;
                        lastID4 = json.getString("lastid");
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
                if (flag && (!(msgNum.equals("0")) ||(!deleted_total.equals("0")))) {
                    if(!deleted_total.equals("0"))
                    {
                        try {
                            s = json.getString("deleted");
                            deleted = new JSONArray(s);

                            for(int i=0; i<Integer.parseInt(deleted_total);i++)
                            {
                                checkInList(deleted.get(i).toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < Integer.parseInt(msgNum); i++) {
                        try {
                            messag = new JSONObject(arr.get(i).toString());
                            Log.e("messagcountry", messag.toString());
                            if (firsTime) {
                                messages.add(msgCount, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"("+messag.getString("age")+")", messag.getString("message"), messag.getString("avatar"),messag.getString("id")));
                            } else {
                                messages.add(0, new chatMessage(messag.getString("uid"), messag.getString("nickname")+" "+"("+messag.getString("age")+")", messag.getString("message"), messag.getString("avatar"),messag.getString("id")));
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
        myTimer.cancel();
        Log.e("json", "destroy");
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        stopTImer=false;
    }

    @Override
    public void onPause(){
        stopTImer=true;
        super.onPause();
    }

    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private void findSmiles(View view){
        s01 = (ImageView) view.findViewById(R.id.s031);
        s01.setOnClickListener(smile_click_listener);
        s02 = (ImageView) view.findViewById(R.id.s032);
        s02.setOnClickListener(smile_click_listener);
        s03 = (ImageView) view.findViewById(R.id.s033);
        s03.setOnClickListener(smile_click_listener);
        s04 = (ImageView) view.findViewById(R.id.s034);
        s04.setOnClickListener(smile_click_listener);
        s05 = (ImageView) view.findViewById(R.id.s035);
        s05.setOnClickListener(smile_click_listener);
        s06 = (ImageView) view.findViewById(R.id.s036);
        s06.setOnClickListener(smile_click_listener);
        s07 = (ImageView) view.findViewById(R.id.s037);
        s07.setOnClickListener(smile_click_listener);
        s08 = (ImageView) view.findViewById(R.id.s038);
        s08.setOnClickListener(smile_click_listener);
        s09 = (ImageView) view.findViewById(R.id.s039);
        s09.setOnClickListener(smile_click_listener);
        s10 = (ImageView) view.findViewById(R.id.s030);
        s10.setOnClickListener(smile_click_listener);
    }
    private View.OnClickListener smile_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.s031:
                    txtSend.append(":)");
                    break;
                case R.id.s032:
                    txtSend.append(":D");
                    break;
                case R.id.s033:
                    txtSend.append(":O");
                    break;
                case R.id.s034:
                    txtSend.append(":(");
                    break;
                case R.id.s035:
                    txtSend.append("*05*");
                    break;
                case R.id.s036:
                    txtSend.append("Z)");
                    break;
                case R.id.s037:
                    txtSend.append("*07*");
                    break;
                case R.id.s038:
                    txtSend.append("*08*");
                    break;
                case R.id.s039:
                    txtSend.append("*09*");
                    break;
                case R.id.s030:
                    txtSend.append("*love*");
                    break;
                default:
                    break;
            }
            txtSend.setText(getSmiledText(getActivity(),txtSend.getText()));
        }
    };

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
