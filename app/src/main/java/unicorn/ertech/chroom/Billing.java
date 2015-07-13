package unicorn.ertech.chroom;

import unicorn.ertech.chroom.util.IabHelper;
import unicorn.ertech.chroom.util.IabResult;
import unicorn.ertech.chroom.util.Inventory;
import unicorn.ertech.chroom.util.Purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Billing extends Fragment {
    // Debug tag, for logging
    static final String TAG = "NK TEST PURCHASE";
    static String URL = "http://im.topufa.org/index.php";

    // ТО ЧТО В СКОБКАХ ДОЛЖНО БЫТЬ ДОБАВЛЕНО В КОНСОЛИ? КАК 4 ПРОДУКТА

    static final String ONE_BUTTON = "iz30";
    static final String TWO_BUTTON = "iz50";
    static final String THREE_BUTTON = "iz100";
    static final String FOUR_BUTTON = "bezlim";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;
    TextView des;
    TextView sms;
    TextView play;
    TextView other;
    TextView tvIz30;
    TextView tvIz50;
    TextView tvIz100;
    TextView tvBezlim;
    TextView b;

    ImageButton butBack;


    int izuminok = 0;
    String token;
    
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //задаем разметку фрагменту
        View view = inflater.inflate(R.layout.billing, container, false);
        //ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();

        tvIz30 = (TextView) view.findViewById(R.id.textView30);
        tvIz50 = (TextView) view.findViewById(R.id.textView50);
        tvIz100 = (TextView) view.findViewById(R.id.textView100);
        tvBezlim = (TextView) view.findViewById(R.id.textViewB);


// КЛЮЧ
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkWzjEZ83E73Ci0ldk3XvSIrQqRq75AnJQnRG0Ax3MkrRpOKlYtpB9UzU3pbcwQILDhcH8reRUnFfIS9FrfykI3TM0/YmbpMjgE5f18Vh8qtkNW3Barlr7dkOK6mXaPswepg4S9vcVMEwAFwBjcY65JgATySJXnf75T2nqeD1x8uBizlB9Vui3vASA8SY/ka0qoNJb3F+ET5Tmt4fFINsm2GyJoYzZoWRLXSWJgiU8Y2bKdrb9ZyzTPPNCM8IEzcW01wrVg2wikOriEVdNpKqN3F3gNZ0x0EQJC6aU8zIW2N5ewNrEa011SqECEOiFEqB1G6xdthu7qBG7L3I8fpnawIDAQAB";

        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        try {
            SharedPreferences userData = context.getSharedPreferences("userdata", Activity.MODE_PRIVATE);
            if((userData.contains("token"))){
                if(!userData.getString("token", "0").equals("0")){
                    token=userData.getString("token", "");
                }
            }else{
                token="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button bt30 = (Button) view.findViewById(R.id.bt30);
        Button bt50 = (Button) view.findViewById(R.id.bt50);
        Button bt100 = (Button) view.findViewById(R.id.bt100);
        Button btB = (Button) view.findViewById(R.id.btB);


        bt30.setOnClickListener(one);
        bt50.setOnClickListener(two);
        bt100.setOnClickListener(three);
        btB.setOnClickListener(four);
        
        return view;
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            Purchase Purchase1 = inventory.getPurchase(ONE_BUTTON);
            if (Purchase1 != null && verifyDeveloperPayload(Purchase1)) {
                mHelper.consumeAsync(inventory.getPurchase(ONE_BUTTON), mConsumeFinishedListener);
                return;
            }

            Purchase Purchase2 = inventory.getPurchase(TWO_BUTTON);
            if (Purchase2 != null && verifyDeveloperPayload(Purchase2)) {
                mHelper.consumeAsync(inventory.getPurchase(TWO_BUTTON), mConsumeFinishedListener);
                return;
            }

            Purchase Purchase3 = inventory.getPurchase(THREE_BUTTON);
            if (Purchase3 != null && verifyDeveloperPayload(Purchase3)) {
                mHelper.consumeAsync(inventory.getPurchase(THREE_BUTTON), mConsumeFinishedListener);
                return;
            }

            Purchase Purchase4 = inventory.getPurchase(FOUR_BUTTON);
            if (Purchase4 != null && verifyDeveloperPayload(Purchase4)) {
                mHelper.consumeAsync(inventory.getPurchase(FOUR_BUTTON), mConsumeFinishedListener);
                return;
            }


            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };


    View.OnClickListener one = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            String payload = "ONE_BUTTON";

            if(!token.equals("")){
                mHelper.launchPurchaseFlow(getActivity(), ONE_BUTTON, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
            else{
                Toast.makeText(context, "������ �����������", Toast.LENGTH_SHORT);
            }
        }
    };

    View.OnClickListener two = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            String payload = "";
            if(!token.equals("")){
                mHelper.launchPurchaseFlow(getActivity(), TWO_BUTTON, RC_REQUEST,
                        mPurchaseFinishedListener, payload);}
            else{
                Toast.makeText(context, "������ �����������", Toast.LENGTH_SHORT);
            }
        }
    };

    View.OnClickListener three = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            String payload = "";
            if(!token.equals("")){
                mHelper.launchPurchaseFlow(getActivity(), THREE_BUTTON, RC_REQUEST,
                        mPurchaseFinishedListener, payload);}
            else{
                Toast.makeText(context, "������ �����������", Toast.LENGTH_SHORT);
            }
        }
    };

    View.OnClickListener four = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            String payload = "";
            if(!token.equals("")){
                mHelper.launchPurchaseFlow(getActivity(), FOUR_BUTTON, RC_REQUEST,
                        mPurchaseFinishedListener, payload);}
            else{
                Toast.makeText(context, "������ �����������", Toast.LENGTH_SHORT);
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

// ЭТО ЕСЛИ? ВСЕ ГУД

            if (purchase.getSku().equals(ONE_BUTTON)) {
                izuminok += 30;
                //count.setText("������: "+izuminok+" �������� ");
                //      tvIz30.setText("������� ��������: " + izuminok);
                //      mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="30";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(TWO_BUTTON)) {
                izuminok += 50;
                //count.setText("������: " + izuminok+" �������� ");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="50";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(THREE_BUTTON)) {
                izuminok += 100;
                //count.setText("������: " + izuminok+" �������� ");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="100";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(FOUR_BUTTON)) {
                //izuminok += 500;
                //count.setText("������: ��������");
                //	tvBezlim.setText("�������� ������");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="p";
                new sendPurchase().execute(t);
            }

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

            }
            else {
                complain("Error while consuming: " + result);
            }

            Log.d(TAG, "End consumption flow.");
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        //alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    private  class sendPurchase extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //������ ������ ��� ���������
            jParser.setParam("token", token);
            jParser.setParam("action", "payment");
            jParser.setParam("type", "1");
            jParser.setParam("amount", args[0]);
            // Getting JSON from URL

            JSONObject json = jParser.getJSONFromUrl(URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String s="";
                try {
                    s = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s.equals("false")){
                    try{
                        Toast.makeText(context,"�������", Toast.LENGTH_SHORT);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }else{
                    if(s.equals("13")){
                        Toast.makeText(context,"�������� �����", Toast.LENGTH_SHORT);
                    }else if(s.equals("22")){
                        Toast.makeText(context,"�� ���������������� � ��", Toast.LENGTH_SHORT);
                    }
                }
            }

        }
    }//����� asyncTask

    public void sms(View v)
    {
        //Intent in = new Intent(context, sms.class);
        //startActivity(in);
        //finish();
    }


}
