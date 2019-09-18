package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum aBranchlistKey {
    BRANCHID("branchid",DataType.INTEGER),
    BRANCHCODE( "branchcode", DataType.TEXT),
    DEVICEID("deviceid", DataType.TEXT),
    DESCRIPTION("description", DataType.TEXT),
    DEVICEID1("deviceID1", DataType.TEXT),
    ACTIVE("active", DataType.TEXT);

    private String key;
    private String dataType;

    aBranchlistKey(String key,String dataType) {
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
