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
}
