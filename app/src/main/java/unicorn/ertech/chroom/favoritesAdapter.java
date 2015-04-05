package unicorn.ertech.chroom;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ильнур on 17.02.2015.
 */
public class favoritesAdapter extends ArrayAdapter<conversationsMsg> {
private List<conversationsMsg> chat;
private Context context;

public favoritesAdapter(List<conversationsMsg> chat, Context ctx) {
        super(ctx, R.layout.chat_layout, chat);
        this.chat = chat;
        this.context = ctx;
        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        chatHolder holder = new chatHolder();
        conversationsMsg p = chat.get(position);
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.conversation_msg_layout, null);

            // Now we can fill the layout with the right values
            TextView from = (TextView) v.findViewById(R.id.fromC);
            TextView msg = (TextView) v.findViewById(R.id.messagC);
            TextView time = (TextView) v.findViewById(R.id.timeC);
            ImageView image = (ImageView)v.findViewById(R.id.imgC);
            TextView online = (TextView)v.findViewById(R.id.onlineC);
            RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.layout_color);

            holder.tvFrom = from;
            holder.tvMsg = msg;
            holder.img = image;
            holder.tvTime = time;
            holder.tvOnline=online;
            //holder.Rl = rl;

            v.setTag(holder);
        }
        else {
            holder = (chatHolder) v.getTag();
        }

        if(p.direction.equals("1"))
        {
            //holder.Rl.setBackgroundResource(R.color.grey);
        }
        else
        {
            //holder.Rl.setBackgroundResource(R.color.white);
        }

        holder.tvFrom.setText(p.from);
        holder.tvOnline.setText(p.online);
        holder.tvMsg.setText(p.message);

        holder.tvMsg.setGravity(Gravity.LEFT);


        Picasso.with(getContext()).load(p.picURL).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new PicassoRoundTransformation()).fit().into(holder.img);


        return v;
    }
}
