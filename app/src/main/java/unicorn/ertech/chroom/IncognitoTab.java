package unicorn.ertech.chroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_incognito);

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


}
