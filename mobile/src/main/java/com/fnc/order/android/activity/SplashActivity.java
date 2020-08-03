package com.fnc.order.android.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.R;
import com.fnc.order.android.database.DbConstants;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.services.OrdersService;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.google.api.core.NanoClock;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import hari.bounceview.BounceView;
import io.opencensus.resource.Resource;

import static com.fnc.order.android.database.DBHelper.DBPath;

public class SplashActivity extends BaseActivity {

    private Context ctx;
    private SharedData sp;
    private AlertDialog alertDialog;
    private ProgressDialog loader;
    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        active = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_splash);

        sp = SharedData.getInstance(ctx);
        if(sp.getData(SharedKey.DEFAULT_DOMAIN_SERVER_URL.getKey()).trim().equals("")) sp.saveData(SharedKey.DEFAULT_DOMAIN_SERVER_URL.getKey(), ServerConstants.DEFAULT_SERVER_URL);
        if(sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).trim().equals("")) sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "");
        if(sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()).trim().equals("")) sp.saveData(SharedKey.LOCAL_SERVER_URL.getKey(), "");
        if(sp.getData(SharedKey.DATABASE.getKey()).trim().equals("")) sp.saveData(SharedKey.DATABASE.getKey(), "");
        if(sp.getData(SharedKey.DATABASEID.getKey()).trim().equals("")) sp.saveData(SharedKey.DATABASEID.getKey(), "");
        if(sp.getData(SharedKey.REF_MAIN_BRANCH.getKey()).trim().equals("")) sp.saveData(SharedKey.REF_MAIN_BRANCH.getKey(), "");
        if(sp.getInt(SharedKey.SKU_VALIDATION.getKey()) == -1) sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 1);
        if(sp.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == -1) sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
        if(sp.getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == -1) sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 0);

        if(sp.getData(SharedKey.SUPPORT_USER.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_USER.getKey(), "");
        if(sp.getData(SharedKey.SUPPORT_PASSWORD.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_PASSWORD.getKey(), "");
        if(sp.getData(SharedKey.SUPPORT_EMP_ID.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_EMP_ID.getKey(), "");

        TedPermission.with(ctx).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    DcStaffs.getInstance(getApplicationContext()); // create database

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

                    if (!isTaskRoot()
                            && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                            && getIntent().getAction() != null
                            && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
                        finish();
                        return;
                    }

                    startProcedure();
                } catch(SecurityException e) {
                    e.printStackTrace();
                } finally {

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


        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void startProcedure() {
        String strCN = sp.getData(SharedKey.DATABASE.getKey());
//        strCN = "";
        if(strCN.equals("")) {
            if (Helper.isNetworkAvailable(this)) {
                alertDialog = actionDialog( ctx, "APP START-UP",
                        "Please enter your ADMIN ACCOUNT CREDENTIALS to initialize this app.",
                        "Username", "Password", "Branch",
                        "SUBMIT", new View.OnClickListener() {
                            public void onClick(View v) {
                                loader = Helper.showSpinnerDialog(ctx, "", "Posting... Please wait..."); loader.show();
                                clientSignin(((EditText) alertDialog.findViewById(R.id.et_edittext1)).getText().toString(),
                                    ((EditText) alertDialog.findViewById(R.id.et_edittext2)).getText().toString());
                            }
                        }, "", null, 0
                );
                EditText etPassword = (EditText) alertDialog.findViewById(R.id.et_edittext2);
//                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext1))).setText("admin@backoffice.com");
//                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext2))).setText("admin123");

                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialog);
            } else {
                alertRequireInternet();
            }
        } else {
//            sp.removeSinglePref(SharedKey.DATABASEID.getKey());
            if(sp.getData(SharedKey.DATABASEID.getKey()).equals("")) {
                loader = Helper.showSpinnerDialog(ctx, "", "Getting possible update... Please wait..."); loader.show();
                clientSignin(String.valueOf(sp.getData(SharedKey.REF_ADMIN_USER.getKey())), String.valueOf(sp.getData(SharedKey.REF_ADMIN_PASSWORD.getKey())));
            } else {
                LinkedList<aBranchlist> abl = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                        aBranchlistKey.DEVICEID.getKey() + " = ? ", new String[] { Helper.getImei(ctx, "") });
                if (abl.size() > 0 ) {
                    if (abl.get(0).getActive().equals("false")) {
                        getDeviceProfile("init");
                    } else {
                        if (abl.get(0).getBranchid() == 12345) {
                            showBranchDialog("reinit");
                        } else {
                            proceedNormal();
                        }
                    }
                } else {
                    getDeviceProfile("init");
                }
            }
        }
    }

    private AlertDialog alertdialogBL;
    private void showBranchDialog(final String type) {
        if (type.equals("init") || type.equals("reinit")) {
            alertdialogBL = actionDialog( ctx, "Assign Branch",
                    getString(R.string.branch_dialog_message),
                    "Username", "Password", "Branch",
                    "SUBMIT", new View.OnClickListener() {
                        public void onClick(View v) {
                            if (!refSelectedBranchId.equals("")) {
                                postBranchImei();
                            } else {
                                Toast.makeText(ctx, "Please select a branch.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, "", null, 1
            );

            loadSpinnerBranches();

            MaterialRippleLayout mlrReload = (MaterialRippleLayout) alertdialogBL.findViewById(R.id.mrl_reload);
            mlrReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loader = Helper.showSpinnerDialog(ctx, "Updating", "Please wait...."); loader.show();
                    getDeviceProfile("reload");
                }
            });

            alertdialogBL.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
            alertdialogBL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            BounceView.addAnimTo(alertdialogBL);
            Helper.dismissSpinnerDialog(loader);
        } else {
            loadSpinnerBranches();
        }
    }

    private LinkedList<aBranchlist> arrBranches = new LinkedList<>();
    private Spinner msBranches;
    private TextView tvBranchdescription;
    private String refSelectedBranchId = "";
    private void loadSpinnerBranches() {
        if(tvBranchdescription != null) tvBranchdescription.setText("Select branch....");
        refSelectedBranchId = "";

        if(alertdialogBL != null) {
            ArrayList<String> refAbx = DcBranchlist.getInstance(ctx).getDescriptions();
            arrBranches = new LinkedList<>();
            if (refAbx.size() > 0) {
                aBranchlist abr = new aBranchlist(0, "Select branch....",
                        "", "","", "", "", "", "");
                arrBranches.add(abr);
                for(int k=0; k<refAbx.size(); k++){
                    if (DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                            aBranchlistKey.BRANCHCODE.getKey() + " = ? ", new String[] { refAbx.get(k) }
                    ).size() > 0) {
                        abr = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                                aBranchlistKey.BRANCHCODE.getKey() + " = ? ", new String[] { refAbx.get(k) }
                        ).get(0);
                        arrBranches.add(abr);
                    }
                }
            }
            ArrayAdapter<aBranchlist> sadapter = new ArrayAdapter<aBranchlist>(ctx,
                    android.R.layout.simple_spinner_dropdown_item, arrBranches) {
                @Override
                public boolean isEnabled(int position) {
                    if(position == 0) { return false; }
                    else { return true; }
                }
                @Override
                public View getDropDownView(int pos, View cv, ViewGroup prnt) {
                    View view = super.getDropDownView(pos, cv, prnt);
                    TextView tv = (TextView) view;
                    if(pos == 0){
                        tv.setTextColor(Color.GRAY);
                        tv.setText(arrBranches.get(pos).getBranchcode());
                    } else {
                        tv.setTextColor(Color.DKGRAY);
//                        tv.setText(arrBranches.get(pos).getBranchcode() + " (" + arrBranches.get(pos).getDescription() + ")");
                        tv.setText(arrBranches.get(pos).getDescription() + " (" + arrBranches.get(pos).getBranchcode() + ")");
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, 88);
                    params.setMargins(20,0,10,0);
                    tv.setLayoutParams(params);
                    return view;
                }
            };
            tvBranchdescription = (TextView) alertdialogBL.findViewById(R.id.tv_branchdescription);
            tvBranchdescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    msBranches.performClick();
                }
            });
            msBranches = (Spinner) alertdialogBL.findViewById(R.id.ms_branches);
            msBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
                    if (pos != 0) {
                        /* tvBranchdescription.setText(arrBranches.get(pos).getBranchcode() +
                                " (" + arrBranches.get(pos).getDescription() + ")"); */
                        tvBranchdescription.setText(arrBranches.get(pos).getDescription() +
                                " (" + arrBranches.get(pos).getBranchcode() + ")");
                        refSelectedBranchId = String.valueOf(arrBranches.get(pos).getBranchid());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            msBranches.setAdapter(sadapter);
        }
    }

    private void getPreRequisite() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
        params.put("branchid", sp.getData(SharedKey.BRANCH_ID.getKey()));
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
            it.remove();
        }
        VolleyInteractor vipr = new VolleyInteractor();
        vipr.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
            @Override
            public void onRequestFail(VolleyError response, String type) { volleyRequestFail(response, type); }
        });
        vipr.getPreRequisite(ctx, params,
                strParams.replaceAll(" ", "%20"));
    }

    private void proceedNormal() {
        if (sp.getData(SharedKey.REF_MAIN_BRANCH.getKey()).trim().equals(sp.getData(SharedKey.BRANCH_DESCRIPTION.getKey()))) {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
        } else {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
        }

        LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
        if (sl.size() > 0) {
            Helper.dismissSpinnerDialog(loader);
            showActivity(LoginActivity.class);
        } else {
            if (Helper.isNetworkAvailable(this)) {
//                loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                showActivity(LoginActivity.class);
//                getPreRequisite();
            } else {
                alertRequireInternet();
            }
        }
    }

    private void clientSignin(String strUsername, String strPassword) {
//        strUsername = "admin@backoffice.com"; strPassword = "admin123";
        if (Helper.isNetworkAvailable(this)) {
            if(strUsername.matches("")){
                BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Error","Please enter username.", "CLOSE",
                        null, false) ); return;
            }
            if(strPassword.matches("")){
                BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Error","Please enter password.", "CLOSE",
                        null, false) ); return;
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("logdb", ServerConstants.LOGDB);
            params.put("userid", strUsername);
            params.put("pass", strPassword);
            VolleyInteractor vil = new VolleyInteractor();
            vil.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
                @Override
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(response, type); }
            });
            vil.login(ctx, params, "");

        } else {
            Helper.dismissSpinnerDialog(loader);
            BounceView.addAnimTo( Helper.okDialog(ctx,
                "Error on Internet Connection","This update requires live data.  Please check your connection!", "CLOSE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false)
            );
        }
    }

    private void alertRequireInternet() {
        BounceView.addAnimTo( Helper.okDialog( ctx,
            "Initialization Error","This app requires internet to initialize.  Please check your connection.",
            "CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            }, false) );
    }

    private void showActivity(final Class<?> cls) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ctx, cls));
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void volleyRequestSuccess(final String response, final String type) {
//    public void onRequestSuccess(final String response, final String type) {
        if (type.equals("login")) {
            try {
                JSONObject obj = new JSONObject(response);
                Helper.dismissSpinnerDialog(loader);
                if (obj.getString("dtcompany").equals("[]")) {
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Initialization Error","Invalid credentials",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false)
                    );
                } else {
                    JSONArray objArr = new JSONArray(obj.getString("dtcompany"));
                    JSONObject objx = new JSONObject(objArr.get(0).toString());
                    if (objx.getString("DatabaseID").equals("")) {
                        BounceView.addAnimTo( Helper.okDialog( ctx,
                            "Initialization Error","Invalid credentials",
                            "CLOSE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, false)
                        );
                    } else {
                        if(alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                        g_jsonobject = obj;
                        new checkAppConfig().execute(objx.getString("DatabaseID"));

                        /*String strCN = "";
                        if (objx.getString("DatabaseID").equals("BackofficeLive")) {
                            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 1);
                            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
                            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 0);
                            strCN = sp.getData(SharedKey.REF_DATABASE.getKey()).trim();
                        } else {
                            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 0);
                            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 0);
                            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 1);
                            strCN = objx.getString("DatabaseID");
                        }
                        sp.saveData(SharedKey.DATABASE.getKey(), strCN);

                        JSONArray objArrY = new JSONArray(obj.getString("dtuser"));
                        JSONObject objy = new JSONObject(objArrY.get(0).toString());
                        sp.saveData(SharedKey.REF_ADMIN_USER.getKey(), objy.getString("Email"));
                        sp.saveData(SharedKey.REF_ADMIN_PASSWORD.getKey(), objy.getString("Password"));
                        String strFN = objy.getString("FirstName") + "|" +
                                objy.getString("MiddleName") + "|" +
                                objy.getString("LastName");
                        String[] arrFN = strFN.split("\\|");
                        if (arrFN.length > 0) {
                            strFN = "";
                            for (int k = 0; k < arrFN.length; k++) {
                                if (!String.valueOf(arrFN[k]).trim().equals(""))
                                    strFN = strFN + arrFN[k] + " ";
                            }
                            sp.saveData(SharedKey.REF_ADMIN_FULLNAME.getKey(), strFN.trim());
                        }

                        String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
                        if (strImeiId.equals("")) {
                            getDeviceProfile("init");
                        } else {
                            proceedNormal();
                        }

                        loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show(); */
                    }
                }
            } catch (JSONException e) {
                Helper.dismissSpinnerDialog(loader);
                BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Initialization Error","Invalid credentials",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                alertDialog.show();
                            }
                        }, false) );
            }
        }
        if (type.equals("getpostbranchimei")) {
            if (response.toLowerCase().equals("true")) {
                getDeviceProfile("reinit");
            } else {
                BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Device Registration","Registration failed. Please contact IT support.",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false) );
            }
        }
        if (type.contains("getdeviceprofile|")) {
            final String[] strRef = type.split("\\|");
            Helper.dismissSpinnerDialog(loader);
            if (response.equals("[]")) {
                if (strRef[1].equals("init")) {
                    alertReferenceBranchesNotFound();
                } else {
                    alertDeviceNotAddedToServer(new String[] {"getdeviceprofile","postbranch"});
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray objArr = new JSONArray(response);
                            if (objArr.length() > 0) {
                                if (strRef[1].equals("init") || strRef[1].equals("proceed")) {
                                    DcBranchlist.getInstance(ctx).emptyBranchlist();
                                    for (int i = 0; i < objArr.length(); i++) {
                                        JSONObject rowObj = objArr.getJSONObject(i);
                                        aBranchlist br = new aBranchlist(
                                            rowObj.getInt("branchid"),
                                            rowObj.getString("branchcode").trim(),
                                            rowObj.getString("deviceid").trim(),
                                            rowObj.getString("description").trim(),
                                            rowObj.getString("deviceID1").trim(),
                                            rowObj.getString("active").trim(),
                                            rowObj.getString("customerID").trim(),
                                            rowObj.getString("old_branchid").trim(),
                                            rowObj.getString("old_customerid").trim()
                                        );
                                        DcBranchlist.getInstance(ctx).insertBranches(br);
                                    }

                                    LinkedList<aBranchlist> ablRs = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(aBranchlistKey.DEVICEID.getKey()
                                                    + " = ? AND " + aBranchlistKey.ACTIVE.getKey() + " = ?",
                                            new String[] { Helper.getImei(ctx, refSelectedBranchId), "true" } );
                                    if (ablRs.size() > 0) {
                                        aBranchlist ablRsx = ablRs.get(0);
                                        sp.saveData(SharedKey.IMEI_ID.getKey(), ablRsx.getDeviceID1().trim());
                                        sp.saveData(SharedKey.BRANCH_ID.getKey(), ablRsx.getBranchid().toString().trim());
                                        sp.saveData(SharedKey.BRANCH_CODE.getKey(), ablRsx.getBranchcode().trim());
                                        sp.saveData(SharedKey.BRANCH_DESCRIPTION.getKey(), ablRsx.getDescription().trim());

                                        if (sp.getData(SharedKey.REF_MAIN_BRANCH.getKey()).trim().equals(ablRsx.getDescription().trim())) {
                                            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
                                        } else {
                                            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
                                        }

                                        getPreRequisite();
                                    } else {
                                        if (strRef[1].equals("init")) {
                                            postBranchImei();
                                        } else {
                                            getPreRequisite();
                                        }
                                    }
                                } else {
                                    JSONObject rowObj = objArr.getJSONObject(0);
                                    if (rowObj.getString("active").trim().equals("false")) {
                                        alertDeviceNotAddedToServer(new String[] {"","postbranch"});
                                    } else {
                                        if (rowObj.getString("branchid").equals("12345")) { // TEST(1234) is not valid so select a valid branch
                                            showBranchDialog("reinit");
                                        } else {
                                            loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                                            sp.saveData(SharedKey.IMEI_ID.getKey(), rowObj.getString("deviceid").trim());
                                            sp.saveData(SharedKey.BRANCH_ID.getKey(), rowObj.getString("branchid").trim());
                                            sp.saveData(SharedKey.BRANCH_CODE.getKey(), rowObj.getString("branchcode").trim());
                                            sp.saveData(SharedKey.BRANCH_DESCRIPTION.getKey(), rowObj.getString("description").trim());

                                            if (sp.getData(SharedKey.REF_MAIN_BRANCH.getKey()).trim().equals(rowObj.getString("description").trim())) {
                                                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
                                            } else {
                                                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
                                            }

                                            DcBranchlist.getInstance(ctx).updateBranchlist(rowObj.getString("deviceid").trim(), aBranchlistKey.ACTIVE, "true");

                                            getDeviceProfile("proceed");
                                        }
                                    }
                                }
                            } else {
                                Log.v("dsxtae", "4");
                                alertReferenceBranchesNotFound();
                            }
                        } catch (JSONException e) {
                            alertDataSyncError();
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }
        }
        if (type.equals("getprerequisite")) {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.length() > 0) {
                    DcStaffs.getInstance(ctx).emptyStaffslist();
                    Helper.insertDefaultStaffs(ctx);
                    JSONArray sArr = obj.getJSONArray("staff");
                    if (sArr.length() > 0) {
                        for (int i = 0; i < sArr.length(); i++) {
                            JSONObject rowObj = sArr.getJSONObject(i);
                            aStaffs sl = new aStaffs(
                                rowObj.getInt("empId"),
                                rowObj.getString("refempno").equals("null") ? "-1" : rowObj.getString("refempno"),
                                rowObj.getString("empNo"),
                                rowObj.getString("Email"),
                                rowObj.getString("name"),
                                rowObj.getInt("Branch"),
                                rowObj.getInt("Jobtitle"),
                                rowObj.getString("pass"),
                                rowObj.getString("active"),
                                rowObj.getString("ismobileadmin")
                            );
                            DcStaffs.getInstance(ctx).insertStaffs(sl);
                        }
                    }
                    Helper.dismissSpinnerDialog(loader);
                    if (alertdialogBL != null) alertdialogBL.dismiss();
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                            "Initialization Success","You can now login using your registered account.",
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ctx, LoginActivity.class));
                                    finish();
                                }
                            }, false) );
                } else {
                    alertDataSyncError();
                }
            } catch (JSONException e) {
                alertDataSyncError();
                e.printStackTrace();
            }
        }
    }

    private void volleyRequestFail(VolleyError response, String type){
//    public void onRequestFail(VolleyError response, String type){
        alertDataSyncError();
        /*if (type.equals("login")) {
            BounceView.addAnimTo( Helper.okDialog( ctx,
                "Initialization Failed","Please contact IT support.",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false) );
        }
        if (type.equals("getdeviceprofile")) {
            BounceView.addAnimTo( Helper.okDialog( ctx,
                "Unrecognized Device", getString(R.string.unrecognized_device),
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false) );
        }
        if (type.equals("getprerequisite")) {
            BounceView.addAnimTo(Helper.okDialog(ctx,
                    "Unrecognized Device", getString(R.string.unrecognized_device),
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false));
        }*/
    }

    private AlertDialog actionDialog(final Context activity, String title, String message,
             String strLabel1, String strLabel2, String strLabel3, String okButtonCaption,
             View.OnClickListener onClickListener, String cancelButtonCaption,
             View.OnClickListener cancelClickListener, Integer flagType) {

        SharedData spx = SharedData.getInstance(this);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_init, null);

        ((TextView) layout.findViewById(R.id.tv_dialog_title)).setText(title);
        ((TextView) layout.findViewById(R.id.tv_message)).setText(message);

        ((TextView) layout.findViewById(R.id.tv_label1)).setText(strLabel1);
        ((TextView) layout.findViewById(R.id.tv_label2)).setText(strLabel2);

        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(onClickListener);

        if(flagType == 1) {
            ((LinearLayout) layout.findViewById(R.id.ll_signin_box)).setVisibility(View.GONE);
            ((LinearLayout) layout.findViewById(R.id.ll_branch_box)).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) layout.findViewById(R.id.ll_branch_box)).setVisibility(View.GONE);
            ((LinearLayout) layout.findViewById(R.id.ll_signin_box)).setVisibility(View.VISIBLE);
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    ////////////////// requests //////////////////

    private void getDeviceProfile(String type) {
        if (loader != null) Helper.dismissSpinnerDialog(loader);
        loader = Helper.showSpinnerDialog(ctx, "Requesting Info", "Please wait..."); loader.show();

        if (Helper.isNetworkAvailable(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            params.put("deviceid", type.equals("init") || type.equals("proceed") ? "n/a" : Helper.getImei(ctx, ""));
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                it.remove();
            }
            VolleyInteractor vidp = new VolleyInteractor();
            vidp.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
                @Override
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(response, type); }
            });
            vidp.getDeviceProfile(getApplicationContext(), params, strParams
                    .replaceAll(" ", "%20"), type);
        } else {
            alertRequireInternet();
        }
    }

    private void postBranchImei() {
        if (Helper.isNetworkAvailable(this)) {
            if (loader != null) Helper.dismissSpinnerDialog(loader);
            loader = Helper.showSpinnerDialog(ctx, "Processing Registration", "Please wait..."); loader.show();
            if (alertdialogBL != null) alertdialogBL.dismiss();
            if (alertDialog != null) alertDialog.dismiss();

            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            params.put("branchid", refSelectedBranchId.equals("") ? "12345" /*mother account*/ : refSelectedBranchId );
            params.put("deviceid", Helper.getImei(ctx, refSelectedBranchId).trim());
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }
            VolleyInteractor viri = new VolleyInteractor();
            viri.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
                @Override
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(response, type); }
            });
            viri.postBranchImei(getApplicationContext(), params,
                    strParams.replaceAll(" ", "%20"));
        } else {
            alertRequireInternet();
        }
    }

    ////////////////// alerts //////////////////

    private void alertReferenceBranchesNotFound() {
        Helper.dismissSpinnerDialog(loader);
        BounceView.addAnimTo( Helper.okDialog( ctx,
            "Initialization Error","Reference branches not found, please contact IT support.",
            "END APP", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            }, false) );
    }

    private void alertDeviceNotAddedToServer(final String[] strRef) {
        Log.d("dsximei", "alertDeviceNotAddedToServer");
        BounceView.addAnimTo( Helper.okDialog( ctx,
            "Device Registration",
            "This device with ID# " + Helper.getImei(ctx, "") + " is NOT YET ACTIVATED. Please contact IT support",
            "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (strRef[1].equals("postbranch")) {
                        postBranchImei();
                    }
                }
            }, false)
        );
    }

    private void alertDataSyncError() {
        Helper.dismissSpinnerDialog(loader);
        BounceView.addAnimTo( Helper.okDialog( ctx,
            "Data Sync Error","Data Sync Error, please contact IT support",
            "CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            }, false) );
    }


    private JSONObject g_jsonobject;
    private Storage storageinit;
    private class checkAppConfig extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String strDbID = params[0];
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw", ctx.getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                    .setCredentials(credentials).setClock(NanoClock.getDefaultClock())
                    .setProjectId(GlobalConstants.GCP_PROJECTID).build().getService();
                try {
                    BlobId blobId = BlobId.of(GlobalConstants.GCP_REFERENCE, "config.log");
                    Blob blob = storageinit.get(blobId);
                    byte[] bytes =  blob.getContent(Blob.BlobSourceOption.generationMatch());
                    return "Success|" + new String(bytes, "UTF-8") + "|" + strDbID;
                } catch (Exception e) {
                    return "Error| exception = " + e.getLocalizedMessage();
                }
            } catch (IOException io) {
                return "Error| io";
            } catch (RuntimeException e) {
                return "Error| io";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String[] resMsg = result.split("\\|");
            if (resMsg[0].equals("Success")) {
                try {
                    String strDatabaseId = resMsg[2];
                    JSONArray objArr = new JSONArray(resMsg[1]);
                    if (objArr.length() > 0) {
                        Boolean isSuccess = false;
                        for (int i = 0; i < objArr.length(); i++) {
                            JSONObject rowObj = objArr.getJSONObject(i);
                            if (rowObj.getString("database_id").equals(strDatabaseId)) {
                                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), rowObj.getString("server_url"));
                                sp.saveData(SharedKey.LOCAL_SERVER_URL.getKey(), rowObj.getString("local_url"));
                                sp.saveData(SharedKey.DATABASE.getKey(), rowObj.getString("cn"));
                                sp.saveData(SharedKey.DATABASEID.getKey(), strDatabaseId);
                                sp.saveData(SharedKey.REF_MAIN_BRANCH.getKey(), rowObj.getString("main_branch"));
                                sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), rowObj.getInt("old_sku_validation"));
                                sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), rowObj.getInt("preload_items"));
                                sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), rowObj.getInt("saved_product_items"));
                                sp.saveData(SharedKey.SUPPORT_USER.getKey(), rowObj.getString("support_user"));
                                sp.saveData(SharedKey.SUPPORT_PASSWORD.getKey(), rowObj.getString("support_password"));
                                sp.saveData(SharedKey.SUPPORT_EMP_ID.getKey(), rowObj.getString("support_employee_id"));
                                isSuccess = true;
                            }
                        }
                        if (!isSuccess) {
                            alertDataSyncError();
                        } else {
//                            Toast.makeText(ctx, "SUCCESS", Toast.LENGTH_SHORT).show();

                            JSONArray objArrY = new JSONArray(g_jsonobject.getString("dtuser"));
                            JSONObject objy = new JSONObject(objArrY.get(0).toString());
                            sp.saveData(SharedKey.REF_ADMIN_USER.getKey(), objy.getString("Email"));
                            sp.saveData(SharedKey.REF_ADMIN_PASSWORD.getKey(), objy.getString("Password"));
                            String strFN = objy.getString("FirstName") + "|" +
                                    objy.getString("MiddleName") + "|" +
                                    objy.getString("LastName");
                            String[] arrFN = strFN.split("\\|");
                            if (arrFN.length > 0) {
                                strFN = "";
                                for (int k = 0; k < arrFN.length; k++) {
                                    if (!String.valueOf(arrFN[k]).trim().equals(""))
                                        strFN = strFN + arrFN[k] + " ";
                                }
                                sp.saveData(SharedKey.REF_ADMIN_FULLNAME.getKey(), strFN.trim());
                            }

                            String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
                            if (strImeiId.equals("")) {
                                getDeviceProfile("init");
                            } else {
                                proceedNormal();
                            }

                            Helper.dismissSpinnerDialog(loader);
                            loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                        }
                    } else {
                        alertDataSyncError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    alertDataSyncError();
                }
            } else {
                alertDataSyncError();
            }
        }
        @Override
        protected void onPreExecute() { Log.d("gcpe", "Task Upload Starting"); }
        @Override
        protected void onProgressUpdate(Integer... values) { Log.d("gcpu", "Running " + + values[0]); }
    }
}