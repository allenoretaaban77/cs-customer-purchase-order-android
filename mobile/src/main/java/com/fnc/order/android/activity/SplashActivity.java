package com.fnc.order.android.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
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
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.R;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.services.OrdersService;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import hari.bounceview.BounceView;

public class SplashActivity extends BaseActivity {

    private Context ctx;
    private SharedData sp;
    private AlertDialog alertDialog;
    private ProgressDialog loader;
    private JSONObject g_jsonobject;
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
        if(sp.getData(SharedKey.CLIENT_ID.getKey()).trim().equals("")) sp.saveData(SharedKey.CLIENT_ID.getKey(), "");
        if(sp.getData(SharedKey.DATABASE.getKey()).trim().equals("")) sp.saveData(SharedKey.DATABASE.getKey(), "");
        if(sp.getData(SharedKey.DATABASEID.getKey()).trim().equals("")) sp.saveData(SharedKey.DATABASEID.getKey(), "");
        if(sp.getData(SharedKey.REF_MAIN_BRANCH_ID.getKey()).trim().equals("")) sp.saveData(SharedKey.REF_MAIN_BRANCH_ID.getKey(), "");
        if(sp.getInt(SharedKey.SKU_VALIDATION.getKey()) == -1) sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 0);
        if(sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == -1) sp.saveInt(SharedKey.REF_EMP_VALIDATION.getKey(), 0);
        if(sp.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == -1) sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 0);
        if(sp.getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == -1) sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 0);

        if(sp.getData(SharedKey.SUPPORT_USER.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_USER.getKey(), "");
        if(sp.getData(SharedKey.SUPPORT_PASSWORD.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_PASSWORD.getKey(), "");
        if(sp.getData(SharedKey.SUPPORT_EMP_ID.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_EMP_ID.getKey(), "");
        if(sp.getData(SharedKey.SUPPORT_REF_EMP_ID.getKey()).trim().equals("")) sp.saveData(SharedKey.SUPPORT_REF_EMP_ID.getKey(), "");

        if(sp.getInt(SharedKey.SHOW_PRICE_COL.getKey()) == -1) sp.saveInt(SharedKey.SHOW_PRICE_COL.getKey(), 0);
        if(sp.getInt(SharedKey.SHOW_TOTAL_COL.getKey()) == -1) sp.saveInt(SharedKey.SHOW_TOTAL_COL.getKey(), 0);
        if(sp.getInt(SharedKey.SHOW_FREE_COL.getKey()) == -1) sp.saveInt(SharedKey.SHOW_FREE_COL.getKey(), 0);
        if(sp.getInt(SharedKey.COMPUTE_QTY_ONLY.getKey()) == -1) sp.saveInt(SharedKey.COMPUTE_QTY_ONLY.getKey(), 0);
        if(sp.getInt(SharedKey.SHOW_SEARCH_PRICE.getKey()) == -1) sp.saveInt(SharedKey.SHOW_SEARCH_PRICE.getKey(), 0);
        if(sp.getInt(SharedKey.PER_AGENT_SETUP.getKey()) == -1) sp.saveInt(SharedKey.PER_AGENT_SETUP.getKey(), 0);
        if(sp.getInt(SharedKey.ENABLE_REPORT_TYPE.getKey()) == -1) sp.saveInt(SharedKey.ENABLE_REPORT_TYPE.getKey(), 0);
        if(sp.getInt(SharedKey.SHOW_SUMMARY_ON_POST.getKey()) == -1) sp.saveInt(SharedKey.SHOW_SUMMARY_ON_POST.getKey(), 0);
        if(sp.getInt(SharedKey.REF_PO_NO.getKey()) == -1) sp.saveInt(SharedKey.REF_PO_NO.getKey(), 0);

        if(sp.getData(SharedKey.LAST_SUCC_CUSTSYNC.getKey()).trim().equals("")) sp.saveData(SharedKey.LAST_SUCC_CUSTSYNC.getKey(), "");

        Log.d("asasa", android.os.Build.MODEL);

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
                            loader = Helper.showSpinnerDialog(ctx, "Client Sign-In", "Posting... Please wait..."); loader.show();
                            clientSignin(((EditText) alertDialog.findViewById(R.id.et_edittext1)).getText().toString(),
                                ((EditText) alertDialog.findViewById(R.id.et_edittext2)).getText().toString());
                        }
                    }, "", null, 0
                );
                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext1))).setText("admin@demo.com");
                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext2))).setText("admin123");
                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialog);
            } else {
                alertRequireInternet();
            }
        } else {
            LinkedList<aBranchlist> bRsImId = DcBranchlist.getInstance(ctx).filterMultiple(false,
                aBranchlistKey.DEVICEID.getKey() + "=? AND " + aBranchlistKey.BRANCHID.getKey() + "=?",
                new String[] { Helper.getImei(ctx), sp.getData(SharedKey.BRANCH_ID.getKey()) }, "");
            if (bRsImId.size() > 0 ) {
                if (bRsImId.get(0).getActive().equals("false")) {
                    refSelectedBranchId = sp.getData(SharedKey.BRANCH_ID.getKey());
                    alertDeviceNotAddedToServer();
                } else {
                    if (bRsImId.get(0).getBranchid() == 12345) {
                        showBranchDialog();
                    } else {
                        showActivity(LoginActivity.class);
                    }
                }
            } else {
                getDeviceProfile("validate");
            }
        }
    }

    private AlertDialog alertdialogBL;
    private void showBranchDialog() {
        alertdialogBL = actionDialog( ctx, "ASSIGN BRANCH",
                getString(R.string.branch_dialog_message),
                "Username", "Password", "Branch",
                "SUBMIT", new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!refSelectedBranchId.equals("")) {
                            postBranchImei();
                        } else {
                            Toast.makeText(ctx, "Please SELECT a BRANCH.", Toast.LENGTH_SHORT).show();
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
                getDeviceProfile("getbranches");
            }
        });

        alertdialogBL.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
        alertdialogBL.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BounceView.addAnimTo(alertdialogBL);
        Helper.dismissSpinnerDialog(loader);
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
                aBranchlist abr = new aBranchlist(0L, "Select branch....",
                "", "","", "", "", "", "");
                arrBranches.add(abr);
                for(int k=0; k<refAbx.size(); k++){
                    if (DcBranchlist.getInstance(ctx).filterMultiple( false,
                    aBranchlistKey.BRANCHCODE.getKey() + " = ? ", new String[] { refAbx.get(k) }, "").size() > 0) {
                        abr = DcBranchlist.getInstance(ctx).filterMultiple( false,
                        aBranchlistKey.BRANCHCODE.getKey() + " = ? ", new String[] { refAbx.get(k) }, "").get(0);
                        arrBranches.add(abr);
                    }
                }
            }
            ArrayAdapter<aBranchlist> sadapter = new ArrayAdapter<aBranchlist>(ctx,
            android.R.layout.simple_spinner_dropdown_item, arrBranches) {
                @Override public boolean isEnabled(int position) {
                    if(position == 0) { return false; }
                    else { return true; }
                }
                @Override public View getDropDownView(int pos, View cv, ViewGroup prnt) {
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

    private void proceedNormal() {
        if (Helper.checkBranchProfile(ctx).get(0).getBranchid().toString().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH_ID.getKey()))) {
            if (Helper.getScrRatio(ctx) > 0.6) {
                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
            } else {
                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
            }
        } else {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
        }

        LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
        if (sl.size() > 0) {
            Helper.dismissSpinnerDialog(loader);
            showActivity(LoginActivity.class);
        } else {
            if (Helper.isNetworkAvailable(this)) {
                showActivity(LoginActivity.class);
            } else {
                alertRequireInternet();
            }
        }
    }

    private void clientSignin(String strUsername, String strPassword) {
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
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(type); }
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
        if (type.equals("login")) {
            try {
                JSONObject obj = new JSONObject(response);
                Helper.dismissSpinnerDialog(loader);
                if (obj.getString("dtcompany").equals("[]")) {
                    BounceView.addAnimTo( Helper.okDialog( ctx, "Initialization Error","Invalid credentials",
                    "CLOSE", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }}, false) );
                } else {
                    JSONArray objArr = new JSONArray(obj.getString("dtcompany"));
                    JSONObject objx = new JSONObject(objArr.get(0).toString());
                    if (objx.getString("DatabaseID").equals("")) {
                        BounceView.addAnimTo( Helper.okDialog( ctx, "Initialization Error","Invalid credentials",
                        "CLOSE", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }}, false) );
                    } else {
                        if(alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();

                        JSONArray objArrY = new JSONArray(obj.getString("dtuser"));
                        JSONObject objy = new JSONObject(objArrY.get(0).toString());
                        sp.saveData(SharedKey.CLIENT_ID.getKey(), objy.getString("identityId"));
                        sp.saveData(SharedKey.REF_ADMIN_USER.getKey(), objy.getString("Email"));
                        sp.saveData(SharedKey.REF_ADMIN_PASSWORD.getKey(), objy.getString("Password"));
                        String strFN = objy.getString("FirstName") + "|" + objy.getString("MiddleName") + "|" +
                        objy.getString("LastName");
                        String[] arrFN = strFN.split("\\|");
                        if (arrFN.length > 0) {
                            strFN = "";
                            for (int k = 0; k < arrFN.length; k++) {
                                if (!String.valueOf(arrFN[k]).trim().equals("")) strFN = strFN + arrFN[k] + " ";
                            }
                            sp.saveData(SharedKey.REF_ADMIN_FULLNAME.getKey(), strFN.trim());
                        }

                        loader = Helper.showSpinnerDialog(ctx, "Fetching Settings", "Please wait..."); loader.show();
                        new getSettings().execute("");
                    }
                }
            } catch (JSONException e) {
                Helper.dismissSpinnerDialog(loader);
                BounceView.addAnimTo( Helper.okDialog( ctx, "Initialization Error","Invalid credentials",
                "CLOSE", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }}, false) );
            }
        }
        if (type.equals("getpostbranchimei")) {
            if (alertdialogBL != null) alertdialogBL.dismiss();
            if (response.toLowerCase().equals("true")) {
                getDeviceProfile("getbranches");
            } else {
                BounceView.addAnimTo( Helper.okDialog( ctx, "Device Registration Failed","Please contact IT support.",
                "CLOSE", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }}, false) );
            }
        }
        if (type.contains("getdeviceprofile")) {
            final String[] strRef = type.split("\\|");
            if (response.equals("[]")) {
                Helper.dismissSpinnerDialog(loader);
                if (strRef[1].equals("validate")) {
                    getDeviceProfile("getbranches");
                } else {
                    alertDeviceNotAddedToServer();
                }
            } else {
                if (strRef[1].equals("validate")) {
                    getDeviceProfile("getbranches");
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray objArr = new JSONArray(response);
                                if (objArr.length() > 0) {
                                    if (strRef[1].equals("getbranches")) {
                                        DcBranchlist.getInstance(ctx).emptyList();
                                        for (int i = 0; i < objArr.length(); i++) {
                                            JSONObject rowObj = objArr.getJSONObject(i);
                                            aBranchlist br = new aBranchlist(
                                                rowObj.getLong("branchid"),
                                                rowObj.getString("branchcode").trim(),
                                                rowObj.getString("deviceid").trim(),
                                                rowObj.getString("description").trim(),
                                                rowObj.getString("deviceID1").trim(),
                                                rowObj.getString("active").trim(),
                                                rowObj.getString("customerID").trim(),
                                                rowObj.getString("old_branchid").trim(),
                                                rowObj.getString("old_customerid").trim()
                                            );
                                            DcBranchlist.getInstance(ctx).insertItems(br);
                                        }
                                        Helper.dismissSpinnerDialog(loader);

                                        LinkedList<aBranchlist> bRs = DcBranchlist.getInstance(ctx).filterMultiple( false,
                                            aBranchlistKey.DEVICEID.getKey() + " LIKE ? ", new String[] { "%" }, "");
                                        if (bRs.size()>0) {
                                            String strDeviceId = Helper.getImei(ctx);
                                            String strBranchId = sp.getData(SharedKey.BRANCH_ID.getKey());
                                            LinkedList<aBranchlist> bRsImId = DcBranchlist.getInstance(ctx).filterMultiple(false,
                                                aBranchlistKey.DEVICEID.getKey() + "=? AND " + aBranchlistKey.BRANCHID.getKey() + "=?",
                                                new String[] { strDeviceId, strBranchId }, "");
                                            if (bRsImId.size()>0) {
                                                aBranchlist bRsx = bRsImId.get(0);
                                                if (bRsx.getActive().trim().equals("true")) {
                                                    if (sp.getData(SharedKey.BRANCH_ID.getKey()).trim().equals("")) {
                                                        showBranchDialog();
                                                    } else {
                                                        if (sp.getData(SharedKey.BRANCH_ID.getKey()).trim().equals(String.valueOf(bRsx.getBranchid()))) {
                                                            sp.saveData(SharedKey.IMEI_ID.getKey(), bRsx.getDeviceid());
                                                            sp.saveData(SharedKey.BRANCH_ID.getKey(), String.valueOf(bRsx.getBranchid()));
                                                            sp.saveData(SharedKey.BRANCH_CODE.getKey(), bRsx.getBranchcode());
                                                            sp.saveData(SharedKey.BRANCH_DESCRIPTION.getKey(), bRsx.getDescription());

                                                            String brId = Helper.checkBranchProfile(ctx).get(0).getBranchid().toString();
                                                            String mBrId = SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH_ID.getKey());
                                                            if (brId.equals(mBrId)) {
                                                                if (Helper.getScrRatio(ctx) > 0.6) {
                                                                    sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
                                                                } else {
                                                                    sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
                                                                }
                                                            } else {
                                                                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
                                                            }

                                                            BounceView.addAnimTo( Helper.okDialog( ctx,
                                                            "INITIALIZATION SUCCESS","You can now LOGIN using YOUR REGISTERED ACCOUNT.",
                                                            "OK", new DialogInterface.OnClickListener() {
                                                            @Override public void onClick(DialogInterface dialog, int which) {
                                                            showActivity(LoginActivity.class); }}, false));
                                                        } else {
                                                            postBranchImei();
                                                        }
                                                    }
                                                } else {
                                                    alertDeviceNotAddedToServer();
                                                }
                                            } else {
                                                showBranchDialog();
                                            }
                                        } else {
                                            getDeviceProfile("getbranches");
                                        }
                                    }
                                } else {
                                    alertReferenceBranchesNotFound();
                                }
                            } catch (JSONException e) {
                                alertDataSyncError("Get Device Profile Fetch Error.");
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                }
            }
        }
    }

    private void volleyRequestFail(String type){
        String strMsg = "";
        if (type.equals("login")) {
            strMsg = "Login Request Error.";
        }
        if (type.equals("getpostbranchimei")) {
            strMsg = "Pos-Branch IMEI Request Error.";
        }
        if (type.equals("getdeviceprofile")) {
            strMsg = "Device Profile Request Error.";
        }
        alertDataSyncError(strMsg);
    }

    private AlertDialog actionDialog(final Context activity, String title, String message, String strLabel1,
        String strLabel2, String strLabel3, String okButtonCaption, View.OnClickListener onClickListener,
        String cancelButtonCaption, View.OnClickListener cancelClickListener, Integer flagType) {

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
            loader = Helper.showSpinnerDialog(ctx, "Requesting Device Profile", "Please wait..."); loader.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            params.put("deviceid", "n/a");
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&"; it.remove(); }
            VolleyInteractor vidp = new VolleyInteractor();
            vidp.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
                @Override
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(type); }
            });
            vidp.getDeviceProfile(getApplicationContext(), params, strParams.replaceAll(" ", "%20"), type);
        } else {
            alertRequireInternet();
        }
    }

    private void postBranchImei() {
        Helper.dismissSpinnerDialog(loader);
        if (Helper.isNetworkAvailable(ctx)) {
            loader = Helper.showSpinnerDialog(ctx, "ASSIGNING BRANCH", "Please wait..."); loader.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            String refBrid = refSelectedBranchId;
            sp.saveData(SharedKey.BRANCH_ID.getKey(), refBrid);
            params.put("branchid", refBrid);
            params.put("deviceid", Helper.getImei(ctx).trim());
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&"; it.remove();
            }
            VolleyInteractor viri = new VolleyInteractor();
            viri.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) { volleyRequestSuccess(response, type); }
                @Override
                public void onRequestFail(VolleyError response, String type) { volleyRequestFail(type); }
            });
            viri.postBranchImei(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
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

    private void alertDeviceNotAddedToServer() {
        Helper.dismissSpinnerDialog(loader);
        BounceView.addAnimTo( Helper.okDialog( ctx,
        "DEVICE REGISTRATION",
        "This device with ID# " + Helper.getImei(ctx) + " is NOT YET ACTIVATED. Please contact IT support",
        "OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            postBranchImei();
        }}, false));
    }

    private void alertDataSyncError(String strMsg) {
        Helper.dismissSpinnerDialog(loader);
        BounceView.addAnimTo( Helper.okDialog( ctx,
        "Data Sync Error", strMsg + " Please contact IT support.",
        "CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        }, false) );
    }

    private class getSettings extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String strClienntId = sp.getData(SharedKey.CLIENT_ID.getKey());
            VolleyInteractor vi = new VolleyInteractor();
            vi.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) {
                    if(alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                    if (loader != null) Helper.dismissSpinnerDialog(loader);

                    String res = response.replace("\r\n","");
                    try {
                        JSONObject sObj = new JSONObject(res);
                        if (sObj.getString("err").equals("false")) {
                            JSONObject rowObjConf = sObj.getJSONObject("res");
                            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), rowObjConf.getString("server_url"));
                            sp.saveData(SharedKey.LOCAL_SERVER_URL.getKey(), rowObjConf.getString("local_url"));
                            sp.saveData(SharedKey.DATABASE.getKey(), rowObjConf.getString("cn"));
                            sp.saveData(SharedKey.REF_MAIN_BRANCH_ID.getKey(), rowObjConf.getString("main_branch_id"));
                            sp.saveData(SharedKey.SUPPORT_USER.getKey(), rowObjConf.getString("support_user"));
                            sp.saveData(SharedKey.SUPPORT_PASSWORD.getKey(), rowObjConf.getString("support_password"));

                            String resconf = rowObjConf.getString("conf_po").replace("\r\n","");
                            JSONObject objConfPO = new JSONObject(resconf);
                            sp.saveData(SharedKey.SUPPORT_EMP_ID.getKey(), objConfPO.getString("support_employee_id"));
                            sp.saveData(SharedKey.SUPPORT_REF_EMP_ID.getKey(), objConfPO.getString("support_ref_employee_id"));
                            sp.saveInt(SharedKey.REF_EMP_VALIDATION.getKey(), objConfPO.getInt("reference_employee_validation"));
                            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), objConfPO.getInt("old_sku_validation"));
                            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), objConfPO.getInt("preload_items"));
                            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), objConfPO.getInt("saved_product_items"));
                            sp.saveInt(SharedKey.SHOW_PRICE_COL.getKey(), objConfPO.getInt("show_price_column_on_order"));
                            sp.saveInt(SharedKey.SHOW_TOTAL_COL.getKey(), objConfPO.getInt("show_total_column_on_order"));
                            sp.saveInt(SharedKey.SHOW_FREE_COL.getKey(), objConfPO.getInt("show_free_column_on_order"));
                            sp.saveInt(SharedKey.COMPUTE_QTY_ONLY.getKey(), objConfPO.getInt("compute_quantity_only"));
                            sp.saveInt(SharedKey.SHOW_SEARCH_PRICE.getKey(), objConfPO.getInt("show_price_on_search"));
                            sp.saveInt(SharedKey.PER_AGENT_SETUP.getKey(), objConfPO.getInt("per_agent_setup"));
                            sp.saveInt(SharedKey.ENABLE_REPORT_TYPE.getKey(), objConfPO.getInt("enable_report_type"));
                            sp.saveInt(SharedKey.SHOW_SUMMARY_ON_POST.getKey(), objConfPO.getInt("show_summary_on_post"));
                            sp.saveInt(SharedKey.REF_PO_NO.getKey(), objConfPO.getInt("reference_po_number"));

                            if (Helper.getScrRatio(ctx) < 0.6) { // modify price and total
                                sp.saveInt(SharedKey.SHOW_PRICE_COL.getKey(), 0);
                                sp.saveInt(SharedKey.SHOW_TOTAL_COL.getKey(), 0);
                                sp.saveInt(SharedKey.COMPUTE_QTY_ONLY.getKey(), 1);
                            }

//                            sp.removeSinglePref(SharedKey.IMEI_ID.getKey());
                            String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
                            if (strImeiId.equals("")) {
                                getDeviceProfile("getbranches");
                            } else {
                                proceedNormal();
                            }
                        } else {
                            alertDataSyncError(sObj.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        alertDataSyncError("App Config Error L1.");
                    }
                }
                @Override
                public void onRequestFail(VolleyError response, String type) {
                    if(alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                }
            });
            HashMap<String, String> vparams = new HashMap<>();
            vparams.put("id", strClienntId);
            String strParams = ""; Iterator it = vparams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }
            vi.getSettings(ctx, vparams, strParams, "");

            return "";
        }
        @Override
        protected void onPostExecute(String result) { }
        @Override
        protected void onPreExecute() { }
        @Override
        protected void onProgressUpdate(Integer... values) { }
    }
}