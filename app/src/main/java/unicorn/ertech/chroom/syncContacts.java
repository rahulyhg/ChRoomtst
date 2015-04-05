package unicorn.ertech.chroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageView;
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
    ArrayList<String> id = new ArrayList<String>();
    final String SAVED_COLOR = "color";
    TextView tvTitle;

    ArrayList<String> newPhone = new ArrayList<String>();

    ProgressDialog progressDialog;

    String data = "";
    String status = "";
    String tmp = "";
    String token;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_contacts_layout);

        tvTitle=(TextView)findViewById(R.id.tvSyncTitle);
        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }

        cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        name = new ArrayList<String>(cursor.getCount());
        phone = new ArrayList<String>(cursor.getCount());
        phoneS = new ArrayList<String>(cursor.getCount());
        id =  new ArrayList<String>(cursor.getCount());


        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){

            while (cursor.moveToNext()) {
                String nameB =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumberB = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String idB = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                id.add(idB.toString());
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


  /*       Intent m = new Intent(this, VActivity.class);
    	 startActivity(m);   */
        progressDialog = ProgressDialog.show(this,
                "Соединение", "Пожалуйста, подождите", true);


        try
        {

            new send().execute();

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка на сервере", Toast.LENGTH_SHORT);
            toast.show();
        }


    }









    void set()
    {


        @SuppressWarnings("unused")
        ListView lvMain = (ListView) findViewById(R.id.listView1);


        ArrayList<String> Nname = new ArrayList<String>();
        ArrayList<String> Nphone = new ArrayList<String>();
        ArrayList<String> NphoneS = new ArrayList<String>();



/*
			  Toast toast = Toast.makeText(getApplicationContext(),
					  phoneS.get(phoneS.size())+"", Toast.LENGTH_SHORT);
		   	              toast.show();

	       */



        if (phoneS.size() > 0)
        {

            for (int i=0; i<phoneS.size()-1;i++)
            {

                if (phoneS.get(i).toString().equals("0"))
                {
                    Nname.add(name.get(i));
                    Nphone.add(phone.get(i));
                    NphoneS.add(phoneS.get(i));
                }

            }


            if(Nname.size() != 0 )
            {
                MyThumbnaildapter	thadapter = new MyThumbnaildapter(
                        syncContacts.this, R.layout.list_dtls,
                        Nname, Nphone, NphoneS);


                lvMain.setAdapter(thadapter);

            }
            else
            {
                Toast toast111 = Toast.makeText(getApplicationContext(),
                        "Ничего не найдено", Toast.LENGTH_SHORT);
                toast111.show();
            }

        }
        else
        {
            Toast toast111 = Toast.makeText(getApplicationContext(),
                    "В телефонной книге нет номеров", Toast.LENGTH_SHORT);
            toast111.show();
        }




    }


    public void open(View v)
    {
        Intent m = new Intent(this, VActivity.class);
        startActivity(m);
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

            LayoutInflater inflater = (LayoutInflater) syncContacts.this.getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);


            view = inflater.inflate(R.layout.list_dtls, parent, false);


            TextView Tphone = (TextView) view.findViewById(R.id.phone);
            TextView check = (TextView) view.findViewById(R.id.check);
            TextView Tname = (TextView) view.findViewById(R.id.name);
            ImageView arrow = (ImageView)view.findViewById(R.id.imageView7);


            Tname.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {


                    //	 final Intent intent = new Intent(Intent.ACTION_VIEW, Contacts.CONTENT_URI);
                    //    startActivity(intent);
                    //	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    //startActivityForResult(intent, 1);


	 				/*	Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
	 	                i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
	 	                startActivity(i);
	 					*/
	 			/*
	 					Intent intent = new Intent(Intent.ACTION_VIEW);
	 				    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf("2"));
	 				    intent.setData(uri);


	 				    Intent intent1 = new Intent(Intents.Insert.ACTION);
	 				intent1.setType(ContactsContract.RawContacts.CONTENT_TYPE);
	 				//startActivity(intent1);




	 				 */
	 				/*
	 					Intent contacts = new Intent();
	            		contacts.setAction(android.content.Intent.ACTION_VIEW);
	            		contacts.setData(ContactsContract.Contacts.CONTENT_URI);
	            		startActivity(contacts);
	            		*/
                    Intent myActivity = new Intent (Intent.ACTION_DIAL, Uri.parse( "tel:"+phone.get(position)+""));
                    startActivity(myActivity);



                    //		Intent myActivity = new Intent(Intent.ACTION_EDIT, Uri.parse("content://contacts/people/1"));
                    //		startActivity(myActivity);

                    //		Intent myActivity = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + id.get(position)));
                    //		startActivity(myActivity);



                    if (phoneS.get(position).equals("1"))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Уже добавлен " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Uri smsUri = Uri.parse("smsto:"+phone.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        intent.putExtra("sms_body", "Привет! Я установил приложение IZUM! Присоединяйся");
                        startActivity(intent);
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
                        Uri smsUri = Uri.parse("smsto:"+phone.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        intent.putExtra("sms_body", "Привет! Я установил приложение IZUM! Присоединяйся");
                        startActivity(intent);
                    }


                }
            });

            arrow.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (phoneS.get(position).equals("1"))
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Уже добавлен " + phone.get(position), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                    {
                        Uri smsUri = Uri.parse("smsto:"+phone.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        intent.putExtra("sms_body", "Привет! Я установил приложение IZUM! Присоединяйся");
                        startActivity(intent);
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
                        Uri smsUri = Uri.parse("smsto:"+phone.get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        intent.putExtra("sms_body", "Привет! Я установил приложение IZUM! Присоединяйся");
                        startActivity(intent);
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
                check.setText("Пригласить");
                //check.setBackgroundResource(R.drawable.back_right);
                //check.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.back_right), null);
            }

            return view;
        }
    }



    public class send extends AsyncTask<Void, Void, String> {

        JSONObject jObject;
        String check;


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
                nameValuePairs.add(new BasicNameValuePair("token", token));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                //    data = EntityUtils.toString(entity);
                if (entity != null) {
                    try {
                        jObject = new JSONObject(EntityUtils.toString(entity));


                        if (jObject.getString("error") != null)
                        {


                            check = jObject.getString("error");

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


                                    for (int i=0; i<cursor.getCount();i++)
                                    {

                                        phoneS.add("0");

                                    }



                                    for (int i=0; i<phone.size();i++)
                                    {

                                        for (int ii=0; ii<newPhone.size();ii++)
                                        {

                                            System.out.println(phone.get(i) + "   " + newPhone.get(ii).toString());

                                            if (phone.get(i).toString().equals(newPhone.get(ii).toString()))

                                            {
                                                phoneS.set(i, "1");
                                            }
                                            else
                                            {

                                            }
                                        }
                                    }





                                }

                            }

                            else
                            {
                                if (jObject.getString("error_code") != null)
                                {
                                    String error_code = jObject.getString("error_code");
                                    status = "Ошибка - " + error_code;


                                    for (int i=0; i<cursor.getCount();i++)
                                    {

                                        phoneS.add("0");

                                    }





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


            if (check.equals("true"))
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        status, Toast.LENGTH_SHORT);
                toast.show();
            }

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

    private void setColor(){
        SharedPreferences sPref;
        sPref = getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if(sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                tvTitle.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                tvTitle.setBackgroundResource(R.color.green);
            } else if (col == 2) {
                tvTitle.setBackgroundResource(R.color.orange);
            } else if (col == 3) {
                tvTitle.setBackgroundResource(R.color.purple);
            }
        }else{
            tvTitle.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setColor();
    }
}
