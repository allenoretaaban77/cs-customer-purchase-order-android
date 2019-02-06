package com.fnc.order.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PasswordVisibility;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.R;

import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements VolleyCallback {

    PasswordVisibility passwordVisibility;
    private FormEditText usernameText;
    private FormEditText passwordEText;
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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        usernameText.setText("7777777");
        passwordEText.setText("7777777");
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
                    return;
                }
                if(passwordEText.getText().toString().matches("")){
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please enter password.", "CLOSE",
                            null, false);
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
    }

    public void onRequestSuccess(String response, String type) {
        dismissSpinnerDialog();
        try {
            JSONObject obj = new JSONObject(response);
            sp = SharedData.getInstance(ctx);

            sp.saveData(API.DATA_BUSINESS.getApi(), obj.getString(API.DATA_BUSINESS.getApi()).toString());
            sp.saveData(API.DATA_COMPANY.getApi(), obj.getString(API.DATA_COMPANY.getApi()).toString());
            sp.saveData(API.DATA_USER.getApi(), obj.getString(API.DATA_USER.getApi()).toString());
            JSONArray datauserArray = obj.getJSONArray(API.DATA_USER.getApi());
            if(datauserArray.toString().equals("[]")) {
                Toast.makeText(ctx, "Login invalid.", Toast.LENGTH_SHORT).show();
            }else{
                if(datauserArray.length() > 0) {
                    JSONObject row = datauserArray.getJSONObject(0);
                    sp.saveData(API.IDENTITY_ID.getApi(), row.getString(API.IDENTITY_ID.getApi()));
                    Toast.makeText(ctx, "Login success...", Toast.LENGTH_SHORT).show();
                    showActivity(MainActivity.class);
                }
            }
        } catch (JSONException e) {
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

}

