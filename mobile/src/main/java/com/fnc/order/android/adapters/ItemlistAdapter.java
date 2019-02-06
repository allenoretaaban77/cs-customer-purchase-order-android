package com.fnc.order.android.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fnc.order.android.R;
import com.fnc.order.android.model.Itemlist;

import java.util.List;

public class ItemlistAdapter extends ArrayAdapter<Itemlist> {

    private final Context context;
    private final List<Itemlist> item_values;

    public ItemlistAdapter(Context context, List<Itemlist> values) {
        super(context, -1, values);
        this.context = context;
        this.item_values = values;
    }

    private ItemlistAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnItemClickListener(final ItemlistAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Itemlist iRs = item_values.get(position);
        View rowView = inflater.inflate(R.layout.item_itemlist, parent, false);
        LinearLayout tvItemBox = (LinearLayout) rowView.findViewById(R.id.tv_item_box);
        LinearLayout tvEmptyItemBox = (LinearLayout) rowView.findViewById(R.id.tv_empty_item_box);

        if(!iRs.getItemName().equals("none")) {
            tvItemBox.setVisibility(View.VISIBLE);
            tvEmptyItemBox.setVisibility(View.GONE);

            LinearLayout listContainer = (LinearLayout) rowView.findViewById(R.id.lst_container);
            listContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onListenerClickListener != null) {
                        onListenerClickListener.onItemClick(v, position);
                    }
                }
            });

            TextView tvItemName = (TextView) rowView.findViewById(R.id.tv_itemname);
            tvItemName.setText(iRs.getItemName());

            TextView tvItemUnit = (TextView) rowView.findViewById(R.id.tv_itemunit);
//            tvItemUnit.setText("Unit: " + iRs.getUnitName());
            tvItemUnit.setText(iRs.getUnitName());


//        TextView tvItemDept = (TextView) rowView.findViewById(R.id.tv_itemdept);
//        tvItemDept.setText(iRs.getDept());
        }else{
            tvItemBox.setVisibility(View.INVISIBLE);
            tvEmptyItemBox.setVisibility(View.VISIBLE);

            TextView tvEmpty = (TextView) rowView.findViewById(R.id.tv_empty);
            tvEmpty.setText("No record found....");
        }

        return rowView;
    }
}
