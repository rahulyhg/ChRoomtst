package unicorn.ertech.chroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class notif extends Service {
    private NotificationManager manager;
    int lastId=0;
    final String SAVED_TOKEN = "token";
    SharedPreferences userData;
    SharedPreferences Notif;
    String URL = "http://im.topufa.org/index.php";
    String lastID4="";
    String lastid="";
    String token="";
    Timer  myTimer;
    final String SAVED_LASTID="lastid";
    final String SAVED_SOUND="sound";
    final String SAVED_VIBRO="vibro";
    final String SAVED_INDICATOR="indicator";
    public notif() {
    }

    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        userData = getSharedPreferences("userdata", MODE_PRIVATE);
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        if((userData.contains(SAVED_TOKEN))) {
            lastID4 = Notif.getString(SAVED_LASTID,"");
            token = userData.getString(SAVED_TOKEN, "");
            myTimer = new Timer();
            someTask();
        }



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTimer.cancel();
        myTimer = null;
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
        new Thread(new Runnable() {
            public void run() {
              myTimer = new Timer();
                myTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {
                        if (isNetworkAvailable()) {
                                globalChat4();
                        }
                    }
                }, 1L * 250, 5L * 1000);
            }
        }).start();
    }

    private void globalChat4 (){
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "dialogs_get");
            jParser.setParam("background", "true");
            jParser.setParam("firstid", lastID4);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            if(json!=null) {
                String realNum = "";
                String fakeNum = "";
                String s = null;
                JSONArray real = null;
                JSONObject messag = null;

                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s.equals("false"))
                {
                    try {
                        realNum = json.getString("total");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        lastID4 = json.getString("lastid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!realNum.equals("0")) {
                        Log.e("notif_num", realNum);
                        Log.e("notif_token", token);
                        Log.e("notif_lastId", lastID4);
                        int i = createInfoNotification("У Вас есть непрочитанные сообщения");
                    }
                }
                else
                {
                    Log.e("notif_token", token);
                    Log.e("notif_error", json.toString());
                }
            }

        }


    public int createInfoNotification(String message){
        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(getApplicationContext(), Main.class);
        i.putExtra("Token",token);
        //startActivity(i); // по клику на уведомлении откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle("ChatRoom"); //заголовок уведомления
                //.setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

        nb.setVisibility(Notification.VISIBILITY_PUBLIC);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(Notif.contains(SAVED_SOUND)&&Notif.contains(SAVED_VIBRO)&&Notif.contains(SAVED_INDICATOR))
        {
            if(Notif.getString(SAVED_SOUND,"").equals("true"))
            {
                nb.setSound(alarmSound);
            }

            if(Notif.getString(SAVED_VIBRO,"").equals("true"))
            {
                nb.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            }

            if(Notif.getString(SAVED_INDICATOR,"").equals("true"))
            {
                nb.setLights(Color.RED, 3000, 3000);
            }
        }
        else
        {
            nb.setDefaults(Notification.DEFAULT_ALL);
        }

        Notification notification = nb.getNotification(); //генерируем уведомление
        manager.notify(0, notification); // отображаем его пользователю.
        //notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        return 0;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
