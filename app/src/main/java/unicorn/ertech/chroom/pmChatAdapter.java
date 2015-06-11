package unicorn.ertech.chroom;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
        final pmChatMessage p = chat.get(position);
        if(convertView == null)
        {
            holder = new chatHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pm_chat_layout, parent, false);

            holder.tvMsg = (TextView) convertView.findViewById(R.id.message_text);
            holder.tvTime = (TextView) convertView.findViewById(R.id.timeC2);
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
        holder.tvMsg.setText(SmileManager.getSmiledText(getContext(), p.message));


        holder.tvMsg.setMaxWidth((int)test);
        holder.tvMsg.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        /*if (k.length() == 1)
        {
            holder.tvMsg.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        }
        else
        {
            holder.tvMsg.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }*/
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.Ll.getLayoutParams();
        FrameLayout.LayoutParams tp = (FrameLayout.LayoutParams) holder.tvTime.getLayoutParams();

        if("0".equals(p.direction)){
            holder.Ll.setBackgroundResource(R.drawable.m11);
            lp.gravity = Gravity.RIGHT;
            tp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

        } else{
            holder.Ll.setBackgroundResource(R.drawable.e1);
            lp.gravity = Gravity.LEFT;
            tp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        }


        Calendar currTime = Calendar.getInstance();
        int cday = currTime.get(currTime.DAY_OF_MONTH);

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
        dateTime[0] = date[1] + " - " + date[2];
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

        holder.tvMsg.setPadding(15, 15, 15, 15);
        holder.tvMsg.setText(SmileManager.getSmiledText(getContext(), p.message));

//        View.OnClickListener photoClick = new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                switch (v.getId()){
//                    case R.id.ivAttachPm:
//                        if(!p.attach[0].equals("")) {
//                            Intent photoIntent = new Intent(context, PhotoViewerPm.class);
//                            photoIntent.putExtra("photos", p.attach);
//                            photoIntent.putExtra("id", 0);
//                            photoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(photoIntent);
//                        }
//                        break;
//                    case R.id.ivAttachPm2:
//                        if(!p.attach[1].equals("")) {
//                            Intent photoIntent2 = new Intent(context, PhotoViewerPm.class);
//                            photoIntent2.putExtra("photos", p.attach);
//                            photoIntent2.putExtra("id", 1);
//                            photoIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(photoIntent2);
//                        }
//                        break;
//                    case R.id.ivAttachPm3:
//                        if(!p.attach[2].equals("")) {
//                            Intent photoIntent3 = new Intent(context, PhotoViewerPm.class);
//                            photoIntent3.putExtra("photos", p.attach);
//                            photoIntent3.putExtra("id", 2);
//                            photoIntent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(photoIntent3);
//                        }
//                        break;
//                    case R.id.ivAttachPm4:
//                        if(!p.attach[3].equals("")){
//                            Intent photoIntent4 = new Intent(context, PhotoViewerPm.class);
//                            photoIntent4.putExtra("photos", p.attach);
//                            photoIntent4.putExtra("id", 3);
//                            photoIntent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(photoIntent4);
//                        }
//                        break;
//                    case R.id.ivAttachPm5:
//                        if(!p.attach[4].equals("")){
//                            Intent photoIntent5 = new Intent(context, PhotoViewerPm.class);
//                            photoIntent5.putExtra("photos", p.attach);
//                            photoIntent5.putExtra("id", 4);
//                            photoIntent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(photoIntent5);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };

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

//                holder.imgAttached2.setOnClickListener(photoClick);
//                holder.imgAttached.setOnClickListener(photoClick);
                break;
            case 2:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*123);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*123);

//                holder.imgAttached2.setOnClickListener(photoClick);
//                holder.imgAttached.setOnClickListener(photoClick);
//                holder.imgAttached3.setOnClickListener(photoClick);
                break;
            case 3:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*82);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*82);
                holder.imgAttached4.getLayoutParams().width=(int)(dimensionScale*82);

//                holder.imgAttached2.setOnClickListener(photoClick);
//                holder.imgAttached.setOnClickListener(photoClick);
//                holder.imgAttached3.setOnClickListener(photoClick);
//                holder.imgAttached4.setOnClickListener(photoClick);
                break;
            case 4:
                holder.imgAttached2.getLayoutParams().height=(int)(dimensionScale*125);
                holder.imgAttached2.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached3.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached4.getLayoutParams().width=(int)(dimensionScale*61);
                holder.imgAttached5.getLayoutParams().width=(int)(dimensionScale*61);

//                holder.imgAttached2.setOnClickListener(photoClick);
//                holder.imgAttached.setOnClickListener(photoClick);
//                holder.imgAttached4.setOnClickListener(photoClick);
//                holder.imgAttached3.setOnClickListener(photoClick);
//                holder.imgAttached5.setOnClickListener(photoClick);
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
//            holder.imgAttached.setOnClickListener(photoClick);
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
}