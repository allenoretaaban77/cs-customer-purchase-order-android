package com.fnc.order.android.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.databinding.ItemItemlistOthersBinding;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.utilities.SharedData;

import java.util.LinkedList;

import easyadapter.dc.com.library.EasyAdapter;

public class OtherItemFragment extends DialogFragment {

    public Context ctx;
    private View v;
    private SharedData sp;
    private MaterialRippleLayout btn_close;
    private RecyclerView rv_others;
    private TextView tv_no_data;
    private EasyAdapter adapter;

    public static OtherItemFragment searchInstance(){
        OtherItemFragment dialogFragment = new OtherItemFragment();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_other_items, container, false);
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

    private void initViews(View v) {
        btn_close = (MaterialRippleLayout) v.findViewById(R.id.btn_close);
        rv_others = (RecyclerView) v.findViewById(R.id.rv_others);
        tv_no_data = (TextView) v.findViewById(R.id.tv_no_data);

        LinkedList<Order> olRs = DcOrder.getInstance(ctx).searchOrderFilterMultiple(OrderKey.OLD_SKU.getKey() + " = ?", new String[] { "null" }, "");
        if (olRs.size() > 0) {
            rv_others.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
            adapter = new EasyAdapter<Order, ItemItemlistOthersBinding>(R.layout.item_itemlist_others) {
                @Override
                public void onBind(@NonNull final ItemItemlistOthersBinding binding, @NonNull final Order model) {
                    binding.cellItemRecid.setText(model.getItemRecid());
                    binding.cellDescription.setText(model.getItemName());
                    binding.cellPrice.setText(model.getSellingPrice());
                    binding.cellUnit.setText(model.getUnitName());
                }
            };
            adapter.addAll(olRs, false);
            rv_others.setAdapter(adapter);
            rv_others.setLayoutManager(new LinearLayoutManager(ctx));
            adapter.notifyDataSetChanged();
        } else {
            rv_others.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners(View v) {
        btn_close.setOnClickListener(new View.OnClickListener() {
            public final void onClick(final View v) {
                dismiss();
            }
        });
    }
}
