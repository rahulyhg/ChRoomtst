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
                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(pickIntent, PICK_RESULT);
            }
        });

        ivInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("sms_body", "Привет! Заходи в ChatRoom!");
                startActivity(smsIntent);
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
                Intent i = new Intent(getActivity(), ShareNetworks.class);
                startActivity(i);
            };
        });


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
                tvSearchTitle.setBackgroundResource(R.drawable.b_string);
            } else if (col == 0) {
                ivContacts.setImageResource(R.drawable.search_cont);
                ivInvite.setImageResource(R.drawable.search_invite);
                ivParam.setImageResource(R.drawable.search_param);
                ivSoc.setImageResource(R.drawable.search_soc);
                ivRandom.setImageResource(R.drawable.search_random);
                tvSearchTitle.setBackgroundResource(R.drawable.g_strip);
            } else if (col == 3) {
                ivContacts.setImageResource(R.drawable.search_contp);
                ivInvite.setImageResource(R.drawable.search_invitep);
                ivParam.setImageResource(R.drawable.search_paramp);
                ivSoc.setImageResource(R.drawable.search_socp);
                ivRandom.setImageResource(R.drawable.search_randomp);
                tvSearchTitle.setBackgroundResource(R.drawable.p_string);
            } else if(col == 2){
                tvSearchTitle.setBackgroundResource(R.drawable.o_strip);
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
    private void publishFeedDialog() {
        Bundle params = new Bundle();
        params.putString("name", "Facebook SDK for Android");
        params.putString("caption", "Build great social apps and get more installs.");
        params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
        params.putString("link", "https://developers.facebook.com/android");
        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(getActivity(),
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(getActivity(),
                                        "Posted story, id: "+postId,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Publish cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Publish cancelled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Error posting story",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }
    private void facebookShare(){
        //facebook.authorize(getActivity(), permissions, Facebook.DialogListener listener);
    }

}
