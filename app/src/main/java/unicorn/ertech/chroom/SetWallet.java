package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetWallet extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private);
        //setContentView(R.layout.tab_incognito);

        Button butBack=(Button)findViewById(R.id.setBack);
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

    public  void deleteOpen()
    {
        Intent i = new Intent(this, deleteAcc.class);
        startActivity(i);
    }
}
