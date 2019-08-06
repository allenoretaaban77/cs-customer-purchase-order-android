package com.fnc.order.android.callback;


import com.android.volley.VolleyError;

public interface VolleyCallback {
    void onRequestSuccess(String response, String type);
    void onRequestFail(VolleyError response, String type);
}