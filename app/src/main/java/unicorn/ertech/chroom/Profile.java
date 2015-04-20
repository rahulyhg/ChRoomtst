package unicorn.ertech.chroom;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Timur on 08.01.2015.
 */
public class Profile extends Activity {
    SharedPreferences sPref;
    SharedPreferences sPref2;
    String[] photosURLs;
    public int pic_width, pic_width2;
    int k=0;
    boolean startService = true;
    static final int GALLERY_REQUEST = 1;
    public static ImageHolder iHolder;
    ImageView profilePhoto;
    ImageView profileGlass;
    ImageView smallProfilePhoto;
    ImageView photo1;
    ImageView photo2;
    ImageView photo3;
    ImageView photo4;
    EditText tvProfStat;
    TextView etName;
    TextView birthDay, hobbiesTv, hereForTv, datePick;
    EditText etProfileAbout;
    //EditText etProfileCity;
    Button saveProfile;
    Spinner profileSex, searchSex, familySpin, regionSpin, etProfileCity;
    int sex, ssex, sp, photo_type, reg, cit, myMonth=-1, myDay=-1, myYear=-1;
    int[] selectedHobbies, selectedHere;
    Picasso mPicasso;
    final String PHOTO_STATE = "photo";
    final String PHOTO_PATH = "path";
    final String SAVED_COLOR = "color";
    final String USER = "user";
    String pathToUserPhoto = new String();
    String URL = "http://im.topufa.org/index.php";
    String userName;
    String userAbout;
    public String token;
    int userID;
    int savedCity=0, incr=0;
    int currentRegion = 8, currentCities=R.array.cities;
    GeoAdapter2 adapter4;

    AlertDialog.Builder ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);
//setContentView(R.layout.tab_incognito);
        mPicasso = Picasso.with(getApplicationContext());
        RelativeLayout topRow = (RelativeLayout) findViewById(R.id.topRowAbout);
        ImageButton back = (ImageButton) findViewById(R.id.profileBack);
        profileGlass = (ImageView) findViewById(R.id.profileGlass);
        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);
        birthDay = (TextView)findViewById(R.id.tvBirthday);
        profileSex = (Spinner)findViewById(R.id.spinnerProfileSex);
        searchSex=(Spinner)findViewById(R.id.spinProfileSearchSex);
        saveProfile = (Button) findViewById(R.id.butProfileSave);
        datePick=(TextView)findViewById(R.id.tvProfSetBirthday);
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        sPref2 = getSharedPreferences("user", MODE_PRIVATE);
        TextView tvProfPhoto = (TextView) findViewById(R.id.tvProfilePhoto);
        TextView tvProfInfo = (TextView) findViewById(R.id.tvProfileInfo);
        etName = (TextView) findViewById(R.id.etName);
        etProfileAbout = (EditText) findViewById(R.id.etProfileAbout);
        etProfileCity = (Spinner) findViewById(R.id.etProfileCity);
        tvProfStat = (EditText) findViewById(R.id.tvProfileStatus);
        hobbiesTv = (TextView)findViewById(R.id.tvPickedHobbies);
        hereForTv=(TextView)findViewById(R.id.tvPickedHerefor);
        familySpin=(Spinner)findViewById(R.id.spinProfileRelationships);
        regionSpin=(Spinner)findViewById(R.id.etProfileRegion);
        selectedHobbies= new int[5];
        selectedHere = new int[5];
        etName.setOnClickListener(showSaveButton); //показ кнопки сохранить при редактировании данных о себе
        //etProfileCity.setOnClickListener(showSaveButton);
        etProfileAbout.setOnClickListener(showSaveButton);
        tvProfStat.setOnClickListener(showSaveButton);
        familySpin.setOnTouchListener(showSaveButton3);
        //regionSpin.setOnItemClickListener(showSaveButton2);
        regionSpin.setOnTouchListener(showSaveButton3);
        searchSex.setOnTouchListener(showSaveButton3);
        profileSex.setOnTouchListener(showSaveButton3);

        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width = metricsB.widthPixels;
        pic_width2=(int)(150*metricsB.density);

        photosURLs= new String[10];
