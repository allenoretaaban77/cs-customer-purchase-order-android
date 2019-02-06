package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fnc.order.android.R;
import com.fnc.order.android.adapters.ItemlistAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.enumeration.ItemlistKey;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;
import com.android.volley.error.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SearchItemFragment extends DialogFragment implements VolleyCallback {

    public Context ctx;
    private View rootView;
    private Button btn_search;
    private Button btn_close;
    private EditText et_item_name;
    private AlertDialog alertDialog;
    private ProgressDialog loader;
    private ItemlistAdapter adapter;
    private ListView listview;
    private Boolean flagItemClicked = false;

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
        et_item_name = (EditText) v.findViewById(R.id.et_item_name);
        listview = (ListView) v.findViewById(R.id.itemlistview);
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
                    Toast.makeText(ctx, "Please input item name.", Toast.LENGTH_SHORT).show();
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_item_name.getWindowToken(), 0);

        showSpinnerDialog(v);
        HashMap<String, String> params = new HashMap<>();
        String searchStr = et_item_name.getText().toString().trim();
        params.put("itemname", searchStr);
        params.put("cn", ServerConstants.CN);
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
            final LinkedList<Itemlist> iRs = new LinkedList<Itemlist>();
            if(objArr.length() > 0) {
                for (int i = 0; i < objArr.length(); i++) {
                    JSONObject rowObj = objArr.getJSONObject(i);
                    Itemlist irsx = new Itemlist();
                    irsx.setRecid(rowObj.getInt(ItemlistKey.RECID.getKey()));
                    irsx.setItemName(rowObj.getString(ItemlistKey.ITEM_NAME_WITH_UNIT.getKey()));
                    irsx.setDept(rowObj.getString(ItemlistKey.DEPT.getKey()));
                    irsx.setUnitName(rowObj.getString(ItemlistKey.UNIT.getKey()));
                    iRs.add(irsx);
                }
            }else{
                Itemlist irsx = new Itemlist();
                irsx.setItemName("none");
                iRs.add(irsx);
            }

            adapter = new ItemlistAdapter(ctx, iRs);
            listview.setAdapter(adapter);
            adapter.setOnItemClickListener(new ItemlistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view,  int pos) {
                    if(getActivity() != null) {
                        Intent i = getActivity().getIntent();
                        Itemlist il = iRs.get(pos);
                        i.putExtra(ItemlistKey.ITEM_RECORD.getKey(), il);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        dismiss();
                    }
                }
            });

        } catch (JSONException e) {
            dismissSpinnerDialog();
            Toast.makeText(ctx, "Something went wrong, please refresh the list.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            dismissSpinnerDialog();
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
