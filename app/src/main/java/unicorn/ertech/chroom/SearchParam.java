package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by Timur on 11.01.2015.
 */
public class SearchParam extends Fragment {
    private Context context;
    Button butSearch;
    TextView tvTitle;
    public JSONParser jParserReserve = null;
    static Spinner sexSpinner;
    static Spinner regionSpinner, hereforSpinner;
    EditText  age_till;
    TextView age_from;
    static Spinner city;
    final String SAVED_COLOR = "color";
    String URL = "http://im.topufa.org/index.php";
    static String token;
    List<sResult> results  = new ArrayList<sResult>();
    CheckBox cb;
    static String sex,region, online;
    static int age1=18, age2=80;
    List<NameValuePair> request = new ArrayList<NameValuePair>(2);
    int currentColorSpinner;
    private PopupWindow pwindo;
    static int cit, reg;
    SharedPreferences savedParams;
    int savedCity=0, incr=0;
    int currentRegion = 8, currentCities=R.array.cities;
    SpinnersCustomAdapterCities adapter;

    /** Handle the results from the voice recognition activity. */
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
        final View view = inflater.inflate(R.layout.tab_search, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        butSearch=(Button)view.findViewById(R.id.butSearch);
        tvTitle=(TextView)view.findViewById(R.id.tvSearchTitle);
        cb = (CheckBox)view.findViewById(R.id.cbSearchOnline);
        sexSpinner = (Spinner)view.findViewById(R.id.spinSearchSex);
        regionSpinner = (Spinner)view.findViewById(R.id.spinSearchRegion);
        hereforSpinner=(Spinner)view.findViewById(R.id.spinSearchHerefor);
        city = (Spinner)view.findViewById(R.id.etSearchCity);
        age_from = (TextView)view.findViewById(R.id.etSearchAge1);
        age_till = (EditText)view.findViewById(R.id.etSearchAge2);
        TextView age_from2 = (TextView)view.findViewById(R.id.tvSearchAge1);
        online = "";
        setColor();
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                {
                    online="1";
                }
                else
                {
                    online="";
                }
            }
        });
        age_from.setClickable(true);
        age_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
        butSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity().getApplicationContext();
                new Searching().execute();

            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                currentRegion=selectedItemPosition;
                currentCities=GeoConvertIds.getCityArrayId(currentRegion);
                //adapter.notifyDataSetChanged();
                //adapter.clear();
                adapter=null;
                adapter = new SpinnersCustomAdapterCities(getActivity().getApplicationContext(),
                        currentColorSpinner, getResources().getStringArray(currentCities));
                adapter.notifyDataSetChanged();
                city.setAdapter(adapter);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    public void setColor(){
        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvTitle.setBackgroundResource(R.color.blue);
                butSearch.setBackgroundResource(R.drawable.but_blue);
                cb.setButtonDrawable(R.drawable.checkbox_selector_b);
                currentColorSpinner=R.layout.spinner_with_arrows_b;
            } else if (col == 0) {
                tvTitle.setBackgroundResource(R.color.green);
                butSearch.setBackgroundResource(R.drawable.but_green);
                cb.setButtonDrawable(R.drawable.checkbox_selector_g);
                currentColorSpinner=R.layout.spinner_with_arrows_g;
            } else if (col == 2) {
                tvTitle.setBackgroundResource(R.color.orange);
                butSearch.setBackgroundResource(R.drawable.but_orange);
                cb.setButtonDrawable(R.drawable.checkbox_selector_o);
                currentColorSpinner=R.layout.spinner_with_arrows_o;
            } else if (col == 3) {
                tvTitle.setBackgroundResource(R.color.purple);
                butSearch.setBackgroundResource(R.drawable.but_purple);
                cb.setButtonDrawable(R.drawable.checkbox_selector_p);
                currentColorSpinner=R.layout.spinner_with_arrows_p;
            }
        }else{
            tvTitle.setBackgroundResource(R.color.green);
            currentColorSpinner=R.layout.spinner_with_arrows_g;
        }
        adapter = new SpinnersCustomAdapterCities(getActivity().getApplicationContext(),
                currentColorSpinner, getResources().getStringArray(R.array.cities));
        city.setAdapter(adapter);
        SpinnersCustomAdapter adapter2 = new SpinnersCustomAdapter(getActivity().getApplicationContext(),
                currentColorSpinner, getResources().getStringArray(R.array.regions));
        regionSpinner.setAdapter(adapter2);
        SpinnersCustomAdapterSex adapter3 = new SpinnersCustomAdapterSex(getActivity().getApplicationContext(),
                currentColorSpinner, getResources().getStringArray(R.array.sex));
        sexSpinner.setAdapter(adapter3);
        SpinnersCustomAdapterHerefor adapter4 = new SpinnersCustomAdapterHerefor(getActivity().getApplicationContext(),
                currentColorSpinner, getResources().getStringArray(R.array.herefor));
        hereforSpinner.setAdapter(adapter4);
    }

    private class Searching extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String s = String.valueOf(sexSpinner.getSelectedItemId());
            //region = regionSpinner.getSelectedItem().toString();
            cit=GeoConvertIds.getServerCityId(city.getSelectedItemPosition());
            reg=GeoConvertIds.getServerRegionId(regionSpinner.getSelectedItemPosition());
            //region = "";

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Ищем ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "global_search");

            if(sexSpinner.getSelectedItemId()!=2) {
                jParser.setParam("sex", String.valueOf(sexSpinner.getSelectedItemId()));
            }
            else
            {
                //jParser.setParam("sex","");
            }

            jParser.setParam("region", Integer.toString(reg));
            jParser.setParam("online", online);
            if(hereforSpinner.getSelectedItemId()==0)
            {
                jParser.setParam("herefor", "");
            }
            else {
                jParser.setParam("herefor", String.valueOf(hereforSpinner.getSelectedItemId()));
            }
            jParser.setParam("city", Integer.toString(cit));
            //jParser.setParam("age_from", age_from.getText().toString());
            //jParser.setParam("age_till", age_till.getText().toString());
            jParser.setParam("age_from", Integer.toString(age1));
            jParser.setParam("age_till", Integer.toString(age2));

            // Getting JSON from URL
            request = jParser.nameValuePairs;
            jParser.setParam("page", "0");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            String status = "";

            try {
                status = json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("false"))
            {
                String num = "";
                try {
                    num = json.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(num.equals("0")) {
                    //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(sexSpinner.getSelectedItemId()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity().getApplicationContext(), "Никто не удовлетворяет вашим параметрам!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String s="";
                    JSONArray arr = null;
                    JSONObject messag = null;
                    try {
                        s = json.getString("data");
                        arr = new JSONArray(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < Integer.parseInt(num); i++)
                    {
                        try {
                            messag = new JSONObject(arr.get(i).toString());
                            Log.e("messagads", messag.toString());
                            results.add(0,new sResult(messag.getString("id"),messag.getString("nickname"),messag.getString("avatar")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }

                    Intent i = new Intent(getActivity().getApplicationContext(),SearchResult.class);
                    i.putExtra("results",s);
                    i.putExtra("total",num);
                    i.putExtra("request",request.toString());
                    startActivity(i);
                }
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setColor();
        savedParams=getActivity().getPreferences(Context.MODE_PRIVATE);
        if(savedParams.contains("spinnerSex")){
            sexSpinner.setSelection(savedParams.getInt("spinnerSex", 0));
            regionSpinner.setSelection(savedParams.getInt("spinnerRegion", 0));
            hereforSpinner.setSelection(savedParams.getInt("spinnerHerefor", 0));
            cb.setSelected(savedParams.getBoolean("online", false));
            if(cb.isChecked()){
                online="1";
            }else{
                online="";
            }
            city.setSelection(savedParams.getInt("spinnerCity", 0));
            //citySpinner.setSelection(savedParams.getInt("spinnerCity", 0));
        }
        if(savedParams.contains("age1")){
            age1=savedParams.getInt("age1", age1);
        }
        if(savedParams.contains("age2")){
            age2=savedParams.getInt("age2", age2);
        }
        age_from.setText(Integer.toString(age1)+" - "+Integer.toString(age2));
    }

    @Override
    public void onPause(){
        savedParams=getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed4 = savedParams.edit();
        ed4.putInt("spinnerSex", sexSpinner.getSelectedItemPosition());
        ed4.putInt("spinnerRegion", regionSpinner.getSelectedItemPosition());
        ed4.putInt("spinnerHerefor", hereforSpinner.getSelectedItemPosition());
        //ed4.putInt("spinnerCity", sexSpinner.getSelectedItemPosition());
        ed4.putInt("age1", age1);
        ed4.putInt("age2", age2);
        ed4.putInt("spinnerCity", city.getSelectedItemPosition());
        ed4.putBoolean("online", cb.isChecked());
        ed4.commit();
        super.onPause();
    }

    public static void getParams(JSONParser jPars)
    {
        jPars.setParam("token", token);
        jPars.setParam("action", "global_search");

        if(sexSpinner.getSelectedItemId()!=2) {
            jPars.setParam("sex", String.valueOf(sexSpinner.getSelectedItemId()));
        }
        else
        {
            //jParser.setParam("sex","");
        }

        jPars.setParam("region", region);
        jPars.setParam("online", online);
        jPars.setParam("herefor",String.valueOf(hereforSpinner.getSelectedItemId()));
        //jPars.setParam("city", city.getText().toString());
        //jPars.setParam("age_from", age_from.getText().toString());
        //jPars.setParam("age_till", age_till.getText().toString());
        jPars.setParam("age_from", Integer.toString(age1));
        jPars.setParam("age_till", Integer.toString(age2));
    }

    private class SpinnersCustomAdapter extends ArrayAdapter<String> {

        public SpinnersCustomAdapter(Context context, int textViewResourceId,
                                String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getActivity().getLayoutInflater();
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

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(currentColorSpinner, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }
    }

    private class SpinnersCustomAdapterCities extends ArrayAdapter<String> {

        public SpinnersCustomAdapterCities(Context context, int textViewResourceId,
                                     String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getActivity().getLayoutInflater();
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

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(currentColorSpinner, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(currentCities)[position]);
            return row;
        }
    }

    private class SpinnersCustomAdapterSex extends ArrayAdapter<String> {

        public SpinnersCustomAdapterSex(Context context, int textViewResourceId,
                                           String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.sex)[position]);
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

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(currentColorSpinner, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.sex)[position]);
            return row;
        }
    }

    private class SpinnersCustomAdapterHerefor extends ArrayAdapter<String> {

        public SpinnersCustomAdapterHerefor(Context context, int textViewResourceId,
                                        String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.herefor)[position]);
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

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(currentColorSpinner, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.herefor)[position]);
            return row;
        }
    }

    private void initiatePopupWindow() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//View view = inflater.inflate(R.layout.fragment_blank, container, false);
            View layout = inflater.inflate(R.layout.numbers_picker, null, false);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int window_width = metricsB.widthPixels;
            int window_height = metricsB.heightPixels;
            List<String> list = new ArrayList<String>();
            for (int i = 10; i < 99; i++){
                list.add(Integer.toString(i));
            }

            pwindo = new PopupWindow(layout, window_width, window_height, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            final NumberPicker np1 = (NumberPicker)layout.findViewById(R.id.numberPicker1);
            final NumberPicker np2 = (NumberPicker)layout.findViewById(R.id.numberPicker2);

            np1.setMinValue(18);
            np1.setMaxValue(80);

            np2.setMinValue(18);
            np2.setMaxValue(80);

            np1.setWrapSelectorWheel(false);
            np2.setWrapSelectorWheel(false);

            np1.setValue(age1);
            np2.setValue(age2);

            np1.refreshDrawableState();
            //RollView roll = (RollView)layout.findViewById(R.id.rollView_a);
            //RollView roll2 = (RollView)layout.findViewById(R.id.rollView_b);
            //roll.setList(list);
            //roll2.setList(list);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    age1=np1.getValue();
                    age2=np2.getValue();
                    String str;
                    str=Integer.toString(age1)+" - "+Integer.toString(age2);
                    age_from.setText(str);
                    pwindo.dismiss();
                }
            });

            pwindo.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    pwindo.dismiss();
                    //age_from.setText(Integer.toString(age1)+"   -   "+Integer.toString(age2));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
