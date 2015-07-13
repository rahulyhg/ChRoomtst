package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Timur on 08.01.2015.
 */
public class SetActivity extends Activity {
    SharedPreferences sPref;
    RelativeLayout topRow;
    ImageButton butBack;
    final String SAVED_COLOR = "color";
    int k=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setContentView(R.layout.tab_incognito);

        topRow = (RelativeLayout)findViewById(R.id.topRowAbout);
        butBack=(ImageButton)findViewById(R.id.setBack);
        topRow.setBackgroundResource(R.color.izum_blue);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        TextView txtclose=(TextView)findViewById(R.id.tvExit);
        Button txtPrivate=(Button)findViewById(R.id.tvPrivate);
        Button setRate=(Button)findViewById(R.id.tvRate);
        TextView txtWallet=(TextView)findViewById(R.id.tvWallet);
        Button setChat=(Button)findViewById(R.id.tvChatSet);
        Button setAbout=(Button)findViewById(R.id.tvAbout);

        LinearLayout laySetClose = (LinearLayout)findViewById(R.id.laySetExit);
        LinearLayout laySetPrivate = (LinearLayout)findViewById(R.id.laySetPrivate);
        LinearLayout laySetChatSet = (LinearLayout)findViewById(R.id.laySetChatSet);
        LinearLayout laySetAbout=(LinearLayout)findViewById(R.id.laySetAbout);
        LinearLayout laySetRate=(LinearLayout)findViewById(R.id.laySetRate);
        final LinearLayout laySetWallet = (LinearLayout)findViewById(R.id.laySetWallet);

        topRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(k<7){
                    k++;
                }else{
                    laySetWallet.setVisibility(View.VISIBLE);
                }
            }
        });


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAll();
            }
        });
        txtPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetWallet.class);
            }
        });
        txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(BillingMainTabs.class);
            }
        });
        setChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetChat.class);
            }
        });
        setAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(About.class);
            }
        });
        setRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=unicorn.ertech.chroom"));
                startActivity(intent);
            }
        });
        /*txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetWallet.class);
            }
        });*/

        laySetClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAll();
            }
        });
        laySetPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetPrivate.class);
            }
        });
        laySetChatSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetChat.class);
            }
        });
        laySetWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(SetWallet.class);
            }
        });

        laySetAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(About.class);
            }
        });

        laySetWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(BillingMainTabs.class);
            }
        });

        laySetRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=unicorn.ertech.chroom"));
                startActivity(intent);
            }
        });

    }

    public void closeMe(){
        this.finish();
    }

    private void closeAll(){
        SharedPreferences userData = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor ed2 = userData.edit();  //Сохраняем токен
        ed2.remove("token");
        ed2.commit();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("finish", false);
        startActivity(intent);
        this.finish();
    }

    private void showActivity(Class activ){
        Intent i = new Intent(this, activ);
        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        topRow.setBackgroundResource(R.color.izum_blue);
        butBack.setBackgroundResource(R.color.izum_blue);
    }
}
