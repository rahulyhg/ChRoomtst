package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 22.01.2015.
 */
public class Profile2 extends Activity implements View.OnClickListener{
    SharedPreferences sPref;
    boolean like_current;
    boolean kiss_current;

    ImageView butKiss;
    ImageView butLike;
    ImageView profilePhoto;
    ImageView profileGlass;
    ImageView smallProfilePhoto;
    ImageView photo1;
    ImageView photo2;
    ImageView photo3;
    ImageView photo4;
    TextView tvProfStat;
    String token, userId, nick;
    String picUrl;
    Picasso mPicasso;
    TextView etName;
    TextView info;
    Button butSend;

    final String LIKE_STATE = "like";
    final String KISS_STATE = "kiss";
    final String PHOTO_STATE="photo";
    final String SAVED_COLOR = "color";

    String URL = "http://im.topufa.org/index.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile2);
        //setContentView(R.layout.tab_incognito);
        mPicasso = Picasso.with(getApplicationContext());
        etName = (TextView)findViewById(R.id.etName);
        info = (TextView)findViewById(R.id.tvProfileStatus);

        RelativeLayout topRow = (RelativeLayout)findViewById(R.id.topRow);
        Button back = (Button)findViewById(R.id.profileBack);
        profileGlass = (ImageView)findViewById(R.id.profileGlass);
        photo1=(ImageView)findViewById(R.id.photo1);
        photo2=(ImageView)findViewById(R.id.photo2);
        photo3=(ImageView)findViewById(R.id.photo3);
        photo4=(ImageView)findViewById(R.id.photo4);
        butSend = (Button)findViewById(R.id.butProfileSend);
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        TextView tvProfPhoto = (TextView)findViewById(R.id.tvProfilePhoto);
        TextView tvProfInfo = (TextView)findViewById(R.id.tvProfileInfo);
        tvProfStat = (TextView)findViewById(R.id.tvProfileStatus);
        if(sPref.contains(SAVED_COLOR)){
            int col = sPref.getInt(SAVED_COLOR,0);
            if(col==1){
                topRow.setBackgroundResource(R.color.blue);
                back.setBackgroundResource(R.color.blue);
                tvProfInfo.setBackgroundResource(R.drawable.b_string);
                tvProfPhoto.setBackgroundResource(R.drawable.b_string);
                tvProfStat.setBackgroundResource(R.color.bluelight);
                profileGlass.setBackgroundResource(R.color.blueglass);
                butSend.setBackgroundResource(R.drawable.but_blue);
            }else if(col==0){
                topRow.setBackgroundResource(R.color.green);
                back.setBackgroundResource(R.color.green);
                tvProfInfo.setBackgroundResource(R.drawable.g_strip);
                tvProfPhoto.setBackgroundResource(R.drawable.g_strip);
                tvProfStat.setBackgroundResource(R.color.greenlight);
                profileGlass.setBackgroundResource(R.color.greenglass);
                butSend.setBackgroundResource(R.drawable.but_green);
            }else if(col==2){
                topRow.setBackgroundResource(R.color.orange);
                back.setBackgroundResource(R.color.orange);
                tvProfInfo.setBackgroundResource(R.drawable.o_strip);
                tvProfPhoto.setBackgroundResource(R.drawable.o_strip);
                tvProfStat.setBackgroundResource(R.color.orangelight);
                profileGlass.setBackgroundResource(R.color.orangeglass);
                butSend.setBackgroundResource(R.drawable.but_orange);
            }else if(col==3){
                topRow.setBackgroundResource(R.color.purple);
                back.setBackgroundResource(R.color.purple);
                tvProfInfo.setBackgroundResource(R.drawable.p_string);
                tvProfPhoto.setBackgroundResource(R.drawable.p_string);
                tvProfStat.setBackgroundResource(R.color.purplelight);
                profileGlass.setBackgroundResource(R.color.purpleglass);
                butSend.setBackgroundResource(R.drawable.but_purple);
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ImageView butGift=(ImageView)findViewById(R.id.profileGift);
        butGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), GiftsActivity.class);
                startActivity(in);
            }
        });

        butLike=(ImageView)findViewById(R.id.profileLike);
        like_current = loadData(LIKE_STATE);
        if(like_current==true){
            butLike.setImageResource(R.drawable.like_fill);
        }
        butLike.setOnClickListener(this);

        butKiss=(ImageView)findViewById(R.id.profileKiss);
        kiss_current = loadData(KISS_STATE);
        if(kiss_current==true){
            butKiss.setImageResource(R.drawable.kiss_fill);
        }
        butKiss.setOnClickListener(this);

        profilePhoto=(ImageView)findViewById(R.id.ivProfilePhoto);
        smallProfilePhoto=(ImageView)findViewById(R.id.profileSmallPhoto);

        Button butBack=(Button)findViewById(R.id.profileBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        final Intent i = getIntent();
        token = i.getStringExtra("token");
        userId = i.getStringExtra("userId");
        picUrl = i.getStringExtra("avatar");
        nick = i.getStringExtra("nick");

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), PhotoViewer.class);
                in.putExtra("photo",i.getStringExtra("avatar"));
                startActivity(in);
            }
        });

        if(isNetworkAvailable()) {
            new getProfile().execute();
            String picUrl = i.getStringExtra("avatar");
            Picasso mPicasso;
            mPicasso = Picasso.with(getApplicationContext());
            mPicasso.load(picUrl).skipMemoryCache().into(profilePhoto);
            Picasso.with(getApplicationContext()).load(picUrl).transform(new PicassoRoundTransformation()).fit().skipMemoryCache().into(smallProfilePhoto);

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Проверьте Ваше подключение к Интернету!", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendMessage(){
        Intent in = new Intent(this, PrivateMessaging.class);
        in.putExtra("userId", userId);
        in.putExtra("token", token);
        in.putExtra("avatar", picUrl);
        in.putExtra("nick", nick);
        in.putExtra("fake" , "false");
        startActivity(in);
    }

    public void closeMe(){
        this.finish();
    }

    void saveData(boolean state, String target) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(target, state);
        ed.commit();
    }

    boolean loadData(String target) {
        boolean current_state;
        sPref = getPreferences(MODE_PRIVATE);
        current_state = sPref.getBoolean(target, false);
        return current_state;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileLike:
                if(like_current==false) {
                    saveData(true, LIKE_STATE);
                    like_current=true;
                    butLike.setImageResource(R.drawable.like_fill);
                }else{
                    saveData(false, LIKE_STATE);
                    like_current=false;
                    butLike.setImageResource(R.drawable.like);
                }
                break;
            case R.id.profileKiss:
                if(kiss_current==false){
                    saveData(true, KISS_STATE);
                    kiss_current=true;
                    butKiss.setImageResource(R.drawable.kiss_fill);
                }else{
                    saveData(false, KISS_STATE);
                    kiss_current=false;
                    butKiss.setImageResource(R.drawable.kiss);
                }
                break;
            default:
                break;
        }
    }


    private class getProfile extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile2.this);
            pDialog.setMessage("Получение данных профиля ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "profile_get");
            jParser.setParam("userid", userId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            String status = "";
            if(json != null) {

                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("false")) {
                    try {
                        etName.setText(json.getString("name"));
                        info.setText(json.getString("info"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении запроса!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Проверьте Ваше подключение к Интернету!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
