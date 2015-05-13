package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import com.giljulio.imagepicker.ui.ImagePickerActivity;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import me.tedyin.circleprogressbarlib.CircleProgressBar;

/**
 * Created by Timur on 22.01.2015.
 */
public class PrivateMessaging extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView lvChat;
    Context context;
    //ProgressBar progressBar;
    //net.heybird.utils.CircleProgressBar progressBar;
    CircleProgressBar progressBar;
    EditText txtSend;
    ImageButton butSend;
    ImageButton butLists;
    ImageButton butSmile, butFile;
    String URL = "http://im.topufa.org/index.php", attached_ID="", attached_ID2="", attached_ID3="", attached_ID4="", attached_ID5="";
    String[] pickedPhotos, attachedPhotos;
    TextView nick, tvCancelAttach, tvCancelAttach2, tvCancelAttach3, tvCancelAttach4, tvCancelAttach5;
    ImageView avatar, attachedPhoto, attachedPhoto2, attachedPhoto3, attachedPhoto4, attachedPhoto5;
    int msgCount = 0, sendedPhotos=0, outPhoto=0;
    int scrolling = 0;
    int lastBlock = 0, pic_width=80;
    String picUrl, myID;
    Date dateTime;
    String shake;
    String[] attached_link;
    RelativeLayout topRow, rlAttach;
    SharedPreferences userData, savedStrings;
    final String USER = "user";
    String fromDIALOGS;
    String lastID4;
    boolean fromShake;
    SwipeRefreshLayout swipeLayout;
    String pathToUserPhoto = new String();
    static final int GALLERY_REQUEST = 1;

    SharedPreferences Notif;
    SharedPreferences.Editor ed2;
    final String SAVED_NOTIF="notif";
    final String SAVED_COLOR = "color";
    final String SAVED_LASTID="lastid";

    conversationsMsg p3;
    List<pmChatMessage> messages = new ArrayList<pmChatMessage>();
    pmChatAdapter adapter;
    Timer myTimer;
    ImageButton butBack;
    TableRow BB;

    String token, sendTo, userProfile, favorite;
    boolean firstTime = true;
    boolean NotOut = true;
    String userId, msgNum, lastId, outMsg, mID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        context=getApplicationContext();
        userData = getSharedPreferences("user", MODE_PRIVATE);

        myID = Integer.toString(userData.getInt(USER,0));

        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        if((Notif.contains(SAVED_LASTID))){
            lastID4=Notif.getString(SAVED_LASTID, "0");
        }
        butSend=(ImageButton)findViewById(R.id.buttonSend);
        butSmile=(ImageButton)findViewById(R.id.buttonSmile);
        lvChat=(ListView)findViewById(R.id.lvChat);
        txtSend=(EditText)findViewById(R.id.sendText);
        nick = (TextView)findViewById(R.id.profileBack);
        avatar = (ImageView)findViewById(R.id.ivChatAvatar);
        butLists=(ImageButton)findViewById(R.id.ibStar);
        butFile=(ImageButton)findViewById(R.id.butFile);
        attachedPhoto=(ImageView)findViewById(R.id.ivAttachedPhoto);
        BB=(TableRow)findViewById(R.id.big_button);
        tvCancelAttach=(TextView)findViewById(R.id.tvCancelAttach);
        final TableLayout smileTable = (TableLayout)findViewById(R.id.smileTablePm);
        topRow=(RelativeLayout)findViewById(R.id.topRowChat);
        rlAttach=(RelativeLayout)findViewById(R.id.rlAttach);
        progressBar=(CircleProgressBar)findViewById(R.id.pbPhoto);

        dateTime = new Date();

        lvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        butLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }});
        butBack=(ImageButton)findViewById(R.id.butNewsBack);
        findSmiles();

        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width=(int)(80*metricsB.density);

        attachedPhotos = new String[5];
        pickedPhotos= new String[5];
        attached_link=new String[5];
        for(int i=0; i<5; i++){
            attachedPhotos[i]="";
            pickedPhotos[i]="";
            attached_link[i]="";
        }
        BB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Profile2.class);
                i.putExtra("userId",userProfile);
                i.putExtra("token",token);
                i.putExtra("nick",nick.getText().toString());
                i.putExtra("avatar",picUrl);
                i.putExtra("userPROFILE", userProfile);
                i.putExtra("fromMessaging", true);
                startActivity(i);
            }
        });

        tvCancelAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attached_ID="";
                attachedPhoto.setVisibility(View.GONE);
                tvCancelAttach.setVisibility(View.GONE);
                byte i=0;
                if(attachedPhoto2.getVisibility()==View.VISIBLE){
                    i++;
                }
                if(attachedPhoto3.getVisibility()==View.VISIBLE){
                    i++;
                }
                if(attachedPhoto4.getVisibility()==View.VISIBLE){
                    i++;
                }
                if(attachedPhoto5.getVisibility()==View.VISIBLE){
                    i++;
                }
                if(i==0){
                    rlAttach.setVisibility(View.GONE);
                }
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        butFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                photoPickerIntent.setType("image*//*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);*/
                outPhoto=0;
                findphotos();
                MediaChooser.setSelectionLimit(5);
                MediaChooser.showOnlyImageTab();
                MediaChooser.showCameraVideoView(false);
                Intent intent = new Intent(context, BucketHomeFragmentActivity.class);
                startActivity(intent);
                /*Intent intent = new Intent(context, ImagePickerActivity.class);
                startActivityForResult(intent, 250);*/
            }
        });

        adapter = new pmChatAdapter(messages,getApplicationContext());
        lvChat.setAdapter(adapter);
        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                /*String[] photo = adapter.getItem(position).attach;
                if(!photo.equals("false")) {
                    Intent i = new Intent(context, PhotoViewerPm.class);
                    i.putExtra("photos", photo);
                    startActivity(i);
                }*/
            }
        });
        lastId = "0";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        favorite = i.getStringExtra("favorite");
        if(favorite.equals("false")){
            if(ConversationsFragment.favorites.contains(Integer.parseInt(userId))){
                favorite="true";
            }
        }
        //mID = i.getStringExtra("mID");
        userProfile = i.getStringExtra("userPROFILE");
        sendTo = i.getStringExtra("userPROFILE");
        nick.setText(i.getStringExtra("nick"));
        picUrl = i.getStringExtra("avatar");
        fromShake=i.getBooleanExtra("fromShake", false);
        fromDIALOGS = i.getStringExtra("fromDialogs");
        if(fromDIALOGS.equals("false"))
        {
            //sendTo = i.getStringExtra("userId");
        }

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container_private);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.green, R.color.blue, R.color.orange, R.color.purple);
        swipeLayout.setDistanceToTriggerSync(20);

        shake = i.getStringExtra("shake");
        if(shake.equals("true")){
            butLists.setVisibility(View.INVISIBLE);
            String[] s = new String[5];
            pmChatMessage p = new pmChatMessage("0","Здравствуйте! Опишите Вашу проблему максимально подробно, наши агенты свяжутся с Вами в ближайшее время.", "1", s);
            messages.add(0, p);
            msgCount++;
            adapter.notifyDataSetChanged();
        }
        if(fromShake){
            ConversationsFragment.update();
        }
        Picasso.with(getApplicationContext()).load(picUrl).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).resize(80,0).transform(new PicassoRoundTransformation()).into(avatar);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isNetworkAvailable()) {
                    outMsg = txtSend.getText().toString();
                    new OutMsg().execute();
                    //imm.hideSoftInputFromWindow(txtSend.getWindowToken(), 0);
                } else {
                    Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
                }
            }
        });

        butSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int savedPosition = lvChat.getFirstVisiblePosition();
                lvChat.setSelectionFromTop(savedPosition, 60);
                //lvChat.setSelection
                if (smileTable.getVisibility() == View.GONE) {
                    smileTable.setVisibility(View.VISIBLE);
                } else {
                    smileTable.setVisibility(View.GONE);
                }
            }
        });

        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //scrolling = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0 && scrolling > firstVisibleItem) {
                    if (lastBlock != -1) {
                        // new getEarlierMessages().execute();
                    }
                }
                //scrolling = firstVisibleItem;
            }
        });

        if(fromDIALOGS.equals("true")) {
            new getEarlierMessages().execute();
        }

        Log.i("pm", "first");
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

        IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        registerReceiver(imageBroadcastReceiver, imageIntentFilter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader imageLoader=ImageLoader.getInstance();
        //imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        boolean pauseOnScroll = false; // or true
        boolean pauseOnFling = true; // or false
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
        lvChat.setOnScrollListener(listener);

        setColor();
        findphotos();
    }


    private void setColor(){
        SharedPreferences sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                topRow.setBackgroundResource(R.color.blue);
                butBack.setBackgroundResource(R.drawable.but_blue);
                butLists.setBackgroundResource(R.drawable.but_blue);
                BB.setBackgroundResource(R.drawable.but_blue);
                butFile.setBackgroundResource(R.drawable.but_blue);
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                butBack.setBackgroundResource(R.drawable.but_green);
                butLists.setBackgroundResource(R.drawable.but_green);
                BB.setBackgroundResource(R.drawable.but_green);
                butFile.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                butBack.setBackgroundResource(R.drawable.but_orange);
                butLists.setBackgroundResource(R.drawable.but_orange);
                BB.setBackgroundResource(R.drawable.but_orange);
                butFile.setBackgroundResource(R.drawable.but_orange);
            } else if(col == 3){
                topRow.setBackgroundResource(R.color.purple);
                butBack.setBackgroundResource(R.drawable.but_purple);
                butLists.setBackgroundResource(R.drawable.but_purple);
                BB.setBackgroundResource(R.drawable.but_purple);
                butFile.setBackgroundResource(R.drawable.but_purple);;
            }
        }else{
            topRow.setBackgroundResource(R.color.green);
        }
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
            if(!attachedPhotos[0].equals("")){
                s=attachedPhotos[0];
            }
            for(int i=1; i<5; i++){
                if(!attachedPhotos[i].equals("")){
                    s=s+","+attachedPhotos[i];
                }
            }
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
            for(int i=0; i<5; i++) {
                attachedPhotos[i] = "";
            }
            //outPhoto++;
            if (json != null) {
                String status = "";
                Log.e("privatesend","555");

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    attached_ID="";
                    rlAttach.setVisibility(View.GONE);
                    //attachedPhoto.setVisibility(View.GONE);
                    //tvCancelAttach.setVisibility(View.GONE);
                    try {
                        userId = json.getString("dialogid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "Сообщение успешно добавлено!", Toast.LENGTH_SHORT).show();
                    pmChatMessage p = new pmChatMessage(userId, outMsg, "0", attached_link);
                    messages.add(msgCount, p);
                    msgCount++;

                    Log.e("privatesend","666");
                    Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                    conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
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
                        pmChatMessage p3 = new pmChatMessage("0", "Спасибо, Ваша заявка принята на рассмотрение, Вам ответят в ближайшее время. Ответ Вы сможете увидеть в личных сообщениях", "1", sArr);
                        messages.add(msgCount, p3);
                        msgCount++;
                    }
                    adapter.notifyDataSetChanged();
                    for(int t=0; t<5; t++){
                        attached_link[t]="";
                    }
                    hidePhotos();
                } else {
                    try {
                        status = json.getString("error_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(status.equals("65"))
                    {
                        Toast.makeText(getApplicationContext(), "Нельзя отправлять больше 5 сообщений в день службе поддержки!", Toast.LENGTH_LONG).show();
                    }
                    else if(status.equals("63")){
                        Toast.makeText(getApplicationContext(), "Вы в чёрном списке этого пользователя", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Ошибка при добавлении сообщения!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }

            /*if(outPhoto<attachedPhotos.length) {
                if ((attachedPhotos[outPhoto] != null) && (!attachedPhotos[outPhoto].equals(""))) {
                    new sendUserPhoto().execute();
                }else{
                    rlAttach.setVisibility(View.GONE);
                    outPhoto=0;
                }
            }*/
        }
    }

    private class getEarlierMessages extends AsyncTask<String, String, JSONObject> {/////////нужен свой ID
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PrivateMessaging.this);
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
                if(s.equals("false")){
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
                                            if(!attacheStatus.equals("false")){
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
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "0", sArr);
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
                                        if(!attacheStatus.equals("false")){
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
                                    pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", sArr);
                                    messages.add(0, p);
                                    msgCount++;
                                }
                        }

                            Calendar c=Calendar.getInstance(); int month = c.get(c.MONTH)+1;
                            conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), outMsg, picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
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
                            Toast.makeText(getApplicationContext(), "История переписки пуста!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        lvChat.setSelection(scrolling);
                        if(Integer.parseInt(realNum)==0)
                        {
                            Toast.makeText(getApplicationContext(), "Больше сообщений нет!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
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
                if(s.equals("false")){
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
                                conversationsMsg p2 = new conversationsMsg(userId, nick.getText().toString(), messag.getString("message"), picUrl, "0","0", c.get(c.YEAR) + "-" + month + "-" + c.get(c.DAY_OF_MONTH) + "%" + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND),userProfile);
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

                                pmChatMessage p = new pmChatMessage(messag.getString("id"), messag.getString("message"), "1", sArr);
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

    private class remove extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_delete");
            jParser.setParam("list", "1");
            jParser.setParam("deleteid",userId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String status = null;
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status.equals("false"))
                {
                    favorite = "false";
                    FavoritesFragment.findNremove(userId);
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "Пользователь успешно удален из списка друзей!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при удалении из списка!", Toast.LENGTH_LONG).show();
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

    ImageView s01, s02, s03, s04, s05, s06, s07, s08, s09, s10;

    private void findSmiles(){
        s01 = (ImageView) findViewById(R.id.s0p1);
        s01.setOnClickListener(smile_click_listener);
        s02 = (ImageView) findViewById(R.id.s0p2);
        s02.setOnClickListener(smile_click_listener);
        s03 = (ImageView) findViewById(R.id.s0p3);
        s03.setOnClickListener(smile_click_listener);
        s04 = (ImageView) findViewById(R.id.s0p4);
        s04.setOnClickListener(smile_click_listener);
        s05 = (ImageView) findViewById(R.id.s0p5);
        s05.setOnClickListener(smile_click_listener);
        s06 = (ImageView) findViewById(R.id.s0p6);
        s06.setOnClickListener(smile_click_listener);
        s07 = (ImageView) findViewById(R.id.s0p7);
        s07.setOnClickListener(smile_click_listener);
        s08 = (ImageView) findViewById(R.id.s0p8);
        s08.setOnClickListener(smile_click_listener);
        s09 = (ImageView) findViewById(R.id.s0p9);
        s09.setOnClickListener(smile_click_listener);
        s10 = (ImageView) findViewById(R.id.s0p0);
        s10.setOnClickListener(smile_click_listener);
    }
    private View.OnClickListener smile_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.s0p1:
                    txtSend.append(":)");
                    break;
                case R.id.s0p2:
                    txtSend.append(":D");
                    break;
                case R.id.s0p3:
                    txtSend.append(":O");
                    break;
                case R.id.s0p4:
                    txtSend.append(":(");
                    break;
                case R.id.s0p5:
                    txtSend.append("*05*");
                    break;
                case R.id.s0p6:
                    txtSend.append("Z)");
                    break;
                case R.id.s0p7:
                    txtSend.append("*07*");
                    break;
                case R.id.s0p8:
                    txtSend.append("*08*");
                    break;
                case R.id.s0p9:
                    txtSend.append("*09*");
                    break;
                case R.id.s0p0:
                    txtSend.append("*love*");
                    break;
                default:
                    break;
            }
            txtSend.setText(getSmiledText(getApplicationContext(),txtSend.getText()));
        }
    };
    //
    //Ниже часть, связанная с отображением смайлов в edittext
    //

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
    private class setFavorite extends AsyncTask<String, String, JSONObject> {

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
            jParser.setParam("action", "list_add");
            jParser.setParam("addid", userId);
            jParser.setParam("list", "1");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    favorite = "true";
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "Собеседник успешно добавлен в друзья!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class setBlackList extends AsyncTask<String, String, JSONObject> {

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
            jParser.setParam("action", "list_add");
            jParser.setParam("addid", userProfile);
            jParser.setParam("list", "2");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {

                    Toast.makeText(getApplicationContext(), "Пользователь успешно добавлен в черный список!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class clearHistory extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(PrivateMessaging.this);
            pDialog.setMessage("Удаление переписки ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_swipe");
            jParser.setParam("dialogid", userId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    msgCount=0;
                    messages.clear();
                    adapter.notifyDataSetChanged();
                    if(!fromShake){
                        ConversationsFragment.update();
                    }
                    Toast.makeText(getApplicationContext(), "История сообщений успешно удалена!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при очистке!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Всплывающее меню
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.add_list_menu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_favorites) {

                    if (favorite.equals("true")) {
                        Toast.makeText(getApplicationContext(), "Пользователь уже находится у Вас в друзьях!", Toast.LENGTH_LONG).show();
                    } else {
                        new setFavorite().execute();
                    }
                }
                if (item.getItemId() == R.id.menu_blacklist) {
                    new setBlackList().execute();
                }
                if (item.getItemId() == R.id.menu_clear_history) {
                    if (shake.equals("false")) {
                        new clearHistory().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Нельзя удалять историю переписки со службой поддержки!", Toast.LENGTH_LONG).show();
                    }
                }
                if (item.getItemId() == R.id.menu_out_from_favorites) {
                    if (favorite.equals("true")) {
                        new remove().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Пользователь и так не находится у Вас в друзьях!", Toast.LENGTH_LONG).show();
                    }
                }


                return false;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    @Override
    public void onPause()
    {
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        ed2 = Notif.edit();
        if(Notif.contains(SAVED_NOTIF))
        {
            if(Notif.getString(SAVED_NOTIF,"").equals("true"))
            {
                ed2.putString(SAVED_LASTID,lastID4);
                ed2.commit();
                Intent srvs = new Intent(this, notif.class);
                startService(srvs);
            }
        }
        NotOut = false;
        savedStrings=getPreferences(MODE_PRIVATE);
        ed2=savedStrings.edit();
        ed2.putString(userId, txtSend.getText().toString());
        ed2.commit();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent srvs = new Intent(this, notif.class);
        stopService(srvs);
        NotOut = true;
        savedStrings=getPreferences(MODE_PRIVATE);
        if(savedStrings.contains(userId)){
            txtSend.setText(savedStrings.getString(userId,""));
        }
    }

    @Override
    public void onDestroy(){
        myTimer.cancel();
        unregisterReceiver(imageBroadcastReceiver);
        Log.e("json", "destroy");
        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        super.onDestroy();
    }

    private class sendUserPhoto extends AsyncTask<String, Integer, JSONObject> {
        long totalSent, fileSize;
        int i;
        CircleProgressBar curBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch(sendedPhotos){
                case 0:
                    curBar=progressBar;
                    break;
                case 1:
                    curBar=(CircleProgressBar)findViewById(R.id.pbPhoto3);
                    break;
                case 2:
                    curBar=(CircleProgressBar)findViewById(R.id.pbPhoto2);
                    break;
                case 3:
                    curBar=(CircleProgressBar)findViewById(R.id.pbPhoto4);
                    break;
                case 4:
                    curBar=(CircleProgressBar)findViewById(R.id.pbPhoto5);
                    break;
                default:
                    curBar=progressBar;
                    break;
            }
            curBar.setVisibility(View.VISIBLE);
            rlAttach.setVisibility(View.VISIBLE);
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            String responseString = new String();
            JSONObject json = null;
            try
            {
                HttpClient client = new DefaultHttpClient();
                //File file = new File(pathToUserPhoto);
                File file = new File(pickedPhotos[sendedPhotos]);
                HttpPost post = new HttpPost(URL);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addBinaryBody("file", file, ContentType.create("image/jpeg"), file.getName());
                entityBuilder.addTextBody("action", "picture_upload");
                entityBuilder.addTextBody("token", token);
                i=0;
                // add more key/value pairs here as needed
                final HttpEntity entity = entityBuilder.build();
                fileSize=entity.getContentLength();
                //Log.v("result", EntityUtils.toString(httpEntity));
                class ProgressiveEntity implements HttpEntity {
                    @Override
                    public void consumeContent() throws IOException {
                        entity.consumeContent();
                    }
                    @Override
                    public InputStream getContent() throws IOException,
                            IllegalStateException {
                        return entity.getContent();
                    }
                    @Override
                    public Header getContentEncoding() {
                        return entity.getContentEncoding();
                    }
                    @Override
                    public long getContentLength() {
                        return entity.getContentLength();
                    }
                    @Override
                    public Header getContentType() {
                        return entity.getContentType();
                    }
                    @Override
                    public boolean isChunked() {
                        return entity.isChunked();
                    }
                    @Override
                    public boolean isRepeatable() {
                        return entity.isRepeatable();
                    }
                    @Override
                    public boolean isStreaming() {
                        return entity.isStreaming();
                    } // CONSIDER put a _real_ delegator into here!

                    @Override
                    public void writeTo(OutputStream outstream) throws IOException {

                        class ProxyOutputStream extends FilterOutputStream {
                            /**
                             * @author Stephen Colebourne
                             */

                            public ProxyOutputStream(OutputStream proxy) {
                                super(proxy);
                            }
                            public void write(int idx) throws IOException {
                                out.write(idx);
                            }
                            public void write(byte[] bts) throws IOException {
                                out.write(bts);
                            }
                            public void write(byte[] bts, int st, int end) throws IOException {
                                out.write(bts, st, end);
                            }
                            public void flush() throws IOException {
                                out.flush();
                            }
                            public void close() throws IOException {
                                out.close();
                            }
                        } // CONSIDER import this class (and risk more Jar File Hell)

                        class ProgressiveOutputStream extends ProxyOutputStream {
                            public ProgressiveOutputStream(OutputStream proxy) {
                                super(proxy);
                            }
                            public void write(byte[] bts, int st, int end) throws IOException {

                                // FIXME  Put your progress bar stuff here!

                                out.write(bts, st, end);
                                totalSent += end;
                                if(totalSent>(fileSize/10)){
                                    i++;
                                    Log.i("progressI", Integer.toString(i));
                                    publishProgress((int)(10*totalSent/fileSize));
                                    //totalSent=0;
                                }
                            }
                        }

                        entity.writeTo(new ProgressiveOutputStream(outstream));
                    }

                };
                ProgressiveEntity myEntity = new ProgressiveEntity();

                post.setEntity(myEntity);
                HttpResponse response = client.execute(post);
                final HttpEntity httpEntity = response.getEntity();
                responseString = EntityUtils.toString(httpEntity);
                json = new JSONObject(responseString);
                Log.d("photoSendResult", responseString);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            sendedPhotos=sendedPhotos+1;
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            curBar.setVisibility(View.GONE);

            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    Log.e("saveToken", e.toString());
                }
                if(status.equals("false"))
                {
                    //Toast.makeText(getApplicationContext(), "Изображение успешно загружено на сервер!", Toast.LENGTH_LONG).show();
                    try {
                        /*if(sendedPhotos==1) {
                            attached_ID = json.getString("attached_id");
                        }else if(sendedPhotos==2){
                            attached_ID2 = json.getString("attached_id");
                        }else if(sendedPhotos==3){
                            attached_ID3 = json.getString("attached_id");
                        }else if(sendedPhotos==4){
                            attached_ID4 = json.getString("attached_id");
                        }else if(sendedPhotos==5){
                            attached_ID5 = json.getString("attached_id");
                        }*/
                        attachedPhotos[sendedPhotos-1]=json.getString("attached_id");
                        attached_link[sendedPhotos-1]=json.getString("link");
                        //attachedPhoto.setVisibility(View.VISIBLE);
                        //tvCancelAttach.setVisibility(View.VISIBLE);
                        //Picasso.with(context).load(attached_link).resize(pic_width, 0).noFade().into(attachedPhoto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при отправке изображения!", Toast.LENGTH_LONG).show();
                }
            }
            if(sendedPhotos<pickedPhotos.length) {
                if ((pickedPhotos[sendedPhotos] != null) && (!pickedPhotos[sendedPhotos].equals(""))) {
                    new sendUserPhoto().execute();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("progress", Integer.toString(values[0]));
            //Log.i("progressbar", Integer.toString(progressBar.getProgress()));
            curBar.setProgress(values[0]*10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData(); //Это путь картинки
                    pathToUserPhoto = getImagePath(selectedImage);
                    Picasso.with(context).load(selectedImage).resize(pic_width, 0).noFade().into(attachedPhoto);
                    attachedPhoto.setVisibility(View.VISIBLE);
                    //new sendUserPhoto().execute();
                }
            case 250:
                if(resultCode == Activity.RESULT_OK){
                    Parcelable[] parcelableUris = imageReturnedIntent.getParcelableArrayExtra(ImagePickerActivity.TAG_IMAGE_URI);

                    //Java doesn't allow array casting, this is a little hack
                    Uri[] uris = new Uri[parcelableUris.length];
                    System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                    //pathToUserPhoto = getImagePath(uris[0]);
                    pathToUserPhoto=uris[0].toString();
                    Picasso.with(context).load(uris[0]).resize(pic_width, 0).noFade().into(attachedPhoto);
                    attachedPhoto.setVisibility(View.VISIBLE);
                    //new sendUserPhoto().execute();
                    //Do something with the uris array
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
                break;
        }
    }

    protected String getImagePath(Uri uri){
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
               setAdapter(intent.getStringArrayListExtra("list"));
            /*int i=0;
            for(String s:intent.getStringArrayExtra("list")){
                pickedPhotos[i]=s;
            }*/
            /*int j=0;
            for(int i=0; i<intent.getStringArrayExtra("list").length; i++){
                pickedPhotos[i]=intent.getStringArrayExtra("list")[i];
            }*/
            //pickedPhotos=intent.getStringArrayExtra("list");
        }
    };

    private void setAdapter( List<String> filePathList) {
        sendedPhotos=0;
        for(int i=0; i<5; i++){
            pickedPhotos[i]="";
        }
        for(int i=0; i<filePathList.size(); i++){
            pickedPhotos[i]=filePathList.get(i);
            switch(i){
                case 0:
                    if(!pickedPhotos[i].equals("")){
                        attachedPhoto.setVisibility(View.VISIBLE);
                        tvCancelAttach.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(new File(pickedPhotos[0])).resize(pic_width, 0).noFade().into(attachedPhoto);
                    }
                    break;
                case 1:
                    tvCancelAttach2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //pickedPhotos[1]="";
                            attachedPhoto2.setVisibility(View.GONE);
                            tvCancelAttach2.setVisibility(View.GONE);
                            byte i=0;
                            if(attachedPhoto.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto3.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto4.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto5.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(i==0){
                                rlAttach.setVisibility(View.GONE);
                            }
                        }
                    });
                    if(!pickedPhotos[i].equals("")) {
                        attachedPhoto2.setVisibility(View.VISIBLE);
                        tvCancelAttach2.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(new File(pickedPhotos[1])).resize(pic_width, 0).noFade().into(attachedPhoto2);
                    }
                    break;
                case 2:
                    tvCancelAttach3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //pickedPhotos[2]="";
                            attachedPhoto3.setVisibility(View.GONE);
                            tvCancelAttach3.setVisibility(View.GONE);
                            byte i=0;
                            if(attachedPhoto2.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto4.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto5.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(i==0){
                                rlAttach.setVisibility(View.GONE);
                            }
                        }
                    });
                    if(!pickedPhotos[i].equals("")){
                        attachedPhoto3.setVisibility(View.VISIBLE);
                        tvCancelAttach3.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(new File(pickedPhotos[2])).resize(pic_width, 0).noFade().into(attachedPhoto3);
                    }
                    break;
                case 3:
                    tvCancelAttach4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //pickedPhotos[3]="";
                            attachedPhoto4.setVisibility(View.GONE);
                            tvCancelAttach4.setVisibility(View.GONE);
                            byte i=0;
                            if(attachedPhoto2.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto3.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto5.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(i==0){
                                rlAttach.setVisibility(View.GONE);
                            }
                        }
                    });
                    if(!pickedPhotos[i].equals("")){
                        attachedPhoto4.setVisibility(View.VISIBLE);
                        tvCancelAttach4.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(new File(pickedPhotos[3])).resize(pic_width, 0).noFade().into(attachedPhoto4);
                    }
                    break;
                case 4:
                    tvCancelAttach5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //pickedPhotos[4]="";

                            attachedPhoto5.setVisibility(View.GONE);
                            tvCancelAttach5.setVisibility(View.GONE);
                            byte i=0;
                            if(attachedPhoto2.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto3.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto4.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(attachedPhoto.getVisibility()==View.VISIBLE){
                                i++;
                            }
                            if(i==0){
                                rlAttach.setVisibility(View.GONE);
                            }
                        }
                    });
                    if(!pickedPhotos[i].equals("")){
                        attachedPhoto5.setVisibility(View.VISIBLE);
                        tvCancelAttach5.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(new File(pickedPhotos[4])).resize(pic_width, 0).noFade().into(attachedPhoto5);
                    }
                    break;
            }
        }
        if((pickedPhotos[0]!=null)&&(!pickedPhotos[0].equals(""))){
            new sendUserPhoto().execute();
        }
    }

    private void findphotos(){
        attachedPhoto3=(ImageView)findViewById(R.id.ivAttachedPhoto2);
        tvCancelAttach3=(TextView)findViewById(R.id.tvCancelAttach2);
        attachedPhoto2=(ImageView)findViewById(R.id.ivAttachedPhoto3);
        tvCancelAttach2=(TextView)findViewById(R.id.tvCancelAttach3);
        attachedPhoto4=(ImageView)findViewById(R.id.ivAttachedPhoto4);
        tvCancelAttach4=(TextView)findViewById(R.id.tvCancelAttach4);
        attachedPhoto5=(ImageView)findViewById(R.id.ivAttachedPhoto5);
        tvCancelAttach5=(TextView)findViewById(R.id.tvCancelAttach5);
    }

    private void hidePhotos(){
        attachedPhoto.setVisibility(View.GONE);
        tvCancelAttach.setVisibility(View.GONE);
        attachedPhoto2.setVisibility(View.GONE);
        tvCancelAttach2.setVisibility(View.GONE);
        attachedPhoto3.setVisibility(View.GONE);
        tvCancelAttach3.setVisibility(View.GONE);
        attachedPhoto4.setVisibility(View.GONE);
        tvCancelAttach4.setVisibility(View.GONE);
        attachedPhoto5.setVisibility(View.GONE);
        tvCancelAttach5.setVisibility(View.GONE);
    }
}