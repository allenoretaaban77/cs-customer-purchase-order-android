package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.LoginActivity;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.adapters.AlphaGridAdapter;
import com.fnc.order.android.adapters.MenuStoresAdapter;
import com.fnc.order.android.adapters.PersonAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.PersonsKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.MainViewModel;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.Person;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hari.bounceview.BounceView;

public class CustomerFragment extends Fragment implements VolleyCallback{

    public Context ctx;
    public View v;
    private MainViewModel mViewModel;
    private MenuStoresAdapter adapter;
    private ProgressDialog loader;
    private VolleyInteractor vi;
    private ListView listview;
    private List<String> customersList = new ArrayList<>();
    private MaterialRippleLayout imgSearch, alphaSort, refreshAll;
    private EditText etCustomerName;
    private GridView alphagridview;
    private ArrayList<String> stringAlpha = new ArrayList<String>(Arrays.asList("A","B",
            "C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U",
            "V","W","X","Y","Z"));
    private AlphaGridAdapter adapterAlpha;
    private LinearLayout alphagridview_box, storelistview_box;
    private LinearLayout ll_version_box;
    private AlertDialog alertDialog;
    private DroppyMenuPopup.Builder sortMenu;
    private DroppyMenuPopup sortMenuObj;
    private String updateCustomerMessage = "Please wait while updating customer lists...";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu, container, false);
        ctx = v.getContext();
        initViews(v);
        initListeners(v);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (isUpdateCustomer) {
                        Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog = Helper.okCancelDialog(ctx,
                                "Closing Application", "Are you sure you want to close this app?",
                                "Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finishAndRemoveTask();
                                    }
                                }, "Cancel", null, false);
                        BounceView.addAnimTo(alertDialog);
                    }
                    return true;
                }
                return false;
            }
        } );

        if (!SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey())
                .equals(SharedData.getInstance(ctx).getData(SharedKey.DATABASE_OLD.getKey()))) {
            SharedData.getInstance(ctx).saveData(SharedKey.DATABASE_OLD.getKey(), SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            requestCustomers("");
            /*alphagridview = (GridView) v.findViewById(R.id.alphagridview);
            ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
            if (refStringAlpha.size() > 0) { stringAlpha = refStringAlpha; }
            adapterAlpha = new AlphaGridAdapter(ctx, stringAlpha);
            adapterAlpha.setOnButtonClickListener(new AlphaGridAdapter.OnBoxClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Helper.hideSoftKeyboard(getActivity());
                    fillData(v, stringAlpha.get(pos), true);
                }
            });
            alphagridview.setAdapter(adapterAlpha);*/
        } else {
            LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "");
            if (llr.size() < 1) {
                requestCustomers("");
            }
        }

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
        alphaSort = (MaterialRippleLayout) v.findViewById(R.id.layout_alpha);
        refreshAll = (MaterialRippleLayout) v.findViewById(R.id.layout_refresh);
        etCustomerName = (EditText) v.findViewById(R.id.et_customername);
        listview = (ListView) v.findViewById(R.id.storelistview);

        TextView tvVersion = (TextView) v.findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, getActivity()));

        alphagridview = (GridView) v.findViewById(R.id.alphagridview);
        if (SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey())
                .equals(SharedData.getInstance(ctx).getData(SharedKey.DATABASE_OLD.getKey()))) {
            ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
            if (refStringAlpha.size() > 0) {
                stringAlpha = refStringAlpha;
            } else {
                stringAlpha = new ArrayList<String>();
            }
        } else {
            stringAlpha = new ArrayList<String>();
        }
        adapterAlpha = new AlphaGridAdapter(ctx, stringAlpha);
        alphagridview.setAdapter(adapterAlpha);

        alphagridview_box = (LinearLayout) v.findViewById(R.id.alphagridview_box);
        storelistview_box = (LinearLayout) v.findViewById(R.id.storelistview_box);

        ll_version_box = (LinearLayout) v.findViewById(R.id.ll_version_box);
        sortMenu = new DroppyMenuPopup.Builder(ctx, ll_version_box);
        sortMenu.setXOffset(85);
        sortMenu.addMenuItem(new DroppyMenuItem("  View Transactions "))
                .addSeparator()
                .addMenuItem(new DroppyMenuItem("  Update Users "))
                .addMenuItem(new DroppyMenuItem("  Log-out "));
    }

    private void initListeners(View v) {
        etCustomerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return false; }

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(!etCustomerName.getText().toString().trim().equals("")) {
                        Helper.hideSoftKeyboard(getActivity());
                        fillData(v, etCustomerName.getText().toString().trim(), false);
                    }else{
//                        Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
                        alertDialog = Helper.okDialog(ctx,
                                "Error","Please input item name.", "CLOSE",
                                null, false);
                        BounceView.addAnimTo(alertDialog);
                    }
                }
                return false;
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }

                if(!etCustomerName.getText().toString().trim().equals("")) {
                    Helper.hideSoftKeyboard(getActivity());
                    fillData(v, etCustomerName.getText().toString().trim(), false);
                }else{
//                    Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
                    alertDialog = Helper.okDialog(ctx,
                            "Error","Please input item name.", "CLOSE",
                            null, false);
                    BounceView.addAnimTo(alertDialog);
                }
            }
        });
        alphaSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
                storelistview_box.setVisibility(View.GONE);
                alphagridview_box.setVisibility(View.VISIBLE);
                alphagridview_box.setAlpha(0.0f);
                alphagridview_box.animate().translationY(0)
                        .alpha(1.0f).setListener(null);
            }
        });
        refreshAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
