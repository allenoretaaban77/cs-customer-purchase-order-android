package com.fnc.order.android.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.databinding.ItemUserBinding;
import com.fnc.order.android.datacontroller.DcStaffs;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.aAdminGroupings;
import com.fnc.order.android.model.aStaffs;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.roacult.backdrop.BackdropLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import easyadapter.dc.com.library.EasyAdapter;
import hari.bounceview.BounceView;

public class UserFragment extends DialogFragment {

    public Context ctx;
    private View v;
    private SharedData sp;
    private MaterialRippleLayout btn_close, btn_add, btn_user_search;
    private EasyAdapter adapter;
    private RecyclerView rv_user;
    private TextView tv_no_data;
    private LinkedList<aAdminGroupings> llJobtitles = new LinkedList<>();
    private ProgressDialog loader;
    private AlertDialog alertDialog, alertDialogUser, alertDialogJobTitle, alertDialogUpdatePassword;
    private EditText et_item_name;
    private LinearLayout ll_content_box_main;

    public static UserFragment searchInstance(){
        UserFragment dialogFragment = new UserFragment();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_users, container, false);
        ctx = v.getContext();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sp = SharedData.getInstance(ctx);

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dismiss();
                    return true;
                }
                return false;
            }
        } );

        initViews(v);
        initListeners(v);

        return v;
    }

    private BackdropLayout containerbdl;
    private void initViews(View v) {
        containerbdl = (BackdropLayout) v.findViewById(R.id.containerbdl);
        ll_content_box_main = (LinearLayout) v.findViewById(R.id.ll_content_box_main);
        btn_close = (MaterialRippleLayout) v.findViewById(R.id.btn_close);
        btn_add = (MaterialRippleLayout) v.findViewById(R.id.btn_add);
        btn_user_search = (MaterialRippleLayout) v.findViewById(R.id.btn_user_search);
        et_item_name = (EditText) v.findViewById(R.id.et_item_name);
        rv_user = (RecyclerView) v.findViewById(R.id.rv_user);
        tv_no_data = (TextView) v.findViewById(R.id.tv_no_data);

        displayUsers("%");

    }

    private Boolean flagTaskRun = false; private Handler m_handler; private Runnable m_runnable;
    private void initListeners(View v) {
        btn_close.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                addUser();
            }
        });
        et_item_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btn_user_search.callOnClick();
                    return true;
                }
                return false;
            }
        });
        et_item_name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_item_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                tv_no_data.setVisibility(View.VISIBLE); tv_no_data.setText("Searching...");
                rv_user.setVisibility(View.GONE);

                if (flagTaskRun) { m_handler.removeCallbacks(m_runnable); }
                m_handler = new Handler();
                m_runnable = new Runnable() {
                    @Override
                    public void run() {
                        displayUsers("%"+s.toString()+"%");
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
        btn_user_search.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(!et_item_name.getText().toString().trim().equals("")) {
                    displayUsers(et_item_name.getText().toString().trim());
                } else {
                    BounceView.addAnimTo( Helper.okDialog(ctx, "Error","Invalid user.", "OK",
                        null, false) );
                }
            }
        });
        ll_content_box_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() > event.getY()) {
                    containerbdl.open();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rv_user.setLayoutParams(
                                    new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                                            Math.round(Helper.convertDpToPixel(ctx, 528f)))
                            );
                        }
                    }, 500);
                }
                return false;
            }
        });
        rv_user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() > event.getY()) {
                    containerbdl.close();
                    rv_user.setLayoutParams(
                            new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                                    Math.round(Helper.convertDpToPixel(ctx, 586f)))
                    );
                }
                return false;
            }
        });
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
                try {
                    response = response.replace("\r\n ", "");
                    Log.d("DSX post response: ", response);
                    SharedData.getInstance(ctx).saveData(SharedKey.REF_JOBTITLES.getKey(), response);
                    JSONArray objArr = new JSONArray(response);
                    llJobtitles = new LinkedList<>();
                    if(objArr.length() > 0) {
                        aAdminGroupings cjt = new aAdminGroupings();
                        cjt.setRecid(0);
                        cjt.setCode("0");
                        cjt.setDescription("Select Job Title:");
                        cjt.setDeleted("false");
                        llJobtitles.add(cjt);
                        for (int ix = 0; ix < objArr.length(); ix++) {
                            JSONObject rowObj = objArr.getJSONObject(ix);
                            cjt = new aAdminGroupings();
                            cjt.setRecid(rowObj.getInt("recid"));
                            cjt.setCode(rowObj.getString("code"));
                            cjt.setDescription(rowObj.getString("Description"));
                            cjt.setDeleted(rowObj.getString("deleted"));
                            llJobtitles.add(cjt);
                        }
                        displaySpinnerJobTitles();
                    } else {
                        Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onRequestFail(VolleyError response, String type) {
                Helper.dismissSpinnerDialog(loader);
                Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
            }
        });
        viag.getAdminGroupings(ctx, params, strParams.replaceAll(" ", "%20"));
    }

    private void displaySpinnerJobTitles() {
        spinneradapter = new ArrayAdapter<aAdminGroupings>(ctx, android.R.layout.simple_spinner_dropdown_item, llJobtitles) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) { return false; }
                else { return true; }
            }
            @Override
            public View getDropDownView(int pos, View cv, ViewGroup prnt) {
                View view = super.getDropDownView(pos, cv, prnt);
                TextView tv = (TextView) view;
                if(pos == 0){ tv.setTextColor(Color.GRAY);  }
                else { tv.setTextColor(Color.DKGRAY); }
                tv.setText(llJobtitles.get(pos).getDescription());
                return view;
            }
        };
        btnAddJobTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int pos, long id) {
                if (pos != 0) {
                    et_jobtitle.setText(llJobtitles.get(pos).getDescription());
                    et_jobtitle_id.setText(llJobtitles.get(pos).getRecid().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        btnAddJobTitle.setAdapter(spinneradapter);
    }

    private void loadUsers() {
        if (!Helper.isNetworkAvailable(ctx)) {
            Toast.makeText(ctx, "User fetch failed.  Please check your connection.",
                    Toast.LENGTH_SHORT).show(); return;
        }

        loader = Helper.showSpinnerDialog(ctx, "Updating", "Please wait..."); loader.show();
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
                                JSONArray sArr = obj.getJSONArray("staff");
                                if (sArr.length() > 0) {
                                    DcStaffs.getInstance(ctx).emptyStaffslist();
                                    Helper.insertDefaultStaffs(ctx);
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
                                } else {
                                    DcStaffs.getInstance(ctx).emptyStaffslist();
                                    Helper.insertDefaultStaffs(ctx); // add main
                                }
                                displayUsers("%");
                                loadAdminJobTitles();
                            } else {
                                Log.d("dsxe getuser", response);
                                Helper.dismissSpinnerDialog(loader);
                                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("dsxe getuser", response);
                            Helper.dismissSpinnerDialog(loader);
                            Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Log.d("dsxe getuser", String.valueOf(response));
                        Helper.dismissSpinnerDialog(loader);
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
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

    private void displayUsers(String strName) {
        try {
            JSONArray objArr = new JSONArray(sp.getData(SharedKey.REF_JOBTITLES.getKey()));
            llJobtitles = new LinkedList<>();
            if(objArr.length() > 0) {
                aAdminGroupings cjt = new aAdminGroupings();
                cjt.setRecid(0);
                cjt.setCode("0");
                cjt.setDescription("Select Job Title:");
                cjt.setDeleted("false");
                llJobtitles.add(cjt);
                for (int i = 0; i < objArr.length(); i++) {
                    JSONObject rowObj = objArr.getJSONObject(i);
                    cjt = new aAdminGroupings();
                    cjt.setRecid(rowObj.getInt("recid"));
                    cjt.setCode(rowObj.getString("code"));
                    cjt.setDescription(rowObj.getString("Description"));
                    cjt.setDeleted(rowObj.getString("deleted"));
                    llJobtitles.add(cjt);
                }
            }
            // DcStaffs.getInstance(ctx).emptyStaffslist();
            LinkedList<aStaffs> rsSt = DcStaffs.getInstance(ctx).getStaffswihtOrder(strName);
            if (rsSt.size() > 0) {
                rv_user.setVisibility(View.VISIBLE);
                tv_no_data.setVisibility(View.GONE);
                adapter = new EasyAdapter<aStaffs, ItemUserBinding>(R.layout.item_user) {
                    @Override
                    public void onBind(@NonNull final ItemUserBinding binding, @NonNull final aStaffs model) {
                        binding.tvUsername.setText(model.getName());

                        binding.tvPosition.setText("");
                        for (int i = 0; i < llJobtitles.size(); i++) {
                            aAdminGroupings rsAG = llJobtitles.get(i);
                            if (rsAG.getRecid() == model.getJobtitle()) {
                                binding.tvPosition.setText(String.valueOf(rsAG.getDescription()));
                            }
                        }

                        LinkedList<aStaffs> iaRs = DcStaffs.getInstance(ctx).checkIfActive(model.getEmpId());
                        if (iaRs.size() > 0) {
                            binding.cbActive.setChecked(false);
                        } else {
                            binding.cbActive.setChecked(true);
                        }
                        binding.cbActive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if( binding.cbActive.isChecked() ) {
                                    DcStaffs.getInstance(ctx).setActive(model);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    BounceView.addAnimTo( Helper.okCancelDialog( ctx,
                                        "Change User Status",
                                        "Are you sure you want to deactivate this user?.",
                                        "YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DcStaffs.getInstance(ctx).setInActive(model);
                                                adapter.notifyDataSetChanged();
                                            } },
                                        "CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                DcStaffs.getInstance(ctx).setActive(model);
                                                adapter.notifyDataSetChanged();
                                            }
                                    }, false) );
                                }
                            }
                        });

                        binding.mlItemBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePassword(model);
                            }
                        });
                    }
                };
                adapter.addAll(rsSt, false);
                rv_user.setAdapter(adapter);
                rv_user.setLayoutManager(new LinearLayoutManager(ctx));
                adapter.notifyDataSetChanged();
            } else {
                rv_user.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Toast.makeText(ctx, "Error on loading Job Title.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addJobtitle() {
        alertDialogJobTitle = addJobTitleDialog(ctx);

        Button btnSubmit = (Button) alertDialogJobTitle.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormEditText et_jobtitle_code = (FormEditText) alertDialogJobTitle.findViewById(R.id.et_jobtitle_code);
                FormEditText et_jobtitle_description = (FormEditText) alertDialogJobTitle.findViewById(R.id.et_jobtitle_description);

                if (et_jobtitle_code.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid job title code.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_jobtitle_description.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid description.", Toast.LENGTH_SHORT).show(); return;
                }
                loader = Helper.showSpinnerDialog(ctx, "Posting Job Title Info", "Please wait..."); loader.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("cn", sp.getData(SharedKey.DATABASE.getKey()));
                params.put("type", "5");
                params.put("code", et_jobtitle_code.getText().toString().trim());
                params.put("desc", et_jobtitle_description.getText().toString().trim());
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                    it.remove();
                }
                final VolleyInteractor vijt = new VolleyInteractor();
                vijt.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(final String response, String type) {
                        if (response.trim().toLowerCase().equals("true")) {
                            Helper.dismissSpinnerDialog(loader);
                            alertDialogJobTitle.dismiss();
                            loadAdminJobTitles();
                            Toast.makeText(ctx, "Update success", Toast.LENGTH_SHORT).show();
                        } else {
                            Helper.dismissSpinnerDialog(loader);
                            Toast.makeText(ctx, "Adding jobtitle failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        Log.d("DSX signup success: ", String.valueOf(response.getMessage()));
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("dsx", strParams);
                vijt.postAddJobTitle(ctx, params, strParams.replaceAll(" ", "%20"));
            }
        });

        alertDialogJobTitle.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
        alertDialogJobTitle.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BounceView.addAnimTo(alertDialogJobTitle);
    }

    private AlertDialog addJobTitleDialog(final Context activity) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_add_jobtitle, null);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogJobTitle.dismiss();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private Spinner btnAddJobTitle;
    private FormEditText et_jobtitle;
    private EditText et_jobtitle_id;
    private ArrayAdapter<aAdminGroupings> spinneradapter;
    private void addUser() {
        alertDialogUser = addUserDialog(ctx);
        et_jobtitle = (FormEditText) alertDialogUser.findViewById(R.id.et_jobtitle);
        et_jobtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddJobTitle.performClick();
            }
        });
        et_jobtitle_id = (EditText) alertDialogUser.findViewById(R.id.et_jobtitle_id);
        btnAddJobTitle = (Spinner) alertDialogUser.findViewById(R.id.btnAddJobTitle);

        MaterialRippleLayout mrlAddJobtitle = (MaterialRippleLayout) alertDialogUser.findViewById(R.id.mrl_add_jobtitle);
        mrlAddJobtitle.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJobtitle();
            }
        });

        Button btnSubmit = (Button) alertDialogUser.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isNetworkAvailable(ctx)) {
                    Toast.makeText(ctx, "Request failed.  Please check your connection.",
                            Toast.LENGTH_SHORT).show(); return;
                }

                FormEditText et_employeeno = (FormEditText) alertDialogUser.findViewById(R.id.et_employeeno);
                FormEditText et_firstname = (FormEditText) alertDialogUser.findViewById(R.id.et_firstname);
                FormEditText et_middlename = (FormEditText) alertDialogUser.findViewById(R.id.et_middlename);
                FormEditText et_lastname = (FormEditText) alertDialogUser.findViewById(R.id.et_lastname);
                FormEditText et_password = (FormEditText) alertDialogUser.findViewById(R.id.et_password);
                FormEditText et_repeatpassword = (FormEditText) alertDialogUser.findViewById(R.id.et_repeatpassword);

                if (et_employeeno.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid employee number.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_firstname.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid first name.", Toast.LENGTH_SHORT).show(); return;
                }
