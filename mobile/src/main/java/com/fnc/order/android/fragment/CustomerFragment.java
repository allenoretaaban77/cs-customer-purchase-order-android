package com.fnc.order.android.fragment;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.adapters.MenuStoresAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.model.MainViewModel;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomerFragment extends Fragment implements VolleyCallback{

    public Context ctx;
    public View v;
    private MainViewModel mViewModel;
    private MenuStoresAdapter adapter;
    private ProgressDialog loader;
    private VolleyInteractor vi;
    private ListView listview;
    private List<String> customersList = new ArrayList<>();
    private MaterialRippleLayout imgSearch;
    private EditText etCustomerName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu, container, false);
        ctx = v.getContext();
        initViews(v);
        initListeners(v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initViews(View v) {
        imgSearch = (MaterialRippleLayout) v.findViewById(R.id.layout_search);
        etCustomerName = (EditText) v.findViewById(R.id.et_customername);
        listview = (ListView) v.findViewById(R.id.storelistview);

        TextView tvVersion = (TextView) v.findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, getActivity()));
    }

    private void initListeners(View v) {
        etCustomerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(!etCustomerName.getText().toString().trim().equals("")) {
                        Helper.hideSoftKeyboard(getActivity());
                        fillData(v);
                    }else{
                        Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCustomerName.getText().toString().trim().equals("")) {
                    Helper.hideSoftKeyboard(getActivity());
                    fillData(v);
                }else{
                    Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillData(View v) {
        showSpinnerDialog(v);
        vi = new VolleyInteractor();
        vi.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", ServerConstants.CN);
        params.put("customer", etCustomerName.getText().toString().trim());

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        vi.getCustomers(v.getContext(), params, strParams);
    }

    private void showSpinnerDialog(View v){
        loader = Helper.buildSpinnerDialog(v.getContext());
        loader.show();
    }

    private void dismissSpinnerDialog() {
        if(loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }

    public void onRequestSuccess(String response, String type) {
        LinkedList<MenuList> mlRS = new LinkedList<MenuList>();
        try {
            JSONArray objArr = new JSONArray(response);
            if(objArr.length() > 0) {
                for (int i = 0; i < objArr.length(); i++) {
                    try {
                        JSONObject obj = objArr.getJSONObject(i);
                        MenuList mlList = new MenuList();
                        mlList.setCustomerID(obj.getString(MenulistKey.CUSTOMER_ID.getKey()));
                        mlList.setCustomerIntegrationId(obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()));
                        mlList.setCustomerName(obj.getString(MenulistKey.CUSTOMER_NAME.getKey()));
                        mlList.setRecordCount(0);
                        mlList.setRemarks("");
                        mlRS.add(mlList);
                    } catch (JSONException e) {
                        dismissSpinnerDialog();
                        e.printStackTrace();
                    }
                }
            }else{
                MenuList mlList = new MenuList();
                mlList.setCustomerID("0000000");
                mlList.setCustomerIntegrationId("0000000");
                mlList.setCustomerName("No Record Found");
                mlList.setRecordCount(0);
                mlList.setRemarks("");
                mlRS.add(mlList);
            }
        } catch (JSONException e) {
            dismissSpinnerDialog();
            Toast.makeText(ctx, "Something went wrong, please refresh the list.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            dismissSpinnerDialog();
            adapter = new MenuStoresAdapter(ctx, mlRS);
            listview.setAdapter(adapter);
            adapter.setOnButtonClickListener(new MenuStoresAdapter.OnButtonClickListener() {
                @Override
                public void onItemClick(View view,  int aid) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new OrderFragment()).addToBackStack("order").commit();
                }
            });
        }
    }

    public void onRequestFail(VolleyError volleyError, String type){
        dismissSpinnerDialog();
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
    }
}
