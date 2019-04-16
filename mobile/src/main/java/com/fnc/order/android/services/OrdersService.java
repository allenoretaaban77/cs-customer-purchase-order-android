package com.fnc.order.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcOrder;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.VolleyInteractor;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class OrdersService extends Service implements VolleyCallback {

    public int counter = 0;
    public VolleyCallback refVB;


    public OrdersService(Context applicationContext) {
        super();
        Log.i("DSX", "here i am");
    }

    public OrdersService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DSX", "service ondestroy");
        Intent broadcastIntent = new Intent(this, ServiceStarter.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        refVB = this;
        timerTask = new TimerTask() {
            public void run() {
                Log.i("DSX", "in timer "+ (counter++));
                LinkedList<Ordered> od =  DcOrdered.getInstance(getApplicationContext())
                        .getOrderedlist();
                if(od.size() > 0 ) {
                    for (int i = 0; i < od.size(); i++) {
                        Ordered rsOD = od.get(i);

                        VolleyInteractor vi = new VolleyInteractor();
                        vi.registerCallback(refVB);
                        vi.postOrders(getApplicationContext(), rsOD.getJson());
//
                        DcOrdered.getInstance(getApplicationContext())
                                .updateStatusViaRecId(rsOD.getCustomerRecid(), 1);
                    }
                }
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onRequestSuccess(String response, String type) {
        Log.d("DSX", response);
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        Log.d("DSX", "request error");
    }
}
