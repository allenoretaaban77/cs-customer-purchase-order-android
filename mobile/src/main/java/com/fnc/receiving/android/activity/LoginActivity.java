package com.fnc.receiving.android.activity;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.receiving.android.BaseActivity;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.constants.ServerConstants;
import com.fnc.receiving.android.datacontroller.DcStaffs;
import com.fnc.receiving.android.datacontroller.DcUsers;
import com.fnc.receiving.android.enumeration.API;
import com.fnc.receiving.android.enumeration.SharedKey;
import com.fnc.receiving.android.enumeration.aUsersKey;
import com.fnc.receiving.android.model.aStaffs;
import com.fnc.receiving.android.model.aUsers;
import com.fnc.receiving.android.utilities.VolleyInteractor;
import com.fnc.receiving.android.utilities.Helper;
import com.fnc.receiving.android.utilities.PasswordVisibility;
import com.fnc.receiving.android.utilities.SharedData;
import com.fnc.receiving.android.R;
import com.mikhaellopez.rxanimation.RxAnimation;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class LoginActivity extends BaseActivity implements VolleyCallback {

    PasswordVisibility passwordVisibility;
    private EditText usernameText, passwordEText;
    private TextView tvVersion;
    private Button loginButton, hidePassword, showPassword;
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
        setContentView(R.layout.a_activity_login);
        vi = new VolleyInteractor();
        vi.registerCallback(this);

        ctx = this;
        initViews();
        initListeners();

//        loginButton.callOnClick();
    }

    private void initViews() {
        final ImageView backgroundOne = (ImageView) findViewById(R.id.iv_bg1);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.iv_bg2);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX + width);
            }
        });
        animator.start();

        loginButton = (Button) findViewById(R.id.btn_login);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        usernameText = (EditText) findViewById(R.id.et_username);
        passwordEText = (EditText) findViewById(R.id.et_password);
        relPassword = (RelativeLayout) findViewById(R.id.rl_eye);
        hidePassword = (Button) findViewById(R.id.hide_password);
        showPassword = (Button) findViewById(R.id.show_password);

        usernameText.setText("051581");
        passwordEText.setText("7777");
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

                if(usernameStr.matches("") || passwordString.matches("")){
                    alertDialog = Helper.okDialog(ctx,
                        "Error","Invalid username or password", "CLOSE",
                        null, false);
                    BounceView.addAnimTo(alertDialog);
                    return;
                }

                LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
                if (sl.size() > 0) {
//                    Toast.makeText(ctx, "Users: " + String.valueOf(llul.size()),Toast.LENGTH_LONG).show();
                    LinkedList<aStaffs> slUP = DcStaffs.getInstance(ctx).checkStaff(usernameStr, passwordString);
                    if (slUP.size() > 0 ) {
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

                    /*if (Helper.checkOffline(ctx)) {
                        isSubmit = false;
                        alertDialog = Helper.okDialog(ctx,
                                "Error","This app needs to be initialized. Please connect to internet", "CLOSE",
                                null, false);
                        BounceView.addAnimTo(alertDialog);
                    } else {
                        isSubmit = true;
                        showSpinnerDialog();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("userid", usernameStr);
                        params.put("pass", passwordString);
                        vi.login(ctx, params);
                    }*/
                }
            }
        });

        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Underconstruction...", Toast.LENGTH_SHORT).show();
