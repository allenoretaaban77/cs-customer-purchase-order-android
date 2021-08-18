package com.fnc.order.android.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordVisibility {

    public static boolean shownNew = false;
    public static boolean shownConfirm = false;

    public static void visibility(final Button hidePassword, final Button showPassword, final EditText etPassword){
        Log.d("test1", "error");
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePassword.setVisibility(View.VISIBLE);
                showPassword.setVisibility(View.GONE);
                etPassword.setTransformationMethod(null);
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        hidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePassword.setVisibility(View.GONE);
                showPassword.setVisibility(View.VISIBLE);
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                etPassword.setSelection(etPassword.getText().length());
            }
        });
    }
    public static void visibilityInValidationNew(final EditText etPassword, final Button showPassword, final Button hidePassword){
        Log.d("test2", "error");
        if(etPassword.getText().length() == 0) {
            showPassword.setVisibility(View.GONE);
            hidePassword.setVisibility(View.GONE);
        }
        shownNew = true;
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(showPassword.getVisibility() == View.VISIBLE && hidePassword.getVisibility() == View.GONE ){
                    showPassword.setVisibility(View.VISIBLE);
                }
                if(showPassword.getVisibility() == View.GONE && hidePassword.getVisibility() == View.VISIBLE){
                    showPassword.setVisibility(View.GONE);
                    hidePassword.setVisibility(View.VISIBLE);
                }
                if(showPassword.getVisibility() == View.GONE && hidePassword.getVisibility() == View.GONE && shownNew == true){
                    shownNew = false;
                    showPassword.setVisibility(View.VISIBLE);
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etPassword.setSelection(etPassword.getText().length());
                }
                showPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPassword.setVisibility(View.GONE);
                        hidePassword.setVisibility(View.VISIBLE);
                        etPassword.setTransformationMethod(null);
                        etPassword.setSelection(etPassword.getText().length());
                        showPassword.setVisibility(View.GONE);
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void visibilityInValidationConfirm(final EditText etPassword, final Button showPassword, final Button hidePassword){
        if(etPassword.getText().length() == 0) {
            showPassword.setVisibility(View.GONE);
            hidePassword.setVisibility(View.GONE);
        }
        shownConfirm = true;
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if((showPassword.getVisibility() == View.GONE && hidePassword.getVisibility() == View.GONE) && shownConfirm == true){
                    Log.d("condition1", "true");
                    shownConfirm = false;
                    showPassword.setVisibility(View.VISIBLE);
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etPassword.setSelection(etPassword.getText().length());
                }
                if(showPassword.getVisibility() == View.VISIBLE && hidePassword.getVisibility() == View.GONE){
                    Log.d("condition2", "true");
                    showPassword.setVisibility(View.VISIBLE);
                }
                if(showPassword.getVisibility() == View.GONE && hidePassword.getVisibility() == View.VISIBLE){
                    Log.d("condition3", "true");
                    showPassword.setVisibility(View.GONE);
                    hidePassword.setVisibility(View.VISIBLE);
                }
                showPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPassword.setVisibility(View.GONE);
                        hidePassword.setVisibility(View.VISIBLE);
                        etPassword.setTransformationMethod(null);
                        etPassword.setSelection(etPassword.getText().length());
                        showPassword.setVisibility(View.GONE);
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
