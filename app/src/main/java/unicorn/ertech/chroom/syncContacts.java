package unicorn.ertech.chroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class syncContacts extends Activity {

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> phone = new ArrayList<String>();
    ArrayList<String> phoneS = new ArrayList<String>();


    ArrayList<String> newPhone = new ArrayList<String>();

    ProgressDialog progressDialog;

    String data = "";
    String status = "";
    String tmp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_contacts_layout);
        Button butBack=(Button)findViewById(R.id.setBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        name = new ArrayList<String>(cursor.getCount());
        phone = new ArrayList<String>(cursor.getCount());
        phoneS = new ArrayList<String>(cursor.getCount());

        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){

            while (cursor.moveToNext()) {
                String nameB =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumberB = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                name.add(nameB.toString());
                phone.add(phoneNumberB.toString());

            }
        }



	/*	if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

			String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);

			Toast toast2 = Toast.makeText(getApplicationContext(),
					searchQuery, Toast.LENGTH_SHORT);
					toast2.show();

        }*/



        progressDialog = ProgressDialog.show(this,
                "Соединение", "Пожалуйста, подождите", true);


        new send().execute();
    }

    public void closeMe(){
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sync_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void set()
    {
        ListView lvMain = (ListView) findViewById(R.id.listView1);


        MyThumbnaildapter	thadapter = new MyThumbnaildapter(
                this, R.layout.list_dtls,
                name, phone, phoneS);


        lvMain.setAdapter(thadapter);

    }

    public class MyThumbnaildapter extends ArrayAdapter<String> {

        List<String> name;
        List<String> phone;
        List<String> phoneS;


        public MyThumbnaildapter(Context ct, int textViewResourceId,
                                 List<String> name, List<String> phone, List<String> phoneS) {
            super(ct, textViewResourceId, name);
            this.name = name;
            this.phone = phone;
            this.phoneS = phoneS;

        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View view = null;

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);


            view = inflater.inflate(R.layout.list_dtls, parent, false);


            TextView Tphone = (TextView) view.findViewById(R.id.phone);
            TextView check = (TextView) view.findViewById(R.id.check);
            TextView Tname = (TextView) view.findViewById(R.id.name);


            Tname.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (phoneS.get(position).equals("1"))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Уже добавлен " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Отправить sms номеру " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }


                }
            });

            check.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (phoneS.get(position).equals("1"))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Уже добавлен " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Отправить sms номеру " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }


                }
            });

            Tphone.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (phoneS.get(position).equals("1"))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Уже добавлен " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Отправить sms номеру " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });



            Tphone.setText("" + phone.get(position));
            Tname.setText("" + name.get(position));
            check.setText(phoneS.get(position));

            if (phoneS.get(position).equals("1"))
            {
                check.setText("УЖЕ ДОБАВЛЕН");
            }
            if (phoneS.get(position).equals("0"))
            {
                check.setText("+");
            }

            return view;
        }
    }


    public class send extends AsyncTask<Void, Void, String> {

        JSONObject jObject;



        @Override
        protected String doInBackground(Void... params) {



            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://im.topufa.org/index.php");


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("action", "phones_sync"));


                String ph = "";

                for (int i=0; i<phone.size();i++)
                {
                    if (i == phone.size())
                    {
                        ph = ph + phone.get(i);
                    }
                    else
                    {
                        ph = ph +phone.get(i) + ", ";
                    }

                }


                tmp = ph;

                nameValuePairs.add(new BasicNameValuePair("phones", ph));
                nameValuePairs.add(new BasicNameValuePair("token", "b5e0d955a1b1ce08cd87cb6c5a231310bae70b80"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                data = EntityUtils.toString(entity);
                if (entity != null) {
                    try {
                        jObject = new JSONObject(data);


                        if (jObject.getString("error") != null)
                        {


                            String check = jObject.getString("error");

                            if (check.equals("false"))
                            {
                                if (jObject.getString("total") != null)
                                {
                                    status = "Количество - " + jObject.getString("total");

                                    JSONArray jArrayNews;
                                    try {
                                        jArrayNews = jObject.getJSONArray("phones");

                                        int arrayNewsCount = jArrayNews.length();
                                        for (int i = 0; i < arrayNewsCount; i++) {
                                            try {

                                                getNews((JSONObject) jArrayNews.get(i));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }






                                    for (int i=0; i<phone.size();i++)
                                    {

                                        for (int ii=0; ii<newPhone.size();ii++)
                                        {

                                            System.out.println(phone.get(i) + "   " + newPhone.get(ii).toString());

                                            if (phone.get(i).toString().equals(newPhone.get(ii).toString()))

                                            {
                                                phoneS.add("1");
                                            }
                                            else
                                            {
                                                phoneS.add("0");
                                            }
                                        }
                                    }





                                }

                            }
                            if (check.equals("true"))
                            {
                                if (jObject.getString("error_code") != null)
                                {
                                    String error_code = jObject.getString("error_code");
                                    status = "Ошибка - " + error_code;
                                }
                            }

                        }

                    } catch (Exception e) {
                        data = "error";
                    }
                }


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }




            return null;
        }




        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            set();
        }
    }


    @SuppressWarnings("unused")
    private void getNews(JSONObject jNews) throws JSONException {

        String Pid = "";
        String Pname = "";
        String Pphone = "";
        Pid    =	jNews.getString("id");
        Pname  = jNews.getString("name");
        Pphone = jNews.getString("phone");

        newPhone.add(Pphone);
    }
}
