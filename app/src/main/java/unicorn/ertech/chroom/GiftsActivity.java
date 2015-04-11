package unicorn.ertech.chroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Timur on 11.01.2015.
 */
public class GiftsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gifts);

        Button butBack=(Button)findViewById(R.id.profileBack2);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        //ImageView gifts = (ImageView)findViewById(R.id.ivGifts);
        /*gifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });*/
    }

    public void closeMe(){
        this.finish();
    }
}
