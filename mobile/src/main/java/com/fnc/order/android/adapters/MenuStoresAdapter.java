package com.fnc.order.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.MenuList;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;

import org.w3c.dom.Text;

import java.util.List;

public class MenuStoresAdapter extends ArrayAdapter<MenuList> {

    private final Context context;
    private final List<MenuList> menulist_values;

    public MenuStoresAdapter(Context context, List<MenuList> values) {
        super(context, -1, values);
        this.context = context;
        this.menulist_values = values;
    }

    private MenuStoresAdapter.OnButtonClickListener onButtonClickListener;
    public interface OnButtonClickListener {
        void onItemClick(View view,  int actionId);
    }
    public void setOnButtonClickListener(final OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_store, parent, false);
        final MenuList mlRS = menulist_values.get(position);

        MaterialRippleLayout mrlStoreTitle = (MaterialRippleLayout) rowView.findViewById(R.id.storetitle);
        Button btnStoreTitle = (Button) rowView.findViewById(R.id.btn_storetitle);


        LinearLayout btnContainer = (LinearLayout) rowView.findViewById(R.id.btn_container);
        btnContainer.setVisibility(View.GONE);

        TextView tvRemarks = (TextView) rowView.findViewById(R.id.tv_remarks);
        tvRemarks.setVisibility(View.GONE);

        TextView tvEmpty = (TextView) rowView.findViewById(R.id.storeempty);
        tvEmpty.setVisibility(View.GONE);

        TextView tvChecklistCount = (TextView) rowView.findViewById(R.id.dr_record_count);
        tvChecklistCount.setText(String.valueOf(mlRS.getRecordCount()));

        if(mlRS.getCustomerName().equals("No Record Found") || mlRS.getCustomerName().equals("")) {
            tvRemarks.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(mlRS.getCustomerName());
        }else{
            if(!mlRS.getRemarks().trim().equals("")) {
                tvRemarks.setText(Helper.toTitleCase(mlRS.getRemarks()));
                tvRemarks.setVisibility(View.VISIBLE);
            }else{
                tvRemarks.setVisibility(View.GONE);
            }

            btnContainer.setVisibility(View.VISIBLE);
            btnStoreTitle.setText(mlRS.getCustomerName());
            mrlStoreTitle.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View v){
                    if(onButtonClickListener != null){
                        onButtonClickListener.onItemClick(v, position);
                    }
                }
            });
        }

        return rowView;
    }
}