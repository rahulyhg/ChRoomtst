package unicorn.ertech.chroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    Spinner sexSpinner, regionSpinner;
    EditText city,age_from, age_till;
    final String SAVED_COLOR = "color";
    String URL = "http://im.topufa.org/index.php";
    String token;
    List<sResult> results  = new ArrayList<sResult>();
    CheckBox cb;
    String sex,region, online;

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
        city = (EditText)view.findViewById(R.id.etSearchCity);
        age_from = (EditText)view.findViewById(R.id.etSearchAge1);
        age_till = (EditText)view.findViewById(R.id.etSearchAge2);
        online = "0";
        setColor();
        token = Main.str;

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
                tvTitle.setBackgroundResource(R.drawable.b_string);
                butSearch.setBackgroundResource(R.drawable.but_blue);
            } else if (col == 0) {
                tvTitle.setBackgroundResource(R.drawable.g_strip);
                butSearch.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                tvTitle.setBackgroundResource(R.drawable.o_strip);
                butSearch.setBackgroundResource(R.drawable.but_orange);
            } else if (col == 3) {
                tvTitle.setBackgroundResource(R.drawable.p_string);
                butSearch.setBackgroundResource(R.drawable.but_purple);
            }
        }else{
            tvTitle.setBackgroundResource(R.drawable.g_strip);
        }
    }

    private class Searching extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String s = sexSpinner.getSelectedItem().toString();
            //region = regionSpinner.getSelectedItem().toString();
            region = "10";
            if(cb.isChecked())
            {
                online = "0";
            }
            else {online = "0";}



            if(s.equals("М")) {
                sex = "1";
            }
            else
            {
                sex = "0";
            }

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
            jParser.setParam("sex", sex);
            jParser.setParam("region", region);
            jParser.setParam("online", online);
            jParser.setParam("city", city.getText().toString());
            jParser.setParam("age_from", age_from.getText().toString());
            jParser.setParam("age_till", age_till.getText().toString());
            //jParser.setParam("deviceid", "");
            // Getting JSON from URL
            Log.e("sendjson", "1111");
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.e("receivedjson", "2222");
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
}
