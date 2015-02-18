package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetBlackList extends Activity {
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    RelativeLayout topRow;
    Button butBack;
    ListView lvBlackList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_blacklist);
        topRow=(RelativeLayout)findViewById(R.id.topRow_sch);
        butBack=(Button)findViewById(R.id.setBack);
        lvBlackList=(ListView)findViewById(R.id.lvBlackList);
        setColor();

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
