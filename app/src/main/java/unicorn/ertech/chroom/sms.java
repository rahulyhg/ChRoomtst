package unicorn.ertech.chroom;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import unicorn.ertech.chroom.util.IabHelper;




public class sms extends Fragment {


	static final String ONE_BUTTON = "iz30";
	static final String TWO_BUTTON = "iz50";
	static final String THREE_BUTTON = "iz100";
	static final String FOUR_BUTTON = "bezlim";
	SharedPreferences sPref2;
	String str_value;
	String str_operator;


	String status30 = "";
	String sign30 = "";

	String status50 = "";
	String sign50 = "";

	String status100 = "";
	String sign100 = "";

	String statusb = "";
	String signb = "";


	final String USER = "user";
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	// The helper object
	IabHelper mHelper;
	TextView des;
	TextView sms;
	TextView play;
	TextView other;
	TextView count;
	TextView tvIz30;
	TextView tvIz50;
	TextView tvIz100;
	TextView tvBezlim;

	ProgressBar pb;
	String otvet = "";
	String token ="";
	String userPhone="";
	String URL = "http://im.topufa.org/index.php";
	int userID;
	
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//задаем разметку фрагменту
		View view = inflater.inflate(R.layout.sms, container, false);
		//ну и контекст, так как фрагменты не содержат собственного
		context = view.getContext();

		tvIz30 = (TextView) view.findViewById(R.id.textView30);
		tvIz50 = (TextView) view.findViewById(R.id.textView50);
		tvIz100 = (TextView) view.findViewById(R.id.textView100);
		tvBezlim = (TextView) view.findViewById(R.id.textViewB);


		//Пробуем получить номер с симки
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		userPhone = telephonyManager.getLine1Number();
		String tst=telephonyManager.getNetworkCountryIso()+telephonyManager.getNetworkOperator()+telephonyManager.getSimSerialNumber();

		//Если на симке не указан номер, то используем сохранённый при логине
		if(userPhone.equals("")) {
			SharedPreferences userData = context.getSharedPreferences("userdata", context.MODE_PRIVATE);
			if (userData.contains("login")) {
				userPhone = userData.getString("login", "");
			}
		}

		//Проверяем на соответствие формата 7хххххххххх
		if(userPhone.charAt(0)=='+'){
			userPhone=userPhone.substring(1);
		}
		if(userPhone.charAt(0)=='8'){
			StringBuffer stringBuffer=new StringBuffer(userPhone);
			stringBuffer.setCharAt(0,'7');
			userPhone=stringBuffer.toString();
		}

		Button bt30 = (Button) view.findViewById(R.id.bt30);
		Button bt50 = (Button) view.findViewById(R.id.bt50);
		Button bt100 = (Button) view.findViewById(R.id.bt100);
		Button btB = (Button) view.findViewById(R.id.btB);


		bt30.setOnClickListener(one);
		bt50.setOnClickListener(two);
		bt100.setOnClickListener(three);
		btB.setOnClickListener(four);

