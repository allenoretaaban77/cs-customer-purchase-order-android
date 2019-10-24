package com.fnc.order.android.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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

        sp = SharedData.getInstance(ctx);
        if(sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), ServerConstants.SERVER_URL);
        }
        if(sp.getData(SharedKey.REF_DATABASE.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.REF_DATABASE.getKey(), ServerConstants.CN);
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

        TedPermission.with(ctx).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    String strRefDeviceImei = Helper.getImei(ctx);
                    startProcedure();
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
                            clientSignin();
                        }
                    }, "", null, 0
                );
                EditText etPassword = (EditText) alertDialog.findViewById(R.id.et_edittext2);
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext1))).setText("admin@backoffice.com");
                ((EditText) ((EditText) alertDialog.findViewById(R.id.et_edittext2))).setText("admin123");

                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialog);
            } else {
                alertRequireInternet();

            }
        } else {
            String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
            if (strImeiId.equals("")) {
                getDeviceProfile("init");
            } else {
                proceedNormal();
            }
        }
    }

    private AlertDialog alertdialogBL;
    private void showBranchDialog(final String type) {
        if (type.equals("init") || type.equals("reinit")) {
            alertdialogBL = actionDialog( ctx, "ASSIGN BRANCH",
                getString(R.string.branch_dialog_message),
                "Username", "Password", "Branch",
                "SUBMIT", new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!refSelectedBranchId.equals("")) {
                            LinkedList<aBranchlist> abl =
                                DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                                    aBranchlistKey.BRANCHID.getKey() + " = ? AND " + aBranchlistKey.DEVICEID.getKey() + " = ? ",
                                    new String[] { refSelectedBranchId, Helper.getImei(ctx) }
                                );
                            if (abl.size() > 0) {
                                int flgx = 0;
                                aBranchlist ab = null;
                                for (int i = 0; i < abl.size(); i++) {
                                    ab = abl.get(i);
                                    if(ab.getActive().equals("true")) {
                                        flgx = 1;
                                    }
                                }
                                if (flgx == 1) {
                                    loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                                    sp.saveData(SharedKey.IMEI_ID.getKey(), ab.getDeviceid());
                                    sp.saveData(SharedKey.BRANCH_ID.getKey(), String.valueOf(ab.getBranchid()));
                                    sp.saveData(SharedKey.BRANCH_CODE.getKey(), String.valueOf(ab.getBranchid()));
                                    getPreRequisite();
                                } else {
                                    postBranchImei();
                                }
                            } else {
                                postBranchImei();
                            }
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
    private String refSelectedBranchId;
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
        vipr.registerCallback(this);
        vipr.getPreRequisite(ctx, params,
                strParams.replaceAll(" ", "%20"));
    }

    private void proceedNormal() {
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

    private void clientSignin() {
        String strUsername = ((EditText) alertDialog.findViewById(R.id.et_edittext1)).getText().toString();
        String strPassword = ((EditText) alertDialog.findViewById(R.id.et_edittext2)).getText().toString();
//        strUsername = "admin@backoffice.com"; strPassword = "admin123";
//        String strBranch = ((EditText) alertDialog.findViewById(R.id.et_edittext3)).getText().toString();

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

        loader = Helper.showSpinnerDialog(ctx, "", "Posting... Please wait..."); loader.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("logdb", ServerConstants.LOGDB);
        params.put("userid", strUsername);
        params.put("pass", strPassword);
        VolleyInteractor vil = new VolleyInteractor();
        vil.registerCallback(this);
        vil.login(ctx, params, "");
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

    @Override
    protected void onStop(){
        super.onStop();
    }

    public void onRequestSuccess(final String response, final String type) {
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
        if (type.equals("login")) {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("dtcompany").equals("[]")) {
                    Helper.dismissSpinnerDialog(loader);
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Initialization Error","Invalid credentials",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false) );
                } else {
                    JSONArray objArr = new JSONArray(obj.getString("dtcompany"));
                    JSONObject objx = new JSONObject(objArr.get(0).toString());
                    if (objx.getString("DatabaseID").equals("")) {
                        Helper.dismissSpinnerDialog(loader);
                        BounceView.addAnimTo( Helper.okDialog( ctx,
                            "Initialization Error","Invalid credentials",
                            "CLOSE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, false) );
                    } else {
                        alertDialog.dismiss();
                        String strCN = "";
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
                        loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
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
        if (type.contains("getdeviceprofile|")) {
            Helper.dismissSpinnerDialog(loader);
            if (response.equals("[]")) {
                alertReferenceBranchesNotFound();
            } else {
                final String[] strRef = type.split("\\|");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray objArr = new JSONArray(response);
                            if (objArr.length() > 0) {
                                DcBranchlist.getInstance(ctx).emptyBranchlist();
                                for (int i = 0; i < objArr.length(); i++) {
                                    JSONObject rowObj = objArr.getJSONObject(i);
                                    aBranchlist br = new aBranchlist(
                                        rowObj.getInt("branchid"),
                                        rowObj.getString("branchcode").trim(),
                                        rowObj.getString("deviceid"),
                                        rowObj.getString("description").trim(),
                                        rowObj.getString("deviceID1").trim(),
                                        rowObj.getString("active").trim(),
                                        rowObj.getString("customerID").trim(),
                                        rowObj.getString("old_branchid").trim(),
                                        rowObj.getString("old_customerid").trim()
                                    );
                                    DcBranchlist.getInstance(ctx).insertBranches(br);
                                }

                                if (strRef[1].equals("reinit")) {
                                    LinkedList<aBranchlist> abl =
//                                        DcBranchlist.getInstance(ctx).searchBranch(refSelectedBranchId, Helper.getImei(ctx));
                                        DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                                            aBranchlistKey.BRANCHID.getKey() + " = ? AND " + aBranchlistKey.DEVICEID.getKey() + " = ? ",
                                            new String[] { refSelectedBranchId, Helper.getImei(ctx) }
                                        );
                                    if (abl.size() > 0) {
                                        int flgx = 0;
                                        aBranchlist ab = null;
                                        for (int i = 0; i < abl.size(); i++) {
                                            ab = abl.get(i);
                                            if(ab.getActive().equals("true")) {
                                                flgx = 1;
                                            }
                                        }
                                        if (flgx == 1) {
                                            loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                                            sp.saveData(SharedKey.IMEI_ID.getKey(), ab.getDeviceid());
                                            sp.saveData(SharedKey.BRANCH_ID.getKey(), String.valueOf(ab.getBranchid()));
                                            sp.saveData(SharedKey.BRANCH_CODE.getKey(), String.valueOf(ab.getBranchid()));
                                            getPreRequisite();
                                        } else {
                                            BounceView.addAnimTo( Helper.okDialog( ctx,
                                                "Device Registration",
                                                "This device with ID# " + Helper.getImei(ctx) + " is NOT YET ACTIVATED. Please contact IT support",
                                                "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        showBranchDialog(strRef[1]);
                                                    }
                                                }, false) );
                                        }
                                    } else {
                                        BounceView.addAnimTo( Helper.okDialog( ctx,
                                            "Device Registration",
                                            "This device with ID# " + Helper.getImei(ctx) + " is NOT YET REGISTERED. Please contact IT support",
                                            "OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    showBranchDialog(strRef[1]);
                                                }
                                            }, false) );
                                    }
                                } else {
                                    showBranchDialog(strRef[1]);
                                }
                            } else {
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

    public void onRequestFail(VolleyError response, String type){
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
            params.put("deviceid", "n/a");
//            params.put("deviceid", Helper.getImei(ctx));
//            params.put("deviceid", "353800100112222"); // timog
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                it.remove();
            }
            VolleyInteractor vidp = new VolleyInteractor();
            vidp.registerCallback(this);
            vidp.getDeviceProfile(ctx, params, strParams
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
            params.put("branchid", refSelectedBranchId);
            params.put("deviceid", Helper.getImei(ctx));
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }
            VolleyInteractor viri = new VolleyInteractor();
            viri.registerCallback(this);
            viri.postBranchImei(ctx, params,
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
}
