package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetChat extends Activity {
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    final String SAVED_CITY = "city";
    RelativeLayout topRow;
    Button butBack;
    Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_chat);
        topRow=(RelativeLayout)findViewById(R.id.topRow_sch);
        butBack=(Button)findViewById(R.id.setBack);
        setColor();
        spinner=(Spinner)findViewById(R.id.spinnerChatCities);
        final SharedPreferences sPref2 = getSharedPreferences("saved_chats", MODE_PRIVATE);
        if(sPref2.contains(SAVED_CITY)){
            spinner.setSelection(sPref2.getInt(SAVED_CITY, 11)-11);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {

                SharedPreferences.Editor ed = sPref2.edit();
                ed.putInt(SAVED_CITY, 11+selectedItemPosition);
                ed.commit();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

    }


    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        int col=sPref.getInt(SAVED_COLOR, 0);
            switch (col) {
                case 0:
                    topRow.setBackgroundResource(R.color.green);
                    butBack.setBackgroundResource(R.color.green);
                    break;
                case 1:
                    topRow.setBackgroundResource(R.color.blue);
                    butBack.setBackgroundResource(R.color.blue);
                    break;
                case 2:
                    topRow.setBackgroundResource(R.color.orange);
                    butBack.setBackgroundResource(R.color.orange);
                    break;
                case 3:
                    topRow.setBackgroundResource(R.color.purple);
                    butBack.setBackgroundResource(R.color.purple);
                    break;
                default:
                    break;
            }
    };

    public void closeMe(){
        this.finish();
    }
}
