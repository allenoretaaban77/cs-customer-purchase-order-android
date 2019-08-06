package com.fnc.order.android.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fnc.order.android.R;
import com.fnc.order.android.model.Ordered;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionsAdapter extends ArrayAdapter<Ordered> {

    private final Context context;
    private final List<Ordered> item_values;

    public TransactionsAdapter(Context context, List<Ordered> values) {
        super(context, -1, values);
        this.context = context;
        this.item_values = values;
    }

    private TransactionsAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnItemClickListener(final TransactionsAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    private class ViewHolder {
        private TextView tv_date;
        private TextView tv_name;
        private TextView tv_grandtotal;
        private TextView tv_remarks;
        private LinearLayout ll_item_box;
        public ViewHolder(View v) {
            tv_date = (TextView) v.findViewById(R.id.tv_date);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_grandtotal = (TextView) v.findViewById(R.id.tv_grandtotal);
            tv_remarks = (TextView) v.findViewById(R.id.tv_remarks);
            ll_item_box = (LinearLayout) v.findViewById(R.id.item_box);

        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TransactionsAdapter.ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Ordered od = item_values.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_itemlist_tr, parent, false);
            holder = new TransactionsAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TransactionsAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_date.setText(od.getDeliveryDate().replace(" 00:00:00", ""));
        holder.tv_name.setText(od.getCustomerName());
        holder.tv_grandtotal.setText(String.valueOf(new DecimalFormat("#,###,###.00")
                .format(Double.parseDouble(od.getGrandtotal()))));
        holder.tv_remarks.setText(od.getRemarks());

        holder.ll_item_box.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                if(onListenerClickListener != null) {
                    onListenerClickListener.onItemClick(v, position);
                    setCurPos(position);
                    notifyDataSetChanged();
                }
            }
        });

        if (this.curPos == position) {
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.tv_name.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.tv_grandtotal.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.tv_remarks.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
        } else {
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_name.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_grandtotal.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_remarks.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
        }

        return convertView;
    }

    private Integer curPos = -1;
    public Integer getCurPos() { return this.curPos; }
    public void setCurPos(Integer position) { this.curPos = position; }
}