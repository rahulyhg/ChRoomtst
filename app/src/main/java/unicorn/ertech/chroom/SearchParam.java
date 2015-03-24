package unicorn.ertech.chroom;

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

/**
 * Created by Timur on 11.01.2015.
 */
public class SearchParam extends Fragment {
    private Context context;
    Button butSearch;
    TextView tvTitle;
    public static JSONParser jParserReserve = null;
    static Spinner sexSpinner, regionSpinner, hereforSpinner;
    static EditText city, age_from, age_till;
    final String SAVED_COLOR = "color";
    String URL = "http://im.topufa.org/index.php";
    static String token;
    List<sResult> results  = new ArrayList<sResult>();
    CheckBox cb;
    static String sex,region, online;
    List<NameValuePair> request = new ArrayList<NameValuePair>(2);
    int currentColorSpinner;

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
        city = (EditText)view.findViewById(R.id.etSearchCity);
        age_from = (EditText)view.findViewById(R.id.etSearchAge1);
        age_till = (EditText)view.findViewById(R.id.etSearchAge2);
        TextView age_from2 = (TextView)view.findViewById(R.id.tvSearchAge1);
        online = "1";
        setColor();
        token = Main.str;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                {
                    online="";
                }
                else
                {
                    online="1";
                }
            }
        });
        age_from2.setClickable(true);
        age_from2.setOnClickListener(new View.OnClickListener() {
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
        //SpinnersCustomAdapterCities adapter = new SpinnersCustomAdapterCities(getActivity().getApplicationContext(),
          //      currentColorSpinner, getResources().getStringArray(R.array.cities));
        //city.setAdapter(adapter);
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
            region = "";

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

            jParser.setParam("region", region);
            jParser.setParam("online", online);
            if(hereforSpinner.getSelectedItemId()==0)
            {
                jParser.setParam("herefor", "");
            }
            else {
                jParser.setParam("herefor", String.valueOf(hereforSpinner.getSelectedItemId()));
            }
            jParser.setParam("city", city.getText().toString());
            jParser.setParam("age_from", age_from.getText().toString());
            jParser.setParam("age_till", age_till.getText().toString());


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
        jPars.setParam("city", city.getText().toString());
        jPars.setParam("age_from", age_from.getText().toString());
        jPars.setParam("age_till", age_till.getText().toString());
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
            label.setText(getResources().getStringArray(R.array.cities)[position]);
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
            label.setText(getResources().getStringArray(R.array.cities)[position]);
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

    private PopupWindow pwindo;
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

            RollView roll = (RollView)layout.findViewById(R.id.rollView_a);
            roll.setList(list);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwindo.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
