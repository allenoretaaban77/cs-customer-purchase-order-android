package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.fnc.order.android.datacontroller.DcBranchlist;
import com.fnc.order.android.datacontroller.DcMenulist;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.PersonsKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.listeners.DatePickerListener;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.model.aBranchlist;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.DatePickerDialogFragment;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PopupMenu;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.fnc.order.android.utilities.RelativePopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

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

public class OrderFragment extends Fragment implements VolleyCallback {

    private static final int PERSON_DIALOG_FRAGMENT = 7;
    private static final int ITEM_DIALOG_FRAGMENT = 8;

    public Context ctx;
    private View rootView;
    private Fragment thisFragment;
    private ProgressDialog loader;
    private MaterialRippleLayout btn_add_item, btn_submit, btn_menu, btn_back, btn_refresh, btn_others;
    private TextView tv_name, tv_grandtotal, tv_grandtotal_lbl, tv_header_price, tv_header_total;
    private EditText et_date;
    private CoordinatorLayout rl_content_box;
    private EditText et_remarks;
    private DroppyMenuPopup.Builder pageMenu;
    private LinearLayout mainTableBox;
    private Integer maintableViewHeight = 0;
    private Integer maintableViewWidth = 0;
    private LinearLayout tblContentBox;
    private TableLayout tl;
    private Integer calcRefId = 0;
    private String refPersonIdentityId = "";
    private static AlertDialog alertDialog, alertDialogRemarks;
    private Boolean isBacked = false;
    private LinearLayout bsCalc;
    private BottomSheetBehavior bsBh;
    private PopupMenu menuPop;
    private VolleyInteractor vi;
    private Boolean isItemClicked = false;
    private String refDate, refStringDate, strBranchEncoding;
    private ListView list_view;
    private OrderlistAdapter adapter;
    private SwipeLayout swipeLayout;
    private LinkedList<Order> curRefArrayList;
    private Integer curRefPos = 0;
    private Boolean oldskuerr = false;
    private LinkedList<Order> curRefArrayListErr;
    private SharedData sp;
    private MenuList refMenulist;
    private static final int USER_DIALOG_FRAGMENT = 7;
    private static final int OTHER_ITEMS_DIALOG_FRAGMENT = 8;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ctx = rootView.getContext();
        thisFragment = this;
        Helper.setPreviousPage(ctx, "customer_fragment");

        curRefArrayList = new LinkedList<Order>();
        curRefArrayListErr = new LinkedList<Order>();

