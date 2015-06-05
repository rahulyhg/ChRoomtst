package unicorn.ertech.chroom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ильнур on 09.01.2015.
 */
public class chatAdapter extends ArrayAdapter<chatMessage> {

    private List<chatMessage> chat;
    private Context context;

    public chatAdapter(List<chatMessage> chat, Context ctx) {
        super(ctx, R.layout.chat_layout, chat);
        this.chat = chat;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        chatHolderGlobal holder = new chatHolderGlobal();
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.chat_layout, null);

            // Now we can fill the layout with the right values
            TextView from = (TextView) v.findViewById(R.id.from);
            TextView msg = (TextView) v.findViewById(R.id.messag);
            ImageView image = (ImageView)v.findViewById(R.id.img);

            holder.tvFrom = from;
            holder.tvMsg = msg;
            holder.img = image;

            v.setTag(holder);
        }
        else {
            holder = (chatHolderGlobal) v.getTag();
        }
            chatMessage p = chat.get(position);
            holder.tvFrom.setText(p.getFrom());
            //holder.tvMsg.setText(p.getMessage());
            holder.tvMsg.setText(SmileManager.getSmiledText(getContext(), p.getMessage()));
            /*int width=100;
            if(GlobalChat.photoWidth>0) {
                width = GlobalChat.photoWidth;
            }*/
            if(!p.getPicURL().equals("http://im.topufa.org/")) {
                Picasso.with(getContext()).load(p.getPicURL()).resize(100, 0).transform(new PicassoRoundTransformation()).noFade().into(holder.img);
                Log.i(holder.tvFrom.toString(), p.getPicURL());
            }else{
                Picasso.with(getContext()).load(R.drawable.nophoto).resize(100, 0).into(holder.img);
            }
            return v;
    }
}
