package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 26.12.2014.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
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

import android.app.Application;
import android.util.Log;
public class newJsonParser extends Application {
    static InputStream is = null;
    static JSONObject jObj = null;
    JSONObject jsonObject;
    static String json;
    URLConnection connection;
    HttpURLConnection httpConnection;
    static  String request="[";
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    // конструктор
    public newJsonParser() {
        jsonObject = new JSONObject();

    }

    public  void setParam(String key, String value)
    {
        // Добавим данные (пара - "название - значение")
        try {
            jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONFromUrl(String Url) {
        // Создадим HttpClient и PostHandler
        String resultString = new String("");
            URL url = null;
            try {
                url = new URL("http://im.topufa.org/index.php");
                int portOfProxy = android.net.Proxy.getDefaultPort();
                if( portOfProxy > 0 ){
                    Proxy proxyDef = new Proxy(Proxy.Type.HTTP,new InetSocketAddress( android.net.Proxy.getDefaultHost(), portOfProxy));
                    connection =  url.openConnection(proxyDef);
                }
                else
                {
                    connection = url.openConnection();
                }

                httpConnection = (HttpURLConnection)connection;
                HttpURLConnection httpConnection = (HttpURLConnection)connection;
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpConnection.setRequestProperty("Connection","Keep-Alive");
                httpConnection.setRequestProperty("Host", "http://im.topufa.org/index.php");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                // здесь можем писать в поток данные запроса
                OutputStreamWriter out = new   OutputStreamWriter(httpConnection.getOutputStream());
                String str = jsonObject.toString();
            out.write(jsonObject.toString());

            out.flush();
            out.close();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();

                InputStreamReader isr = new InputStreamReader(in, "UTF-8");

                StringBuffer data = new StringBuffer();
                int c;
                while ((c = isr.read()) != -1){
                    data.append((char) c);
                }

                resultString = new String (data.toString());

            }
            else
            {
                resultString = "";
            }
        }
        catch (MalformedURLException e) {
            resultString = "MalformedURLException:" + e.getMessage();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(resultString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
