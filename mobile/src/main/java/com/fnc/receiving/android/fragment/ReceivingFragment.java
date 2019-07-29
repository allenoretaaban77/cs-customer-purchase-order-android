package com.fnc.receiving.android.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.ULocale;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.constants.GlobalConstants;
import com.fnc.receiving.android.databinding.AFragmentReceivingBinding;
import com.fnc.receiving.android.databinding.AItemChecklistBinding;
import com.fnc.receiving.android.datacontroller.DcChecklist;
import com.fnc.receiving.android.datacontroller.DcItems;
import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.enumeration.aItemsKey;
import com.fnc.receiving.android.listeners.DatePickerListener;
import com.fnc.receiving.android.model.Itemlist;
import com.fnc.receiving.android.model.aChecklist;
import com.fnc.receiving.android.model.aItems;
import com.fnc.receiving.android.utilities.CloudStorage;
import com.fnc.receiving.android.utilities.DatePickerDialogFragment;
import com.fnc.receiving.android.utilities.Helper;
import com.google.api.core.NanoClock;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import easyadapter.dc.com.library.EasyAdapter;

public class ReceivingFragment extends Fragment implements VolleyCallback {

    private AFragmentReceivingBinding binding;
    private Context ctx;
    private View rv;
    private Spinner spnr_posstatus;
    private MaterialRippleLayout ll_startdate, ll_enddate;
    private EditText et_startdate, et_enddate, et_supplier;
    private String refStartDate, refStringStartDate, refEndDate, refStringEndDate;
    private FragmentTabHost mTabHost;
    private Storage storageinit;
    private ArrayList<String> strFileArr = new ArrayList<String>();
    private ProgressBar progressBar;

    private int progressStatus = 0;
    private Handler handler = new Handler();

    public ReceivingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        binding = DataBindingUtil.setContentView(this.getActivity(), R.layout.a_fragment_receiving);
        rv = inflater.inflate(R.layout.a_fragment_receiving, container, false);
        ctx = rv.getContext();

