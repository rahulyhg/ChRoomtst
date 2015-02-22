package unicorn.ertech.chroom;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.RelativeLayout.LayoutParams;

/**
 * Created by Ильнур on 28.01.2015.
 */
public class conversationsAdapter extends ArrayAdapter<conversationsMsg> {
    private List<conversationsMsg> chat;
    private Context context;

    public conversationsAdapter(List<conversationsMsg> chat, Context ctx) {
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
            RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.layout_color);

            holder.tvFrom = from;
            holder.tvMsg = msg;
            holder.img = image;
            holder.tvTime = time;
            holder.Rl = rl;

            v.setTag(holder);
        }
        else {
            holder = (chatHolder) v.getTag();
        }

        if(p.direction.equals("1"))
        {
            holder.Rl.setBackgroundResource(R.color.conversationsGrey);
        }
        else
        {
            holder.Rl.setBackgroundResource(R.color.conversationsWhite);
        }

        holder.tvFrom.setText(p.from);
        holder.tvMsg.setText(p.message);
        Calendar currTime = Calendar.getInstance();int cday = currTime.get(currTime.DAY_OF_MONTH);
        Calendar msgTime = Calendar.getInstance(); int cmonth = 1 + currTime.get(currTime.MONTH); int cyear = currTime.get(currTime.YEAR);
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
        msgTime.set(Calendar.YEAR,Integer.parseInt(date[0])); int year = Integer.parseInt(date[0]);
        msgTime.set(Calendar.MONTH,Integer.parseInt(date[1])); int month = Integer.parseInt(date[1]);if(month<10&&date[1].length()==1){date[1]="0"+date[1];}
        msgTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date[2])); int day = Integer.parseInt(date[2]);if(day<10&&date[2].length()==1){date[2]="0"+date[2];}
        dateTime[0]=date[0]+"-"+date[1]+"-"+date[2];

        holder.tvTime.setGravity(Gravity.RIGHT);
        holder.tvMsg.setGravity(Gravity.LEFT);
        if(year == cyear)
        {
            if(month == cmonth)
            {
                if(day == cday)
                {
                    holder.tvTime.setText(dateTime[1]);
                }
                else
                {
                    day++;
                    if(day == cday)
                    {
                        holder.tvTime.setText("Вчера");
                    }
                    else
                    {
                        holder.tvTime.setText(dateTime[0]);
                    }
                }

            }
            else
            {
                holder.tvTime.setText(dateTime[0]);
            }
        }
        else
        {
            holder.tvTime.setText(dateTime[0]);
        }

        Picasso.with(getContext()).load(p.picURL).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new PicassoRoundTransformation()).fit().into(holder.img);


        return v;
    }

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ":)", R.drawable.kiss_fill);
        addPattern(emoticons, ":-)", R.drawable.like_fill);
        // ...
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }
}
