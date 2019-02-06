package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fnc.order.android.R;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcReturn;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.DriversKey;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.enumeration.PersonsKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.Drivers;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.Return;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.PopupCalc;
import com.fnc.order.android.utilities.PopupMenu;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.android.volley.error.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.fnc.order.android.utilities.RelativePopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReturnsFragment extends Fragment implements VolleyCallback {

    private static final int PERSON_DIALOG_FRAGMENT = 7;
    private static final int ITEM_DIALOG_FRAGMENT = 8;

    public Context ctx;
    private View rootView;
    private Fragment thisFragment;
    private ProgressDialog loader;
    private MaterialRippleLayout btn_add_item;
    private MaterialRippleLayout btn_submit;
    private MaterialRippleLayout btn_menu;
    private MaterialRippleLayout btn_back;
    private TextView tv_name;
    private CoordinatorLayout rl_content_box;
    private EditText et_remarks;
    private Spinner et_driver;
    private DroppyMenuPopup.Builder pageMenu;
    private DroppyMenuPopup.Builder driverMenu;
    private LinearLayout mainTableBox;
    private Integer maintableViewHeight = 0;
    private Integer maintableViewWidth = 0;
    private LinearLayout tblContentBox;
    private TableLayout tl;
    private PopupCalc calcPop;
    private Integer calcRefId = 0;
    private String refPersonIdentityId = "";
    private static AlertDialog alertDialog;
    private Boolean isBacked = false;
    private LinearLayout bsCalc;
    private BottomSheetBehavior bsBh;
    private PopupMenu menuPop;
    private String refPassword;
    private String refDriverId;
    private JSONArray driverArray;
    private VolleyInteractor vi;
    private ArrayAdapter<String> spinneradapter;
    private Boolean isItemClicked = false;
    private ArrayList<Drivers> driverList;


    public ReturnsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_returns, container, false);
        ctx = rootView.getContext();
        thisFragment = this;

        mainTableBox = (LinearLayout) rootView.findViewById(R.id.actual_table_box);
        mainTableBox.post(new Runnable() {
            @Override
            public void run() {
                maintableViewHeight = mainTableBox.getHeight();
                maintableViewWidth = mainTableBox.getWidth();
                try {
//                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                    LinearLayout hScrlVw = new LinearLayout(ctx);
//                    mainTableBox.addView(hScrlVw);
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
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    backItNow(v);
                }
                return false;
            }
        } );

        try {
            initViews(rootView);
        } finally {
            initListeners(rootView);
            // empty return table
            DcReturn.getInstance(ctx).emptyReturnlist();

            initCalc(rootView);

//            requestDrivers(rootView);
        }

        return rootView;
    }

    private void initViews(View v) {
        btn_add_item = (MaterialRippleLayout) v.findViewById(R.id.btn_add_item);
        btn_back = (MaterialRippleLayout) v.findViewById(R.id.btn_back);
        tv_name = (TextView) v.findViewById(R.id.tv_name);
        SharedData sd = SharedData.getInstance(ctx);
        tv_name.setText(sd.getData(SharedKey.CURRENT_STORE.getKey()));
        rl_content_box = (CoordinatorLayout) v.findViewById(R.id.content_box);

        btn_menu  = (MaterialRippleLayout) v.findViewById(R.id.btn_menu);
        pageMenu = new DroppyMenuPopup.Builder(ctx, btn_menu);
        pageMenu.setXOffset(8).setYOffset(0);
        pageMenu.addMenuItem(new DroppyMenuItem("  View Transactions  "))
                .addSeparator()
                .addMenuItem(new DroppyMenuItem("  Log-out  "));

        btn_submit = (MaterialRippleLayout) v.findViewById(R.id.btn_submit);
        et_remarks = (EditText) v.findViewById(R.id.et_remarks);

        et_driver = (Spinner) v.findViewById(R.id.et_driver);
        et_driver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TextView tv = (TextView) parentView.getChildAt(0);
                if(position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.gray_5));
                }else {
                    Drivers refLst = driverList.get(position-1);
                    refDriverId = refLst.getEmployeeNumber();
                    tv.setTextColor(getResources().getColor(R.color.red_2));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        bsCalc = (LinearLayout) v.findViewById(R.id.bs_calculator);
        bsCalc.setVisibility(View.VISIBLE);
    }

    private void initListeners(View v) {
        bsBh = BottomSheetBehavior.from(bsCalc);
        bsBh.setHideable(true);
        bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
        bsCalc.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                bsBh.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(isBacked == false) {
                    isBacked = true;
                    getActivity().onBackPressed();
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
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new TransactionFragment()).addToBackStack("transactionlist").commit();
                        break;
                    default:
                        backItNow(v);
                        break;
                }
            }
        }).build();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                Helper.hideSoftKeyboard(getActivity());

                String strDriver = et_driver.getSelectedItem().toString().trim();
                if(strDriver.equals("") || strDriver.equals("Select driver name here....")) {
                    Toast.makeText(ctx, "Please select driver.", Toast.LENGTH_LONG).show();
                }else{
                    LinkedList<Return> rl =  DcReturn.getInstance(ctx).getReturnlistAsc();
                    final ArrayList<HashMap> detailsArrayList = new ArrayList();
                    if(rl.size() > 0) {
                        Boolean errFlag = false;
                        for (int i = 0; i < rl.size(); i++) {
                            Return rowRl = rl.get(i);
                            if(rowRl.getQuantity().equals("") || rowRl.getQuantity().equals("0")) {
                                errFlag = true;
                                TableRow trx = (TableRow) tl.findViewById(Integer.parseInt(rowRl.getItemRecid()));
                                TextView tvQty = (TextView) trx.getChildAt(0);
                                tvQty.setTextColor(getResources().getColor(R.color.red_2));
                                TextView tvUnit = (TextView) trx.getChildAt(1);
                                tvUnit.setTextColor(getResources().getColor(R.color.red_2));
                                LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
                                TextView tvDesc = (TextView) tvDescBox.getChildAt(0);
                                tvDesc.setTextColor(getResources().getColor(R.color.red_2));
                            }
                        }

                        if(errFlag) {
                            isPosted = false;
                            dismissSpinnerDialog();
                            Toast.makeText(ctx, "Please check 0 or empty quantity.", Toast.LENGTH_LONG).show();
                        }else{
                            alertDialog = okCancelInputDialogBuilder(ctx,
                                "Please input your password to proceed the request.",
                                "Proceed",
                                null,
                                "Cancel",
                                cancelCallback
                            );
                        }
                    }else{
                        Toast.makeText(ctx, "Please add an item.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void requestDrivers(View v) {
        showSpinnerDialog(v);
        vi = new VolleyInteractor();
        vi.registerCallback(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("cn", ServerConstants.CN);
        params.put("jobtitle", ServerConstants.DRIVER);

        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        vi.getDrivers(v.getContext(), params, strParams);
    }

    View.OnClickListener cancelCallback = new View.OnClickListener() {
        public void onClick(View v) {
            Helper.hideSoftKeyboard(getActivity());
            dismissSpinnerDialog();
            alertDialog.dismiss();
        }
    };

    private Boolean isPosted = false;
    private void submitReturns() {
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(this);

        showSpinnerDialog(rootView);
        final HashMap<String, Object> paramsArray = new HashMap();
        paramsArray.clear();

        LinkedList<Return> rl =  DcReturn.getInstance(ctx).getReturnlistAsc();
        final ArrayList<HashMap> detailsArrayList = new ArrayList();
        if (rl.size() > 0) {
            Boolean errFlag = false;
            for (int i = 0; i < rl.size(); i++) {
                Return rowRl = rl.get(i);
                if(!rowRl.getQuantity().equals("") && !rowRl.getQuantity().equals("0")) {
                    HashMap<String, Object> detailMap = new HashMap();
                    detailMap.put("quantity", rowRl.getQuantity().equals("") ? "0" : rowRl.getQuantity() );
                    detailMap.put("item_recid", rowRl.getItemRecid());
                    detailMap.put("remarks", rowRl.getRemarks());
                    detailsArrayList.add(detailMap);
                }
            }
            if(!errFlag) {
                paramsArray.put("details", detailsArrayList);

                SharedData sp = SharedData.getInstance(ctx);
                HashMap<String, String> headersMap = new HashMap();
                headersMap.put("companydb", ServerConstants.CN);
                headersMap.put("customer_recid", sp.getData(SharedKey.CURRENT_CUSTOMER_ID.getKey()));
                headersMap.put("return_date", Helper.getPostingDate());
                headersMap.put("remarks", et_remarks.getText().toString().trim());
                headersMap.put("createdby", sp.getData(API.IDENTITY_ID.getApi()));
                String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                headersMap.put("pcortab_id", android_id);
                headersMap.put("pwd", refPassword);
                headersMap.put("drivername", refDriverId);
                paramsArray.put("header", headersMap);

                String paramsArrayStr = new JSONObject(paramsArray).toString();
                Log.v("post_params_length", String.valueOf(paramsArrayStr.length()));
                vi.postReturns(ctx, paramsArrayStr);
            }
        }else{
            dismissSpinnerDialog();
            Toast.makeText(ctx, "Error request. Please try again.", Toast.LENGTH_SHORT).show();
            isPosted = false;
        }
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
                    ArrayList<Itemlist> arrayList = new ArrayList<Itemlist>();
                    arrayList = (ArrayList) extras.get(SharedKey.SEARCHED_ITEMS.getKey());

                    for(Itemlist il : arrayList){
                        LinkedList<Return> rlx = DcReturn.getInstance(ctx).getReturnlist(il.getRecid());
                        if(rlx.size() == 0) {
                            TableRow trx = (TableRow) getLayoutInflater().inflate(R.layout.table_row_item_list, null);
                            trx.setId(il.getRecid());
                            trx.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isItemClicked = true;
                                    onTableItemClick(v);
                                }
                            });
                            trx.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    showMenuPopup(v);
                                    return true;
                                }
                            });

                            TextView tvQty = (TextView) trx.findViewById(R.id.cell_qty);
                            tvQty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    View vx = (View) v.getParent();
                                    onTableItemClick(vx);
                                }
                            });
                            tvQty.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    DcReturn.getInstance(ctx).updateReturnlistTallyString(
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

                            tl.addView(trx);
                            Return rowR = new Return();
                            rowR.setQuantity("");
                            rowR.setItemRecid(String.valueOf(il.getRecid()));
                            rowR.setItemName(String.valueOf(il.getItemName()));
                            rowR.setUnitName(String.valueOf(il.getUnitName()));
                            rowR.setRemarks("");
                            DcReturn.getInstance(ctx).insertReturnlist(rowR);
                        }else{
                            Toast.makeText(ctx, "Some item is already exist on the list", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED){ }
                break;
        }
    }

    private void onTableItemClick(View v) {
        Helper.hideSoftKeyboard(getActivity());
        bsBh.setState(BottomSheetBehavior.STATE_EXPANDED);
        calcRefId = v.getId();
        try {
            LinkedList<Return> refRS = DcReturn.getInstance(ctx).getReturnlistAsc();
            for (int i = 0; i < refRS.size(); i++) {
                Return rlx = refRS.get(i);
                TableRow trx = (TableRow) tblContentBox.findViewById(Integer.parseInt(rlx.getItemRecid()));
                if (trx != null) {
                    TextView tvQty = (TextView) trx.getChildAt(0);
                    tvQty.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
//                    tvQty.setHighlightColor(getResources().getColor(R.color.transparent));
                    tvQty.setTextColor(getResources().getColor(R.color.black));
                    TextView tvUnit = (TextView) trx.getChildAt(1);
                    tvUnit.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
                    LinearLayout tvDescBox = (LinearLayout) trx.getChildAt(2);
                    tvDescBox.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cell_background));
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
        }
    }

    public void onRequestSuccess(String response, String type) {
        isPosted = false;
        dismissSpinnerDialog();
        try {
            response = response.replace("\r\n", "");
            JSONArray objArr = new JSONArray(response);
            if(type.equals("searchemployee")) {
                if(objArr.length() > 0) {
                    driverList = new ArrayList<Drivers>();
                    List<String> list = new ArrayList<String>();
                    list.add("Select driver name here....");
                    for (int i = 0; i < objArr.length(); i++) {
                        JSONObject obj = objArr.getJSONObject(i);
                        list.add(obj.getString(DriversKey.EMPLOYEE_NAME.getKey()));

                        Drivers rsDrv = new Drivers();
                        rsDrv.setEmployeeName(obj.getString(DriversKey.EMPLOYEE_NAME.getKey()));
                        rsDrv.setEmployeeNumber(obj.getString(DriversKey.EMPLOYEE_NUMBER.getKey()));
                        driverList.add(rsDrv);
                    }
                    spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_driver_item, list) {
                        @Override
                        public boolean isEnabled(int position) {
                            if(position == 0) { return false; }
                            else { return true; }
                        }
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if(position == 0){ tv.setTextColor(Color.LTGRAY); }
                            else { tv.setTextColor(getResources().getColor(R.color.red_2)); }
                            return view;
                        }
                    };
                    et_driver.setAdapter(spinneradapter);
                }
            }else{
                if(objArr.length() > 0) {
                    JSONObject obj = objArr.getJSONObject(0);
                    Boolean status = obj.getBoolean("error");
                    if(!status){
                        Toast.makeText(ctx, obj.getString("tag"), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }else{
                        Toast.makeText(ctx, "Post error... "+obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                }else{
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
                TableRow trx = (TableRow) tblContentBox.findViewById(calcRefId);
                TextView tvQty = (TextView) trx.getChildAt(0);
                String refStr = tvQty.getText().toString();
                if (refStr.length() > 0) {
                    refStr = refStr.substring(0, refStr.length() - 1);
                }
                tvQty.setText(refStr);
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
        TableRow trx = (TableRow) tblContentBox.findViewById(calcRefId);
        TextView tvQty = (TextView) trx.getChildAt(0);
        String refStr = tvQty.getText().toString();
        if(isItemClicked) {
            refStr = "";
            isItemClicked = false;
        }
        if(val.equals(".")) {
            if(!refStr.contains(".")) {
                refStr = refStr+val;
            }
            if(refStr.trim().equals(".")) {
                refStr = "0.";
            }
        }else{
            if(refStr.trim().equals("0")) {
                refStr = "";
            }
            refStr = refStr+val;
        }
        tvQty.setText(refStr);
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
                    alertDialog = okCancelInputRemarksDialogBuilder(ctx,
                            "Add Remarks",
                            "Save", null, "Cancel", cancelCallback );
                    alertDialog.show();
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
                        DcReturn.getInstance(ctx).deleteReturnItemViaId(String.valueOf(calcRefId));
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
        final EditText etText = (EditText) layout.findViewById(R.id.et_password);

        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
//                Toast.makeText(activity, etText.getText().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Remarks saved successfully.", Toast.LENGTH_SHORT).show();
                DcReturn.getInstance(activity).updateReturnlistRemarksString(calcRefId.toString(),
                        etText.getText().toString(), false);
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

    private AlertDialog okCancelInputDialogBuilder(final Context activity, String message,
        String okButtonCaption, View.OnClickListener onClickListener,
        String cancelButtonCaption, View.OnClickListener cancelClickListener) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_ok_input_dialog, null);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        final EditText etText = (EditText) layout.findViewById(R.id.et_password);

        Button btnOK = (Button) layout.findViewById(R.id.btnOk);
        btnOK.setText(okButtonCaption);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refPassword = etText.getText().toString();
                alertDialog.dismiss();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
                } else {
                    builder = new AlertDialog.Builder(ctx);
                }
                builder.setCancelable(false);
                builder.setTitle("Post Transaction").setMessage("Are you sure want to post this transaction?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!isPosted) {
                                isPosted = true;
                                submitReturns();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isPosted = false;
                        }
                    })
                    .show();
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
}
