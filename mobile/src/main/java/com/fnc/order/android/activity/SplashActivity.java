package com.fnc.order.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.R;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.utilities.SharedData;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class SplashActivity extends BaseActivity {

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);

        TedPermission.with(this).setPermissionListener(new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    new DBHelper(getApplicationContext());
//                    showActivity(LoginActivity.class);
                    if(!SharedData.getInstance(ctx).isPrefExists(API.IDENTITY_ID.getApi())) {
                        showActivity(LoginActivity.class);
                    }else{
                        showActivity(MainActivity.class);
                    }
                }
                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    finish();
                }
            }
        ).setDeniedMessage("If you reject permission, you cannot use this application.")
        .setPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).check();
    }

    private void showActivity(final Class<?> cls) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), cls));
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}
