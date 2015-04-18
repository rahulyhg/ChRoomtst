package unicorn.ertech.chroom;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ильнур on 09.01.2015.
 */
public class    chatAdapter extends ArrayAdapter<chatMessage> {

    private List<chatMessage> chat;
    private Context context;

    public chatAdapter(List<chatMessage> chat, Context ctx) {
        super(ctx, R.layout.chat_layout, chat);
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
            holder = (chatHolder) v.getTag();
        }
            chatMessage p = chat.get(position);
            holder.tvFrom.setText(p.getFrom());
            //holder.tvMsg.setText(p.getMessage());
            holder.tvMsg.setText(getSmiledText(getContext(),p.getMessage()));
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
