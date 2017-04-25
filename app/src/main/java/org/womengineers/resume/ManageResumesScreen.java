package org.womengineers.resume;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.womengineers.resume.util.IabBroadcastReceiver;
import org.womengineers.resume.util.IabHelper;
import org.womengineers.resume.util.IabResult;
import org.womengineers.resume.util.Inventory;
import org.womengineers.resume.util.Purchase;

import java.util.Random;

public class ManageResumesScreen extends AppCompatActivity  {
    ResumeInfoDb theDb;
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;

    static final String TAG = "TrialPremiumPkg";

    boolean mIsPremium = false;

    static final String SKU_PREMIUM = "premium";

    static final int RC_REQUEST = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_resumes_screen);

        //creates a database to store the names of the files used in creating a resume
        theDb = new ResumeInfoDb(this);

        theDb.insertData("1", "resume1.pdf", "res1File1", "res1File2", "res1File3", "res1File4");
        theDb.insertData("2", "resume2.pdf", "res2File1", "res2File2", "res2File3", "res2File4");
        theDb.insertData("3", "resume3.pdf", "res3File1", "res3File2", "res3File3", "res3File4");

        //constructs the public key from Google Develop Console
        String one = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8" +
                "AMIIBCgKCAQEAk7XyJTWY2JKn+rfEeQ8EkY0KXb" +
                "guD/a5CUvbWsYULw6ztuhgTZpZ+B3OTHln5BRvICW" +
                "j0i4Wf/t6rHXWH2urcSKNNelAaZ7CGY8x4jSKNSTQMf" +
                "er475u2LrX1+Mf3Squ0gR0qjEqiPc5zaQJMTmZSuZIt+Y" +
                "AnXbrwCK4gYGTHSqg5juPmN0Lttodltz6BlZNsKbcfjBj" +
                "IVNx62W82p0Fy0esNPqswcBzFZNUzaqJTzZkozjLBMvEO5" +
                "r2LipFDR7W28zt7Y7EpGgB0mgRL7vBGCNBbEVznRavWBIr" +
                "HuZEWRHGen87mqMokXs8qE88sfSyHfgpjgNBqtxIapfcVms10wIDAQAB";

        mHelper = new IabHelper(ManageResumesScreen.this, one);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.i("Iab Setup Problem", "Problem setting up In-app Billing: " + result);
                }

                /*
                mBroadcastReceiver = new IabBroadcastReceiver(ManageResumesScreen.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
                */

                //supposed to be more secure than sample???
                LocalBroadcastManager.getInstance(ManageResumesScreen.this).registerReceiver(mBroadcastReceiver,
                        new IntentFilter("my-event"));

                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

            }
        });

        //when clicked sends the user to contactInfo to edit first saved resume
        Button toStartResume1 = (Button) findViewById(R.id.button21);
        toStartResume1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                Intent toContactInfo = new Intent(ManageResumesScreen.this, ContactInfo.class);
                toContactInfo.putExtra("resNum", "2");
                startActivity(toContactInfo);
                finish();
            }
        });

        //when clicked sends user to contactInfo to edit second saved resume
        Button toStartResume2 = (Button) findViewById(R.id.button23);
        toStartResume2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toContactInfo = new Intent(ManageResumesScreen.this, ContactInfo.class);
                toContactInfo.putExtra("resNum", "1");
                startActivity(toContactInfo);
                finish();
            }
        });

        //when clicked sends user to contactInfo to edit third saved resume
        Button toStartResume3 = (Button) findViewById(R.id.button25);
        toStartResume3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toContactInfo = new Intent(ManageResumesScreen.this, ContactInfo.class);
                toContactInfo.putExtra("resNum", "3");
                startActivity(toContactInfo);
                finish();
            }
        });

        //when clicked sends the user to the main screen
        Button toMainScreen = (Button) findViewById(R.id.button24);
        toMainScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(ManageResumesScreen.this, MainScreen.class));
                finish();
            }
        });

        //when clicked initiates the process of purchasing the premium package which gives the user 2 additional resumes
        //and one additional template
        final Button purchasePrem = (Button) findViewById(R.id.button22);
        purchasePrem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                onUpgradeAppButtonClicked(purchasePrem);

            }
        });



    }

    // Listener that's called when we finish querying the items we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.i("Query issue","Failed to query inventory: " + result);
                return;
            }

            //Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct!
             */
            else{
                // Do we have the premium upgrade?
                Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
                mIsPremium = (premiumPurchase != null && verDevPayload(premiumPurchase));
                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

                receivedBroadcast();
                updateUi();
                setWaitScreen(false);
                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            }

        }
    };

    //@Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.i("Querying issue", "Error querying inventory. Another async operation in progress.");
        }
    }

    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeAppButtonClicked(View arg0) {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verDevPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */

        String parts = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random randGen = new Random();
        String payld = "";

        for(int i = 0; i < 15; i++){
            int index = randGen.nextInt(parts.length());
            payld += parts.charAt(index);
        }


        try {
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, payld);
        } catch (Exception e) {
            Log.i("Purchase Flow", "Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    //Verifies the developer payload of a purchase.
    boolean verDevPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            //Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.i("Purchases","Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            else
                Log.i("Purchases", "Purchase successful");

            if (!verDevPayload(purchase)) {
                Log.i("Purchase","Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                premiumPurchaseAlert();
                mIsPremium = true;
                updateUi();
                setWaitScreen(false);
            }
        }
    };

    // updates UI to reflect model
    public void updateUi() {

        // "Upgrade" button is only visible if the user is not premium
        findViewById(R.id.button22).setVisibility(mIsPremium ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.textView).setVisibility(mIsPremium ? View.INVISIBLE : View.VISIBLE);

        //only show additional two resumes when user is under premium package
        findViewById(R.id.button23).setVisibility(mIsPremium ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.button25).setVisibility(mIsPremium ? View.VISIBLE : View.INVISIBLE);
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        //findViewById(R.id.screen_wait).setVisibility(set ? View.GONE : View.VISIBLE);
        //findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    //puts an alert on the screen to notify a user of their completed purchase
    public void premiumPurchaseAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Congratulations on purchasing the premium package!");
        alert.setNeutralButton("Ok", null);
        alert.create().show();
    }

    //closes unneeded components of the app that would otherwise use up memory
    public void onDestroy() {
        theDb.close();
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    //unregisters the receiver when the activity is not visible
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }
}
