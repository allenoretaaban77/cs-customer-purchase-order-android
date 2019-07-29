package com.fnc.receiving.android.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fnc.receiving.android.BaseActivity;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.constants.GlobalConstants;
import com.fnc.receiving.android.constants.ServerConstants;
import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.datacontroller.DcStaffs;
import com.fnc.receiving.android.datacontroller.DcUsers;
import com.fnc.receiving.android.enumeration.API;
import com.fnc.receiving.android.enumeration.SharedKey;
import com.fnc.receiving.android.enumeration.aUsersKey;
import com.fnc.receiving.android.model.aStaffs;
import com.fnc.receiving.android.model.aUsers;
import com.fnc.receiving.android.utilities.Helper;
import com.fnc.receiving.android.utilities.SharedData;
import com.fnc.receiving.android.utilities.VolleyInteractor;
import com.google.api.core.NanoClock;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;
import io.grpc.Server;
import spencerstudios.com.bungeelib.Bungee;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SplashActivity extends BaseActivity implements VolleyCallback{

    private Context ctx;
    private TextView tvVersion;
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
        setContentView(R.layout.a_activity_splash);

        sp = SharedData.getInstance(this);
        if(sp.getData(SharedKey.DOMAIN_SERVER_URL.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), ServerConstants.SERVER_URL);
        }
        if(sp.getData(SharedKey.DATABASE.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DATABASE.getKey(), ServerConstants.CN);
        }
        if(sp.getData(SharedKey.DATABASE_OLD.getKey()).trim().equals("")) {
            sp.saveData(SharedKey.DATABASE_OLD.getKey(), "");
        }

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
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, this));

        final VolleyInteractor vidp = new VolleyInteractor();
        vidp.registerCallback(this);
        sp = SharedData.getInstance(ctx);
        String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey());
//        strImeiId = "";
        if (strImeiId.equals("")) {
            if (Helper.isNetworkAvailable(this)) {
                TedPermission.with(ctx).setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("cn", ServerConstants.CN);
                            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                            String strIMEI = tm.getImei();
//                            params.put("deviceid", strIMEI);
                            params.put("deviceid", "353800100112222"); // timog
                            Iterator it = params.entrySet().iterator();
                            String strParams = "";
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                                it.remove();
                            }
                            vidp.getDeviceProfile(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
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
            }else{
                alertDialog = Helper.okDialog(ctx,
                    "Connection Error","Please check your internet connection",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            }
        } else {
            LinkedList<aStaffs> sl = DcStaffs.getInstance(ctx).getStaffs();
            if (sl.size() > 0) {
                showActivity(LoginActivity.class);
            } else {
                loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();

                VolleyInteractor vipr = new VolleyInteractor();
                vipr.registerCallback(this);
                HashMap<String, String> params = new HashMap<>();
                params.put("cn", ServerConstants.CN);
                SharedData sppr = SharedData.getInstance(ctx);
                params.put("branchid", sppr.getData(SharedKey.BRANCH_ID.getKey()));
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                    it.remove();
                }
                vipr.getPreRequisite(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));
            }
        }
    }

    private Storage storageinit;
    private ArrayList<String> strFileArr = new ArrayList<String>();
    private class googleClouFetchdProc extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String strCustomerName = params[0].replace("\r\n", "");
            try {
                InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw", getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setClock(NanoClock.getDefaultClock())
                    .setProjectId(GlobalConstants.GCP_PROJECTID)
                    .build()
                    .getService();

                File file = new File(Helper.getProjectPath(ctx)); if(!file.exists()){ file.mkdir(); } // make first main dir
                file = new File(Helper.getFilePath(ctx)); if(!file.exists()){ file.mkdir(); }

                Page<Bucket> buckets = storageinit.list();
                for (Bucket bucket : buckets.iterateAll()) {
                    if (bucket.getName().equals(GlobalConstants.GCP_BUCKET)) {
                        Iterable<Blob> blobs = bucket.list().getValues();
                        for (Blob b : blobs) {
                            if (b != null) {
                                String[] refStr = b.getGeneratedId().split("/");
                                if (refStr[1].equals("OG")) { // get only selected costumer
                                    if (refStr[2].equals("07252019")) { // get only selected date
                                        if (!refStr[3].equals("")) {
                                            Log.d("gcpd", Helper.getFilePath(ctx) + refStr[3]);
                                            b.downloadTo(Paths.get(Helper.getFilePath(ctx) + refStr[3]));
                                            strFileArr.add(refStr[3]);
                                        }
                                    }
                                }
                            }
                        }
                        // Blob blob = storageinit.get(BlobId.of(bucket.getName(), "OG/07252019"));
                    }
                }
                Log.d("gcpf", "Task Completed");

                return "Task Completed";
            } catch (IOException io) {
                Log.d("gcpf", "Task Failed");
                return "Task Failed";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            for (String strFile : strFileArr) {
                Log.d("gcpt", strFile);
                File file = new File(Helper.getFilePath(ctx), strFile);
                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    file.delete();
                    Log.d("gcpt", text.toString());
                } catch (IOException e) {
                    Log.d("gcpf", "Task Read Failed");
                }
            }

            Log.d("gcpe", "Task Post");
        }
        @Override
        protected void onPreExecute() {
            Log.d("gcpe", "Task Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("gcpu", "Running " + + values[0]);
        }
    }

    private class googleCloudUploadProc extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String srcFile = params[0].replace("\r\n", "");
