package com.fnc.order.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.fragment.SearchItemFragment;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PasswordVisibility;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.R;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import hari.bounceview.BounceView;
import spencerstudios.com.bungeelib.Bungee;

public class LoginActivity extends BaseActivity implements VolleyCallback {

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
        vi = new VolleyInteractor();
        vi.registerCallback(this);
        SharedData sp = SharedData.getInstance(this);
        sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "http://apics.fncnathaniel.com/");
        sp.saveData(SharedKey.LOCAL_SERVER_URL.getKey(), "http://192.168.1.200:81/");

        ctx = this;
        initViews();
        initListeners();
    }

    private void initViews() {
        relPassword = (RelativeLayout)findViewById(R.id.relPassword);
        usernameText = (FormEditText)findViewById(R.id.username);
        usernameText.setFilters(new InputFilter[] { filter });
        passwordEText = (FormEditText) findViewById(R.id.password);
        passwordEText.setText("");
        hidePassword = (Button)findViewById(R.id.hide_password);
        showPassword = (Button)findViewById(R.id.show_password);
        loginButton = (Button)findViewById(R.id.login_button);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        usernameText.setText("19130");
//        passwordEText.setText("nathaniels@1994");
//        usernameText.setText("aban.allen@yahoo.com");
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

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordEText.getWindowToken(), 0);

                if(usernameText.getText().toString().matches("")){
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please enter username.", "CLOSE",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
                    return;
                }
                if(passwordEText.getText().toString().matches("")){
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please enter password.", "CLOSE",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
                    return;
                }

                isSubmit = true;
                showSpinnerDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("userid", usernameText.getText().toString());
                params.put("pass", passwordEText.getText().toString());
                vi.login(ctx, params);
            }
        });

        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = settingsDialog(ctx,
                    "Please update your Employee ID to continue using this application.",
                    "Update", null,
                    "Cancel", new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    } );
                BounceView.addAnimTo(alertDialog);
            }
        });
    }

    private void updateEmployeeId(String old_employee_id) {
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
    }

    private void requestCustomers(String stringSearch) {
        VolleyInteractor vic = new VolleyInteractor();
        vic.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", ServerConstants.CN);
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
    }

    public void onRequestSuccess(String response, String type) {
        final VolleyCallback refThis = this;
        try {
            if (type.equals("searchcustomer")) {
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
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
                                dismissSpinnerDialog();
                                showActivity(MainActivity.class);
                                Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        1000
                    );
                }
            } else if (type.equals("updateemployeeid")) {
                if(!response.trim().equals("") && !response.trim().equals("null")) {
                    sp = SharedData.getInstance(ctx);
                    sp.saveData(API.EMPLOYEE_ID.getApi(), response.trim());
                    LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "");
                    if(llr.size() > 0) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        dismissSpinnerDialog();
                                        showActivity(MainActivity.class);
                                        Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(ctx, "Employee id updated successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                1000
                        );
                    } else {
                        requestCustomers("");
                    }
                }
            } else if (type.equals("validate")) {
                if (response.trim().equals("True")) {
                    LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "");
                    if(llr.size() > 0) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        dismissSpinnerDialog();
                                        showActivity(MainActivity.class);
                                        Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                1000
                        );
                    } else {
                        requestCustomers("");
                    }
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
                            } );
                    BounceView.addAnimTo(alertDialog);
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
    }

    public void onRequestFail(VolleyError volleyError, String type){
        dismissSpinnerDialog();
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
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
                dismissSpinnerDialog();
                startActivity(new Intent(getApplicationContext(), cls));
                Bungee.inAndOut(ctx);
                finish();
            }
        }, 300);
    }

    private void showSpinnerDialog(){
        loader = Helper.buildSpinnerDialog(this);
        loader.show();
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
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            finish();
        }
    };

    DialogInterface.OnClickListener cancelCallback = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismissSpinnerDialog();
            alertDialog.hide();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    private AlertDialog okCancelInputDialogBuilder(final Context activity, String message,
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
    }

    private AlertDialog settingsDialog(final Context activity, String message,
                                                   String okButtonCaption, View.OnClickListener onClickListener,
                                                   String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, null);
        final EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_domain_svr);
        final EditText etLocalServerName = (EditText) layout.findViewById(R.id.et_local_svr);
        etDomainServerName.setHint("e.g.: http://apics.fncnathaniel.com/");
        etLocalServerName.setText("e.g.: http://192.168.1.200:81/");

        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedData sp = SharedData.getInstance(ctx);
                alertDialog.dismiss();
                Toast.makeText(activity, "Server settings saved successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(true);
        return builder.show();
    }
}

