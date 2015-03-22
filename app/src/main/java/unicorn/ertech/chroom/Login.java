package unicorn.ertech.chroom;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.facebook.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Login extends Activity {

    Button logButton;
    TextView regButton;
    EditText log, pass;
    TextView resetPass;
    CheckBox checkSavePass;
    String URL = "http://im.topufa.org/index.php";
    final String FILENAME = "token";
    final String USER = "user";
    final String SAVED_TOKEN = "token";
    final String SAVED_LOGIN = "login";
    final String SAVED_PASSWORD = "password";
    public String token;
    int userID;
    SharedPreferences sPref;
    SharedPreferences userData;
    auto_auth aaTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("finish", false)){this.finish();}
        setContentView(R.layout.activity_login);

        //getActionBar().hide();

        logButton = (Button)findViewById(R.id.logButton);
        log = (EditText)findViewById(R.id.logText);
        pass = (EditText)findViewById(R.id.pasText);
        regButton = (TextView)findViewById(R.id.regButton);
        resetPass = (TextView)findViewById(R.id.tvResetPassword);
        checkSavePass=(CheckBox)findViewById(R.id.checkBoxSavePass);

        userData = getSharedPreferences("userdata", MODE_PRIVATE);
        if(userData.contains(SAVED_LOGIN)){
            log.setText(userData.getString(SAVED_LOGIN,""));
        }
        if((userData.contains(SAVED_PASSWORD))){
            if(!userData.getString(SAVED_PASSWORD, "0").equals("0")){
                pass.setText(userData.getString(SAVED_PASSWORD,""));
                token=userData.getString(SAVED_TOKEN, "");
                aaTask= new auto_auth();
                aaTask.execute();
        }
        }
        if((userData.contains(SAVED_TOKEN))){

        }

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMain();
            }
        });
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReg();
            }
        });
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReset();
            }
        });

    }

    private void startReg(){
        Intent i = new Intent(this, Reg1.class);
        startActivity(i);
    }

    private void startReset(){
        Intent i = new Intent(this, Reset.class);
        startActivity(i);
    }

    private void startMain() {

        if(isNetworkAvailable()) {
            new auth().execute();
            //readFile();
            //Intent i = new Intent(this, Main.class);
            //i.putExtra("Token", token);
            //startActivity(i);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Нет активного соединения с Интернет!",Toast.LENGTH_SHORT).show();
        }

    }

    void writeFile() {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            bw.write(token);
            // закрываем поток
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readFile() {
        StringBuffer s2;
        String s=null;
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));

            s2 = new StringBuffer();
            // читаем содержимое
            while ((s = br.readLine()) != null) {
                s2.append(s) ;
            }
            token = s2.toString();
            //Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    private class auth extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Выполняем вход ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            SharedPreferences.Editor ed2 = userData.edit();  //Сохраняем логин
            ed2.putString(SAVED_LOGIN, log.getText().toString());
            if(checkSavePass.isChecked()){
                ed2.putString(SAVED_PASSWORD, pass.getText().toString());
            }else{ed2.putString(SAVED_PASSWORD, "0");}
            ed2.commit();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("action", "auth_singin");
            jParser.setParam("phone", log.getText().toString());
            jParser.setParam("password", pass.getText().toString());
            jParser.setParam("deviceid", "");
            //jParser.setParam("device_id", "dsfadfg");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            boolean error;
            error=true;
            try {
                token = json.getString("token");
                userID=json.getInt("id");
                error=json.getBoolean("error");

                SharedPreferences.Editor ed2 = userData.edit();  //Сохраняем токен и пароль
                //if(checkSavePass.isChecked()) {
                //    ed2.putString(SAVED_PASSWORD, pass.getText().toString());
                //}
                ed2.putString(SAVED_TOKEN, token);
                ed2.commit();

                sPref = getSharedPreferences("user", MODE_PRIVATE); //Сохраняем ID юзера, для доступа в профиле
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt(USER, userID);
                ed.commit();

                Log.e("saveToken", token);
            } catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            if(error==false) {
                if (token.equals("false")) {
                    Toast.makeText(getApplicationContext(), "Необходима авторизация!", Toast.LENGTH_LONG).show();
                } else {
                    //writeFile();
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    i.putExtra("Token", token);
                    startActivity(i);
                    Log.e("writefile", token);
                }
            }
        }
    }

    private class auto_auth extends AsyncTask<String, String, JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences.Editor ed2 = userData.edit();  //Сохраняем логин
            ed2.putString(SAVED_LOGIN, log.getText().toString());
            ed2.commit();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            jParser.setParam("action", "check");
            jParser.setParam("token", token);
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null){
                boolean result;
            result = true;
            try {
                result = json.getBoolean("auth");
//                Log.e("saveToken", token);
            } catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            if (result == true) {
                if (token.equals("false")) {
                    Toast.makeText(getApplicationContext(), "Необходима авторизация!", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    i.putExtra("Token", token);
                    startActivity(i);
                    Log.e("writefile", token);
                }
            }
        }
            else{
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("login","resume");
        aaTask = new auto_auth();
//        Log.d("login", aaTask.getStatus().toString());
        AsyncTask.Status aaStatus= aaTask.getStatus();
        if(aaStatus== AsyncTask.Status.FINISHED){
            aaTask = new auto_auth();
            aaTask.execute();
        }else if(aaStatus== AsyncTask.Status.PENDING){
            //aaTask.execute();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }


}
