package unicorn.ertech.chroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.LoginButton;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import com.facebook.widget.ProfilePictureView;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Timur on 31.01.2015.
 */
public class ShareNetworks extends Fragment {
    private Context context;

    //fb
    private static final String PERMISSION = "publish_actions";
    private static final Location SEATTLE_LOCATION = new Location("") {
        {
            setLatitude(47.6097);
            setLongitude(-122.3331);
        }
    };

    private final String PENDING_ACTION_BUNDLE_KEY = "unicorn.ertech.chroom:PendingAction";

    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private Button pickFriendsButton;
    private Button pickPlaceButton;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private ViewGroup controlsContainer;
    private GraphUser user;
    private GraphPlace place;
    private List<GraphUser> tags;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    private UiLifecycleHelper uiHelper;

    //vk
    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS
    };

    //twitter
    private Twitter mTwitter;

    public static final String CONSUMER_KEY = "Vur7gbEtoa1jf8iabBvyRCh7K";
    public static final String CONSUMER_SECRET = "G4GnQbMw596bm0xXahzaV41fPfTSJeLbwLNQi470Kd28IgUfOW";
    public static final String CALLBACK_URL = "";

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        final View view = inflater.inflate(R.layout.fragment_socials, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                ShareNetworks.this.user = user;
                updateUI();
                // It's possible that we were waiting for this.user to be populated in order to post a
                // status update.
                handlePendingAction();
            }
        });
        //ну и контекст, так как фрагменты не содержaт собственного
        context = view.getContext();
        view.findViewById(R.id.vkontakte_button).setOnClickListener(vk);
        view.findViewById(R.id.facebook_button).setOnClickListener(fb);
        view.findViewById(R.id.twitter_button).setOnClickListener(twitter);
        view.findViewById(R.id.odk_button).setOnClickListener(ok);
        view.findViewById(R.id.layVk).setOnClickListener(vk);
        view.findViewById(R.id.layFace).setOnClickListener(fb);
        view.findViewById(R.id.layTwit).setOnClickListener(twitter);
        view.findViewById(R.id.layOk).setOnClickListener(ok);

        canPresentShareDialog = FacebookDialog.canPresentShareDialog(getActivity(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
        canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(getActivity(),
                FacebookDialog.ShareDialogFeature.PHOTOS);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
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
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(getActivity().getApplicationContext());

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(getActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
        updateUI();
    }

    private void updateUI() {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if (enableButtons && user != null) {
            performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
        } else {
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
        return new FacebookDialog.ShareDialogBuilder(getActivity())
                .setName("Hello Facebook")
                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
                .setLink("http://developers.facebook.com/android");
    }

    private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, place, tags, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            Bundle params = new Bundle();
            params.putString("name", "IZUM");
            params.putString("caption", "Get new IM");
            params.putString("description", "Hello, world!");
            params.putString("link", "http://im.topufa.org/");
            params.putString("picture", "https://raw.githubusercontent.com/JawaJedi/ChRoomtst/master/logo.png");

            WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context, Session.getActiveSession(), params)).setOnCompleteListener(new WebDialog.OnCompleteListener() {

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
            feedDialog.show();

            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private FacebookDialog.PhotoShareDialogBuilder createShareDialogBuilderForPhoto(Bitmap... photos) {
        return new FacebookDialog.PhotoShareDialogBuilder(getActivity())
                .addPhotos(Arrays.asList(photos));
    }

    private void postPhoto() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
        if (canPresentShareDialogWithPhotos) {
            FacebookDialog shareDialog = createShareDialogBuilderForPhoto(image).build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (hasPublishPermission()) {
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    showPublishResult(getString(R.string.photo_post), response.getGraphObject(), response.getError());
                }
            });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoSession) {
        Session session = Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
                return;
            }
        }

        if (allowNoSession) {
            pendingAction = action;
            handlePendingAction();
        }
    }

    View.OnClickListener fb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginButton.performClick();
            //Intent i = new Intent(getActivity(), ShareFacebook.class);
            //getActivity().startActivity(i);
            /*if (isAppInstalled("com.facebook.katana")) {

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Привет, друзья! Я установил приложение IZUM - присоединяйтесь!");
                shareIntent.setPackage("com.facebook.katana");
                shareIntent.setType("text/plain");
                (getActivity()).startActivity(shareIntent);
            } else {
                fbAuthorize();
            }*/
        }
    };

    public void fbAuthorize() {

        /*facebook.authorize(getActivity(), permissions, new Facebook.DialogListener() {
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
            }});*/
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
        /*facebook.dialog(context, "feed", params, new Facebook.DialogListener() {

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
            }});*/

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
