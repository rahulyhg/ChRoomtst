package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetPrivate extends Activity {
    TextView blackList;
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    RelativeLayout topRow;
    AlertDialog.Builder ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_private);
        //setContentView(R.layout.tab_incognito);

        ImageButton butBack=(ImageButton)findViewById(R.id.setBack);
        blackList=(TextView)findViewById(R.id.tvBlackList);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        blackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackOpen();
            }
        });
        topRow=(RelativeLayout)findViewById(R.id.topRow_sp);

        ad = new AlertDialog.Builder(this);
        ad.setTitle("Удаление профиля");  // заголовок
        ad.setMessage("Вы уверены, что хотите удалить профиль?"); // сообщение
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    public void closeMe(){
        this.finish();
    }
    public void blackOpen(){
        Intent i = new Intent(this, SetBlackList.class);
        startActivity(i);
    }

    private void setColor(){
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
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

    public void myClick(View v)
    {
        blackOpen();
    }

}
