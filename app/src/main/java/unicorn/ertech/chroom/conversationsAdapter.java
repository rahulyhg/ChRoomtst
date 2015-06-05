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

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ильнур on 28.01.2015.
 */
public class conversationsAdapter extends ArrayAdapter<conversationsMsg> {
    private List<conversationsMsg> chat;
    private Context context;

    public conversationsAdapter(List<conversationsMsg> chat, Context ctx) {
        super(ctx, R.layout.conversations, chat);
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
            v = inflater.inflate(R.layout.conversations, null);

            // Now we can fill the layout with the right values
            TextView from = (TextView) v.findViewById(R.id.fromC2);
            TextView msg = (TextView) v.findViewById(R.id.messagC2);
            TextView time = (TextView) v.findViewById(R.id.timeC2);
            TextView online = (TextView)v.findViewById(R.id.onlineC2);
            ImageView image = (ImageView)v.findViewById(R.id.imgC2);
            RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.conversations_color);

            holder.tvFrom = from;
            holder.tvMsg = msg;
            holder.img = image;
            holder.tvTime = time;
            holder.tvOnline= online;
            holder.Rl = rl;

            v.setTag(holder);
        }
        else {
            holder = (chatHolder) v.getTag();
        }

        if(p.direction.equals("false"))
        {
            holder.Rl.setBackgroundResource(R.color.conversationsReadNot);
        }
        else
        {
           holder.Rl.setBackgroundResource(R.color.conversationsRead);
        }

        holder.tvFrom.setText(p.from);
        holder.tvMsg.setText(SmileManager.getSmiledText(getContext(), p.message));
        holder.tvOnline.setText(p.online);
        Calendar currTime = Calendar.getInstance();
        int cday = currTime.get(currTime.DAY_OF_MONTH);

        //
        //Раскоментить, когда разберёмся со временем
        //

        Calendar msgTime = Calendar.getInstance();
        int cmonth = 1 + currTime.get(currTime.MONTH);
        int cyear = currTime.get(currTime.YEAR);
        String[] dateTime = p.time.split("%");
        String[] time = dateTime[1].split(":");
        String[] date = dateTime[0].split("-");
        dateTime[1]=time[0]+":"+time[1];
        int hour = currTime.get(currTime.HOUR_OF_DAY);
        time[0] = String.valueOf(hour);
        dateTime[1] = time[0]+":"+time[1];
        msgTime.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
        msgTime.set(Calendar.MINUTE,Integer.parseInt(time[1]));int minute = Integer.parseInt(time[1]);
        if(minute<10&&time[1].length()==1)
        {
            time[1]="0"+time[1];
            dateTime[1] = time[0]+":"+time[1];
        }
        msgTime.set(Calendar.SECOND,Integer.parseInt(time[2]));
        msgTime.set(Calendar.YEAR,Integer.parseInt(date[0]));
        int year = Integer.parseInt(date[0]);
        msgTime.set(Calendar.MONTH,Integer.parseInt(date[1]));
        int month = Integer.parseInt(date[1]);if(month<10&&date[1].length()==1){date[1]="0"+date[1];}
        msgTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date[2]));
        int day = Integer.parseInt(date[2]);if(day<10&&date[2].length()==1){date[2]="0"+date[2];}
        dateTime[0]=date[0]+"-"+date[1]+"-"+date[2];

        holder.tvTime.setGravity(Gravity.RIGHT);
        holder.tvMsg.setGravity(Gravity.LEFT);
        if(!date[0].equals("0000")) {
            if (year == cyear) {
                if (month == cmonth) {
                    if (day == cday) {
                        holder.tvTime.setText(dateTime[1]);
                    } else {
                        day++;
                        if (day == cday) {
                            holder.tvTime.setText("Вчера");
                        } else {
                            holder.tvTime.setText(dateTime[0]);
                        }
                    }

                } else {
                    holder.tvTime.setText(dateTime[0]);
                }
            } else {
                holder.tvTime.setText(dateTime[0]);
            }
        }
        else
        {
            holder.tvTime.setText("");
        }
        //Display display = getActivity().getWindowManager().getDefaultDisplay(); //определяем ширину экрана
        //DisplayMetrics metricsB = new DisplayMetrics();
       // display.getMetrics(metricsB);
       // pic_width2=(int)(50*metricsB.density);
        if(p.picURL!=null){
            if(!p.picURL.equals("http://im.topufa.org/")){
                Picasso.with(getContext()).load(p.picURL).resize(100,0).transform(new PicassoRoundTransformation()).into(holder.img);
            }else{
                Picasso.with(getContext()).load(R.drawable.nophoto).resize(100, 0).into(holder.img);
            }
        }else{
            Picasso.with(getContext()).load(R.drawable.nophoto).resize(100, 0).into(holder.img);
        }

        return v;
    }
}
