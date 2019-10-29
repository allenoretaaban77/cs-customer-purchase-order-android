package com.fnc.order.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.database.DbConstants;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.VolleyInteractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;

import static com.fnc.order.android.database.DBHelper.DBPath;

public class OrdersService extends Service {

    public int counter = 0;
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

        handler.removeCallbacks(postRunnable);
        counter = 0;
        handler.postDelayed(postRunnable, 1000);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);

            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification = new Notification.Builder(this, channelId)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();
            startForeground(111, notification);

        } else {

            Intent activityIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this).
                    setContentTitle(getText(R.string.app_name)).
                    setContentIntent(pendingIntent).build();
            startForeground(1, notification);

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("dsxo", "service destroyed");
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
            Log.i("dsxo", "order service in counting "+ (counter++));
            Context ctx = getApplicationContext();

            SQLiteDatabase checkDB = null;
            try {
                checkDB = SQLiteDatabase.openDatabase(DBPath + getApplicationContext().getPackageName()
                        + File.separator + DbConstants.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                checkDB.close();

                LinkedList<Ordered> od =  DcOrdered.getInstance(getApplicationContext())
                        .getPostedSingle();
                if(od.size() > 0 ) {
                    Ordered rsOD = od.get(0);
                    VolleyInteractor vi = new VolleyInteractor();
                    vi.registerCallback(new VolleyCallback() {
                        @Override
                        public void onRequestSuccess(String response, String type) {
                            Log.d("dsxo", response);
                            try {
                                response = response.replace("\r\n ", "");
                                JSONArray objArr = new JSONArray(response);
                                if(objArr.length() > 0) {
                                    JSONObject rowObj = objArr.getJSONObject(0);
                                    if (!rowObj.getBoolean("error")) {
                                        DcOrdered.getInstance(getApplicationContext())
                                            .updateStatusViaRecId(rsOD.getCustomerRecid(), 1);
                                    } else {
                                        DcOrdered.getInstance(getApplicationContext())
                                            .updateStatusViaRecId(rsOD.getCustomerRecid(), 0);
                                    }
                                } else {
                                    DcOrdered.getInstance(getApplicationContext())
                                    .updateStatusViaRecId(rsOD.getCustomerRecid(), 0);
                                }
                            } catch (JSONException e) {
                                DcOrdered.getInstance(getApplicationContext())
                                        .updateStatusViaRecId(rsOD.getCustomerRecid(), 0);
                            }
                            handler.postDelayed(postRunnable, 1000);
                        }
                        @Override
                        public void onRequestFail(VolleyError response, String type) {
                            DcOrdered.getInstance(getApplicationContext())
                                .updateStatusViaRecId(rsOD.getCustomerRecid(), 0);
                            handler.postDelayed(postRunnable, 1000);
                        }
                    });
                    vi.postOrders(getApplicationContext(), rsOD.getJson());
                } else {
                    handler.postDelayed(this, 1000);
                }
            } catch (SQLiteException e) {
                handler.postDelayed(this, 1000);
                Log.d("dsxs", "database doesn't exist yet.");
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("dsxo", "on task removed");
    }
}