//ClassLoader classLoader = MultipartEntityBuilder.class.getClassLoader();
//URL resource = classLoader.getResource("org/apache/http/message/BasicHeaderValueFormatter.class");
//Log.e("profile", resource.toString());
        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }
        if (sPref2.contains(USER)) {
            userID = sPref2.getInt(USER, 0);
            if (userID != 0) {
                new loadUserData().execute();
            }
        }
        hobbiesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(R.array.hobbies, hobbiesTv, selectedHobbies, 1);
                saveProfile.setVisibility(View.VISIBLE);
            }
        });
        hereForTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(R.array.herefor, hereForTv, selectedHere, 0);
                saveProfile.setVisibility(View.VISIBLE);
            }
        });
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                topRow.setBackgroundResource(R.color.blue);
                back.setBackgroundResource(R.color.blue);
                saveProfile.setBackgroundResource(R.color.blue);
                tvProfInfo.setBackgroundResource(R.color.blue);
                tvProfPhoto.setBackgroundResource(R.color.blue);
                //tvProfStat.setBackgroundResource(R.color.bluelight);
                //profileGlass.setBackgroundResource(R.color.blueglass);
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                back.setBackgroundResource(R.color.green);
                tvProfInfo.setBackgroundResource(R.color.green);
                tvProfPhoto.setBackgroundResource(R.color.green);
                saveProfile.setBackgroundResource(R.color.green);
                //tvProfStat.setBackgroundResource(R.color.greenlight);
                //profileGlass.setBackgroundResource(R.color.greenglass);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                back.setBackgroundResource(R.color.orange);
                tvProfInfo.setBackgroundResource(R.color.orange);
                saveProfile.setBackgroundResource(R.color.orange);
                tvProfPhoto.setBackgroundResource(R.color.orange);
                //tvProfStat.setBackgroundResource(R.color.orangelight);
                //profileGlass.setBackgroundResource(R.color.orangeglass);
            } else if (col == 3) {
                topRow.setBackgroundResource(R.color.purple);
                back.setBackgroundResource(R.color.purple);
                tvProfInfo.setBackgroundResource(R.color.purple);
                saveProfile.setBackgroundResource(R.color.purple);
                tvProfPhoto.setBackgroundResource(R.color.purple);
                //tvProfStat.setBackgroundResource(R.color.purplelight);
                //profileGlass.setBackgroundResource(R.color.purpleglass);
            }
        }
        saveProfile.setVisibility(View.INVISIBLE);
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = profileSex.getSelectedItemPosition();
                ssex=searchSex.getSelectedItemPosition();
                saveProfile.setVisibility(View.INVISIBLE);
//hob=hobbiesSpin.getSelectedItemPosition();
//here=hereForSpin.getSelectedItemPosition();
                new sendUserData().execute();
            }
        });
        profilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PhotoViewer.class);
                i.putExtra("photos", photosURLs);
                i.putExtra("id", 0);
                startActivity(i);
            }
        });
        smallProfilePhoto = (ImageView) findViewById(R.id.profileSmallPhoto);
        smallProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService = false;
                photo_type=0;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