//                showSpinnerDialog(v);
                storelistview_box.setVisibility(View.GONE);
                alphagridview_box.setVisibility(View.VISIBLE);
                requestCustomers("");
            }
        });
        adapterAlpha.setOnButtonClickListener(new AlphaGridAdapter.OnBoxClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
                Helper.hideSoftKeyboard(getActivity());
                fillData(v, stringAlpha.get(pos), true);
            }
        });

        sortMenu.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }

                switch(id){
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new TransactionFragment(), "transaction_fragment")
                                .addToBackStack(null)
                                .commit();
                        break;
                    case 1:
                        alertDialog = Helper.okCancelDialog(ctx,
                                "Update Users", "Are you sure you want to update user records?",
                                "Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        getActivity().finish();
                                        Toast.makeText(ctx, "Module error, please contact developer", Toast.LENGTH_LONG).show();
                                    }
                                }, "Cancel", null, false);
                        break;
                    case 2:
                        alertDialog = Helper.okCancelDialog(ctx,
                                "Log Out", "Are you sure you want to log-out?",
                                "Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finishAndRemoveTask();
                                        startActivity(new Intent(ctx, LoginActivity.class));
                                    }
                                }, "Cancel", null, false);
                        BounceView.addAnimTo(alertDialog);
                        break;
                }
            }
        });
        sortMenu.build();

    }

    private void requestCustomers(String stringSearch) {
        showSpinnerDialog();
        Toast.makeText(ctx, updateCustomerMessage, Toast.LENGTH_SHORT).show();

        VolleyInteractor vic = new VolleyInteractor();
        vic.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
        params.put("customer", stringSearch);

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        vic.getCustomers(ctx, params, strParams);
    }

    private void fillData(View v, String stringSearch, Boolean isAlpha) {
        alphagridview_box.setVisibility(View.GONE);
        storelistview_box.setVisibility(View.VISIBLE);
        storelistview_box.setAlpha(0.0f);
        storelistview_box.animate().translationY(0)
                .alpha(1.0f).setListener(null);

        showSpinnerDialog();
//        vi = new VolleyInteractor();
//        vi.registerCallback(this);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("cn", ServerConstants.CN);
//        params.put("customer", stringSearch);
//
//        Iterator it = params.entrySet().iterator();
//        String strParams = "";
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
//            it.remove();
//        }
//        strParams = strParams.replaceAll(" ", "%20");
//        vi.getCustomers(v.getContext(), params, strParams);

        final LinkedList<MenuList> mlRSx = DcMenulist.getInstance(ctx).getAllMenulist(isAlpha, stringSearch);
        if (mlRSx.size() == 0) {
            MenuList mlList = new MenuList();
            mlList.setCustomerID("0000000");
            mlList.setCustomerIntegrationId("0000000");
            mlList.setCustomerName("No Record Found");
            mlList.setRecordCount(0);
            mlList.setRemarks("");
            mlList.setAlphachar("");
            mlRSx.add(mlList);
        }
        adapter = new MenuStoresAdapter(ctx, mlRSx);
        dismissSpinnerDialog();
        listview.setAdapter(adapter);
        if (mlRSx.size() > 0) {
            adapter.setOnButtonClickListener(new MenuStoresAdapter.OnButtonClickListener() {
                @Override
                public void onItemClick(View view,  int aid) {
                    MenuList mlRS = mlRSx.get(aid);
                    SharedData sp = SharedData.getInstance(ctx);
                    sp.saveData(SharedKey.CURRENT_REMARKS.getKey(), mlRS.getRemarks());
                    sp.saveData(SharedKey.CURRENT_STORE.getKey(), mlRS.getCustomerName());
                    sp.saveData(SharedKey.CURRENT_CUSTOMER_ID.getKey(), mlRS.getCustomerID());
                    sp.saveData(SharedKey.CURRENT_CUSTOMER_INTEGRATION_ID.getKey(), mlRS.getCustomerIntegrationId());
                    getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new OrderFragment(), "order_fragment")
                        .addToBackStack(null)
                        .commit();
                }
            });
        }
    }

    private void showSpinnerDialog(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader = Helper.buildSpinnerDialog(ctx);
                loader.show();
            }
        });
    }

    private void dismissSpinnerDialog() {
        if(loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }


    private ProgressBar progressBarx;
    private Boolean isUpdateCustomer = false;
    private class customerAT extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            isUpdateCustomer = true;
            try {
                String response = params[0].replace("\r\n", "");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    DcMenulist.getInstance(ctx).emptyMenulist();
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject obj = objArr.getJSONObject(i);
                        MenuList mlList = new MenuList();
                        mlList.setCustomerID(obj.getString(MenulistKey.CUSTOMER_ID.getKey()));
                        mlList.setCustomerIntegrationId(obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()));
                        String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                        mlList.setCustomerName(strCustomerName);
                        mlList.setRecordCount(0);
                        mlList.setRemarks("");
                        if (!strCustomerName.equals("")) {
                            if (String.valueOf(strCustomerName.charAt(0)).equals("0")) {
                                mlList.setAlphachar(String.valueOf(strCustomerName.charAt(5)).toUpperCase());
                            } else {
                                mlList.setAlphachar(String.valueOf(strCustomerName.charAt(0)).toUpperCase());
                            }
                            DcMenulist.getInstance(ctx).insertMenulist(mlList);
                        }
                        publishProgress(i+1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                isUpdateCustomer = false;
                return "Task Error";
            }
            /*for (; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d("dsxcf", "searchcustomer saved");

            progressBarx.setVisibility(View.GONE);

            alphagridview = (GridView) v.findViewById(R.id.alphagridview);
            ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
            if (refStringAlpha.size() > 0) { stringAlpha = refStringAlpha; }
            adapterAlpha = new AlphaGridAdapter(ctx, stringAlpha);
            adapterAlpha.setOnButtonClickListener(new AlphaGridAdapter.OnBoxClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
                    Helper.hideSoftKeyboard(getActivity());
                    fillData(v, stringAlpha.get(pos), true);
                }
            });

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            dismissSpinnerDialog();
                            storelistview_box.setVisibility(View.GONE);
                            alphagridview_box.setVisibility(View.VISIBLE);
                            alphagridview_box.setAlpha(0.0f);
                            alphagridview_box.animate().translationY(0)
                                .alpha(1.0f).setListener(null);
                            alphagridview.setAdapter(adapterAlpha);

                            isUpdateCustomer = false;

                            Toast.makeText(ctx, "Records updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    500
            );

        }
        @Override
        protected void onPreExecute() {
            progressBarx.setVisibility(View.VISIBLE);
            Log.d("asynctask", "Task Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("asynctask", "Running " + + values[0]);
            progressBarx.setProgress(values[0]);
        }
    }

    public void onRequestSuccess(String response, String type) {
        LinkedList<MenuList> mlRS = new LinkedList<MenuList>();
        try {
            if (type.equals("searchcustomer")) {
                Log.d("dsxcf", "searchcustomer");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {

                    dismissSpinnerDialog();
                    progressBarx = (ProgressBar) v.findViewById(R.id.progressBar);
                    progressBarx.setMax(objArr.length());
                    new customerAT().execute(response);

                    /*
                    DcMenulist.getInstance(ctx).emptyMenulist();
                    for (int i = 0; i < objArr.length(); i++) {
                        try {
                            JSONObject obj = objArr.getJSONObject(i);
                            MenuList mlList = new MenuList();
                            mlList.setCustomerID(obj.getString(MenulistKey.CUSTOMER_ID.getKey()));
                            mlList.setCustomerIntegrationId(obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()));
                            String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                            mlList.setCustomerName(strCustomerName);
                            mlList.setRecordCount(0);
                            mlList.setRemarks("");
                            if (!strCustomerName.equals("")) {
                                if (String.valueOf(strCustomerName.charAt(0)).equals("0")) {
                                    mlList.setAlphachar(String.valueOf(strCustomerName.charAt(5)));
                                } else {
                                    mlList.setAlphachar(String.valueOf(strCustomerName.charAt(0)));
                                }
                                DcMenulist.getInstance(ctx).insertMenulist(mlList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    */
                    Log.d("dsxcf", "searchcustomer saved");

                    /* alphagridview = (GridView) v.findViewById(R.id.alphagridview);
                    ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
                    if (refStringAlpha.size() > 0) { stringAlpha = refStringAlpha; }
                    adapterAlpha = new AlphaGridAdapter(ctx, stringAlpha);
                    adapterAlpha.setOnButtonClickListener(new AlphaGridAdapter.OnBoxClickListener() {
                        @Override
                        public void onItemClick(View v, int pos) {
                            Helper.hideSoftKeyboard(getActivity());
                            fillData(v, stringAlpha.get(pos), true);
                        }
                    });
                    alphagridview.setAdapter(adapterAlpha);

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    dismissSpinnerDialog();
                                    storelistview_box.setVisibility(View.GONE);
                                    alphagridview_box.setVisibility(View.VISIBLE);
                                    alphagridview_box.setAlpha(0.0f);
                                    alphagridview_box.animate().translationY(0)
                                        .alpha(1.0f).setListener(null);
//                                    dismissSpinnerDialog();
//                                    showActivity(MainActivity.class);
//                                    Toast.makeText(ctx, "Welcome!", Toast.LENGTH_SHORT).show();
                                }
                            },
                            500
                    ); */
                }
            } else {
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
            }
        } catch (JSONException e) {
            dismissSpinnerDialog();
//            Toast.makeText(ctx, "Something went wrong, please refresh the list.", Toast.LENGTH_SHORT).show();
            alertDialog = Helper.okDialog(ctx,
                    "Error","Something went wrong, please refresh the list.", "CLOSE",
                    null, false);
            BounceView.addAnimTo(alertDialog);
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
