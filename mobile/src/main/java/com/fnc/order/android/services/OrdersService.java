package com.fnc.order.android.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.VolleyInteractor;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OrdersService extends Service implements VolleyCallback {

    public int counter = 0;
    public VolleyCallback refVB;
    Context c;
    LocalBroadcastManager broadcaster = null;
    Handler handler = new Handler();
    Runnable runner;

    @Override
    public void onCreate() {
        super.onCreate();

        c = getApplicationContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("DSX", "service on start command");
//        startTimer();

        handler.removeCallbacks(postRunnable);
        counter = 0;
        handler.postDelayed(postRunnable, 2000);

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this).
                setContentTitle(getText(R.string.app_name)).
                setContentIntent(pendingIntent).build();
        startForeground(1, notification);

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DSX", "service destroyed");
        startService(new Intent(getBaseContext(), OrdersService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Runnable postRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("DSX", "service in counting "+ (counter++));
            LinkedList<Ordered> od =  DcOrdered.getInstance(getApplicationContext())
                    .getOrderedlistCheckStatus();
            if(od.size() > 0 ) {
                for (int i = 0; i < od.size(); i++) {
                    Ordered rsOD = od.get(i);

                    VolleyInteractor vi = new VolleyInteractor();
                    vi.registerCallback(refVB);
                    Log.i("DSX", "post orders");
                    vi.postOrders(getApplicationContext(), rsOD.getJson());

                    DcOrdered.getInstance(getApplicationContext())
                            .updateStatusViaRecId(rsOD.getCustomerRecid(), 1);
                }
            }

            handler.postDelayed(this, 2000);
        }
    };

//    public void startTimer() {
//        runner = new Runnable() {
//            @Override
//            public void run() {
//            }
//        };
//
//        handler.postDelayed(runner, 1000);
//    }

    public void onRequestSuccess(String response, String type) {
        Log.d("DSX", response);
//
    }

    public void onRequestFail(VolleyError volleyError, String type) {
        Log.d("DSX", "request error");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("DSX", "on task removed");
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.submit(runner).cancel(true);
    }
}

