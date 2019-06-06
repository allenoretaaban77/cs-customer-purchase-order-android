package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.adapters.ItemlistAdapterRv;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

public class SearchItemFragment extends DialogFragment implements VolleyCallback {

    public Context ctx;
    private View rootView;
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
        rootView = inflater.inflate(R.layout.dialog_search_item, container, false);
        ctx = rootView.getContext();

        try {
            initViews(rootView);
        } finally {
            initListeners(rootView);
        }

        return rootView;
    }

    private void initViews(View v) {
        btn_search = (Button) v.findViewById(R.id.btn_search);
        btn_close = (Button) v.findViewById(R.id.btn_close);
        btn_add_to_list = (Button) v.findViewById(R.id.btn_add_to_list);
        et_item_name = (EditText) v.findViewById(R.id.et_item_name);
        tv_no_data = (TextView) v.findViewById(R.id.tv_no_data);
        rvItems = (RecyclerView) v.findViewById(R.id.itemrecyclerview);
    }

    private void initListeners(View v) {
        et_item_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btn_search.callOnClick();
                }
                return false;
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(!et_item_name.getText().toString().trim().equals("")) {
                    requestItem(v);
                }else{
//                    Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please input item name.", "OK",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
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

    private void requestItem(View v) {
        final SharedData sp = SharedData.getInstance(ctx);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_item_name.getWindowToken(), 0);

        showSpinnerDialog(v);
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
//                    if(!rowObj.getString(ItemlistKey.OLD_SKU.getKey()).trim().equals("null") &&
//                            !rowObj.getString(ItemlistKey.OLD_SKU.getKey()).trim().equals("")) {
//                        iRs.add(irsx);
//                    }
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
            dismissSpinnerDialog();
            Toast.makeText(ctx, "Something went wrong, please refresh the list.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            dismissSpinnerDialog();
                        }
                    },
                    1000
            );
        }
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        dismissSpinnerDialog();
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
