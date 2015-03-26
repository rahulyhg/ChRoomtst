package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 11.01.2015.
 */
public class IncognitoRnd  extends Fragment {
    private Context context;
    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;
    Sensor sensorGravity;
    Animation myAnim;
    ImageView buttonStart;
    final String SAVED_COLOR = "color";
    String URL = "http://im.topufa.org/index.php", token;


    StringBuilder sb = new StringBuilder();
    Timer timer;


    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.incognito_rnd, container, false);
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        sensorManager = (SensorManager) getActivity().getSystemService(context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        TextView tvText = (TextView)view.findViewById(R.id.textView12);
        Typeface tface = Typeface.createFromAsset(getActivity().getAssets(), "FiraSans-Regular.ttf");
        tvText.setTypeface(tface);
        buttonStart = (ImageView) view.findViewById(R.id.ivIncognitoRnd);
        myAnim = AnimationUtils.loadAnimation(context, R.anim.vibro_anim);
        myAnim.setRepeatCount(10);
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                buttonStart.startAnimation(myAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        buttonStart.startAnimation(myAnim);
        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                buttonStart.setImageResource(R.drawable.search_randomb);
            } else if (col == 0) {
                buttonStart.setImageResource(R.drawable.search_random);
            } else if (col == 2) {
                buttonStart.setImageResource(R.drawable.search_randomo);
            } else if (col == 3) {
                buttonStart.setImageResource(R.drawable.search_randomp);
            }
        } else {
            buttonStart.setBackgroundResource(R.drawable.search_random);
        }
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(listener, sensorAccel,
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(listener, sensorLinAccel,
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(listener, sensorGravity,
                        SensorManager.SENSOR_DELAY_NORMAL);

                    new Searching().execute();
            }
        });


        return view;
    }


    /*public void startListen(){
        sensorManager = (SensorManager)getActivity().getSystemService(context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }*/


    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i]
                                - valuesAccelGravity[i];
                    }
                    int j = 0;
                    if (valuesAccelMotion[0] > 5) j++;
                    if (valuesAccelMotion[1] > 5) j++;
                    if (valuesAccelMotion[2] > 5) j++;
                    if (j > 1) {
                        /*Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        long milliseconds = 1000;
                        v.vibrate(milliseconds);*/
                        new Searching().execute();
                        sensorManager.unregisterListener(listener);
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
            }

        }

    };

    private class Searching extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Ищем ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            JSONObject json = null;
            long wait = 2200;
            String status = "";String num = "";
            long elapsedtime = 0;
            long startTime=   System.currentTimeMillis();
            do {

                //ставим нужные нам параметры
                jParser.setParam("token", token);
                jParser.setParam("action", "lookfor");
                jParser.setParam("type", "3");
                if(elapsedtime<5000) {
                    json = jParser.getJSONFromUrl(URL);
                    try {
                        status = json.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status.equals("false")) {
                        try {
                            num = json.getString("total");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!num.equals("0")) {
                            break;
                        } else {
                            try {
                                Thread.sleep(wait);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    long currTime = System.currentTimeMillis();
                    elapsedtime = currTime - startTime;

                    if (elapsedtime > 5000) {
                        break;
                    }
                }
                else{break;}
            }while(true);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            String status = "";
            if(json!=null) {
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    String num = "";
                    try {
                        num = json.getString("total");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (num.equals("0")) {
                        Toast.makeText(getActivity().getApplicationContext(), "Нет результатов!", Toast.LENGTH_SHORT).show();
                        sensorManager.registerListener(listener, sensorAccel,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        sensorManager.registerListener(listener, sensorLinAccel,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        sensorManager.registerListener(listener, sensorGravity,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        String s = "", avatar = "", id = "";
                        JSONObject messag = null;

                        try {
                            s = json.getString("nick");
                            avatar = json.getString("avatar");
                            id = json.getString("id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }


                        Intent i = new Intent(getActivity().getApplicationContext(), anonMessaging.class);
                        i.putExtra("nick", s);
                        i.putExtra("userId", id);
                        i.putExtra("fake", "true");
                        i.putExtra("token", token);
                        i.putExtra("avatar", avatar);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска, проверьте Ваше подключение к Интернету!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(listener);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                buttonStart.setImageResource(R.drawable.search_randomb);
            } else if (col == 0) {
                buttonStart.setImageResource(R.drawable.search_random);
            } else if (col == 2) {
                buttonStart.setImageResource(R.drawable.search_randomo);
            } else if (col == 3) {
                buttonStart.setImageResource(R.drawable.search_randomp);
            }
        }
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
