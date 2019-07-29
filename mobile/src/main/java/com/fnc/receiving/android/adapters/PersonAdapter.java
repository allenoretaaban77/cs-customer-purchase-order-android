package com.fnc.receiving.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fnc.receiving.android.R;
import com.fnc.receiving.android.model.Person;

import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person> {

    private final Context context;
    private final List<Person> person_values;

    public PersonAdapter(Context context, List<Person> values) {
        super(context, -1, values);
        this.context = context;
        this.person_values = values;
    }

    private PersonAdapter.OnItemClickListener onListenerClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view,  int actionId);
    }
    public void setOnItemClickListener(final PersonAdapter.OnItemClickListener onListenerClickListener) {
        this.onListenerClickListener = onListenerClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Person pRs = person_values.get(position);
        View rowView = inflater.inflate(R.layout.item_person, parent, false);
        LinearLayout tvItemBox = (LinearLayout) rowView.findViewById(R.id.tv_item_box);
        LinearLayout tvEmptyItemBox = (LinearLayout) rowView.findViewById(R.id.tv_empty_item_box);

        if(!pRs.getName().equals("none")) {
            tvItemBox.setVisibility(View.VISIBLE);
            tvEmptyItemBox.setVisibility(View.GONE);

            LinearLayout listContainer = (LinearLayout) rowView.findViewById(R.id.lst_container);
            listContainer.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View v){
                    if(onListenerClickListener != null){
                        onListenerClickListener.onItemClick(v, position);
                    }
                }
            });

            TextView tvName = (TextView) rowView.findViewById(R.id.tv_name);
            tvName.setText(pRs.getName());
        }else{
            tvItemBox.setVisibility(View.INVISIBLE);
            tvEmptyItemBox.setVisibility(View.VISIBLE);

            TextView tvEmpty = (TextView) rowView.findViewById(R.id.tv_empty);
            tvEmpty.setText("No record found....");
        }

        return rowView;
    }
}
