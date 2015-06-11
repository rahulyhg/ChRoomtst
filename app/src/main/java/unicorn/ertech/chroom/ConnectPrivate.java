package unicorn.ertech.chroom;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ILDAR on 09.06.2015.
 */
public class ConnectPrivate extends ArrayList<HashMap<String, String>> implements Parcelable {

    @Override
    public boolean add(HashMap<String, String> object) {
        return super.add(object);
    }

    @Override
    public HashMap<String, String> remove(int index) {
        return super.remove(index);
    }

    @Override
    public HashMap<String, String> get(int index) {
        return super.get(index);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(super.size());
        dest.writeInt(super.get(0).keySet().size());

        for(int k = 0; k < super.size(); k++)
            for(String s: super.get(k).keySet()){
                dest.writeString(s);
                dest.writeString(super.get(k).get(s));
            }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new ConnectPrivate(source);
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };

    public ConnectPrivate(HashMap<String, String> map){
        super.add(map);
    }

    private ConnectPrivate(Parcel parcel){
        int arrSize = parcel.readInt();
        int count = parcel.readInt();

        for(int k = 0; k < arrSize; k++){
            HashMap<String, String> map = new HashMap<String, String>();
            for(int i = 0; i < count; i++)
                map.put(parcel.readString(), parcel.readString());
            super.add(map);
        }
    }
}