        LinkedList<MenuList> mlRs = DcMenulist.getInstance(ctx).searchMenuFilterMultiple(
                MenulistKey.CUSTOMER_ID.getKey() + " = ?",
                new String[] { SharedData.getInstance(ctx).getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) } );
        if (mlRs.size() > 0 ) {
            refMenulist = DcMenulist.getInstance(ctx).searchMenuFilterMultiple(
                MenulistKey.CUSTOMER_ID.getKey() + " = ?",
                new String[] { SharedData.getInstance(ctx).getData(SharedKey.ORDER_CUSTOMER_ID.getKey()) }
            ).get(0);
        }

        if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
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
                    if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
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

        sp = SharedData.getInstance(ctx);
        sp.saveInt(SharedKey.PRELOAD_ITEMS.getKey(), 1);
        if (sp.getInt(SharedKey.PRELOAD_ITEMS.getKey()) == 1) {
            DcOrder.getInstance(ctx).updateSetAllQuantity("");
            fillItems(rootView);
        } else {
            DcOrder.getInstance(ctx).emptyOrderlist();
        }

        return rootView;
    }

    private void fillItems(View v) {
        if (Helper.isNetworkAvailable(ctx)) {
            final SharedData sp = SharedData.getInstance(ctx);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);

            showSpinnerDialog(v);
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
            if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                Toast.makeText(ctx, "Please check internet connection....", Toast.LENGTH_SHORT).show();
            } else {
//                try {
                    LinkedList<Order> llOrderRs = DcOrder.getInstance(ctx).getOrderlist();
                    if (llOrderRs.size() > 0) {
                        for (int i = 0; i < llOrderRs.size(); i++) {
                            Order ol = llOrderRs.get(i);
                            if (!ol.getOldSku().equals("null") && !ol.getOldSku().equals("0")) {
                                curRefArrayList.add(ol);
                            }
                        }

                        adapter = new OrderlistAdapter(ctx, curRefArrayList);
                        adapter.setCurPos(curRefPos);
                        adapter.setOnItemClickListener(new OrderlistAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                adapter.setCurPos(position);
                                adapter.notifyDataSetChanged();
                                bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                                curRefPos = adapter.getCurPos();
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
                    } else {
                        Toast.makeText(ctx, "Error fetching offline items", Toast.LENGTH_SHORT).show();
                    }
//                } catch (JSONException e) {
//                    Toast.makeText(ctx, "Error fetching offline items", Toast.LENGTH_SHORT).show();
//                }
            }

        }
    }

    private void initViews(View v) {
        btn_refresh = (MaterialRippleLayout) v.findViewById(R.id.btn_refresh);
        btn_others = (MaterialRippleLayout) v.findViewById(R.id.btn_others);
        btn_add_item = (MaterialRippleLayout) v.findViewById(R.id.btn_add_item);
        btn_back = (MaterialRippleLayout) v.findViewById(R.id.btn_back);
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_name.setText(refMenulist.getCustomerName());
        rl_content_box = (CoordinatorLayout) v.findViewById(R.id.content_box);
        et_date = (EditText) v.findViewById(R.id.et_date);
        btn_menu  = (MaterialRippleLayout) v.findViewById(R.id.btn_menu);
        tv_header_price = (TextView) v.findViewById(R.id.tv_header_price);
        tv_header_total = (TextView) v.findViewById(R.id.tv_header_total);

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
        tv_grandtotal_lbl = (TextView) v.findViewById(R.id.tv_grandtotal_lbl);

        if(!Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") && !Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
            ((RelativeLayout) v.findViewById(R.id.rl_back_box)).setVisibility(View.GONE);
        }

        if (SharedData.getInstance(getContext()).getData(SharedKey.DATABASE.getKey()).equals(
                SharedData.getInstance(getContext()).getData(SharedKey.REF_DATABASE.getKey()).trim())) {
            tv_grandtotal_lbl.setText("Count Total:");
            tv_header_price.setVisibility(View.GONE);
            tv_header_total.setVisibility(View.GONE);
        } else {
            tv_grandtotal_lbl.setText("Grand Total:");
            tv_header_price.setVisibility(View.VISIBLE);
            tv_header_total.setVisibility(View.VISIBLE);
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
                        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
                        showDatePicker();
                    }
                }, 300);
            }
        });
        et_remarks.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
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

        pageMenu.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                switch(id){
                    case 1:
                        Helper.changePage(ctx, getActivity().getSupportFragmentManager(),
                            new TransactionFragment(), "transaction_fragment", "order_fragment");
                        break;
                    case 2:
                        callRefreshItems();
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
            }
        }).build();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {

                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    if (sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-1") || sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-2")) {
                        BounceView.addAnimTo( Helper.okDialog(ctx,
                            "Error","Your account is not valid to process this request. Please contact IT support.", "CLOSE",
                            null, false) );
                        return;
                    }
                } else {
                    if (sp.getData(SharedKey.REF_EMP_NO.getKey()).equals("-2")) {
                        BounceView.addAnimTo( Helper.okDialog(ctx,
                                "Error","Your account is not valid to process this request. Please contact IT support.", "CLOSE",
                                null, false) );
                        return;
                    }
                }

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
                        dismissSpinnerDialog();
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
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
                            } else {
                                builder = new AlertDialog.Builder(ctx);
                            }
                            builder.setCancelable(false);
                            BounceView.addAnimTo(
                                builder.setTitle("Post Transaction").setMessage("Are you sure want to post this transaction?")
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
                                    }
                                })
                                .show()
                            );
                        }
                    }
                }else{
//                    Toast.makeText(ctx, "Please add an item.", Toast.LENGTH_LONG).show();
                    BounceView.addAnimTo( Helper.okDialog(ctx,
                            "Error","Please add an item.", "CLOSE",
                            null, false) );
                }
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                callRefreshItems();
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
            builder.setTitle("Post Transaction").setMessage("Are you sure want to refresh the list? All items will reset.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Helper.isNetworkAvailable(ctx)) {
                            Toast.makeText(ctx, "Fetch items  process failed. Please check internet connection.", Toast.LENGTH_SHORT).show(); return;
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
            dismissSpinnerDialog();
            alertDialogRemarks.dismiss();
        }
    };

    private Boolean isPosted = false;
    private void submitOrders() {
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(this);

        showSpinnerDialog(rootView);
        final HashMap<String, Object> paramsArray = new HashMap();
        paramsArray.clear();

//        LinkedList<Order> ol =  DcOrder.getInstance(ctx).getOrderlistAsc();
        final ArrayList<HashMap> detailsArrayList = new ArrayList();
        final ArrayList<HashMap> detailsArrayListC = new ArrayList();
        if (curRefArrayList.size() > 0) {
            Boolean errFlag = false;
            for (int i = 0; i < curRefArrayList.size(); i++) {
                Order rowOl = curRefArrayList.get(i);
                if(!rowOl.getQuantity().equals("") && !rowOl.getQuantity().equals("0")) {
                    LinkedHashMap<String, Object> detailMap = new LinkedHashMap();
                    String rqty = rowOl.getQuantity().equals("") ? "0" : rowOl.getQuantity();
                    rqty = rqty.replace(",", "");
                    detailMap.put("quantity", rqty);
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
                }
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
                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    headersMap.put("createdby", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    headersMap.put("createdby", sp.getData(SharedKey.EMP_NO.getKey()));
                }
                String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                headersMap.put("pcortab_id", android_id);

                if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
                    headersMap.put("order_type", "1"); // int (customer order: 1 / store order: 2)
                } else {
                    headersMap.put("order_type", "2"); // int (customer order: 1 / store order: 2)
                }
//                headersMap.put("reference_employee_no", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    headersMap.put("reference_employee_no", sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    headersMap.put("reference_employee_no", sp.getData(SharedKey.EMP_NO.getKey()));
                }
                headersMap.put("branch_encoding", strBranchEncoding);
                String gt = tv_grandtotal.getText().toString().trim().replace(",", "");
                headersMap.put("grand_total", gt);
                paramsArray.put("header", headersMap);

                String paramsArrayStr = new JSONObject(paramsArray).toString();

                Ordered ol = new Ordered();
                ol.setCustomerIntegRecid(refMenulist.getCustomerIntegrationId());
                ol.setCustomerRecid(refMenulist.getCustomerID());
                ol.setCustomerName(refMenulist.getCustomerName());
                ol.setDeliveryDate(refStringDate);
//                ol.setCreatedBy(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    ol.setCreatedBy(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    ol.setCreatedBy(sp.getData(SharedKey.EMP_NO.getKey()));
                }
                ol.setRemarks(et_remarks.getText().toString().trim());
