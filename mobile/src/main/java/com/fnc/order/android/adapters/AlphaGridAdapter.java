package com.fnc.order.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.fnc.order.android.R;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.utilities.SharedData;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class AlphaGridAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<String> alphaStringArray;

    public AlphaGridAdapter(Context context, ArrayList<String> array) {
        ctx = context;
        alphaStringArray = array;
    }

    public int getCount() {
        return alphaStringArray.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        public TextView tv_alpha;
        public MaterialRippleLayout mrl_box;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        AlphaGridAdapter.ViewHolder vh =  new AlphaGridAdapter.ViewHolder();

        if(v == null){
            LayoutInflater inflater =  (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_itemalpha_gv,null);
            vh.tv_alpha = (TextView) v.findViewById(R.id.tv_alpha);
            vh.mrl_box = (MaterialRippleLayout) v.findViewById(R.id.mrl_box);
            v.setTag(vh);
        } else {
            vh = (AlphaGridAdapter.ViewHolder) v.getTag();
        }

        vh.tv_alpha.setText(alphaStringArray.get(position));
        vh.mrl_box.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                if(onBoxClickListener != null){
                    onBoxClickListener.onItemClick(v, position);
                }
            }
        });

        return v;
    }

    private AlphaGridAdapter.OnBoxClickListener onBoxClickListener;
    public interface OnBoxClickListener {
        void onItemClick(View view,  int actionId);
    }
    public void setOnButtonClickListener(final AlphaGridAdapter.OnBoxClickListener onBoxClickListener) {
        this.onBoxClickListener = onBoxClickListener;
    }

}

