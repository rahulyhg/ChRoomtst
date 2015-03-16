package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    int col;
    Animation pulse;

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

        pulse= AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        setColor();
    }

    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        col=sPref.getInt(SAVED_COLOR, 0);
        switch (col) {
            case 0:
                topRow.setBackgroundResource(R.color.green);
                butGreen.startAnimation(pulse);
                break;
            case 1:
                topRow.setBackgroundResource(R.color.blue);
                butBlue.startAnimation(pulse);
                break;
            case 2:
                topRow.setBackgroundResource(R.color.orange);
                butOrange.startAnimation(pulse);
                break;
            case 3:
                topRow.setBackgroundResource(R.color.purple);
                butPurple.startAnimation(pulse);
                break;
            default:
                break;
        }
    };

    private View.OnClickListener setColor = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (col) {
                case 0:
                    butGreen.clearAnimation();
                    break;
                case 1:
                    butBlue.clearAnimation();
                    break;
                case 2:
                    butOrange.clearAnimation();
                    break;
                case 3:
                    butPurple.clearAnimation();
                    break;
                default:
                    break;
            }
            pulse.cancel();
            switch (v.getId()) {
                case R.id.setGreen:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putInt(SAVED_COLOR, 0);
                    col=0;
                    ed.commit();
                    topRow.setBackgroundResource(R.color.green);
                    butGreen.startAnimation(pulse);
                    break;
                case R.id.setBlue:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed1 = sPref.edit();
                    ed1.putInt(SAVED_COLOR, 1);
                    col=1;
                    ed1.commit();
                    topRow.setBackgroundResource(R.color.blue);
                    butBlue.startAnimation(pulse);
                    break;
                case R.id.setOrange:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed2 = sPref.edit();
                    ed2.putInt(SAVED_COLOR, 2);
                    col=2;
                    ed2.commit();
                    topRow.setBackgroundResource(R.color.orange);
                    butOrange.startAnimation(pulse);
                    break;
                case R.id.setPurple:
                    sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
                    SharedPreferences.Editor ed3 = sPref.edit();
                    col=3;
                    ed3.putInt(SAVED_COLOR, 3);
                    topRow.setBackgroundResource(R.color.purple);
                    butPurple.startAnimation(pulse);
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
