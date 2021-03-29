package com.fnc.order.android.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.activity.SplashActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.enumeration.aStaffsKey;
import com.fnc.order.android.fragment.CustomerFragment;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aStaffs;
import com.google.api.core.NanoClock;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.callback.Callback;

import hari.bounceview.BounceView;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.pm.PackageInfoCompat.getLongVersionCode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Helper {

    public static String getPostingDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("M/dd/yyyy HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static long dateToMillis(String date,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date mDate = dateFormat.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ProgressDialog buildSpinnerDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressSpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        return progressDialog;
    }

    public static boolean isTextFieldBlank(EditText editText){
        String text = editText.getText().toString();
        if(text.matches("")){
            return  true;
        }else{
            return false;
        }
    }

    public static AlertDialog okCancelDialog(final Context activity, String title, String message,
        String okButtonCaption, DialogInterface.OnClickListener okClickListener,
        String cancelButtonCaption, DialogInterface.OnClickListener cancelClickListener,
        Boolean isCancelable) {

        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(activity,
                    android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new android.app.AlertDialog.Builder(activity);
        }
        builder.setCancelable(isCancelable);
        builder.setTitle(title).setMessage(message)
            .setPositiveButton(okButtonCaption, okClickListener)
            .setNegativeButton(cancelButtonCaption, cancelClickListener);
        builder.create();
        return builder.show();
    }

    public static AlertDialog okCancelInputDialogBuilder(final Context activity, String message,
               String okButtonCaption, View.OnClickListener onClickListener,
                 String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_dialog, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(cancelButtonCaption);
        Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
        btnCancel.setText(okButtonCaption);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        EditText etText = (EditText) layout.findViewById(R.id.et_password);

        btnOK.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(cancelClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    public static AlertDialog okCancelInputRemarksDialogBuilder(final Context activity, String message,
                                                         String okButtonCaption, View.OnClickListener onClickListener,
                                                         String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_remarks_dialog, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(cancelButtonCaption);
        Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
        btnCancel.setText(okButtonCaption);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        EditText etText = (EditText) layout.findViewById(R.id.et_password);

        btnOK.setOnClickListener(onClickListener);
        btnOK.setOnClickListener(cancelClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    public static AlertDialog okDialog(final Context activity, String title, String message,
        String okButtonCaption, DialogInterface.OnClickListener okClickListener,
        Boolean isCancelable) {

        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(activity,
                    android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new android.app.AlertDialog.Builder(activity);
        }
        builder.setCancelable(isCancelable);
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(okButtonCaption, okClickListener);
        builder.create();
        return builder.show();
    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String getVolleyError(VolleyError volleyError) {
        String message = "";
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        } else {
            message = "Request Error! Please try again.";
        }
        return message;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String getVersion(Context context, Activity activity) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static void syncData(Context ctx, VolleyCallback volleycb) {
        VolleyInteractor v = new VolleyInteractor();
        v.registerCallback(volleycb);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
        params.put("customer", "");

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        v.getCustomers(ctx, params, strParams);
    }

    public static double roundTo(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean checkOfflineLogin(Context ctx){
        if(isNetworkAvailable(ctx)){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setLogo(ImageView ivlogo, Context ctx) {
        SharedData sp = SharedData.getInstance(ctx);
        switch (sp.getData(SharedKey.DATABASE.getKey())) {
            case "massive":
                ivlogo.setImageResource(R.drawable.massive_logo);
                break;
            default:
                ivlogo.setImageResource(R.drawable.logo);
                break;
        }
    }

    public static String getNumericMonthDay() {
        Date date = Calendar.getInstance().getTime();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Sunday
        String day          = (String) DateFormat.format("dd",   date); // 23
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2019
        return monthNumber + day;
    }

    public static double getScrRatio(Context ctx) { // if >= to 0.6 the tab
        double refMult = Double.parseDouble(String.valueOf(Helper.getScreenDimension(ctx, "w"))) /
                Double.parseDouble(String.valueOf(Helper.getScreenDimension(ctx, "h")));
        return refMult;
    }

    public static int getScreenDimension(Context ctx, String strSide) {
        WindowManager wm = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        if (strSide.equals("w")) {
            return displayMetrics.widthPixels;
        } else {
            return displayMetrics.heightPixels;
        }
    }

    public static int getDialogWidthSignup(Context ctx) {
        if (Helper.getScreenDimension(ctx, "w") >= 1200) {
            return 1100;
        } else {
            return 700;
        }
    }

    public static int getDialogWidth(Context ctx) {
        Double tWidth = 0.0;
        if (Helper.getScreenDimension(ctx, "w") >= 1200) {
            tWidth = Helper.getScreenDimension(ctx, "w") * 0.7;
        } else {
            tWidth = Helper.getScreenDimension(ctx, "w") * 0.80;
        }
        return (int)Math.round(tWidth);
    }

    public static void changePage(Context ctx, FragmentManager fm, Fragment fr,
        String target_fragmentname, String previous_fragmentname) {
        fm.beginTransaction().replace(R.id.container, fr, target_fragmentname).addToBackStack(null).commit();
        SharedData.getInstance(ctx).saveData(SharedKey.CURRENT_PAGE.getKey(), previous_fragmentname);
    }
    public static void setPreviousPage(Context ctx, String page) {
        SharedData.getInstance(ctx).saveData(SharedKey.CURRENT_PAGE.getKey(), page);
    }
    public static String getPage(Context ctx) {
        return SharedData.getInstance(ctx).getData(SharedKey.CURRENT_PAGE.getKey());
    }

    public static String getImei(Context ctx, String strBranchId) {
        SharedData sp = SharedData.getInstance(ctx);
        String strImeiId = sp.getData(SharedKey.IMEI_ID.getKey()), resStrImeiId = "";
        if (strImeiId.equals("")) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
//                sp.saveData("ref_device_id", "CO-82EC1E7D-79EC-4D70-BCBC-E5AE06EE3C66");
                sp.saveData("ref_device_id", "CO-" + UUID.randomUUID().toString().toUpperCase());
                resStrImeiId = sp.getData("ref_device_id");
            } else if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resStrImeiId = getImeiNew(ctx).trim();
            } else {
                resStrImeiId = getImeiOld(ctx).trim();
            }
            sp.saveData(SharedKey.IMEI_ID.getKey(), resStrImeiId);
            return resStrImeiId;
        } else {
            return strImeiId;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private static String getImeiOld(Context ctx) {
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch(SecurityException e) {
            e.printStackTrace();
            return "";
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private static String getImeiNew(Context ctx) {
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getImei();
        } catch(SecurityException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return getVersionCodeNew(ctx);
        } else {
            return getVersionCodOld(ctx);
        }
    }
    @TargetApi(Build.VERSION_CODES.P)
    private static int getVersionCodeNew(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            return Integer.parseInt(String.valueOf(getLongVersionCode(pInfo)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private static int getVersionCodOld(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static ProgressDialog showSpinnerDialog(Context context, String title, String message){
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressSpinnerTheme);
        if (!title.trim().equals("")) { progressDialog.setTitle(title); }
        if (!message.trim().equals("")) { progressDialog.setMessage(message); }
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void dismissSpinnerDialog(ProgressDialog loader) {
        if(loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    public static void insertDefaultStaffs(Context ctx) {
        DcStaffs.getInstance(ctx).deleteStaffsViaId(SharedKey.SUPPORT_EMP_ID.getKey());
        DcStaffs.getInstance(ctx).insertStaffs(Helper.adminStaff(ctx)); // add admin
    }

    public static void updateAdministratorPassword(Context ctx) {
        /*SharedData sp = SharedData.getInstance(ctx);
        aStaffsKey kArr[] = { aStaffsKey.EMPNO , aStaffsKey.PASS};
        String[] vArr =  {  "", "" };
        DcStaffs.getInstance(ctx).updateStaff(
            aStaffsKey.EMPID.getKey(), sp.getData(SharedKey.REF_ADMIN_PASSWORD.getKey()),

        );*/
    }

    private static aStaffs defaultStaff(Context ctx) {
        SharedData sp = SharedData.getInstance(ctx);
        aStaffs cs = new aStaffs(1910454835L, "-2", "administrator", "aoaban@nathaniels.com.ph",
            "IT Support", Long.parseLong(sp.getData(SharedKey.BRANCH_ID.getKey())),
            1912072415L, "P@ssw0rd" + Helper.getReqDate(0, ""),
            "true", "true");
        return cs;
    }

    private static aStaffs adminStaff(Context ctx) {
        SharedData sp = SharedData.getInstance(ctx);
        aStaffs cs = new aStaffs(
            Long.parseLong(sp.getData(SharedKey.SUPPORT_EMP_ID.getKey())),
            sp.getData(SharedKey.SUPPORT_REF_EMP_ID.getKey()),
            sp.getData(SharedKey.SUPPORT_USER.getKey()),
            sp.getData(SharedKey.REF_ADMIN_USER.getKey()),
            sp.getData(SharedKey.REF_ADMIN_FULLNAME.getKey()),
            Long.parseLong(sp.getData(SharedKey.BRANCH_ID.getKey())),
            1912072415L, sp.getData(SharedKey.SUPPORT_PASSWORD.getKey()),
            "true", "true");
        return cs;
    }

    public static String getReqDate(Integer type, String dateSample) {
        Date date = Calendar.getInstance().getTime();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Sunday
        String day          = (String) DateFormat.format("dd",   date); // 23
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2019
        switch (type) {
            case 7:
                String[] dArr = dateSample.split(" ");
                String[] dArrx = dArr[0].split("/");
                String sM = dArrx[0].length() > 1 ? dArrx[0] : "0" + dArrx[0] ;
                String sD = dArrx[1].length() > 1 ? dArrx[1] : "0" + dArrx[1] ;
                java.text.DateFormat dfyx = new SimpleDateFormat(GlobalConstants.TIME_REF_FORMAT);
                return dArrx[2] + "-" + sM + "-" + sD + " " + dfyx.format(date);
            case 5:
                java.text.DateFormat dfy = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_GCP);
                return dfy.format(date);
            case 4:
                java.text.DateFormat dfx = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_POST);
                return dfx.format(date);
            case 3:
                try{
                    SimpleDateFormat format = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_TZ);
                    date = format.parse(dateSample + "Z");
                    java.text.DateFormat df = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
                    return df.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            case 2:
                return  monthNumber + day + year;
            case 1:
                try{
                    SimpleDateFormat format = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_TZ);
                    date = format.parse(dateSample + "Z");
                    java.text.DateFormat df = new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT);
                    return df.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            default:
                return monthNumber + day;
        }
    }

    public static float convertPixelsToDp(Context context, float px){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static float convertDpToPixel(Context context, float dp){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static LinkedList<aBranchlist> checkBranchProfile(Context ctx) {
        return DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
            aBranchlistKey.BRANCHID.getKey() + " = ? AND " + aBranchlistKey.DEVICEID.getKey() + " = ? ",
            new String[] { SharedData.getInstance(ctx).getData(SharedKey.BRANCH_ID.getKey()), Helper.getImei(ctx, "") }
        );
    }

    public static String getBranchCode(Context ctx) {
        SharedData sp = SharedData.getInstance(ctx);
        String branchCode = "";
        if(!sp.getData(SharedKey.BRANCH_CODE.getKey()).trim().equals("")) {
            branchCode =  sp.getData(SharedKey.BRANCH_CODE.getKey());
        } else {
            branchCode =  "";
        }
        Log.d("dsxbc", "BRANCH CODE: " + branchCode);
        return  branchCode;
    }

    public static String getDateLongInteger() {
        Long dtLong = new Date().getTime();
        return String.valueOf(dtLong);
    }

    public static String getProjectPath(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
            return ctx.getExternalFilesDir(null).toString() + File.separator;
        } else {
            return Environment.getExternalStorageDirectory().toString() + File.separator + "Android"
                + File.separator + "data" + File.separator + ctx.getPackageName() + File.separator;
        }
    }

    public static String getFilePath(Context ctx) {
        return getProjectPath(ctx)  + "gcf" + File.separator;
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

}
