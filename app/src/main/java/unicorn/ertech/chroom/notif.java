package unicorn.ertech.chroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    String lastID4="";
    String lastid="";
    String token="";
    Timer  myTimer;
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
        token = intent.getStringExtra("token");
        myTimer = new Timer();
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                                new globalChat4().execute();
                        } else {
                            //Toast.makeText(getActivity().getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, 1L * 250, 2L * 1000);
            }
        }).start();
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
            jParser.setParam("token", Main.str);
            jParser.setParam("action", "pm_get");
            jParser.setParam("firstid", lastID4);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String realNum = "";
                String fakeNum = "";
                String s = null;
                JSONArray real = null;
                JSONObject messag = null;
                try {
                    realNum = json.getString("real_total");
                    fakeNum = json.getString("fake_total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    lastID4 = json.getString("firstid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!realNum.equals("0"))
                {
                    int i = createInfoNotification("У Вас есть непрочитанные сообщения");
                }
            }

        }
    }//конец asyncTask


    public int createInfoNotification(String message){
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(getApplicationContext(), Main.class);
        i.putExtra("Token",token);
        //startActivity(i); // по клику на уведомлении откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle("ChatRoom") //заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

        Notification notification = nb.getNotification(); //генерируем уведомление
        manager.notify(lastId, notification); // отображаем его пользователю.
        //notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        return lastId++;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
