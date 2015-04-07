package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    static String token;
    ListView favotites;
    static String URL = "http://im.topufa.org/index.php";
    static int currentPosition=0;

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
        SharedPreferences userData = getActivity().getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        favotites = (ListView)view.findViewById(R.id.lvFavorites);
        adapter = new conversationsAdapter(messages,context);
        favotites.setAdapter(adapter);
        favotites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {

                String UserId = adapter.getItem(position).uid;
                String nick = adapter.getItem(position).from;
                String fake = adapter.getItem(position).msgId;
                adapter.getItem(position).direction = "true";
                Intent i = new Intent(getActivity(),PrivateMessaging.class);
                i.putExtra("userId",UserId);
                i.putExtra("token",token);
                i.putExtra("nick",nick);
                i.putExtra("favorite","true");
                i.putExtra("shake", "false");
                i.putExtra("userPROFILE",adapter.getItem(position).userid);
                i.putExtra("mID",fake);
                i.putExtra("fromDialogs","true");
                i.putExtra("avatar",adapter.getItem(position).picURL);
                adapter.notifyDataSetChanged();
                startActivity(i);

            }
        });



        return view;
    }
    public  static void updateList()
    {
        messages.clear();
        adapter.notifyDataSetChanged();
    }

    public static void findNremove(String id)//удаляет чела с id из сиписка диалогов
    {
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(id))
            {
                messages.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public  static void addList(conversationsMsg p)
    {
        if(!checkInList(p)) {
            messages.add(0, p);
        }
        adapter.notifyDataSetChanged();
    }

    private static boolean checkInList(conversationsMsg msg) {
        boolean flag = false;                                   //избавиться от флага и лишних шагов цикла
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(msg.uid))
            {
                messages.get(i).message = msg.message;
                messages.get(i).time = msg.time;
                conversationsMsg m = messages.get(i);
                m.direction = "false";
                messages.remove(i);
                messages.add(0,m);
                adapter.notifyDataSetChanged();
                flag = true;
            }
        }
        return  flag;
    }

    public static void newMsg(conversationsMsg p)
    {
        boolean flag = true;
        for(int i=0; i<messages.size();i++)
        {
            if(messages.get(i).uid.equals(p.uid))
            {
                flag = false;
                conversationsMsg m = new conversationsMsg(p.uid,messages.get(i).from,p.message,messages.get(i).picURL,p.direction,messages.get(i).msgId,p.time,p.userid);
                messages.remove(i);
                messages.add(0,m);
                adapter.notifyDataSetChanged();
            }
        }
        if(flag)
        {
            conversationsMsg m = new conversationsMsg(p.uid,p.from,p.message,p.picURL,p.direction,p.msgId,p.time, p.userid);
            messages.add(0,m);
        }
    }

    public static void startOnlineCheck(){
        if(messages.size()>0) {
            String s = messages.get(0).userid;
            new getOnline().execute(s);
        }
    }

    private static class getOnline extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "online_get");
            jParser.setParam("userid", args[0]);
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String s=null;
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s.equals("false")){
                    try{
                        s=json.getString("online");
                        if(s.equals("true")&&messages.size()>0){
                            messages.get(currentPosition).online="| online";
                        }else{
                            messages.get(currentPosition).online="";
                        }
                        currentPosition++;
                        if(currentPosition<messages.size()){
                            new getOnline().execute(messages.get(currentPosition).userid);
                        }else{
                            adapter.notifyDataSetChanged();
                            currentPosition=0;
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }
            }

        }
    }//конец asyncTask
}
