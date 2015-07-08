package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Timur on 11.01.2015.
 */
public class GeoLocationTab extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener{
    TextView incognitoTitle;
    final String SAVED_COLOR = "color";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    String URL = "http://im.topufa.org/index.php";

    MapFragment map;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationManager mLocationManager;
    Timer geoSearchTimer;
    boolean canSearch, toDraw, firstTime = true;
    float maxZoom, nowZoom;
    private GoogleMap googleMap;
    private Marker pointMarker;
    Context context;

    ImageView checkButton, zoomButton;
    static String token;
    LatLng ll, checkInLL;
    long time1, time2;
    String longitude, latitude, time_checkin = "360";

    private ArrayList<CustomMarker> arrayMarker = new ArrayList<CustomMarker>();

    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    @Override
    public void onBackPressed() {

        Calendar calend = Calendar.getInstance();
        if(time1==0){
            time1=calend.getTimeInMillis();
            Toast.makeText(getApplicationContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
        }else{
            time2=calend.getTimeInMillis();
            if(time2-time1<=2000){
                finish();
            }else{
                time1=time2;
                Toast.makeText(getApplicationContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_incognito);

//        Analytics

        analytics = GoogleAnalytics.getInstance(this);

        tracker = analytics.newTracker(R.xml.tracker_config);
        tracker.enableAdvertisingIdCollection(true);

//        Analytics

        setContext(this);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);


        SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
        if((userData.contains("token"))){
            if(!userData.getString("token", "0").equals("0")){
                token=userData.getString("token", "");
            }
        }

        incognitoTitle=(TextView)findViewById(R.id.tvIncognitoTitle);

        map = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        googleMap = map.getMap();
        googleMap.setOnMarkerClickListener(this);
        if (googleMap.getUiSettings().isMapToolbarEnabled()) googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (googleMap.getUiSettings().isRotateGesturesEnabled()) googleMap.getUiSettings().setRotateGesturesEnabled(false);
        if (googleMap.getUiSettings().isCompassEnabled()) googleMap.getUiSettings().setCompassEnabled(false);
        map.getMapAsync(this);
        checkButton = (ImageView)findViewById(R.id.checkButton);
        zoomButton = (ImageView)findViewById(R.id.zoomButton);
        if (checkPlayServices()) {
            Log.e("GeoLocation", "Service enable");
            buildGoogleApiClient();
        }

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    checkInLL = displayLocation();

                    if (checkInLL == null) return;
                } else {
                    showDialog(context);
                    return;
                }



                showDialogCheckIn(context);
            }
        });

        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    ll = displayLocation();
