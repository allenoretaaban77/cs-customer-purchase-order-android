package com.fnc.order.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.fragment.app.DialogFragment;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.adapters.PersonAdapter;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.enumeration.PersonsKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.Person;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SearchPersonFragment extends DialogFragment implements VolleyCallback {

    public Context ctx;
    private View rootView;
    private Button btn_search;
    private EditText et_person_name;
    private AlertDialog alertDialog;
    private ProgressDialog loader;
    private PersonAdapter adapter;
    private ListView listview;

    public static SearchPersonFragment searchInstance(){
        SearchPersonFragment dialogFragment = new SearchPersonFragment();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_search_person, container, false);
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
        et_person_name = (EditText) v.findViewById(R.id.et_person_name);
        listview = (ListView) v.findViewById(R.id.personlistview);
    }

    private void initListeners(View v) {
        et_person_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btn_search.callOnClick();
                }
                return false;
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                if(!et_person_name.getText().toString().trim().equals("")) {
                    requestPerson(v);
                }else{
                    Toast.makeText(ctx, "Please input name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestPerson(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_person_name.getWindowToken(), 0);

        showSpinnerDialog(v);
        HashMap<String, String> params = new HashMap<>();
        params.put("name", et_person_name.getText().toString().trim());
        params.put("cn", SharedData.getInstance(ctx).getData(SharedKey.DATABASE.getKey()));
        Iterator it = params.entrySet().iterator();
        String strParams = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            strParams = strParams + pair.getKey()+"="+pair.getValue()+"&";
            it.remove();
        }
        VolleyInteractor vi = new VolleyInteractor();
        vi.registerCallback(this);
//        vi.getUsers(ctx, params, strParams);
    }

    public void onRequestSuccess(String response, String type) {
        final SharedData sp = SharedData.getInstance(ctx);
        try {
            response = response.replace("\r\n ", "");
            JSONArray objArr = new JSONArray(response);
            final LinkedList<Person> pRs = new LinkedList<Person>();
            if(objArr.length() > 0) {
                for (int i = 0; i < objArr.length(); i++) {
                    JSONObject rowObj = objArr.getJSONObject(i);
                    Person prsx = new Person();
                    prsx.setIdentityId(rowObj.getString(PersonsKey.IDENTITY_ID.getKey()));
                    prsx.setName(rowObj.getString(PersonsKey.NAME.getKey()));
                    pRs.add(prsx);
                }
            }else{
                Person prsx = new Person();
                prsx.setName("none");
                pRs.add(prsx);
            }

            adapter = new PersonAdapter(ctx, pRs);
            listview.setAdapter(adapter);
            adapter.setOnItemClickListener(new PersonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view,  int pos) {
                    Intent i = getActivity().getIntent();
                    Person p = pRs.get(pos);
                    i.putExtra(PersonsKey.IDENTITY_ID.getKey(), p.getIdentityId());
                    i.putExtra(PersonsKey.NAME.getKey(), p.getName());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    dismiss();
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
