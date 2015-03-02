package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetCol extends Activity {
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    RelativeLayout topRow;
    Button butGreen;
    Button butBlue;
    Button butOrange;
    Button butPurple;
    ImageButton butBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_color);
        topRow=(RelativeLayout)findViewById(R.id.topRow_sc);
        butBack=(ImageButton)findViewById(R.id.setBack);
        butGreen = (Button)findViewById(R.id.setGreen);
        butBlue = (Button)findViewById(R.id.setBlue);
        butOrange = (Button)findViewById(R.id.setOrange);
        butPurple = (Button)findViewById(R.id.setPurple);

        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        butBlue.setOnClickListener(setColor);
        butOrange.setOnClickListener(setColor);
        butPurple.setOnClickListener(setColor);
        butGreen.setOnClickListener(setColor);


    }


    private View.OnClickListener setColor = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.setGreen:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putInt(SAVED_COLOR, 0);
                    ed.commit();
                    topRow.setBackgroundResource(R.color.green);
                    break;
                case R.id.setBlue:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed1 = sPref.edit();
                    ed1.putInt(SAVED_COLOR, 1);
                    ed1.commit();
                    topRow.setBackgroundResource(R.color.blue);
                    break;
                case R.id.setOrange:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed2 = sPref.edit();
                    ed2.putInt(SAVED_COLOR, 2);
                    ed2.commit();
                    topRow.setBackgroundResource(R.color.orange);
                    break;
                case R.id.setPurple:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed3 = sPref.edit();
                    ed3.putInt(SAVED_COLOR, 3);
                    topRow.setBackgroundResource(R.color.purple);
                    ed3.commit();
                    break;
                default:
                    break;
            }
        }
    };

    public void closeMe(){
        this.finish();
    }
}
