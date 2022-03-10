package com.fnc.order.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.aAdminGroupings;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PasswordVisibility;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.R;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import hari.bounceview.BounceView;

public class LoginActivity extends BaseActivity {

    PasswordVisibility passwordVisibility;
    private EditText usernameText, passwordEText;
    private TextView tvVersion;
    private Button loginButton;
    private Button hidePassword, showPassword;
    private ProgressDialog loader, getloader, updateloader;
    private String blockCharacterSet = "~#^|$%&*!()+-$";
    private RelativeLayout relPassword;
    private Context ctx;
    private Boolean isSubmit = false;
    private AlertDialog alertDialog, alertDialogSettingsAuth, alertDialogSettings, alertDialogUser;
    private RequestQueue requestQueue;
    private SharedData sp;
    private VolleyCallback callback;
    private VolleyInteractor vi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        loader = Helper.buildSpinnerDialog(ctx);
        sp = SharedData.getInstance(this);

        initViews();
        initListeners();

//        addUser();

        // philbest
//        usernameText.setText("1");
//        passwordEText.setText("clovis");
        // massive
//        usernameText.setText("gabby");
//        passwordEText.setText("gBuensuceso");
        // commi
//        usernameText.setText("12105");
//        passwordEText.setText("7777");

//        loginButton.callOnClick();
    }

    private void initViews() {
        relPassword = (RelativeLayout) findViewById(R.id.relPassword);
        usernameText = (EditText) findViewById(R.id.username);
        usernameText.setFilters(new InputFilter[] { filter });
        passwordEText = (EditText) findViewById(R.id.password);
        passwordEText.setText("");
        hidePassword = (Button) findViewById(R.id.hide_password);
        showPassword = (Button) findViewById(R.id.show_password);
        loginButton = (Button) findViewById(R.id.login_button);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initListeners(){
        passwordEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(Helper.isTextFieldBlank(passwordEText)){
                    relPassword.setVisibility(View.GONE);
                }else{
                    relPassword.setVisibility(View.VISIBLE);
                    passwordVisibility.visibility(hidePassword, showPassword, passwordEText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        loginButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSubmit == true) { return; }

                String usernameStr = usernameText.getText().toString().trim();
                String passwordString = passwordEText.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordEText.getWindowToken(), 0);

                if(usernameStr.matches("")){
                    alertDialog = Helper.okDialog(ctx,
                        "Error","Please enter username.", "CLOSE",
                        null, false);
                    BounceView.addAnimTo(alertDialog);
                    return;
                }
                if(passwordString.matches("")){
                    alertDialog = Helper.okDialog(ctx,
                        "Error","Please enter password.", "CLOSE",
                        null, false);
                    BounceView.addAnimTo(alertDialog);
                    return;
                }

                LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
                if (sl.size() > 0) {
                    LinkedList<aStaffs> slUP = DcStaffs.getInstance(ctx).checkStaff(usernameStr);
                    if (slUP.size() > 0 ) {
                        aStaffs slx = slUP.get(0);

                        if (Helper.decryptMsg(slx.getPass(), Helper.generateKey()).trim().equals(passwordString)) {
                            if (slx.getRefempno().equals("-1")) {
                                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                                    if(sp.getData(SharedKey.DATABASE.getKey()).equals(ServerConstants.DEFAULT_CN)) {
                                        BounceView.addAnimTo( Helper.okDialog( ctx,
                                        "Account Error","Reference employee number not recognized. Please contact IT support to update your account.", "CLOSE",
                                        null, false) );
                                        return;
                                    }
                                }
                            }

                            String refEmpIDOld = SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID.getKey());
                            SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_ID_OLD.getKey(), refEmpIDOld);
                            SharedData.getInstance(ctx).saveData(SharedKey.IDENTITY_ID.getKey(), String.valueOf(slx.getRefempno()));
                            SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_NO.getKey(), String.valueOf(slx.getRefempno()));
                            SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_ID.getKey(), String.valueOf(slx.getEmpId()));
                            SharedData.getInstance(ctx).saveData(SharedKey.EMP_NO.getKey(), String.valueOf(slx.getEmpNo()));
                            SharedData.getInstance(ctx).saveData(SharedKey.EMP_NAME.getKey(), String.valueOf(slx.getName()));
                            SharedData.getInstance(ctx).saveData(SharedKey.EMP_POSITION.getKey(), String.valueOf(slx.getJobtitle()));
                            SharedData.getInstance(ctx).saveData(SharedKey.EMP_ISMOBILEADMIN.getKey(), String.valueOf(slx.getIsmobileadmin()));

                            if (Helper.checkBranchProfile(ctx).size() > 0) {
                                if (Helper.checkBranchProfile(ctx).get(0).getBranchid().toString()
                                .equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH_ID.getKey()))) {
                                    showActivity(MainActivity.class);
                                } else {
                                    LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "%");
                                    if (llr.size() > 0) {
                                        showActivity(MainActivity.class);
                                        Toast.makeText(ctx, "Welcome " + slx.getName() + "!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        requestCustomers("");
                                    }
                                }
                            } else {
                                isSubmit = false;
                                Toast.makeText(ctx, "Login Error, please contact IT support.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            isSubmit = false;
                            alertDialog = Helper.okDialog(ctx,
                            "Error","Invalid username or password", "CLOSE", null, false);
                            BounceView.addAnimTo(alertDialog);
                        }
                    } else {
                        isSubmit = false;
                        BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Error","Invalid username or password", "CLOSE",
                        null, false) );
                    }
                } else {
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Data Sync Required","Users data empty. This app needs to be restarted.",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                        }
                    }, false) );
                }
            }
        });

        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BounceView.addAnimTo(Helper.okDialog(ctx, "Update App Config",
                    "Device ID: " + sp.getData(SharedKey.IMEI_ID.getKey()) +
                    "\r\nClient Name: " + sp.getData(SharedKey.DATABASEID.getKey()) +
                    "\r\nClient Server: " + sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()) +
                    "\r\nBranch Name: " + sp.getData(SharedKey.BRANCH_DESCRIPTION.getKey()),
//                    "\r\n\r\nThis will update system settings.  Are you sure you want to continue?",
                    /*"Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateloader = Helper.showSpinnerDialog(ctx, "", "Getting possible update... Please wait..."); updateloader.show();
                            clientSignin(String.valueOf(sp.getData(SharedKey.REF_ADMIN_USER.getKey())), String.valueOf(sp.getData(SharedKey.REF_ADMIN_PASSWORD.getKey())));
                        }
                    }, */
                    "Close", null, false)
                );

                /*alertDialogSettingsAuth = actionDialog(ctx, "SETTINGS",
                    "Validate settings security account",
                    "Username", "Password",
                    "SUBMIT", new View.OnClickListener() {
                        public void onClick(View v) {
                            LinearLayout layout = (LinearLayout) ((ViewGroup) v.getParent()).getParent().getParent();
                            EditText etUsername = (EditText) layout.findViewById(R.id.et_edittext1);
                            EditText etPassword = (EditText) layout.findViewById(R.id.et_edittext2);
                            SharedData spx = SharedData.getInstance(ctx);
                            alertDialogSettingsAuth.dismiss();

                            LinkedList<aStaffs> slUP = DcStaffs.getInstance(ctx).checkStaff(
                                etUsername.getText().toString().trim(),
                                etPassword.getText().toString().trim() );
                            if (slUP.size() > 0 ) {
                                aStaffs slx = slUP.get(0);
                                if (slx.getIsmobileadmin().equals("true") && slx.getName().equals("IT Support")) {
//                                  if (spx.getData(SharedKey.DEV_USERNAME.getKey()).equals(etUsername.getText().toString()) &&
//                                  spx.getData(SharedKey.DEV_PASSWORD.getKey()).equals(etPassword.getText().toString())) {
                                    alertDialogSettings = actionDialog(ctx, "SETTINGS",
                                        "Customize settings per client as required",
                                        "Server", "Database",
                                        "UPDATE", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                LinearLayout layout = (LinearLayout) ((ViewGroup) v.getParent()).getParent().getParent();
                                                EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_edittext1);
                                                EditText etDatabase = (EditText) layout.findViewById(R.id.et_edittext2);
                                                Switch sw_skuvalid = (Switch) layout.findViewById(R.id.sw_skuvalid);
                                                Switch sw_preloaditems = (Switch) layout.findViewById(R.id.sw_preloaditems);
                                                Switch sw_saveitems = (Switch) layout.findViewById(R.id.sw_saveitems);
                                                SharedData spx = SharedData.getInstance(ctx);
                                                spx.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "http://" + etDomainServerName.getText().toString().trim() + "/");
                                                spx.saveData(SharedKey.DATABASE.getKey(), etDatabase.getText().toString().trim());
                                                spx.saveInt(SharedKey.SKU_VALIDATION.getKey(), sw_skuvalid.isChecked() ? 1 : 0);
                                                spx.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), sw_preloaditems.isChecked() ? 1 : 0);
                                                spx.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), sw_saveitems.isChecked() ? 1 : 0);

                                                DcAitemlist.getInstance(ctx).emptyaItemlist();
                                                DcOrdered.getInstance(ctx).emptyOrderedlist();
                                                DcMenulist.getInstance(ctx).emptyMenulist();
                                                DcStaffs.getInstance(ctx).emptyStaffslist();

                                                postBranchImei(); // check branch before success
                                            }
                                        },
                                        "CHANGE BRANCH", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                addUser();
                                            }
                                        },1);

                                    LinearLayout ll_skuvalid = (LinearLayout) alertDialogSettings.findViewById(R.id.ll_validations);
                                    ll_skuvalid.setVisibility(View.VISIBLE);

                                    EditText etDomainServerName = (EditText) alertDialogSettings.findViewById(R.id.et_edittext1);
                                    String strSN = spx.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).replace("http://","").replace("/","");
                                    etDomainServerName.setText(strSN);
                                    EditText etDatabase = (EditText) alertDialogSettings.findViewById(R.id.et_edittext2);
                                    etDatabase.setText(spx.getData(SharedKey.DATABASE.getKey()).toLowerCase());

                                    Switch sw_skuvalid = (Switch) alertDialogSettings.findViewById(R.id.sw_skuvalid);
                                    sw_skuvalid.setChecked(spx.getInt(SharedKey.SKU_VALIDATION.getKey()) == 1 ? true : false);

                                    Switch sw_preloaditems = (Switch) alertDialogSettings.findViewById(R.id.sw_preloaditems);
                                    sw_preloaditems.setChecked(spx.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == 1 ? true : false);

                                    Switch sw_saveitems = (Switch) alertDialogSettings.findViewById(R.id.sw_saveitems);
                                    sw_saveitems.setChecked(spx.getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1 ? true : false);

                                    LinearLayout ll_add_user = (LinearLayout) alertDialogSettings.findViewById(R.id.ll_add_user);
//                                ll_add_user.setVisibility(View.VISIBLE);

                                    LinearLayout ll_branch_box = (LinearLayout) alertDialogSettings.findViewById(R.id.ll_branch_box);
//                                ll_branch_box.setVisibility(View.VISIBLE);

                                    tvBranchdescription = (TextView) alertDialogSettings.findViewById(R.id.tv_branchdescription);
                                    tvBranchdescription.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            msBranches.performClick();
                                        }
                                    });
                                    msBranches = (Spinner) alertDialogSettings.findViewById(R.id.ms_branches);
                                    loadSpinnerBranches();

                                    MaterialRippleLayout mlrReload = (MaterialRippleLayout) alertDialogSettings.findViewById(R.id.mrl_reload);
                                    mlrReload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            EditText etDomainServerName = (EditText) alertDialogSettings.findViewById(R.id.et_edittext1);
                                            EditText etDatabase = (EditText) alertDialogSettings.findViewById(R.id.et_edittext2);
                                            Switch sw_skuvalid = (Switch) alertDialogSettings.findViewById(R.id.sw_skuvalid);
                                            Switch sw_preloaditems = (Switch) alertDialogSettings.findViewById(R.id.sw_preloaditems);
                                            Switch sw_saveitems = (Switch) alertDialogSettings.findViewById(R.id.sw_saveitems);
                                            SharedData spx = SharedData.getInstance(ctx);
                                            spx.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "http://" + etDomainServerName.getText().toString().trim() + "/");
                                            spx.saveData(SharedKey.DATABASE.getKey(), etDatabase.getText().toString().trim());
                                            spx.saveInt(SharedKey.SKU_VALIDATION.getKey(), sw_skuvalid.isChecked() ? 1 : 0);
                                            spx.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), sw_preloaditems.isChecked() ? 1 : 0);
                                            spx.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), sw_saveitems.isChecked() ? 1 : 0);
                                            loader = Helper.showSpinnerDialog(ctx, "Updating", "Please wait...."); loader.show();
                                            getDeviceProfile("reload");
                                        }
                                    });

                                    alertDialogSettings.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    alertDialogSettings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    BounceView.addAnimTo(alertDialogSettings);
                                } else {
                                    Toast.makeText(ctx, "Access denied. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ctx, "Access denied. Invalid credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    "Cancel", new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialogSettingsAuth.dismiss();
                        }
                    },
                    2);

                EditText etPassword = (EditText) alertDialogSettingsAuth.findViewById(R.id.et_edittext2);
//                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

//                EditText etUsername = (EditText) alertDialogSettingsAuth.findViewById(R.id.et_edittext1);
//                etUsername.setText("administrator");
//                etPassword.setText("P@ssw0rd" + Helper.getReqDate(0, ""));

                LinearLayout ll_add_user = (LinearLayout) alertDialogSettingsAuth.findViewById(R.id.ll_add_user);
                ll_add_user.setVisibility(View.GONE);
                LinearLayout ll_branch_box = (LinearLayout) alertDialogSettingsAuth.findViewById(R.id.ll_branch_box);
                ll_branch_box.setVisibility(View.GONE);

                alertDialogSettingsAuth.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialogSettingsAuth.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialogSettingsAuth); */
            }
        });
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }else{

            }
            return null;
        }
    };

    private void showActivity(final Class<?> cls) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                usernameText.setText(""); passwordEText.setText("");
                dismissSpinnerDialog();
                startActivity(new Intent(ctx, cls));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);
            }
        }, 0);
    }

    private void dismissSpinnerDialog() {
        isSubmit = false;
        if(loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        alertDialog =  Helper.okCancelDialog(ctx, "Closing Application", "Are you sure you want to close this app?",
        "Yes", closeApp,
        "No", null, false);
        BounceView.addAnimTo(alertDialog);
    }

    DialogInterface.OnClickListener closeApp = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finishAndRemoveTask();
        }
    };

    @Override
    public void onResume(){
        super.onResume();

        if (Helper.isNetworkAvailable(ctx)) {
            loader = Helper.showSpinnerDialog(ctx, "", "Getting possible update... Please wait..."); loader.show();
//            new getSettings().execute("");
            new getUsersAsync().execute("");
            new android.os.Handler().postDelayed(
                new Runnable() { public void run() { Helper.dismissSpinnerDialog(loader); } },
                1000
            );
        }
    }

    private class getUsersAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            VolleyInteractor viu = new VolleyInteractor();
            viu.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(String response, String type) {
                    Helper.dismissSpinnerDialog(loader);
                    Log.d("dsxs getuser", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.length() > 0) {
                            DcStaffs.getInstance(ctx).emptyStaffslist();
                            JSONArray sArr = obj.getJSONArray("staff");
                            if (sArr.length() > 0) {
                                for (int i = 0; i < sArr.length(); i++) {
                                    JSONObject rowObj = sArr.getJSONObject(i);
                                    aStaffs sl = new aStaffs(
                                        rowObj.getLong("empId"),
                                        rowObj.getString("refempno").equals("null") ? "-1" : rowObj.getString("refempno"),
                                        rowObj.getString("empNo"),
                                        rowObj.getString("Email"),
                                        rowObj.getString("name"),
                                        rowObj.getLong("Branch"),
                                        rowObj.getLong("Jobtitle"),
                                        Helper.encryptMsg(rowObj.getString("pass"), Helper.generateKey()),
                                        rowObj.getString("active"),
                                        rowObj.getString("ismobileadmin")
                                    );
                                    DcStaffs.getInstance(ctx).insertStaffs(sl);
                                }
                            }
                        } else {
                            Log.d("dsxe getuser", response);
                        }
                    } catch (JSONException e) {
                        Log.d("dsxe getuser", response);
                    }
                }
                @Override
                public void onRequestFail(VolleyError response, String type) {
                    Log.d("dsxe getuser", String.valueOf(response));
                }
            });
            HashMap<String, String> viparams = new HashMap<>();
            viparams.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            viparams.put("branchid", sp.getData(SharedKey.BRANCH_ID.getKey()));
            Iterator it = viparams.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }
            viu.getPreRequisite(getApplicationContext(), viparams, strParams.replaceAll(" ", "%20"));

            return "Task Done";
        }
        @Override
        protected void onPostExecute(String result) {
        }
        @Override
        protected void onPreExecute() { }
        @Override
        protected void onProgressUpdate(Integer... values) { }
    }

    private void requestCustomers(String stringSearch) {
        if (Helper.isNetworkAvailable(ctx)) {
            loader = Helper.showSpinnerDialog(ctx, "Updating", "Please wait...."); loader.show();
            VolleyInteractor vic = new VolleyInteractor();
            vic.registerCallback(new VolleyCallback() {
                @Override
                public void onRequestSuccess(final String response, String type) {
                    try {
                        JSONArray objArr = new JSONArray(response);
                        if(objArr.length() > 0) {
                            new customerAT().execute(response);
                        } else {
                            Toast.makeText(ctx, "Customer record empty, pleas contact developer", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        isSubmit = false;
                        Helper.dismissSpinnerDialog(loader);
                        Toast.makeText(ctx, "Customer record fetch error, pleas contact developer", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onRequestFail(VolleyError response, String type) {
                    isSubmit = false;
                    Helper.dismissSpinnerDialog(loader);
                    Toast.makeText(ctx, "Customer record fetch error, pleas contact developer", Toast.LENGTH_SHORT).show();
                }
            });
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            params.put("customer", stringSearch);
            if(sp.getInt(SharedKey.PER_AGENT_SETUP.getKey()) == 0) {
                params.put("agentid", "");
            } else {
                params.put("agentid", SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID.getKey()));
            }
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                it.remove();
            }
            strParams = strParams.replaceAll(" ", "%20");
            vic.getCustomers(ctx, params, strParams);
        } else {
            isSubmit = false;
            Toast.makeText(ctx, "Fetch customer failed. Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private class customerAT extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String response = params[0].replace("\r\n", "");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    DcMenulist.getInstance(ctx).emptyMenulist();
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject obj = objArr.getJSONObject(i);

                        String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                        String strInvoice = ""; try { strInvoice = obj.getString(MenulistKey.INVOICE.getKey());
                        } catch (Exception e) { strInvoice = "false"; }
                        MenuList mlList = new MenuList(
                            obj.getString(MenulistKey.CUSTOMER_ID.getKey()),
                            obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()),
                            strCustomerName,
                            "", 0, "",
                            strInvoice
                        );
                        if (!strCustomerName.equals("")) {
                            if (String.valueOf(strCustomerName.charAt(0)).equals("0")) {
                                mlList.setAlphachar(String.valueOf(strCustomerName.charAt(5)).toUpperCase());
                            } else {
                                mlList.setAlphachar(String.valueOf(strCustomerName.charAt(0)).toUpperCase());
                            }
                            DcMenulist.getInstance(ctx).insertMenulist(mlList);
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "Task Error";
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            Helper.dismissSpinnerDialog(loader);
            showActivity(MainActivity.class);
        }
        @Override
        protected void onPreExecute() {
            Log.d("asynctask", "Task Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("asynctask", "Running " + + values[0]);
        }
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

                            if (Integer.parseInt(rowObjConf.getString("ver_po")) > Helper.getVersionCode(ctx)) {
                                BounceView.addAnimTo( Helper.okDialog( ctx,
                                "App Update",
                                "App Update\n\nA new version of this app is now available.\n\n*** It is REQUIRED TO UPDATE your app before you start any transactions.",
                                "PROCEED UPDATE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ctx.getPackageName())));
                                    }
                                }, false) );
                            }

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

                            tvVersion.setText(Helper.getVersion(ctx, LoginActivity.this) + " | " +
                                SharedData.getInstance(ctx).getData(SharedKey.BRANCH_DESCRIPTION.getKey())
                            );

                            if (Helper.checkBranchProfile(ctx).get(0).getBranchid().toString().equals(SharedData.getInstance(ctx)
                                .getData(SharedKey.REF_MAIN_BRANCH_ID.getKey()))) {
                                if (Helper.getScrRatio(ctx) > 0.6) {
                                    sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.LOCAL_SERVER_URL.getKey()));
                                } else {
                                    sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
                                }
                            } else {
                                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
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
}