//saveData(true, PHOTO_STATE);
            }
        });
        photo1.setOnClickListener(photoClick);
        photo2.setOnClickListener(photoClick);
        photo3.setOnClickListener(photoClick);
        photo4.setOnClickListener(photoClick);
        if (loadData(PHOTO_STATE)) {
            String tmp_path = GetPath();
            if (!tmp_path.equals("")) {
                iHolder = new ImageHolder();
                iHolder.imageWidth=profilePhoto.getLayoutParams().width;
//pic_height = profilePhoto.getMaxHeight();
                mPicasso.load(tmp_path).resize(pic_width, 0).noFade().into(profilePhoto);
//mPicasso.load(tmp_path).centerInside().fit().noFade().into(profilePhoto);
                mPicasso.load(tmp_path).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(smallProfilePhoto);
            }
        }

        ImageButton butBack = (ImageButton) findViewById(R.id.profileBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        MyCustomAdapter3 adapter2 = new MyCustomAdapter3(this,
                R.layout.spinner_without_arrows, getResources().getStringArray(R.array.sex));
        profileSex.setAdapter(adapter2);
        searchSex.setAdapter(adapter2);
        GeoAdapter adapter = new GeoAdapter(this,
                R.layout.spinner_without_bg, getResources().getStringArray(R.array.regions));
        regionSpin.setAdapter(adapter);
        SpAdapter adapter3 = new SpAdapter(this,
                R.layout.spinner_without_bg, getResources().getStringArray(R.array.family));
        familySpin.setAdapter(adapter3);
        adapter4 = new GeoAdapter2(this,
                R.layout.spinner_without_bg, getResources().getStringArray(R.array.cities));
        etProfileCity.setAdapter(adapter4);
        regionSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                currentRegion=selectedItemPosition;
                currentCities=GeoConvertIds.getCityArrayId(currentRegion);
                //adapter.notifyDataSetChanged();
                //adapter.clear();
                adapter4=null;
                adapter4 = new GeoAdapter2(getApplicationContext(),
                        R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
                adapter4.notifyDataSetChanged();
                etProfileCity.setAdapter(adapter4);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        etProfileCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                if(itemSelected==null) {
                    etProfileCity.setSelection(savedCity);
                    //incr++;
                }
                Log.d("selectedspinner", Integer.toString(selectedItemPosition));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }else{
            final String[] mActions ={"Загрузить", "Открыть"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Выберите действие");
            builder.setItems(mActions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    switch(item){
                        case 0:
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                            break;
                        case 1:
                            Intent photoIntent = new Intent(getApplicationContext(), PhotoViewer.class);
                            photoIntent.putExtra("photos", photosURLs);
                            photoIntent.putExtra("id", photo_type);
                            startActivity(photoIntent);
                            break;
                        default:
                            break;
                }
            }
            });
            builder.setCancelable(true);
            return builder.create();
        }
    }

    protected View.OnClickListener photoClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.photo1:
                    photo_type=1;
                    showDialog(2);
                    //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                    //photoPickerIntent.setType("image/*");
                    //startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                    break;
                case R.id.photo2:
                    photo_type=2;
                    showDialog(2);
                    //Intent photoPickerIntent2 = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                    //photoPickerIntent2.setType("image/*");
                    //startActivityForResult(photoPickerIntent2, GALLERY_REQUEST);
                    break;
                case R.id.photo3:
                    photo_type=3;
                    showDialog(2);
                    //Intent photoPickerIntent3 = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                    //photoPickerIntent3.setType("image/*");
                    //startActivityForResult(photoPickerIntent3, GALLERY_REQUEST);
                    break;
                case R.id.photo4:
                    photo_type=4;
                    showDialog(2);
                    //Intent photoPickerIntent4 = new Intent(Intent.ACTION_PICK); //Здесь запускает галерею
                    //photoPickerIntent4.setType("image/*");
                    //startActivityForResult(photoPickerIntent4, GALLERY_REQUEST);
                    break;
                default:
                    break;
            }
        }
    };
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
    //
