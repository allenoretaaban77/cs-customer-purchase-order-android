package com.fnc.order.android.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.daimajia.swipe.SwipeLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.model.Itemlist;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemlistAdapter extends ArrayAdapter<Itemlist> {

    private final Context context;

    private final List<Itemlist> item_values;

    private ArrayList<Itemlist> arr_item_values;
    public ArrayList<Itemlist> getItemValues() { return this.arr_item_values; }
    public void setItem_values(ArrayList<Itemlist> arr_item_values) { this.arr_item_values = arr_item_values; }

    public ItemlistAdapter(Context context, List<Itemlist> values) {
        super(context, -1, values);
        this.context = context;
        this.item_values = values;
        this.arr_item_values = (ArrayList<Itemlist>) values;
    }

    private ItemlistAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int actionId);
    }
    public void setOnItemClickListener(final ItemlistAdapter.OnItemClickListener onListenerClickListener) {
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
        ItemlistAdapter.ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Itemlist iRs = item_values.get(position);
//        View rowView = inflater.inflate(R.layout.item_itemlist, parent, false);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_itemlist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cell_unit.setText(iRs.getUnitName());

        holder.cell_description.setText(iRs.getItemName());

        if(!iRs.getSellingPrice().equals("null")) {
            double u_price = Double.parseDouble(iRs.getSellingPrice());
            DecimalFormat df = new DecimalFormat("#.00");
            holder.cell_price.setText(df.format(u_price));
        }else{
            holder.cell_price.setText("0.00");
        }

        holder.cell_total.setText("0.00");

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

        /*if(!iRs.getItemName().equals("none")) {
            holder.tv_item_box.setVisibility(View.VISIBLE);
            holder.tv_empty_item_box.setVisibility(View.GONE);

            holder.lst_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onListenerClickListener != null) {
                        onListenerClickListener.onItemClick(v, position);
                    }
                }
            });

            holder.tv_itemname.setText(iRs.getItemName());
            holder.tv_itemunit.setText(iRs.getUnitName());
        }else{
            holder.tv_item_box.setVisibility(View.INVISIBLE);
            holder.tv_empty_item_box.setVisibility(View.VISIBLE);

            holder.tv_empty.setText("No record found....");
        }*/

        if(this.curPos == position) {
            holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
            holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background_selected));
        }else{
            holder.cell_qty.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_unit.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_description.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_price.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
            holder.cell_total.setBackground(ContextCompat.getDrawable(context, R.drawable.cell_background));
        }

        return convertView;
    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
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
