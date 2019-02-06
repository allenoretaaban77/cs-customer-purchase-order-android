package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum StoreListKey {
    CUSTOMER_NAME("customername", DataType.TEXT),
    RECID("recid", DataType.INTEGER),
    CUSTOMER_RECID("customer_recid", DataType.TEXT),
    DELIVERY_DATE("deliveryDate", DataType.TEXT),
    DELIVERY_TIME("deliveryTime", DataType.TEXT),
    FOOD_SERVICE("foodservice", DataType.TEXT),
    ORDER_TYPE("ordertype", DataType.TEXT),
    STATUS("status", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    DRIVER_NAME("drivername", DataType.TEXT),
    PLATE_NO("plateno", DataType.TEXT),
    DEVICE_ID("deviceid", DataType.TEXT),
    IS_LOADED("isloaded", DataType.TEXT),
    INCLUDED_PO("includedpo", DataType.TEXT),
    STATUS_LBL("status_lbl", DataType.TEXT),
    CREATED_BY("createdbyName", DataType.TEXT),
    IS_DONE("isdone", DataType.TEXT);

    private String key;
    private String dataType;

    StoreListKey(String key,String dataType) {
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
