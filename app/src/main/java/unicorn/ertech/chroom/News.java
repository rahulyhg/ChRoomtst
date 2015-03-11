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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class News extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private Context context;
    List<newsItem> news_list = new ArrayList<newsItem>();
    newsAdapter adapter3;
    TextView tvNewsTitle;
    final String SAVED_COLOR = "color";
    ListView lvNews;
    SwipeRefreshLayout swipeLayout;

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
        final View view = inflater.inflate(R.layout.tab_news, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        lvNews = (ListView) view.findViewById(R.id.lvNews);
        tvNewsTitle = (TextView) view.findViewById(R.id.tvNewsTitle);
        adapter3 = new newsAdapter(news_list, context);

        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvNewsTitle.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tvNewsTitle.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                tvNewsTitle.setBackgroundResource(R.color.orange);
            } else if (col == 3) {
                tvNewsTitle.setBackgroundResource(R.color.purple);
            }
        } else {
            tvNewsTitle.setBackgroundResource(R.color.green);
        }

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.green, R.color.blue, R.color.orange, R.color.purple);
        swipeLayout.setDistanceToTriggerSync(20);

        lvNews.setAdapter(adapter3);
        new GetRssFeed().execute("http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/mainnews.rss");
        //new GetRssFeed().execute("http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/news.rss");

        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String URL = adapter3.getItem(position).URL;
                String title = adapter3.getItem(position).title;
                NewsContainer parentActivity = (NewsContainer)getActivity();
                parentActivity.startWeb(URL, title);
                //Intent browseIntent = new Intent(context, NewsWeb.class);
                //browseIntent.putExtra("URL", URL);
                //browseIntent.putExtra("Title", title);
                //Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                //startActivity(browseIntent);
            }
        });

        lvNews.refreshDrawableState();
        return view;
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        new GetRssFeed().execute("http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/mainnews.rss");
        //new GetRssFeed().execute("http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/news.rss");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                //parser.parseAsync();
            }
        }, 4000);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("news", adapter3.toString());
        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvNewsTitle.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tvNewsTitle.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                tvNewsTitle.setBackgroundResource(R.color.orange);
            } else if (col == 3) {
                tvNewsTitle.setBackgroundResource(R.color.purple);
            }
        }else{
            tvNewsTitle.setBackgroundResource(R.color.green);
        }
        lvNews.refreshDrawableState();
    }

    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute(){
            news_list.clear();
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                RssItem item;
                int rssSize = rssReader.getItems().size();
                for (int j=0; j<rssSize-1; j++){
                    item=rssReader.getItems().get(j);
                    newsItem currentItem = new newsItem(item.getTitle(), item.getDescription(), item.getLink(), item.getImageUrl());
                    news_list.add(currentItem);
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter3.notifyDataSetChanged();
            lvNews.refreshDrawableState();
        }
    }

    protected boolean checkRepeat(String URL){
        boolean flag=false;
        for(int i=0; i<news_list.size()-1; i++){
            if(news_list.get(i).getURL().equals(URL)){
                flag=true;
                break;
            }
        }
        if(flag==false){
            return true;
        }else{
            return false;
        }
    }
}
