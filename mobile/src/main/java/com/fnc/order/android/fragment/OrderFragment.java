package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.daimajia.swipe.SwipeLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.LoginActivity;
import com.fnc.order.android.adapters.OrderlistAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcAitemlist;
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.enumeration.PersonsKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.listeners.DatePickerListener;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aItemlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.DatePickerDialogFragment;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PopupMenu;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.core.NanoClock;
import com.google.api.services.storage.model.Objects;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.fnc.order.android.utilities.RelativePopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hari.bounceview.BounceView;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OrderFragment extends Fragment implements VolleyCallback {

    private static final int PERSON_DIALOG_FRAGMENT = 7;
    private static final int ITEM_DIALOG_FRAGMENT = 8;
    private static final int SUMMARY_DIALOG_FRAGMENT = 9;
    private static final int USER_DIALOG_FRAGMENT = 5;
    private static final int OTHER_ITEMS_DIALOG_FRAGMENT = 6;

    public Context ctx;
    private View rootView;
    private Fragment thisFragment;
    private ProgressDialog loader;
    private MaterialRippleLayout btn_add_item, btn_submit, btn_menu, btn_back, btn_refresh,
            btn_others, btn_setzero, btn_search, btn_closesearch, btn_view;
    private TextView tv_name, tv_grandtotal, tv_grandtotal_count, tv_grandtotal_lbl, tv_header_price, tv_header_total, tv_hdr_freeitem;
    private EditText et_date, et_remarks, et_search;
    private CoordinatorLayout rl_content_box;
    private DroppyMenuPopup.Builder pageMenu;
    private LinearLayout mainTableBox;
    private LinearLayout tblContentBox;
    private TableLayout tl;
    private String refPersonIdentityId = "";
    private static AlertDialog alertDialog, alertDialogRemarks, alertDialogPONumber;
    private LinearLayout bsCalc, ll_search, ll_closesearch, llet_search;
    private BottomSheetBehavior bsBh;
    private PopupMenu menuPop;
    private VolleyInteractor vi;
    private String refDate, refStringDate, strBranchEncoding;
    private ListView list_view;
    private OrderlistAdapter adapter;
    private SwipeLayout swipeLayout;
    private LinkedList<Order> curRefArrayList;
    private LinkedList<Order> curRefArrayListErr;
    private SharedData sp;
    private MenuList refMenulist;
    private BroadcastReceiver connStatusReceiver;
    private Integer calcRefId = 0, curRefPos = 0, maintableViewHeight = 0, maintableViewWidth = 0;
    private Boolean isBacked = false, isItemClicked = false, isSummary, oldskuerr = false,
        mx_taskrun = false;
    private Handler mx_handler; private Runnable mx_runnable;

    public OrderFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(connStatusReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ctx = rootView.getContext();
        thisFragment = this;
        sp = SharedData.getInstance(ctx);
        Helper.setPreviousPage(ctx, "customer_fragment");

        curRefArrayList = new LinkedList<Order>();
        curRefArrayListErr = new LinkedList<Order>();

        LinkedList<MenuList> mlRs = DcMenulist.getInstance(ctx).searchMenuFilterMultiple(
                MenulistKey.CUSTOMER_ID.getKey() + " = ?",
                new String[] { sp.getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) } );
        if (mlRs.size() > 0 ) {
            refMenulist = DcMenulist.getInstance(ctx).searchMenuFilterMultiple(
                MenulistKey.CUSTOMER_ID.getKey() + " = ?",
                new String[] { sp.getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) }
            ).get(0);
        }

        if (Helper.checkBranchProfile(ctx).get(0).getDescription().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
            strBranchEncoding = "1";
        } else {
            strBranchEncoding = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(
                    aBranchlistKey.CUSTOMERID.getKey() + " = ?",
                    new String[] { SharedData.getInstance(ctx).getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) }
            ).get(0).getOld_branchid();
        }

        mainTableBox = (LinearLayout) rootView.findViewById(R.id.actual_table_box);
        mainTableBox.post(new Runnable() {
            @Override
            public void run() {
                maintableViewHeight = mainTableBox.getHeight();
                maintableViewWidth = mainTableBox.getWidth();
                try {
                    tblContentBox = new LinearLayout(ctx);
                    tblContentBox.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    mainTableBox.addView(tblContentBox);
                } finally {
                    LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_table_content, null);
                    tl = (TableLayout) ll.findViewById(R.id.checklist_table_layout);
                    tl.setLayoutParams(new LinearLayout.LayoutParams(maintableViewWidth,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    tblContentBox.addView(ll);
                }
            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (Helper.checkBranchProfile(ctx).get(0).getDescription().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
//                    if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                        backItNow(v);
                        return true;
                    } else{
                        return false;
                    }
                }
                return false;
            }
        } );

        initViews(rootView);
        initListeners(rootView);
        initCalc(rootView);

        /* if(sp.getData(SharedKey.DATABASEID.getKey()).equals(ServerConstants.DEFAULT_DBID)) {
            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
            btn_refresh.setVisibility(View.VISIBLE);
        } else {
            sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 0);
            btn_refresh.setVisibility(View.GONE);
        } */
        if (sp.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == 1) {
            btn_refresh.setVisibility(View.VISIBLE);
            DcOrder.getInstance(ctx).updateSetAllQuantity("", null);
            fillItems(rootView);
        } else {
            btn_refresh.setVisibility(View.GONE);
            DcOrder.getInstance(ctx).emptyOrderlist();
        }

        connStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("netConnStat")) {
                    ((TextView) rootView.findViewById(R.id.tv_conn_stat_conn)).setText("ALIVE V-" + Helper.getVersion(ctx, getActivity()));
                    if (Helper.getScrRatio(ctx) < 0.6) {
                        ((TextView) rootView.findViewById(R.id.tv_conn_stat_conn)).setText("ALIVE");
                    }
                    ((TextView) rootView.findViewById(R.id.tv_conn_stat)).setText("DOWN V-" + Helper.getVersion(ctx, getActivity()));
                    if (intent.getStringExtra("isConnected").equals("false")) {
                        ((TextView) rootView.findViewById(R.id.tv_conn_stat_conn)).setVisibility(View.GONE);
                        ((TextView) rootView.findViewById(R.id.tv_conn_stat)).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView) rootView.findViewById(R.id.tv_conn_stat_conn)).setVisibility(View.VISIBLE);
                        ((TextView) rootView.findViewById(R.id.tv_conn_stat)).setVisibility(View.GONE);
                    }
                }
            }
        };
        getActivity().registerReceiver(connStatusReceiver, new IntentFilter("netConnStat"));

        return rootView;
    }

    private void fillItems(View v) {
//        Boolean hasx = false; if (hasx) {
        if (Helper.isNetworkAvailable(ctx)) {
            final SharedData sp = SharedData.getInstance(ctx);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            loader = Helper.showSpinnerDialog(ctx, "", "Updating PRELOADED LIST... Please wait..."); loader.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("itemname", "");
            params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
            params.put("customerid", refMenulist.getCustomerID());
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
        } else {
            if (Helper.checkBranchProfile(ctx).get(0).getDescription().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
//            if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                Toast.makeText(ctx, "Please check internet connection....", Toast.LENGTH_SHORT).show();
            } else {
//                try {
                    LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).getOrderlist();
                    showItems(llOrderRs);
//                } catch (JSONException e) {
//                    Toast.makeText(ctx, "Error fetching offline items", Toast.LENGTH_SHORT).show();
//                }
            }

        }
    }

    private Integer refQtyOrFree = 0;
    private void showItems(LinkedList<Order> llOrderRs) {
        if (llOrderRs.size() > 0) {
            curRefArrayList = new LinkedList<Order>();
            for (int i = 0; i < llOrderRs.size(); i++) {
                Order ol = llOrderRs.get(i);
                if (SharedData.getInstance(ctx).getInt(SharedKey.SKU_VALIDATION.getKey()) == 1) {
                    if (!ol.getOldSku().equals("null") && !ol.getOldSku().equals("0")) {
                        curRefArrayList.add(ol);
                    }
                } else {
                    curRefArrayList.add(ol);
                }
            }

            adapter = new OrderlistAdapter(ctx, curRefArrayList);
            adapter.setCurPos(curRefPos);
            adapter.setOnItemClickListener(new OrderlistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, int flag) {
                    adapter.setCurPos(position);
                    curRefPos = adapter.getCurPos();
                    Helper.hideSoftKeyboard(getActivity());
                    refQtyOrFree = flag;
                    adapter.setIsFreeClicked(flag);
                    adapter.notifyDataSetChanged();
                    new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        }, 300
                    );
                }
            });
            adapter.setOnSrbClickListener(new OrderlistAdapter.OnSrbClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    getSrb(position);
                }
            });
            adapter.setOnRemarksClickListener(new OrderlistAdapter.OnRemarksClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                    adapter.setCurPos(position);
                    alertDialogRemarks = okCancelInputRemarksDialogBuilder(ctx,
                        "Add Remarks",
                        "SAVE", null, "CANCEL", cancelCallback);
                    BounceView.addAnimTo(alertDialogRemarks);
                }
            });
            list_view.setAdapter(adapter);

            recomputeGrandTotal();
        } else {
            Toast.makeText(ctx, "Error fetching items", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSrb(Integer pos) {
        loader = Helper.showSpinnerDialog(ctx, "Fetching RUNNING BALANCE.", "Please wait..."); loader.show();
        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
        adapter.setCurPos(pos);
        Order ol =  curRefArrayList.get(adapter.getCurPos());

        VolleyInteractor vsrb = new VolleyInteractor();
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
        params.put("item_recid", ol.getItemRecid());
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        strParams = strParams.replaceAll(" ", "%20");
        vsrb.registerCallback(new VolleyCallback() {
            @Override
            public void onRequestSuccess(String response, String type) {
                loader.dismiss();
                try {
                    response = response.replace("\r\n ", "");
                    if (!response.equals("[]")) {
                        JSONArray objArr = new JSONArray(response);
                        String strInfo = "";
                        for (int j = 0; j < objArr.length(); j++) {
                            JSONObject obj = objArr.getJSONObject(j);
                            String strCode = String.valueOf(obj.getString("code"));
                            String strCnt = String.valueOf(obj.getString("SRB")).equals("null") ? "---" :
                                String.valueOf(obj.getString("SRB"));
                            strInfo = strInfo + strCode.toLowerCase().toUpperCase() + ": " + strCnt + "\n";
                        }
                        BounceView.addAnimTo( Helper.okDialog(ctx,
                            "Stock Running Balance", "\n" + ol.getItemName() + "\n\n" + strInfo
                            , "CLOSE",null, false) );
                    } else {
                        Toast.makeText(ctx, "SRB info not found.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ctx, "Request SRB Error, please contact IT support.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onRequestFail(VolleyError response, String type) {
                loader.dismiss();
                Toast.makeText(ctx, "Request SRB Error, please contact IT support.", Toast.LENGTH_SHORT).show();
            }
        });
        vsrb.getSrb(ctx, params, strParams);
    }

    private void initViews(View v) {
        btn_view = (MaterialRippleLayout) v.findViewById(R.id.btn_view);
        llet_search = (LinearLayout) v.findViewById(R.id.llet_search);
        ll_search = (LinearLayout) v.findViewById(R.id.ll_search);
        ll_closesearch = (LinearLayout) v.findViewById(R.id.ll_closesearch);
        btn_closesearch = (MaterialRippleLayout) v.findViewById(R.id.btn_closesearch);
        btn_search = (MaterialRippleLayout) v.findViewById(R.id.btn_search);
        btn_setzero = (MaterialRippleLayout) v.findViewById(R.id.btn_setzero);
        btn_refresh = (MaterialRippleLayout) v.findViewById(R.id.btn_refresh);
        btn_others = (MaterialRippleLayout) v.findViewById(R.id.btn_others);
        btn_add_item = (MaterialRippleLayout) v.findViewById(R.id.btn_add_item);
        btn_back = (MaterialRippleLayout) v.findViewById(R.id.btn_back);
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_name.setText(refMenulist.getCustomerName());
        rl_content_box = (CoordinatorLayout) v.findViewById(R.id.content_box);
        et_date = (EditText) v.findViewById(R.id.et_date);
        et_search = (EditText) v.findViewById(R.id.et_search);
        btn_menu  = (MaterialRippleLayout) v.findViewById(R.id.btn_menu);

        pageMenu = new DroppyMenuPopup.Builder(ctx, btn_menu);
        pageMenu.setXOffset(8).setYOffset(0);
        pageMenu.addMenuItem(new DroppyMenuItem("  View Transactions  ").setId(1)).addSeparator();
        if (SharedData.getInstance(ctx).getInt(SharedKey.SAVE_PRODUCT_ITEMS.getKey()) == 1) {
            pageMenu.addMenuItem(new DroppyMenuItem("  Update Product Items  ").setId(2)).addSeparator();
        }
        if (SharedData.getInstance(ctx).getData(SharedKey.EMP_POSITION.getKey()).equals("1912072415") ||
                SharedData.getInstance(ctx).getData(SharedKey.EMP_ISMOBILEADMIN.getKey()).equals("true")) {
            pageMenu.addMenuItem(new DroppyMenuItem("  Users  ").setId(3)).addSeparator();
        }
        pageMenu.addMenuItem(new DroppyMenuItem("  Log-out  ").setId(0));

        btn_submit = (MaterialRippleLayout) v.findViewById(R.id.btn_submit);
        et_remarks = (EditText) v.findViewById(R.id.et_remarks);
        bsCalc = (LinearLayout) v.findViewById(R.id.bs_calculator);
        bsCalc.setVisibility(View.VISIBLE);

        TextView tvVersion = (TextView) v.findViewById(R.id.tv_version);
        tvVersion.setText(Helper.getVersion(ctx, getActivity()));

        list_view = (ListView) v.findViewById(R.id.list_view);
        tv_grandtotal = (TextView) v.findViewById(R.id.tv_grandtotal);
        tv_grandtotal_count = (TextView) v.findViewById(R.id.tv_grandtotal_count);

        if (SharedData.getInstance(ctx).getInt(SharedKey.COMPUTE_QTY_ONLY.getKey()) == 1) {
            tv_grandtotal_count.setVisibility(View.VISIBLE);
        } else {
            tv_grandtotal.setVisibility(View.VISIBLE);
        }

        tv_grandtotal_lbl = (TextView) v.findViewById(R.id.tv_grandtotal_lbl);

        tv_grandtotal_lbl.setText("Total:");
        if (SharedData.getInstance(ctx).getInt(SharedKey.COMPUTE_QTY_ONLY.getKey()) == 1) {
            tv_grandtotal_lbl.setText("Total:");
        }

        tv_header_price = (TextView) v.findViewById(R.id.tv_header_price);
        tv_header_price.setVisibility(View.GONE);
        if (SharedData.getInstance(ctx).getInt(SharedKey.SHOW_PRICE_COL.getKey()) == 1) {
            tv_header_price.setVisibility(View.VISIBLE);
        }

        tv_header_total = (TextView) v.findViewById(R.id.tv_header_total);
        tv_header_total.setVisibility(View.GONE);
        if (SharedData.getInstance(ctx).getInt(SharedKey.SHOW_TOTAL_COL.getKey()) == 1) {
            tv_header_total.setVisibility(View.VISIBLE);
        }

        tv_hdr_freeitem = (TextView) v.findViewById(R.id.tv_hdr_freeitem);
        tv_hdr_freeitem.setVisibility(View.GONE);
        if (SharedData.getInstance(ctx).getInt(SharedKey.SHOW_FREE_COL.getKey()) == 1) {
            tv_hdr_freeitem.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners(View v) {
        bsBh = BottomSheetBehavior.from(bsCalc);
        bsBh.setHideable(true);
        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
        et_date.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                Helper.hideSoftKeyboard(getActivity());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callCloseSearch();
                        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                        showDatePicker();
                    }
                }, 300);
            }
        });
        et_remarks.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                et_search.setText("");
                llet_search.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                ll_closesearch.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }, 300);
            }
        });
        bsCalc.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(isBacked == false) {
                    isBacked = true;
                    backItNow(v);
                }
            }
        });
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                DialogFragment dialogFrag = SearchItemFragment.searchInstance();
                dialogFrag.setTargetFragment(thisFragment, ITEM_DIALOG_FRAGMENT);
                dialogFrag.setCancelable(false);
                dialogFrag.show(getActivity().getSupportFragmentManager(), "dialog_search_item");
            }
        });
        rl_content_box.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                Helper.hideSoftKeyboard(getActivity());
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                showSummary("false");
            }
        });

        pageMenu.setOnClick((v1, id) -> {
            switch(id){
                case 1:
                    Helper.changePage(ctx, getActivity().getSupportFragmentManager(),
                        new TransactionFragment(), "transaction_fragment", "order_fragment");
                    break;
                case 2:
                    requestProductItems();
                    break;
                case 3:
                    loadUsers();
                    break;
                case 0:
                    BounceView.addAnimTo( Helper.okCancelDialog(ctx,
                        "Log Out", "Are you sure you want to log-out?",
                        "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                startActivity(new Intent(ctx, LoginActivity.class));
                            }
                        }, "Cancel", null, false) );
                    break;
            }
        }).build();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                Helper.hideSoftKeyboard(getActivity());

                new android.os.Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (!isPosted) {
                                isPosted = true;
                                et_search.setText("");
                                llet_search.setVisibility(View.GONE);
                                ll_search.setVisibility(View.VISIBLE);
                                ll_closesearch.setVisibility(View.GONE);

                                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                                    if (sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-1") || sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-2")) {
                                        BounceView.addAnimTo( Helper.okDialog(ctx,
                                            "Error","Your account is not valid to process this request. Please contact IT support.", "CLOSE",
                                            null, false) );
                                        isPosted = false;
                                        return;
                                    }
                                } else {
                                    if (sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-2")) {
                                        BounceView.addAnimTo( Helper.okDialog(ctx,
                                            "Error","Your account is not valid to process this request. Please contact IT support.", "CLOSE",
                                            null, false) );
                                        isPosted = false;
                                        return;
                                    }
                                }

                                loader = Helper.showSpinnerDialog(ctx, "", "Please wait..."); loader.show();
                                LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).getOrderlist();
                                showItems(llOrderRs);

                                new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            isPosted = false;
                                            bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                                            Helper.dismissSpinnerDialog(loader);
                                            callSubmit();
                                        }
                                    }, 500
                                );
                            }
                        }
                    }, 300
                );

            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                callRefreshItems();
            }
        });

        btn_setzero.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                callSetZero();
            }
        });

        btn_others.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                DialogFragment dialogFrag = OtherItemFragment.searchInstance();
                dialogFrag.setTargetFragment(thisFragment, OTHER_ITEMS_DIALOG_FRAGMENT);
                dialogFrag.setCancelable(false);
                dialogFrag.show(getActivity().getSupportFragmentManager(), "other_item");
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                llet_search.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                ll_closesearch.setVisibility(View.VISIBLE);
                Helper.hideSoftKeyboard(getActivity());
                new android.os.Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }, 300
                );
            }
        });

        btn_closesearch.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                callCloseSearch();
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.v("zcv", et_search.getText().toString().trim());
                    LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx)
                            .searchOrderFilterMultiple(OrderKey.ITEM_NAME.getKey() + " LIKE ?",
                                    new String[] { "%" + et_search.getText().toString().trim() + "%" },
                                    " ORDER BY " +  OrderKey.ITEM_NAME.getKey() + " ASC" );
                    showItems(llOrderRs);
                    return true;
                }
                return false;
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                if (mx_taskrun) { mx_handler.removeCallbacks(mx_runnable); }

                mx_handler = new Handler();
                mx_runnable = new Runnable() {
                    @Override
                    public void run() {
                        String searchStr = s.toString().trim().equals("") ? "noitem" : s.toString() ;

                        LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx)
                                .searchOrderFilterMultiple(OrderKey.ITEM_NAME.getKey() + " LIKE ?",
                                        new String[] { "%" + et_search.getText().toString().trim() + "%" },
                                        " ORDER BY " +  OrderKey.ITEM_NAME.getKey() + " ASC" );
                        showItems(llOrderRs);
                        mx_taskrun = false;
                    }
                };
                mx_taskrun = mx_handler.postDelayed(mx_runnable, 700);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    private void callCloseSearch() {
        et_search.setText("");
        llet_search.setVisibility(View.GONE);
        ll_search.setVisibility(View.VISIBLE);
        ll_closesearch.setVisibility(View.GONE);
        Helper.hideSoftKeyboard(getActivity());
        new android.os.Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).getOrderlist();
                    showItems(llOrderRs);
                    bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }, 300
        );
    }

    private void callSubmit() {

        if (oldskuerr == true) {
            String strSkuMsg = "";
            for (int i = 0; i < curRefArrayListErr.size(); i++) {
                Order rowRl = curRefArrayListErr.get(i);
                String refCnt = String.valueOf(i+1);
                strSkuMsg = strSkuMsg + "\n " + refCnt + ". " + rowRl.getItemName();
            }
            BounceView.addAnimTo( Helper.okDialog(ctx,
                    "Warning","Some items does not have reference SKU, please contact the developer for assistance.\n" + strSkuMsg, "OK",
                    null, false) );

            return;
        }

        Helper.hideSoftKeyboard(getActivity());

//                LinkedList<Order> ol =  DcOrder.getInstance(ctx).getOrderlistAsc();
//                final ArrayList<HashMap> detailsArrayList = new ArrayList();
        if(curRefArrayList.size() > 0) {
            Boolean errFlag = false;
            for (int i = 0; i < curRefArrayList.size(); i++) {
                Order rowRl = curRefArrayList.get(i);
                if(rowRl.getQuantity().equals("")) {
//                            if(rowRl.getRemarks().equals("") || rowRl.getRemarks().equals("null")) {
                    errFlag = true;
//                            errFlag = false;
                    curRefArrayList.get(i).setIsError(1);
//                            curRefArrayList.get(i).setIsError(false);
//                            curRefArrayList.get(i).setQuantity("0");

//                            TableRow trx = (TableRow) tl.findViewById(Integer.parseInt(rowRl.getItemRecid()));
//                            TextView tvQty = (TextView) trx.getChildAt(0);
//                            tvQty.setTextColor(getResources().getColor(R.color.red_2));
//                            TextView tvUnit = (TextView) trx.getChildAt(1);
//                            tvUnit.setTextColor(getResources().getColor(R.color.red_2));
//                            LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
//                            TextView tvDesc = (TextView) tvDescBox.getChildAt(0);
//                            tvDesc.setTextColor(getResources().getColor(R.color.red_2));
//                            TextView tvPrice = (TextView) trx.getChildAt(3);
//                            tvPrice.setTextColor(getResources().getColor(R.color.red_2));
//                            TextView tvTotal = (TextView) trx.getChildAt(4);
//                            tvTotal.setTextColor(getResources().getColor(R.color.red_2));
//                            }
                }else{
                    curRefArrayList.get(i).setIsError(0);
                }
            }

            if (errFlag) {
                Helper.dismissSpinnerDialog(loader);
                adapter.notifyDataSetChanged();
//                        Toast.makeText(ctx, "Please check 0 or empty quantity.", Toast.LENGTH_LONG).show();
                BounceView.addAnimTo( Helper.okDialog(ctx,
                    "Error","Please set 0 value on EMPTY quantity.", "OK",
                    null, false) );
            } else {
                if (refStringDate == null) {
//                            Toast.makeText(ctx, "Please select delivery date.", Toast.LENGTH_LONG).show();
                    BounceView.addAnimTo( Helper.okDialog(ctx,
                        "Error","Please select delivery date.", "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (alertDialog != null) alertDialog.dismiss();
                                Helper.hideSoftKeyboard(getActivity());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                                        showDatePicker();
                                    }
                                }, 300);
                            }
                        }
                        , false
                    ) );
                } else {
                    if(!isPosted) {
                        isPosted = true;

                        // filter for kiosk of comissary fnc
                        if (Helper.checkBranchProfile(ctx).get(0).getDescription()
                            .equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
                            if (SharedData.getInstance(ctx).getInt(SharedKey.SHOW_SUMMARY_ON_POST.getKey()) == 1) {
                                showSummary("true");
                            } else {
                                if(sp.getInt(SharedKey.ENABLE_REPORT_TYPE.getKey()) == 0) {
                                    if (sp.getInt(SharedKey.REF_PO_NO.getKey()) == 1) {
                                        inputPONumber();
                                    } else {
                                        validateToPost();
                                    }
                                } else {
                                    getReportType();
                                }
                            }
                        } else {
                            if(sp.getInt(SharedKey.ENABLE_REPORT_TYPE.getKey()) == 0) {
                                validateToPost();
                            } else {
                                getReportType();
                            }
                        }
                    }
                }
            }
        }else{
            BounceView.addAnimTo( Helper.okDialog(ctx,
                "Error","Please add an item.", "CLOSE",
                null, false) );
        }
    }

    private void showSummary(String is_posting) {
        if (Helper.isNetworkAvailable(ctx)) {
            LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx)
                .searchOrderFilterMultiple(OrderKey.ITEM_NAME.getKey() + " LIKE ? AND ("
                + OrderKey.QUANTITY.getKey() + " != '' AND " + OrderKey.QUANTITY.getKey() + " != '0')",
                new String[] { "%" }, " ORDER BY " +  OrderKey.ITEM_NAME.getKey() + " ASC" );
            if (llOrderRs.size() > 0) {
                loader = Helper.showSpinnerDialog(ctx, "", "Requesting updated values. Please wait..."); loader.show();
                ArrayList<HashMap> dArrLst = new ArrayList();
                for (int i = 0; i < llOrderRs.size(); i++) {
                    Order rowOl = llOrderRs.get(i);
                    if(!rowOl.getQuantity().equals("") && !rowOl.getQuantity().equals("0")) {
                        LinkedHashMap<String, Object> dMap = new LinkedHashMap();
                        dMap.put("recid", rowOl.getItemRecid());
                        dArrLst.add(dMap);
                    }
                }
                HashMap<String, Object> hmParams = new HashMap();
                hmParams.put("itemrecids", dArrLst);

                HashMap<String, String> prmx = new HashMap<>();
                prmx.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
                prmx.put("customerid", sp.getData(SharedKey.ORDER_CUSTOMER_ID.getKey()));
//                prmx.put("area_recid", "10006");
                Iterator it = prmx.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next(); strParams = strParams + pair.getKey() + "=" + pair.getValue() + "&"; it.remove();
                }
                VolleyInteractor vgis = new VolleyInteractor();
                vgis.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(String response, String type) {
                        isPosted = false;
                        new getItemsSrp().execute(response, is_posting);
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        isPosted = false;
                        loader.dismiss();
                        Toast.makeText(ctx, "Request Item SRP Error, please contact IT support.", Toast.LENGTH_SHORT).show();
                    }
                });
                vgis.getItemlistSrp(ctx, prmx, strParams, new JSONObject(hmParams).toString());
            } else {
                isPosted = false;
                Toast.makeText(ctx, "No summary to display. Please input quantity on item/s", Toast.LENGTH_SHORT).show();
            }
        } else {
            isPosted = false;
            Toast.makeText(ctx, "Request failed. Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSetZero() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setCancelable(false);

        BounceView.addAnimTo(
            builder.setTitle("Set Zero Update").setMessage("Are you sure you want all empty quantities turn to ZERO?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DcOrder.getInstance(ctx).updateSetAllQuantity("0", "quantity == ''");
                        LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).getOrderlist();
                        showItems(llOrderRs);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isPosted = false;
                    }
                })
                .show()
        );
    }

    private void callRefreshItems() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setCancelable(false);

        BounceView.addAnimTo(
            builder.setTitle("Update Records").setMessage("Are you sure you want to UPDATE PRELOADED list? All ITEMS BELOW will RESET.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Helper.isNetworkAvailable(ctx)) {
                            Toast.makeText(ctx, "Fetch items failed. Please check internet connection.", Toast.LENGTH_SHORT).show(); return;
                        }

                        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);

                        curRefArrayList = new LinkedList<Order>();
                        curRefArrayListErr = new LinkedList<Order>();

                        mainTableBox = (LinearLayout) rootView.findViewById(R.id.actual_table_box);
                        mainTableBox.post(new Runnable() {
                            @Override
                            public void run() {
                                maintableViewHeight = mainTableBox.getHeight();
                                maintableViewWidth = mainTableBox.getWidth();
                                try {
                                    tblContentBox = new LinearLayout(ctx);
                                    tblContentBox.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                    mainTableBox.addView(tblContentBox);
                                } finally {
                                    LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_table_content, null);
                                    tl = (TableLayout) ll.findViewById(R.id.checklist_table_layout);
                                    tl.setLayoutParams(new LinearLayout.LayoutParams(maintableViewWidth,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    tblContentBox.addView(ll);
                                }
                            }
                        });

                        fillItems(rootView);

                        tv_grandtotal.setText("0.00");
                        tv_grandtotal_count.setText("0.00");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isPosted = false;
                    }
                })
                .show()
        );
    }

    View.OnClickListener cancelCallback = new View.OnClickListener() {
        public void onClick(View v) {
            Helper.hideSoftKeyboard(getActivity());
            Helper.dismissSpinnerDialog(loader);
            if (alertDialogRemarks != null ) alertDialogRemarks.dismiss();
            if (alertDialogPONumber != null ) alertDialogPONumber.dismiss();
        }
    };

    private void inputPONumber() {
        if (refMenulist.getInvoice().equals("false")) {
            validateToPost();
        } else {
            alertDialogPONumber = okCancelInputDialog(ctx,
                "Please input PO Number.",
                "DONE", null,
                "CANCEL", cancelCallback);
            BounceView.addAnimTo(alertDialogPONumber);
        }
    }

    private void validateToPost() {
        isPosted = false;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setCancelable(false);
        BounceView.addAnimTo( builder.setTitle("Post Transaction").setMessage("Are you sure want to post this transaction?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!isPosted) {
                        isPosted = true;
                        submitOrders();
                    }
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isPosted = false;
                    Helper.dismissSpinnerDialog(loader);
                }
            })
            .show()
        );
        new android.os.Handler().postDelayed( new Runnable() { public void run() { isPosted = false; }}, 500 );
    }

    private String reportType = "0", referenceCustomerPo = "";
    private void getReportType() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setCancelable(false);
        BounceView.addAnimTo(
            builder.setTitle("Report Type Option").setMessage("Please select REPORT TYPE.")
            .setPositiveButton("SALES INVOICE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!isPosted) {
                        isPosted = true;
                        reportType = "1";
                        dialog.dismiss();
                        validateToPost();
                    }
                }
            })
            .setNegativeButton("DELIVERY RECEIPT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!isPosted) {
                        isPosted = true;
                        reportType = "0";
                        dialog.dismiss();
                        validateToPost();
                    }
                }
            })
            .show()
        );
        new android.os.Handler().postDelayed( new Runnable() { public void run() { isPosted = false; }}, 500 );
    }

    private Boolean isPosted = false;
    private void submitOrders() {
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(this);

        loader = Helper.showSpinnerDialog(ctx, "", "Posting... Please wait..."); loader.show();
        final HashMap<String, Object> paramsArray = new HashMap();
        paramsArray.clear();

//        LinkedList<Order> ol =  DcOrder.getInstance(ctx).getOrderlistAsc();
        final ArrayList<HashMap> detailsArrayList = new ArrayList();
        final ArrayList<HashMap> detailsArrayListC = new ArrayList();
        if (curRefArrayList.size() > 0) {
            Double dGT = 0.00, dGTc = 0.00;
            Boolean errFlag = false, zeroErrFlag = true;
            for (int i = 0; i < curRefArrayList.size(); i++) {
                Order rowOl = curRefArrayList.get(i);
                if(!rowOl.getQuantity().equals("") && !rowOl.getQuantity().equals("0")) {
                    LinkedHashMap<String, Object> detailMap = new LinkedHashMap();
                    String rqty = rowOl.getQuantity().equals("") ? "0" : rowOl.getQuantity();
                    rqty = rqty.replace(",", "");
                    if (!rqty.equals("0")) { zeroErrFlag = false; }
                    detailMap.put("quantity", rqty);
                    String rfqty = rowOl.getFree().equals("") ? "0" : rowOl.getFree();
                    rfqty = rfqty.replace(",", "");
                    detailMap.put("free", rfqty);
                    detailMap.put("item_recid", rowOl.getItemRecid());
                    detailMap.put("remarks", rowOl.getRemarks());
                    detailMap.put("old_sku", rowOl.getOldSku().equals("null") ? "0" : rowOl.getOldSku());
                    detailMap.put("selling_price", rowOl.getSellingPrice());
                    String rtotal = rowOl.getTotal().replace(",", "");
                    detailMap.put("total", rtotal);
                    detailsArrayList.add(detailMap);

                    detailMap.put("unitName", rowOl.getUnitName());
                    detailMap.put("itemname", rowOl.getItemName());
                    detailMap.put("is_checked", false);
                    detailMap.put("is_error", false);
                    detailMap.put("is_locked", false);
                    detailsArrayListC.add(detailMap);

                    dGT = dGT + Double.parseDouble(rtotal);
                    dGTc = dGTc + Double.parseDouble(rqty);
                }
            }

            if (zeroErrFlag) {
                Helper.dismissSpinnerDialog(loader);
                BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Error","Invalid POST! All items has a ZERO quantity.",
                    "CLOSE", null, false) );
                isPosted = false;
                return;
            }

            if(!errFlag) {
                SharedData sp = SharedData.getInstance(ctx);
//                sp.saveData(SharedKey.DOMAIN_SERVER_URL.getKey(), "http://beta.apics.fncnathaniel.com/");
//                sp.saveData(SharedKey.DATABASE.getKey(), "beta");

                paramsArray.put("details", detailsArrayList);

                LinkedHashMap<String, String> headersMap = new LinkedHashMap();
                headersMap.put("companydb", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
                headersMap.put("customer_recid", refMenulist.getCustomerID());
                headersMap.put("customer_integ_recid", refMenulist.getCustomerIntegrationId());
                headersMap.put("deliver_date", refStringDate);
                headersMap.put("remarks", et_remarks.getText().toString().trim());
                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                    headersMap.put("createdby", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    headersMap.put("createdby", sp.getData(SharedKey.EMP_NO.getKey()));
                }
                String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                headersMap.put("pcortab_id", android_id);

                if (Helper.checkBranchProfile(ctx).get(0).getDescription().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
//                if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                    headersMap.put("order_type", "1"); // int (customer order: 1 / store order: 2)
                } else {
                    headersMap.put("order_type", "2"); // int (customer order: 1 / store order: 2)
                }
                headersMap.put("report_type", reportType );
                headersMap.put("refcustomerpo", referenceCustomerPo );
//                headersMap.put("reference_employee_no", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                    headersMap.put("reference_employee_no", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    headersMap.put("reference_employee_no", sp.getData(SharedKey.EMP_NO.getKey()));
                }
                headersMap.put("branch_encoding", strBranchEncoding);
                String gt = tv_grandtotal.getText().toString().trim().replace(",", "");
                String gtc = tv_grandtotal_count.getText().toString().trim().replace(",", "");
                headersMap.put("grand_total", sp.getInt(SharedKey.COMPUTE_QTY_ONLY.getKey()) == 1 ? gtc : gtc);
                paramsArray.put("header", headersMap);

                String paramsArrayStr = new JSONObject(paramsArray).toString();

                Ordered ol = new Ordered();
                ol.setCustomerIntegRecid(refMenulist.getCustomerIntegrationId());
                ol.setCustomerRecid(refMenulist.getCustomerID());
                ol.setCustomerName(refMenulist.getCustomerName());
                ol.setDeliveryDate(refStringDate);
//                ol.setCreatedBy(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                    ol.setCreatedBy(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    ol.setCreatedBy(sp.getData(SharedKey.EMP_NO.getKey()));
                }
                ol.setRemarks(et_remarks.getText().toString().trim());
