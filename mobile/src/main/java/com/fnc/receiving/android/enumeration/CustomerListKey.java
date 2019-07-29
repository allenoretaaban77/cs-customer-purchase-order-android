package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum CustomerListKey {
    RECID("recid", DataType.TEXT),
    CUSTOMER_ID("customerID", DataType.TEXT),
    CUSTOMER_NAME("customername", DataType.TEXT);

    private String key;
    private String dataType;

    CustomerListKey(String key,String dataType) {
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
