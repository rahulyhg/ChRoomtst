package unicorn.ertech.chroom;
import android.app.Application;
import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
            holder.imgAttached=(ImageView)convertView.findViewById(R.id.ivAttachPm);
            holder.Ll=(LinearLayout)convertView.findViewById(R.id.pm_chat_lay);
            convertView.setTag(holder);
        }
        else
            holder = (chatHolder) v.getTag();
        holder.tvMsg.setText(getSmiledText(getContext(),p.message));
        Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        double test = display.getWidth()/(1.3);
        holder.tvMsg.setMaxWidth((int)test);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.Ll.getLayoutParams();
        //LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) holder.imgAttached.getLayoutParams();
        String k = holder.tvMsg.getText().toString();
        holder.tvMsg.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        /*if (k.length() == 1)
        {
            holder.tvMsg.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        }
        else
        {
            holder.tvMsg.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }*/
//Check whether message is mine to show green background and align to right
        if(p.direction.equals("0"))
        {
            //holder.tvMsg.setBackgroundResource(R.drawable.m11);
            holder.Ll.setBackgroundResource(R.drawable.m11);
            lp.gravity = Gravity.RIGHT;
            //lp2.gravity=Gravity.RIGHT;
        }
//If not mine then it is from sender to show orange background and align to left
        else
        {
            //holder.tvMsg.setBackgroundResource(R.drawable.e1);
            holder.Ll.setBackgroundResource(R.drawable.e1);
            lp.gravity = Gravity.LEFT;
            //lp.gravity=Gravity.LEFT;
        }
        holder.tvMsg.setLayoutParams(lp);
        holder.tvMsg.setPadding(15, 15, 15, 15);
        holder.tvMsg.setText(getSmiledText(getContext(),p.message));
        /*DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        int pic_max=(int)(300*metricsB.density);*/
        if(!p.attach.equals("")){
            Picasso.with(getContext()).load(p.attach).noFade().into(holder.imgAttached);
            holder.imgAttached.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached.setVisibility(View.GONE);
        }
        return convertView;
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