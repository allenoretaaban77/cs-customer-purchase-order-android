package com.fnc.receiving.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.fnc.receiving.android.R;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.databinding.AFragmentReceivingBinding;
import com.fnc.receiving.android.databinding.AItemItemsBinding;
import com.fnc.receiving.android.datacontroller.DcItems;
import com.fnc.receiving.android.model.aItems;
import java.util.LinkedList;
import easyadapter.dc.com.library.EasyAdapter;

public class ItemsFragment extends Fragment implements VolleyCallback {

    private AFragmentReceivingBinding binding;
    private Context ctx;
    private View rv;
    private ProgressBar progressBar;

    public ItemsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rv = inflater.inflate(R.layout.a_fragment_items, container, false);
        ctx = rv.getContext();

        initViews(rv);
        initListeners(rv);

        return rv;
    }

    private void initViews(View v) {
        ActionBar actionBar = getActivity().getActionBar(). ;
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) rv.findViewById(R.id.pb_loader);
        RecyclerView rvItems = (RecyclerView) rv.findViewById(R.id.rv_view);

        LinkedList<aItems> iRs = DcItems.getInstance(ctx).getItems();
        EasyAdapter adapter = new EasyAdapter<aItems, AItemItemsBinding>(R.layout.a_item_items) {
            @Override
            public void onBind(@NonNull AItemItemsBinding binding, @NonNull final aItems model) {
                binding.tvDescription.setText(model.getItemName_wUnit());
            }
        };
        adapter.addAll(iRs, false);

        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(ctx));
    }

    private void initListeners(View v) {
    }

    public void onRequestSuccess(String response, String type) {
    }

    public void onRequestFail(VolleyError volleyError, String type) {
    }
}
