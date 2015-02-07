package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 07.01.2015.
 */
public class Reset extends Activity{
    Button resetButton;
    EditText phonenumber;
    TextView datePick;
    String URL = "http://im.topufa.org/index.php";
    final String FILENAME = "token";
    public String token;
    boolean error;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        resetButton = (Button)findViewById(R.id.butResetPass);
        phonenumber=(EditText)findViewById(R.id.etResetPhone);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                new ResetSend().execute();
            }
        });
    }


    private class ResetSend extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            //ставим нужные нам параметры
            jParser.setParam("action", "auth_singup");
            jParser.setParam("phone", phonenumber.getText().toString());
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                error=json.getBoolean("error");
                Log.e("saveToken", token);
            }catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            if(error==false){
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                getParent().finish();
            }
            }
        }
    }
