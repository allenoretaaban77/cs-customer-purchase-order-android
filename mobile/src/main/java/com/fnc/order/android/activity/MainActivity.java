package com.fnc.order.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.fragment.CustomerFragment;
import com.fnc.order.android.fragment.OrderFragment;
import com.fnc.order.android.fragment.TransactionFragment;
import com.fnc.order.android.R;

public class MainActivity extends BaseActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        setContentView(R.layout.activity_main);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        openFragment(new OrderFragment(), "order");
        openFragment(new CustomerFragment(), "customer");
    }

    private void openFragment(Fragment fragment, String tag) {
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
    }

    @Override
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
    }

    private void showLogout(){
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
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openFragment(new OrderFragment(), "menu");
                    }
                })
                .show();
    }
}
