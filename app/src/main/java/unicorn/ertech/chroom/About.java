package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Timur on 08.04.2015.
 */
public class About extends Activity {
    RelativeLayout topRow;
    final String SAVED_COLOR = "color";
    ImageButton butBack;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        topRow=(RelativeLayout)findViewById(R.id.topRowAbout);
        butBack=(ImageButton)findViewById(R.id.butBackAbout);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
    }
    public void closeMe(){
        this.finish();
    }
    private void setColor(){
        SharedPreferences sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
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

    @Override
    public void onResume(){
        super.onResume();
        setColor();
    }
}
