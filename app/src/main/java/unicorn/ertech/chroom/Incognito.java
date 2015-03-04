package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 04.01.2015.
 */
public class Incognito extends Fragment {
    private Context context;
    ImageView ivRandom;
    ImageView ivChat;
    ImageView ivMJ;
    ImageView ivJM;
    final String SAVED_COLOR = "color";
    SharedPreferences sPref;
    String URL = "http://im.topufa.org/index.php";


    /**
     * Handle the results from the voice recognition activity.
     */
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
        final View view = inflater.inflate(R.layout.fragment_incognito, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        ivRandom = (ImageView) view.findViewById(R.id.ivIncognitoRand);
        ivChat = (ImageView) view.findViewById(R.id.ivIncognitoChat);
        ivMJ = (ImageView) view.findViewById(R.id.ivIncognitoMj);
        ivJM = (ImageView) view.findViewById(R.id.ivIncognitoJm);
        setColor();
        ivRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncognitoTab incognito_parent = (IncognitoTab) getActivity();
                incognito_parent.startRandom();
            }
        });
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncognitoTab incognito_parent = (IncognitoTab) getActivity();
                incognito_parent.startChat();
            }
        });

        ivMJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchingMj().execute();
            }
        });

        ivJM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchingJm().execute();
            }
        });
        return view;
    }

    private void setColor() {
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                ivMJ.setImageResource(R.drawable.incognito_mjb);
                ivJM.setImageResource(R.drawable.incognito_jmb);
                ivChat.setImageResource(R.drawable.incognito_chatb);
                ivRandom.setImageResource(R.drawable.incognito_rndb);
            } else if (col == 0) {
                ivMJ.setImageResource(R.drawable.incognito_mj);
                ivJM.setImageResource(R.drawable.incognito_jm);
                ivChat.setImageResource(R.drawable.incognito_chat);
                ivRandom.setImageResource(R.drawable.incognito_rnd);
            } else if (col == 3) {
                ivMJ.setImageResource(R.drawable.incognito_mjp);
                ivJM.setImageResource(R.drawable.incognito_jmp);
                ivChat.setImageResource(R.drawable.incognito_chatp);
                ivRandom.setImageResource(R.drawable.incognito_rndp);
            }else if(col==2){
                ivMJ.setImageResource(R.drawable.incognito_mjo);
                ivJM.setImageResource(R.drawable.incognito_jmo);
                ivChat.setImageResource(R.drawable.incognito_chato);
                ivRandom.setImageResource(R.drawable.incognito_rndo);
            }
        }
    }

    private class SearchingMj extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Ищем ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            JSONObject json = null;
            long wait = 2200;
            String status = "";String num = "";
            long elapsedtime = 0;
            long startTime=   System.currentTimeMillis();
            do {

                //ставим нужные нам параметры
                jParser.setParam("token", Main.str);
                jParser.setParam("action", "lookfor");
                jParser.setParam("type", "1");
                if(elapsedtime<5000) {
                    json = jParser.getJSONFromUrl(URL);
                    try {
                        status = json.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status.equals("false")) {
                        try {
                            num = json.getString("total");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!num.equals("0")) {
                            break;
                        } else {
                            try {
                                Thread.sleep(wait);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    long currTime = System.currentTimeMillis();
                    elapsedtime = currTime - startTime;

                    if (elapsedtime > 5000) {
                        break;
                    }
                }
                else{break;}
            }while(true);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            String status = "";
            if (json != null) {
            try {
                status = json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status.equals("false"))
            {
                String num = "";
                try {
                    num = json.getString("total");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(num.equals("0")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Нет результатов!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String s="", avatar = "", id = "";
                    JSONObject messag = null;

                    try {
                        s = json.getString("nick");
                        avatar = json.getString("avatar");
                        id = json.getString("id");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("NullPointerException", e.toString());
                    }


                    Intent i = new Intent(getActivity().getApplicationContext(),anonMessaging.class);
                    i.putExtra("nick",s);
                    i.putExtra("userId",id);
                    i.putExtra("fake","true");
                    i.putExtra("token",Main.str);
                    i.putExtra("avatar",avatar);
                    startActivity(i);
                }
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска!", Toast.LENGTH_LONG).show();
            }
        }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска, проверьте Ваше подключение к Интернету!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SearchingJm extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Ищем ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            JSONObject json = null;
            long wait = 2200;
            String status = "";String num = "";
            long elapsedtime = 0;
            long startTime=   System.currentTimeMillis();
            do {

                //ставим нужные нам параметры
                jParser.setParam("token", Main.str);
                jParser.setParam("action", "lookfor");
                jParser.setParam("type", "2");
                if(elapsedtime<5000) {
                    json = jParser.getJSONFromUrl(URL);
                    try {
                        status = json.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status.equals("false")) {
                        try {
                            num = json.getString("total");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!num.equals("0")) {
                            break;
                        } else {
                            try {
                                Thread.sleep(wait);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    long currTime = System.currentTimeMillis();
                    elapsedtime = currTime - startTime;

                    if (elapsedtime > 5000) {
                        break;
                    }
                }
                else{break;}
            }while(true);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            String status = "";
            if (json != null) {
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    String num = "";
                    try {
                        num = json.getString("total");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (num.equals("0")) {
                        Toast.makeText(getActivity().getApplicationContext(), "Нет результатов!", Toast.LENGTH_SHORT).show();
                    } else {
                        String s = "", avatar = "", id = "";
                        JSONObject messag = null;

                        try {
                            s = json.getString("nick");
                            avatar = json.getString("avatar");
                            id = json.getString("id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Log.e("NullPointerException", e.toString());
                        }


                        Intent i = new Intent(getActivity().getApplicationContext(), anonMessaging.class);
                        i.putExtra("nick", s);
                        i.putExtra("userId", id);
                        i.putExtra("fake", "true");
                        i.putExtra("token", Main.str);
                        i.putExtra("avatar", avatar);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Ошибка при совершении поиска, проверьте Ваше подключение к Интернету!", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        setColor();
    }
}
