package com.fnc.order.android.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.LoginActivity;
import com.fnc.order.android.adapters.AlphaGridAdapter;
import com.fnc.order.android.adapters.MenuStoresAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.model.MainViewModel;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.aItemlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.liaoinstan.springview.widget.SpringView;
import com.roacult.backdrop.BackdropLayout;
import com.shehabic.droppy.DroppyMenuPopup;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
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
    private ProgressDialog loader, floader;
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
    private LinearLayout alphagridview_box, storelistview_box, includedFront, ll_version_box;
    private AlertDialog alertDialog, alertDialogUser, alertDialogJobTitle;
    private DroppyMenuPopup.Builder boxMenu;
    private DroppyMenuPopup sortMenuObj;
    private String updateCustomerMessage = "Please wait while updating customer lists...";
    private SharedData sp;
    private Fragment thisFragment;
    private static final int USER_DIALOG_FRAGMENT = 7;
    private SpringView springView;
    private PowerMenu pmMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu, container, false);
        ctx = v.getContext();
        thisFragment = this;
        Helper.setPreviousPage(ctx, "main_page");
        sp = SharedData.getInstance(ctx);

        initViews(v);
        initListeners(v);

//        DcMenulist.getInstance(ctx).emptyMenulist();

        String eiOld = SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID_OLD.getKey());
        String inNew = SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID.getKey());
        if (inNew.equals(eiOld)) {
            LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "%");
            if (llr.size() < 1) {
                requestCustomers("");
            } else {
                loadSavedItems();
            }
        } else {
            SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_ID_OLD.getKey(), inNew);
            requestCustomers("");
        }

        /*if (!SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey())
                .equals(SharedData.getInstance(ctx).getData(SharedKey.DATABASE_OLD.getKey()))) {
            SharedData.getInstance(ctx).saveData(SharedKey.DATABASE_OLD.getKey(), SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            requestCustomers("");
        } else {
            String eiOld = SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID_OLD.getKey());
            String inNew = SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID.getKey());
            if (inNew.equals(eiOld)) {
                LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "%");
                if (llr.size() < 1) {
                    requestCustomers("");
                } else {
                    loadSavedItems();
                }
            } else {
                SharedData.getInstance(ctx).saveData(SharedKey.REF_EMP_ID_OLD.getKey(), inNew);
                requestCustomers("");
            }
        }*/

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel

