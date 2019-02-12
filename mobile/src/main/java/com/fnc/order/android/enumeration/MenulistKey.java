package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum MenulistKey {
    CUSTOMER_ID("customerID", DataType.TEXT),
    CUSTOMER_INTEG_ID("integration_recid", DataType.TEXT),
    CUSTOMER_NAME("customername", DataType.TEXT),
    REMARKS("remarks", DataType.TEXT),
    RECORD_COUNT("recordcount", DataType.INTEGER);

    private String key;
    private String dataType;

    MenulistKey(String key,String dataType) {
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
