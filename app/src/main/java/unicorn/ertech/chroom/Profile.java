package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import unicorn.ertech.chroom.PicassoRoundTransformation;


import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * Created by Timur on 08.01.2015.
 */
public class Profile extends Activity {
    SharedPreferences sPref;
    SharedPreferences sPref2;
    public int pic_height;
    public int pic_width;

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
    TextView tvProfStat;
    TextView etName;
    TextView birthDay;
    EditText etProfileAbout;
    EditText etProfileCity;
    Button saveProfile;
    Spinner profileSex, searchSex, hobbiesSpin, hereForSpin;
    int sex, ssex, hob, here;

    Picasso mPicasso;


    final String LIKE_STATE = "like";
    final String KISS_STATE = "kiss";
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);
        //setContentView(R.layout.tab_incognito);
        mPicasso = Picasso.with(getApplicationContext());

        RelativeLayout topRow = (RelativeLayout) findViewById(R.id.topRow);
        Button back = (Button) findViewById(R.id.profileBack);
        profileGlass = (ImageView) findViewById(R.id.profileGlass);
        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);
        birthDay = (TextView)findViewById(R.id.tvBirthday);
        profileSex = (Spinner)findViewById(R.id.spinnerProfileSex);
        saveProfile = (Button) findViewById(R.id.butProfileSave);
        sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        sPref2 = getSharedPreferences("user", MODE_PRIVATE);
        TextView tvProfPhoto = (TextView) findViewById(R.id.tvProfilePhoto);
        TextView tvProfInfo = (TextView) findViewById(R.id.tvProfileInfo);
        etName = (TextView) findViewById(R.id.etName);
        etProfileAbout = (EditText) findViewById(R.id.etProfileAbout);
        etProfileCity = (EditText) findViewById(R.id.etProfileCity);
        tvProfStat = (TextView) findViewById(R.id.tvProfileStatus);

        etName.setOnClickListener(showSaveButton);  //показ кнопки сохранить при редактировании данных о себе
        etProfileCity.setOnClickListener(showSaveButton);
        etProfileAbout.setOnClickListener(showSaveButton);

        Display display = getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        pic_width = metricsB.widthPixels;

        //ClassLoader classLoader = MultipartEntityBuilder.class.getClassLoader();
        //URL resource = classLoader.getResource("org/apache/http/message/BasicHeaderValueFormatter.class");
        //Log.e("profile", resource.toString());

        token = Main.str;
        if (sPref2.contains(USER)) {
            userID = sPref2.getInt(USER, 0);
            if (userID != 0) {
                new loadUserData().execute();
            }
        }
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                topRow.setBackgroundResource(R.color.blue);
                back.setBackgroundResource(R.color.blue);
                tvProfInfo.setBackgroundResource(R.drawable.b_string);
                tvProfPhoto.setBackgroundResource(R.drawable.b_string);
                tvProfStat.setBackgroundResource(R.color.bluelight);
                profileGlass.setBackgroundResource(R.color.blueglass);
            } else if (col == 0) {
                topRow.setBackgroundResource(R.color.green);
                back.setBackgroundResource(R.color.green);
                tvProfInfo.setBackgroundResource(R.drawable.g_strip);
                tvProfPhoto.setBackgroundResource(R.drawable.g_strip);
                tvProfStat.setBackgroundResource(R.color.greenlight);
                profileGlass.setBackgroundResource(R.color.greenglass);
            } else if (col == 2) {
                topRow.setBackgroundResource(R.color.orange);
                back.setBackgroundResource(R.color.orange);
                tvProfInfo.setBackgroundResource(R.drawable.o_strip);
                tvProfPhoto.setBackgroundResource(R.drawable.o_strip);
                tvProfStat.setBackgroundResource(R.color.orangelight);
                profileGlass.setBackgroundResource(R.color.orangeglass);
            } else if (col == 3) {
                topRow.setBackgroundResource(R.color.purple);
                back.setBackgroundResource(R.color.purple);
                tvProfInfo.setBackgroundResource(R.drawable.p_string);
                tvProfPhoto.setBackgroundResource(R.drawable.p_string);
                tvProfStat.setBackgroundResource(R.color.purplelight);
                profileGlass.setBackgroundResource(R.color.purpleglass);
            }
        }

        saveProfile.setVisibility(View.INVISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = profileSex.getSelectedItemPosition();
                ssex=searchSex.getSelectedItemPosition();
                hob=hobbiesSpin.getSelectedItemPosition();
                here=hereForSpin.getSelectedItemPosition();
                new sendUserData().execute();
            }
        });

        profilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        //profilePhoto=(ImageView)etProfileCity.getText();
        smallProfilePhoto = (ImageView) findViewById(R.id.profileSmallPhoto);
        smallProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService = false;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);  //Здесь запускает галерею
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                //saveData(true, PHOTO_STATE);
            }
        });

        if (loadData(PHOTO_STATE)) {
            String tmp_path = GetPath();
            if (!tmp_path.equals("")) {
                iHolder = new ImageHolder();
                iHolder.imageWidth=profilePhoto.getLayoutParams().width;
                //pic_height = profilePhoto.getMaxHeight();
                mPicasso.load(tmp_path).resize(pic_width, 0).noFade().into(profilePhoto);
                //mPicasso.load(tmp_path).centerInside().fit().noFade().into(profilePhoto);
                mPicasso.load(tmp_path).transform(new PicassoRoundTransformation()).fit().noFade().into(smallProfilePhoto);
            }
        }


        Button butBack = (Button) findViewById(R.id.profileBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

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
                    //Bitmap bitmap = decodeSampledBitmapFromResource(selectedImage.getPath(), profilePhoto.getWidth(), profilePhoto.getHeight());
                    /*try {
                        galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }*/
                    //profilePhoto.setImageBitmap(bitmap);
                    mPicasso.load(selectedImage).resize(pic_width, 0).noFade().into(profilePhoto);
                    //mPicasso.load(selectedImage).centerInside().fit().noFade().into(profilePhoto);
                    mPicasso.load(selectedImage).transform(new PicassoRoundTransformation()).fit().noFade().into(smallProfilePhoto);
                    //profilePhoto.setImageURI(selectedImage);
                    SavePath(selectedImage.toString());
                    pathToUserPhoto = getImagePath(selectedImage);
                    startService = true;
                    new sendUserPhoto().execute();
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
                Log.e("profile", json.getString("name"));
                Log.e("profile", json.getString("info"));
                etProfileCity.setText(json.getString("city"));
                userName = json.getString("name");
                userAbout = json.getString("info");
                birthDay.setText(json.getString("age"));
                String sex = json.getString("sex");
                profileSex.setSelection(0);
                Log.e("selectedItem", profileSex.getSelectedItem()+"");
                if(sex.equals("1")){profileSex.setSelection(0);}
                if(sex.equals("0")){profileSex.setSelection(1);}
                Log.e("selectedItem", profileSex.getSelectedItem()+"");
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
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            //ставим нужные нам параметры
            jParser.setParam("action", "profile_set");
            //jParser.setParam("userid", Integer.toString(userID));
            jParser.setParam("token", token);
            jParser.setParam("city", etProfileCity.getText().toString());
            jParser.setParam("info", etProfileAbout.getText().toString());

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
}
