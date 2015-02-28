package unicorn.ertech.chroom;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ильнур on 26.01.2015.
 */
public class pmChatAdapter extends ArrayAdapter<pmChatMessage> {

    private List<pmChatMessage> chat;
    private Context context;

    public pmChatAdapter(List<pmChatMessage> chat, Context ctx) {
        super(ctx, R.layout.pm_chat_layout, chat);
        this.chat = chat;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        chatHolder holder;
        pmChatMessage p = chat.get(position);
        if(convertView == null)
        {
            holder = new chatHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pm_chat_layout, parent, false);
            holder.tvMsg = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        }
        else
            holder = (chatHolder) v.getTag();

        holder.tvMsg.setText(p.message);

        Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        double test = display.getWidth()/(1.3);
        holder.tvMsg.setMaxWidth((int)test);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvMsg.getLayoutParams();

        String k = holder.tvMsg.getText().toString();
        if (k.length() == 1)
        {
            holder.tvMsg.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        }
        else
        {
            holder.tvMsg.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        //Check whether message is mine to show green background and align to right
        if(p.direction.equals("0"))
        {
            holder.tvMsg.setBackgroundResource(R.drawable.m11);
            lp.gravity = Gravity.RIGHT;
        }
        //If not mine then it is from sender to show orange background and align to left
        else
        {
            holder.tvMsg.setBackgroundResource(R.drawable.e1);
            lp.gravity = Gravity.LEFT;
        }
        holder.tvMsg.setLayoutParams(lp);

        holder.tvMsg.setPadding(15, 15, 15, 15);
        holder.tvMsg.setText("" + p.message + "");

        return convertView;
    }
}
