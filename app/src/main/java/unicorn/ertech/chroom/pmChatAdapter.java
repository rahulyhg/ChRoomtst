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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
    private float dimensionScale;
    private double test;
    public pmChatAdapter(List<pmChatMessage> chat, Context ctx) {
        super(ctx, R.layout.pm_chat_layout, chat);
        this.chat = chat;
        this.context = ctx;
        Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.test = display.getWidth()/(1.3);
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        int pic_max=(int)(250*metricsB.density);
        this.dimensionScale=metricsB.density;

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
            holder.imgAttached2=(ImageView)convertView.findViewById(R.id.ivAttachPm2);
            holder.imgAttached3=(ImageView)convertView.findViewById(R.id.ivAttachPm3);
            holder.imgAttached4=(ImageView)convertView.findViewById(R.id.ivAttachPm4);
            holder.imgAttached5=(ImageView)convertView.findViewById(R.id.ivAttachPm5);
            holder.Ll=(LinearLayout)convertView.findViewById(R.id.pm_chat_lay);
            convertView.setTag(holder);
        }
        else
            holder = (chatHolder) v.getTag();
        holder.tvMsg.setText(getSmiledText(getContext(),p.message));

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
        holder.tvMsg.setText(getSmiledText(getContext(), p.message));

        int additionalPhotos=0;
        for(int i=1; i<5; i++){
            if((p.attach[i]!=null)&&(!p.attach[i].equals(""))){
                additionalPhotos++;
            }
        }
        switch (additionalPhotos){
            case 0:
                break;
            case 1:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*250);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*250);
                break;
            case 2:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*123);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*123);
                break;
            case 3:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*82);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*82);
                holder.imgAttached4.getLayoutParams().width=(int)(dimensionScale*82);
                break;
            case 4:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached4.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached5.getLayoutParams().width=(int)(dimensionScale*61);
                break;
            default:
                break;
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.white_holder)
                .delayBeforeLoading(1000)
                .cacheInMemory(true)
                .build();
        if((p.attach[0]!=null)&&(!p.attach[0].equals(""))){
            imageLoader.displayImage(p.attach[0], holder.imgAttached, options);
//            Picasso.with(getContext()).load(p.attach[0]).resize(pic_max,0).placeholder(R.drawable.camera128).into(holder.imgAttached);
            holder.imgAttached.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached.setVisibility(View.GONE);
        }

        if((p.attach[1]!=null) && (!p.attach[1].equals(""))){
            imageLoader.displayImage(p.attach[1], holder.imgAttached2, options);
//            Picasso.with(getContext()).load(p.attach[1]).noFade().resize(pic_max, 0).placeholder(R.drawable.camera128).into(holder.imgAttached2);
            holder.imgAttached2.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached2.setVisibility(View.GONE);
        }

        if((p.attach[2]!=null)&&(!p.attach[2].equals(""))){
            imageLoader.displayImage(p.attach[2], holder.imgAttached3, options);
//            Picasso.with(getContext()).load(p.attach[2]).noFade().resize(pic_max, 0).placeholder(R.drawable.camera128).into(holder.imgAttached3);
            holder.imgAttached3.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached3.setVisibility(View.GONE);
        }

        if((p.attach[3]!=null)&&(!p.attach[3].equals(""))){
            imageLoader.displayImage(p.attach[3], holder.imgAttached4, options);
//            Picasso.with(getContext()).load(p.attach[3]).noFade().resize(pic_max, 0).placeholder(R.drawable.camera128).into(holder.imgAttached4);
            holder.imgAttached4.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached4.setVisibility(View.GONE);
        }

        if((p.attach[4]!=null)&&(!p.attach[4].equals(""))){
            imageLoader.displayImage(p.attach[4], holder.imgAttached5, options);
//            Picasso.with(getContext()).load(p.attach[4]).noFade().resize(pic_max,0).placeholder(R.drawable.camera128).into(holder.imgAttached5);
            holder.imgAttached5.setVisibility(View.VISIBLE);
        }else{
            holder.imgAttached5.setVisibility(View.GONE);
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