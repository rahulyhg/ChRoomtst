package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Timur on 11.01.2015.
 */
public class Support extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        //setContentView(R.layout.tab_incognito);

        Button butBack=(Button)findViewById(R.id.setBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        stopService(new Intent(this, notif.class));
    }

    @Override
    public void onPause(){
        Intent i = new Intent(this, notif.class);
        startService(i);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        stopService(new Intent(this, notif.class));
    }

    public void closeMe(){
        this.finish();
    }
}
