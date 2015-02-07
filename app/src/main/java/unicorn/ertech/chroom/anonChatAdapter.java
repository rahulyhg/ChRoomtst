package unicorn.ertech.chroom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ильнур on 19.01.2015.
 */
public class anonChatAdapter extends ArrayAdapter<anonChat> {
    private List<anonChat> chat;
    private Context context;

    public anonChatAdapter(List<anonChat> chat, Context ctx) {
        super(ctx, R.layout.anon_chat_layout, chat);
        this.chat = chat;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        chatHolder holder = new chatHolder();
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.anon_chat_layout, null);

            // Now we can fill the layout with the right values
            TextView msg = (TextView) v.findViewById(R.id.messagAnon);
            TextView from = (TextView) v.findViewById(R.id.fromAnon);
            ImageView image = (ImageView)v.findViewById(R.id.imgSex);


            holder.tvMsg = msg;
            holder.img = image;
            holder.tvFrom = from;

            v.setTag(holder);
        }
        else {
            holder = (chatHolder) v.getTag();
        }
        anonChat p = chat.get(position);
        holder.tvMsg.setText(p.getMessage());
        holder.tvFrom.setText(p.from);
        if(p.getSex().equals("1"))//если мужской пол
        {
            //holder.img.setImageDrawable(getContext().getResources().getDrawable(getContext().getResources().getIdentifier("drawable/like_fill", "drawable", getContext().getPackageName())));
            holder.img.setImageResource(R.drawable.like_fill);
        }
        else
        {
            holder.img.setImageResource(R.drawable.kiss_fill);
            //holder.img.setImageDrawable(getContext().getResources().getDrawable(getContext().getResources().getIdentifier("drawable/kiss_fill", "drawable", getContext().getPackageName())));
        }



        return v;
    }
}
