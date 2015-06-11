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
        topRow.setBackgroundResource(R.color.izum_blue);
    };

    @Override
    public void onResume(){
        super.onResume();
        setColor();
    }
}
