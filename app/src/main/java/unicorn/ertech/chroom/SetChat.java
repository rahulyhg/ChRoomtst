package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    int currentRegion = 8, currentCities=R.array.cities;
    MyCustomAdapter adapter;
    int savedCity=0, incr=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_chat);
        topRow=(RelativeLayout)findViewById(R.id.topRow_sch);
        butBack=(ImageButton)findViewById(R.id.setBack);
        setColor();
        spinner=(Spinner)findViewById(R.id.spinnerChatCities);
        spinnerReg=(Spinner)findViewById(R.id.spinnerChatRegions);


        final MyCustomAdapter2 adapter2 = new MyCustomAdapter2(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(R.array.regions));
        spinnerReg.setAdapter(adapter2);

        final SharedPreferences sPref2 = getSharedPreferences("saved_chats", MODE_PRIVATE);

        if(sPref2.contains("region")){
            currentRegion=sPref2.getInt("region", 0);
            currentCities=GeoConvertIds.getCityArrayId(currentRegion);
            GeoConvertIds.region=currentRegion;
            spinnerReg.setSelection(currentRegion);
        }

        adapter = new MyCustomAdapter(this,
                R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
        spinner.setAdapter(adapter);

        if(sPref2.contains(SAVED_CITY)){
            savedCity=sPref2.getInt(SAVED_CITY, 0);
            spinner.setSelection(savedCity);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {

                SharedPreferences.Editor ed = sPref2.edit();
                ed.putInt("citySrv", GeoConvertIds.getServerCityId(selectedItemPosition));
                ed.putInt(SAVED_CITY, selectedItemPosition);
                ed.putString("cityStr", adapter.getItem(selectedItemPosition).toString());
                ed.commit();
                if(incr<2) {
                    spinner.setSelection(savedCity);
                    incr++;
                }else{
                    //incr++;
                }
                Log.d("selectedspinner", Integer.toString(selectedItemPosition));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerReg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                Log.d("selected", Integer.toString(incr));
                currentRegion=selectedItemPosition;
                currentCities=GeoConvertIds.getCityArrayId(currentRegion);
                //adapter.notifyDataSetChanged();
                //adapter.clear();
                adapter=null;
                adapter = new MyCustomAdapter(getApplicationContext(),
                        R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
                adapter.notifyDataSetChanged();
                spinner.setAdapter(adapter);
                //spinner.setSelection(savedCity);
                /*if(incr>2) {
                    spinner.setSelection(savedCity);
                }else{
                    incr++;
                }*/
                SharedPreferences.Editor ed = sPref2.edit();
                ed.putInt("regSrv", GeoConvertIds.getServerRegionId(selectedItemPosition));
                ed.putInt("region", selectedItemPosition);
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
        topRow.setBackgroundResource(R.color.izum_blue);
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
            label.setText(getResources().getStringArray(currentCities)[position]);
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
            label.setText(getResources().getStringArray(currentCities)[position]);
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
