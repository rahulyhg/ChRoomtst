package unicorn.ertech.chroom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 31.01.2015.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context mContext;
    private List<sResult> results;

    public SearchResultAdapter(List<sResult> chat, Context ctx) {
        //super(ctx, R.layout.grid_item, chat);
        this.results = chat;
        this.mContext = ctx;
    }

    public int getCount() {
        return results.size();
    }

    public Object getItem(int position) {
        return results.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        chatHolder holder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_item, null);
            holder = new chatHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.ivResultPhoto);
            holder.tvFrom = (TextView)convertView.findViewById(R.id.tvResultName);
            convertView.setTag(holder);
        }
        else {
            holder = (chatHolder)convertView.getTag();
        }

        final sResult entry = results.get(position);

        Picasso.with(mContext).load(entry.picUrl).into(holder.img);
        holder.tvFrom.setText(entry.name);

        holder.img.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, Profile2.class);
                        // passing array index
                        i.putExtra("userId", entry.uid);
                        //i.putExtra("token", Main.str);
                        i.putExtra("avatar", entry.picUrl);
                        mContext.startActivity(i);
                    }
                }
        );

        return convertView;
    }
}
