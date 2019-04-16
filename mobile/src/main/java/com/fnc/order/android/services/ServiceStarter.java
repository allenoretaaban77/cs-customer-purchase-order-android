package com.fnc.order.android.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops!");
        Log.i("DSX", "service stops");
        context.startService(new Intent(context, OrdersService.class));

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            context.startService(new Intent(context, OrdersService.class));
        }
    }
}