        LinkedList<aChecklist> cRs = DcChecklist.getInstance(ctx).getChecklist();
        aChecklist acRs = cRs.get(0);
//        adapter = new aItemsAdapter(ctx, aiRs);
        EasyAdapter adapter = new EasyAdapter<aChecklist, AItemChecklistBinding>(R.layout.a_item_checklist) {
            @Override
            public void onBind(@NonNull AItemChecklistBinding binding, @NonNull final aChecklist model) {

                binding.tvIncludedpo.setText(model.getIncludedPO());

                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date date = format.parse(model.getDeliveryDate() + "Z");
                    DateFormat df = new SimpleDateFormat("MMM d, yyyy");
                    binding.tvReceiveddate.setText(df.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                binding.tvSuppliername.setText("FNC Nathaniels");

                binding.tvStatus.setText("");

                binding.llContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ctx, String.valueOf(model.getRecid()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        adapter.addAll(cRs, false);

        RecyclerView rvItems = (RecyclerView) rv.findViewById(R.id.rv_view);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(ctx));

        progressBar = (ProgressBar) rv.findViewById(R.id.pb_loader);

        new googleClouFetchdProc().execute("OG");

        return rv;
    }

    private void initViews(View v) {
//        et_startdate = (EditText) v.findViewById(R.id.et_startdate);
//        ll_startdate = (MaterialRippleLayout) v.findViewById(R.id.ll_startdate);
//        et_enddate = (EditText) v.findViewById(R.id.et_enddate);
//        ll_enddate = (MaterialRippleLayout) v.findViewById(R.id.ll_enddate);
//
//        spnr_posstatus = (Spinner) v.findViewById(R.id.spnr_posstatus);
//        ArrayList<String> sl = new ArrayList<String>();
//        sl.add("Select Status...."); sl.add("FOR RECEIVING"); sl.add("PARTIAL"); sl.add("POSTED");
//        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(ctx, R.layout.a_item_spinner_small, sl) {
//            @Override
//            public boolean isEnabled(int position) {
//                if(position == 0) { return false; }
//                else {
//                    return true;
//                }
//            }
//            @Override
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                parent.setBackgroundResource(R.color.green_1);
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                if(position == 0){
//                    tv.setTextColor(getResources().getColor(R.color.green_3));
//                } else {
//                    tv.setTextColor(getResources().getColor(R.color.green_5));
//                }
//                return view;
//            }
//        };
//        spinneradapter.setDropDownViewResource(R.layout.a_item_spinner);
//        spinneradapter.notifyDataSetChanged();
//        spnr_posstatus.setAdapter(spinneradapter);
//        spnr_posstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                et_supplier.requestFocus();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

//        mTabHost = (FragmentTabHost) v.findViewById(android.R.id.tabhost);
//        mTabHost.setup(ctx, getActivity().getSupportFragmentManager(), R.id.realtabcontent);
//        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Checklist"),
//                PurchaseOrderListFragment.class, null);
//        for( int i=0; i<mTabHost.getTabWidget().getChildCount(); i++) {
//            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//            tv.setTextColor(getResources().getColor(R.color.green_5));
//        }

//        et_supplier = (EditText) v.findViewById(R.id.et_supplier);
    }

    private void initListeners(View v) {
//        ll_startdate.setOnClickListener(new View.OnClickListener() {
//            public final void onClick(final View v) {
//                Helper.hideSoftKeyboard(getActivity());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showDatePicker(et_startdate, "start");
//                    }
//                }, 500);
//            }
//        });
//
//        ll_enddate.setOnClickListener(new View.OnClickListener() {
//            public final void onClick(final View v) {
//                Helper.hideSoftKeyboard(getActivity());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showDatePicker(et_enddate, "end");
//                    }
//                }, 500);
//            }
//        });

//        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                onTabClick();
//            }
//        });
//        new android.os.Handler().postDelayed(
//            new Runnable() {
//                @Override
//                public void run() {
//                    mTabHost.setCurrentTab(0);
//                    onTabClick();
//                }
//            }, 300
//        );
    }

//    private void showSpinnerDialog(){
//        loader = Helper.buildSpinnerDialog(this);
//        loader.show();
//    }
//
//    private void dismissSpinnerDialog() {
//        isSubmit = false;
//        if(loader != null && loader.isShowing()) {
//            loader.dismiss();
//        }
//    }

    private void onTabClick () {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#44284E38")); // unselected
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(12);
            Typeface face = ResourcesCompat.getFont(ctx, R.font.light); tv.setTypeface(face);
            tv.setTextColor(getResources().getColor(R.color.green_3));
        }
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#99284E38")); // selected
        TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(getResources().getColor(R.color.green_5));
    }

    public void onRequestSuccess(String response, String type) {
    }

    public void onRequestFail(VolleyError volleyError, String type) {
    }

    private void showDatePicker(final EditText et, final String strFlag) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (!et.getText().toString().equals("")){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
                c.setTime(sdf.parse(et.getText().toString()));
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
                Calendar c = Calendar.getInstance();
                int curday = c.get(Calendar.DAY_OF_MONTH);
                SimpleDateFormat sdf = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
                int prevday = c.get(Calendar.DAY_OF_MONTH);
                switch (strFlag) {
                    case "end":
                        refStringEndDate = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_POST).format(date);
                        refEndDate = new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT).format(date);
                        et.setText(new SimpleDateFormat(GlobalConstants.DATE_FORMAT).format(date));
                        try {
                            c.setTime(sdf.parse(refStringEndDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        refStringStartDate = new SimpleDateFormat(GlobalConstants.DATE_FORMAT_POST).format(date);
                        refStartDate = new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT).format(date);
                        et.setText(new SimpleDateFormat(GlobalConstants.DATE_FORMAT).format(date));
                        try {
                            c.setTime(sdf.parse(refStringStartDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    private class googleClouFetchdProc extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String strCustomerName = params[0].replace("\r\n", "");
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(GlobalConstants.GCP_CREDENTIAL, "raw", ctx.getPackageName()));
                GoogleCredentials credentials = GoogleCredentials.fromStream(ins);
                storageinit = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .setClock(NanoClock.getDefaultClock())
                        .setProjectId(GlobalConstants.GCP_PROJECTID)
                        .build()
                        .getService();

                File file = new File(Helper.getProjectPath(ctx)); if(!file.exists()){ file.mkdir(); } // make first main dir
                file = new File(Helper.getFilePath(ctx)); if(!file.exists()){ file.mkdir(); }

                Page<Blob> blobs = storageinit.list(GlobalConstants.GCP_BUCKET, Storage.BlobListOption.prefix("OG/07252019"));
                for (Blob b : blobs.iterateAll()) {
                    if (b != null) {
                        String[] refStr = b.getGeneratedId().split("/");
                        if (!refStr[3].equals("")) {
                            Log.d("dsx", Helper.getFilePath(ctx) + refStr[3]);
                            b.downloadTo(Paths.get(Helper.getFilePath(ctx) + refStr[3]));
                            strFileArr.add(refStr[3]);
                        }
                    }
                }
                Log.d("dsx", "Task Items Fetch Completed");

                // save to db
                DcItems.getInstance(ctx).emptyItems();
                DcChecklist.getInstance(ctx).emptyChecklist();

                for (String strFile : strFileArr) {
                    file = new File(Helper.getFilePath(ctx), strFile);
                    StringBuilder refStr = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            refStr.append(line);
                            refStr.append('\n');
                        }
                        br.close();
                        file.delete();
//                    Log.d("dsx", refStr.toString());
                        String response = refStr.toString().replace("\r\n ", "");
                        try {
                            JSONObject obj = new JSONObject(response);

                            JSONArray objArrCL = obj.getJSONArray("checklist");
                            JSONArray objArrDR = obj.getJSONArray("dr");
                            if(objArrCL.length() > 0) {
                                publishProgress(objArrCL.length());
                                for (int i = 0; i < objArrCL.length(); i++) {
                                    JSONObject oCl = objArrCL.getJSONObject(i);
                                    JSONObject oDl = objArrDR.getJSONObject(i);
//                                Log.d("dsx", oIl.getString(aItemsKey.ITEMNAME_WUNIT.getKey()));
                                    aChecklist cl = new aChecklist(
                                            oCl.getInt(aChecklistKey.RECID.getKey()),
                                            oCl.getString(aChecklistKey.BRANCH_CODE.getKey()),
                                            oCl.getString(aChecklistKey.CUSTOMER_NAME.getKey()),
                                            oCl.getString(aChecklistKey.DELIVERY_DATE.getKey()),
                                            oCl.getString(aChecklistKey.ORDER_TYPE.getKey()),
                                            oCl.getString(aChecklistKey.INCLUDED_PO.getKey()),
                                            oCl.getString(aChecklistKey.DRIVER.getKey()),
                                            oCl.getString(aChecklistKey.PLATE_NO.getKey()),
                                            oCl.getString(aChecklistKey.CREATED_BY.getKey()),
                                            oDl.getInt(aChecklistKey.RECID.getKey()),
                                            oDl.getInt(aChecklistKey.DR_NUMBER.getKey()),
                                            oDl.getString(aChecklistKey.INVOICE_NUMBER.getKey()),
                                            oDl.getInt(aChecklistKey.PAGE_NO.getKey()),
                                            oDl.getString(aChecklistKey.STATUS.getKey()),
                                            oDl.getString(aChecklistKey.STATUS_LBL.getKey()),
                                            oDl.getString(aChecklistKey.REMARKS.getKey()),
                                            oDl.getString(aChecklistKey.FOOD_SERVICE.getKey()),
                                            oDl.getString(aChecklistKey.CREATED_BY.getKey()),
                                            0,
                                            ""
                                    );
                                    DcChecklist.getInstance(ctx).insertChecklist(cl);
                                    publishProgress(i);
                                }
                            }

                            JSONArray objArr = obj.getJSONArray("items");
                            if(objArr.length() > 0) {
                                publishProgress(objArrCL.length());
                                for (int i = 0; i < objArr.length(); i++) {
                                    JSONObject oIl = objArr.getJSONObject(i);
//                                Log.d("dsx", oIl.getString(aItemsKey.ITEMNAME_WUNIT.getKey()));
                                    aItems il = new aItems(
                                            oIl.getInt(aItemsKey.RECID.getKey()),
                                            oIl.getInt(aItemsKey.CHECKLIST_RECID.getKey()),
                                            oIl.getString(aItemsKey.REC_TALLY.getKey()),
                                            oIl.getString(aItemsKey.REC_QUANTITY.getKey()),
                                            oIl.getString(aItemsKey.TALLY.getKey()),
                                            oIl.getLong(aItemsKey.QUANTITY.getKey()),
                                            oIl.getLong(aItemsKey.PO_QUANTITY.getKey()),
                                            oIl.getString(aItemsKey.NUNIT.getKey()),
                                            oIl.getString(aItemsKey.UNIT.getKey()),
                                            oIl.getString(aItemsKey.ITEMNAME_WUNIT.getKey()),
                                            oIl.getInt(aItemsKey.ALLOW_DECIMAL.getKey()),
                                            oIl.getInt(aItemsKey.OLD_SKU.getKey()),
                                            oIl.getString(aItemsKey.OLD_BARCODE.getKey()),
                                            oIl.getString(aItemsKey.ITEM_RECID.getKey()),
                                            oIl.getString(aItemsKey.SELLING_PRICE.getKey())
                                    );
                                    DcItems.getInstance(ctx).insertItems(il);
                                    publishProgress(i);
                                }
                            }

                            Log.d("dsx", "Task Save Done");

                        } catch (JSONException je) {
                            Log.d("dsx", "Task Items Request Saving Error");
                        }
                    } catch (IOException e) {
                        Log.d("dsx", "Task Items Request Error");
                    }
                }

                return "Task Completed";
            } catch (IOException io) {
                Log.d("dsx", "Task Items Failed");
                return "Task Failed";
            } catch (java.lang.Exception je) {
                return "Task Failed";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);


            Log.d("dsx", "Task Items Post");
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            Log.d("dsx", "Task Items Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("dsx", "Running " + values[0]);
            progressBar.setProgress(values[0]);
        }
    }
}
