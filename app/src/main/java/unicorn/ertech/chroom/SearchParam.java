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
    Spinner sexSpinner, regionSpinner, hereforSpinner;
    EditText city, age_from, age_till;
    final String SAVED_COLOR = "color";
    String URL = "http://im.topufa.org/index.php";
    String token;
    List<sResult> results  = new ArrayList<sResult>();
    CheckBox cb;
    String sex,region, online;
    List<NameValuePair> request = new ArrayList<NameValuePair>(2);

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
        online = "1";
        setColor();
        token = Main.str;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                {
                    online="0";
                }
                else
                {
                    online="1";
                }
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
            } else if (col == 0) {
                tvTitle.setBackgroundResource(R.color.green);
                butSearch.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                tvTitle.setBackgroundResource(R.color.orange);
                butSearch.setBackgroundResource(R.drawable.but_orange);
            } else if (col == 3) {
                tvTitle.setBackgroundResource(R.color.purple);
                butSearch.setBackgroundResource(R.drawable.but_purple);
            }
        }else{
            tvTitle.setBackgroundResource(R.color.green);
        }
    }

    private class Searching extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String s = String.valueOf(sexSpinner.getSelectedItemId());
            //region = regionSpinner.getSelectedItem().toString();
            region = "10";

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

            if(sexSpinner.getSelectedItemId()!=2) {
                jParser.setParam("sex", String.valueOf(sexSpinner.getSelectedItemId()));
            }
            else
            {
                //jParser.setParam("sex","");
            }

            jParser.setParam("region", region);
            jParser.setParam("online", online);
            jParser.setParam("herefor",String.valueOf(hereforSpinner.getSelectedItemId()));
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
}
