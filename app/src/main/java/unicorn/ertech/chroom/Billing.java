package unicorn.ertech.chroom;

import unicorn.ertech.chroom.util.IabHelper;
import unicorn.ertech.chroom.util.IabResult;
import unicorn.ertech.chroom.util.Inventory;
import unicorn.ertech.chroom.util.Purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Billing extends Activity {
    // Debug tag, for logging
    static final String TAG = "NK TEST PURCHASE";
    static String URL = "http://im.topufa.org/index.php";

    // РўРћ Р§РўРћ Р’ РЎРљРћР‘РљРђРҐ Р”РћР›Р–РќРћ Р‘Р«РўР¬ Р”РћР‘РђР’Р›Р•РќРћ Р’ РљРћРќРЎРћР›Р? РљРђРљ 4 РџР РћР”РЈРљРўРђ

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
    TextView count;
    TextView tvIz30;
    TextView tvIz50;
    TextView tvIz100;
    TextView tvBezlim;
    TextView b;
    TextView title;

    ImageButton butBack;

    RelativeLayout topRow;

    int izuminok = 0;
    String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing);

        count = (TextView) findViewById(R.id.count);
        des = (TextView) findViewById(R.id.des);

        sms = (TextView) findViewById(R.id.des);
        play = (TextView) findViewById(R.id.des);
        other = (TextView) findViewById(R.id.des);

        tvIz30 = (TextView) findViewById(R.id.textView30);
        tvIz50 = (TextView) findViewById(R.id.textView50);
        tvIz100 = (TextView) findViewById(R.id.textView100);
        tvBezlim = (TextView) findViewById(R.id.textViewB);
        b = (TextView) findViewById(R.id.b);
        title = (TextView) findViewById(R.id.title);

        butBack=(ImageButton)findViewById(R.id.setBack);

        //Typeface font = Typeface.createFromAsset(this.getAssets(), "MyriadProRegular.ttf");
       // tvIz30.setTypeface(font, Typeface.BOLD);

       // tvIz50.setTypeface(font, Typeface.BOLD);

       // tvIz100.setTypeface(font, Typeface.BOLD);

       // tvBezlim.setTypeface(font, Typeface.BOLD);
      //  b.setTypeface(font, Typeface.BOLD);

     //   count.setTypeface(font);
     //   des.setTypeface(font, Typeface.ITALIC);

    //    sms.setTypeface(font);
    //    play.setTypeface(font);
    //    other.setTypeface(font);
    //    count.setTypeface(font);
    //    title.setTypeface(font, Typeface.BOLD);

        Button bt30 = (Button) findViewById(R.id.bt30);
        Button bt50 = (Button) findViewById(R.id.bt50);
        Button bt100 = (Button) findViewById(R.id.bt100);
        Button btB = (Button) findViewById(R.id.btB);

    //    bt30.setTypeface(font);
    //    bt50.setTypeface(font);
    //    bt100.setTypeface(font);
    //    btB.setTypeface(font);

        topRow = (RelativeLayout)findViewById(R.id.topRowWal);
        butBack=(ImageButton)findViewById(R.id.setBackWal);

        // count.setText("Изюминок: " + izuminok);

