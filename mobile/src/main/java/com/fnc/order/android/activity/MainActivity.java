package com.fnc.order.android.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.fragment.CustomerFragment;
import com.fnc.order.android.fragment.OrderFragment;
import com.fnc.order.android.fragment.TransactionFragment;
import com.fnc.order.android.services.OrdersService;
import com.fnc.receiving.android.R;

import hari.bounceview.BounceView;

import static android.content.Context.ACTIVITY_SERVICE;

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

        String svcname = "OrderService";
        Boolean isSvcRunning = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
            if(service.service.getClassName().indexOf(svcname)>0){
                isSvcRunning = true;
            }
        }

        if (!isSvcRunning) {
            startService(new Intent(getBaseContext(), OrdersService.class));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startService(new Intent(getBaseContext(), OrdersService.class));
//    //                    startForegroundService(mServiceIntent);
//            } else {
//                startService(new Intent(getBaseContext(), OrdersService.class));
//    //                    startService(mServiceIntent);
//            }
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

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new CustomerFragment(), "customer_fragment")
            .addToBackStack(null)
            .commit();
    }

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
}
