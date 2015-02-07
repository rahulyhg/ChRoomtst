package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Timur on 28.01.2015.
 */
public class Reg1 extends Activity {
    EditText phonenumber;
    EditText etHuman;
    public static String number;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_1);

        phonenumber=(EditText)findViewById(R.id.editText);
        Button next=(Button)findViewById(R.id.button4);
        etHuman=(EditText)findViewById(R.id.etHuman);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.parseDouble(etHuman.getText().toString())==2){
                number=phonenumber.getText().toString();
                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
                }
            }
        });
    }
}