//                ol.setReferenceEmployeeNo(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getInt(SharedKey.REF_EMP_VALIDATION.getKey()) == 1) {
                    ol.setReferenceEmployeeNo(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    ol.setReferenceEmployeeNo(sp.getData(SharedKey.EMP_NO.getKey()));
                }
                ol.setJson(paramsArrayStr);
                ol.setJsonComplete(new JSONArray(detailsArrayListC).toString());
                ol.setGrandtotal(String.valueOf(dGT));
                ol.setGrandtotalcount(String.valueOf(dGTc));
                ol.setDateTime(Helper.getPostingDate());
                ol.setStatus(0);
                ol.setReferenceRecid(Helper.getReqDate(5, ""));
                ol.setDeliver_date_default(Helper.getReqDate(7, refStringDate));

                try {
                    DcOrdered.getInstance(ctx).insertOrderedlist(ol);
                } catch (Exception e) {
                    Helper.dismissSpinnerDialog(loader);
                    BounceView.addAnimTo( Helper.okDialog( ctx,
                        e.getMessage(),"Error on saving to POST items. Please take an SCREENSHOT and contact IT support.",
                        "CLOSE", null, false) );
                    isPosted = false;
                    return;
                }

                Helper.dismissSpinnerDialog(loader);

                if (Helper.checkBranchProfile(ctx).get(0).getDescription().equals(SharedData.getInstance(ctx).getData(SharedKey.REF_MAIN_BRANCH.getKey()))) {
//                if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                    loader = Helper.showSpinnerDialog(ctx, "Posting Transaction", "Please wait..."); loader.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Helper.dismissSpinnerDialog(loader);
                            getActivity().onBackPressed();
                        }
                    }, 2000);
                } else {
                    /* curRefArrayList = new LinkedList<Order>();
                    curRefArrayListErr = new LinkedList<Order>();
                    refMenulist = DcMenulist.getInstance(ctx).searchMenuFilterMultiple(MenulistKey.CUSTOMER_ID.getKey() + " = ?",
                        new String[] { SharedData.getInstance(ctx).getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) } ).get(0);
                    strBranchEncoding = DcBranchlist.getInstance(ctx).searchBranchFilterMultiple(aBranchlistKey.CUSTOMERID.getKey() + " = ?",
                        new String[] { SharedData.getInstance(ctx).getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) } ).get(0).getOld_branchid();

                    et_date.setText("");
                    et_remarks.setText("");
                    tv_grandtotal.setText("0.00");
                    adapter = new OrderlistAdapter(ctx, curRefArrayList);
                    list_view.setAdapter(adapter);
                    isPosted = false; */

                    loader = Helper.showSpinnerDialog(ctx, "Posting Transaction", "Please wait..."); loader.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Helper.dismissSpinnerDialog(loader);
                            Toast.makeText(ctx, "Purchase order save successfully.", Toast.LENGTH_LONG).show();
                            getActivity().finishAndRemoveTask();
                        }
                    }, 2000);
                }

                Log.v("post_params", String.valueOf(paramsArrayStr));
            }
        }else{
            Helper.dismissSpinnerDialog(loader);
//            Toast.makeText(ctx, "Error request. Please try again.", Toast.LENGTH_SHORT).show();
            BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Error","Error request. Please try again.", "CLOSE",
                    null, false) );
            isPosted = false;
        }
    }

    public void onRequestSuccess(String response, String type) {
        isPosted = false;
        try {
            response = response.replace("\r\n", "");
            JSONArray objArr = new JSONArray(response);
            if (type.equals("searchitem")) {
                if(objArr.length() > 0) {
                    DcOrder.getInstance(ctx).emptyOrderlist();
                    List<Itemlist> iRs = new ArrayList<Itemlist>();

                    oldskuerr = false;
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject rowObj = objArr.getJSONObject(i);
                        Itemlist irsx = new Itemlist();
                        irsx.setRecid(rowObj.getLong(ItemlistKey.RECID.getKey()));
                        irsx.setItemName(rowObj.getString(ItemlistKey.ITEM_NAME_WITH_UNIT.getKey()));
                        irsx.setDept(rowObj.getString(ItemlistKey.DEPT.getKey()));
                        irsx.setUnitName(rowObj.getString(ItemlistKey.UNIT.getKey()));
                        irsx.setIsChecked(false);
                        /* sp = SharedData.getInstance(ctx);
                        if (sp.getInt(SharedKey.SKU_VALIDATION.getKey()) == 1) {
                            if (rowObj.getString(ItemlistKey.OLD_SKU.getKey()).equals("null") || rowObj.getString(ItemlistKey.OLD_SKU.getKey()).equals("0")) {
                                oldskuerr = true;
                            }
                        } */
                        irsx.setOldSku(rowObj.getString(ItemlistKey.OLD_SKU.getKey()));
                        irsx.setSellingPrice(rowObj.getString(ItemlistKey.SELLING_PRICE.getKey()));
//                        if (!rowObj.getString(ItemlistKey.OLD_SKU.getKey()).equals("null") && !rowObj.getString(ItemlistKey.OLD_SKU.getKey()).equals("0")) {
                            iRs.add(irsx);
//                        }
                    }

                    ArrayList<HashMap> detailsArrayListC = new ArrayList();
                    for(Itemlist il : iRs) {
                        Order ol = new Order();
                        ol.setQuantity("");
                        ol.setFree("");
                        ol.setItemRecid(String.valueOf(il.getRecid()));
                        ol.setItemName(String.valueOf(il.getItemName()));
                        ol.setUnitName(String.valueOf(il.getUnitName()));
                        ol.setRemarks("");
                        ol.setOldSku(String.valueOf(il.getOldSku()));
                        String strSP = String.valueOf(il.getSellingPrice()).equals("null") ? "0" : String.valueOf(il.getSellingPrice());
                        ol.setSellingPrice(strSP);
                        ol.setSrb("");
                        ol.setTotal("null");
                        ol.setIsChecked(0);
                        ol.setIsError(0);
                        ol.setIsLocked(1);

                        if (SharedData.getInstance(ctx).getInt(SharedKey.SKU_VALIDATION.getKey()) == 1) {
                            if (!il.getOldSku().equals("null") && !il.getOldSku().equals("0")) {
                                curRefArrayList.add(ol);
                            }
                        } else {
                            curRefArrayList.add(ol);
                        }
                        DcOrder.getInstance(ctx).insertOrderlist(ol);

                        // get preloaded
//                        detailMap.put("quantity", "");
                        LinkedHashMap<String, Object> detailMap = new LinkedHashMap();
                        detailMap.put("item_recid", il.getRecid());
                        detailMap.put("itemname", il.getItemName());
                        detailMap.put("unitName", il.getUnitName());
//                        detailMap.put("remarks", "");
                        detailMap.put("old_sku", String.valueOf(il.getOldSku()));
                        detailMap.put("selling_price", strSP);
//                        detailMap.put("total", "null");
//                        detailMap.put("is_checked", false);
//                        detailMap.put("is_error", false);
//                        detailMap.put("is_locked", false);
                        detailsArrayListC.add(detailMap);

                        if (String.valueOf(il.getOldSku()).equals("null")) {
                            curRefArrayListErr.add(ol);
                        }
                    }

                    adapter = new OrderlistAdapter(ctx, curRefArrayList);
                    adapter.setCurPos(curRefPos);
                    adapter.setOnItemClickListener(new OrderlistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view,  int position, int flag) {
                            adapter.setCurPos(position);
                            curRefPos = adapter.getCurPos();
                            Helper.hideSoftKeyboard(getActivity());
                            refQtyOrFree = flag;
                            adapter.setIsFreeClicked(flag);
                            adapter.notifyDataSetChanged();
                            new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    }
                                }, 300
                            );
                        }
                    });
                    adapter.setOnSrbClickListener(new OrderlistAdapter.OnSrbClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            getSrb(position);
                        }
                    });
                    adapter.setOnRemarksClickListener(new OrderlistAdapter.OnRemarksClickListener() {
                        @Override
                        public void onItemClick(View view,  int position) {
                            bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                            adapter.setCurPos(position);
                            alertDialogRemarks = okCancelInputRemarksDialogBuilder(ctx,
                                "Add Remarks",
                                "SAVE", null, "CANCEL", cancelCallback );
                            BounceView.addAnimTo(alertDialogRemarks);
                        }
                    });
                    list_view.setAdapter(adapter);

                    /* if (oldskuerr) {
                        String strSkuMsg = "";
                        for (int i = 0; i < curRefArrayListErr.size(); i++) {
                            Order rowRl = curRefArrayListErr.get(i);
                            String refCnt = String.valueOf(i+1);
                            strSkuMsg = strSkuMsg + "\n " + refCnt + ". " + rowRl.getItemName();
                        }
                        BounceView.addAnimTo( Helper.okDialog(ctx,
                            "Warning","Some items does not have reference SKU, " +
                                    "please contact the developer for assistance.\n" + strSkuMsg, "OK",
                            null, false) );
                    } */

                    if (SharedData.getInstance(ctx).getInt(SharedKey.SKU_VALIDATION.getKey()) == 1) {
                        LinkedList<Order> olRs = DcOrder.getInstance(ctx).searchOrderFilterMultiple(OrderKey.OLD_SKU.getKey() + " = ?", new String[] { "null" }, "");
                        if (olRs.size() > 0) {
                            ((LinearLayout) rootView.findViewById(R.id.btn_others_box)).setVisibility(View.VISIBLE);
                            String strSkuMsg = "";
                            /*for (int i = 0; i < olRs.size(); i++) {
                                Order rowRl = olRs.get(i);
                                String refCnt = String.valueOf(i+1);
                                strSkuMsg = strSkuMsg + "\n " + refCnt + ". " + rowRl.getItemName();
                            }*/
                            BounceView.addAnimTo( Helper.okDialog(ctx,
                                    "Warning","Some items does not have reference SKU, " +
                                            "please contact INVENTORY OFFICERS for assistance.\n" + strSkuMsg, "OK",
                                    null, false) );
                        } else {
                            ((LinearLayout) rootView.findViewById(R.id.btn_others_box)).setVisibility(View.GONE);
                        }
                    }

                    Helper.dismissSpinnerDialog(loader);
                } else {
                    Helper.dismissSpinnerDialog(loader);
                }
            } else {
                Helper.dismissSpinnerDialog(loader);
                if (objArr.length() > 0) {
                    JSONObject obj = objArr.getJSONObject(0);
                    Boolean status = obj.getBoolean("error");
                    if(!status){
                        Toast.makeText(ctx, "Purchase order save successfully.", Toast.LENGTH_LONG).show();
//                        Toast.makeText(ctx, obj.getString("tag"), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }else{
                        Toast.makeText(ctx, "Post error... "+obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx, "Post error... Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            Helper.dismissSpinnerDialog(loader);
            e.printStackTrace();
        }
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        isPosted = false;
        Helper.dismissSpinnerDialog(loader);
        Toast.makeText(ctx, Helper.getVolleyError(volleyError), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PERSON_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    tv_name.setText(extras.getString(PersonsKey.NAME.getKey()));
                    refPersonIdentityId = extras.getString(PersonsKey.IDENTITY_ID.getKey());
                }
                if (resultCode == Activity.RESULT_CANCELED){ }
                break;
            case ITEM_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    curRefPos = adapter != null ? adapter.getCurPos() : -1 ;
                    ArrayList<Itemlist> arrayList = new ArrayList<Itemlist>();
                    arrayList = (ArrayList) extras.get(SharedKey.SEARCHED_ITEMS.getKey());
                    int errFlag = 0; String refItems = ""; int refItemNo = 0;

                    for(Itemlist il : arrayList){
                        LinkedList<Order> xRs = DcOrder.getInstance(ctx)
                            .searchOrderFilterMultiple(OrderKey.ITEM_RECID.getKey() + " = ?",
                                    new String[] { String.valueOf(il.getRecid()) }, "");

                        Order ol = new Order();
                        ol.setQuantity("");
                        ol.setFree("");
                        ol.setItemRecid(String.valueOf(il.getRecid()));
                        ol.setItemName(String.valueOf(il.getItemName()));
                        ol.setUnitName(String.valueOf(il.getUnitName()));
                        ol.setRemarks("");
                        ol.setOldSku(String.valueOf(il.getOldSku()));
                        String strSP = String.valueOf(il.getSellingPrice()).equals("null") ? "0" : String.valueOf(il.getSellingPrice()) ;
                        ol.setSellingPrice(strSP);
                        ol.setSrb("-");
                        ol.setTotal("null");
                        ol.setIsChecked(0);
                        ol.setIsError(0);
                        ol.setIsLocked(0);

                        if (xRs.size() > 0 ) {
                            errFlag = 1;
                            refItemNo++;
                            refItems = refItems + refItemNo + ". " + ol.getItemName() + "\n";
                        } else {
                            curRefArrayList.add(ol);
                            DcOrder.getInstance(ctx).insertOrderlist(ol);
                        }
                    }

                    if (errFlag == 1) {
                        String strMessage = "Some ITEM/s is/are ALREADY EXISTING on the list.\"" +
                            "\n\nITEM NAME:\n" + refItems + "\n\n NOTE: Please also CHECK CONFLICTED ITEM/s.";
                        BounceView.addAnimTo( Helper.okDialog(ctx,
                            "Reminder on some ITEM/s",strMessage, "CLOSE",
                            null, false) );
                    }

                    adapter = new OrderlistAdapter(ctx, curRefArrayList);
                    adapter.setCurPos(curRefPos);
                    adapter.setOnItemClickListener(new OrderlistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view,  int position, int flag) {
                            adapter.setCurPos(position);
                            Helper.hideSoftKeyboard(getActivity());
                            curRefPos = adapter.getCurPos();
                            refQtyOrFree = flag;
                            adapter.setIsFreeClicked(flag);
                            adapter.notifyDataSetChanged();
                            new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    }
                                }, 300
                            );
                        }
                    });
                    adapter.setOnSrbClickListener(new OrderlistAdapter.OnSrbClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            getSrb(position);
                        }
                    });
                    adapter.setOnRemarksClickListener(new OrderlistAdapter.OnRemarksClickListener() {
                        @Override
                        public void onItemClick(View view,  int position) {
                            adapter.setCurPos(position);
                            alertDialogRemarks =  okCancelInputRemarksDialogBuilder(ctx,
                                "Add Remarks",
                                "Save", null, "Cancel", cancelCallback );
                            BounceView.addAnimTo(alertDialogRemarks);
                        }
                    });
                    list_view.setAdapter(adapter);

                    /*for(Itemlist il : arrayList){
                        LinkedList<Order> rlx = DcOrder.getInstance(ctx).getOrderlist(il.getRecid());
                        if(rlx.size() == 0) {
                            TableRow trx = (TableRow) getLayoutInflater().inflate(R.layout.table_row_item_list, null);
                            trx.setId(il.getRecid());
                            trx.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isItemClicked = true;
                                    Helper.hideSoftKeyboard(getActivity());
                                    onTableItemClick(v);
                                    new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                                            }
                                        }, 300
                                    );
                                }
                            });
                            trx.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    onTableItemClick(v);
                                    showMenuPopup(v);
                                    return true;
                                }
                            });

                            TextView tvQty = (TextView) trx.findViewById(R.id.cell_qty);
                            tvQty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isItemClicked = true;
                                    View vx = (View) v.getParent();
                                    Helper.hideSoftKeyboard(getActivity());
                                    onTableItemClick(vx);
                                    new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                                            }
                                        }, 300
                                    );
                                }
                            });
                            tvQty.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    DcOrder.getInstance(ctx).updateOrderlistTallyString(
                                            calcRefId.toString(), s.toString(),false);
                                }
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    TableRow trx = (TableRow) tl.findViewById(calcRefId);
                                    TextView tvQty = (TextView) trx.getChildAt(0);
                                    tvQty.setTextColor(getResources().getColor(R.color.brown_4));
                                    TextView tvUnit = (TextView) trx.getChildAt(1);
                                    tvUnit.setTextColor(getResources().getColor(R.color.brown_4));
                                    LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
                                    TextView tvDesc = (TextView) tvDescBox.getChildAt(0);
                                    tvDesc.setTextColor(getResources().getColor(R.color.brown_4));}
                                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                            });

                            TextView etDesc = (TextView) trx.findViewById(R.id.cell_description);
                            etDesc.setText(il.getItemName());
                            TextView etUnit = (TextView) trx.findViewById(R.id.cell_unit);
                            etUnit.setText(il.getUnitName());
                            TextView etPrice = (TextView) trx.findViewById(R.id.cell_price);
                            if(!il.getSellingPrice().equals("null")) {
                                double u_price = Double.parseDouble(il.getSellingPrice());
                                DecimalFormat df = new DecimalFormat("#.00");
                                etPrice.setText(df.format(u_price));
                            }else{
                                etPrice.setText("0.00");
                            }
                            TextView etTotal = (TextView) trx.findViewById(R.id.cell_total);
                            etTotal.setText("0.00");

                            tl.addView(trx);
                            Order rowO = new Order();
                            rowO.setQuantity("");
                            rowO.setItemRecid(String.valueOf(il.getRecid()));
                            rowO.setItemName(String.valueOf(il.getItemName()));
                            rowO.setUnitName(String.valueOf(il.getUnitName()));
                            rowO.setRemarks("");
                            rowO.setOldSku(String.valueOf(il.getOldSku()));
                            rowO.setSellingPrice(String.valueOf(il.getSellingPrice()));
                            DcOrder.getInstance(ctx).insertOrderlist(rowO);
                        }else{
                            Toast.makeText(ctx, "Some item is already exist on the list", Toast.LENGTH_SHORT).show();
                        }
                    }*/
                }
                if (resultCode == Activity.RESULT_CANCELED){ }
                break;
            case SUMMARY_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if(sp.getInt(SharedKey.ENABLE_REPORT_TYPE.getKey()) == 0) {
                        if (sp.getInt(SharedKey.REF_PO_NO.getKey()) == 1) {
                            inputPONumber();
                        } else {
                            validateToPost();
                        }
                    } else {
                        getReportType();
                    }
                }
                break;
        }
    }

    private void backItNow(final View v) {
        Helper.dismissSpinnerDialog(loader);
        Helper.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    private void initCalc(View v) {
        Button btn0 = (Button) v.findViewById(R.id.cal_0);
        btn0.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("0");
            }
        });
        Button btn1 = (Button) v.findViewById(R.id.cal_1);
        btn1.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("1");
            }
        });
        Button btn2 = (Button) v.findViewById(R.id.cal_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("2");
            }
        });
        Button btn3 = (Button) v.findViewById(R.id.cal_3);
        btn3.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("3");
            }
        });
        Button btn4 = (Button) v.findViewById(R.id.cal_4);
        btn4.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("4");
            }
        });
        Button btn5 = (Button) v.findViewById(R.id.cal_5);
        btn5.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("5");
            }
        });
        Button btn6 = (Button) v.findViewById(R.id.cal_6);
        btn6.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("6");
            }
        });
        Button btn7 = (Button) v.findViewById(R.id.cal_7);
        btn7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("7");
            }
        });
        Button btn8 = (Button) v.findViewById(R.id.cal_8);
        btn8.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("8");
            }
        });
        Button btn9 = (Button) v.findViewById(R.id.cal_9);
        btn9.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally("9");
            }
        });
        Button btnDot = (Button) v.findViewById(R.id.cal_dot);
        btnDot.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                recomputeTally(".");
            }
        });
        Button btnDelete = (Button) v.findViewById(R.id.cal_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                Order ol =  curRefArrayList.get(adapter.getCurPos());
                String refStr = ol.getQuantity();
                if (refQtyOrFree == 1) {
                    refStr = ol.getFree();
                }
                if (refStr.length() > 0) {
                    refStr = refStr.substring(0, refStr.length() - 1);
                }
                if (refQtyOrFree == 0) {
                    ol.setQuantity(refStr);
                } else {
                    ol.setFree(refStr);
                }
                adapter.notifyDataSetChanged();
                recomputeTally("");
            }
        });
        Button btnClose = (Button) v.findViewById(R.id.cal_cancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void recomputeTally(String val) {
        Order ol =  curRefArrayList.get(adapter.getCurPos());

        String refStr = "";
        if (refQtyOrFree == 0) {
            refStr = ol.getQuantity();
        } else {
            refStr = ol.getFree();
        }
        double u_price = Double.parseDouble(ol.getSellingPrice());

        /*TableRow trx = (TableRow) tblContentBox.findViewById(calcRefId);
        TextView tvQty = (TextView) trx.getChildAt(0);
        TextView tvPrice = (TextView) trx.getChildAt(3);
        double u_price = Double.parseDouble(tvPrice.getText().toString());
        TextView tvTotal = (TextView) trx.getChildAt(4);
        String refStr = tvQty.getText().toString();
        Log.v("isItemClicked", isItemClicked.toString());
        if(isItemClicked) {
            refStr = "";
            isItemClicked = false;
        }*/

        if(val.equals(".")) {
            if(!refStr.contains(".")) {
                refStr = refStr+val;
            }
            if(refStr.trim().equals(".")) {
                refStr = "0.";
            }
        }else{
            if(refStr.trim().equals("0")) {

            }else{
                refStr = refStr + val;
            }
        }

        refStr = refStr.replace(",", "");
        DecimalFormat df1 = new DecimalFormat("###.##");
        if (!refStr.equals("")) {
            DecimalFormat df = new DecimalFormat("#,###,###");
            if (!refStr.contains(".")) {
                double u_qty = Double.parseDouble(refStr);
                refStr = String.valueOf(df.format(u_qty));
            } else {
                String[] strSplit = refStr.split("\\.");
                int isplit0 = Integer.parseInt(strSplit[0]);
                if (strSplit.length > 1) {
                    if (strSplit[1].length() > 1) {
                        double refD = Double.parseDouble(String.valueOf(isplit0) + "." + strSplit[1]);
                        DecimalFormat dfd = new DecimalFormat("#,###,###.##");
                        refStr = String.valueOf(dfd.format(refD));
                    } else {
                        refStr = String.valueOf(df.format(isplit0) + "." + strSplit[1]);
                    }
                } else {
                    refStr =  String.valueOf(df.format(isplit0)) + ".";
                }
            }
        }
//        if (refStr.contains(".")) {
//            String[] strSplit = refStr.split("\\.");
//            if (strSplit.length > 1) {
//                double xy = Double.parseDouble(refStr);
//                refStr = df1.format(xy);
//            } else {
//                refStr = strSplit[0] + ".";
//            }
//        }
        if (refQtyOrFree == 0) {
            ol.setQuantity(refStr);
        } else {
            ol.setFree(refStr);
        }

        refStr = refStr.replace(",", "");
        refStr = refStr.equals("") ? "0" : refStr ;
        double u_quantity = Double.parseDouble(refStr);
        double u_total = u_quantity * u_price;
        if (refQtyOrFree == 0) {
            if(u_total == 0.0) {
                ol.setTotal("0.00");
            }else{
                DecimalFormat df2 = new DecimalFormat("#,###,###.00");
                ol.setTotal(df2.format(u_total));
            }
        }

        if (refQtyOrFree == 1) {
            DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.FREE, ol.getFree());
        }
        DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.QUANTITY, ol.getQuantity());
        DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.TOTAL, ol.getTotal());

        adapter.notifyDataSetChanged();

        // recompute grand total
        recomputeGrandTotal();
    }

    private void recomputeGrandTotal() {
        if (refQtyOrFree == 0) {
            LinkedList<Order> refOS = curRefArrayList; //DcOrder.getInstance(ctx).getOrderlistAsc();
            if(refOS.size() > 0) {
                double gt = 0.00, ct = 0.00;
                for (int i = 0; i < refOS.size(); i++) {
                    Order olx = refOS.get(i);
                    String refCountTotal = String.valueOf(olx.getQuantity().trim()).replace(",", "");
                    ct = refCountTotal.equals("") || refCountTotal.equals("null") ? ct + 0.0 : ct + Double.parseDouble(refCountTotal) ;
                    String refTotal = String.valueOf(olx.getTotal().trim()).replace(",", "");
                    gt = refTotal.equals("") || refTotal.equals("null") ? gt + 0.0 : gt + Double.parseDouble(refTotal) ;
                }
                DecimalFormat df = new DecimalFormat("#,###,###.00");

                tv_grandtotal.setText(df.format(gt).equals(".00") ? "0.00" : df.format(gt));
                tv_grandtotal_count.setText(df.format(ct).equals(".00") ? "0.00" : df.format(ct));
            } else {
                tv_grandtotal.setText("0.00");
                tv_grandtotal_count.setText("0.00");
            }
        }
    }

    private void showMenuPopup(View v) {
        try {
            if(menuPop != null) menuPop.dismiss();
        } finally {
            menuPop = new PopupMenu(ctx);
            menuPop.showOnAnchor(v,
                    RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, -5 , 0);
            menuPop.getBtnRemarks().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuPop.dismiss();
                    bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                    alertDialogRemarks = okCancelInputRemarksDialogBuilder(ctx,
                        "Add Remarks",
                        "SAVE", null, "CANCEL", cancelCallback );
                    BounceView.addAnimTo( alertDialogRemarks );
                }
            });
            menuPop.getBtnDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    triggerDeleteItem(v);
                    menuPop.dismiss();
                }
            });
            calcRefId = v.getId();
        }
    }

    private void triggerDeleteItem(final View v) {
        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setTitle("Remove Item").setMessage("Are you sure you want to remove this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ctx, "Item deleted....", Toast.LENGTH_SHORT).show();
                        tl.removeView((TableRow) tl.findViewById(calcRefId));
                        DcOrder.getInstance(ctx).deleteOrderItemViaId(String.valueOf(calcRefId));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private AlertDialog okCancelInputRemarksDialogBuilder(final Context activity, String message,
          String okButtonCaption, View.OnClickListener onClickListener,
          String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_remarks_dialog, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        final EditText etText = (EditText) layout.findViewById(R.id.et_remarks);
        Order ol =  curRefArrayList.get(adapter.getCurPos());
        etText.setText(ol.getRemarks());

        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogRemarks.dismiss();
                Toast.makeText(activity, "Remarks saved successfully.", Toast.LENGTH_SHORT).show();
                Order ol =  curRefArrayList.get(adapter.getCurPos());
                ol.setRemarks(etText.getText().toString());
                DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.REMARKS, ol.getRemarks());
                adapter.notifyDataSetChanged();
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
        btnCancel.setText(cancelButtonCaption);
        btnCancel.setOnClickListener(cancelClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private AlertDialog okCancelInputDialog(final Context activity, String message,
          String okButtonCaption, View.OnClickListener onClickListener,
          String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_dialog_b, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        final EditText etText = (EditText) layout.findViewById(R.id.et_inputtext);

        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                referenceCustomerPo = String.valueOf(etText.getText());
                if (referenceCustomerPo.trim().equals("")) {
                    Toast.makeText(ctx, "Invalid input value!", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialogPONumber.dismiss();
                    validateToPost();
                }
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
        btnCancel.setText(cancelButtonCaption);
        btnCancel.setOnClickListener(cancelClickListener);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (!et_date.getText().toString().equals("")){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
                c.setTime(sdf.parse(et_date.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            year = c.get(Calendar.YEAR); month = c.get(Calendar.MONTH); day = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialogFragment calendar = new DatePickerDialogFragment();
        calendar.setDate(year, month, day);
        calendar.show(getFragmentManager(), "");
        calendar.setDatePickerListener(new DatePickerListener() {
            @Override
            public void didPickDate(Context context, Date date) {
                refStringDate = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_POST).format(date);
                refDate = new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT).format(date);
                et_date.setText(new SimpleDateFormat(GlobalConstants.DATE_FORMAT).format(date));

                Calendar c = Calendar.getInstance();
                int curday = c.get(Calendar.DAY_OF_MONTH);
                SimpleDateFormat sdf = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
                try {
                    c.setTime(sdf.parse(refStringDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int prevday = c.get(Calendar.DAY_OF_MONTH);
            }
        });
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
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.length() > 0) {
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
                                            rowObj.getString("pass"),
                                            rowObj.getString("active"),
                                            rowObj.getString("ismobileadmin")
                                        );
                                        DcStaffs.getInstance(ctx).insertStaffs(sl);
                                    }
                                    Helper.insertDefaultStaffs(ctx);
                                }
                                loadAdminJobTitles();
                            } else {
//                                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
//                            Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
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

    private void requestProductItems() {
        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        BounceView.addAnimTo(
            builder.setTitle("Update Records").setMessage("Are you sure want to UPDATE PRODUCT ITEM RECORDS? All RECORDS will RESET.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Helper.isNetworkAvailable(ctx)) {
                            Toast.makeText(ctx, "Fetch items failed. Please check internet connection.", Toast.LENGTH_SHORT).show(); return;
                        }

                        loader = Helper.showSpinnerDialog(ctx, "", "Updating PRODUCT ITEM RECORDS... Please wait..."); loader.show();

                        VolleyInteractor vi = new VolleyInteractor();
                        vi.registerCallback(new VolleyCallback() {
                            @Override
                            public void onRequestSuccess(String response, String type) {
                                new requestProductItemsAsync().execute(response);
                            }

                            @Override
                            public void onRequestFail(VolleyError response, String type) {
                                Helper.dismissSpinnerDialog(loader);
                                Toast.makeText(ctx, "Fetch failed. Please contact IT support.", Toast.LENGTH_SHORT).show(); return;
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
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isPosted = false;
                    }
                })
                .show()
        );

    }

    private class requestProductItemsAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String response = params[0].replace("\r\n ", "");
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
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
            Helper.dismissSpinnerDialog(loader);
            Toast.makeText(ctx, "Records updated successfully.", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    private class getItemsSrp extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String response = params[0].replace("\r\n", "");
                String is_posting = params[1];
                JSONArray objArr = new JSONArray(response);
                if(objArr.length() > 0) {
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject rowObj = objArr.getJSONObject(i);
                        String strSRP = rowObj.getString(aItemlistKey.SELLING_PRICE.getKey());
                        strSRP = strSRP.replace(",", "");
                        String irecid = String.valueOf(rowObj.getLong(aItemlistKey.RECID.getKey()));
                        String strSRB = String.valueOf(rowObj.getString(OrderKey.SRB.getKey()));

                        LinkedList<Order> xRs = DcOrder.getInstance(ctx).searchOrderFilterMultiple(OrderKey.ITEM_RECID.getKey() + " = ?",
                            new String[] { irecid }, "");
                        if (xRs.size() > 0) {
                            Order oRs = xRs.get(0);
                            DcOrder.getInstance(ctx).updateOrderlist(irecid, OrderKey.SELLING_PRICE, strSRP);
                            String strQty = oRs.getQuantity(); strQty = strQty.replace(",", "");
                            if (strSRP.equals("null")) { strSRP = "0"; }
                            double u_total = Double.parseDouble(strQty) * Double.parseDouble(strSRP);
                            DcOrder.getInstance(ctx).updateOrderlist(irecid, OrderKey.TOTAL, String.valueOf(u_total));
                            DcOrder.getInstance(ctx).updateOrderlist(irecid, OrderKey.SRB, strSRB);
                        }
                    }
                }
                return "Task Done|" + is_posting;
            } catch (JSONException e) {
                e.printStackTrace();
                return "Task Error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String[] resArr = result.split("\\|");
            if (resArr[1] != null)
            loader.dismiss();
            LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).searchOrderFilterMultiple(OrderKey.ITEM_NAME.getKey() + " LIKE ?",
                new String[] { "%" }, " ORDER BY " +  OrderKey.ITEM_NAME.getKey() + " ASC" );
            ArrayList<HashMap> refArray = new ArrayList();
            if (llOrderRs.size() > 0) {
                Boolean errFlag = false, zeroErrFlag = true;
                for (int i = 0; i < llOrderRs.size(); i++) {
                    Order rowOl = llOrderRs.get(i);
                    if(!rowOl.getQuantity().equals("") && !rowOl.getQuantity().equals("0")) {
                        LinkedHashMap<String, Object> detailMap = new LinkedHashMap();
                        String rqty = rowOl.getQuantity().equals("") ? "0" : rowOl.getQuantity();
                        rqty = rqty.replace(",", "");
                        if (!rqty.equals("0")) { zeroErrFlag = false; }
                        detailMap.put("quantity", rqty);
                        detailMap.put("free", rowOl.getFree());
                        detailMap.put("item_recid", rowOl.getItemRecid());
                        detailMap.put("remarks", rowOl.getRemarks());
                        detailMap.put("old_sku", rowOl.getOldSku().equals("null") ? "0" : rowOl.getOldSku());
                        detailMap.put("selling_price", rowOl.getSellingPrice());
                        detailMap.put("SRB", rowOl.getSrb());
                        String rtotal = rowOl.getTotal().replace(",", "");
                        detailMap.put("total", rtotal);
                        detailMap.put("unitName", rowOl.getUnitName());
                        detailMap.put("itemname", rowOl.getItemName());
                        detailMap.put("is_checked", false);
                        detailMap.put("is_error", false);
                        detailMap.put("is_locked", false);
                        refArray.add(detailMap);
                    }
                }
            }

            DialogFragment dialogFrag = TrasactionItemsFragment.searchInstance();
            Bundle args = new Bundle();
            args.putString("details", new JSONArray(refArray).toString());
            args.putString("is_posting", resArr[1] != null ? resArr[1] : "");
            dialogFrag.setArguments(args);
            dialogFrag.setTargetFragment(thisFragment, SUMMARY_DIALOG_FRAGMENT);
            dialogFrag.setCancelable(false);
            dialogFrag.show(getActivity().getSupportFragmentManager(), "dialog_search_item");
        }
        @Override
        protected void onPreExecute() { }
        @Override
        protected void onProgressUpdate(Integer... values) { }
    }

}
