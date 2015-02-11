package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 07.01.2015.
 */
public class Registration extends Activity{
    Button regButton;
    EditText nick, name;
    TextView datePick, phonenumber;
    String URL = "http://im.topufa.org/index.php";
    Spinner sexSpin, searchSpin, spSpin;
    final String FILENAME = "token";
    public String token;
    int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 01;
    int myDay = 01;
    int sex = 1;
    int searchSex=0;
    int sp=0;
    boolean success;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regButton = (Button)findViewById(R.id.butReg);
        datePick = (TextView)findViewById(R.id.tvDatePick);
        //nick= (EditText)findViewById(R.id.etRegNick);
        name = (EditText)findViewById(R.id.etRegName);
        phonenumber=(TextView)findViewById(R.id.etRegNumber);
        phonenumber.setText(Reg1.number);
        sexSpin = (Spinner)findViewById(R.id.spinRegSex);
        searchSpin =(Spinner)findViewById(R.id.spinRegSexSearch);
        spSpin =(Spinner)findViewById(R.id.spinSp);
        sexSpin.setSelection(2);
        searchSpin.setSelection(2);
        //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        //String line1Number = telephonyManager.getLine1Number();
        //phonenumber.setText(line1Number);
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });

        token= getIntent().getStringExtra("Token");

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                sex=sexSpin.getSelectedItemPosition();
                searchSex=searchSpin.getSelectedItemPosition();
                sp=spSpin.getSelectedItemPosition();
                new RegSend().execute();
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            datePick.setText(myDay + "/" + myMonth + "/" + myYear);
        }
    };
    private class RegSend extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Получение данных профиля ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры

            jParser.setParam("action", "profile_set");
            jParser.setParam("token", token);
            jParser.setParam("name", name.getText().toString());
            jParser.setParam("day", Integer.toString(myDay));
            jParser.setParam("month", Integer.toString(myMonth));
            jParser.setParam("year", Integer.toString(myYear));
            if(sex<2) {
                jParser.setParam("sex", Integer.toString(sex));
            }
            if(searchSex<2) {
                jParser.setParam("lookingfor", Integer.toString(searchSex));
            }
            if(sp>0){
                jParser.setParam("sp", Integer.toString(sp));
            }
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                success=json.getBoolean("error");
                Log.e("saveToken", token);
            }catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            if(success==false){
                Intent i = new Intent(getApplicationContext(), Main.class);
                i.putExtra("Token", token);
                startActivity(i);
                //getParent().finish();
            }
            }
        }
    }
