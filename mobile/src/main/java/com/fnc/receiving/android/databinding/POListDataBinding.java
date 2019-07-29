package com.fnc.receiving.android.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class POListDataBinding extends BaseObservable {
    private String celsius;

    public POListDataBinding(String celsius) {
        this.celsius = celsius;
    }

    @Bindable
    public String getCelsius() {
        return celsius;
    }

    public void setCelsius(String celsius) {
        this.celsius = celsius;
//        notifyPropertyChanged(BR.celsius);

    }
}
