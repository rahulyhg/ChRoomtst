package unicorn.ertech.chroom;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Timur on 06.02.2015.
 */
public class NewsWeb extends Fragment {
    private WebView mWebView;

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
        final View view = inflater.inflate(R.layout.news_web, container, false);

        NewsContainer parentActivity = (NewsContainer)getActivity();
        mWebView = (WebView) view.findViewById(R.id.webview);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebView.loadUrl(parentActivity.selectedURL);

        TextView newsTitle = (TextView)view.findViewById(R.id.newsTitle);
        newsTitle.setText(parentActivity.selectedTitle);
        mWebView.setWebViewClient(new HelloWebViewClient());

        Button backButton=(Button)view.findViewById(R.id.butNewsBack);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                NewsContainer parentActivity = (NewsContainer)getActivity();
                parentActivity.startList();
            }
        });

        return view;
    }

    private class HelloWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }


    public void closeMe(){
        //this.finalize();
    }
}
