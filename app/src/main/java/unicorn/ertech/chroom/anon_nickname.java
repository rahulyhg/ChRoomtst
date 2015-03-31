package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class anon_nickname extends Activity {

    EditText nick;
    SharedPreferences sPref, sPref2;
    ImageButton butBack;
    RelativeLayout topRow;
    String token;
    final String SAVED_COLOR="color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anon_nickname_layout);
        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        butBack=(ImageButton)findViewById(R.id.setBack);
        Button readyBut=(Button)findViewById(R.id.readyButton);
        nick = (EditText)findViewById(R.id.etNick);
        topRow =(RelativeLayout)findViewById(R.id.topRow_sp);
        readyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nick.getText().toString().equals(""))
                {
                    if(isNetworkAvailable()) {
                        new setAnonNick().execute();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Ошибка при изменении! Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Нельзя вводить пустой ник!", Toast.LENGTH_LONG).show();
                }
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        setColor();
        sPref2=getPreferences(MODE_PRIVATE);
        if(sPref2.contains("anon_nick")){
            nick.setText(sPref2.getString("anon_nick",""));
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anon_nickname, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void closeMe(){
        this.finish();
    }

    private class setAnonNick extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры

            jParser.setParam("token", token);
            jParser.setParam("action", "profile_set");
            jParser.setParam("nick",nick.getText().toString());
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    Log.e("saveToken", e.toString());
                }

                if(status.equals("false"))
                {
                    SharedPreferences.Editor ed=sPref2.edit();
                    ed.putString("anon_nick", nick.getText().toString());
                    ed.commit();
                    Toast.makeText(getApplicationContext(), "Анонимный ник успешно изменён!", Toast.LENGTH_LONG).show();
                    close();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при изменении!", Toast.LENGTH_LONG).show();
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

    void close()
    {
        this.finish();
    }
}