//
//Здесь обрабатывается полученный путь картинки
//
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData(); //Это путь картинки
                    if(photo_type==0){
                        mPicasso.load(selectedImage).resize(pic_width, 0).noFade().into(profilePhoto);
//mPicasso.load(selectedImage).centerInside().fit().noFade().into(profilePhoto);
                        mPicasso.load(selectedImage).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(smallProfilePhoto);
//profilePhoto.setImageURI(selectedImage);
                        SavePath(selectedImage.toString());
                        pathToUserPhoto = getImagePath(selectedImage);
                        startService = true;
                        new sendUserPhoto().execute();
                    }else{
                        pathToUserPhoto = getImagePath(selectedImage);
                        switch (photo_type) {
                            case 1:
                                mPicasso.load(selectedImage).resize(pic_width2, 0).noFade().into(photo1);
                                photosURLs[5]=pathToUserPhoto;
                                break;
                            case 2:
                                mPicasso.load(selectedImage).resize(pic_width2, 0).noFade().into(photo2);
                                break;
                            case 3:
                                mPicasso.load(selectedImage).resize(pic_width2, 0).noFade().into(photo3);
                                break;
                            case 4:
                                mPicasso.load(selectedImage).resize(pic_width2, 0).noFade().into(photo4);
                                break;
                            default:
                                break;
                        }
                        try {
                            new sendAdditionalPhoto().execute();
                        }catch (Exception e){
                            Log.d("sendphoto", e.toString());
                        }
                    }
                }
        }
    }
    protected void SavePhoto() {
        Bitmap bitmap = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "profile_avatar.png");
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void SavePath(String path) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(PHOTO_PATH, path);
        ed.putBoolean(PHOTO_STATE, true);
        ed.commit();
    }
    protected String GetPath() {
        String path;
        sPref = getPreferences(MODE_PRIVATE);
        path = sPref.getString(PHOTO_PATH, "");
        return path;
    }
    protected String getImagePath(Uri uri){
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    private class loadUserData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("action", "profile_get");
            jParser.setParam("userid", Integer.toString(userID));
            jParser.setParam("token", token);
// Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String city;
                Log.e("profile", json.getString("name"));
                Log.e("profile", json.getString("info"));
                try {
                    int region=json.getInt("region");
                    if(region!=0){
                        currentRegion=GeoConvertIds.getAppRegionId(region);
                        currentCities=GeoConvertIds.getCityArrayId(currentRegion);
                        Log.d("selectedProf", Integer.toString(currentRegion));
                        regionSpin.setSelection(currentRegion);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                city=json.getString("city");
                if(!city.equals(null)){
                    try {
                        int cityId=GeoConvertIds.getAppCityId(Integer.parseInt(city));
                        String[] stringsArr = getResources().getStringArray(currentCities);
                        birthDay.setText(stringsArr[cityId]);
                        adapter4=null;
                        adapter4 = new GeoAdapter2(getApplicationContext(),
                                R.layout.spinner_with_arrows, getResources().getStringArray(currentCities));
                        //adapter4.notifyDataSetChanged();
                        etProfileCity.setAdapter(adapter4);
                        savedCity=cityId;
                        Log.d("selectedProf", Integer.toString(cityId));
                        etProfileCity.setSelection(cityId);
                        adapter4.notifyDataSetChanged();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                userName = json.getString("name");
                userAbout = json.getString("info");
                tvProfStat.setText(json.getString("status"));
                birthDay.setText(birthDay.getText() + " | "+json.getString("age"));
                int sex = json.getInt("sex");
                Log.e("selectedItem", profileSex.getSelectedItem()+"");
                profileSex.setSelection(sex);
                sex = json.getInt("lookingfor");
                searchSex.setSelection(sex);
                familySpin.setSelection(json.getInt("sp"));
                hobbiesTv.setText(getStringFromArray(json.getString("interest"), R.array.hobbies));
                hereForTv.setText(getStringFromArray(json.getString("herefor"), R.array.herefor));
                photosURLs[0]=json.getString("photo1");
                photosURLs[1]=json.getString("photo2");
                photosURLs[2]=json.getString("photo3");
                photosURLs[3]=json.getString("photo4");
                photosURLs[4]=json.getString("avatar");;
                photosURLs[5]=json.getString("photo1_full");
                photosURLs[6]=json.getString("photo2_full");
                photosURLs[7]=json.getString("photo3_full");
                photosURLs[8]=json.getString("photo4_full");
                photosURLs[9]=json.getString("avatar_full");
                if(!photosURLs[3].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[3]).resize(pic_width2, 0).noFade().into(photo4);
                }
                if(!photosURLs[2].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[2]).resize(pic_width2, 0).noFade().into(photo3);
                }
                if(!photosURLs[1].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[1]).resize(pic_width2, 0).noFade().into(photo2);
                }
                if(!photosURLs[0].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[0]).resize(pic_width2, 0).noFade().into(photo1);
                }
                if(!photosURLs[4].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[4]).resize(pic_width2, 0).transform(new PicassoRoundTransformation()).noFade().into(smallProfilePhoto);
                }
                if(!photosURLs[9].equals("http://im.topufa.org/")){
                    mPicasso.load(photosURLs[9]).resize(pic_width, 0).noFade().into(profilePhoto);
                }
                sPref = getSharedPreferences("user", MODE_PRIVATE); //Сохраняем ID юзера, для доступа в профиле
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("avatar_link", photosURLs[4]);
                ed.commit();
                Log.e("selectedItem", profileSex.getSelectedItem() + "");
            } catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
            etName.setText(userName);
            etProfileAbout.setText(userAbout);
        }
    }
    private class sendUserData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sp=familySpin.getSelectedItemPosition();
            reg=GeoConvertIds.getServerRegionId(regionSpin.getSelectedItemPosition());
            cit=GeoConvertIds.getServerCityId(etProfileCity.getSelectedItemPosition());
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("action", "profile_set");
//jParser.setParam("userid", Integer.toString(userID));
            jParser.setParam("token", token);
            if(myDay>0) {
                jParser.setParam("day", Integer.toString(myDay));
            }
            if(myMonth>0) {
                jParser.setParam("month", Integer.toString(myMonth));
            }
            if(myYear>0) {
                jParser.setParam("year", Integer.toString(myYear));
            }
            jParser.setParam("interest1", Integer.toString(selectedHobbies[0]));
            jParser.setParam("interest2", Integer.toString(selectedHobbies[1]));
            jParser.setParam("interest3", Integer.toString(selectedHobbies[2]));
            jParser.setParam("interest4", Integer.toString(selectedHobbies[3]));
            jParser.setParam("interest5", Integer.toString(selectedHobbies[4]));
            jParser.setParam("herefor1", Integer.toString(selectedHere[0]));
            jParser.setParam("herefor2", Integer.toString(selectedHere[1]));
            jParser.setParam("herefor3", Integer.toString(selectedHere[2]));
            jParser.setParam("herefor4", Integer.toString(selectedHere[3]));
            jParser.setParam("herefor5", Integer.toString(selectedHere[4]));
            jParser.setParam("sp", Integer.toString(sp));
            jParser.setParam("region", Integer.toString(reg));
            jParser.setParam("city", Integer.toString(cit));
            jParser.setParam("info", etProfileAbout.getText().toString());
            jParser.setParam("status", tvProfStat.getText().toString());
            jParser.setParam("sex",String.valueOf(profileSex.getSelectedItemId()));
            jParser.setParam("lookingfor",String.valueOf(searchSex.getSelectedItemId()));
