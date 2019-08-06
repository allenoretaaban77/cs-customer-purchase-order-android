package com.fnc.order.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.daimajia.swipe.SwipeLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionItemsAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final List<Order> item_values;

    public TransactionItemsAdapter(Context context, List<Order> values) {
        super(context, -1, values);
        this.context = context;
        this.item_values = values;
    }

    private TransactionItemsAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnItemClickListener(final TransactionItemsAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    private TransactionItemsAdapter.OnRemarksClickListener onRemarksClickListener;
    public interface OnRemarksClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnRemarksClickListener(final TransactionItemsAdapter.OnRemarksClickListener onRemarksClickListener) {
        this.onRemarksClickListener = onRemarksClickListener;
    }

    private class ViewHolder {
        private TextView cell_qty;
        private TextView cell_description;
        private TextView cell_price;
        private TextView cell_total;
        private TextView cell_unit;
        private LinearLayout item_box;

        public ViewHolder(View v) {
            cell_qty = (TextView) v.findViewById(R.id.cell_qty);
            cell_description = (TextView) v.findViewById(R.id.cell_description);
            cell_price = (TextView) v.findViewById(R.id.cell_price);
            cell_total = (TextView) v.findViewById(R.id.cell_total);
            item_box = (LinearLayout) v.findViewById(R.id.item_box);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TransactionItemsAdapter.ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Order iRs = item_values.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_itemlist_tritems, parent, false);
            holder = new TransactionItemsAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TransactionItemsAdapter.ViewHolder) convertView.getTag();
        }

        holder.cell_qty.setText(iRs.getQuantity());

        holder.cell_description.setText(iRs.getItemName() + " | " + iRs.getUnitName());

        if (!iRs.getSellingPrice().equals("null")) {
            double u_price = Double.parseDouble(iRs.getSellingPrice());
            DecimalFormat df = new DecimalFormat("#.00");
            holder.cell_price.setText(df.format(u_price).equals(".00") ? "0.00" : df.format(u_price));
        } else {
            holder.cell_price.setText("0.00");
        }

        if (!iRs.getTotal().equals("null")) {
            holder.cell_total.setText(iRs.getTotal());
        } else {
            holder.cell_total.setText("0.00");
        }

        /*if (iRs.getOldSku().equals("null")) {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.orange_2));
        } else {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.gray_8));
        }

        if (this.curPos == position) {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.green_5));
            holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected_qty));
            holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
        } else {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.black));
            holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
        }*/

        return convertView;
    }

    private Integer curPos = -1;
    public Integer getCurPos() { return this.curPos; }
    public void setCurPos(Integer position) { this.curPos = position; }
}
