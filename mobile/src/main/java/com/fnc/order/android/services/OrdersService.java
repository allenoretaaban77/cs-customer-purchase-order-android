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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.fnc.order.android.R;
import com.fnc.order.android.activity.LoginActivity;
import com.fnc.order.android.activity.MainActivity;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.database.DbConstants;
import com.fnc.order.android.datacontroller.DcOrdered;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.Helper;
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
    Boolean isNoPendingRequest = true;
    Service refService;
    Integer refTimeOut = 2000;

    @Override
    public void onCreate() {
        super.onCreate();

        c = getApplicationContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("dsxos", "service on start command");

        refService = this;

        handler.removeCallbacks(postRunnable);
        counter = 0;
        handler.postDelayed(postRunnable, refTimeOut);

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
    public void onStart(Intent intent, int startid)
    {
        Intent intents = new Intent(getBaseContext(), LoginActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("dsxod", "service destroyed");
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
            Log.i("dsxoc", "order service in counting "+ (counter++));
            Context ctx = getApplicationContext();

            if (Helper.isNetworkAvailable(ctx)) {

                refService.sendBroadcast(new Intent("netConnStat").putExtra("isConnected", "true"));

                SQLiteDatabase checkDB = null;
                try {
                    checkDB = SQLiteDatabase.openDatabase(DBPath + getApplicationContext().getPackageName()
                            + File.separator + DbConstants.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
                    checkDB.close();

                    LinkedList<Ordered> od =  DcOrdered.getInstance(getApplicationContext()).getPostedSingle();
                    if(od.size() > 0 ) {
                        final Ordered rsOD = od.get(0);
                        VolleyInteractor vi = new VolleyInteractor();
                        vi.registerCallback(new VolleyCallback() {
                            @Override
                            public void onRequestSuccess(String response, String type) {
                                Log.d("dsxor", response);
                                try {
                                    response = response.replace("\r\n ", "");
                                    JSONArray objArr = new JSONArray(response);
                                    if(objArr.length() > 0) {
                                        JSONObject rowObj = objArr.getJSONObject(0);
                                        if (!rowObj.getBoolean("error")) {
                                            Log.d("dsxos", rsOD.getReferenceRecid());
                                            DcOrdered.getInstance(getApplicationContext())
                                                .updateStatusViaRecId(rsOD.getReferenceRecid(), 1);
                                        } else {
//                                            DcOrdered.getInstance(getApplicationContext())
//                                                .updateStatusViaRecId(rsOD.getReferenceRecid(), 0);
                                            Log.d("dsxoe 1", "request error");
                                        }
                                    } else {
//                                        DcOrdered.getInstance(getApplicationContext())
//                                        .updateStatusViaRecId(rsOD.getReferenceRecid(), 0);
                                        Log.d("dsxoe 2", "request error");
                                    }
                                    isNoPendingRequest = true;
                                    handler.postDelayed(postRunnable, refTimeOut);
                                } catch (JSONException e) {
//                                    DcOrdered.getInstance(getApplicationContext())
//                                            .updateStatusViaRecId(rsOD.getReferenceRecid(), 0);
                                    Log.d("dsxoe 3", "request error");
                                    isNoPendingRequest = true;
                                    handler.postDelayed(postRunnable, refTimeOut);
                                }
                            }
                            @Override
                            public void onRequestFail(VolleyError response, String type) {
//                                DcOrdered.getInstance(getApplicationContext())
//                                    .updateStatusViaRecId(rsOD.getReferenceRecid(), 0);
                                Log.d("dsxoe 4", "request fail " + response);
                                isNoPendingRequest = true;
                                handler.postDelayed(postRunnable, refTimeOut);
                            }
                        });
                        if (isNoPendingRequest) {
                            Log.d("dsxop", "posting");
                            vi.postOrders(getApplicationContext(), rsOD.getJson());
                            isNoPendingRequest = false;
                        }
                    } else {
                        handler.postDelayed(this, refTimeOut);
                        Log.d("dsxoe 5", "no order to post");
                    }
                } catch (SQLiteException e) {
                    Log.d("dsxoe 6", "database doesn't exist yet.");
                    handler.postDelayed(this, refTimeOut);
                }
            } else {

                refService.sendBroadcast(new Intent("netConnStat").putExtra("isConnected", "false"));

                handler.postDelayed(this, refTimeOut);
                Log.d("dsxoe 7", "no internet connection");
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("dsxorm", "on task removed");
    }
}