// Getting JSON from URL
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
                    Log.e("saveToken", e.toString());
                }
                if(status.equals("false"))
                {
                    Toast.makeText(getApplicationContext(), "Данные успешно изменены!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при изменении данных!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private class sendUserPhoto extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            String responseString = new String();
            JSONObject json = null;
            try
            {
                HttpClient client = new DefaultHttpClient();
                File file = new File(pathToUserPhoto);
                HttpPost post = new HttpPost(URL);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addBinaryBody("file", file, ContentType.create("image/jpeg"), file.getName());
                entityBuilder.addTextBody("action", "avatar_set");
                entityBuilder.addTextBody("token", token);
                // add more key/value pairs here as needed
                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                final HttpEntity httpEntity = response.getEntity();
                //Log.v("result", EntityUtils.toString(httpEntity));
                responseString = EntityUtils.toString(httpEntity);
                json = new JSONObject(responseString);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return json;
        }
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
                    Toast.makeText(getApplicationContext(), "Изображение успешно добавлено на сервер!", Toast.LENGTH_LONG).show();
                    new reloadUserData().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении изображения на сервер!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class reloadUserData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
//ставим нужные нам параметры
            jParser.setParam("action", "profile_get");
            jParser.setParam("userid", Integer.toString(userID));
            jParser.setParam("token", token);
// Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                photosURLs[0]=json.getString("photo1");
                photosURLs[1]=json.getString("photo2");
                photosURLs[2]=json.getString("photo3");
                photosURLs[3]=json.getString("photo4");
                photosURLs[4]=json.getString("avatar");;
                photosURLs[5]=json.getString("photo1_full");
                photosURLs[6]=json.getString("photo2_full");
                photosURLs[7]=json.getString("photo3_full");
                photosURLs[8]=json.getString("photo4_full");
                photosURLs[9]=json.getString("avatar_full");

                sPref = getSharedPreferences("user", MODE_PRIVATE); //Сохраняем ID юзера, для доступа в профиле
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("avatar_link", photosURLs[4]);
                ed.commit();
            } catch (JSONException e) {
                Log.e("saveToken", e.toString());
            }
        }
    }
    private class sendAdditionalPhoto extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            String responseString;
            JSONObject json = null;
            try
            {
                HttpClient client = new DefaultHttpClient();
                File file = new File(pathToUserPhoto);
                HttpPost post = new HttpPost(URL);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addBinaryBody("file", file, ContentType.create("image/jpeg"), file.getName());
                entityBuilder.addTextBody("action", "photo_set");
                entityBuilder.addTextBody("token", token);
                entityBuilder.addTextBody("position", Integer.toString(photo_type));
// add more key/value pairs here as needed
                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                final HttpEntity httpEntity = response.getEntity();
//Log.v("result", EntityUtils.toString(httpEntity));
                responseString = EntityUtils.toString(httpEntity);
                json = new JSONObject(responseString);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return json;
        }
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
                    Toast.makeText(getApplicationContext(), "Изображение успешно добавлено на сервер!", Toast.LENGTH_LONG).show();
                    new reloadUserData().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при добавлении изображения на сервер!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    View.OnClickListener showSaveButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveProfile.setVisibility(View.VISIBLE);
        }
    };

    AdapterView.OnItemClickListener showSaveButton2 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView view, View view2, int pos, long lng){
            saveProfile.setVisibility(View.VISIBLE);
        }
    };

    View.OnTouchListener showSaveButton3 = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view2,MotionEvent event){
            saveProfile.setVisibility(View.VISIBLE);
            return false;
        }
    };

    private PopupWindow pwindo;
    private void initiatePopupWindow(final int arrayId, TextView targetTV, int[] targetArray, final int selector) {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//View view = inflater.inflate(R.layout.fragment_blank, container, false);
            View layout = inflater.inflate(R.layout.popup_multiple_choose, null, false);
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metricsB = new DisplayMetrics();
            display.getMetrics(metricsB);
            int window_width = metricsB.widthPixels;
            int window_height = metricsB.heightPixels;
            pwindo = new PopupWindow(layout, window_width, window_height, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            //pwindo.setOutsideTouchable(false);
            pwindo.setFocusable(true);
            //pwindo.showAsDropDown(layout, 50, window_height);

            pwindo.setBackgroundDrawable(new BitmapDrawable(pwindo.getContentView().getResources(), (Bitmap) null));
            pwindo.setOutsideTouchable(true);
            pwindo.showAsDropDown(layout, 50, window_height);



            pwindo.setBackgroundDrawable(new BitmapDrawable());

            View popUpWindowLaout = pwindo.getContentView();
            popUpWindowLaout.setFocusableInTouchMode(true);

            //first press doesnt get caught here
            popUpWindowLaout.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {

                        pwindo.dismiss();
                        return true;
                    }
                    return startService;
                }
            });


            final ListView choiceList = (ListView)layout.findViewById(R.id.lvChoiceList);
            LinearLayout.LayoutParams myParams= new LinearLayout.LayoutParams(window_width/3*2, window_height/3*2);
            myParams.gravity= Gravity.CENTER;
            //pwindo.
            choiceList.setLayoutParams(myParams);
            String[] stringsArr2 = getResources().getStringArray(arrayId);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice, stringsArr2);
            choiceList.setAdapter(adapter);
            int[] myTarget = targetArray;
            TextView myTv=targetTV;
            if(selector==0){
                for(int i=0; i<5; i++){
                    selectedHere[i]=-1;
                }
            }else if(selector==1){
                for(int i=0; i<5; i++){
                    selectedHobbies[i]=-1;
                }
            }
            k=0;
            choiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    k = 0;
                    if(choiceList.getCheckedItemCount()>5){
                        choiceList.setItemChecked(position, false);
                    }
                    if (selector == 0) {
                        for (int i = 0; i < 5; i++) {
                            if(selectedHere[i]==position){
                                selectedHere[i]=-1;
                                break;
                            }
                            if (selectedHere[i] == -1) {
                                selectedHere[i] = position;
                                break;
                            }
                        }
                    } else if (selector == 1) {
                        for (int i = 0; i < 5; i++) {
                            if(selectedHobbies[i]==position){
                                selectedHobbies[i]=-1;
                                break;
                            }
                            if (selectedHobbies[i] == -1) {
                                selectedHobbies[i] = position;
                                break;
                            }
                        }
                    }
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] stringsArr = getResources().getStringArray(arrayId);
                    if(selector==0){
                        String str="";
                        for(int i=0; i<5; i++){
                            if(selectedHere[i]>0){
                                str=str+stringsArr[selectedHere[i]]+",";
                            }else{
                                break;
                            }
                        }
                        if(str.length()>1) {
                            str = str.substring(0, str.length() - 1);
                        }
                        hereForTv.setText(str);
/*for(int i=0; i<5; i++){
int tmp = selectedHere[i];
hereForTv.setText("");
if (tmp > -1) {
hereForTv.setText(hereForTv.getText()+", "+stringsArr[tmp]);
//hereForTv.append(", " + stringsArr[tmp]);
}
}*/
                    }else if(selector==1){
                        String str="";
                        for(int i=0; i<5; i++){
                            if(selectedHobbies[i]>0){
                                str=str+stringsArr[selectedHobbies[i]]+",";
                            }else{
                                break;
                            }
                        }
                        if(str.length()>1) {
                            str = str.substring(0, str.length() - 1);
                        }
                        hobbiesTv.setText(str);
/*for(int i=0; i<5; i++){
int tmp = selectedHobbies[i];
hobbiesTv.setText("");
if (tmp > -1) {
hereForTv.setText(hereForTv.getText()+", "+stringsArr[tmp]);
//hobbiesTv.append(", " + stringsArr[tmp]);
}
}*/
                    }
