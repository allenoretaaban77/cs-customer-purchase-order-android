package com.fnc.order.android.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.R;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.fragment.CustomerFragment;
import com.fnc.order.android.fragment.OrderFragment;
import com.fnc.order.android.fragment.TransactionFragment;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.google.api.core.NanoClock;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import hari.bounceview.BounceView;

public class MainActivity extends BaseActivity {
    Context ctx;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("DSX", "service is running");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

//        String svcname = "OrdersService";
//        Boolean isSvcRunning = false;
//        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
//            if(service.service.getClassName().indexOf(svcname)>0){
//                isSvcRunning = true;
//            }
//        }
//        if (!isSvcRunning) {
//            startService(new Intent(getBaseContext(), OrdersService.class));
//        }

//        OrdersService mSensorService = new OrdersService(getApplicationContext());
//        Intent mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
//        if (!isMyServiceRunning(mSensorService.getClass())) {
//
//        }

        setContentView(R.layout.activity_main);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        openFragment(new OrderFragment(), "order");
//        openFragment(new CustomerFragment(), "customer");

        if (Helper.checkBranchProfile(ctx).size() > 0) {
            aBranchlist mBl = Helper.checkBranchProfile(ctx).get(0);
            String strRef = SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH_ID.getKey());
            if (mBl.getBranchid().toString().equals(strRef)) {
                Helper.changePage(ctx, getSupportFragmentManager(), new CustomerFragment(),
                    "customer_fragment", "main_page");
            } else {
                SharedData.getInstance(ctx).saveData( SharedKey.ORDER_CUSTOMER_ID.getKey(), mBl.getCustomerID() );
                Helper.changePage(ctx, getSupportFragmentManager(), new OrderFragment(),
                    "order_fragment", "customer_fragment");
            }
        } else {
            finishAndRemoveTask();
            Toast.makeText(ctx, "Login Error, please contact IT support.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        String refPage = Helper.getPage(ctx);
        Log.d("dsx", refPage);

        if (refPage.equals("transaction_fragment")) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new TransactionFragment(), "transaction_fragment")
                .addToBackStack(null).commit();
        } else if (refPage.equals("customer_fragment")) {
            if (Helper.checkBranchProfile(ctx).get(0).getBranchid().toString().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH_ID.getKey()))) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CustomerFragment(), "customer_fragment")
                    .addToBackStack(null).commit();
            } else {
                BounceView.addAnimTo( Helper.okCancelDialog(ctx,
                    "Closing Application", "Are you sure you want to close this app?",
                    "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, "Cancel", null, false
                ) );
            }
        } else if (refPage.equals("order_fragment")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new OrderFragment(), "order_fragment")
                    .addToBackStack(null).commit();
        } else {
            BounceView.addAnimTo( Helper.okCancelDialog(ctx,
                "Closing Application", "Are you sure you want to close this app?",
                "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, "Cancel", null, false
            ) );
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    /*private void openFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Integer sbec = fm.getBackStackEntryCount(); //
        switch(sbec){
            case 1:
                transaction.replace(R.id.container, new CustomerFragment()).commit();
                break;
            case 2:
                transaction.replace(R.id.container, new OrderFragment()).commit();
                break;
            case 3:
                transaction.replace(R.id.container, new TransactionFragment()).commit();
                break;
            default:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStackImmediate();
                }
                transaction.replace(R.id.container, fragment);
                if(tag != null) {
                    transaction.addToBackStack(tag);
                }
                transaction.commit();
                fm.executePendingTransactions();
                break;
        }
    }*/

    /*@Override
    public void onBackPressed() {
        final FragmentManager fm = getSupportFragmentManager();
        Integer sbec = fm.getBackStackEntryCount();
        if(sbec > 0){
            String fragmentTag = fm.getBackStackEntryAt(sbec - 1).getName();
            fm.popBackStackImmediate();
            switch(fragmentTag){
                case "order":
                    fm.beginTransaction().replace(R.id.container, new CustomerFragment()).commit();
                    fm.executePendingTransactions();
                    break;
                case "transactionlist":
                    fm.beginTransaction().replace(R.id.container, new OrderFragment()).commit();
                    fm.executePendingTransactions();
                    break;
                case "customer":
                default:
                    showLogout();
                    break;
            }
        }else{
            showLogout();
        }
    }*/

    /* private void showLogout(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setTitle("Close Application").setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openFragment(new OrderFragment(), "menu");
                    }
                })
                .show();
    } */

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
