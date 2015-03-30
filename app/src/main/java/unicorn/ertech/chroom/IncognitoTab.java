package unicorn.ertech.chroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 11.01.2015.
 */
public class IncognitoTab extends FragmentActivity{
    Incognito frag1;
    IncognitoChat frag2;
    IncognitoRnd frag3;
    FragmentTransaction fTrans;
    TextView incognitoTitle;
    final String SAVED_COLOR = "color";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_incognito);

        incognitoTitle=(TextView)findViewById(R.id.tvIncognitoTitle);

        frag1 = new Incognito();
        frag2 = new IncognitoChat();
        frag3 = new IncognitoRnd();

        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont2, frag1);
        fTrans.commit();
    }

    public void startChat(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont2, frag2);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void startRandom(){
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont2, frag3);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                incognitoTitle.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                incognitoTitle.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                incognitoTitle.setBackgroundResource(R.color.orange);
            } else if (col == 3) {
                incognitoTitle.setBackgroundResource(R.color.purple);
            }
        } else {
            incognitoTitle.setBackgroundResource(R.color.green);
        }
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frgmCont2, frag1);
        fTrans.commit();
    }
}
