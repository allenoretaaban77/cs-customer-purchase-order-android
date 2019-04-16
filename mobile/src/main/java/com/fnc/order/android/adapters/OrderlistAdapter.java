package com.fnc.order.android.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.daimajia.swipe.SwipeLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderlistAdapter extends ArrayAdapter<Order> {

    private final Context context;

    private final List<Order> item_values;

    public OrderlistAdapter(Context context, List<Order> values) {
        super(context, -1, values);
        this.context = context;
        this.item_values = values;
    }

    private OrderlistAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnItemClickListener(final OrderlistAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    private class ViewHolder {
        private TextView cell_qty;
        private TextView cell_description;
        private TextView cell_price;
        private TextView cell_total;
        private TextView cell_unit;
        private SwipeLayout swipeLayout;
        private MaterialRippleLayout btn_delete;
        private MaterialRippleLayout btn_remarks;
        private LinearLayout item_box;

        public ViewHolder(View v) {
            swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_layout);

            cell_qty = (TextView) v.findViewById(R.id.cell_qty);
            cell_description = (TextView) v.findViewById(R.id.cell_description);
            cell_price = (TextView) v.findViewById(R.id.cell_price);
            cell_total = (TextView) v.findViewById(R.id.cell_total);
            cell_unit = (TextView) v.findViewById(R.id.cell_unit);
            btn_delete = (MaterialRippleLayout) v.findViewById(R.id.btn_delete);
            btn_remarks = (MaterialRippleLayout) v.findViewById(R.id.btn_remarks);
            item_box = (LinearLayout) v.findViewById(R.id.item_box);

            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        OrderlistAdapter.ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Order iRs = item_values.get(position);
//        View rowView = inflater.inflate(R.layout.item_itemlist, parent, false);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_itemlist, parent, false);
            holder = new OrderlistAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (OrderlistAdapter.ViewHolder) convertView.getTag();
        }

        holder.cell_qty.setText(String.valueOf(iRs.getQuantity()));

        holder.cell_unit.setText(iRs.getUnitName());

        holder.cell_description.setText(iRs.getItemName());

        if (!iRs.getSellingPrice().equals("null")) {
            double u_price = Double.parseDouble(iRs.getSellingPrice());
            DecimalFormat df = new DecimalFormat("#.00");
            holder.cell_price.setText(df.format(u_price));
        } else {
            holder.cell_price.setText("0.00");
        }

        if (!iRs.getTotal().equals("null")) {
            holder.cell_total.setText(iRs.getTotal());
        } else {
            holder.cell_total.setText("0.00");
        }

        holder.btn_remarks.setOnClickListener(onDeleteListener(position, holder));
        holder.btn_delete.setOnClickListener(onDeleteListener(position, holder));
        holder.item_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onListenerClickListener != null) {
                    onListenerClickListener.onItemClick(v, position);
                }
            }
        });

        if (iRs.getIsError()) {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_unit.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.red_2));
            if (this.curPos == position) {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.green_5));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            } else {
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            }
        } else {
            if (this.curPos == position) {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.green_5));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            } else {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.black));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            }
        }
        return convertView;
    }

    private View.OnClickListener onDeleteListener(final int position, final OrderlistAdapter.ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_values.remove(position);
                holder.swipeLayout.close();
                notifyDataSetChanged();
            }
        };
    }

    private Integer curPos = -1;
    public Integer getCurPos() { return this.curPos; }
    public void setCurPos(Integer position) { this.curPos = position; }
}
