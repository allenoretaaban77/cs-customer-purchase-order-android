package com.fnc.order.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class OrdersService extends Service {

    public int counter = 0;
    public VolleyCallback refVB;
    Context c;
    LocalBroadcastManager broadcaster = null;
    Handler handler = new Handler();

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
            Log.i("DSX", "order service in counting "+ (counter++));
            LinkedList<Ordered> od =  DcOrdered.getInstance(getApplicationContext())
                    .getOrderedlistCheckStatus();
            if(od.size() > 0 ) {
                for (int i = 0; i < od.size(); i++) {
                    final Ordered rsOD = od.get(i);

                    VolleyInteractor vi = new VolleyInteractor();
                    vi.registerCallback(new VolleyCallback() {
                        @Override
                        public void onRequestSuccess(String response, String type) {
                            try {
                                response = response.replace("\r\n ", "");
                                Log.d("DSX post response: ", response);
                                JSONArray objArr = new JSONArray(response);
                                if(objArr.length() > 0) {
                                    JSONObject rowObj = objArr.getJSONObject(0);
                                    if (!rowObj.getBoolean("error")) {
                                        Log.d("DSX post success > ", "error:" + rowObj.getString("error") + " | msg:"
                                                + rowObj.getString("tag") + " | id:" + rowObj.getString("reference_recid"));
                                        DcOrdered.getInstance(getApplicationContext())
                                                .updateStatusViaRecId(rsOD.getCustomerRecid(), 1);
                                    } else {
                                        Log.d("DSX post error: ", rowObj.getString("error_msg"));
                                    }
                                } else {
                                    Log.d("DSX post error: ", "json value error");
                                }
                            } catch (JSONException e) {
                            }
                        }

                        @Override
                        public void onRequestFail(VolleyError response, String type) {
                            Log.d("DSX post error: ", response.getMessage());

                            DcOrdered.getInstance(getApplicationContext())
                                    .updateStatusViaRecId(rsOD.getCustomerRecid(), 0);
                        }
                    });
                    vi.postOrders(getApplicationContext(), rsOD.getJson());
                }
            }

            handler.postDelayed(this, 2000);
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("DSX", "on task removed");

//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.submit(runner).cancel(true);
    }
}

