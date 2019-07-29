package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum NotificationKey {
    NOTIFICATION_ID("notif_id",DataType.INTEGER),
    TITLE("title", DataType.TEXT),
    MESSAGE("message",DataType.TEXT),
    TYPE("type",DataType.TEXT),
    NOTIFICATION_DATE("notification_date",DataType.NUMERIC),
    ITEM_ID("item_id", DataType.TEXT);

    private String key;
    private String dataType;

    NotificationKey(String key,String dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public String getDataType() {
        return dataType;
    }
}