//            srcFile = "fnc_sample_" + Helper.getDateLongInteger() + ".txt";
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw", getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .setClock(NanoClock.getDefaultClock())
                        .setProjectId(GlobalConstants.GCP_PROJECTID)
                        .build()
                        .getService();

                BlobId blobId = BlobId.of(GlobalConstants.GCP_BUCKET, srcFile);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/html").build();

                String sampleString = "";
//                sampleString = "FNC SAMPLE GCP UPLOAD: " + Helper.getPostingDate() + "\n\n";
                Document doc = Jsoup.connect("https://nathaniels.com.ph/").get();
//                sampleString = sampleString + doc.title() + "\n\n";
                sampleString = sampleString + doc.select("html").first();
//                sampleString = sampleString + doc.select("body").first();
                /*Elements newsHeadlines = doc.select("#mp-itn b a");
                for (Element headline : newsHeadlines) {
                    sampleString = sampleString + headline.attr("title");
                    sampleString = sampleString + headline.html();
                }*/
                try {
                    Blob blob = storageinit.create(blobInfo, sampleString.getBytes(UTF_8));
                } catch (Exception e) {

                }

                return "Task Upload Completed";
            } catch (IOException io) {
                Log.d("gcpf", "Task Upload Failed");
                return "Task Upload Failed";
            }
        }
        @Override
        protected void onPostExecute(String result) {
//            new googleClouFetchdProc().execute(srcFile);
            Log.d("gcpe", "Task Upload Post");
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

    public static void main(String... args) throws Exception {
        // Instantiates a client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // The name for the new bucket
        String bucketName = "store_checklist";

        // Creates the new bucket
        Bucket bucket = storage.create(BucketInfo.of(bucketName));

        System.out.printf("Bucket %s created.%n", bucket.getName());
    }

    private void showActivity(final Class<?> cls) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), cls));
                Bungee.fade(ctx);
                finish();
            }
        }, 100);
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

        if (type.equals("getdeviceprofile")) {
            if (response.equals("[]")) {
                alertDialog = Helper.okDialog(ctx,
                    "Unrecognized Device","Please contact IT support",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            } else {

                loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();

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
                            params.put("cn", ServerConstants.CN);
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
                            alertDialog = Helper.okDialog(ctx,
                                    "Data Sync Error","Please contact IT support",
                                    "CLOSE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finishAndRemoveTask();
                                        }
                                    }, false);
                            BounceView.addAnimTo(alertDialog);
                            e.printStackTrace();
                        }
                    }
                }, 1000);
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

//                    final VolleyInteractor viag = new VolleyInteractor();
//                    viag.registerCallback(this);
//
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("cn", c_global.DBNAME);
//                    params.put("type", "5");
//                    Iterator it = params.entrySet().iterator();
//                    String strParams = "";
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry) it.next();
//                        strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
//                        it.remove();
//                    }
//                    viag.getAdminGroupings(getApplicationContext(), params, strParams.replaceAll(" ", "%20"));

                    Helper.dismissSpinnerDialog(loader);
                    showActivity(LoginActivity.class);

                } else {
                    Helper.dismissSpinnerDialog(loader);
                    alertDialog = Helper.okDialog(ctx,
                        "Data Sync Error","Please contact IT support",
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAndRemoveTask();
                            }
                        }, false);
                    BounceView.addAnimTo(alertDialog);
                }
            } catch (JSONException e) {
                Helper.dismissSpinnerDialog(loader);
                alertDialog = Helper.okDialog(ctx,
                    "Data Sync Error","Please contact IT support",
                    "CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }, false);
                BounceView.addAnimTo(alertDialog);
            }

        }
    }

    public void onRequestFail(VolleyError response, String type){

        if (type.equals("getdeviceprofile")) {
            alertDialog = Helper.okDialog(ctx,
                "Unrecognized Device","Please contact IT support",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false);
            BounceView.addAnimTo(alertDialog);
        }

        if (type.equals("getprerequisite")) {
//            pd.dismiss();
            alertDialog = Helper.okDialog(ctx,
                "Unrecognized Device","Please contact IT support",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false);
            BounceView.addAnimTo(alertDialog);
        }

        if (type.equals("getadmingroupings")) {
            alertDialog = Helper.okDialog(ctx,
                "Data Sync Erro","Please contact IT support",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                }, false);
            BounceView.addAnimTo(alertDialog);
        }
    }
}