		pb=(ProgressBar)view.findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);

		token = DataClass.getToken();

		SharedPreferences sPref2 = context.getSharedPreferences("user", context.MODE_PRIVATE);
		if (sPref2.contains(USER)) {
			userID = sPref2.getInt(USER, 0);
			if (userID != 0) {
				//    new loadUserData().execute();
			}
		}
		
		return view;
	}



	View.OnClickListener one = new View.OnClickListener() {
		@Override
		public void onClick(View v){
			if (status30.equals(""))
			{
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask30().execute("30.00");
			}
			else
			{
				pb.setVisibility(View.VISIBLE);
				Toast.makeText(context, "Проверяем предидущий запрос", Toast.LENGTH_LONG).show();
				new checkStatus30().execute();
			}
		}
	};

	View.OnClickListener two = new View.OnClickListener() {
		@Override
		public void onClick(View v){
			if (status50.equals(""))
			{
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask50().execute("50.00");
			}
			else
			{
				pb.setVisibility(View.VISIBLE);
				Toast.makeText(context, "Проверяем предидущий запрос", Toast.LENGTH_LONG).show();
				new checkStatus50().execute();
			}
		}
	};

	View.OnClickListener three = new View.OnClickListener() {
		@Override
		public void onClick(View v){
			if (status100.equals(""))
			{
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask100().execute("100.00");
			}
			else
			{
				pb.setVisibility(View.VISIBLE);
				Toast.makeText(context, "Проверяем предидущий запрос", Toast.LENGTH_LONG).show();
				new checkStatus100().execute();
			}
		}
	};

	View.OnClickListener four = new View.OnClickListener() {
		@Override
		public void onClick(View v){
			if (statusb.equals(""))
			{
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTaskb().execute("500.00");
			}
			else
			{
				pb.setVisibility(View.VISIBLE);
				Toast.makeText(context, "Проверяем предидущий запрос", Toast.LENGTH_LONG).show();
				new checkStatusb().execute();
			}
		}
	};



	//для запросов на сервер

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}





	@SuppressLint("NewApi")
	public void postData30(String valueIwantToSend) {
		HttpClient httpclient = new DefaultHttpClient();

		try{
			HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"));
			sign30 = md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e");

			HttpResponse response = httpclient.execute(httppost);


			HttpEntity entity = response.getEntity();
			if(entity != null) {
				otvet = EntityUtils.toString(entity);
			}


			int responseCode = response.getStatusLine().getStatusCode();

			//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

			switch(responseCode) {
				case 200:

					break;
			}

			// Convert String to json object
			JSONObject json = null;
			try {
				json = new JSONObject(otvet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			try {
				str_value=json.getString("order_id");
				str_operator = json.getString("operator");


				status30 = json.getString("order_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //<< get value here



			//	otvet = EntityUtils.toString(response.toString());


		} catch (ClientProtocolException e) {
			// process execption
		} catch (IOException e) {
			// process execption
		}
	}



	@SuppressLint("NewApi")
	public void postData50(String valueIwantToSend) {
		HttpClient httpclient = new DefaultHttpClient();

		try{
			HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"));
			sign50 = md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e");

			HttpResponse response = httpclient.execute(httppost);


			HttpEntity entity = response.getEntity();
			if(entity != null) {
				otvet = EntityUtils.toString(entity);
			}


			int responseCode = response.getStatusLine().getStatusCode();

			//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

			switch(responseCode) {
				case 200:

					break;
			}

			// Convert String to json object
			JSONObject json = null;
			try {
				json = new JSONObject(otvet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			try {
				str_value=json.getString("order_id");
				str_operator = json.getString("operator");


				status50 = json.getString("order_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //<< get value here



			//	otvet = EntityUtils.toString(response.toString());


		} catch (ClientProtocolException e) {
			// process execption
		} catch (IOException e) {
			// process execption
		}
	}



	@SuppressLint("NewApi")
	public void postData100(String valueIwantToSend) {
		HttpClient httpclient = new DefaultHttpClient();

		try{
			HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"));
			sign100 = md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e");

			HttpResponse response = httpclient.execute(httppost);


			HttpEntity entity = response.getEntity();
			if(entity != null) {
				otvet = EntityUtils.toString(entity);
			}


			int responseCode = response.getStatusLine().getStatusCode();

			//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

			switch(responseCode) {
				case 200:

					break;
			}

			// Convert String to json object
			JSONObject json = null;
			try {
				json = new JSONObject(otvet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			try {
				str_value=json.getString("order_id");
				str_operator = json.getString("operator");


				status100 = json.getString("order_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //<< get value here



			//	otvet = EntityUtils.toString(response.toString());


		} catch (ClientProtocolException e) {
			// process execption
		} catch (IOException e) {
			// process execption
		}
	}



	@SuppressLint("NewApi")
	public void postDatab(String valueIwantToSend) {
		HttpClient httpclient = new DefaultHttpClient();

		try{
			HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"));
			signb = md5("/api/mc.init/?service_id=100781&test=0&amount="+valueIwantToSend+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e");

			HttpResponse response = httpclient.execute(httppost);


			HttpEntity entity = response.getEntity();
			if(entity != null) {
				otvet = EntityUtils.toString(entity);
			}


			int responseCode = response.getStatusLine().getStatusCode();

			//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

			switch(responseCode) {
				case 200:

					break;
			}

			// Convert String to json object
			JSONObject json = null;
			try {
				json = new JSONObject(otvet);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			try {
				str_value=json.getString("order_id");
				str_operator = json.getString("operator");


				statusb = json.getString("order_id");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //<< get value here



			//	otvet = EntityUtils.toString(response.toString());


		} catch (ClientProtocolException e) {
			// process execption
		} catch (IOException e) {
			// process execption
		}
	}







	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class checkStatus30 extends AsyncTask<String, Integer, Double>{

		String value ="";
		String value1 ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub




			HttpClient httpclient = new DefaultHttpClient();

			try{

				//	HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status30+"&service_id=100781&sign="+sign30);






				HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status30+"&service_id=100781&sign="+md5("/api/mc.get/?order_id="+status30+"&service_id=100781d96b7b83cad5f1f2ff83ec054a867204b603296e"));





				HttpResponse response = httpclient.execute(httppost);


				HttpEntity entity = response.getEntity();
				if(entity != null) {
					otvet = EntityUtils.toString(entity);
				}


				int responseCode = response.getStatusLine().getStatusCode();

				//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

				switch(responseCode) {
					case 200:

						break;
				}

				// Convert String to json object
				JSONObject json = null;
				try {
					json = new JSONObject(otvet);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				try {
					value=json.getString("status");
					value1 = json.getString("phone");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //<< get value here



				//	otvet = EntityUtils.toString(response.toString());


			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}



			return null;
		}

		protected void onPostExecute(Double result){
			pb.setVisibility(View.INVISIBLE);

			Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();


			// 	    Toast.makeText(context, value, Toast.LENGTH_LONG).show();
			//	Toast.makeText(context, value1, Toast.LENGTH_LONG).show();

			Toast.makeText(context, status30, Toast.LENGTH_LONG).show();
			Toast.makeText(context, sign30, Toast.LENGTH_LONG).show();



			if(value.equals("17"))
			{
				Toast.makeText(context, "Подождите, Предыдущий платеж не завершен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				status30 = "";
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask30().execute("30.00");
			}
			if(value.equals("1"))
			{
				Toast.makeText(context, "Ожидает обработки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				Toast.makeText(context, "Оплачен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("4"))
			{
				Toast.makeText(context, "Ошибка, недостаточно средств", Toast.LENGTH_LONG).show();
			}
			if(value.equals("5"))
			{
				Toast.makeText(context, "Внутренняя ошибка системы", Toast.LENGTH_LONG).show();
			}
			if(value.equals("6"))
			{
				Toast.makeText(context, "Отменено покупателем", Toast.LENGTH_LONG).show();
			}
			if(value.equals("7"))
			{
				Toast.makeText(context, "Отменено продавцом", Toast.LENGTH_LONG).show();
			}
			if(value.equals("8"))
			{
				Toast.makeText(context, "Возвращено покупателю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("9"))
			{
				Toast.makeText(context, "Передано оператору", Toast.LENGTH_LONG).show();
			}
			if(value.equals("10"))
			{
				Toast.makeText(context, "Ожидает отправки на инициацию", Toast.LENGTH_LONG).show();
			}
			if(value.equals("11"))
			{
				Toast.makeText(context, "Необходимо подтверждение инициации", Toast.LENGTH_LONG).show();
			}
			if(value.equals("12"))
			{
				Toast.makeText(context, "Нет ответа от сервера оператора", Toast.LENGTH_LONG).show();
			}
			if(value.equals("13"))
			{
				Toast.makeText(context, "Лимит по количеству платежей за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("14"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("15"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за неделю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("16"))
			{
				Toast.makeText(context, "Лимит по минимальному остатку на счете", Toast.LENGTH_LONG).show();
			}
			if(value.equals("18"))
			{
				Toast.makeText(context, "Услуга недоступна для абонента", Toast.LENGTH_LONG).show();
			}
			if(value.equals("19"))
			{
				Toast.makeText(context, "Время ожидания абонента истекло", Toast.LENGTH_LONG).show();
			}
			if(value.equals("20"))
			{
				Toast.makeText(context, "Превышены другие лимиты", Toast.LENGTH_LONG).show();
			}
			if(value.equals("21"))
			{
				Toast.makeText(context, "Не оплачен (по другой причине)", Toast.LENGTH_LONG).show();
			}
			if(value.equals("22"))
			{
				Toast.makeText(context, "Сумма платежа меньше допустимой", Toast.LENGTH_LONG).show();
			}
			if(value.equals("23"))
			{
				Toast.makeText(context, "Платеж перенаправлен на Pay-Per-Click", Toast.LENGTH_LONG).show();
			}


			pb.setVisibility(View.INVISIBLE);


		}

		protected void onProgressUpdate(Integer... progress){
			//    pb.setProgress(progress[0]);
		}
	}






	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class checkStatus50 extends AsyncTask<String, Integer, Double>{

		String value ="";
		String value1 ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub




			HttpClient httpclient = new DefaultHttpClient();

			try{

				//	HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status30+"&service_id=100781&sign="+sign30);






				HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status50+"&service_id=100781&sign="+md5("/api/mc.get/?order_id="+status50+"&service_id=100781d96b7b83cad5f1f2ff83ec054a867204b603296e"));





				HttpResponse response = httpclient.execute(httppost);


				HttpEntity entity = response.getEntity();
				if(entity != null) {
					otvet = EntityUtils.toString(entity);
				}


				int responseCode = response.getStatusLine().getStatusCode();

				//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

				switch(responseCode) {
					case 200:

						break;
				}

				// Convert String to json object
				JSONObject json = null;
				try {
					json = new JSONObject(otvet);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				try {
					value=json.getString("status");
					value1 = json.getString("phone");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //<< get value here



				//	otvet = EntityUtils.toString(response.toString());


			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}



			return null;
		}

		protected void onPostExecute(Double result){
			pb.setVisibility(View.INVISIBLE);

			Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();


			// 	    Toast.makeText(context, value, Toast.LENGTH_LONG).show();
			//	Toast.makeText(context, value1, Toast.LENGTH_LONG).show();

			Toast.makeText(context, status50, Toast.LENGTH_LONG).show();
			Toast.makeText(context, sign50, Toast.LENGTH_LONG).show();



			if(value.equals("17"))
			{
				Toast.makeText(context, "Подождите, Предыдущий платеж не завершен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				status50 = "";
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask50().execute("50.00");
			}
			if(value.equals("1"))
			{
				Toast.makeText(context, "Ожидает обработки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				Toast.makeText(context, "Оплачен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("4"))
			{
				Toast.makeText(context, "Ошибка, недостаточно средств", Toast.LENGTH_LONG).show();
			}
			if(value.equals("5"))
			{
				Toast.makeText(context, "Внутренняя ошибка системы", Toast.LENGTH_LONG).show();
			}
			if(value.equals("6"))
			{
				Toast.makeText(context, "Отменено покупателем", Toast.LENGTH_LONG).show();
			}
			if(value.equals("7"))
			{
				Toast.makeText(context, "Отменено продавцом", Toast.LENGTH_LONG).show();
			}
			if(value.equals("8"))
			{
				Toast.makeText(context, "Возвращено покупателю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("9"))
			{
				Toast.makeText(context, "Передано оператору", Toast.LENGTH_LONG).show();
			}
			if(value.equals("10"))
			{
				Toast.makeText(context, "Ожидает отправки на инициацию", Toast.LENGTH_LONG).show();
			}
			if(value.equals("11"))
			{
				Toast.makeText(context, "Необходимо подтверждение инициации", Toast.LENGTH_LONG).show();
			}
			if(value.equals("12"))
			{
				Toast.makeText(context, "Нет ответа от сервера оператора", Toast.LENGTH_LONG).show();
			}
			if(value.equals("13"))
			{
				Toast.makeText(context, "Лимит по количеству платежей за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("14"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("15"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за неделю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("16"))
			{
				Toast.makeText(context, "Лимит по минимальному остатку на счете", Toast.LENGTH_LONG).show();
			}
			if(value.equals("18"))
			{
				Toast.makeText(context, "Услуга недоступна для абонента", Toast.LENGTH_LONG).show();
			}
			if(value.equals("19"))
			{
				Toast.makeText(context, "Время ожидания абонента истекло", Toast.LENGTH_LONG).show();
			}
			if(value.equals("20"))
			{
				Toast.makeText(context, "Превышены другие лимиты", Toast.LENGTH_LONG).show();
			}
			if(value.equals("21"))
			{
				Toast.makeText(context, "Не оплачен (по другой причине)", Toast.LENGTH_LONG).show();
			}
			if(value.equals("22"))
			{
				Toast.makeText(context, "Сумма платежа меньше допустимой", Toast.LENGTH_LONG).show();
			}
			if(value.equals("23"))
			{
				Toast.makeText(context, "Платеж перенаправлен на Pay-Per-Click", Toast.LENGTH_LONG).show();
			}


			pb.setVisibility(View.INVISIBLE);


		}

		protected void onProgressUpdate(Integer... progress){
			//    pb.setProgress(progress[0]);
		}
	}






	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class checkStatus100 extends AsyncTask<String, Integer, Double>{

		String value ="";
		String value1 ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub




			HttpClient httpclient = new DefaultHttpClient();

			try{

				//	HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status30+"&service_id=100781&sign="+sign30);






				HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status100+"&service_id=100781&sign="+md5("/api/mc.get/?order_id="+status100+"&service_id=100781d96b7b83cad5f1f2ff83ec054a867204b603296e"));





				HttpResponse response = httpclient.execute(httppost);


				HttpEntity entity = response.getEntity();
				if(entity != null) {
					otvet = EntityUtils.toString(entity);
				}


				int responseCode = response.getStatusLine().getStatusCode();

				//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

				switch(responseCode) {
					case 200:

						break;
				}

				// Convert String to json object
				JSONObject json = null;
				try {
					json = new JSONObject(otvet);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				try {
					value=json.getString("status");
					value1 = json.getString("phone");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //<< get value here



				//	otvet = EntityUtils.toString(response.toString());


			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}



			return null;
		}

		protected void onPostExecute(Double result){
			pb.setVisibility(View.INVISIBLE);

			Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();


			// 	    Toast.makeText(context, value, Toast.LENGTH_LONG).show();
			//	Toast.makeText(context, value1, Toast.LENGTH_LONG).show();

			Toast.makeText(context, status100, Toast.LENGTH_LONG).show();
			Toast.makeText(context, sign100, Toast.LENGTH_LONG).show();



			if(value.equals("17"))
			{
				Toast.makeText(context, "Подождите, Предыдущий платеж не завершен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				status100 = "";
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTask100().execute("100.00");
			}
			if(value.equals("1"))
			{
				Toast.makeText(context, "Ожидает обработки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				Toast.makeText(context, "Оплачен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("4"))
			{
				Toast.makeText(context, "Ошибка, недостаточно средств", Toast.LENGTH_LONG).show();
			}
			if(value.equals("5"))
			{
				Toast.makeText(context, "Внутренняя ошибка системы", Toast.LENGTH_LONG).show();
			}
			if(value.equals("6"))
			{
				Toast.makeText(context, "Отменено покупателем", Toast.LENGTH_LONG).show();
			}
			if(value.equals("7"))
			{
				Toast.makeText(context, "Отменено продавцом", Toast.LENGTH_LONG).show();
			}
			if(value.equals("8"))
			{
				Toast.makeText(context, "Возвращено покупателю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("9"))
			{
				Toast.makeText(context, "Передано оператору", Toast.LENGTH_LONG).show();
			}
			if(value.equals("10"))
			{
				Toast.makeText(context, "Ожидает отправки на инициацию", Toast.LENGTH_LONG).show();
			}
			if(value.equals("11"))
			{
				Toast.makeText(context, "Необходимо подтверждение инициации", Toast.LENGTH_LONG).show();
			}
			if(value.equals("12"))
			{
				Toast.makeText(context, "Нет ответа от сервера оператора", Toast.LENGTH_LONG).show();
			}
			if(value.equals("13"))
			{
				Toast.makeText(context, "Лимит по количеству платежей за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("14"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("15"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за неделю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("16"))
			{
				Toast.makeText(context, "Лимит по минимальному остатку на счете", Toast.LENGTH_LONG).show();
			}
			if(value.equals("18"))
			{
				Toast.makeText(context, "Услуга недоступна для абонента", Toast.LENGTH_LONG).show();
			}
			if(value.equals("19"))
			{
				Toast.makeText(context, "Время ожидания абонента истекло", Toast.LENGTH_LONG).show();
			}
			if(value.equals("20"))
			{
				Toast.makeText(context, "Превышены другие лимиты", Toast.LENGTH_LONG).show();
			}
			if(value.equals("21"))
			{
				Toast.makeText(context, "Не оплачен (по другой причине)", Toast.LENGTH_LONG).show();
			}
			if(value.equals("22"))
			{
				Toast.makeText(context, "Сумма платежа меньше допустимой", Toast.LENGTH_LONG).show();
			}
			if(value.equals("23"))
			{
				Toast.makeText(context, "Платеж перенаправлен на Pay-Per-Click", Toast.LENGTH_LONG).show();
			}


			pb.setVisibility(View.INVISIBLE);


		}

		protected void onProgressUpdate(Integer... progress){
			//    pb.setProgress(progress[0]);
		}
	}






	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class checkStatusb extends AsyncTask<String, Integer, Double>{

		String value ="";
		String value1 ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub




			HttpClient httpclient = new DefaultHttpClient();

			try{

				//	HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+status30+"&service_id=100781&sign="+sign30);






				HttpGet httppost = new HttpGet("https://client.mixplat.ru/api/mc.get/?order_id="+statusb+"&service_id=100781&sign="+md5("/api/mc.get/?order_id="+statusb+"&service_id=100781d96b7b83cad5f1f2ff83ec054a867204b603296e"));





				HttpResponse response = httpclient.execute(httppost);


				HttpEntity entity = response.getEntity();
				if(entity != null) {
					otvet = EntityUtils.toString(entity);
				}


				int responseCode = response.getStatusLine().getStatusCode();

				//		Toast.makeText(context, responseCode+"", Toast.LENGTH_LONG).show();

				switch(responseCode) {
					case 200:

						break;
				}

				// Convert String to json object
				JSONObject json = null;
				try {
					json = new JSONObject(otvet);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				try {
					value=json.getString("status");
					value1 = json.getString("phone");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //<< get value here



				//	otvet = EntityUtils.toString(response.toString());


			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}



			return null;
		}

		protected void onPostExecute(Double result){
			pb.setVisibility(View.INVISIBLE);

			Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();


			// 	    Toast.makeText(context, value, Toast.LENGTH_LONG).show();
			//	Toast.makeText(context, value1, Toast.LENGTH_LONG).show();

			Toast.makeText(context, statusb, Toast.LENGTH_LONG).show();
			Toast.makeText(context, signb, Toast.LENGTH_LONG).show();



			if(value.equals("17"))
			{
				Toast.makeText(context, "Подождите, Предыдущий платеж не завершен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				statusb = "";
				pb.setVisibility(View.VISIBLE);
				new MyAsyncTaskb().execute("500.00");
			}
			if(value.equals("1"))
			{
				Toast.makeText(context, "Ожидает обработки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("2"))
			{
				Toast.makeText(context, "Оплачен", Toast.LENGTH_LONG).show();
			}
			if(value.equals("4"))
			{
				Toast.makeText(context, "Ошибка, недостаточно средств", Toast.LENGTH_LONG).show();
			}
			if(value.equals("5"))
			{
				Toast.makeText(context, "Внутренняя ошибка системы", Toast.LENGTH_LONG).show();
			}
			if(value.equals("6"))
			{
				Toast.makeText(context, "Отменено покупателем", Toast.LENGTH_LONG).show();
			}
			if(value.equals("7"))
			{
				Toast.makeText(context, "Отменено продавцом", Toast.LENGTH_LONG).show();
			}
			if(value.equals("8"))
			{
				Toast.makeText(context, "Возвращено покупателю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("9"))
			{
				Toast.makeText(context, "Передано оператору", Toast.LENGTH_LONG).show();
			}
			if(value.equals("10"))
			{
				Toast.makeText(context, "Ожидает отправки на инициацию", Toast.LENGTH_LONG).show();
			}
			if(value.equals("11"))
			{
				Toast.makeText(context, "Необходимо подтверждение инициации", Toast.LENGTH_LONG).show();
			}
			if(value.equals("12"))
			{
				Toast.makeText(context, "Нет ответа от сервера оператора", Toast.LENGTH_LONG).show();
			}
			if(value.equals("13"))
			{
				Toast.makeText(context, "Лимит по количеству платежей за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("14"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за сутки", Toast.LENGTH_LONG).show();
			}
			if(value.equals("15"))
			{
				Toast.makeText(context, "Лимит по сумме оплаты за неделю", Toast.LENGTH_LONG).show();
			}
			if(value.equals("16"))
			{
				Toast.makeText(context, "Лимит по минимальному остатку на счете", Toast.LENGTH_LONG).show();
			}
			if(value.equals("18"))
			{
				Toast.makeText(context, "Услуга недоступна для абонента", Toast.LENGTH_LONG).show();
			}
			if(value.equals("19"))
			{
				Toast.makeText(context, "Время ожидания абонента истекло", Toast.LENGTH_LONG).show();
			}
			if(value.equals("20"))
			{
				Toast.makeText(context, "Превышены другие лимиты", Toast.LENGTH_LONG).show();
			}
			if(value.equals("21"))
			{
				Toast.makeText(context, "Не оплачен (по другой причине)", Toast.LENGTH_LONG).show();
			}
			if(value.equals("22"))
			{
				Toast.makeText(context, "Сумма платежа меньше допустимой", Toast.LENGTH_LONG).show();
			}
			if(value.equals("23"))
			{
				Toast.makeText(context, "Платеж перенаправлен на Pay-Per-Click", Toast.LENGTH_LONG).show();
			}


			pb.setVisibility(View.INVISIBLE);


		}

		protected void onProgressUpdate(Integer... progress){
			//    pb.setProgress(progress[0]);
		}
	}


	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class MyAsyncTask30 extends AsyncTask<String, Integer, Double>{

		String value ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			value = params[0];

			postData30(params[0]);
			return null;
		}

		protected void onPostExecute(Double result){

			//Toast.makeText(context, "https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"), Toast.LENGTH_LONG).show();
			//Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();
			//		Toast.makeText(context, str_value, Toast.LENGTH_LONG).show();



			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("Оповещение");

			// set dialog message
			alertDialogBuilder
					.setMessage("Ожидайте смс с подтверждением оплаты")
					.setCancelable(false)
					.setPositiveButton("Ок",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					});


			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();







			new sendData().execute();




		}

		protected void onProgressUpdate(Integer... progress){
			//   pb.setProgress(progress[0]);
		}
	}







	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class MyAsyncTask50 extends AsyncTask<String, Integer, Double>{

		String value ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			value = params[0];

			postData50(params[0]);
			return null;
		}

		protected void onPostExecute(Double result){

			//Toast.makeText(context, "https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"), Toast.LENGTH_LONG).show();
			//Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();
			//		Toast.makeText(context, str_value, Toast.LENGTH_LONG).show();



			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("Оповещение");

			// set dialog message
			alertDialogBuilder
					.setMessage("Ожидайте смс с подтверждением оплаты")
					.setCancelable(false)
					.setPositiveButton("Ок",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					});


			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();







			new sendData().execute();




		}

		protected void onProgressUpdate(Integer... progress){
			//   pb.setProgress(progress[0]);
		}
	}






	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class MyAsyncTask100 extends AsyncTask<String, Integer, Double>{

		String value ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			value = params[0];

			postData100(params[0]);
			return null;
		}

		protected void onPostExecute(Double result){

			//Toast.makeText(context, "https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"), Toast.LENGTH_LONG).show();
			//Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();
			//		Toast.makeText(context, str_value, Toast.LENGTH_LONG).show();



			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("Оповещение");

			// set dialog message
			alertDialogBuilder
					.setMessage("Ожидайте смс с подтверждением оплаты")
					.setCancelable(false)
					.setPositiveButton("Ок",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					});


			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();







			new sendData().execute();




		}

		protected void onProgressUpdate(Integer... progress){
			//   pb.setProgress(progress[0]);
		}
	}





	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class MyAsyncTaskb extends AsyncTask<String, Integer, Double>{

		String value ="";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			value = params[0];

			postDatab(params[0]);
			return null;
		}

		protected void onPostExecute(Double result){

			//Toast.makeText(context, "https://client.mixplat.ru/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_message&sign="+ md5("/api/mc.init/?service_id=100781&test=0&amount="+value+"&phone="+userPhone+"&description=oplata_description&currency=RUB&success_message=success_messaged96b7b83cad5f1f2ff83ec054a867204b603296e"), Toast.LENGTH_LONG).show();
			//Toast.makeText(context, otvet, Toast.LENGTH_LONG).show();
			//		Toast.makeText(context, str_value, Toast.LENGTH_LONG).show();



			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("Оповещение");

			// set dialog message
			alertDialogBuilder
					.setMessage("Ожидайте смс с подтверждением оплаты")
					.setCancelable(false)
					.setPositiveButton("Ок",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					});


			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();







			new sendData().execute();




		}

		protected void onProgressUpdate(Integer... progress){
			//   pb.setProgress(progress[0]);
		}
	}




	@SuppressLint("NewApi")
	private class sendData extends AsyncTask<String, String, JSONObject> {
		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jParser = new JSONParser();
			jParser.setParam("action", "smspayment");
			jParser.setParam("token", token);
			jParser.setParam("order_id", str_value);
			jParser.setParam("operator", str_operator);

			JSONObject json = jParser.getJSONFromUrl(URL);
			Log.e("saveToken", json.toString());
			return json;
		}
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(JSONObject json) {
			if (json != null) {
				String status = "";
				try {
					status = json.getString("error");
				} catch (JSONException e) {
					Log.e("saveToken", e.toString());
				}
				if(status.equals("false"))
				{
					Toast.makeText(context, "ok!", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
				}
				pb.setVisibility(View.INVISIBLE);
			}
		}
	}

}
