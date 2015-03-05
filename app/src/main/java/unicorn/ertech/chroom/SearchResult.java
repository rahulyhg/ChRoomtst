package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
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
    int scrolling = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        GridView gridview = (GridView) findViewById(R.id.gridView);

        gridview.setNumColumns(3);


        Intent k = getIntent();
        String result = k.getStringExtra("results");
        String num = k.getStringExtra("total");

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
                results.add(0,new sResult(messag.getString("id"),messag.getString("nickname"),messag.getString("avatar")));
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

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0 && scrolling > firstVisibleItem) {

                    Toast.makeText(getApplicationContext(), "Внизу!", Toast.LENGTH_SHORT).show();

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
}
