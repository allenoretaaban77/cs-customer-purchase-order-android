package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.adapters.ItemlistAdapterRv;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.aItemlist;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hari.bounceview.BounceView;

public class SearchItemFragment extends DialogFragment implements VolleyCallback {

    public Context ctx;
    private View rv;
    private Button btn_search;
    private Button btn_close;
    private Button btn_add_to_list;
    private EditText et_item_name;
    private AlertDialog alertDialog;
    private ProgressDialog loader;
    private RecyclerView rvItems;
    private Boolean flagItemClicked = false;
    private ItemlistAdapterRv adapter;
    private TextView tv_no_data;

    public static SearchItemFragment searchInstance(){
        SearchItemFragment dialogFragment = new SearchItemFragment();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rv = inflater.inflate(R.layout.dialog_search_item, container, false);
        ctx = rv.getContext();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

        try {
            initViews(rv);
        } finally {
            initListeners(rv);
        }

        return rv;
    }

    private void initViews(View v) {
        btn_search = (Button) v.findViewById(R.id.btn_search);
        btn_close = (Button) v.findViewById(R.id.btn_close);
        btn_add_to_list = (Button) v.findViewById(R.id.btn_add_to_list);
        et_item_name = (EditText) v.findViewById(R.id.et_item_name);
        tv_no_data = (TextView) v.findViewById(R.id.tv_no_data);
        rvItems = (RecyclerView) v.findViewById(R.id.itemrecyclerview);
    }

    private Boolean flagTaskRun = false; private Handler m_handler; private Runnable m_runnable;

