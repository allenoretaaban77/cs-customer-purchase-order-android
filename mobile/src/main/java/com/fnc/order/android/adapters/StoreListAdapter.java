package com.fnc.order.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fnc.order.android.R;
import com.fnc.order.android.enumeration.ChecklistKey;
import com.fnc.order.android.enumeration.SharedKey;
import com.fnc.order.android.enumeration.StoreListKey;
import com.fnc.order.android.model.StoreList;
import com.fnc.order.android.utilities.Helper;
import com.fnc.order.android.utilities.SharedData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoreListAdapter extends ArrayAdapter<StoreList> {

    private final Context context;
    private final List<StoreList> storelists;

    public StoreListAdapter(Context context, ArrayList<StoreList> storelists) {
        super(context, 0, storelists);
        this.context = context;
        this.storelists = storelists;
    }

    private StoreListAdapter.OnItemClickListener onLIstenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view,  int actionId);
    }
    public void setOnItemClickListener(final StoreListAdapter.OnItemClickListener onLIstenerClickListener) {
        this.onLIstenerClickListener = onLIstenerClickListener;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final StoreList storeRs = storelists.get(position);

        View v = inflater.inflate(R.layout.item_storelist, parent, false);

//        RelativeLayout listItemBox = (RelativeLayout) v.findViewById(R.id.listitem_box_inner);
        LinearLayout listItemBox = (LinearLayout) v.findViewById(R.id.listitem_box_inner_2);
        listItemBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                if(onLIstenerClickListener != null){
                    SharedData sp = SharedData.getInstance(context);
                    sp.saveData(ChecklistKey.RECID.getKey(), String.valueOf(storeRs.getRecid()));
                    sp.saveData(SharedKey.CURRENT_INCLUDEDPO.getKey(), storeRs.getIncludedPO());
                    sp.saveData(StoreListKey.ORDER_TYPE.getKey(), String.valueOf(storeRs.getOrderType()));
                    sp.saveData(StoreListKey.FOOD_SERVICE.getKey(), storeRs.getFoodService());
                    sp.saveData(StoreListKey.DELIVERY_TIME.getKey(), storeRs.getDeliveryTime());
                    sp.saveData(StoreListKey.DELIVERY_DATE.getKey(), storeRs.getDeliveryDate());
                    sp.saveData(StoreListKey.DRIVER_NAME.getKey(), storeRs.getDriverName());
                    sp.saveData(StoreListKey.PLATE_NO.getKey(), storeRs.getPlateNo());
                    onLIstenerClickListener.onItemClick(v, position);
                }
            }
        });

        TextView tvStoreOrder = (TextView) v.findViewById(R.id.tv_storeorder);
        tvStoreOrder.setText(storeRs.getOrderType());

        TextView tvFoodService = (TextView) v.findViewById(R.id.tv_foodservice);
        tvFoodService.setText(storeRs.getFoodService().equals("") || storeRs.getFoodService().equals("null")
                ? "-" : storeRs.getFoodService() );

        TextView tvRefId = (TextView) v.findViewById(R.id.tv_refid);
        tvRefId.setText(String.valueOf(storeRs.getRecid()).toString());

        TextView tvRemarks = (TextView) v.findViewById(R.id.tv_remarks);
        tvRemarks.setText("Remarks: " + Helper.toTitleCase(storeRs.getRemarks()));

        String strDate = "";
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(storeRs.getDeliveryDate().toString() + "Z");
            DateFormat df = new SimpleDateFormat("MMM d, yyyy");
            strDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            TextView tvDeliveryDate = (TextView) v.findViewById(R.id.tv_date);
            tvDeliveryDate.setText(strDate);
        }

        String strTime = "";
        try{
            DateFormat format = new SimpleDateFormat("hh:mm:ss");
            Date date = format.parse(storeRs.getDeliveryTime());
            DateFormat df = new SimpleDateFormat("H:mm a");
            strTime = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            TextView tvDeliveryTime = (TextView) v.findViewById(R.id.tv_time);
            tvDeliveryTime.setText(strTime);
        }

        return v;
    }
}