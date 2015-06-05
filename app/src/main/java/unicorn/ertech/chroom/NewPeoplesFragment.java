package unicorn.ertech.chroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 04.01.2015.
 */
public class NewPeoplesFragment extends Fragment {

    private Context context;
    GridView gvNewPeople;
    PageAdapter adapter;
    List<sResult> peopleList = new ArrayList<sResult>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.new_people, container, false);

        context = v.getContext();
        adapter = new PageAdapter(peopleList, context);
        gvNewPeople = (GridView)v.findViewById(R.id.gvNewPeoplesToday);
        gvNewPeople.setVisibility(View.VISIBLE);
        gvNewPeople.setNumColumns(3);

        gvNewPeople.setAdapter(adapter);

        return v;
    }

    public class PageAdapter extends BaseAdapter{

        private Context context;
        private List<sResult> list;

        public PageAdapter(List<sResult> list, Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            chatHolder holder = null;

            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.grid_item, null);
                holder = new chatHolder();
                holder.img = (ImageView)convertView.findViewById(R.id.ivResultPhoto);
                holder.tvFrom = (TextView)convertView.findViewById(R.id.tvResultName);
                convertView.setTag(holder);
            }
            else {
                holder = (chatHolder)convertView.getTag();
            }

            final sResult entry = list.get(position);

            if(!entry.picUrl.equals("http://im.topufa.org/")) {
                Picasso.with(context).load(entry.picUrl).into(holder.img);
            }else{
                Picasso.with(context).load(R.drawable.nophoto).resize(100, 0).into(holder.img);
            }
            holder.tvFrom.setText(entry.name);

            holder.img.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, Profile2.class);
                            i.putExtra("userId", entry.uid);
                            i.putExtra("avatar", entry.picUrl);
                            context.startActivity(i);
                        }
                    }
            );

            return convertView;
        }
    }
}
