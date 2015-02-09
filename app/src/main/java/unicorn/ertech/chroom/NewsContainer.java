package unicorn.ertech.chroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.webkit.WebView;

/**
 * Created by Timur on 04.01.2015.
 */
public class NewsContainer extends FragmentActivity {
    News frag1;
    NewsWeb frag2;
    FragmentTransaction fTrans;
    public String selectedURL;
    public String selectedTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);

        frag1 = new News();
        frag2 = new NewsWeb();

        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmContNews, frag1);
        fTrans.commit();

    }

    public void startWeb(String URL, String title){
        selectedURL=URL;
        selectedTitle=title;
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmContNews, frag2);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onBackPressed(){
/*        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frgmContNews);
        if(frag.getView().getId()==) {
            if (((WebView) frag.getView().findViewById(R.id.webview)).canGoBack()) {
                ((WebView) frag.getView().findViewById(R.id.webview)).goBack();
            } else {
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }
}
