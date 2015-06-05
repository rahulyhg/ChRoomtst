package unicorn.ertech.chroom;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by ILDAR on 26.05.2015.
 */
public class CustomMarker {
    private String avatar;
    private String name;
    private String latitude;
    private String longitude;
    private String userId;
    private Bitmap userImg;
    private Marker uMarker;

    public CustomMarker(String avatar, String name, String latitude, String longitude,  String userId){
        this.avatar = avatar;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setUserImg(Bitmap userImg) {
        this.userImg = userImg;
    }

    public void setUMarker(Marker uMarker) {
        this.uMarker = uMarker;
    }

    public Marker getUMarker() {
        return uMarker;
    }

    public String getAvatar(){
        return this.avatar;
    }

    public String getName(){
        return this.name;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public String getLongitude(){
        return this.longitude;
    }

    public String getUserId(){
        return this.userId;
    }

    public Bitmap getUserImg() {
        return userImg;
    }

}