//                if (et_middlename.getText().toString().trim().equals("")) {
//                    Toast.makeText(ctx, "Invalid middle name.", Toast.LENGTH_SHORT).show(); return;
//                }
                if (et_lastname.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid last name.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_jobtitle_id.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Please select job title.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_password.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_repeatpassword.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (!et_repeatpassword.getText().toString().trim().equals(et_password.getText().toString().trim())) {
                    Toast.makeText(ctx, "Psssword does not match.", Toast.LENGTH_SHORT).show(); return;
                }

                loader = Helper.showSpinnerDialog(ctx, "Posting User Info", "Please wait..."); loader.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("cnstr", sp.getData(SharedKey.DATABASE.getKey()));
                params.put("empNo", et_employeeno.getText().toString().trim());
                params.put("email", "");
                params.put("pass", et_password.getText().toString().trim());
                params.put("fname", et_firstname.getText().toString().trim());
                params.put("mname", et_middlename.getText().toString().trim().equals("") ? "N/A" : et_middlename.getText().toString().trim() );
                params.put("lname", et_lastname.getText().toString().trim());
                params.put("branch", sp.getData(SharedKey.BRANCH_ID.getKey()));
                params.put("jobtitle", et_jobtitle_id.getText().toString().trim());
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                    it.remove();
                }
                Log.d("dsx", strParams);
                final VolleyInteractor vidp = new VolleyInteractor();
                vidp.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(final String response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        try {
                            JSONArray objArr = new JSONArray(response);
                            JSONObject obj = new JSONObject(objArr.get(0).toString());
                            if (obj.getString("error").equals("true")) {
                                Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("DSX signup success", response);
                                Toast.makeText(ctx, "User successfully added!", Toast.LENGTH_SHORT).show();
                                loadUsers();
                                alertDialogUser.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Post error. This user is already assign at the other store. Please contact IT support.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        Log.d("DSX signup success: ", String.valueOf(response.getMessage()));
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("dsx", strParams);
                vidp.postBranchSignUp(ctx, params, strParams.replaceAll(" ", "%20"));
            }
        });

        alertDialogUser.getWindow().setLayout(Helper.getDialogWidthSignup(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
        alertDialogUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BounceView.addAnimTo(alertDialog);

        loadAdminJobTitles();
    }

    private AlertDialog addUserDialog(final Context activity) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_add_user, null);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogUser.dismiss();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }

    private void updatePassword(final aStaffs sl) {
        alertDialogUpdatePassword = updatePasswordDialog(ctx);

        Button btnSubmit = (Button) alertDialogUpdatePassword.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormEditText et_old_password = (FormEditText) alertDialogUpdatePassword.findViewById(R.id.et_old_password);
                FormEditText et_password = (FormEditText) alertDialogUpdatePassword.findViewById(R.id.et_password);
                FormEditText et_repeatpassword = (FormEditText) alertDialogUpdatePassword.findViewById(R.id.et_repeatpassword);

                if (et_old_password.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid old password.", Toast.LENGTH_SHORT).show(); return;
                } else {
                    if ( !sl.getPass().equals(et_old_password.getText().toString()) ) {
                        Toast.makeText(ctx, "Invalid old password.", Toast.LENGTH_SHORT).show(); return;
                    }
                }
                if (et_password.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (et_repeatpassword.getText().toString().trim().equals("")) {
                    Toast.makeText(ctx, "Invalid password.", Toast.LENGTH_SHORT).show(); return;
                }
                if (!et_repeatpassword.getText().toString().trim().equals(et_password.getText().toString().trim())) {
                    Toast.makeText(ctx, "Psssword does not match.", Toast.LENGTH_SHORT).show(); return;
                }
                loader = Helper.showSpinnerDialog(ctx, "Updating Psssword", "Please wait..."); loader.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("logdb", sp.getData(SharedKey.DATABASE.getKey()));
                params.put("empid", String.valueOf(sl.getEmpId()));
                params.put("password", et_password.getText().toString());
                if (sp.getData(SharedKey.DATABASE.getKey()).equals(sp.getData(SharedKey.REF_DATABASE.getKey()).trim())) {
                    params.put("updatedby", sp.getData(SharedKey.REF_EMP_ID.getKey()));
                } else {
                    params.put("updatedby", sp.getData(SharedKey.REF_EMP_ID.getKey()));
                }
                Iterator it = params.entrySet().iterator();
                String strParams = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
                    it.remove();
                }
                final VolleyInteractor viup = new VolleyInteractor();
                viup.registerCallback(new VolleyCallback() {
                    @Override
                    public void onRequestSuccess(final String response, String type) {
                        if (response.trim().toLowerCase().equals("true")) {
                            Helper.dismissSpinnerDialog(loader);
                            alertDialogUpdatePassword.dismiss();
                            loadUsers();
                            Toast.makeText(ctx, "Update success", Toast.LENGTH_SHORT).show();
                        } else {
                            Helper.dismissSpinnerDialog(loader);
                            Toast.makeText(ctx, "Update password failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onRequestFail(VolleyError response, String type) {
                        Helper.dismissSpinnerDialog(loader);
                        Log.d("DSX signup success: ", String.valueOf(response.getMessage()));
                        Toast.makeText(ctx, "Request Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("dsx", strParams);
                viup.postUpdatePassword(ctx, params, strParams.replaceAll(" ", "%20"));
            }
        });


        ((TextView) alertDialogUpdatePassword.findViewById(R.id.tv_employee_number))
                .setText("Employee Number: " + sl.getEmpNo());
        ((TextView) alertDialogUpdatePassword.findViewById(R.id.tv_user_name))
                .setText("Name: " + sl.getName());
        ((TextView) alertDialogUpdatePassword.findViewById(R.id.tv_job_title)).setVisibility(View.GONE);
//                .setText("Job Title: " + sl.getJobtitle());

        alertDialogUpdatePassword.getWindow().setLayout(Helper.getDialogWidth(ctx), RelativeLayout.LayoutParams.WRAP_CONTENT);
        alertDialogUpdatePassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BounceView.addAnimTo(alertDialogJobTitle);
    }

    private AlertDialog updatePasswordDialog(final Context activity) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_update_password, null);

        MaterialRippleLayout btnClose = (MaterialRippleLayout) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogUpdatePassword.dismiss();
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(layout);
        builder.create();
        builder.setCancelable(false);
        return builder.show();
    }
}
