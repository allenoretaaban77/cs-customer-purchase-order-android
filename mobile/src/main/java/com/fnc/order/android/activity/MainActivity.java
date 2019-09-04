package com.fnc.order.android.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.R;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.fragment.CustomerFragment;
import com.fnc.order.android.fragment.OrderFragment;
import com.fnc.order.android.fragment.SearchItemFragment;
import com.fnc.order.android.fragment.TransactionFragment;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.aItemlist;
import com.fnc.order.android.services.OrdersService;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

import static android.content.Context.ACTIVITY_SERVICE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends BaseActivity {

    Context ctx;
    private AlertDialog alertDialog;

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

        String svcname = "OrdersService";
        Boolean isSvcRunning = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
            if(service.service.getClassName().indexOf(svcname)>0){
                isSvcRunning = true;
            }
        }

        if (!isSvcRunning) {
            startService(new Intent(getBaseContext(), OrdersService.class));
        }

//        OrdersService mSensorService = new OrdersService(getApplicationContext());
//        Intent mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
//        if (!isMyServiceRunning(mSensorService.getClass())) {
//
//        }

        setContentView(R.layout.activity_main);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        openFragment(new OrderFragment(), "order");
//        openFragment(new CustomerFragment(), "customer");

        Helper.changePage(ctx, getSupportFragmentManager(), new CustomerFragment(),
            "customer_fragment", "main_page");
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
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new CustomerFragment(), "customer_fragment")
                .addToBackStack(null).commit();
        } else if (refPage.equals("order_fragment")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new OrderFragment(), "order_fragment")
                    .addToBackStack(null).commit();
        } else {
            alertDialog = Helper.okCancelDialog(ctx,
                    "Closing Application", "Are you sure you want to close this app?",
                    "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, "Cancel", null, false
            );
            BounceView.addAnimTo(alertDialog);
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        String svcname = "OrderService";
//        Boolean isSvcRunning = false;
//        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
//            if(service.service.getClassName().indexOf(svcname)>0){
//                isSvcRunning = true;
//            }
//        }
//
//        if (!isSvcRunning) {
//            startService(new Intent(getBaseContext(), OrdersService.class));
//        }
//    }
}
