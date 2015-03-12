package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetWallet extends Activity {
    final String SAVED_COLOR = "color";
    SharedPreferences sPref;
    RelativeLayout topRow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private);
        //setContentView(R.layout.tab_incognito);

        ImageButton butBack=(ImageButton)findViewById(R.id.setBack);
        TextView blackList=(TextView)findViewById(R.id.tvBlackList);
        TextView notif=(TextView)findViewById(R.id.tvNotifications);
        TextView anonNick = (TextView)findViewById(R.id.tvPrivateNick);
        TextView delete = (TextView)findViewById(R.id.tvDeleteAcc);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOpen();
            }
        });
        anonNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonNickOpen();
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        blackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackOpen();
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifOpen();
            }
        });
        topRow=(RelativeLayout)findViewById(R.id.topRow_sp);

        setColor();
    }


    public void closeMe(){
        this.finish();
    }
    public void blackOpen(){
        Intent i = new Intent(this, SetBlackList.class);
        startActivity(i);
    }

    public void notifOpen(){
        Intent i = new Intent(this, Notifications.class);
        startActivity(i);
    }

    public void anonNickOpen(){
        Intent i = new Intent(this, anon_nickname.class);
        startActivity(i);
    }

    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        int col=sPref.getInt(SAVED_COLOR, 0);
        switch (col) {
            case 0:
                topRow.setBackgroundResource(R.color.green);
                break;
            case 1:
                topRow.setBackgroundResource(R.color.blue);
                break;
            case 2:
                topRow.setBackgroundResource(R.color.orange);
                break;
            case 3:
                topRow.setBackgroundResource(R.color.purple);
                break;
            default:
                break;
        }
    };

    public  void deleteOpen()
    {
        Intent i = new Intent(this, deleteAcc.class);
        startActivity(i);
    }
}
