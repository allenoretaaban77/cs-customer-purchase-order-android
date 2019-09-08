package com.fnc.order.android.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.R;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

public class SplashActivity extends BaseActivity implements VolleyCallback {

    private Context ctx;
    private SharedData sp;
    private AlertDialog alertDialog;
    private ProgressDialog loader;

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
        Helper.setLogo((ImageView) findViewById(R.id.iv_logo), ctx);

        sp = SharedData.getInstance(ctx);
        if(sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), ServerConstants.SERVER_URL);
        }
        if(sp.getData(SharedKey.DATABASE.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DATABASE.getKey(), ServerConstants.CN);
        }
        if(sp.getInt(SharedKey.SKU_VALIDATION.getKey()) == -1) {
            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 1);
        }
        if(sp.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == -1) {
            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
        }
        if(sp.getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == -1) {
            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 0);
        }

        /*SharedData sp = SharedData.getInstance(ctx);
        switch (sp.getData(SharedKey.DATABASE.getKey())) {
            case "massive":
                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".MASSIVES"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".FNC"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
            default:
                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(BuildConfig.APPLICATION_ID,  BuildConfig.APPLICATION_ID + ".FNC"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                getPackageManager().setComponentEnabledSetting(
                        new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".MASSIVES"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                break;
        }*/

//        TedPermission.with(this).setPermissionListener(new PermissionListener() {
//               @Override
//               public void onPermissionGranted() {
//                   new DBHelper(getApplicationContext());
//                   if(!SharedData.getInstance(ctx).isPrefExists(API.IDENTITY_ID.getApi())) {
//                       showActivity(LoginActivity.class);
//                   }else{
////                        showActivity(MainActivity.class);
//                       showActivity(LoginActivity.class);
//                   }
//               }
//               @Override
//               public void onPermissionDenied(List<String> deniedPermissions) {
//                   finish();
//               }
//           }
//        ).setDeniedMessage("If you reject permission, you cannot use this application.")
//                .setPermissions(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                ).check();

        final VolleyInteractor vidp = new VolleyInteractor();
        vidp.registerCallback(this);
        String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
        strImeiId = "";
        if (strImeiId.equals("")) {
            if (Helper.isNetworkAvailable(this)) {
                TedPermission.with(ctx).setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("cn", ServerConstants.CN);
                            params.put("deviceid", Helper.getImei(ctx));
//                            params.put("deviceid", "353800100112222"); // timog
                            Iterator it = params.entrySet().iterator();
                            String strParams = "";
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                                it.remove();
                            }
                            vidp.getDeviceProfile(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
                        } catch(SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finishAndRemoveTask();
                    }
                }).setDeniedMessage("Specified permission is required to continue using this application.")
                        .setPermissions(
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ).check();
            }else{
                alertDialog = Helper.okDialog(ctx,
                    "Initialization Error","This app requires internet to initialize.  Please check your connection.",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            }
        } else {
            LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
            if (sl.size() > 0) {
                showActivity(LoginActivity.class);
//                showActivity(MainActivity.class);
            } else {
                if (Helper.isNetworkAvailable(this)) {
                    loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();

                    VolleyInteractor vipr = new VolleyInteractor();
                    vipr.registerCallback(this);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("cn", ServerConstants.CN);
                    SharedData sppr = SharedData.getInstance(ctx);
                    params.put("branchid", sppr.getData(SharedKey.BRANCH_ID.getKey()));
                    Iterator it = params.entrySet().iterator();
                    String strParams = "";
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                        it.remove();
                    }
                    vipr.getPreRequisite(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
                } else {
                    alertDialog = Helper.okDialog(ctx,
                        "Initialization Error","This app requires internet to initialize.  Please check your connection.",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }, false);
                    BounceView.addAnimTo(alertDialog);
                }
            }
        }
    }

    private void showActivity(final Class<?> cls) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), cls));
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    public void onRequestSuccess(final String response, String type) {
        if (type.equals("getdeviceprofile")) {
            if (response.equals("[]")) {
                alertDialog = Helper.okDialog(ctx,
                    "Unrecognized Device","Unrecognized Device, please contact IT support",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            } else {
                loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();

                final VolleyInteractor vipr = new VolleyInteractor();
                vipr.registerCallback(this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray objArr = new JSONArray(response);
                            JSONObject obj = new JSONObject(objArr.get(0).toString());
                            sp.saveData(SharedKey.IMEI_ID.getKey(), obj.getString("deviceid"));
                            sp.saveData(SharedKey.BRANCH_ID.getKey(), obj.getString("branchid"));
                            sp.saveData(SharedKey.BRANCH_CODE.getKey(), obj.getString("branchcode"));
                            sp.saveData(SharedKey.BRANCH_DESCRIPTION.getKey(), obj.getString("description"));

                            HashMap<String, String> params = new HashMap<>();
                            params.put("cn", ServerConstants.CN);
                            params.put("branchid", obj.getString("branchid"));
                            Iterator it = params.entrySet().iterator();
                            String strParams = "";
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                                it.remove();
                            }
                            vipr.getPreRequisite(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
                        } catch (JSONException e) {
                            Helper.dismissSpinnerDialog(loader);
                            alertDialog = Helper.okDialog(ctx,
                                "Data Sync Error","Data Sync Error, please contact IT support",
                                "CLOSE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finishAndRemoveTask();
                                    }
                                }, false);
                            BounceView.addAnimTo(alertDialog);
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }
        }
        if (type.equals("getprerequisite")) {
            DcStaffs.getInstance(ctx).emptyStaffslist();
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.length() > 0) {
                    JSONArray sArr = obj.getJSONArray("staff");
                    if (sArr.length() > 0) {
                        DcStaffs.getInstance(ctx).insertStaffs(Helper.defaultStaff(ctx)); // add main
                        for (int i = 0; i < sArr.length(); i++) {
                            JSONObject rowObj = sArr.getJSONObject(i);
                            aStaffs sl = new aStaffs(
                                rowObj.getInt("empId"),
                                rowObj.getString("refempno").equals("null") ? -1 : rowObj.getInt("refempno"),
                                rowObj.getString("empNo"),
                                rowObj.getString("Email"),
                                rowObj.getString("name"),
                                rowObj.getInt("Branch"),
                                rowObj.getInt("Jobtitle"),
                                rowObj.getString("pass"),
                                rowObj.getString("active")
                            );
                            DcStaffs.getInstance(ctx).insertStaffs(sl);
                        }
                    }
                    Helper.dismissSpinnerDialog(loader);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    Helper.dismissSpinnerDialog(loader);
                    alertDialog = Helper.okDialog(ctx,
                        "Data Sync Error","Data Sync Error, please contact IT support",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }, false);
                    BounceView.addAnimTo(alertDialog);
                }
            } catch (JSONException e) {
                Helper.dismissSpinnerDialog(loader);
                alertDialog = Helper.okDialog(ctx,
                    "Data Sync Error","Data Sync Error, please contact IT support",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            }
        }
    }

    public void onRequestFail(VolleyError response, String type){
        if (type.equals("getdeviceprofile")) {
            alertDialog = Helper.okDialog(ctx,
                "Unrecognized Device","Unrecognized Device, please contact IT support.",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false);
            BounceView.addAnimTo(alertDialog);
        }
        if (type.equals("getprerequisite")) {
            alertDialog = Helper.okDialog(ctx,
                "Unrecognized Device","Unrecognized Device, please contact IT support.",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false);
            BounceView.addAnimTo(alertDialog);
        }

    }
}
