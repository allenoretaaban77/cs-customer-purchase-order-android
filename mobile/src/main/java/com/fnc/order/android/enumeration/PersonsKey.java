package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum PersonsKey {
    IDENTITY_ID("identityid", DataType.TEXT),
    NAME("name", DataType.TEXT);

    private String key;
    private String dataType;

    PersonsKey(String key,String dataType) {
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
