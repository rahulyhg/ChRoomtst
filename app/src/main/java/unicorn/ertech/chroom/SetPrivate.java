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
public class SetPrivate extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private);
        //setContentView(R.layout.tab_incognito);

        Button butBack=(Button)findViewById(R.id.setBack);
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
}
