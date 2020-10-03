package com.fnc.order.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.fnc.order.android.R;
import com.fnc.order.android.adapters.TransactionItemsAdapter;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.utilities.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrasactionItemsFragment extends DialogFragment {

    public Context ctx;
    private View rv;
    private Button btn_close;
    private ListView list_view;
    private TransactionItemsAdapter adapter;
    private TextView tv_title;

    public static TrasactionItemsFragment searchInstance(){
        TrasactionItemsFragment dialogFragment = new TrasactionItemsFragment();
        return dialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rv = inflater.inflate(R.layout.dialog_trasactionitems, container, false);
        ctx = rv.getContext();

        rv.setFocusableInTouchMode(true);
        rv.requestFocus();
        rv.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dismiss();
                    return true;
                }
                return false;
            }
        } );

        initViews(rv);
        initListeners(rv);

        return rv;
    }

    private void initViews(View v) {
        btn_close = (Button) v.findViewById(R.id.btn_close);
        tv_title = (TextView) v.findViewById(R.id.tv_title);

        list_view = (ListView) v.findViewById(R.id.list_view);
        String strArr = getArguments().getString("details");
//        String strArrH = getArguments().getString("header");

        try {
            JSONArray objArr = new JSONArray(strArr);
//            JSONArray objArrH = new JSONArray(strArrH);

            // display count of itemsx
            ((TextView) v.findViewById(R.id.tv_title)).setText("TRANSACTION ITEMS (" + String.valueOf(objArr.length()) + ")");
//            Toast.makeText(ctx, objArrH.getString("remarks"), Toast.LENGTH_LONG).show();

            if(objArr.length() > 0) {
                ArrayList<Order> arrLst = new ArrayList<>();
                for (int i = 0; i < objArr.length(); i++) {
                    JSONObject obj = objArr.getJSONObject(i);
                    Order ol = new Order();

                    ol.setQuantity(obj.getString("quantity"));
                    ol.setFree(obj.getString("free"));
                    ol.setOldSku(obj.getString("old_sku"));
                    ol.setRemarks(obj.getString("remarks"));
                    ol.setTotal(obj.getString("total"));
                    ol.setSellingPrice(obj.getString("selling_price"));
                    ol.setItemRecid(obj.getString("item_recid"));
                    ol.setQuantity(obj.getString("quantity"));
                    ol.setFree(obj.getString("free"));
                    ol.setUnitName(obj.getString("unitName"));
                    ol.setItemName(obj.getString("itemname"));
                    ol.setIsChecked(0);
                    ol.setIsError(0);
                    ol.setIsLocked(0);
                    arrLst.add(ol);
                }

                adapter = new TransactionItemsAdapter(ctx, arrLst);
                adapter.setCurPos(0);
                list_view.setAdapter(adapter);
            }

        } catch(JSONException e) {
            Toast.makeText(ctx, "Error on process.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        LinearLayout ll_content_box_main = (LinearLayout) v.findViewById(R.id.ll_content_box_main);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_content_box_main.getLayoutParams();
        int iMrgnTP =  Helper.getScrRatio(ctx) < 0.6 ? 10 : 200 ;
        int iMrgnLR =  Helper.getScrRatio(ctx) < 0.6 ? 10 : 100 ;
        params.setMargins(iMrgnLR, iMrgnTP, iMrgnLR, iMrgnTP);
        ll_content_box_main.setLayoutParams(params);
    }

    private void initListeners(View v) {
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
