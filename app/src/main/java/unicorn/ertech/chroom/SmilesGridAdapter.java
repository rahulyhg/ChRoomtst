package unicorn.ertech.chroom;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Azat on 19.05.15.
 */
public class SmilesGridAdapter extends BaseAdapter{

    private ArrayList<Integer> paths;
    private int pageNumber;
    Context mContext;

    KeyClickListener mListener;

    public SmilesGridAdapter(Context context, ArrayList<Integer> paths, int pageNumber, KeyClickListener listener) {
        this.mContext = context;
        this.paths = paths;
        this.pageNumber = pageNumber;
        this.mListener = listener;
    }
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.smile_item, null);
        }

        final Integer path = paths.get(position);

        ImageView image = (ImageView) v.findViewById(R.id.item);
        image.setImageBitmap(getImage(path));

        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.keyClickedIndex(path);
            }
        });

        return v;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Integer getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPageNumber () {
        return pageNumber;
    }

    private Bitmap getImage (Integer path) {

        Bitmap temp = BitmapFactory.decodeResource(mContext.getResources(), path);
        return temp;
    }

    public interface KeyClickListener {

        public void keyClickedIndex(Integer index);
    }
}
