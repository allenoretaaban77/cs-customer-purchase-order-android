package com.fnc.order.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fnc.order.android.R;
import com.fnc.order.android.model.Itemlist;

import java.util.ArrayList;
import java.util.List;

public class ItemlistAdapterRv extends RecyclerView.Adapter<ItemlistAdapterRv.ViewHolder> implements Filterable {

    public List<Itemlist> original_items = new ArrayList<>();
    public List<Itemlist> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private Context ctx;

    private OnItemClickListener mOnItemClickListener;

    public ArrayList<Itemlist> searched_items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, Itemlist obj, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemlistAdapterRv(Context context, List<Itemlist> items) {
        original_items = items;
        filtered_items = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvName;
        public TextView tvUnit;
        public CheckBox cb_select;
        public LinearLayout item_box;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_itemname);
            tvUnit = (TextView) v.findViewById(R.id.tv_itemunit);
            cb_select = (CheckBox) v.findViewById(R.id.cb_select);
            item_box = (LinearLayout) v.findViewById(R.id.item_box);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    public void filter(String text) {
        filtered_items = new ArrayList<>();
        if(text.isEmpty()){
            filtered_items = original_items;
        } else{
            text = text.toLowerCase();

            for(int i = 0; i < original_items.size(); i++ ){
                Itemlist item = original_items.get(i);
                try {
                    String strName = item.getItemName();
                    if (strName.toLowerCase().contains(text)) {
                        filtered_items.add(item);
                    }
                }catch (Exception e){
                    Log.v("ERROR",e.toString());
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Itemlist f = filtered_items.get(position);

        if(!f.getItemName().equals("none")) {
            holder.tvName.setText(f.getItemName());
            if(f.getOldSku().trim().equals("null") || f.getOldSku().trim().equals("")) {
                holder.cb_select.setVisibility(View.GONE);
                holder.tvUnit.setText("Not sync to database.");
//                holder.item_box.setBackground(ctx.getResources().getDrawable(R.color.gray_3));
                holder.tvName.setTextColor(ctx.getResources().getColor(R.color.gray_5));
                holder.tvUnit.setTextColor(ctx.getResources().getColor(R.color.gold_3));
//                holder.item_box.setAlpha(0.7f);
            }else{
                holder.tvName.setTextColor(ctx.getResources().getColor(R.color.red_2));
                holder.tvUnit.setTextColor(ctx.getResources().getColor(R.color.orange_1));
//                holder.item_box.setAlpha(ctx.getResources().getDrawable(R.color.transparent));
                holder.cb_select.setVisibility(View.VISIBLE);
                if(f.getIsChecked()){
                    holder.cb_select.setChecked(true);
                }else{
                    holder.cb_select.setChecked(false);
                }
                holder.tvUnit.setText(f.getUnitName());

                holder.item_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Integer> positions = new ArrayList<>();
                        positions.add(position);
                        Integer x = 0;
                        boolean check = false;
                        if(!f.getIsChecked()){
                            check = true;
                            f.setIsChecked(true);
                        }
                        for(Itemlist c : original_items){
                            if(c.getRecid().equals(f.getRecid())){
                                positions.add(x);
                                c.setIsChecked(check);
                                f.setIsChecked(check);
                            }
                            x++;
                        }
                        for (int y = 0 ;y<positions.size(); y++)
                            notifyItemChanged(positions.get(y));
                    }
                });
                holder.cb_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Integer> positions = new ArrayList<>();
                        positions.add(position);
                        Integer x = 0;
                        boolean check = false;
                        if(!f.getIsChecked()){
                            check = true;
                            f.setIsChecked(true);
                        }
                        for(Itemlist c : original_items){
                            if(c.getRecid().equals(f.getRecid())){
                                positions.add(x);
                                c.setIsChecked(check);
                                f.setIsChecked(check);
                            }
                            x++;
                        }
                        for (int y = 0 ;y<positions.size(); y++)
                            notifyItemChanged(positions.get(y));

                    }
                });
            }
        }
    }

    @Override
    public ItemlistAdapterRv.ViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itemlist_rv, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        if (filtered_items.get(position).getRecid() != null)
            return filtered_items.get(position).getRecid();

        return RecyclerView.NO_ID;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Itemlist> list = original_items;
            final List<Itemlist> result_list = new ArrayList<>(list.size());

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Itemlist>) results.values;
            notifyDataSetChanged();
        }
    }

}
