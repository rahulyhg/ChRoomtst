package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Timur on 11.01.2015.
 */
public class SearchMain extends Fragment {
    private Context context;
    ImageView ivRandom;
    final int PICK_RESULT=1;
    ImageView ivParam;
    ImageView ivContacts;
    ImageView ivSoc;
    final String SAVED_COLOR = "color";
    ImageView ivInvite;
    SharedPreferences sPref;
    TextView tvSearchTitle;
    private UiLifecycleHelper uiHelper;
    String URL = "http://im.topufa.org/index.php";



    /** Handle the results from the voice recognition activity. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.search_random, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        ivRandom=(ImageView)view.findViewById(R.id.ivSearchRand);
        ivParam = (ImageView)view.findViewById(R.id.ivSearchParam);
        ivContacts = (ImageView)view.findViewById(R.id.ivSearchCont);
        ivInvite = (ImageView)view.findViewById(R.id.ivSearchInvite);
        ivSoc = (ImageView)view.findViewById(R.id.ivSearchSoc);
        tvSearchTitle = (TextView)view.findViewById(R.id.tvSearchTitle);

        setColor();

        ivContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(pickIntent, PICK_RESULT);*/

                Intent m = new Intent(context, VActivity.class);
                startActivity(m);
            }
        });

        ivInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("sms_body", "Привет! Заходи в ChatRoom!");
                startActivity(smsIntent);*/

                Intent i = new Intent(context, syncContacts.class);
                startActivity(i);
            }
        });
        ivRandom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Search search_parent = (Search)getActivity();
                search_parent.startRandom();
            }
        });
        ivParam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Search search_parent = (Search)getActivity();
                search_parent.startParam();
            }
        });
        ivSoc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //publishFeedDialog();
                Search search_parent = (Search)getActivity();
                search_parent.startShare();
            };
        });

        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse_animation);
        Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_animation);

        // при запуске начинаем с эффекта увеличения
        ivSoc.startAnimation(pulse);
        ivRandom.startAnimation(slide);
        return view;
    }

    private void setColor() {
        sPref = getActivity().getSharedPreferences("color_scheme", Context.MODE_PRIVATE);
        if (sPref.contains(SAVED_COLOR)) {
            int col = sPref.getInt(SAVED_COLOR, 0);
            if (col == 1) {
                ivContacts.setImageResource(R.drawable.search_contb);
                ivInvite.setImageResource(R.drawable.search_inviteb);
                ivParam.setImageResource(R.drawable.search_paramb);
                ivRandom.setImageResource(R.drawable.search_randomb);
                ivSoc.setImageResource(R.drawable.search_socb);
                tvSearchTitle.setBackgroundResource(R.color.blue);
            } else if (col == 0) {
                ivContacts.setImageResource(R.drawable.search_cont);
                ivInvite.setImageResource(R.drawable.search_invite);
                ivParam.setImageResource(R.drawable.search_param);
                ivSoc.setImageResource(R.drawable.search_soc);
                ivRandom.setImageResource(R.drawable.search_random);
                tvSearchTitle.setBackgroundResource(R.color.green);
            } else if (col == 3) {
                ivContacts.setImageResource(R.drawable.search_contp);
                ivInvite.setImageResource(R.drawable.search_invitep);
                ivParam.setImageResource(R.drawable.search_paramp);
                ivSoc.setImageResource(R.drawable.search_socp);
                ivRandom.setImageResource(R.drawable.search_randomp);
                tvSearchTitle.setBackgroundResource(R.color.purple);
            } else if(col == 2){
                tvSearchTitle.setBackgroundResource(R.color.orange);
                ivContacts.setImageResource(R.drawable.search_conto);
                ivInvite.setImageResource(R.drawable.search_inviteo);
                ivParam.setImageResource(R.drawable.search_paramo);
                ivSoc.setImageResource(R.drawable.search_soco);
                ivRandom.setImageResource(R.drawable.search_randomo);
            }
        }
    }



    @Override
    public  void onResume(){
        super.onResume();
        setColor();
    }

}
