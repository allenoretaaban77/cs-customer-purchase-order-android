package com.fnc.order.android.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.fnc.order.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.callback.Callback;

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
        ProgressDialog progressDialog = new ProgressDialog(context,R.style.ProgressSpinnerTheme);
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
        btnOK.setOnClickListener(cancelClickListener);

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
}