//                ol.setReferenceEmployeeNo(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    ol.setReferenceEmployeeNo(sp.getData(SharedKey.REF_EMP_NO.getKey()));
                } else {
                    ol.setReferenceEmployeeNo(sp.getData(SharedKey.EMP_NO.getKey()));
                }
                ol.setJson(paramsArrayStr);
                ol.setJsonComplete(new JSONArray(detailsArrayListC).toString());
                ol.setGrandtotal(gt);
                ol.setDateTime(Helper.getPostingDate());
                ol.setStatus(0);
                ol.setReferenceRecid(Helper.getReqDate(5, ""));
                DcOrdered.getInstance(ctx).insertOrderedlist(ol);

                dismissSpinnerDialog();

                if(Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(ctx).get(0).getDescription().equals("Main")) {
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
            dismissSpinnerDialog();
//            Toast.makeText(ctx, "Error request. Please try again.", Toast.LENGTH_SHORT).show();
            BounceView.addAnimTo( Helper.okDialog( ctx,
                    "Error","Error request. Please try again.", "CLOSE",
                    null, false) );
            isPosted = false;
        }
    }

    public void onRequestSuccess(String response, String type) {
        isPosted = false;
        dismissSpinnerDialog();
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
                        irsx.setRecid(rowObj.getInt(ItemlistKey.RECID.getKey()));
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
                        ol.setItemRecid(String.valueOf(il.getRecid()));
                        ol.setItemName(String.valueOf(il.getItemName()));
                        ol.setUnitName(String.valueOf(il.getUnitName()));
                        ol.setRemarks("");
                        ol.setOldSku(String.valueOf(il.getOldSku()));
                        String strSP = String.valueOf(il.getSellingPrice()).equals("null") ? "0" : String.valueOf(il.getSellingPrice());
                        ol.setSellingPrice(strSP);
                        ol.setTotal("null");
                        ol.setIsChecked(0);
                        ol.setIsError(0);
                        ol.setIsLocked(1);
                        if (!il.getOldSku().equals("null") && !il.getOldSku().equals("0")) {
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
                        public void onItemClick(View view,  int position) {
                            adapter.setCurPos(position);
                            adapter.notifyDataSetChanged();
                            bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                            curRefPos = adapter.getCurPos();
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

                    if (oldskuerr) {
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
                    }

                    if (SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()).equals("BackofficeLive")) {
                        LinkedList<Order> olRs = DcOrder.getInstance(ctx).searchOrderFilterMultiple(OrderKey.OLD_SKU.getKey() + " = ?", new String[] { "null" });
                        if (olRs.size() > 0) {
                            ((LinearLayout) rootView.findViewById(R.id.btn_others_box)).setVisibility(View.VISIBLE);
                        } else {
                            ((LinearLayout) rootView.findViewById(R.id.btn_others_box)).setVisibility(View.GONE);
                        }
                    }
                }
            } else {
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
            e.printStackTrace();
        }
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        isPosted = false;
        dismissSpinnerDialog();
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
                    for(Itemlist il : arrayList){
                        Order ol = new Order();
                        ol.setQuantity("");
                        ol.setItemRecid(String.valueOf(il.getRecid()));
                        ol.setItemName(String.valueOf(il.getItemName()));
                        ol.setUnitName(String.valueOf(il.getUnitName()));
                        ol.setRemarks("");
                        ol.setOldSku(String.valueOf(il.getOldSku()));
                        String strSP = String.valueOf(il.getSellingPrice()).equals("null") ? "0" : String.valueOf(il.getSellingPrice()) ;
                        ol.setSellingPrice(strSP);
                        ol.setTotal("null");
                        ol.setIsChecked(0);
                        ol.setIsError(0);
                        ol.setIsLocked(0);
                        curRefArrayList.add(ol);
                        DcOrder.getInstance(ctx).insertOrderlist(ol);
                    }

                    adapter = new OrderlistAdapter(ctx, curRefArrayList);
                    adapter.setCurPos(curRefPos);
                    adapter.setOnItemClickListener(new OrderlistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view,  int position) {
                            adapter.setCurPos(position);
                            adapter.notifyDataSetChanged();
                            bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
                            curRefPos = adapter.getCurPos();
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
        }
    }

    private void onTableItemClick(final View v) {
        new android.os.Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    calcRefId = v.getId();
                    try {
                        LinkedList<Order> refOS = DcOrder.getInstance(ctx).getOrderlistAsc();
                        for (int i = 0; i < refOS.size(); i++) {
                            Order olx = refOS.get(i);
                            TableRow trx = (TableRow) tblContentBox.findViewById(Integer.parseInt(olx.getItemRecid()));
                            if (trx != null) {
                                TextView tvQty = (TextView) trx.getChildAt(0);
                                tvQty.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
            //                    tvQty.setHighlightColor(getResources().getColor(R.color.transparent));
                                tvQty.setTextColor(getResources().getColor(R.color.black));
                                TextView tvUnit = (TextView) trx.getChildAt(1);
                                tvUnit.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
                                LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
                                tvDescBox.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
                                TextView tvPrice = (TextView) trx.getChildAt(3);
                                tvPrice.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
                                TextView tvTotal = (TextView) trx.getChildAt(4);
                                tvTotal.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
                            }
                        }
                    } finally {
                        TableRow trx = (TableRow) v;
                        TextView tvQty = (TextView) trx.getChildAt(0);
                        tvQty.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background_selected));
                        tvQty.setTextColor(getResources().getColor(R.color.green_5));
                        TextView tvUnit = (TextView) trx.getChildAt(1);
                        tvUnit.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background_selected));
                        LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
                        tvDescBox.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background_selected));
                        TextView tvPrice = (TextView) trx.getChildAt(3);
                        tvPrice.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background_selected));
                        TextView tvTotal = (TextView) trx.getChildAt(4);
                        tvTotal.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background_selected));
                    }
                }
            }, 300
        );
    }

    private void backItNow(final View v) {
        dismissSpinnerDialog();
        Helper.hideSoftKeyboard(getActivity());
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
                if (refStr.length() > 0) {
                    refStr = refStr.substring(0, refStr.length() - 1);
                }
                ol.setQuantity(refStr);
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
        String refStr = ol.getQuantity();
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
                int u_qty = Integer.parseInt(refStr);
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
        ol.setQuantity(refStr);

        refStr = refStr.replace(",", "");
        refStr = refStr.equals("") ? "0" : refStr ;
        double u_quantity = Double.parseDouble(refStr);
        double u_total = u_quantity * u_price;
        if(u_total == 0.0) {
            ol.setTotal("0.00");
        }else{
            DecimalFormat df2 = new DecimalFormat("#,###,###.00");
            ol.setTotal(df2.format(u_total));
        }
        DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.QUANTITY, ol.getQuantity());
        DcOrder.getInstance(ctx).updateOrderlist(ol.getItemRecid(), OrderKey.TOTAL, ol.getTotal());

        adapter.notifyDataSetChanged();

        // recompute grand total
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
            if (SharedData.getInstance(getContext()).getData(SharedKey.DATABASE.getKey()).equals(
                    SharedData.getInstance(getContext()).getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                tv_grandtotal.setText(df.format(ct).equals(".00") ? "0.00" : df.format(ct));
            } else {
                tv_grandtotal.setText(df.format(gt).equals(".00") ? "0.00" : df.format(gt));
            }
        } else {
            tv_grandtotal.setText("0.00");
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
                        Log.d("dsxs getuser", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.length() > 0) {
                                DcStaffs.getInstance(ctx).emptyStaffslist();
                                Helper.insertDefaultStaffs(ctx);
                                JSONArray sArr = obj.getJSONArray("staff");
                                if (sArr.length() > 0) {
                                    for (int i = 0; i < sArr.length(); i++) {
                                        JSONObject rowObj = sArr.getJSONObject(i);
                                        aStaffs sl = new aStaffs(
                                                rowObj.getInt("empId"),
                                                rowObj.getString("refempno").equals("null") ? "-1" : rowObj.getString("refempno"),
                                                rowObj.getString("empNo"),
                                                rowObj.getString("Email"),
                                                rowObj.getString("name"),
                                                rowObj.getInt("Branch"),
                                                rowObj.getInt("Jobtitle"),
                                                rowObj.getString("pass"),
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
