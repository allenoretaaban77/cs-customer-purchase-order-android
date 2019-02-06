package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.fnc.order.android.R;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.utilities.Helper;
import com.android.volley.error.VolleyError;

public class TransactionFragment extends Fragment implements VolleyCallback {

    public Context ctx;
    private View refV;
    private ProgressDialog loader;
    private ImageButton btn_back;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        refV = inflater.inflate(R.layout.fragment_transaction, container, false);
        ctx = refV.getContext();

        refV.setFocusableInTouchMode(true);
        refV.requestFocus();
        refV.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    backItNow(v);
                }
                return false;
            }
        } );

        try {
            initViews(refV);
        } finally {
            initListeners(refV);
        }

        return refV;
    }

    private void initViews(View v) {
        btn_back = (ImageButton) refV.findViewById(R.id.btn_back);
    }

    private void initListeners(View v) {
        btn_back.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                backItNow(v);
            }
        });
    }

    public void onRequestSuccess(String response, String type) { }

    public void onRequestFail(VolleyError volleyError, String type) { }

    private void backItNow(final View v) {
        dismissSpinnerDialog();
        hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    private void showSpinnerDialog(View v) {
        loader = Helper.buildSpinnerDialog(v.getContext());
        loader.show();
    }

    private void dismissSpinnerDialog() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}