//                alertDialog = settingsDialog(ctx,
//                    "Please update your Employee ID to continue using this application.",
//                    "Update", null,
//                    "Cancel", new View.OnClickListener() {
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                        }
//                    } );
//                BounceView.addAnimTo(alertDialog);
            }
        });
    }

    private void updateEmployeeId(String old_employee_id) {
        loader = Helper.showSpinnerDialog(ctx,"", ""); loader.show();
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

    private class requestUsers extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                VolleyInteractor vic = new VolleyInteractor();
                vic.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(String response, String type) {
//                        Log.d("dsx success", response);
                        Helper.dismissSpinnerDialog(loader);
                        try {
                            response = response.replace("\r\n ", "");
                            JSONArray objArr = new JSONArray(response);
                            if(objArr.length() > 0) {
                                for (int i = 0; i < objArr.length(); i++) {
                                    try {
                                        JSONObject obj = objArr.getJSONObject(i);
                                        aUsers ul = new aUsers();
                                        ul.setIdentityId(obj.getString(aUsersKey.IDENTITYID.getKey()));
                                        ul.setEmail(obj.getString(aUsersKey.EMAIL.getKey()));
                                        ul.setPassword(obj.getString(aUsersKey.PASSWORD.getKey()));
                                        ul.setFirstName(obj.getString(aUsersKey.FIRSTNAME.getKey()));
                                        ul.setMiddleName(obj.getString(aUsersKey.MIDDLENAME.getKey()));
                                        ul.setLastName(obj.getString(aUsersKey.LASTNAME.getKey()));
                                        ul.setDateOfBirth(obj.getString(aUsersKey.DATEOFBIRTH.getKey()));
                                        int isv =  obj.getBoolean(aUsersKey.VERIFIED.getKey()) ? 1 : 0 ;
                                        ul.setVerified(isv);
                                        ul.setCompany_UniqId(obj.getString(aUsersKey.COMPANY_UNIQIE.getKey()));
                                        ul.setReference_employee_no(obj.getString(aUsersKey.REF_EMPLOYEE_NO.getKey()));
                                        ul.setTempo_id(obj.getString(aUsersKey.TEMPO_ID.getKey()));
                                        DcUsers.getInstance(ctx).insertUsers(ul);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {}
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        Toast.makeText(ctx, Helper.getVolleyError(response), Toast.LENGTH_SHORT).show();
                    }
                });
                HashMap<String, String> pr = new HashMap<>();
                pr.put("cn", ServerConstants.LOGDB);
                pr.put("vcomp", "");
                Iterator it = pr.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                    it.remove();
                }
                strParams = strParams.replaceAll(" ", "%20");
                vic.getUsers(ctx, pr, strParams);

                return "Task Get Users Completed";
            } catch (Exception e) {
                return "Task Get Users Failed";
            }
        }
        @Override
        protected void onPostExecute(String result) {
//            new googleClouFetchdProc().execute(srcFile);
            Log.d("dsx", "Task Get Users Post" + result);
        }
        @Override
        protected void onPreExecute() {
            Log.d("gcpe", "Task Get Users Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
//            Log.d("gcpu", "Running " + + values[0]);
        }
    }

    public void onRequestSuccess(String response, String type) {
        try {
            if (type.equals("searchcustomer")) {
            } else {
                JSONObject obj = new JSONObject(response);
                sp = SharedData.getInstance(ctx);
                sp.saveData(API.DATA_BUSINESS.getApi(), obj.getString(API.DATA_BUSINESS.getApi()).toString());
                sp.saveData(API.DATA_COMPANY.getApi(), obj.getString(API.DATA_COMPANY.getApi()).toString());
                sp.saveData(API.DATA_USER.getApi(), obj.getString(API.DATA_USER.getApi()).toString());
                JSONArray datauserArray = obj.getJSONArray(API.DATA_USER.getApi());
                if (datauserArray.toString().equals("[]")) {
                    Helper.dismissSpinnerDialog(loader);
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Invalid username or password", "CLOSE",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
                } else {
                    if(datauserArray.length() > 0) {
                        JSONObject row = datauserArray.getJSONObject(0);
                        sp.saveData(API.IDENTITY_ID.getApi(), row.getString(API.IDENTITY_ID.getApi()));
                        sp.saveData(API.EMPLOYEE_ID.getApi(), row.getString(API.EMPLOYEE_ID.getApi()));

                        // validate
                        Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                        new requestUsers().execute("");
                        showActivity(MainActivity.class);
                    }
                }
            }
        } catch (JSONException e) {
            Helper.dismissSpinnerDialog(loader);
            alertDialog = Helper.okDialog(ctx,
                    "Error","Login error, please contact app developer", "CLOSE",
                    null, false);
            BounceView.addAnimTo(alertDialog);
            e.printStackTrace();
        }
    }

    public void onRequestFail(VolleyError volleyError, String type){
        Helper.dismissSpinnerDialog(loader);
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
//                Helper.dismissSpinnerDialog(loader);
//                startActivity(new Intent(ctx, cls));
                ActivityOptions options = ActivityOptions.makeCustomAnimation(ctx, R.anim.fade_in, R.anim.fade_out);
                startActivity(new Intent(getApplicationContext(), cls), options.toBundle());
                finish();
            }
        }, 100);
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
            Helper.dismissSpinnerDialog(loader);
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

        final SharedData spx = SharedData.getInstance(this);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, null);
        final EditText etDomainServerName = (EditText) layout.findViewById(R.id.et_domain_svr);
        etDomainServerName.setText(spx.getData(SharedKey.DOMAIN_SERVER_URL.getKey()));
        final EditText etDatabase = (EditText) layout.findViewById(R.id.et_database);
        etDatabase.setText(spx.getData(SharedKey.DATABASE.getKey()));
        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                spx.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), etDomainServerName.getText().toString().trim());
                spx.saveData(SharedKey.DATABASE.getKey(), etDatabase.getText().toString().trim());
                alertDialog.dismiss();
                Toast.makeText(activity, "Server settings saved successfully.", Toast.LENGTH_SHORT).show();
            }
        });

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

