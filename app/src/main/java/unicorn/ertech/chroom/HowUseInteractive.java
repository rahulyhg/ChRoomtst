package unicorn.ertech.chroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Azat on 28.04.15.
 */
public class HowUseInteractive extends ActionBarActivity {
    Button btnNext;
    int k = 0;
    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_use);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setText("Далее");

        token= getIntent().getStringExtra("Token");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vpOut);
        viewPager.setAdapter(new HowUseAdapter(getSupportFragmentManager()));
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k++;
                if (k == 4){
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    i.putExtra("Token", token);
                    startActivity(i);
                }
                if (k == 3){
                    btnNext.setText("Готово");
                }
                viewPager.setCurrentItem(k, true);
            }
        });
    }
}
