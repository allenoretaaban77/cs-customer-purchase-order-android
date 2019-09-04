package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.adapters.TransactionsAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.Helper;
import java.util.LinkedList;

public class TransactionFragment extends Fragment implements VolleyCallback {

    public Context ctx;
    private View refV;
    private ProgressDialog loader;
    private ImageButton btn_back;
    private ListView list_view;
    private TransactionsAdapter adapter;
    private Fragment thisFragment;
    private static final int ITEM_DIALOG_FRAGMENT = 8;

    public TransactionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        refV = inflater.inflate(R.layout.fragment_transaction, container, false);
        ctx = refV.getContext();
        thisFragment = this;

//        refV.setFocusableInTouchMode(true);
//        refV.requestFocus();
//        refV.setOnKeyListener( new View.OnKeyListener() {
//            @Override
//            public boolean onKey( View v, int keyCode, KeyEvent event ) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    backItNow(v);
//                    return true;
//                }
//                return false;
//            }
//        } );

        try {
            initViews(refV);
        } finally {
            initListeners(refV);
        }

        return refV;
    }

    private void initViews(View v) {
        btn_back = (ImageButton) refV.findViewById(R.id.btn_back);
        list_view = (ListView) refV.findViewById(R.id.list_view);

        final LinkedList<Ordered> odll = DcOrdered.getInstance(ctx).getOrderedlist();
//        ArrayList<Ordered> arrayList = new ArrayList<Ordered>();
        if(odll.size() > 0) {
            adapter = new TransactionsAdapter(ctx, odll);
            adapter.setOnItemClickListener(new TransactionsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view,  int pos) {
                    Ordered rso = odll.get(pos);
//                    try {
//                        String strRs = new JSONObject(rso.getJsonComplete()).getString("details");
//                        String strArr = new JSONArray(strRs).get(0).toString();
                        DialogFragment dialogFrag = TrasactionItemsFragment.searchInstance();
                        Bundle args = new Bundle();
                        args.putString("details", rso.getJsonComplete());
                        dialogFrag.setArguments(args);
                        dialogFrag.setTargetFragment(thisFragment, ITEM_DIALOG_FRAGMENT);
                        dialogFrag.setCancelable(false);
                        dialogFrag.show(getActivity().getSupportFragmentManager(), "dialog_search_item");
//                    } catch (JSONException e) {
//                        Toast.makeText(ctx, "Error on process.", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
                }
            });
            list_view.setAdapter(adapter);
        }
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