    private void initListeners(final View v) {
        et_item_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 0) {
                        btn_search.callOnClick();
                        return true;
                    }
                }
                return false;
            }
        });

        if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1) {
            et_item_name.setImeOptions(EditorInfo.IME_ACTION_DONE);
            et_item_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(final Editable s) {
                    tv_no_data.setVisibility(View.VISIBLE); tv_no_data.setText("Searching...");
                    rvItems.setVisibility(View.GONE);

                    if (flagTaskRun) { m_handler.removeCallbacks(m_runnable); }
                    m_handler = new Handler();
                    m_runnable = new Runnable() {
                        @Override
                        public void run() {
                            String searchStr = s.toString().trim().equals("") ? "noitem" : s.toString() ;
                            Log.d("dsx", searchStr);
                            requestItemLocal(v, searchStr);
                            flagTaskRun = false;
                            tv_no_data.setText("No record found...");
                        }
                    };
                    flagTaskRun = m_handler.postDelayed(m_runnable, 700);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {

                if(!et_item_name.getText().toString().trim().equals("")) {
                    if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1) {
                        requestItemLocal(v, et_item_name.getText().toString().trim());
                    } else {
                        requestItem(v);
                    }
                }else{
                    if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1) {
                        requestItemLocal(v, "noitem");
                    } else {
                        alertDialog = Helper.okDialog(ctx, "Error","Please input item name.", "OK",
                            null, false);
                        BounceView.addAnimTo(alertDialog);
                    }

                }
            }
        });

        btn_add_to_list.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(adapter != null) {
                    ArrayList<Itemlist> requestedItems = new ArrayList<Itemlist>(adapter.original_items);
                    ArrayList<Itemlist> arrayListchecked = new ArrayList<>();
                    if (requestedItems.size() > 0) {
                        int j = 0;
                        arrayListchecked.clear();
                        while (requestedItems.size() > j) {
                            Itemlist c = requestedItems.get(j);
                            if (c.getIsChecked()) {
                                if (arrayListchecked.size() > 0) {
                                    Boolean found = false;
                                    for (int x = 0; x < arrayListchecked.size(); x++) {
                                        if (arrayListchecked.get(x).getRecid().equals(c.getRecid()))
                                            found = true;
                                    }
                                    if (!found)
                                        arrayListchecked.add(c);
                                } else
                                    arrayListchecked.add(c);
                            }
                            j++;
                        }

                        if (getActivity() != null) {
                            Intent i = getActivity().getIntent();
                            i.putExtra(SharedKey.SEARCHED_ITEMS.getKey(), arrayListchecked);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                            dismiss();
                        }
                    }
                }else{
//                    Toast.makeText(ctx, "Please select items...", Toast.LENGTH_SHORT).show();
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please select an item/s", "OK",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
                }
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                dismiss();
            }
        });
    }

    private void requestItemLocal(View v, String searchStr) {
        try {
            LinkedList<aItemlist> ailRs =  DcAitemlist.getInstance(ctx).getFilteraItemlist(searchStr);
            List<Itemlist> iRs = new ArrayList<Itemlist>();
            if(ailRs.size() > 0) {
                for (int i = 0; i < ailRs.size(); i++) {
                    aItemlist ail = ailRs.get(i);
                    Itemlist irsx = new Itemlist();
                    irsx.setRecid(ail.getRecid());
                    irsx.setItemName(ail.getItemname());
                    irsx.setDept(ail.getDept());
                    irsx.setUnitName(ail.getUnit());
                    irsx.setIsChecked(false);
                    irsx.setOldSku(ail.getOld_sku());
                    irsx.setSellingPrice(String.valueOf(ail.getSelling_price()));
                    iRs.add(irsx);
                }
                if(iRs.size() == 0) {
                    tv_no_data.setVisibility(View.VISIBLE);
                    rvItems.setVisibility(View.GONE);
                }else{
                    tv_no_data.setVisibility(View.GONE);
                    rvItems.setVisibility(View.VISIBLE);
                }
            }else{
                tv_no_data.setVisibility(View.VISIBLE);
                rvItems.setVisibility(View.GONE);
            }

            adapter = new ItemlistAdapterRv(ctx, iRs);
            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(ctx));
            rvItems.setHasFixedSize(true);
        } catch (Exception e) {
            tv_no_data.setVisibility(View.VISIBLE);
            rvItems.setVisibility(View.GONE);
            Log.d("dsx", "search error " + e.getMessage());
            Toast.makeText(ctx, "Search items error.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void requestItem(View v) {
        tv_no_data.setVisibility(View.VISIBLE); tv_no_data.setText("Searching...");
        rvItems.setVisibility(View.GONE);

        final SharedData sp = SharedData.getInstance(ctx);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_item_name.getWindowToken(), 0);

//        showSpinnerDialog(v);
        HashMap<String, String> params = new HashMap<>();
        String searchStr = et_item_name.getText().toString().trim();
        params.put("itemname", searchStr);
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
//        params.put("customerid", sp.getData(SharedKey.CURRENT_CUSTOMER_ID.getKey()));
        params.put("customerid", "-1");
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(this);
        strParams = strParams.replaceAll(" ", "%20");
        vi.getItemlist(ctx, params, strParams);
    }

    public void onRequestSuccess(String response, String type) {
        tv_no_data.setText("No record found...");

        final SharedData sp = SharedData.getInstance(ctx);
        try {
            response = response.replace("\r\n ", "");
            JSONArray objArr = new JSONArray(response);
            List<Itemlist> iRs = new ArrayList<Itemlist>();
            if(objArr.length() > 0) {
                for (int i = 0; i < objArr.length(); i++) {
                    JSONObject rowObj = objArr.getJSONObject(i);
                    Itemlist irsx = new Itemlist();
                    irsx.setRecid(rowObj.getInt(ItemlistKey.RECID.getKey()));
                    irsx.setItemName(rowObj.getString(ItemlistKey.ITEM_NAME_WITH_UNIT.getKey()));
                    irsx.setDept(rowObj.getString(ItemlistKey.DEPT.getKey()));
                    irsx.setUnitName(rowObj.getString(ItemlistKey.UNIT.getKey()));
                    irsx.setIsChecked(false);
                    irsx.setOldSku(rowObj.getString(ItemlistKey.OLD_SKU.getKey()));
                    irsx.setSellingPrice(rowObj.getString(ItemlistKey.SELLING_PRICE.getKey()));
                    iRs.add(irsx);
                }
                if(iRs.size() == 0) {
                    tv_no_data.setVisibility(View.VISIBLE);
                    rvItems.setVisibility(View.GONE);
                }else{
                    tv_no_data.setVisibility(View.GONE);
                    rvItems.setVisibility(View.VISIBLE);
                }
            }else{
                tv_no_data.setVisibility(View.VISIBLE);
                rvItems.setVisibility(View.GONE);
            }

            adapter = new ItemlistAdapterRv(ctx, iRs);
            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(ctx));
            rvItems.setHasFixedSize(true);
        } catch (JSONException e) {
            tv_no_data.setVisibility(View.VISIBLE);
            rvItems.setVisibility(View.GONE);
            Toast.makeText(ctx, "Something went wrong, please refresh the list.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        tv_no_data.setVisibility(View.VISIBLE);
        rvItems.setVisibility(View.GONE);
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
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
}
