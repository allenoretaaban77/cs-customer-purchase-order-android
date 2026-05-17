package com.fnc.order.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fnc.order.android.R;
import com.fnc.order.android.model.Ordered;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        private TextView tv_date, tv_name, tv_grandtotal, tv_status, tv_refpo;
        private LinearLayout ll_item_box;
        public ViewHolder(View v) {
            tv_date = (TextView) v.findViewById(R.id.tv_date);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_grandtotal = (TextView) v.findViewById(R.id.tv_grandtotal);
            tv_status = (TextView) v.findViewById(R.id.tv_status);
            tv_refpo = (TextView) v.findViewById(R.id.tv_refpo);
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

        String[] dtArr = od.getDeliveryDate().replace(" 00:00:00", "").split("/");
        String[] dtArr2 = od.getDeliver_date_default().split(" ");
        String sTime = dtArr2.length > 1 ? dtArr2[1] : "00:00:00" ;
        holder.tv_date.setText(dtArr[0] + "/" + dtArr[1] + "/" + dtArr[2].substring(2,4) + "\r\n" + sTime);
        holder.tv_name.setText(od.getCustomerName());
        holder.tv_grandtotal.setText(String.valueOf(new DecimalFormat("#,###,###.00")
                .format(Double.parseDouble(od.getGrandtotal()))));
        if (od.getStatus() == 1) {
            holder.tv_status.setText("SENT");
        } else {
            holder.tv_status.setText("UNSENT");
        }

        try {
//            JSONObject obj = new JSONObject(String.valueOf(od.getJson()));
            String sHeader = new JSONObject(String.valueOf(od.getJson())).getString("header");
            JSONObject objx = new JSONObject(sHeader);
            String sRefCPO = objx.getString("refcustomerpo");
            holder.tv_refpo.setText(sRefCPO.trim().equals("") ? "---" : sRefCPO);
        } catch (JSONException e) {
            holder.tv_refpo.setText("---");
        }

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
            holder.tv_status.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
        } else {
            holder.tv_date.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_name.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_grandtotal.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.tv_status.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
        }

        return convertView;
    }

    private Integer curPos = -1;
    public Integer getCurPos() { return this.curPos; }
    public void setCurPos(Integer position) { this.curPos = position; }
}