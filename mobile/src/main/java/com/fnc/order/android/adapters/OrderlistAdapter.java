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

import androidx.core.content.ContextCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.daimajia.swipe.SwipeLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.model.Itemlist;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;

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
        void onItemClick(View view, int actionId, int flag);
    }
    public void setOnItemClickListener(final OrderlistAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    private OrderlistAdapter.OnRemarksClickListener onRemarksClickListener;
    public interface OnRemarksClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnRemarksClickListener(final OrderlistAdapter.OnRemarksClickListener onRemarksClickListener) {
        this.onRemarksClickListener = onRemarksClickListener;
    }

    private class ViewHolder {
        private TextView cell_qty, cell_fqty, cell_description, cell_price, cell_total, cell_unit;
        private SwipeLayout swipeLayout;
        private MaterialRippleLayout btn_delete, btn_remarks;
        private LinearLayout item_box;

        public ViewHolder(View v) {
            swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_layout);

            cell_qty = (TextView) v.findViewById(R.id.cell_qty);
            cell_fqty = (TextView) v.findViewById(R.id.cell_fqty);
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
        SharedData sp = SharedData.getInstance(context);
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

        holder.cell_qty.setText(iRs.getQuantity());
        holder.cell_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onListenerClickListener != null) {
                    onListenerClickListener.onItemClick(v, position, 0);
                }
            }
        });

        holder.cell_fqty.setText(iRs.getFree());
        holder.cell_fqty.setVisibility(View.GONE);
        if (sp.getInt(SharedKey.SHOW_FREE_COL.getKey()) == 1) {
            holder.cell_fqty.setVisibility(View.VISIBLE);
            holder.cell_fqty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onListenerClickListener != null) {
                        onListenerClickListener.onItemClick(v, position,1);
                    }
                }
            });
        }

        holder.cell_unit.setText(iRs.getUnitName());

        holder.cell_description.setText(iRs.getItemName());

        if (!iRs.getSellingPrice().equals("null")) {
            String strPrice = new DecimalFormat("#,###,###.00").format(Double.parseDouble(String.valueOf(iRs.getSellingPrice())));
            holder.cell_price.setText(strPrice.equals(".00") ? "0.00" : strPrice);
        } else {
            holder.cell_price.setText("0.00");
        }

        if (!iRs.getTotal().equals("null")) {
            holder.cell_total.setText(iRs.getTotal());
        } else {
            holder.cell_total.setText("0.00");
        }

        holder.btn_remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onRemarksClickListener != null) {
                    onRemarksClickListener.onItemClick(v, position);
                }
            }
        });
        if (iRs.getIsLocked() == 1) {
//            Toast.makeText(context, "Item cannot be deleted", Toast.LENGTH_LONG).show();
            holder.btn_delete.setVisibility(View.GONE);
        } else {
            holder.btn_delete.setOnClickListener(onDeleteListener(position, holder));
        }
        holder.item_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onListenerClickListener != null) {
                    onListenerClickListener.onItemClick(v, position, 0);
                }
            }
        });

        if (iRs.getOldSku().equals("null")) {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_fqty.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_unit.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.orange_2));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.orange_2));
        } else {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_fqty.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_unit.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.gray_8));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.gray_8));
        }

        if (iRs.getIsError() == 1) {
            holder.cell_qty.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_fqty.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_unit.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_description.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_price.setTextColor(context.getResources().getColor(R.color.red_2));
            holder.cell_total.setTextColor(context.getResources().getColor(R.color.red_2));
            if (this.curPos == position) {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.green_5));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected_qty));
                holder.cell_fqty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected_fqty));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            } else {
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_fqty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            }
        } else {
            if (this.curPos == position) {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.green_5));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected_qty));
                holder.cell_fqty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected_fqty));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            } else {
                holder.cell_qty.setTextColor(context.getResources().getColor(R.color.black));
                holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_fqty.setTextColor(context.getResources().getColor(R.color.black));
                holder.cell_fqty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
                holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            }
        }

//        if(Helper.checkBranchProfile(getContext()).get(0).getDescription().equals("Commissary") || Helper.checkBranchProfile(getContext()).get(0).getDescription().equals("Main")) {
        if (SharedData.getInstance(getContext()).getData(SharedKey.BRANCH_DESCRIPTION.getKey()).equals("Commissary") || SharedData.getInstance(getContext()).getData(SharedKey.BRANCH_DESCRIPTION.getKey()).equals("Main")) {
            holder.cell_price.setVisibility(View.VISIBLE);
            holder.cell_total.setVisibility(View.VISIBLE);
        } else {
            holder.cell_price.setVisibility(View.GONE);
            holder.cell_total.setVisibility(View.GONE);
        }

        holder.cell_price.setVisibility(View.GONE);
        if (sp.getInt(SharedKey.SHOW_PRICE_COL.getKey()) == 1) {
            holder.cell_price.setVisibility(View.VISIBLE);
        }

        holder.cell_total.setVisibility(View.GONE);
        if (sp.getInt(SharedKey.SHOW_TOTAL_COL.getKey()) == 1) {
            holder.cell_total.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private View.OnClickListener onDeleteListener(final int position, final OrderlistAdapter.ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order ol = item_values.get(position);
                DcOrder.getInstance(getContext()).deleteOrderItemViaId(ol.getItemRecid());
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
