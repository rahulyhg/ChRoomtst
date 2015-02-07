package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shirwa.simplistik_rss.RssItem;
import com.shirwa.simplistik_rss.RssReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.theengine.android.simple_rss2_android.RSSItem;
import at.theengine.android.simple_rss2_android.SimpleRss2Parser;
import at.theengine.android.simple_rss2_android.SimpleRss2ParserCallback;

/**
 * Created by Timur on 04.01.2015.
 */
public class News extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<HashMap<String, Object>> newsList;
    List<newsItem> news_list = new ArrayList<newsItem>();
    newsAdapter adapter3;
    TextView tvNewsTitle;
    SimpleRss2Parser parser;
    private static final String TITLE = "message_author"; // Верхний текст
    private static final String DESCRIPTION = "message_body"; // ниже главного
    private static final String ICON = "avatar";  // будущая картинка
    final String SAVED_COLOR = "color";
    SimpleAdapter adapter;
    ArrayAdapter adapter2;
    ListView lvNews;
    SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_news);

        lvNews=(ListView)findViewById(R.id.lvNews);
        newsList = new ArrayList<HashMap<String, Object>>();
        adapter2 = new ArrayAdapter<String>(this, R.layout.list_item2);
        adapter = new SimpleAdapter(getBaseContext(), newsList,  R.layout.list_item2, new String[] { TITLE, DESCRIPTION, ICON},  new int[] { R.id.textTitle, R.id.textLink, R.id.img });
        tvNewsTitle = (TextView) findViewById(R.id.tvNewsTitle);
        adapter3 = new newsAdapter(news_list, this);

        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvNewsTitle.setBackgroundResource(R.drawable.b_string);
            } else if (col == 0) {
                tvNewsTitle.setBackgroundResource(R.drawable.g_strip);
            } else if (col == 2) {
                tvNewsTitle.setBackgroundResource(R.drawable.o_strip);
            } else if (col == 3) {
                tvNewsTitle.setBackgroundResource(R.drawable.p_string);
            }
        }else{
            tvNewsTitle.setBackgroundResource(R.drawable.g_strip);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.setDistanceToTriggerSync(20);

        lvNews.setAdapter(adapter3);
        new GetRssFeed().execute("http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/mainnews.rss");

        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String URL =  adapter3.getItem(position).URL;
                String title = adapter3.getItem(position).title;
                Intent browseIntent = new Intent(getApplicationContext(), NewsWeb.class);
                browseIntent.putExtra("URL", URL);
                browseIntent.putExtra("Title", title);
                //Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browseIntent);
            }
        });

//        parser = new SimpleRss2Parser("https://news.google.com/news?pz=1&cf=all&ned=ru_ru&hl=ru&output=rss",
//                new SimpleRss2ParserCallback() {
//                    @Override
//                    public void onFeedParsed(List<RSSItem> items) {
//                        for(int i = 0; i < items.size(); i++){
//                            HashMap<String, Object> hm;
//                            hm = new HashMap<String, Object>();
//                            hm.put(TITLE, items.get(i).getTitle()); // Название
//                            hm.put(DESCRIPTION, items.get(i).getLink().toString()); // Описание
//                            hm.put(ICON,  R.drawable.ic_launcher); // Картинка
//                            newsList.add(0,hm);
//                            //items.get(i).
//                            Log.d("SimpleRss2ParserDemo", items.get(i).getTitle());
//                            Log.d("SimpleRss2ParserDemo", items.get(i).getDescription());
//                            Log.d("SimpleRss2ParserDemo", items.get(i).getLink().toString());
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                    @Override
//                    public void onError(Exception ex) {
//                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//        parser.parseAsync();

    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                new GetRssFeed().execute("http://feeds.bbci.co.uk/news/rss.xml");
                //parser.parseAsync();
            }
        }, 4000);
    }
    private static long back_pressed;

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                News.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvNewsTitle.setBackgroundResource(R.drawable.b_string);
            } else if (col == 0) {
                tvNewsTitle.setBackgroundResource(R.drawable.g_strip);
            } else if (col == 2) {
                tvNewsTitle.setBackgroundResource(R.drawable.o_strip);
            } else if (col == 3) {
                tvNewsTitle.setBackgroundResource(R.drawable.p_string);
            }
        }else{
            tvNewsTitle.setBackgroundResource(R.drawable.g_strip);
        }
    }

    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                int it=0;
                for (RssItem item : rssReader.getItems()){
                    //adapter2.add(item.getTitle());
                    news_list.add(it, new newsItem(item.getTitle(), item.getDescription(), item.getLink(), item.getImageUrl()));
                    it++;
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //adapter2.notifyDataSetChanged();
            adapter3.notifyDataSetChanged();
        }
    }
}
