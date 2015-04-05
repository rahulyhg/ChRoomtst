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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
/**
 * Created by Timur on 22.01.2015.
 */
public class Profile2 extends Activity implements View.OnClickListener {
    SharedPreferences sPref;
    boolean like_current;
    boolean kiss_current, fromMessaging;
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
    String token, userId, nick,userProfile;
    String[] photosURLs;
    String picUrl;
    Picasso mPicasso;
    TextView etName, onlineTv;
    TextView info, hobbiesTv, hereForTv, familyTv, etProfileCity, birthDay, profileSex, searchSex;
    Button butSend;
    int pic_width, pic_width2;
    final String SAVED_COLOR = "color";
    String picURL = "";
    String picURLFull = "";
    String URL = "http://im.topufa.org/index.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile2);
//setContentView(R.layout.tab_incognito);
        mPicasso = Picasso.with(getApplicationContext());
        etName = (TextView) findViewById(R.id.etName);
        info = (TextView) findViewById(R.id.etProfile2About);
        hobbiesTv = (TextView) findViewById(R.id.etProfile2Hobbies);
        hereForTv = (TextView) findViewById(R.id.etProfile2Herefor);
        familyTv = (TextView) findViewById(R.id.etProfile2Relationships);
        tvProfStat = (TextView) findViewById(R.id.tvProfileStatus);
        etProfileCity = (TextView) findViewById(R.id.etProfile2City);
        birthDay = (TextView) findViewById(R.id.tvBirthday2);
        profileSex = (TextView) findViewById(R.id.etProfile2Sex);
        searchSex = (TextView) findViewById(R.id.etProfile2SearchSex);
        onlineTv=(TextView)findViewById(R.id.tvProfOnline);
        searchSex.setText("Не выбрано");
        profileSex.setText("Не выбрано");
        RelativeLayout topRow = (RelativeLayout) findViewById(R.id.topRow);
        ImageButton back = (ImageButton) findViewById(R.id.profileBack);
        profileGlass = (ImageView) findViewById(R.id.profileGlass);
        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);
        butSend = (Button) findViewById(R.id.butProfileSend);

        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width = metricsB.widthPixels;
        pic_width2 = (int) (150 * metricsB.density);
        photosURLs = new String[10];

        photo1.setOnClickListener(photoClick);
        photo2.setOnClickListener(photoClick);
        photo3.setOnClickListener(photoClick);
        photo4.setOnClickListener(photoClick);

        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        TextView tvProfPhoto = (TextView) findViewById(R.id.tvProfilePhoto);
        TextView tvProfInfo = (TextView) findViewById(R.id.tvProfileInfo);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                topRow.setBackgroundResource(R.color.blue);
                back.setBackgroundResource(R.color.blue);
                tvProfInfo.setBackgroundResource(R.color.blue);
                tvProfPhoto.setBackgroundResource(R.color.blue);
                //tvProfStat.setBackgroundResource(R.color.bluelight);
                //profileGlass.setBackgroundResource(R.color.blueglass);
                butSend.setBackgroundResource(R.drawable.but_blue);
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                back.setBackgroundResource(R.color.green);
                tvProfInfo.setBackgroundResource(R.color.green);
                tvProfPhoto.setBackgroundResource(R.color.green);
                //tvProfStat.setBackgroundResource(R.color.greenlight);
                //profileGlass.setBackgroundResource(R.color.greenglass);
                butSend.setBackgroundResource(R.drawable.but_green);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                back.setBackgroundResource(R.color.orange);
                tvProfInfo.setBackgroundResource(R.color.orange);
                tvProfPhoto.setBackgroundResource(R.color.orange);
                //tvProfStat.setBackgroundResource(R.color.orangelight);
                //profileGlass.setBackgroundResource(R.color.orangeglass);
                butSend.setBackgroundResource(R.drawable.but_orange);
            } else if (col == 3) {
                topRow.setBackgroundResource(R.color.purple);
                back.setBackgroundResource(R.color.purple);
                tvProfInfo.setBackgroundResource(R.color.purple);
                tvProfPhoto.setBackgroundResource(R.color.purple);
                //tvProfStat.setBackgroundResource(R.color.purplelight);
                //profileGlass.setBackgroundResource(R.color.purpleglass);
                butSend.setBackgroundResource(R.drawable.but_purple);
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ImageView butGift = (ImageView) findViewById(R.id.profileGift);
        butGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), GiftsActivity.class);
                startActivity(in);
            }
        });
        butLike = (ImageView) findViewById(R.id.profileLike);
        butLike.setOnClickListener(this);
        butKiss = (ImageView) findViewById(R.id.profileKiss);
        butKiss.setOnClickListener(this);
        profilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        smallProfilePhoto = (ImageView) findViewById(R.id.profileSmallPhoto);
        ImageButton butBack = (ImageButton) findViewById(R.id.profileBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        final Intent i = getIntent();
        //token = i.getStringExtra("token");
        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        userId = i.getStringExtra("userId");
        userProfile=i.getStringExtra("userPROFILE");
        fromMessaging=i.getBooleanExtra("fromMessaging", false);
        //picUrl = i.getStringExtra("avatar");
        //nick = i.getStringExtra("nick");
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
                in.putExtra("photos", photosURLs);
                in.putExtra("id", 0);
                startActivity(in);
            }
        });
        if (isNetworkAvailable()) {
            new getProfile().execute();
//String picUrl = i.getStringExtra("avatar");
        } else {
            Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернету!", Toast.LENGTH_SHORT).show();
        }
    }

    protected View.OnClickListener photoClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.photo1:
                    Intent photoIntent = new Intent(getApplicationContext(), PhotoViewer.class);
                    photoIntent.putExtra("photos", photosURLs);
                    photoIntent.putExtra("id", 1);
                    startActivity(photoIntent);
                    break;
                case R.id.photo2:
                    Intent photoIntent2 = new Intent(getApplicationContext(), PhotoViewer.class);
                    photoIntent2.putExtra("photos", photosURLs);
                    photoIntent2.putExtra("id", 2);
                    startActivity(photoIntent2);
                    break;
                case R.id.photo3:
                    Intent photoIntent3 = new Intent(getApplicationContext(), PhotoViewer.class);
                    photoIntent3.putExtra("photos", photosURLs);
                    photoIntent3.putExtra("id", 3);
                    startActivity(photoIntent3);
                    break;
                case R.id.photo4:
                    Intent photoIntent4 = new Intent(getApplicationContext(), PhotoViewer.class);
                    photoIntent4.putExtra("photos", photosURLs);
                    photoIntent4.putExtra("id", 4);
                    startActivity(photoIntent4);
                    break;
                default:
                    break;
            }
        }
    };

    public void sendMessage() {
        if(fromMessaging){
            this.finish();
        }else {
            Intent in = new Intent(this, PrivateMessaging.class);
            in.putExtra("userId", userId);
            in.putExtra("token", token);
            in.putExtra("avatar", photosURLs[4]);
            in.putExtra("nick", nick);
            in.putExtra("favorite", "false");
            in.putExtra("shake", "false");
            in.putExtra("fromDialogs", "false");
            in.putExtra("fake", "false");
            in.putExtra("userPROFILE", userProfile);
            startActivity(in);
        }
    }

    public void closeMe() {
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
                if (like_current == false) {
//saveData(true, LIKE_STATE);
                    new giveLike().execute();
                    like_current = true;
                    butLike.setImageResource(R.drawable.like_fill);
                } else {
//saveData(false, LIKE_STATE);
                    new deleteLike().execute();
                    like_current = false;
                    butLike.setImageResource(R.drawable.like);
                }
                break;
            case R.id.profileKiss:
                if (kiss_current == false) {
//saveData(true, KISS_STATE);
                    new giveKiss().execute();
                    kiss_current = true;
                    butKiss.setImageResource(R.drawable.kiss_fill);
                } else {
//saveData(false, KISS_STATE);
                    new deleteKiss().execute();
                    kiss_current = false;
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
            if (json != null) {
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("false")) {
                    try {
                        String like = "";
                        String kiss = "";
                        nick=json.getString("name");
                        etName.setText(nick);
                        tvProfStat.setText(json.getString("status"));
                        userId = json.getString("id");
                        like = json.getString("like");
                        kiss = json.getString("kiss");
                        picURL = json.getString("avatar");
                        picURLFull = json.getString("avatar_full");
                        if(json.getBoolean("online")){
                            onlineTv.setText("online");
                        }else{
                            onlineTv.setText("offline");
                        }
                        if (like.equals("1")) {
                            butLike.setImageResource(R.drawable.like_fill);
                            like_current = true;
                        }
                        if (kiss.equals("1")) {
                            butKiss.setImageResource(R.drawable.kiss_fill);
                            kiss_current = true;
                        }
                        Picasso mPicasso;
                        mPicasso = Picasso.with(getApplicationContext());
                        if(!picURLFull.equals("http://im.topufa.org/")){
                            mPicasso.load(picURLFull).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(profilePhoto);
                        }
                        if(!picURL.equals("http://im.topufa.org/")){
                            mPicasso.load(picURL).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(smallProfilePhoto);
                        }
                        etProfileCity.setText(json.getString("city"));
                        info.setText(json.getString("info"));
                        birthDay.setText(json.getString("age"));
                        Integer sex = json.getInt("sex");
                        String[] sexarray = getResources().getStringArray(R.array.sex);
                        if (sex == 1) {
                            profileSex.setText(sexarray[1]);
                        }
                        if (sex == 0) {
                            profileSex.setText(sexarray[0]);
                        }
                        sex = json.getInt("lookingfor");
                        if (sex == 1) {
                            searchSex.setText(sexarray[1]);
                        }
                        if (sex == 0) {
                            searchSex.setText(sexarray[0]);
                        }
                        String[] familyarray = getResources().getStringArray(R.array.family);
                        familyTv.setText(familyarray[json.getInt("sp")]);
                        hobbiesTv.setText(getStringFromArray(json.getString("interest"), R.array.hobbies));
                        hereForTv.setText(getStringFromArray(json.getString("herefor"), R.array.herefor));
                        photosURLs[0] = json.getString("photo1");
                        photosURLs[1] = json.getString("photo2");
                        photosURLs[2] = json.getString("photo3");
                        photosURLs[3] = json.getString("photo4");
                        photosURLs[4] = picURL;
                        photosURLs[5] = json.getString("photo1_full");
                        photosURLs[6] = json.getString("photo2_full");
                        photosURLs[7] = json.getString("photo3_full");
                        photosURLs[8] = json.getString("photo4_full");
                        photosURLs[9] = picURLFull;
                        if(!photosURLs[3].equals("http://im.topufa.org/")){
                            mPicasso.load(photosURLs[3]).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(photo4);
                        }
                        if(!photosURLs[2].equals("http://im.topufa.org/")){
                            mPicasso.load(photosURLs[2]).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(photo3);
                        }
                        if(!photosURLs[1].equals("http://im.topufa.org/")){
                            mPicasso.load(photosURLs[1]).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(photo2);
                        }
                        if(!photosURLs[0].equals("http://im.topufa.org/")){
                            mPicasso.load(photosURLs[0]).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(photo1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении запроса!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернету!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class giveLike extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "like_set");
//jParser.setParam("userid", myID);
            jParser.setParam("userid", userId);
            jParser.setParam("type", "1");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("false")) {
                    like_current = true;
                    Toast.makeText(getApplicationContext(), "Вы поставили пользователю лайк!", Toast.LENGTH_LONG).show();
                } else {
                    butLike.setImageResource(R.drawable.like);
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class giveKiss extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "like_set");
//jParser.setParam("userid", myID);
            jParser.setParam("userid", userId);
            jParser.setParam("type", "2");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("false")) {
                    kiss_current = true;
                    Toast.makeText(getApplicationContext(), "Вы отправили пользователю поцелуй!", Toast.LENGTH_LONG).show();
                } else {
                    butKiss.setImageResource(R.drawable.kiss);
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class deleteKiss extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "like_delete");
//jParser.setParam("userid", myID);
            jParser.setParam("userid", userId);
            jParser.setParam("type", "2");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("false")) {
                    butKiss.setImageResource(R.drawable.kiss);
                    kiss_current = false;
                    Toast.makeText(getApplicationContext(), "Вы забрали у пользователя поцелуй!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class deleteLike extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("privatesend", "222");
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "like_delete");
//jParser.setParam("userid", myID);
            jParser.setParam("userid", userId);
            jParser.setParam("type", "1");
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                String status = "";
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("false")) {
                    butLike.setImageResource(R.drawable.like);
                    like_current = false;
                    Toast.makeText(getApplicationContext(), "Вы забрали у пользователя свой лайк!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении действия!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Проверьте Ваше подключение к Интернет!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private String getStringFromArray(String arrayPositions, int arrayId) {
        String[] stringsArr = getResources().getStringArray(arrayId);
        String str = "";
        String[] strArr = arrayPositions.split(",");
        for (int i = 0; i < 5; i++) {
            if (Integer.parseInt(strArr[i]) > 0) {
                str = str + stringsArr[Integer.parseInt(strArr[i])] + ",";
            } else {
                break;
            }
        }
        if (str.length() > 1) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
