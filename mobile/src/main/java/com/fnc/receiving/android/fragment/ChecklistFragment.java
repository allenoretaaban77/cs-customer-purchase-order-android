package com.fnc.receiving.android.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.constants.GlobalConstants;
import com.fnc.receiving.android.databinding.AFragmentReceivingBinding;
import com.fnc.receiving.android.databinding.AItemChecklistBinding;
import com.fnc.receiving.android.datacontroller.DcChecklist;
import com.fnc.receiving.android.datacontroller.DcItems;
import com.fnc.receiving.android.enumeration.API;
import com.fnc.receiving.android.enumeration.SharedKey;
import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.enumeration.aItemsKey;
import com.fnc.receiving.android.listeners.DatePickerListener;
import com.fnc.receiving.android.model.aChecklist;
import com.fnc.receiving.android.model.aItems;
import com.fnc.receiving.android.utilities.DatePickerDialogFragment;
import com.fnc.receiving.android.utilities.Helper;
import com.fnc.receiving.android.utilities.SharedData;
import com.google.api.core.NanoClock;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
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

import easyadapter.dc.com.library.EasyAdapter;

public class ChecklistFragment extends Fragment implements VolleyCallback {

    private AFragmentReceivingBinding binding;
    private Context ctx;
    private View rv;
    private RecyclerView rvItems;
    private Storage storageinit;
    private ArrayList<String> strFileArr = new ArrayList<String>();
    private ProgressBar progressBar;
    private MaterialRippleLayout btn_add_checklist;
    private DatePickerDialogFragment dtDialog;
    private SharedData sp;

    public ChecklistFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rv = inflater.inflate(R.layout.a_fragment_checklist, container, false);
        ctx = rv.getContext();

//        LottieAnimationView animationView = rv.findViewById(R.id.lottieAnimationView);

        initViews(rv);
        initListeners(rv);

        new googleClouFetchdProc().execute("");

        return rv;
    }

    private void initViews(View v) {
        btn_add_checklist = (MaterialRippleLayout) v.findViewById(R.id.btn_add_checklist);

        progressBar = (ProgressBar) v.findViewById(R.id.pb_loader);
        RecyclerView rvItems = (RecyclerView) v.findViewById(R.id.rv_view);

        LinkedList<aChecklist> cRs = DcChecklist.getInstance(ctx).getChecklist();
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
                        getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ItemsFragment(), "items_fragment")
                            .addToBackStack(null)
                            .commit();
                    }
                });
            }
        };
        adapter.addAll(cRs, false);

        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(ctx));

        final Calendar c = Calendar.getInstance();
        dtDialog = new DatePickerDialogFragment();
        dtDialog.setShowsDialog(true);
        dtDialog.setDatePickerListener(new DatePickerListener() {
            @Override
            public void didPickDate(Context context, Date date) {
                Toast.makeText(ctx, new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT).format(date), Toast.LENGTH_SHORT).show();
                sp = SharedData.getInstance(ctx);
                sp.saveData(SharedKey.SELECTED_DATE.getKey(), new SimpleDateFormat(GlobalConstants.DATE_REF_FORMAT).format(date));
            }
        });
    }

    private void initListeners(View v) {
        btn_add_checklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vx) {
                dtDialog.show(getFragmentManager(), "");
            }
        });
    }

    public void onRequestSuccess(String response, String type) {
    }

    public void onRequestFail(VolleyError volleyError, String type) {
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

                String strBranchCode = SharedData.getInstance(ctx).getData(SharedKey.BRANCH_CODE.getKey());
                String strSelectedDate = SharedData.getInstance(ctx).getData(SharedKey.SELECTED_DATE.getKey());

                Page<Blob> blobs = storageinit.list(GlobalConstants.GCP_BUCKET, Storage.BlobListOption.prefix(strBranchCode + "/" + strSelectedDate));
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
            progressBar.setMax(20);
            Log.d("dsx", "Task Items Starting");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("dsx", "Running " + values[0]);
            progressBar.setProgress(values[0]);
        }
    }
}
