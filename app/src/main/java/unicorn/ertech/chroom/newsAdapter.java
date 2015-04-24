package unicorn.ertech.chroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Timur on 04.02.2015.
 */
public class newsAdapter extends ArrayAdapter<newsItem> {
    private List<newsItem> news;
    private Context context;

    public newsAdapter(List<newsItem> news, Context ctx) {
        super(ctx, R.layout.list_item2, news);
        this.news = news;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        newsHolder holder = new newsHolder();
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item2, null);
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.layOut);

            // Now we can fill the layout with the right values
            TextView title = (TextView) v.findViewById(R.id.textTitle);
            TextView description = (TextView) v.findViewById(R.id.textLink);
            ImageView image = (ImageView)v.findViewById(R.id.img);

            if(position == 0) {
                title.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                image.setVisibility(View.GONE);

                AdView adView = new AdView(context);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId("ca-app-pub-4930517446192586/9664058155");

                AdRequest adRequest = new AdRequest.Builder().build();
                layout.setPadding(0, 20, 0, 20);
                layout.addView(adView);
                adView.loadAd(adRequest);
            }

            holder.tvTitle = title;
            holder.tvDesription = description;
            holder.img = image;

            v.setTag(holder);
        }
        else {
            holder = (newsHolder) v.getTag();
        }
        /*chatMessage p = chat.get(position);
        holder.tvFrom.setText(p.getFrom());
        holder.tvMsg.setText(getSmiledText(getContext(),p.getMessage()));
        Picasso.with(getContext()).load(p.getPicURL()).transform(new PicassoRoundTransformation()).fit().into(holder.img);*/
        newsItem n = news.get(position);
        holder.tvTitle.setText(n.getTitle());
        holder.tvDesription.setText(n.getDescription());
        Picasso.with(getContext()).load(n.getPicURL()).transform(new PicassoRoundTransformation()).fit().into(holder.img);

        return v;
    }
}