/*for (int i = 0; i < 5; i++) {
int tmp = myTarget[i];
if (tmp > -1) {
myTv.setText(stringsArr[tmp]);
}
}*/
                    pwindo.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringFromArray(String arrayPositions, int arrayId){
        String[] stringsArr = getResources().getStringArray(arrayId);
        String str="";
        String[] strArr=arrayPositions.split(",");
/*StringTokenizer strTok=new StringTokenizer(arrayPositions, ",");
while(strTok.hasMoreTokens()==true){
str=str+stringsArr[Integer.parseInt(strTok.nextToken())]+" ";
}*/
        for(int i=0; i<5; i++){
            if(Integer.parseInt(strArr[i])>0){
                str=str+stringsArr[Integer.parseInt(strArr[i])]+",";
            }else{
                break;
            }
        }
        if(str.length()>1) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private class MyCustomAdapter3 extends ArrayAdapter<String> {

        public MyCustomAdapter3(Context context, int textViewResourceId,
                                String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.sex)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_without_arrows, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tvWA);
            label.setText(getResources().getStringArray(R.array.sex)[position]);
            return row;
        }
    }

    private class GeoAdapter extends ArrayAdapter<String> {
        public GeoAdapter(Context context, int textViewResourceId,
                                String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_without_bg, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tvWB);
            label.setText(getResources().getStringArray(R.array.regions)[position]);
            return row;
        }
    }

    private class SpAdapter extends ArrayAdapter<String> {
        public SpAdapter(Context context, int textViewResourceId,
                          String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(R.array.family)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_without_bg, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tvWB);
            label.setText(getResources().getStringArray(R.array.family)[position]);
            return row;
        }
    }
    private class GeoAdapter2 extends ArrayAdapter<String> {
        public GeoAdapter2(Context context, int textViewResourceId,
                          String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.dropdown_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView35);
            label.setText(getResources().getStringArray(currentCities)[position]);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // TODO Auto-generated method stub
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_without_bg, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tvWB);
            label.setText(getResources().getStringArray(currentCities)[position]);
            return row;
        }
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            datePick.setText(myDay + "/" + myMonth + "/" + myYear);
        }
    };
}