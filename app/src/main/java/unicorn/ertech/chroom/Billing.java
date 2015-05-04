package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import unicorn.ertech.chroom.util.IabHelper;
import unicorn.ertech.chroom.util.IabResult;
import unicorn.ertech.chroom.util.Inventory;
import unicorn.ertech.chroom.util.Purchase;

/**
 * Created by Timur on 04.05.2015.
 */
public class Billing extends Activity {
    // Debug tag, for logging
    static final String TAG = "NK TEST PURCHASE";

    // “Œ ◊“Œ ¬ — Œ¡ ¿’ ƒŒÀ∆ÕŒ ¡€“‹ ƒŒ¡¿¬À≈ÕŒ ¬  ŒÕ—ŒÀ»  ¿  4 œ–Œƒ” “¿

    static final String ONE_BUTTON = "gas";
    static final String TWO_BUTTON = "android.test.canceled";
    static final String THREE_BUTTON = "android.test.purchased";
    static final String FOUR_BUTTON = "android.test.purchased";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;
    TextView count;

    int izuminok = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_wallet);

        count = (TextView) findViewById(R.id.tvWalletBalance);
        count.setText("»Á˛ÏËÌÓÍ: " + izuminok);

//  Àﬁ◊
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

        mHelper.launchPurchaseFlow(this, ONE_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void two(View arg0) {
        String payload = "";

        mHelper.launchPurchaseFlow(this, TWO_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void three(View arg0) {
        String payload = "";

        mHelper.launchPurchaseFlow(this, THREE_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void four(View arg0) {
        String payload = "";

        mHelper.launchPurchaseFlow(this, FOUR_BUTTON, RC_REQUEST,
                mPurchaseFinishedListener, payload);
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

// ›“Œ ≈—À» ¬—≈ √”ƒ

            if (purchase.getSku().equals(ONE_BUTTON)) {
                izuminok += 1;
                count.setText("»Á˛ÏËÌÓÍ: " + izuminok);
                //      mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }

            if (purchase.getSku().equals(TWO_BUTTON)) {
                izuminok += 25;
                count.setText("»Á˛ÏËÌÓÍ: " + izuminok);
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }

            if (purchase.getSku().equals(THREE_BUTTON)) {
                izuminok += 50;
                count.setText("»Á˛ÏËÌÓÍ: " + izuminok);
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }

            if (purchase.getSku().equals(FOUR_BUTTON)) {
                izuminok += 100;
                count.setText("»Á˛ÏËÌÓÍ: " + izuminok);
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
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
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
}
