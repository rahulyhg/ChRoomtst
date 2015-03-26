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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class VActivity extends Activity {

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> phone = new ArrayList<String>();
    ArrayList<String> phoneS = new ArrayList<String>();
    //	ArrayList<String> id = new ArrayList<String>();
    ArrayList<String> idS = new ArrayList<String>();
    ArrayList<String> newPhone = new ArrayList<String>();
    String token;

    ArrayList<String> newData = new ArrayList<String>();


    ProgressDialog progressDialog;

    String data = "";
    String status = "";
    String tmp = "";
    Cursor cursor;


    int zaraza = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vactivity_layout);

        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }

        try
        {


            progressDialog = ProgressDialog.show(this,
                    "Соединение", "Пожалуйста, подождите", true);

            cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            name = new ArrayList<String>(cursor.getCount());
            phone = new ArrayList<String>(cursor.getCount());
            phoneS = new ArrayList<String>(cursor.getCount());
            //     id =  new ArrayList<String>(cursor.getCount());


            if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){

                while (cursor.moveToNext()) {
                    String nameB =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumberB = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String idB = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                    //    id.add(idB.toString());
                    name.add(nameB.toString());
                    phone.add(phoneNumberB.toString());
                    phoneS.add("0");

                }

                new send().execute();
            }



	/*	if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

			String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);

			Toast toast2 = Toast.makeText(getApplicationContext(),
					searchQuery, Toast.LENGTH_SHORT);
					toast2.show();

        }*/

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка на сервере", Toast.LENGTH_SHORT);
            toast.show();
        }


    }









    void set()
    {



	/*	 Toast toast = Toast.makeText(getApplicationContext(),
				 phoneS.size()+"  "  + name.size() + "  " + phone.size(), Toast.LENGTH_SHORT);
   	              toast.show();
		*/

        ListView lvMain = (ListView) findViewById(R.id.listView1);


        ArrayList<String> Nname = new ArrayList<String>();
        ArrayList<String> Nphone = new ArrayList<String>();
        ArrayList<String> NphoneS = new ArrayList<String>();
        //	ArrayList<String> idSS = new ArrayList<String>();



        for (int i=0; i<phoneS.size()-1;i++)
        {

            if (!phoneS.get(i).toString().equals("0"))
            {

	        	/*    toast = Toast.makeText(getApplicationContext(),
	      				 phoneS.size()+"  "  + name.size() + "  " + phone.size(), Toast.LENGTH_SHORT);
	         	              toast.show();

	        	*/

                //    phoneS.get(i)


                //   idSS.add(idS.get(i));
                Nname.add(name.get(i));
                Nphone.add(phone.get(i));
                NphoneS.add(phoneS.get(i));
            }

        }


        if(Nname.size() > 0 )
        {
            MyThumbnaildapter	thadapter = new MyThumbnaildapter(
                    VActivity.this, R.layout.list_dtls,
                    Nname, Nphone, NphoneS);


            lvMain.setAdapter(thadapter);

        }
        else
        {
            Toast toast1 = Toast.makeText(getApplicationContext(),
                    "Ничего не найдено", Toast.LENGTH_SHORT);
            toast1.show();
        }










    }



    public class MyThumbnaildapter extends ArrayAdapter<String> {

        List<String> name;
        List<String> phone;
        List<String> phoneS;
        //	List<String> idSS;


        public MyThumbnaildapter(Context ct, int textViewResourceId,
                                 List<String> name, List<String> phone, List<String> phoneS) {
            super(ct, textViewResourceId, name);
            this.name = name;
            this.phone = phone;
            this.phoneS = phoneS;
            //	this.idSS = idSS;

        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View view = null;

            LayoutInflater inflater = (LayoutInflater) VActivity.this.getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);


            view = inflater.inflate(R.layout.list_dtls, parent, false);


            TextView Tphone = (TextView) view.findViewById(R.id.phone);
            TextView check = (TextView) view.findViewById(R.id.check);
            TextView Tname = (TextView) view.findViewById(R.id.name);
            ImageView arrow=(ImageView)view.findViewById(R.id.imageView7);


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
                    //     		Intent myActivity = new Intent (Intent.ACTION_DIAL, Uri.parse( "tel:"+phone.get(position)+""));
                    //     		startActivity(myActivity);



                    //		Intent myActivity = new Intent(Intent.ACTION_EDIT, Uri.parse("content://contacts/people/1"));
                    //		startActivity(myActivity);

                    //		Intent myActivity = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + id.get(position)));
                    //		startActivity(myActivity);




                    Intent i = new Intent(getApplicationContext(),Profile2.class);
                    i.putExtra("userId",phoneS.get(position));
                    i.putExtra("token",token);
                    startActivity(i);


                }
            });

            check.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(),Profile2.class);
                    i.putExtra("userId",phoneS.get(position));
                    i.putExtra("token",token);
                    startActivity(i);
                }
            });

            arrow.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(),Profile2.class);
                    i.putExtra("userId",phoneS.get(position));
                    i.putExtra("token",token);
                    startActivity(i);
                }
            });

            Tphone.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),Profile2.class);
                    i.putExtra("userId",phoneS.get(position));
                    i.putExtra("token",token);
                    startActivity(i);
                }
            });



            Tphone.setText("" + phone.get(position));
            Tname.setText("" + name.get(position));
            check.setText(phoneS.get(position));
            //check.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.back_right), null);


            check.setText("Профиль");


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


                //   System.out.println(EntityUtils.toString(entity).toString());

                //   data = EntityUtils.toString(entity) + "";
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




                                    for (int i=0; i<phone.size();i++)
                                    {

                                        for (int ii=0; ii<newPhone.size();ii++)
                                        {

                                            //        	   System.out.println(phone.get(i) + "   " + newPhone.get(ii).toString());

                                            if (phone.get(i).toString().equals(newPhone.get(ii).toString()))
                                            {

                                                phoneS.set(i, idS.get(ii));
                                                zaraza = zaraza +1;

                                            }
                                            else
                                            {
                                                //  phoneS.add("0");

                                            }



                                        }
                                    }





                                }

                            }
                            else
                            if (check.equals("true"))
                            {
                                if (jObject.getString("error_code") != null)
                                {
                                    String error_code = jObject.getString("error_code");
                                    status = "Ошибка - " + error_code;


                                    for (int i=0; i<cursor.getCount();i++)
                                    {

                                        //   phoneS.add("0");

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
            else
            {
                set();
            }



            progressDialog.dismiss();








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
        idS.add(Pid);

    }
}
