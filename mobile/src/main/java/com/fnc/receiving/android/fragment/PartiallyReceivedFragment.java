package com.fnc.receiving.android.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.FrameMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fnc.receiving.android.R;
import com.fnc.receiving.android.databinding.AItemPurchaseorderlistItemBinding;
import com.fnc.receiving.android.model.Ordered;

import easyadapter.dc.com.library.EasyAdapter;

public class PartiallyReceivedFragment extends Fragment {

    public Context ctx;
    private View rv;

    public PartiallyReceivedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rv = inflater.inflate(R.layout.a_partiallyreceived_fragment, container, false);
        ctx = rv .getContext();

        return rv;
    }
}
