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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetWallet extends Activity {
    final String SAVED_COLOR = "color";
    SharedPreferences sPref;
    RelativeLayout topRow;
    String token;
    AlertDialog.Builder ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private);
        //setContentView(R.layout.tab_incognito);

        ImageButton butBack=(ImageButton)findViewById(R.id.setBack);
        TextView blackList=(TextView)findViewById(R.id.tvBlackList);
        TextView notif=(TextView)findViewById(R.id.tvNotifications);
        TextView anonNick = (TextView)findViewById(R.id.tvPrivateNick);
        TextView delete = (TextView)findViewById(R.id.tvDeleteAcc);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOpen();
            }
        });
        anonNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonNickOpen();
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        blackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackOpen();
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifOpen();
            }
        });
        topRow=(RelativeLayout)findViewById(R.id.topRow_sp);
        SharedPreferences userData = getSharedPreferences("userdata", MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        ad = new AlertDialog.Builder(this);
        ad.setTitle("Удаление профиля");  // заголовок
        ad.setMessage("Вы уверены, что хотите безвозвратно удалить профиль?"); // сообщение
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                new delAcc().execute();
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        setColor();
    }


    public void closeMe(){
        this.finish();
    }
    public void blackOpen(){
        Intent i = new Intent(this, SetBlackList.class);
        startActivity(i);
    }

    public void notifOpen(){
        Intent i = new Intent(this, Notifications.class);
        startActivity(i);
    }

    public void anonNickOpen(){
        Intent i = new Intent(this, anon_nickname.class);
        startActivity(i);
    }

    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        int col=sPref.getInt(SAVED_COLOR, 0);
        switch (col) {
            case 0:
                topRow.setBackgroundResource(R.color.green);
                break;
            case 1:
                topRow.setBackgroundResource(R.color.blue);
                break;
            case 2:
                topRow.setBackgroundResource(R.color.orange);
                break;
            case 3:
                topRow.setBackgroundResource(R.color.purple);
                break;
            default:
                break;
        }
    };

    public  void deleteOpen()
    {
        ad.show();
        //Intent i = new Intent(this, deleteAcc.class);
        //startActivity(i);
    }

    @Override
    public  void onResume(){
        super.onResume();
        setColor();
    }

    public class delAcc extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "profile_delete");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                String success = "";
                try {
                    status = json.getString("error");
                    success = json.getString("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status.equals("false"))
                {
                    if(success.equals("true")) {
                        Toast.makeText(getApplicationContext(), "Аккаунт успешно удален!", Toast.LENGTH_LONG).show();
                        SharedPreferences userData = getSharedPreferences("userdata", MODE_PRIVATE);
                        SharedPreferences.Editor ed2 = userData.edit();  //Сохраняем токен
                        ed2.remove("token");
                        ed2.commit();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("finish", false);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Ошибка при удалении!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при удалении!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