//        Snackbar snack = Snackbar.make(v, "tbtG!", Snackbar.LENGTH_INDEFINITE);
//        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
//                CoordinatorLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.TOP;
//        View view = snack.getView();
//        view.setLayoutParams(params);
//        snack.show();
    }

    private void openBackdrop(BackdropLayout bdl, LinearLayout layout) {
        bdl.open();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setLayoutParams( new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT ));
            }
        }, 300);
    }

    private void closeBackdrop(BackdropLayout bdl, LinearLayout layout) {
        bdl.close();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setLayoutParams( new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT  ));
            }
        }, 100);
    }

    private void loadSavedItems(){
        // load alpha grid view
        /*if (SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey())
                .equals(SharedData.getInstance(ctx).getData(SharedKey.DATABASE_OLD.getKey()))) {
            ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
            if (refStringAlpha.size() > 0) {
                stringAlpha = refStringAlpha;
            } else {
                stringAlpha = new ArrayList<String>();
            }
        } else {
            stringAlpha = new ArrayList<String>();
        } */

        ArrayList<String> refStringAlpha = DcMenulist.getInstance(ctx).getAllMenulistAlpha();
        if (refStringAlpha.size() > 0) {
            stringAlpha = refStringAlpha;
        } else {
            stringAlpha = new ArrayList<String>();
        }

        adapterAlpha = new AlphaGridAdapter(ctx, stringAlpha);
        adapterAlpha.setOnButtonClickListener(new AlphaGridAdapter.OnBoxClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
                Helper.hideSoftKeyboard(getActivity());
                showListBox();
                fillData(v, stringAlpha.get(pos), true);
                openBackdrop(containerbdl, includedFront);
            }
        });
        alphagridview.setAdapter(adapterAlpha);
        alphagridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("dsxof", String.valueOf(event.getX()) + " & " + String.valueOf(event.getY()));
                if (event.getX() > event.getY()) {
                    closeBackdrop(containerbdl, includedFront);
                }
                return false;
            }
        });

        // load list view
        fillData(v, "%", false);

        LinkedList<MenuList> llr = DcMenulist.getInstance(ctx).getAllMenulist(false, "%");
        if (llr.size() > 15) {
            storelistview_box.setVisibility(View.GONE);
            alphagridview_box.setVisibility(View.VISIBLE);
            closeBackdrop(containerbdl, includedFront);
        } else {
            alphagridview_box.setVisibility(View.GONE);
            storelistview_box.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openBackdrop(containerbdl, includedFront);
                }
            }, 500);
        }
    }

    private BackdropLayout containerbdl;
    private void initViews(View v) {
        includedFront = (LinearLayout) v.findViewById(R.id.includedFront);
        containerbdl = (BackdropLayout) v.findViewById(R.id.containerbdl);

        LinearLayout ll_content_box_main = (LinearLayout) v.findViewById(R.id.ll_content_box_main);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ll_content_box_main.getLayoutParams();
        int iMrgnTP =  Helper.getScrRatio(ctx) < 0.6 ? 10 : 200 ;
        int iMrgnLR =  Helper.getScrRatio(ctx) < 0.6 ? 10 : 100 ;
        params.setMargins(iMrgnLR, iMrgnTP, iMrgnLR, iMrgnTP);
        ll_content_box_main.setLayoutParams(params);
        ll_content_box_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("dsxof", String.valueOf(event.getX()) + " & " + String.valueOf(event.getY()));
                if (event.getX() > event.getY()) {
                    openBackdrop(containerbdl, includedFront);
                }
                return false;
            }
        });

        storelistview_box = (LinearLayout) v.findViewById(R.id.storelistview_box);
        listview = (ListView) v.findViewById(R.id.storelistview);
        alphagridview_box = (LinearLayout) v.findViewById(R.id.alphagridview_box);
        alphagridview = (GridView) v.findViewById(R.id.alphagridview);

        imgSearch = (MaterialRippleLayout) v.findViewById(R.id.layout_search);
        alphaSort = (MaterialRippleLayout) v.findViewById(R.id.layout_alpha);
        refreshAll = (MaterialRippleLayout) v.findViewById(R.id.layout_refresh);
        etCustomerName = (EditText) v.findViewById(R.id.et_customername);

        TextView tvVersion = (TextView) v.findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, getActivity()));

        MaterialRippleLayout btn_menu = (MaterialRippleLayout) v.findViewById(R.id.btn_menu);
        setupMenu();
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View vx) {
                if (Helper.getScreenDimension(ctx, "w") >= 1200) {
                    pmMenu.showAsAnchorRightTop(containerbdl, -iMrgnLR, -14);
                } else {
                    pmMenu.showAtCenter(containerbdl);
                }
            }
        });
    }

    private void setupMenu() {
        String dtLastSync = (sp.getData(SharedKey.LAST_SUCC_CUSTSYNC.getKey()).equals("")) ? "" :
            sp.getData(SharedKey.LAST_SUCC_CUSTSYNC.getKey());
        pmMenu = new PowerMenu.Builder(ctx)
            .addItem(new PowerMenuItem(" View Transactions ", false))
            .setDivider(new ColorDrawable(ContextCompat.getColor(ctx, R.color.white_1))).setDividerHeight(1)
            .addItem(new PowerMenuItem(" Re-Sync Customer Lists" + dtLastSync, false))
            .addItem(new PowerMenuItem(" Users ", false))
            .addItem(new PowerMenuItem(" Log-Out ", false))
            .setAnimation(MenuAnimation.ELASTIC_TOP_RIGHT)
            .setMenuRadius(10f).setBackgroundAlpha(0.4f).setWidth(400)
            .setMenuColor(ContextCompat.getColor(ctx, R.color.orange_2))
            .setTextColor(ContextCompat.getColor(ctx, R.color.white_1)).setTextGravity(Gravity.LEFT).setTextSize(14)
            .setTextTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .setIsClipping(true)
            .build();
    }

    private static final int SWIPTE_MAX_DISTANCE = 120;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private final GestureDetector.SimpleOnGestureListener mglx = new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Log.d("dsxof", String.valueOf(velocityX) + " & " + String.valueOf(velocityY));
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("dsxof", "up");
                return false; // Bottom to top
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                Log.d("dsxof", "down");
                return false; // Top to bottom
            }
            return true;
        }
    };

    /*private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }*/

    /*private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onLeftSwipe();
                }
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onRightSwipe();
                }
            } catch (Exception e) {

            }
            return false;
        }
    }*/

    private void initListeners(View v) {

        etCustomerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return false; }

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(!etCustomerName.getText().toString().trim().equals("")) {
                        Helper.hideSoftKeyboard(getActivity());
                        fillData(v, "%" +etCustomerName.getText().toString().trim() + "%", false);
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
                    fillData(v, "%" + etCustomerName.getText().toString().trim() + "%", false);
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
                showAlphaBox();
                closeBackdrop(containerbdl, includedFront);
            }
        });

        refreshAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
                storelistview_box.setVisibility(View.GONE);
                alphagridview_box.setVisibility(View.VISIBLE);
                requestCustomers("");
            }
        });
    }

    private OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int id, PowerMenuItem item) {
            pmMenu.dismiss();
            if (isUpdateCustomer) { Toast.makeText(ctx, updateCustomerMessage,  Toast.LENGTH_SHORT).show(); return; }
            switch(id){
                case 0:
                    Helper.changePage(ctx, getActivity().getSupportFragmentManager(),
                        new TransactionFragment(), "transaction_fragment", "customer_fragment");
                    break;
                case 1:
                    BounceView.addAnimTo( Helper.okCancelDialog(ctx,
                        "Update Customers", "Are you sure you want to re-fetch customer list?",
                        "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                storelistview_box.setVisibility(View.GONE);
                                alphagridview_box.setVisibility(View.VISIBLE);
                                requestCustomers("");
                            }
                        }, "Cancel", null, false) );
                    break;
                case 2:
                    loadUsers();
                    break;
                case 3:
                    BounceView.addAnimTo( Helper.okCancelDialog(ctx,
                        "Log Out", "Are you sure you want to log-out?",
                        "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finishAndRemoveTask();
                                startActivity(new Intent(ctx, LoginActivity.class));
                            }
                        }, "Cancel", null, false) );
                    break;
            }
        }
    };

    private void requestCustomers(String stringSearch) {
        if (Helper.isNetworkAvailable(ctx)) {
            floader = Helper.showSpinnerDialog(ctx, "Requesting CUSTOMER RECORDS...", "Pleasee wait."); floader.show();
//            showSpinnerDialog();
//            Toast.makeText(ctx, updateCustomerMessage, Toast.LENGTH_SHORT).show();

            VolleyInteractor vic = new VolleyInteractor();
            vic.registerCallback(this);
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            params.put("customer", stringSearch);
            if(sp.getInt(SharedKey.PER_AGENT_SETUP.getKey()) == 0) {
                params.put("agentid", "");
            } else {
                params.put("agentid", SharedData.getInstance(ctx).getData(SharedKey.REF_EMP_ID.getKey()));
            }

            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                it.remove();
            }
            strParams = strParams.replaceAll(" ", "%20");
            vic.getCustomers(ctx, params, strParams);
        } else {
            Toast.makeText(ctx, "Please check internet connectoin", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlphaBox() {
        storelistview_box.setVisibility(View.GONE);
        alphagridview_box.setVisibility(View.VISIBLE);
        alphagridview_box.setAlpha(0.0f);
        alphagridview_box.animate().translationY(0).alpha(1.0f).setListener(null);
    }

    private void showListBox() {
        alphagridview_box.setVisibility(View.GONE);
        storelistview_box.setVisibility(View.VISIBLE);
        storelistview_box.setAlpha(0.0f);
        storelistview_box.animate().translationY(0).alpha(1.0f).setListener(null);
    }

    private void fillData(View v, String stringSearch, Boolean isAlpha) {
        showSpinnerDialog();
        final LinkedList<MenuList> mlRSx = DcMenulist.getInstance(ctx).getAllMenulist(isAlpha, stringSearch);
        if (mlRSx.size() == 0) {
            mlRSx.add(new MenuList("0000000", "0000000", "No Record Found", "", 0, "", "false"));
        }

        // add bottom filler
        mlRSx.add(new MenuList("0000000", "0000000", "", "", 0, "", "false"));

        adapter = new MenuStoresAdapter(ctx, mlRSx);
        listview.setAdapter(adapter);
        if (mlRSx.size() > 0) {
            adapter.setOnButtonClickListener(new MenuStoresAdapter.OnButtonClickListener() {
                @Override
                public void onItemClick(View view,  int aid) {
                    MenuList mlRS = mlRSx.get(aid);
                    String strCIID = String.valueOf(mlRS.getCustomerIntegrationId());
                    if (SharedData.getInstance(ctx).getData(SharedKey.DATABASEID.getKey()).equals(ServerConstants.DEFAULT_DBID)) {
                        if (strCIID.equals("null") || strCIID.equals("0")) {
                            BounceView.addAnimTo(Helper.okDialog(ctx, "Customer Data Error",
                                "Customer Integration Record ID not found! Please contact IT support.",
                                "CLOSE", null,true));
                        } else {
                            SharedData.getInstance(ctx).saveData(SharedKey.ORDER_CUSTOMER_ID.getKey(), String.valueOf(mlRS.getCustomerID()));
                            Helper.changePage(ctx, getActivity().getSupportFragmentManager(), new OrderFragment(),
                            "order_fragment", "customer_fragment");
                        }
                    } else {
                        SharedData.getInstance(ctx).saveData(SharedKey.ORDER_CUSTOMER_ID.getKey(), String.valueOf(mlRS.getCustomerID()));
                        Helper.changePage(ctx, getActivity().getSupportFragmentManager(), new OrderFragment(),
                        "order_fragment", "customer_fragment");

                    }
                }
            });
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissSpinnerDialog();
            }
        }, 300);
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
                DcMenulist.getInstance(ctx).emptyMenulist();
                if(objArr.length() > 0) {
                    PRTotal = objArr.length();
                    xProgressDialog.setMax(PRTotal);
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject obj = objArr.getJSONObject(i);

                        String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                        String strInvoice = ""; try { strInvoice = obj.getString(MenulistKey.INVOICE.getKey());
                        } catch (Exception e) { strInvoice = "false"; }
                        MenuList mlList = new MenuList(
                            obj.getString(MenulistKey.CUSTOMER_ID.getKey()),
                            obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()),
                            strCustomerName,
                            "",
                            0,
                            "",
                            strInvoice
                        );
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
                    return "Task Completed.";
                } else {
                    return "Task No Customer Found.";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                isUpdateCustomer = false;
                return "Task Error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            progressBarx.setVisibility(View.GONE);
            loadSavedItems();
            isUpdateCustomer = false;
            xProgressDialog.dismiss();

            if (result.equals("Task No Customer Found.")) {
                Helper.dismissSpinnerDialog(floader);
                Toast.makeText(ctx, "No customer found!", Toast.LENGTH_SHORT).show();
            } else {
                new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Helper.dismissSpinnerDialog(floader);

                            SharedData.getInstance(ctx).saveData(SharedKey.LAST_SUCC_CUSTSYNC.getKey(), "\r\n ["+ Helper.getReqDate(4, "") + "]");
                            setupMenu();

                            if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1) {
                                LinkedList<aItemlist> ailRs = DcAitemlist.getInstance(ctx).getaItemlist();
                                loader = Helper.showSpinnerDialog(ctx,"Requesting PRODUCT ITEMS...", "Please wait."); loader.show();
                                requestProductItems();
                            } else {
                                Toast.makeText(ctx, "Records updated successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    500
                );
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            xProgressDialog = new ProgressDialog(ctx, R.style.ProgressSpinnerTheme);
            xProgressDialog.setMessage("Updating CUSTOMER RECORDS... Please wait.");
            xProgressDialog.setIndeterminate(false);
            xProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            xProgressDialog.setCancelable(false);
            xProgressDialog.setCanceledOnTouchOutside(false);
            xProgressDialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            xProgressDialog.setProgress(values[0]);
        }
    }

    public void onRequestSuccess(String response, String type) {
        LinkedList<MenuList> mlRS = new LinkedList<MenuList>();
        try {
            if (type.equals("getprerequisite")) {
                DcStaffs.getInstance(ctx).emptyStaffslist();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.length() > 0) {
                        DcStaffs.getInstance(ctx).emptyStaffslist();
                        JSONArray sArr = obj.getJSONArray("staff");
                        if (sArr.length() > 0) {
                            for (int i = 0; i < sArr.length(); i++) {
                                JSONObject rowObj = sArr.getJSONObject(i);
                                aStaffs sl = new aStaffs(
                                    rowObj.getLong("empId"),
                                    rowObj.getString("refempno").equals("null") ? "-1" : rowObj.getString("refempno"),
                                    rowObj.getString("empNo"),
                                    rowObj.getString("Email"),
                                    rowObj.getString("name"),
                                    rowObj.getLong("Branch"),
                                    rowObj.getLong("Jobtitle"),
                                    Helper.encryptMsg(rowObj.getString("pass"), Helper.generateKey()),
                                    rowObj.getString("active"),
                                    rowObj.getString("ismobileadmin")
                                );
                                DcStaffs.getInstance(ctx).insertStaffs(sl);
                            }
                        }
                        Helper.dismissSpinnerDialog(loader);
                        Toast.makeText(ctx, "Request success...", Toast.LENGTH_SHORT).show();
                    } else {
                        Helper.dismissSpinnerDialog(loader);
                        BounceView.addAnimTo( Helper.okDialog( ctx,
                            "Data Sync Error","Data Sync Error, please contact IT support",
                            "CLOSE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, false) );
                    }
                } catch (JSONException e) {
                    Helper.dismissSpinnerDialog(loader);
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        "Unrecognized Device", getString(R.string.unrecognized_device),
                        "CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false)
                    );
                }
            } else if (type.equals("searchcustomer")) {
                JSONArray objArr = new JSONArray(response);
                progressBarx = (ProgressBar) v.findViewById(R.id.progressBar);
                progressBarx.setMax(objArr.length());
                Helper.dismissSpinnerDialog(floader);
                new customerAT().execute(response);
            } else {
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    for (int i = 0; i < objArr.length(); i++) {
                        try {
                            JSONObject obj = objArr.getJSONObject(i);
                            String strCustomerName = obj.getString(MenulistKey.CUSTOMER_NAME.getKey());
                            String strInvoice = ""; try { strInvoice = obj.getString(MenulistKey.INVOICE.getKey());
                            } catch (Exception e) { strInvoice = "false"; }
                            MenuList mlList = new MenuList(
                                obj.getString(MenulistKey.CUSTOMER_ID.getKey()),
                                obj.getString(MenulistKey.CUSTOMER_INTEG_ID.getKey()),
                                strCustomerName,
                                "", 0, "",
                                strInvoice
                            );
                            mlRS.add(mlList);
                        } catch (JSONException e) {
                            dismissSpinnerDialog();
                            e.printStackTrace();
                        }
                    }
                }else{
                    mlRS.add(new MenuList("0000000", "0000000", "No Record Found", "", 0, "", "false"));
                }
            }
        } catch (JSONException e) {
            dismissSpinnerDialog();
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
        Helper.dismissSpinnerDialog(loader);
        Helper.dismissSpinnerDialog(floader);
        dismissSpinnerDialog();
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
    }

    private void requestProductItems() {
        Log.d("dsx", "request product items");
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(String response, String type) {
                Helper.dismissSpinnerDialog(loader);
                new requestProductItemsAsync().execute(response);
            }

            @Override
            public void onRequestFail(VolleyError response, String type) {
                Log.d("DSX fetch items error: ", response.getMessage());
            }
        });

        HashMap<String, String> params = new HashMap<>();
        String searchStr = "";
        params.put("itemname", searchStr);
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
        params.put("customerid", "-1");
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        vi.getItemlist(ctx, params, strParams);
    }

    private Integer PRTotal = 0;
    ProgressDialog  xProgressDialog;
    private class requestProductItemsAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String response = params[0].replace("\r\n ", "");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    PRTotal = objArr.length();
                    xProgressDialog.setMax(PRTotal);
                    DcAitemlist.getInstance(ctx).emptyaItemlist();
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject rowObj = objArr.getJSONObject(i);
                        aItemlist ail = new aItemlist(
                            String.valueOf(rowObj.getString(aItemlistKey.INTEGRATION_RECID.getKey())),
                            rowObj.getLong(aItemlistKey.RECID.getKey()),
                            String.valueOf(rowObj.getString(aItemlistKey.OLD_SKU.getKey())),
                            rowObj.getInt(aItemlistKey.BASEUNIT_RECID.getKey()),
                            rowObj.getDouble(aItemlistKey.BASEUNIT_QTY.getKey()),
                            rowObj.getString(aItemlistKey.ITEMNO.getKey()),
                            rowObj.getString(aItemlistKey.ITEMNAME.getKey()),
                            rowObj.getString(aItemlistKey.ITEMNAME_WUNIT.getKey()),
                            rowObj.getDouble(aItemlistKey.QUANTITY_INUNIT.getKey()),
                            rowObj.getString(aItemlistKey.DEPT.getKey()),
                            rowObj.getString(aItemlistKey.UNIT.getKey()),
                            rowObj.getInt(aItemlistKey.TBLUNIT_RECID.getKey()),
                            rowObj.getInt(aItemlistKey.UNIT_TOCONVERT.getKey()),
                            rowObj.getString(aItemlistKey.BARCODENO.getKey()),
                            rowObj.getBoolean(aItemlistKey.F_BASE.getKey()) == true ? 1 : 0,
                            rowObj.getString(aItemlistKey.D_ITEMDEPARTMENT_CODE.getKey()),
                            String.valueOf(rowObj.getString(aItemlistKey.SELLING_PRICE.getKey())).equals("null")
                                    ? 0.00 : rowObj.getDouble(aItemlistKey.SELLING_PRICE.getKey()),
                            String.valueOf(rowObj.getString(aItemlistKey.COST_PRICE.getKey())).equals("null")
                                    ? 0.00 : rowObj.getDouble(aItemlistKey.COST_PRICE.getKey()),
                            rowObj.getString(aItemlistKey.TAXCODE.getKey()),
                            String.valueOf(rowObj.getString(aItemlistKey.EXPENSE_ACCT.getKey())).equals("null")
                                    ? 0 : rowObj.getInt(aItemlistKey.EXPENSE_ACCT.getKey()),
                            String.valueOf(rowObj.getString(aItemlistKey.INCOME_ACCT.getKey())).equals("null")
                                    ? 0 : rowObj.getInt(aItemlistKey.INCOME_ACCT.getKey()),
                            rowObj.getString(aItemlistKey.DATA_VISIBILITY.getKey()),
                            rowObj.getString(aItemlistKey.BARCODENO1.getKey())
                        );
                        DcAitemlist.getInstance(ctx).insertaItemlist(ail);
                        publishProgress(i+1);
                    }
                }
                return "fetch items success";
            } catch (JSONException e) {
                e.printStackTrace();
                return "fetch items error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            xProgressDialog.dismiss();
            Toast.makeText(ctx, "Records updated successfully.", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            xProgressDialog = new ProgressDialog(ctx, R.style.ProgressSpinnerTheme);
            xProgressDialog.setMessage("Updating PRODUCT ITEMS... Please wait.");
            xProgressDialog.setIndeterminate(false);
            xProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            xProgressDialog.setCancelable(false);
            xProgressDialog.setCanceledOnTouchOutside(false);
            xProgressDialog.setProgress(0);
            xProgressDialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            xProgressDialog.setProgress(values[0]);
        }
    }

    private void updateUsers() {
        if (Helper.isNetworkAvailable(getActivity())) {
            loader = Helper.showSpinnerDialog(ctx, "", "Updating... Please wait..."); loader.show();

            VolleyInteractor vipr = new VolleyInteractor();
            vipr.registerCallback(this);
            HashMap<String, String> params = new HashMap<>();
            params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            params.put("branchid", SharedData.getInstance(ctx).getData(SharedKey.BRANCH_ID.getKey()));
            Iterator it = params.entrySet().iterator();
            String strParams = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                it.remove();
            }
            vipr.getPreRequisite(ctx, params, strParams.replaceAll(" ", "%20"));
        } else {
            BounceView.addAnimTo( Helper.okDialog( ctx,
                "Initialization Error","This app requires internet to initialize.  Please check your connection.",
                "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false) );
        }
    }

    private void loadUsers() {
        if (!Helper.isNetworkAvailable(ctx)) {
            Toast.makeText(ctx, "User fetch failed.  Please check your connection.",
                    Toast.LENGTH_SHORT).show(); return;
        }

        loader = Helper.showSpinnerDialog(ctx, "Reloading Users", "Please wait..."); loader.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final VolleyInteractor vipr = new VolleyInteractor();
                vipr.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(final String response, String type) {
                        Log.d("dsxs getuser", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.length() > 0) {
                                DcStaffs.getInstance(ctx).emptyStaffslist();
                                JSONArray sArr = obj.getJSONArray("staff");
                                if (sArr.length() > 0) {
                                    for (int i = 0; i < sArr.length(); i++) {
                                        JSONObject rowObj = sArr.getJSONObject(i);
                                        aStaffs sl = new aStaffs(
                                            rowObj.getLong("empId"),
                                            rowObj.getString("refempno").equals("null") ? "-1" : rowObj.getString("refempno"),
                                            rowObj.getString("empNo"),
                                            rowObj.getString("Email"),
                                            rowObj.getString("name"),
                                            rowObj.getLong("Branch"),
                                            rowObj.getLong("Jobtitle"),
                                            Helper.encryptMsg(rowObj.getString("pass"), Helper.generateKey()),
                                            rowObj.getString("active"),
                                            rowObj.getString("ismobileadmin")
                                        );
                                        DcStaffs.getInstance(ctx).insertStaffs(sl);
                                    }
                                }
                                loadAdminJobTitles();
                            } else {
                                Log.d("dsxe getuser", response);
//                                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("dsxe getuser", response);
//                            Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        Log.d("dsxe getuser", String.valueOf(response));
//                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
                HashMap<String, String> params = new HashMap<>();
                params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
                params.put("branchid", sp.getData(SharedKey.BRANCH_ID.getKey()));
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
                    it.remove();
                }
                vipr.getPreRequisite(ctx, params, strParams.replaceAll(" ", "%20"));
            }
        }, 300);
    }

    private void loadAdminJobTitles() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
        params.put("type", "5");
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&";
            it.remove();
        }
        VolleyInteractor viag = new VolleyInteractor();
        viag.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(String response, String type) {
                Helper.dismissSpinnerDialog(loader);
                response = response.replace("\r\n ", "");
                Log.d("DSX post response: ", response);

                SharedData.getInstance(ctx).saveData(SharedKey.REF_JOBTITLES.getKey(), response);

                DialogFragment dialogFrag = UserFragment.searchInstance();
                dialogFrag.setTargetFragment(thisFragment, USER_DIALOG_FRAGMENT);
                dialogFrag.setCancelable(false);
                dialogFrag.show(getActivity().getSupportFragmentManager(), "user_search_item");
            }
            @Override
            public void onRequestFail(VolleyError response, String type) {
                Helper.dismissSpinnerDialog(loader);
                Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
            }
        });
        viag.getAdminGroupings(ctx, params, strParams.replaceAll(" ", "%20"));
    }
}
