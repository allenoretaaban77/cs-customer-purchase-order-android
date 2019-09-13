package com.fnc.order.android.activity;

import android.Manifest;
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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fnc.order.android.BaseActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.R;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.google.api.core.NanoClock;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

import static java.nio.charset.StandardCharsets.UTF_8;

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
//        Helper.setLogo((ImageView) findViewById(R.id.iv_logo), ctx);

        sp = SharedData.getInstance(ctx);
        if(sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), ServerConstants.SERVER_URL);
        }
//        if(sp.getData(SharedKey.DATABASE.getKey()).trim().equals("")) {
//            sp.saveData(SharedKey.DATABASE.getKey(), ServerConstants.CN);
//        }
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
    }

    private void startProcedure() {
        String strCN = sp.getData(SharedKey.DATABASE.getKey());
        if(strCN.equals("")) {
            if (Helper.isNetworkAvailable(this)) {
                alertDialog = actionDialog( ctx, "APP START-UP",
                    "Please enter your ADMIN ACCOUNT CREDENTIALS to initialize this app.",
                    "Username", "Password", "Branch",
                    "SUBMIT", new View.OnClickListener() {
                        public void onClick(View v) {
                            clientSignin();
                        }
                    }, "", null
                );
                EditText etPassword = (EditText) alertDialog.findViewById(R.id.et_edittext2);
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                alertDialog.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                BounceView.addAnimTo(alertDialog);
            } else {
                alertRequireInternet();
            }
        } else {
            String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
            if (strImeiId.equals("")) {
                new requestToRegImei().execute(Helper.getImei(ctx));
            } else {
                proceedNormal();
            }
        }
    }

    private Storage storageinit;
    private class requestToRegImei extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String strImei = params[0].replace("\r\n", ""),
                strCn = sp.getData(SharedKey.DATABASE.getKey()),
                strRefBranch = sp.getData(SharedKey.DUMMY_BRANCH_ID.getKey()),
                strContents = "";
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw",ctx.getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .setClock(NanoClock.getDefaultClock())
                        .setProjectId(GlobalConstants.GCP_PROJECTID)
                        .build()
                        .getService();
                BlobId blobId = BlobId.of(GlobalConstants.GCP_BUCKET_TARGET,
                        strImei + "_" + strCn + ".log");
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

                try {
                    strContents = strImei + "\n\n" + strCn + "\n\n" + strRefBranch + "\n\n" + Helper.getPostingDate();
                    Blob blob = storageinit.create(blobInfo, strContents.getBytes(UTF_8));
                    return "Success: Upload reference = " + blob.getBlobId() + "|" + strImei + "_" + strCn;
                } catch (Exception e) {
                    return "Error: exception = " + e.getLocalizedMessage();
                }
            } catch (IOException io) {
                return "Error: io";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d("gcp",  result);
            String[] resMsg = result.split(":");
            getDeviceProfile();
            if (resMsg[0].equals("Success")) { } else { }
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

    private void getDeviceProfile() {
        if (Helper.isNetworkAvailable(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
            params.put("deviceid", Helper.getImei(ctx));
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
            vidp.getDeviceProfile(getApplicationContext(), params, strParams
                    .replaceAll(" ", "%20"));
        } else {
            alertRequireInternet();
        }
    }

    private void proceedNormal() {
        LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
        if (sl.size() > 0) {
            showActivity(LoginActivity.class);
        } else {
            if (Helper.isNetworkAvailable(this)) {
                loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();
                VolleyInteractor vipr = new VolleyInteractor();
                vipr.registerCallback(this);
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
                vipr.getPreRequisite(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
            } else {
                alertRequireInternet();
            }
        }
    }

    private void clientSignin() {
        String strUsername = ((EditText) alertDialog.findViewById(R.id.et_edittext1)).getText().toString();
        String strPassword = ((EditText) alertDialog.findViewById(R.id.et_edittext2)).getText().toString();
//        strUsername = "admin@backoffice.com"; strPassword = "admin123";
        String strBranch = ((EditText) alertDialog.findViewById(R.id.et_edittext3)).getText().toString();

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
        if(strBranch.matches("")){
            BounceView.addAnimTo( Helper.okDialog( ctx,
                "Error","Please enter branch name.", "CLOSE",
                null, false) ); return;
        }
        sp.saveData(SharedKey.DUMMY_BRANCH_ID.getKey(), strBranch);

        loader = Helper.showSpinnerDialog(ctx, "", "Posting... Please wait..."); loader.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", strUsername);
        params.put("pass", strPassword);
        VolleyInteractor vil = new VolleyInteractor();
        vil.registerCallback(this);
        vil.login(getApplicationContext(), params);
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
        if (type.equals("login")) {
            try {
                Helper.dismissSpinnerDialog(loader);
                JSONObject obj = new JSONObject(response);
                if (obj.getString("dtcompany").equals("[]")) {
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Initialization Error","Invalid credentials",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false) );
                } else {
                    JSONArray objArr = new JSONArray(obj.getString("dtcompany").toString());
                    JSONObject objx = new JSONObject(objArr.get(0).toString());
                    if (objx.getString("DatabaseID").equals("")) {
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
                            strCN = "backoffice";
                            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 1);
                            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
                            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 0);
                        } else {
                            sp.saveInt(SharedKey.SKU_VALIDATION.getKey(), 0);
                            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 0);
                            sp.saveInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey(), 1);
                            strCN = objx.getString("DatabaseID");
                        }
                        sp.saveData(SharedKey.DATABASE.getKey(), strCN);
                        String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
                        if (strImeiId.equals("")) {
                            new requestToRegImei().execute(Helper.getImei(ctx));
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
                            }
                        }, false) );
            }
        }
        if (type.equals("getdeviceprofile")) {
            if (response.equals("[]")) {
                BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Unrecognized Device",  getString(R.string.unrecognized_device),
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false) );
            } else {
                loader = Helper.showSpinnerDialog(ctx, "", "Syncing Data... Please wait..."); loader.show();

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
                            params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
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
                            BounceView.addAnimTo( Helper.okDialog( ctx,
                                "Data Sync Error","Data Sync Error, please contact IT support",
                                "CLOSE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finishAndRemoveTask();
                                    }
                                }, false) );
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
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Data Sync Error","Data Sync Error, please contact IT support",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }, false) );
                }
            } catch (JSONException e) {
                Helper.dismissSpinnerDialog(loader);
                BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Unrecognized Device", getString(R.string.unrecognized_device),
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false) );
            }
        }
    }

    public void onRequestFail(VolleyError response, String type){
        if (type.equals("login")) {
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
                "Unrecognized Device",getString(R.string.unrecognized_device),
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false) );
        }
        if (type.equals("getprerequisite")) {
            BounceView.addAnimTo( Helper.okDialog( ctx,
                "Unrecognized Device",getString(R.string.unrecognized_device),
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false) );
        }
    }

    private AlertDialog actionDialog(final Context activity, String title, String message,
        String strLabel1, String strLabel2, String strLabel3, String okButtonCaption,
        View.OnClickListener onClickListener, String cancelButtonCaption,
        View.OnClickListener cancelClickListener) {

        SharedData spx = SharedData.getInstance(this);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_init, null);

        ((TextView) layout.findViewById(R.id.tv_dialog_title)).setText(title);
        ((TextView) layout.findViewById(R.id.tv_message)).setText(message);

        ((TextView) layout.findViewById(R.id.tv_label1)).setText(strLabel1);
        ((TextView) layout.findViewById(R.id.tv_label2)).setText(strLabel2);
        ((TextView) layout.findViewById(R.id.tv_label3)).setText(strLabel3);

        Button btnOK = (Button) layout.findViewById(R.id.btn_update);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(onClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }
}
