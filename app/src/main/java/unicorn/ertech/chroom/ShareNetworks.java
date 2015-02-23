package unicorn.ertech.chroom;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import net.londatiga.android.twitter.Twitter;
import net.londatiga.android.twitter.TwitterDialog;
import net.londatiga.android.twitter.TwitterRequest;
import net.londatiga.android.twitter.TwitterUser;
import net.londatiga.android.twitter.Twitter.AccesTokenTask;
import net.londatiga.android.twitter.oauth.OauthAccessToken;
import net.londatiga.android.twitter.util.Debug;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import com.vk.sdk.util.VKUtil;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Timur on 31.01.2015.
 */
public class ShareNetworks extends Fragment {
    private Context context;

    //fb
    public final String API_KEY = "435465129943262";
    public final String[] permissions = {"publish_stream"};
    Facebook facebook = new Facebook(API_KEY);
    //vk
    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS
    };




    //twitter
    private Twitter mTwitter;

    public static final String CONSUMER_KEY = "QbB5UmppahrslAET344vyyvBi";
    public static final String CONSUMER_SECRET = "x3f9KBNNyIs9jdX8v7cdFvaJOgXUtGi8aNk6XflejL4OOiC73a";
    public static final String CALLBACK_URL = "";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_socials, container, false);
        //ну и контекст, так как фрагменты не содержaт собственного
        context = view.getContext();
        view.findViewById(R.id.vkontakte_button).setOnClickListener(vk);
        view.findViewById(R.id.facebook_button).setOnClickListener(fb);
        view.findViewById(R.id.twitter_button).setOnClickListener(twitter);
        view.findViewById(R.id.odk_button).setOnClickListener(ok);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //vk

    View.OnClickListener vk = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isAppInstalled("com.vkontakte.android"))
            {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!");
                shareIntent.setPackage("com.vkontakte.android");
                shareIntent.setType("text/plain");
                (getActivity()).startActivity(shareIntent);
            }
            else
            {
             vk_aut();
            }
        }
    };

    void vk_aut()
    {

        VKUIHelper.onCreate(getActivity());
        VKSdk.initialize(sdkListener, "4795571");
        if (VKSdk.wakeUpSession()) {
            makePost(null, "Hello, friends!");
            return;
        }
        else
        {
            //Intent intent = new Intent(getActivity().getApplicationContext(), ShareLoginActivity.class);
            //startActivity(intent);
            VKSdk.authorize(sMyScope, true, false);
        }

        String[] fingerprint = VKUtil.getCertificateFingerprint(context, getActivity().getPackageName());
        Log.d("Fingerprint", fingerprint[0]);
    }



    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            //          startTestActivity();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //        startTestActivity();
        }
    };





    public void vk1(View v)
    {
        vk_aut();

    }


    @SuppressWarnings("unused")
    private void makePost(VKAttachments attachments) {
        makePost(attachments, null);
    }
    private void makePost(VKAttachments attachments, String message) {
        VKRequest post = VKApi.wall().post(VKParameters.from( VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));

        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                Toast toast = Toast.makeText(context,"Успешно!", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onError(VKError error) {
                showError(error.apiError != null ? error.apiError : error);
            }
        });
    }


    private void showError(VKError error) {
        new AlertDialog.Builder(getActivity())
                .setMessage(error.errorMessage)
                .setPositiveButton("OK", null)
                .show();

        if (error.httpError != null) {
            Log.w("Test", "Error in request or upload", error.httpError);
        }
    }




    /////////////////////////////ok

    View.OnClickListener ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isAppInstalled("ru.ok.android")) {

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!");
                shareIntent.setPackage("ru.ok.android");
                shareIntent.setType("text/plain");
                (getActivity()).startActivity(shareIntent);
            } else {
                ok_aut();
                };
            }
        };


    public void ok1(View v)
    {
        ok_aut();
    }



    void ok_aut()
    {

        final okDialog dialog = new okDialog(getActivity(),
                "http://google.ua",
                "", null);

        dialog.show();


        //	  webview.loadUrl("http://connect.ok.ru/dk?st.cmd=WidgetMediatopicPost&st.app=1123372032&st.attachment=%7B%22media%22%3A%5B%7B%22type%22%3A%22text%22%2C%22text%22%3A%22hello%20world!%22%7D%5D%7D&st.signature=f63d9ae4167c8d2c1f2a7a376462c262&st.popup=off&st.silent=on&st.utext=off&st.nohead=off");

    }




    ///twitter

    View.OnClickListener twitter = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            if (isAppInstalled("com.twitter.android")) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!");
                shareIntent.setPackage("com.twitter.android");
                shareIntent.setType("text/plain");
                (getActivity()).startActivity(shareIntent);
            } else {
                tw_aut();
            }
        }
    };




    public void twitter1(View v)
    {
        tw_aut();

    }

    void tw_aut()
    {
        mTwitter = new Twitter(getActivity(), CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);

        if (mTwitter.sessionActive()) {
            postT();
        } else {

            signinTwitter();
        }
    }



    private void signinTwitter() {
        mTwitter.signin(new Twitter.SigninListener() {
            public void onSuccess(OauthAccessToken accessToken, String userId, String screenName) {
                getCredentials();
            }

            public void onError(String error) {
                showToast(error);
            }
        });
    }

    private void getCredentials() {
        final ProgressDialog progressDlg = new ProgressDialog(getActivity());

        progressDlg.setMessage("Getting credentials...");
        progressDlg.setCancelable(false);

        progressDlg.show();

        TwitterRequest request = new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        request.verifyCredentials(new TwitterRequest.VerifyCredentialListener() {

            public void onSuccess(TwitterUser user) {
                progressDlg.dismiss();

                showToast("Hello " + user.name);

                postT();

                //startActivity(new Intent(getActivity(), UserActivity.class));

                //	finish();
            }

            public void onError(String error) {
                progressDlg.dismiss();

                showToast(error);
            }
        });
    }


    public void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    void postT()
    {

        final ProgressDialog progressDlg = new ProgressDialog(getActivity());

        progressDlg.setMessage("Sending...");
        progressDlg.setCancelable(false);

        progressDlg.show();

        TwitterRequest request 		= new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        String updateStatusUrl		= "https://api.twitter.com/1.1/statuses/update.json";

        List<NameValuePair> params 	= new ArrayList<NameValuePair>(1);

        params.add(new BasicNameValuePair("status", "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!"));

        request.createRequest("POST", updateStatusUrl, params, new TwitterRequest.RequestListener() {

            public void onSuccess(String response) {
                progressDlg.dismiss();

                showToast("Успешно!");

                Debug.i(response);
            }

            public void onError(String error) {
                showToast(error);

                progressDlg.dismiss();
            }
        });

    }















    //fb
    View.OnClickListener fb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isAppInstalled("com.facebook.katana")) {

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!");
                shareIntent.setPackage("com.facebook.katana");
                shareIntent.setType("text/plain");
                (getActivity()).startActivity(shareIntent);
            } else {
                fbAuthorize();
            }
        }
    };

    public void fbAuthorize() {

        facebook.authorize(getActivity(), permissions, new Facebook.DialogListener() {
            @Override
            public void onComplete(Bundle values) {
                //Toast.makeText(getActivity(), "Authorization successful", Toast.LENGTH_SHORT).show();
                fb1();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                Toast.makeText(getActivity(), "Facebook error, try again later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(DialogError e) {
                Toast.makeText(getActivity(), "Error, try again later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //Этот коллбэк никогда не срабатывает, вероятно, ошибка в SDK
                Toast.makeText(getActivity(), "Authorization canceled", Toast.LENGTH_SHORT).show();
            }});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    public void fb1()
    {
        Bundle params = new Bundle();
        params.putString("name", "Facebook SDK for Android");
        params.putString("caption", "Build great social apps and get more installs.");
        params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
        params.putString("link", "https://developers.facebook.com/android");
        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
        /*WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(), API_KEY, params)).setOnCompleteListener(new WebDialog.OnCompleteListener() {

            @Override
            public void onComplete(Bundle values,
                                   FacebookException error) {
                if (error == null) {
                    // When the story is posted, echo the success
                    // and the post Id.
                    final String postId = values.getString("post_id");
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
        feedDialog.show();*/
        facebook.dialog(context, "feed", params, new Facebook.DialogListener() {

            @Override
            public void onComplete(Bundle values) {
                Toast.makeText(getActivity(), "Tnx!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFacebookError(FacebookError e) {
                Toast.makeText(getActivity(), "Facebook error, try again later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(DialogError e) {
                Toast.makeText(getActivity(), "Error, try again later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //Этот коллбэк никогда не срабатывает, вероятно, ошибка в SDK
                Toast.makeText(getActivity(), "Authorization canceled", Toast.LENGTH_SHORT).show();
            }});

    }

    //�������� ��� ������������� ����������
    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }



    //������� � �������
    public static boolean openApplicationInMarket(Context context, String string) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((String) String.format((String) "market://details?id=%s", (Object[]) new Object[]{string})));
            intent.setFlags(268435456);
            context.getApplicationContext().startActivity(intent);
            return true;
        }
        catch (Exception var3_3) {
            return false;
        }
    }
}
