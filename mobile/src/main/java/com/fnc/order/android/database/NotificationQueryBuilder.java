package com.fnc.order.android.database;

import android.content.ContentValues;
import android.util.Log;

import com.fnc.order.android.constants.GlobalConstants;
import com.fnc.order.android.enumeration.NotificationKey;
import com.fnc.order.android.model.Notification;
import com.fnc.order.android.utilities.Helper;

/**
 * Created by Al on 14/07/2017.
 */

public class NotificationQueryBuilder {

    public static ContentValues prepareNotificationInsertValues(Notification notif) {
        ContentValues values = new ContentValues();
        values.put(NotificationKey.NOTIFICATION_ID.getKey(),notif.getNotifiationId());
        values.put(NotificationKey.TITLE.getKey(),notif.getTitle());
        values.put(NotificationKey.MESSAGE.getKey(),notif.getMessage());
        values.put(NotificationKey.TYPE.getKey(),notif.getType());
        values.put(NotificationKey.NOTIFICATION_DATE.getKey(),notif.getNotificationDate());
        return values;
    }

    public static String prepareDeleteNotifAfter30DaysQuery(String serverTime) {
        String sql = "DELETE from " + Table.NOTIFICATIONS.getName() + " where (((" +
                Helper.dateToMillis(serverTime,GlobalConstants.APP_DATE_FORMAT) + " - ("
                +NotificationKey.NOTIFICATION_DATE.getKey()+ "*1000))/(60*1000) / 60) / 24) > 30";
        Log.i(NotificationQueryBuilder.class.getSimpleName(),"sql = " + sql);
        return sql;
    }
}
