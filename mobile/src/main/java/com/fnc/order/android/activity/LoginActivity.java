package com.fnc.order.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.datacontroller.DcUserslist;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.UserslistKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.Userslist;
import com.fnc.order.android.model.aItemlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PasswordVisibility;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.R;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    private AlertDialog alertDialog;
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

        initViews();
        initListeners();
    }

    @Override
    public void onResume(){
        super.onResume();
        super.onResume();

        SharedData sp = SharedData.getInstance(this);
        sp.saveData(SharedKey.DEV_USERNAME.getKey(), "dev");
        sp.saveData(SharedKey.DEV_PASSWORD.getKey(), "P@ssw0rd" + Helper.getNumericMonthDay());

        Helper.setLogo((ImageView) findViewById(R.id.iv_logo), this);
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

//        usernameText.setText("02");
//        passwordEText.setText("7777777");
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

        loginButton.setOnClickListener(new View.OnClickListener() {
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
                        SharedData.getInstance(ctx).saveData(SharedKey.IDENTITY_ID.getKey(), "-1");
                        SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_NO.getKey(), "-1");
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_NO.getKey(), String.valueOf(slx.getEmpNo()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_NAME.getKey(), String.valueOf(slx.getName()));
                        SharedData.getInstance(ctx).saveData(SharedKey.EMP_POSITION.getKey(), String.valueOf(slx.getJobtitle()));
                        isSubmit = true;
                        showActivity(MainActivity.class);
                        Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                    } else {
                        isSubmit = false;
                        alertDialog = Helper.okDialog(ctx,
                            "Error","Invalid username or password", "CLOSE",
                            null, false);
                        BounceView.addAnimTo(alertDialog);
                    }
                } else {
                    alertDialog = Helper.okDialog(ctx,
                        "Data Sync Erro","This app needs to be initialized, please connect to the internet",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }, false);
                    BounceView.addAnimTo(alertDialog);
                }

            }
        });

        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = actionDialog(ctx,
                    "Validate settings security account",
                    "Username", "Password",
                    "SUBMIT", new View.OnClickListener() {
                        public void onClick(View v) {
                            LinearLayout layout = (LinearLayout) ((ViewGroup) v.getParent()).getParent().getParent();
                            EditText etUsername = (EditText) layout.findViewById(R.id.et_edittext1);
                            EditText etPassword = (EditText) layout.findViewById(R.id.et_edittext2);
                            SharedData spx = SharedData.getInstance(ctx);
                            alertDialog.dismiss();

                            if (spx.getData(SharedKey.DEV_USERNAME.getKey()).equals(etUsername.getText().toString()) &&
                                    spx.getData(SharedKey.DEV_PASSWORD.getKey()).equals(etPassword.getText().toString())) {
                                alertDialog = actionDialog(ctx,
                                        "Customize settings per client as required",
                                        "Server", "Database",
                                        "UPATE", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                LinearLayout layout = (LinearLayout) ((ViewGroup) v.getParent()).getParent().getParent();
                                                EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_edittext1);
                                                EditText etDatabase = (EditText) layout.findViewById(R.id.et_edittext2);
                                                Switch sw_skuvalid = (Switch) layout.findViewById(R.id.sw_skuvalid);
                                                Switch sw_preloaditems = (Switch) layout.findViewById(R.id.sw_preloaditems);
                                                Switch sw_saveitems = (Switch) layout.findViewById(R.id.sw_saveitems);
                                                alertDialog.dismiss();
                                                SharedData spx = SharedData.getInstance(ctx);
                                                spx.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "http://" + etDomainServerName.getText().toString().trim() + "/");
                                                spx.saveData(SharedKey.DATABASE.getKey(), etDatabase.getText().toString().trim());
                                                spx.saveInt(SharedKey.SKU_VALIDATION.getKey(), sw_skuvalid.isChecked() ? 1 : 0);
                                                spx.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), sw_preloaditems.isChecked() ? 1 : 0);
                                                spx.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), sw_saveitems.isChecked() ? 1 : 0);
                                                Toast.makeText(ctx, "Server settings saved successfully.", Toast.LENGTH_SHORT).show();

                                                DcAitemlist.getInstance(ctx).emptyaItemlist();
                                                DcOrdered.getInstance(ctx).emptyOrderedlist();
                                                DcMenulist.getInstance(ctx).emptyMenulist();
                                            }
                                        },
                                        "Cancel", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        },1);
                                EditText etDomainServerName = (EditText) alertDialog.findViewById(R.id.et_edittext1);
                                String strSN = spx.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).replace("http://","").replace("/","");
                                etDomainServerName.setText(strSN);
                                EditText etDatabase = (EditText) alertDialog.findViewById(R.id.et_edittext2);
                                etDatabase.setText(spx.getData(SharedKey.DATABASE.getKey()));

                                Switch sw_skuvalid = (Switch) alertDialog.findViewById(R.id.sw_skuvalid);
                                sw_skuvalid.setChecked(spx.getInt(SharedKey.SKU_VALIDATION.getKey()) == 1 ? true : false);

                                Switch sw_preloaditems = (Switch) alertDialog.findViewById(R.id.sw_preloaditems);
                                sw_preloaditems.setChecked(spx.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == 1 ? true : false);

                                Switch sw_saveitems = (Switch) alertDialog.findViewById(R.id.sw_saveitems);
                                sw_saveitems.setChecked(spx.getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1 ? true : false);

                                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                BounceView.addAnimTo(alertDialog);
                            } else {
                                Toast.makeText(ctx, "Access denied. Invalid credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    "Cancel", new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    },
                    2);

                EditText etPassword = (EditText) alertDialog.findViewById(R.id.et_edittext2);
                LinearLayout ll_skuvalid = (LinearLayout) alertDialog.findViewById(R.id.ll_validations);
                ll_skuvalid.setVisibility(View.GONE);
//                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialog);
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

    /* private AlertDialog okCancelInputDialogBuilder(final Context activity, String message,
                                                   String okButtonCaption, View.OnClickListener onClickListener,
                                                   String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_dialog_default, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        final EditText etText = (EditText) layout.findViewById(R.id.et_inputtext);

        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!etText.getText().toString().trim().equals("")) {
                    alertDialog.dismiss();
                    updateEmployeeId(etText.getText().toString().trim());
                }else{
                    dismissSpinnerDialog();
                    Toast.makeText(activity, "Please input employee id.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
        btnCancel.setText(cancelButtonCaption);
        btnCancel.setOnClickListener(cancelClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    } */

    private AlertDialog actionDialog(final Context activity, String message, String strLabel1, String strLabel2,
                                     String okButtonCaption, View.OnClickListener onClickListener,
                                     String cancelButtonCaption, View.OnClickListener cancelClickListener, Integer typeFlag) {

        SharedData spx = SharedData.getInstance(this);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, null);

        TextView tv_message = (TextView) layout.findViewById(R.id.tv_message);
        tv_message.setText(message);

        TextView tv_lbl1 = (TextView) layout.findViewById(R.id.tv_label1);
        tv_lbl1.setText(strLabel1);
        EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_edittext1);

        TextView tv_lbl2 = (TextView) layout.findViewById(R.id.tv_label2);
        tv_lbl2.setText(strLabel2);
        EditText etDatabase = (EditText) layout.findViewById(R.id.et_edittext2);

        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(onClickListener);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }
}

