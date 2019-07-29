package com.fnc.receiving.android.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fnc.receiving.android.R;
import com.fnc.receiving.android.databinding.AItemPurchaseorderlistItemBinding;
import com.fnc.receiving.android.model.Ordered;
import easyadapter.dc.com.library.EasyAdapter;

public class PurchaseOrderListFragment extends Fragment {

    public Context ctx;
    private View rv;
    private EasyAdapter adapter;
    private RecyclerView rv_view;

    public PurchaseOrderListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        AFragmentPurchaseorderlistBinding binding = DataBindingUtil.inflate(
//                inflater, R.layout.a_fragment_purchaseorderlist, container, false);
//        View view = binding.getRoot();
//        //here data must be an instance of the class MarsDataProvider
//        binding.setMarsdata(data);
//        return view;

//        ActivityMainBinding bindings = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        bindings.setProduct(product);
//        product.put("name", "Sleek Navy Blue Sandals");
//        product.put("description", "Beautiful sleek sandals for your casual and cocktail dinner. Comes in different color");

//        rv = inflater.inflate(R.layout.a_fragment_purchaseorderlist, container, false);
//        ctx = rv .getContext();
//
//        adapter = new EasyAdapter<Ordered, AItemPurchaseorderlistItemBinding>(R.layout.a_item_purchaseorderlist_item) {
//            @Override
//            public void onBind(@NonNull AItemPurchaseorderlistItemBinding binding, @NonNull Ordered model) {
//                binding.customerName.setText(model.getCustomerName());
//            }
//        };
//        Ordered oo = new Ordered();
//        oo.setCustomerName("SM");
//        adapter.add(oo);
//        rv_view = (RecyclerView) rv.findViewById(R.id.rv_view);
//        rv_view.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

//        adapter.setOnDataUpdateListener {
//            if (it.size <= 0) {
//                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show()
//            }
//        }

        return rv;
    }
}
