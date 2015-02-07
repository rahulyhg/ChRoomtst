package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 26.12.2014.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
public class JSONParser  {
    static InputStream is = null;
    static JSONObject jObj = null;
    JSONObject jsonObject = new JSONObject();
    static String json;
    static String js;
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    // конструктор
    public JSONParser() {
        json = "";
    }

    public  void setParam(String key, String value)
    {
        // Добавим данные (пара - "название - значение")
        nameValuePairs.add(new BasicNameValuePair(key, value));
    }

    public JSONObject getJSONFromUrl(String url) {
        // Создадим HttpClient и PostHandler
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            Log.e("nameValuePairs", nameValuePairs.toString());
            // Выполним запрос
            response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

        String responseString = new String();
        try {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                try {

                    responseString = EntityUtils.toString(responseEntity);
                    try {
                        jsonObject = new JSONObject(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // return JSON
            Log.e("nameValuePairsResponse", jsonObject.toString());
            return jsonObject;
        }catch (NullPointerException e)
        {
            return null;
        }
    }
}
