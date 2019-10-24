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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcOrdered;
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
import com.google.api.core.NanoClock;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import hari.bounceview.BounceView;

public class LoginActivity extends BaseActivity {

    PasswordVisibility passwordVisibility;
    private FormEditText usernameText;
    private FormEditText passwordEText;
    private TextView tvVersion;
    private Button loginButton;
    private Button hidePassword, showPassword;
    private ProgressDialog loader;
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
    }

    private void initViews() {
        relPassword = (RelativeLayout) findViewById(R.id.relPassword);
        usernameText = (FormEditText) findViewById(R.id.username);
        usernameText.setFilters(new InputFilter[] { filter });
        passwordEText = (FormEditText) findViewById(R.id.password);
        passwordEText.setText("");
        hidePassword = (Button) findViewById(R.id.hide_password);
        showPassword = (Button) findViewById(R.id.show_password);
        loginButton = (Button) findViewById(R.id.login_button);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        usernameText.setText("18015");
        passwordEText.setText("1629");
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
                    LinkedList<aStaffs> slUP = DcStaffs.getInstance(ctx).checkStaff(usernameStr, passwordString);
                    if (slUP.size() > 0 ) {
                        aStaffs slx = slUP.get(0);
                        if (slx.getRefempno().equals("-1")) {
                            if(sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                                isSubmit = false;
                                BounceView.addAnimTo( Helper.okDialog( ctx,
                                    "Account Error","Reference employee number not recognized. Please contact IT support to update your account.", "CLOSE",
                                    null, false) );
                                return;
                            }
                        }
                        SharedData.getInstance(ctx).saveData(SharedKey.IDENTITY_ID.getKey(), String.valueOf(slx.getRefempno()));
                        SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_NO.getKey(), String.valueOf(slx.getRefempno()));
                        SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_ID.getKey(), String.valueOf(slx.getEmpId()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_NO.getKey(), String.valueOf(slx.getEmpNo()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_NAME.getKey(), String.valueOf(slx.getName()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_POSITION.getKey(), String.valueOf(slx.getJobtitle()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_ISMOBILEADMIN.getKey(), String.valueOf(slx.getIsmobileadmin()));
                        isSubmit = true;

                        if (Helper.checkBranchProfile(ctx).size() > 0) {
                            if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary")) {
//                                SharedData.getInstance(ctx).saveData(SharedKey.DATABASE.getKey());
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
                        BounceView.addAnimTo( Helper.okDialog( ctx,
                            "Error","Invalid username or password", "CLOSE",
                            null, false) );
                    }
                } else {
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Data Sync Required","This app needs to be restarted.",
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
                alertDialogSettingsAuth = actionDialog(ctx, "SETTINGS",
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
                BounceView.addAnimTo(alertDialogSettingsAuth);
            }
        });
    }

    /*private void updateEmployeeId(String old_employee_id) {
        showSpinnerDialog();
        VolleyInteractor viv = new VolleyInteractor();
        viv.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("logdb", ServerConstants.LOGDB);
        params.put("identityid", sp.getData(API.IDENTITY_ID.getApi()));
        params.put("old_employeeid", old_employee_id);

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        viv.updateEmployeeId(ctx, params, strParams);
    }*/

    /* private void requestUsers() {
        Log.d("dsx", "request users");
        VolleyInteractor vic = new VolleyInteractor();
        vic.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", ServerConstants.LOGDB);
        params.put("vcomp", "");
        params.put("name", "");

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        vi.getUsers(ctx, params, strParams);
    } */

    /* public void onRequestSuccess(String response, String type) {
        final VolleyCallback refThis = this;
        try {
            if (type.equals("searchcustomer")) {
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    DcMenulist.getInstance(ctx).emptyMenulist();
                    for (int i = 0; i < objArr.length(); i++) {
                        try {
                            JSONObject obj = objArr.getJSONObject(i);
                            MenuList mlList = new MenuList();
                            mlList.setCustomerID(obj.getString(MenulistKey.CUSTOMER_ID.getKey()));
                            mlList.setCustomerIntegrationId(obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()));
                            String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                            mlList.setCustomerName(strCustomerName);
                            mlList.setRecordCount(0);
                            mlList.setRemarks("");
                            if (!strCustomerName.equals("")) {
                                if (String.valueOf(strCustomerName.charAt(0)).equals("0")) {
                                    mlList.setAlphachar(String.valueOf(strCustomerName.charAt(5)));
                                } else {
                                    mlList.setAlphachar(String.valueOf(strCustomerName.charAt(0)));
                                }
                                DcMenulist.getInstance(ctx).insertMenulist(mlList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                showSpinnerDialog();
                                requestUsers();
                                showActivity(MainActivity.class);
                                Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        300
                    );
                }
            } else if (type.equals("searchitem")) {

            } else if (type.equals("updateemployeeid")) {
                if(!response.trim().equals("") && !response.trim().equals("null")) {
                    sp = SharedData.getInstance(ctx);
                    sp.saveData(API.EMPLOYEE_ID.getApi(), response.trim());
//                    LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "");
//                    if(llr.size() > 0) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    showSpinnerDialog();
                                    requestUsers();
                                    showActivity(MainActivity.class);
                                    Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                                }
                            },
                            300
                    );
//                    } else {
//                        requestCustomers("");
//                    }
                }
            } else if (type.equals("validate")) {
                if (response.trim().equals("True")) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    showSpinnerDialog();
                                    requestUsers();
                                    showActivity(MainActivity.class);
                                    Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                                }
                            },
                            300
                    );
                } else {
                    dismissSpinnerDialog();
                    alertDialog = okCancelInputDialogBuilder(ctx,
                            "Please update your Employee ID to continue using this application.",
                            "Update", null,
                            "Cancel", new View.OnClickListener() {
                                public void onClick(View v) {
                                    Helper.hideSoftKeyboard(LoginActivity.this);
                                    alertDialog.dismiss();
                                }
                            });
                    BounceView.addAnimTo(alertDialog);
                }
            } else if (type.equals("getusers")) {
                Log.d("dsx", "save users");
                dismissSpinnerDialog();
                response = response.replace("\r\n ", "");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    for (int i = 0; i < objArr.length(); i++) {
                        try {
                            JSONObject obj = objArr.getJSONObject(i);
                            Userslist ul = new Userslist();
                            ul.setIdentityId(obj.getString(UserslistKey.IDENTITYID.getKey()));
                            ul.setEmail(obj.getString(UserslistKey.EMAIL.getKey()));
                            ul.setPassword(obj.getString(UserslistKey.PASSWORD.getKey()));
                            ul.setFirstName(obj.getString(UserslistKey.FIRSTNAME.getKey()));
                            ul.setMiddleName(obj.getString(UserslistKey.MIDDLENAME.getKey()));
                            ul.setLastName(obj.getString(UserslistKey.LASTNAME.getKey()));
                            ul.setDateOfBirth(obj.getString(UserslistKey.DATEOFBIRTH.getKey()));
                            int isv =  obj.getBoolean(UserslistKey.VERIFIED.getKey()) ? 1 : 0 ;
                            ul.setVerified(isv);
                            ul.setCompany_UniqId(obj.getString(UserslistKey.COMPANY_UNIQIE.getKey()));
                            ul.setReference_employee_no(obj.getString(UserslistKey.REF_EMPLOYEE_NO.getKey()));
                            ul.setTempo_id(obj.getString(UserslistKey.TEMPO_ID.getKey()));
                            DcUserslist.getInstance(ctx).inserUserslist(ul);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                JSONObject obj = new JSONObject(response);
                sp = SharedData.getInstance(ctx);
                sp.saveData(API.DATA_BUSINESS.getApi(), obj.getString(API.DATA_BUSINESS.getApi()).toString());
                sp.saveData(API.DATA_COMPANY.getApi(), obj.getString(API.DATA_COMPANY.getApi()).toString());
                sp.saveData(API.DATA_USER.getApi(), obj.getString(API.DATA_USER.getApi()).toString());
                JSONArray datauserArray = obj.getJSONArray(API.DATA_USER.getApi());
                if (datauserArray.toString().equals("[]")) {
                    dismissSpinnerDialog();
                    Toast.makeText(ctx, "Login invalid.", Toast.LENGTH_SHORT).show();
                } else {
                    if(datauserArray.length() > 0) {
                        JSONObject row = datauserArray.getJSONObject(0);
                        sp.saveData(API.IDENTITY_ID.getApi(), row.getString(API.IDENTITY_ID.getApi()));
                        sp.saveData(API.EMPLOYEE_ID.getApi(), row.getString(API.EMPLOYEE_ID.getApi()));

                        // validate
                        Toast.makeText(ctx, "Login Success! Validating account...", Toast.LENGTH_SHORT).show();
//                        showSpinnerDialog();
                        new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    VolleyInteractor viv = new VolleyInteractor();
                                    viv.registerCallback(refThis);
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("cn", ServerConstants.LOGDB);
                                    params.put("identityid", sp.getData(API.IDENTITY_ID.getApi()));

                                    Iterator it = params.entrySet().iterator();
                                    String strParams = "";
                                    while (it.hasNext()) {
                                        Map.Entry pair = (Map.Entry)it.next();
                                        strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                                        it.remove();
                                    }
                                    strParams = strParams.replaceAll(" ", "%20");
                                    viv.validate(ctx, params, strParams);
                                }
                            },
                            500
                        );
                    }
                }
            }
        } catch (JSONException e) {
            dismissSpinnerDialog();
            Toast.makeText(ctx, "Login invalid.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    } */

    /* public void onRequestFail(VolleyError volleyError, String type){
        dismissSpinnerDialog();
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
    } */

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

    private void showSpinnerDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.show();
            }
        });
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
//            moveTaskToBack(true);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
            finishAndRemoveTask();
        }
    };

    DialogInterface.OnClickListener cancelCallback = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismissSpinnerDialog();
            alertDialog.hide();
        }
    };

    private AlertDialog actionDialog(final Context activity, String title, String message,
        String strLabel, String strLabel2, String okButtonCaption, View.OnClickListener onClickListener,
        String cancelButtonCaption, View.OnClickListener cancelClickListener, Integer typeFlag) {

        SharedData spx = SharedData.getInstance(this);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, null);

        ((TextView) layout.findViewById(R.id.tv_message)).setText(title);
        ((TextView) layout.findViewById(R.id.tv_message)).setText(message);

        TextView tv_lbl1 = (TextView) layout.findViewById(R.id.tv_label1);
        tv_lbl1.setText(strLabel);
        EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_edittext1);

        TextView tv_lbl2 = (TextView) layout.findViewById(R.id.tv_label2);
        tv_lbl2.setText(strLabel2);
        EditText etDatabase = (EditText) layout.findViewById(R.id.et_edittext2);

        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(onClickListener);

        Button btnAdduser = (Button) layout.findViewById(R.id.btn_add_user);
        btnAdduser.setText(cancelButtonCaption);
        btnAdduser.setOnClickListener(cancelClickListener);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (alertDialog != null) alertDialog.dismiss();
                if (alertDialogSettings != null) alertDialogSettings.dismiss();
                if (alertDialogSettingsAuth != null) alertDialogSettingsAuth.dismiss();
            }
        });

        MaterialRippleLayout btnInfo = (MaterialRippleLayout) layout.findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Info", "Device ID: " + Helper.getImei(ctx), "CLOSE",
                    null, false) );
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private AlertDialog addUserDialog(final Context activity) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_add_user, null);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogUser.dismiss();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private Spinner btnAddJobTitle;
    private FormEditText et_jobtitle;
    private EditText et_jobtitle_id;
    private ArrayAdapter<aAdminGroupings> spinneradapter;
    private void addUser() {
        alertDialogUser = addUserDialog(ctx);
        et_jobtitle = (FormEditText) alertDialogUser.findViewById(R.id.et_jobtitle);
        et_jobtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddJobTitle.performClick();
            }
        });
        et_jobtitle_id = (EditText) alertDialogUser.findViewById(R.id.et_jobtitle_id);
        btnAddJobTitle = (Spinner) alertDialogUser.findViewById(R.id.btnAddJobTitle);

        Button btnSubmit = (Button) alertDialogUser.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormEditText et_employeeno = (FormEditText) alertDialogUser.findViewById(R.id.et_employeeno);
                FormEditText et_firstname = (FormEditText) alertDialogUser.findViewById(R.id.et_firstname);
                FormEditText et_middlename = (FormEditText) alertDialogUser.findViewById(R.id.et_middlename);
                FormEditText et_lastname = (FormEditText) alertDialogUser.findViewById(R.id.et_lastname);
                FormEditText et_password = (FormEditText) alertDialogUser.findViewById(R.id.et_password);
                FormEditText et_repeatpassword = (FormEditText) alertDialogUser.findViewById(R.id.et_repeatpassword);

                if (et_employeeno.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid employee number.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_firstname.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid first name.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_middlename.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid middle name.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_lastname.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid last name.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_jobtitle_id.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Please select job title.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_password.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_repeatpassword.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (!et_repeatpassword.getText().toString().trim().equals(et_password.getText().toString().trim())) {
                    Toast.makeText(ctx, "Psssword does not match.", Toast.LENGTH_SHORT).show(); return;
                }

                loader = Helper.showSpinnerDialog(ctx, "Signing-Up", "Please wait..."); loader.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("cnstr", sp.getData(SharedKey.DATABASE.getKey()));
                params.put("empNo", et_employeeno.getText().toString().trim());
                params.put("email", "");
                params.put("pass", et_password.getText().toString().trim());
                params.put("fname", et_firstname.getText().toString().trim());
                params.put("mname", et_middlename.getText().toString().trim());
                params.put("lname", et_lastname.getText().toString().trim());
                params.put("branch", sp.getData(SharedKey.BRANCH_ID.getKey()));
                params.put("jobtitle", et_jobtitle_id.getText().toString().trim());
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                    it.remove();
                }

                final VolleyInteractor vidp = new VolleyInteractor();
                vidp.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(final String response, String type) {
                        try {
                            JSONArray objArr = new JSONArray(response);
                            JSONObject obj = new JSONObject(objArr.get(0).toString());
                            if (obj.getString("error").equals("true")) {
                                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("DSX signup success", response);
                                Toast.makeText(ctx, "User successfully added!", Toast.LENGTH_SHORT).show();
                                new getUsersAsync().execute("");
                                alertDialogUser.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Log.d("DSX signup success: ", String.valueOf(response.getMessage()));
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("dsx", strParams);
                vidp.postBranchSignUp(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
            }
        });

        alertDialogUser.getWindow().setLayout(Helper.getDialogWidthSignup(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
        alertDialogUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BounceView.addAnimTo(alertDialog);

        VolleyInteractor viag = new VolleyInteractor();
        viag.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(String response, String type) {
                try {
                    response = response.replace("\r\n ", "");
                    Log.d("DSX post response: ", response);
                    JSONArray objArr = new JSONArray(response);
                    final LinkedList<aAdminGroupings> sl = new LinkedList<>();
                    if(objArr.length() > 0) {
                        aAdminGroupings cjt = new aAdminGroupings();
                        cjt.setRecid(0);
                        cjt.setCode("0");
                        cjt.setDescription("Select Job Title:");
                        cjt.setDeleted("false");
                        sl.add(cjt);
                        for (int ix = 0; ix < objArr.length(); ix++) {
                            JSONObject rowObj = objArr.getJSONObject(ix);
                            cjt = new aAdminGroupings();
                            cjt.setRecid(rowObj.getInt("recid"));
                            cjt.setCode(rowObj.getString("code"));
                            cjt.setDescription(rowObj.getString("Description"));
                            cjt.setDeleted(rowObj.getString("deleted"));
                            sl.add(cjt);
                        }
                        spinneradapter = new ArrayAdapter<aAdminGroupings>(ctx, android.R.layout.simple_spinner_dropdown_item, sl) {
                            @Override
                            public boolean isEnabled(int position) {
                                if(position == 0) { return false; }
                                else { return true; }
                            }
                            @Override
                            public View getDropDownView(int pos, View cv, ViewGroup prnt) {
                                View view = super.getDropDownView(pos, cv, prnt);
                                TextView tv = (TextView) view;
                                if(pos == 0){ tv.setTextColor(Color.GRAY);  }
                                else { tv.setTextColor(Color.GRAY); }
                                tv.setText(sl.get(pos).getDescription());
                                return view;
                            }
                        };
                        btnAddJobTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
                                if (pos != 0) {
                                    et_jobtitle.setText(sl.get(pos).getDescription());
                                    et_jobtitle_id.setText(sl.get(pos).getRecid().toString());
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) { }
                        });
                        btnAddJobTitle.setAdapter(spinneradapter);
                    } else {
                        Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onRequestFail(VolleyError response, String type) {
                Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
            }
        });

        // load admin positions
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
        params.put("type", "5");
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
            it.remove();
        }
        viag.getAdminGroupings(ctx, params, strParams.replaceAll(" ", "%20"));
    }

    private void getDeviceProfile(String type) {
        if (loader != null) Helper.dismissSpinnerDialog(loader);
        if (type.equals("default")) {
            loader = Helper.showSpinnerDialog(ctx, "Requesting Info", "Please wait..."); loader.show();
        }

        if (Helper.isNetworkAvailable(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            params.put("deviceid", "n/a");
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
                public void onRequestSuccess(final String response, String typex) {
                    Helper.dismissSpinnerDialog(loader);
                    if (response.equals("[]")) {
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    } else {
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
                                    }

                                    final String[] strRef = typex.split("\\|");
                                    if (strRef[1].equals("reinit")) {
//                                        Toast.makeText(ctx, "Validate", Toast.LENGTH_SHORT).show();
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
                                                sp.saveData(SharedKey.IMEI_ID.getKey(), ab.getDeviceid());
                                                sp.saveData(SharedKey.BRANCH_ID.getKey(), String.valueOf(ab.getBranchid()));
                                                Helper.dismissSpinnerDialog(loader);
                                                alertDialogSettings.dismiss();
                                                Toast.makeText(ctx, "App settings successfully updated.", Toast.LENGTH_SHORT).show();
                                                new getUsersAsync().execute("");
                                            } else {
                                                BounceView.addAnimTo( Helper.okDialog( ctx,
                                                    "Device Registration",
                                                    "This device with ID# " + Helper.getImei(ctx) + " is NOT YET ACTIVATED. Please contact IT support",
                                                    "OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            loadSpinnerBranches();
                                                        }
                                                    }, false) );
                                            }
                                        } else  {
                                            BounceView.addAnimTo( Helper.okDialog( ctx,
                                                "Device Registration",
                                                "This device with ID# " + Helper.getImei(ctx) + " is NOT YET REGISTERED. Please contact IT support",
                                                "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        loadSpinnerBranches();
                                                    }
                                                }, false) );
                                        }
                                    } else if (strRef[1].equals("reload")) {
                                        loadSpinnerBranches();
                                    } else {
                                        Log.d("dsx", "branch list successfully loaded");
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }, 300);
                    }
                }
                @Override
                public void onRequestFail(VolleyError response, String type) {
                    Helper.dismissSpinnerDialog(loader);
                    Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                }
            });
            vidp.getDeviceProfile(getApplicationContext(), params, strParams
                    .replaceAll(" ", "%20"), type);
        } else {
            Helper.dismissSpinnerDialog(loader);
            Toast.makeText(ctx, "This app requires internet to initialize.  Please check your connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private LinkedList<aBranchlist> arrBranches = new LinkedList<>();
    private Spinner msBranches;
    private TextView tvBranchdescription;
    private String refSelectedBranchId = "";
    private void loadSpinnerBranches() {
        if (alertDialogSettings == null) {
            Toast.makeText(ctx, "Fatal error, please contact IT support.", Toast.LENGTH_SHORT).show(); return;
        }
        if(tvBranchdescription != null) tvBranchdescription.setText("Select branch....");
        refSelectedBranchId = "";

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
//                    tv.setText(arrBranches.get(pos).getBranchcode() + " (" + arrBranches.get(pos).getDescription() + ")");
                    tv.setText(arrBranches.get(pos).getDescription() + " (" + arrBranches.get(pos).getBranchcode() + ")");
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, 88);
                params.setMargins(20,0,10,0);
                tv.setLayoutParams(params);
                return view;
            }
        };
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

        if(!sp.getData(SharedKey.BRANCH_ID.getKey()).equals("")) {
            if ( DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                    aBranchlistKey.BRANCHID.getKey() + " = ? ", new String[] { sp.getData(SharedKey.BRANCH_ID.getKey()) }
            ).size() > 0) {
                aBranchlist rsBl = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                        aBranchlistKey.BRANCHID.getKey() + " = ? ", new String[] { sp.getData(SharedKey.BRANCH_ID.getKey()) }
                ).get(0);
                tvBranchdescription.setText(rsBl.getBranchcode() + " (" + rsBl.getDescription() + ")");
                refSelectedBranchId = String.valueOf(rsBl.getBranchid());
            }
        }
    }

    private void postBranchImei() {
        if (!Helper.isNetworkAvailable(this)) {
            Toast.makeText(ctx, "This app requires internet to initialize.  Please check your connection.",
                Toast.LENGTH_SHORT).show(); return;
        }

        if (loader != null) Helper.dismissSpinnerDialog(loader);
        loader = Helper.showSpinnerDialog(ctx, "Processing Registration", "Please wait..."); loader.show();

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
        viri.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(final String response, String type) {
                Helper.dismissSpinnerDialog(loader);
                if (response.toLowerCase().equals("true")) {
                    getDeviceProfile("reinit");
                } else {
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Device Registration","Registration failed. Please contact IT support.",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            }
                        }, false) );
                }
            }
            @Override
            public void onRequestFail(VolleyError response, String type) {
                Helper.dismissSpinnerDialog(loader);
                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
            }
        });
        viri.postBranchImei(getApplicationContext(), params,
                strParams.replaceAll(" ", "%20"));
    }

    @Override
    public void onResume(){
        super.onResume();
        sp = SharedData.getInstance(this);

        new getUsersAsync().execute("");
        Helper.updtaeAdministratorPasswor(ctx);
        new checkVersionUpdate().execute("");
        if (DcBranchlist.getInstance(ctx).getBranchlist().size() < 1) {
            getDeviceProfile("default");
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

    private Storage storageinit;
    private class checkVersionUpdate extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw", ctx.getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .setClock(NanoClock.getDefaultClock())
                        .setProjectId(GlobalConstants.GCP_PROJECTID)
                        .build()
                        .getService();
                try {
                    BlobId blobId = BlobId.of(GlobalConstants.GCP_BUCKET_TARGET_FOR_VERSION, "version.log");
                    Blob blob = storageinit.get(blobId);
                    byte[] bytes =  blob.getContent(Blob.BlobSourceOption.generationMatch());
                    return "Success|" + new String(bytes, "UTF-8");
                } catch (Exception e) {
                    return "Error| exception = " + e.getLocalizedMessage();
                }
            } catch (IOException io) {
                return "Error| io";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String[] resMsg = result.split("\\|");
            if (resMsg[0].equals("Success")) {
                try {
                    Log.d("gcp",  resMsg[1]);
                    JSONArray objArr = new JSONArray(resMsg[1]);
                    if (objArr.length() > 0) {
                        for (int i = 0; i < objArr.length(); i++) {
                            JSONObject rowObj = objArr.getJSONObject(i);
                            if (rowObj.getString("app_name").equals("CUSTOMER PO")) {
                                String strVersionName = rowObj.getString("version_name");
                                if (Integer.parseInt(rowObj.getString("version_code")) > Helper.getVersionCode(ctx)) {
                                    BounceView.addAnimTo( Helper.okDialog( ctx,
                                            "App Update",
                                            "A new version of this app is now available.",
                                            "UPDATE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ctx.getPackageName())));
                                                }
                                            }, false) );
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            Log.d("gcpe", "Task Upload Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("gcpu", "Running " + + values[0]);
        }
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
                        MenuList mlList = new MenuList();
                        mlList.setCustomerID(obj.getString(MenulistKey.CUSTOMER_ID.getKey()));
                        mlList.setCustomerIntegrationId(obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()));
                        String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                        mlList.setCustomerName(strCustomerName);
                        mlList.setRecordCount(0);
                        mlList.setRemarks("");
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
}

