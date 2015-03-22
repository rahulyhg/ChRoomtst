package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 28.01.2015.
 */
public class Reg1 extends Activity {
    EditText phonenumber;
    EditText etHuman;
    int clicks;
    String URL = "http://im.topufa.org/index.php";
    public static String number;
    boolean error;
    String token;
    TableRow trPass;
    EditText etPass;
    int userID;
    final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_1);

        phonenumber=(EditText)findViewById(R.id.editText1);
        Button next=(Button)findViewById(R.id.button4);
        etHuman=(EditText)findViewById(R.id.etHuman);
        trPass=(TableRow)findViewById(R.id.trReceivedPass);
        etPass=(EditText)findViewById(R.id.etReceivedPass);
        clicks=0;

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.parseDouble(etHuman.getText().toString())==2){
                    if(clicks==0) {
                        number = phonenumber.getText().toString();
                        new RegSend().execute();
                    }else{
                        new auth().execute();
                    }
                }
            }
        });
    }

    private class RegSend extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private String phone;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Reg1.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            phone="7"+phonenumber.getText().toString();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            jParser.setParam("action", "auth_singup");
            jParser.setParam("phone", phone);
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                token = json.getString("token");
                Log.e("saveToken", token);
            } catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            try {
                error=json.getBoolean("error");
            }catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            if(error==false){
                clicks++;
                phonenumber.setEnabled(false);
                trPass.setVisibility(View.VISIBLE);
            }
        }
    }

    private class auth extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Reg1.this);
            pDialog.setMessage("Выполняем вход ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("action", "auth_singin");
            jParser.setParam("phone", number);
            jParser.setParam("password", etPass.getText().toString());
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

                SharedPreferences sPref = getSharedPreferences("user", MODE_PRIVATE); //Сохраняем ID юзера, для доступа в профиле
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
                    Intent i = new Intent(getApplicationContext(), Registration.class);
                    i.putExtra("Token", token);
                    startActivity(i);
                }
            }
        }
    }
}