//                ll = new LatLng(lat, lng);
                    if (ll == null) return;
                } else {
                    showDialog(context);
                    return;
                }

                Log.e("GeoLocation", "Zoom " + nowZoom);
                nowZoom += 3;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ll)
                    .zoom(nowZoom)
                    .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                if (nowZoom >= maxZoom){
                    nowZoom = 12;
                }
            }
        });

        toDraw = false;
        canSearch = false;
        geoSearchTimer = new Timer();
        geoSearchTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if(canSearch) {
                    if (isNetworkAvailable()) {
                        new GeoSearch().execute();

                        if (toDraw){
                            Log.e("GeoLocation", "Условие выполнено");
                            new HandlerImage().execute(arrayMarker);
                        }
                    }
                }
            }
        }, 0, 2L * 1000);

        autoCheckin();
    }

    private void autoCheckin() {

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            checkInLL = displayLocation();

            if (checkInLL == null) return;
        } else {
            return;
        }

        new GeoCheckin().execute();
    }

    private void setContext(Context context){
        this.context = context;
    }

    private void drawAvatar() {

        Log.e("GeoLocation", "Рисуем аватарки");
        Log.e("GeoLocation", "Количество аватарок = " + arrayMarker.size());

        LatLng checkLL;

        googleMap.clear();

        Marker checkMarker;

        for(int i = 0; i < arrayMarker.size(); i++) {

            double longitude = Double.parseDouble(arrayMarker.get(i).getLongitude());
            double latitude = Double.parseDouble(arrayMarker.get(i).getLatitude());
            Log.e("GeoLocation", "Координаты пользователя = " + longitude + "  " + latitude);
            checkLL = new LatLng(latitude, longitude);

            checkMarker = googleMap.addMarker(new MarkerOptions()
                    .position(checkLL)
                    .icon(BitmapDescriptorFactory.fromBitmap(arrayMarker.get(i).getUserImg()))
                    .flat(false));
            arrayMarker.get(i).setUMarker(checkMarker);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("GeoLocation_STATUS", "onStart");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("GeoLocation_STATUS", "onStop");
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("GeoLocation_STATUS", "onDestroy");
        geoSearchTimer.cancel();
    }

    private LatLng displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.e("GeoLocation", "Возвращает " + latitude + " " + longitude);
            return new LatLng(latitude, longitude);
        } else {
            Log.e("GeoLocation", "Возвращает null");
            return null;
        }
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Мы не можем найти Вас")
                .setMessage("Включите службу определения местоположения(GPS)")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDialogDebug(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Debug")
                .setMessage(msg)
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
        alert.cancel();
    }

    private void showDialogCheckIn(Context context){
        final String[] time_arr = {"1 час", "3 часа", "6 часов"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Указать местоположение на");
        builder.setItems(time_arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0: time_checkin = "60";
                        break;
                    case 1: time_checkin = "180";
                        break;
                    case 2: time_checkin = "360";
                        break;
                }

                new GeoCheckin().execute();
            }
        });
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("GeoLocation_STATUS", "onResume");
        incognitoTitle.setBackgroundResource(R.color.izum_blue);
        canSearch = true;
        showDialogDebug(this, "");
        time1=0; time2=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("GeoLocation_STATUS", "onPause");
        canSearch = false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("GeoLocation_STATUS", "onConnected");
        maxZoom = googleMap.getMaxZoomLevel();
        nowZoom = 12;
        ll = displayLocation();
        if (ll == null) return;
        if (firstTime) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ll)
                    .zoom(nowZoom)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            firstTime = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("GeoLocation", "onMarkerClick");
        for (int i = 0; i<arrayMarker.size(); i++) {


            if (arrayMarker.get(i).getUMarker().getId().equals(marker.getId())) {
                String UserId = arrayMarker.get(i).getUserId();
                String nick = arrayMarker.get(i).getName();
                Intent intent = new Intent(this,Profile2.class);
                intent.putExtra("userId",UserId);
                intent.putExtra("token",token);
                intent.putExtra("nick",nick);
                intent.putExtra("avatar",arrayMarker.get(i).getAvatar());
                intent.putExtra("userPROFILE", UserId);
                startActivity(intent);
            }
        }
        return true;
    }

    private class GeoCheckin extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            longitude = new Double(checkInLL.longitude).toString();
            latitude = new Double(checkInLL.latitude).toString();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "checkin");
            jParser.setParam("longitude", longitude);
            jParser.setParam("latitude", latitude);
            jParser.setParam("time", time_checkin);

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String status = "";
            Object nullObj = null;

            if (json != null){
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if("false".equals(status))
                {
                    boolean success = false;
                    try {
                        success = json.getBoolean("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if("true".equals(success)) {
                        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(sexSpinner.getSelectedItemId()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Зачекинились", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка при совершении поиска!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class GeoSearch extends AsyncTask<String, String, ArrayList<CustomMarker>> {

        String le, loe;

        private ArrayList<CustomMarker> arrayMarkerSearch = new ArrayList<CustomMarker>();

        Object nullObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            le = "55.9811511";
            loe = "54.7508083";
        }

        @Override
        protected ArrayList<CustomMarker> doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            Log.e("GeoLocation", "Выполняется поиск");

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "geosearch");
            jParser.setParam("longitude", loe);
            jParser.setParam("latitude", le);

            JSONObject json = jParser.getJSONFromUrl(URL);

            if (json == null)
                return arrayMarkerSearch;

            String status = "";

            try {
                status = json.getString("error").equals(nullObj) ? "true" : json.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if("false".equals(status))
            {
                String s="";
                Integer num = 0;
                JSONArray arr = null;
                JSONObject checkIn = null;


                try {
                    s = json.getString("data");
                    arr = new JSONArray(s);
                    num = Integer.parseInt(json.getString("total"));
                    Log.e("GeoLocation", "Количество = " + num);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < num; i++)
                {
                    Log.e("GeoLocation", "Выполняется добавление");
                    try {
                        checkIn = new JSONObject(arr.get(i).toString());
//                        Log.e("GeoLocation", arr.get(i).toString());
                        if (checkIn!=null) {
//                            Log.e("GeoLocation", "check!=null");
                            arrayMarkerSearch.add(
                                    new CustomMarker(checkIn.getString("avatar"),
                                            checkIn.getString("name"),
                                            checkIn.getString("latitude"),
                                            checkIn.getString("longitude"),
                                            checkIn.getString("userid")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("GeoLocation", "Количество в массиве " + arrayMarkerSearch.size());
            }
            return arrayMarkerSearch;
        }
        @Override
        protected void onPostExecute(ArrayList<CustomMarker> arrayMarkerSearch) {


            if (!toCompare(arrayMarkerSearch, arrayMarker)){
                Log.e("GeoLocation","Массивы не идентичны");
                arrayMarker = arrayMarkerSearch;
                toDraw = true;
            }

            if(pointMarker != nullObj) pointMarker.remove();

            ll = displayLocation();
//            ll = new LatLng(lat, lng);
            if (ll == null) return;

            pointMarker = googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.point)));
        }
    }

    private boolean toCompare(ArrayList<CustomMarker> arr1, ArrayList<CustomMarker> arr2) {
        int count, userId1, userId2;
        double Longitude1, Latitude1, Longitude2, Latitude2;
        boolean user, lng, lat;
        Log.e("GeoLocation","Arr1.equals(Arr2)");
        for(CustomMarker cm1 : arr1){
            count = 0;
            Longitude1 = Double.parseDouble(cm1.getLongitude());
            Latitude1 = Double.parseDouble(cm1.getLatitude());
            userId1 = Integer.parseInt(cm1.getUserId());
            for (CustomMarker cm2 : arr2){
                Longitude2 = Double.parseDouble(cm2.getLongitude());
                Latitude2 = Double.parseDouble(cm2.getLatitude());
                userId2 = Integer.parseInt(cm2.getUserId());
                Log.e("GeoLocation",cm1.getUserId() + " " + cm2.getUserId());
                Log.e("GeoLocation",Latitude1 + " " + Latitude2);
                Log.e("GeoLocation",Longitude1 + " " + Longitude2);
                if (userId1 == userId2
                        && Latitude1 == Latitude2
                        && Longitude1 == Longitude2){
                    count = 1;
                    Log.e("GeoLocation","count = " + count);
                }
            }
            if (count == 0){
                return false;
            }
        }
        Log.e("GeoLocation","Arr2.equals(Arr1)");
        for(CustomMarker cm1 : arr2){
            Longitude1 = Double.parseDouble(cm1.getLongitude());
            Latitude1 = Double.parseDouble(cm1.getLatitude());
            userId1 = Integer.parseInt(cm1.getUserId());
            count = 0;
            for (CustomMarker cm2 : arr1){
                Longitude2 = Double.parseDouble(cm2.getLongitude());
                Latitude2 = Double.parseDouble(cm2.getLatitude());
                userId2 = Integer.parseInt(cm2.getUserId());
                Log.e("GeoLocation",cm1.getUserId() + " " + cm2.getUserId());
                Log.e("GeoLocation",cm1.getLatitude() + " " + cm2.getLatitude());
                Log.e("GeoLocation",cm1.getLongitude() + " " + cm2.getLongitude());
                if (userId1 == userId2
                        && Latitude1 == Latitude2
                        && Longitude1 == Longitude2){
                    count = 1;
                    Log.e("GeoLocation","count = " + count);
                }
            }
            if (count == 0){
                return false;
            }
        }

        if (arr1.size() != arr2.size()){
            Log.e("GeoLocation","Размеры не равны");
            return false;
        }

        return true;
    }

    private class HandlerImage extends AsyncTask<ArrayList<CustomMarker>, Bitmap, ArrayList<CustomMarker>>{

        LatLng checkLL = null;
        Bitmap img = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        @Override
        protected ArrayList<CustomMarker> doInBackground(ArrayList<CustomMarker>... params) {
            ArrayList<CustomMarker> arrayInBackMarker = params[0];
            Log.e("GeoLocation", "Количество в backgrounde = " + arrayInBackMarker.size());
            for(int i = 0; i < arrayInBackMarker.size(); i++) {

                String url = arrayInBackMarker.get(i).getAvatar();

                if (!"http://im.topufa.org/".equals(url)) {
                    try {
                        img = Picasso.with(getApplicationContext())
                                .load(url)
                                .resize(100, 0)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        int x, y;
                                        int size = Math.min(source.getWidth(), source.getHeight());
                                        x = (source.getWidth() - size) / 2;
                                        y = (source.getHeight() - size) / 2;
                                        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                                        if (squaredBitmap!=source){
                                            source.recycle();
                                        }
                                        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
                                        Canvas canvas = new Canvas(bitmap);
                                        Paint paint = new Paint();
                                        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                        paint.setShader(shader);
                                        paint.setAntiAlias(true);
                                        float radius = size / 2f;
                                        canvas.drawCircle(radius, radius, radius, paint);
                                        paint = new Paint();
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setColor(context.getResources().getColor(R.color.izum_blue));
                                        paint.setStrokeWidth(5);
                                        paint.setAntiAlias(true);
                                        canvas.drawCircle(radius, radius, radius - 2f, paint);
                                        squaredBitmap.recycle();
                                        return bitmap;
                                    }

                                    @Override
                                    public String key() {
                                        return "circle";
                                    }
                                })
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .noFade()
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        img = Picasso.with(getApplicationContext())
                                .load(R.drawable.nophoto)
                                .resize(100, 0)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        int x, y;
                                        int size = Math.min(source.getWidth(), source.getHeight());
                                        x = (source.getWidth() - size) / 2;
                                        y = (source.getHeight() - size) / 2;
                                        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                                        if (squaredBitmap!=source){
                                            source.recycle();
                                        }
                                        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
                                        Canvas canvas = new Canvas(bitmap);
                                        Paint paint = new Paint();
                                        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                                        paint.setShader(shader);
                                        paint.setAntiAlias(true);
                                        float radius = size / 2f;
                                        canvas.drawCircle(radius, radius, radius, paint);
                                        paint = new Paint();
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setColor(context.getResources().getColor(R.color.izum_blue));
                                        paint.setStrokeWidth(5);
                                        paint.setAntiAlias(true);
                                        canvas.drawCircle(radius, radius, radius - 2f, paint);
                                        squaredBitmap.recycle();
                                        return bitmap;
                                    }

                                    @Override
                                    public String key() {
                                        return "circle";
                                    }
                                })
                                .noFade()
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                arrayInBackMarker.get(i).setUserImg(img);
            }
            return arrayInBackMarker;
        }

        @Override
        protected void onPostExecute(ArrayList<CustomMarker> arrayListResult) {
            super.onPostExecute(arrayListResult);
            arrayMarker = arrayListResult;
            drawAvatar();
            toDraw = false;
        }
    }
}