// РљР›Р®Р§
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkWzjEZ83E73Ci0ldk3XvSIrQqRq75AnJQnRG0Ax3MkrRpOKlYtpB9UzU3pbcwQILDhcH8reRUnFfIS9FrfykI3TM0/YmbpMjgE5f18Vh8qtkNW3Barlr7dkOK6mXaPswepg4S9vcVMEwAFwBjcY65JgATySJXnf75T2nqeD1x8uBizlB9Vui3vASA8SY/ka0qoNJb3F+ET5Tmt4fFINsm2GyJoYzZoWRLXSWJgiU8Y2bKdrb9ZyzTPPNCM8IEzcW01wrVg2wikOriEVdNpKqN3F3gNZ0x0EQJC6aU8zIW2N5ewNrEa011SqECEOiFEqB1G6xdthu7qBG7L3I8fpnawIDAQAB";

        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

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
            SharedPreferences userData = getSharedPreferences("userdata", Activity.MODE_PRIVATE);
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

        setColor();
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });

        new getBalance().execute();
    }

    public void closeMe(){
        this.finish();
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

    public void one(View arg0) {
        String payload = "ONE_BUTTON";

        if(!token.equals("")){
            mHelper.launchPurchaseFlow(this, ONE_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);
        }
        else{
            Toast.makeText(getApplicationContext(), "Ошибка авторизации", Toast.LENGTH_SHORT);
        }
    }

    public void two(View arg0) {
        String payload = "";
        if(!token.equals("")){
        mHelper.launchPurchaseFlow(this, TWO_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);}
        else{
            Toast.makeText(getApplicationContext(), "Ошибка авторизации", Toast.LENGTH_SHORT);
        }
    }

    public void three(View arg0) {
        String payload = "";
        if(!token.equals("")){
        mHelper.launchPurchaseFlow(this, THREE_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);}
        else{
            Toast.makeText(getApplicationContext(), "Ошибка авторизации", Toast.LENGTH_SHORT);
        }
    }

    public void four(View arg0) {
        String payload = "";
        if(!token.equals("")){
        mHelper.launchPurchaseFlow(this, FOUR_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);}
        else{
            Toast.makeText(getApplicationContext(), "Ошибка авторизации", Toast.LENGTH_SHORT);
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

// Р­РўРћ Р•РЎР›Р? Р’РЎР• Р“РЈР”

            if (purchase.getSku().equals(ONE_BUTTON)) {
                izuminok += 30;
                count.setText("Баланс: "+izuminok+" изюминок ");
                //      tvIz30.setText("Куплено изюминок: " + izuminok);
                //      mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="30";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(TWO_BUTTON)) {
                izuminok += 50;
                count.setText("Баланс: " + izuminok+" изюминок ");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="50";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(THREE_BUTTON)) {
                izuminok += 100;
                count.setText("Баланс: " + izuminok+" изюминок ");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                String[] t= new String[1];
                t[0]="100";
                new sendPurchase().execute(t);
            }

            if (purchase.getSku().equals(FOUR_BUTTON)) {
                //izuminok += 500;
                count.setText("Баланс: БЕЗЛИМИТ");
                //	tvBezlim.setText("БЕЗЛИМИТ куплен");
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
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
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

            //ставим нужные нам параметры
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
                        Toast.makeText(getApplicationContext(),"Успешно", Toast.LENGTH_SHORT);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }else{
                    if(s.equals("13")){
                        Toast.makeText(getApplicationContext(),"Неверная сумма", Toast.LENGTH_SHORT);
                    }else if(s.equals("22")){
                        Toast.makeText(getApplicationContext(),"Не синхронизировано с БД", Toast.LENGTH_SHORT);
                    }
                }
            }

        }
    }//конец asyncTask

    public void sms(View v)
    {
        //Intent in = new Intent(getApplicationContext(), sms.class);
        //startActivity(in);
        //finish();
    }

    private void setColor(){
        SharedPreferences sPref = getSharedPreferences("color_scheme", MODE_PRIVATE);
        int col=sPref.getInt("color", 0);
        switch (col) {
            case 0:
                topRow.setBackgroundResource(R.color.green);
                break;
            case 1:
                topRow.setBackgroundResource(R.color.blue);
                break;
            case 2:
                topRow.setBackgroundResource(R.color.orange);
                break;
            case 3:
                topRow.setBackgroundResource(R.color.purple);
                break;
            default:
                break;
        }
    };

    private  class getBalance extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "balance_get");
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
                if("false".equals(s)){
                    try {
                        //String s1=new String("Баланс: " + json.getString("balance")+" изюминок ", "");
                        count.setText(json.getString("balance")+" ");
                        count.append(getResources().getString(R.string.Wallet2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                }else{
                    if(s.equals("13")){
                        Toast.makeText(getApplicationContext(),"Неверная сумма", Toast.LENGTH_SHORT);
                    }else if(s.equals("22")){
                        Toast.makeText(getApplicationContext(),"Не синхронизировано с сервером", Toast.LENGTH_SHORT);
                    }
                }
            }

        }
    }//конец asyncTask
}
