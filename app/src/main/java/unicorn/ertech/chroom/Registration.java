package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    Spinner sexSpin, searchSpin, spSpin, regionSpin, citySpin;
    int currentRegion = 0, currentCities=R.array.citiesMsk;
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
    MyCustomAdapter adapter;


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
        regionSpin=(Spinner)findViewById(R.id.spinnerRegReg);
        citySpin=(Spinner)findViewById(R.id.spinnerRegCit);
        final MyCustomAdapter2 adapter2 = new MyCustomAdapter2(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(R.array.regions));
        regionSpin.setAdapter(adapter2);
        adapter = new MyCustomAdapter(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
        citySpin.setAdapter(adapter);

        regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                currentRegion=selectedItemPosition;
                currentCities=GeoConvertIds.getCityArrayId(currentRegion);
                adapter=null;
                adapter = new MyCustomAdapter(getApplicationContext(),
                        R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
                adapter.notifyDataSetChanged();
                citySpin.setAdapter(adapter);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                if((name.getText().length()==0)&&(!name.getText().equals(" "))){
                    Toast.makeText(getApplicationContext(), "Пустое имя!", Toast.LENGTH_SHORT).show();
                }else{
                    Context context = getApplicationContext();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    sex=sexSpin.getSelectedItemPosition();
                    searchSex=searchSpin.getSelectedItemPosition();
                    sp=spSpin.getSelectedItemPosition();
                    new RegSend().execute();
                }
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
        private int reg, cit;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Получение данных профиля ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            reg=GeoConvertIds.getServerRegionId(regionSpin.getSelectedItemPosition());
            cit=GeoConvertIds.getServerCityId(citySpin.getSelectedItemPosition());
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
            jParser.setParam("region", Integer.toString(reg));
            jParser.setParam("city", Integer.toString(cit));
            jParser.setParam("status", "Всем привет, я теперь в изюме!");
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
                SharedPreferences sPref2 = getSharedPreferences("saved_chats", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref2.edit();
                ed.putInt("regSrv", reg);
                ed.putInt("region", regionSpin.getSelectedItemPosition());
                ed.putInt("citySrv", cit);
                ed.putInt("city", citySpin.getSelectedItemPosition());
                ed.putString("cityStr", citySpin.getSelectedItem().toString());
                ed.commit();
                Intent i = new Intent(getApplicationContext(), Main.class);
                i.putExtra("Token", token);
                startActivity(i);
                //getParent().finish();
            }
            }
        }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(currentCities)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_with_arrows, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(currentCities)[position]);
            return row;
        }
    }

    public class MyCustomAdapter2 extends ArrayAdapter<String> {

        public MyCustomAdapter2(Context context, int textViewResourceId,
                                String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_with_arrows, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }
    }
    }
