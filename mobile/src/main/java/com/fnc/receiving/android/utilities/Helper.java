package com.fnc.receiving.android.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.enumeration.SharedKey;
import com.fnc.receiving.android.model.aStaffs;
import com.mikhaellopez.rxanimation.RxAnimation;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Helper {

    public static String getProjectPath(Context ctx) {
        return DBHelper.DBPath + ctx.getPackageName() + File.separator;
    }

    public static String getFilePath(Context ctx) {
        return DBHelper.DBPath + ctx.getPackageName() + File.separator + "file" + File.separator;
    }


    public static String getPostingDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("M/dd/yyyy HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getDateLongInteger() {
        Long dtLong = new Date().getTime();
        return String.valueOf(dtLong);
    }

    public static long dateToMillis(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date mDate = dateFormat.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ProgressDialog showSpinnerDialog(Context context, String title, String message){
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressSpinnerTheme);
        if (!title.trim().equals("")) { progressDialog.setTitle(title); }
        if (!message.trim().equals("")) { progressDialog.setMessage(message); }
        progressDialog.setCancelable(false);
//        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        return progressDialog;
    }

    public static void dismissSpinnerDialog(ProgressDialog loader) {
        if(loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    /*public static ProgressDialog buildSpinnerDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressSpinnerTheme);
        if (!title.trim().equals("")) { progressDialog.setTitle(title); }
        if (!message.trim().equals("")) { progressDialog.setMessage(message); }
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        return progressDialog;
    }*/

//    public static ProgressBar buildProgressDialog(Activity activity, RelativeLayout layout) {
//        ProgressBar progressBar = new ProgressBar(activity,null, android.R.attr.actionBarStyle);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(progressBar,params);
//    }

    public static boolean isTextFieldBlank(EditText editText){
        String text = editText.getText().toString();
        if(text.matches("")){
            return  true;
        }else{
            return false;
        }
    }

    public static AlertDialog okCancelSpinnerDialogBuilder(final Context activity, String message,
                                                           ArrayList<String> sl,
                                                           String okButtonCaption, View.OnClickListener onClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.a_dialog_dropdown_ok, null);
        layout.setBackground(new ColorDrawable(Color.TRANSPARENT));

        Button btnOK = (Button) layout.findViewById(R.id.btn_ok);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(onClickListener);

        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Spinner spnrOption = (Spinner) layout.findViewById(R.id.spnr_option);
        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(activity, R.layout.a_item_spinner, sl) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) { return false; }
                else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                convertView.setBackgroundResource(R.color.transparent);
                parent.setBackgroundResource(R.color.green_1);
                View view = super.getDropDownView(position, convertView, parent);
//                parent.setBackgroundrColor(R.drawable.a_rounded_left_end);
//                view.setBackground(activity.getResources().getDrawable(R.drawable.a_rounded_all));
                TextView tv = (TextView) view;
//                tv.setBackground(activity.getResources().getDrawable(R.drawable.a_rounded_all));
//                tv.setBackgroundColor(activity.getResources().getColor());
                if(position == 0){
                    tv.setTextColor(activity.getResources().getColor(R.color.green_5));
                } else {
                    tv.setTextColor(activity.getResources().getColor(R.color.white_1));
                }
                return view;
            }
        };
        spinneradapter.setDropDownViewResource(R.layout.a_item_spinner);
        spnrOption.setAdapter(spinneradapter);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
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
                    R.style.ProgressSpinnerTheme);
        } else {
            builder = new android.app.AlertDialog.Builder(activity);
        }
        builder.setIcon(R.drawable.calendar_check_outline);
        builder.setCancelable(isCancelable);
        builder.setTitle("").setMessage(message)
                .setPositiveButton(okButtonCaption, okClickListener);
        builder.create();
        return builder.show();
    }


    public static AlertDialog okCancelDialog(final Context activity, String title, String message,
                                             String okButtonCaption, DialogInterface.OnClickListener okClickListener,
                                             String cancelButtonCaption, DialogInterface.OnClickListener cancelClickListener,
                                             Boolean isCancelable) {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(activity, R.style.ProgressSpinnerTheme);
        } else {
            builder = new android.app.AlertDialog.Builder(activity);
        }
        builder.setIcon(R.drawable.calendar_check_outline);
        builder.setCancelable(isCancelable);
        builder.setTitle("").setMessage(message)
                .setPositiveButton(okButtonCaption, okClickListener)
                .setNegativeButton(cancelButtonCaption, cancelClickListener);
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
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
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

    public static boolean checkOffline(Context ctx){
        if(isNetworkAvailable(ctx)){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(
                ctx.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static aStaffs defaultStaff(Context ctx) {
        SharedData sp = SharedData.getInstance(ctx);
        aStaffs cs = new aStaffs(-777, "2", "aban.allen@yahoo.com",
        "IT Support", Integer.parseInt(sp.getData(SharedKey.BRANCH_ID.getKey())),
                1900000000, "P@ssw0rd" + Helper.getNumericMonthDay(), "true");
        return cs;
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
}
