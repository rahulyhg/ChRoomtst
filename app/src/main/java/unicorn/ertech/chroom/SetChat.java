package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Timur on 11.01.2015.
 */
public class SetChat extends Activity {
    SharedPreferences sPref;
    final String SAVED_COLOR = "color";
    final String SAVED_CITY = "city";
    RelativeLayout topRow;
    ImageButton butBack;
    Spinner spinner, spinnerReg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_chat);
        topRow=(RelativeLayout)findViewById(R.id.topRow_sch);
        butBack=(ImageButton)findViewById(R.id.setBack);
        setColor();
        spinner=(Spinner)findViewById(R.id.spinnerChatCities);
        spinnerReg=(Spinner)findViewById(R.id.spinnerChatRegions);

        MyCustomAdapter adapter = new MyCustomAdapter(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(R.array.cities));
        spinner.setAdapter(adapter);
        MyCustomAdapter2 adapter2 = new MyCustomAdapter2(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(R.array.regions));
        spinnerReg.setAdapter(adapter2);


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

    public void closeMe(){
        this.finish();
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.cities)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_with_arrows, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.cities)[position]);
            return row;
        }
    }

    public class MyCustomAdapter2 extends ArrayAdapter<String> {

        public MyCustomAdapter2(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_with_arrows, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView34);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }
    }
}
