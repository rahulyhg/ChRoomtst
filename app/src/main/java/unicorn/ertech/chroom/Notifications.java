package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class Notifications extends Activity {

    CheckBox notifications;
    CheckBox sound;
    CheckBox vibro;
    CheckBox indicator;

    String notif, Sound, Vibro, Indicator;

    final String SAVED_NOTIF="notif";
    final String SAVED_SOUND="sound";
    final String SAVED_VIBRO="vibro";
    final String SAVED_INDICATOR="indicator";

    SharedPreferences Notif;
    SharedPreferences.Editor ed2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Button butBack=(Button)findViewById(R.id.setBack);
        notifications = (CheckBox)findViewById(R.id.checkBox1);
        sound = (CheckBox)findViewById(R.id.checkBox2);
        vibro = (CheckBox)findViewById(R.id.checkBox3);
        indicator = (CheckBox)findViewById(R.id.checkBox4);

        Notif = getSharedPreferences("notifications",MODE_PRIVATE);
        ed2 = Notif.edit();

        if(Notif.contains(SAVED_SOUND))
        {
            Sound = Notif.getString(SAVED_SOUND,"");
            if(Sound.equals("true"))
            {
                sound.setChecked(true);
            }
            else
            {
                sound.setChecked(false);
            }
        }

        if(Notif.contains(SAVED_VIBRO))
        {
            Vibro = Notif.getString(SAVED_VIBRO,"");
            if(Vibro.equals("true"))
            {
                vibro.setChecked(true);
            }
            else
            {
                vibro.setChecked(false);
            }
        }

        if(Notif.contains(SAVED_INDICATOR))
        {
            Indicator = Notif.getString(SAVED_INDICATOR,"");
            if(Indicator.equals("true"))
            {
                indicator.setChecked(true);
            }
            else
            {
                indicator.setChecked(false);
            }
        }


        if(Notif.contains(SAVED_NOTIF))
        {
            notif = Notif.getString(SAVED_NOTIF,"");
            if(notif.equals("true"))
            {
                notifications.setChecked(true);
            }
            else
            {
                notifications.setChecked(false);
                sound.setVisibility(View.GONE);
                vibro.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
            }
        }
        else
        {
            notifications.setChecked(true);
            sound.setChecked(true);
            vibro.setChecked(true);
            indicator.setChecked(true);
        }



        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notifications.isChecked())
                {
                    sound.setEnabled(true); sound.setVisibility(View.VISIBLE); sound.setChecked(true);
                    vibro.setEnabled(true); vibro.setVisibility(View.VISIBLE);
                    indicator.setEnabled(true); indicator.setVisibility(View.VISIBLE);
                    ed2.putString(SAVED_NOTIF,"true");
                    ed2.putString(SAVED_SOUND,"true");
                    ed2.commit();

                    Intent srvs = new Intent(getApplicationContext(), notif.class);
                    startService(srvs);
                }
                else
                {
                    Intent srvs = new Intent(getApplicationContext(), notif.class);
                    stopService(srvs);
                    sound.setEnabled(false); sound.setChecked(false); sound.setVisibility(View.GONE);
                    vibro.setEnabled(false); vibro.setChecked(false); vibro.setVisibility(View.GONE);
                    indicator.setEnabled(false); indicator.setChecked(false); indicator.setVisibility(View.GONE);
                    ed2.putString(SAVED_NOTIF,"false");
                    ed2.putString(SAVED_SOUND,"false");
                    ed2.putString(SAVED_VIBRO,"false");
                    ed2.putString(SAVED_INDICATOR,"false");
                    ed2.commit();
                }
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sound.isChecked())
                {
                    ed2.putString(SAVED_SOUND,"true");
                    ed2.commit();
                }
                else
                {
                    ed2.putString(SAVED_SOUND,"false");
                    ed2.commit();
                }
            }
        });

        vibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vibro.isChecked())
                {
                    ed2.putString(SAVED_VIBRO,"true");
                    ed2.commit();
                }
                else
                {
                    ed2.putString(SAVED_VIBRO,"false");
                    ed2.commit();
                }
            }
        });

        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indicator.isChecked())
                {
                    ed2.putString(SAVED_INDICATOR,"true");
                    ed2.commit();
                }
                else
                {
                    ed2.putString(SAVED_INDICATOR,"false");
                    ed2.commit();
                }
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void closeMe(){
        this.finish();
    }
}
