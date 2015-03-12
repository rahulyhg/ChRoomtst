package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 31.01.2015.
 */
public class SearchResult extends Activity {
    List<sResult> results  = new ArrayList<sResult>();
    SearchResultAdapter adapter;
    int page = 1;
    int scrolling = 0, ScrollState=-1;
    String request = "";
    JSONObject reqJson = null;
    int count = 0;
    boolean state = true;
    boolean notAll = true;
    PopupWindow filterPopup;
    Button backButton;
    ImageButton butFilter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        backButton=(Button)findViewById(R.id.searchBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        butFilter=(ImageButton)findViewById(R.id.butFilter);
        butFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });
        final GridView gridview = (GridView) findViewById(R.id.gridView);

        gridview.setNumColumns(3);


        Intent k = getIntent();
        String result = k.getStringExtra("results");
        String num = k.getStringExtra("total");
        request = k.getStringExtra("request");
        request = request.replace('=',':');

        JSONArray arr = null;

        JSONObject messag = null;
        try {
            arr = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Integer.parseInt(num); i++)
        {
            try {
                messag = new JSONObject(arr.get(i).toString());
                Log.e("search", messag.toString());
                results.add(count,new sResult(messag.getString("id"),messag.getString("nickname"),messag.getString("avatar")));
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                //Log.e("NullPointerException1111", e.toString());
            }
        }
        adapter = new SearchResultAdapter(results,this);
        gridview.setAdapter(adapter);

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                        ScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrolling < firstVisibleItem && state) {
                        state = false;
                    if(notAll) {
                        new Searching().execute();
                    }

                }
                scrolling = firstVisibleItem;
            }
        });
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            // TODO Auto-generated method stub
            // Sending image id to FullScreenActivity
            Intent i = new Intent(getApplicationContext(), Profile2.class);
            // passing array index
            i.putExtra("userId", results.get(position).uid);
            i.putExtra("token", Main.str);
            i.putExtra("avatar", results.get(position).picUrl);
            startActivity(i);
        }
    };

    private class Searching extends AsyncTask<String, String, JSONObject> {
        //private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {

            /*pDialog = new ProgressDialog(SearchResult.this);
            pDialog.setMessage("Загрузка ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();*/


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            SearchParam.getParams(jParser);
            jParser.setParam("page",String.valueOf(page));
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            //pDialog.dismiss();
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
                    notAll=false;
                    //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(sexSpinner.getSelectedItemId()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Больше нет результатов!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String s="";
                    JSONArray arr = null;
                    JSONObject messag = null;
                    try {
                        page++;
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
                            results.add(count,new sResult(messag.getString("id"),messag.getString("nickname"),messag.getString("avatar")));
                            count++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }
                    }
                }
                state = true;
                adapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Ошибка при добавлении!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initiatePopupWindow() {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.tab_search, null, false);
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int window_width = metricsB.widthPixels;
            int window_height = metricsB.heightPixels;
            filterPopup = new PopupWindow(layout, 3*window_width/2, 3*window_height/2, true);
            filterPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button butSearch=(Button)layout.findViewById(R.id.butSearch);
            TextView tvTitle=(TextView)layout.findViewById(R.id.tvSearchTitle);
            CheckBox cb = (CheckBox)layout.findViewById(R.id.cbSearchOnline);
            Spinner sexSpinner = (Spinner)layout.findViewById(R.id.spinSearchSex);
            Spinner regionSpinner = (Spinner)layout.findViewById(R.id.spinSearchRegion);
            Spinner hereforSpinner=(Spinner)layout.findViewById(R.id.spinSearchHerefor);
            EditText city = (EditText)layout.findViewById(R.id.etSearchCity);
            EditText age_from = (EditText)layout.findViewById(R.id.etSearchAge1);
            EditText age_till = (EditText)layout.findViewById(R.id.etSearchAge2);
            String online = "1";
            String token = Main.str;

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
filterPopup.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeMe(){
        this.finish();
    }

    private void showFilter(){
        //initiatePopupWindow();
        this.finish();
    }
}
