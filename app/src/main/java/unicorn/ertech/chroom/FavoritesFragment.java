package unicorn.ertech.chroom;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

/**
 * Created by Timur on 18.01.2015.
 */
public class FavoritesFragment extends Fragment {
    Context context;

    static conversationsAdapter adapter;
    static List<conversationsMsg> messages = new ArrayList<conversationsMsg>();
    Timer myTimer;
    String token;
    conversationsMsg agent;
    String URL = "http://im.topufa.org/index.php";

    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_priv2, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        token = Main.str;

        return view;
    }

    public void getfavorites()
    {
        new Favorites().execute();
    }

    private class Favorites extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_get");
            jParser.setParam("list", "1");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String status = null;
                String num = null;
                JSONArray arr = null;
                JSONObject messag = null;
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status.equals("false"))
                {
                    try {
                        num = json.getString("total");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!num.equals("0"))
                    {
                        try {
                            arr = json.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < Integer.parseInt(num); i++) {
                            try {
                                messag = new JSONObject(arr.get(i).toString());
                                conversationsMsg p = new conversationsMsg(messag.getString("uid"), messag.getString("nickname"),""/*messag.getString("message")*/, messag.getString("avatar"),"1","false",messag.getString("time"));

                                    messages.add(0, p);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                Log.e("NullPointerException", e.toString());
                            }
                        }
                    }
                }



            }

        }
    }//конец asyncTask
}
