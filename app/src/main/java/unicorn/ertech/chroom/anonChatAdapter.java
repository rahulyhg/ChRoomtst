package unicorn.ertech.chroom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        holder.tvMsg.setText(getSmiledText(getContext(),p.getMessage()));
        holder.tvFrom.setText(p.from);
        if(p.getSex().equals("1"))//если мужской пол
        {
            //holder.img.setImageDrawable(getContext().getResources().getDrawable(getContext().getResources().getIdentifier("drawable/like_fill", "drawable", getContext().getPackageName())));
            holder.img.setImageResource(R.drawable.man);
        }
        else
        {
            holder.img.setImageResource(R.drawable.women);
            //holder.img.setImageDrawable(getContext().getResources().getDrawable(getContext().getResources().getIdentifier("drawable/kiss_fill", "drawable", getContext().getPackageName())));
        }



        return v;
    }


    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ":)", R.drawable.s01);
        addPattern(emoticons, ":D", R.drawable.s02);
        addPattern(emoticons, ":O", R.drawable.s03);
        addPattern(emoticons, ":(", R.drawable.s04);
        addPattern(emoticons, "*05*", R.drawable.s05);
        addPattern(emoticons, "Z)", R.drawable.s06);
        addPattern(emoticons, "*07*", R.drawable.s07);
        addPattern(emoticons, "*08*", R.drawable.s08);
        addPattern(emoticons, "*09*", R.drawable.s09);
        addPattern(emoticons, "*love*", R.drawable.s10);
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
