package com.fnc.order.android.enumeration;

import com.fnc.order.android.database.DataType;

public enum aStaffsKey {
    EMPID("empId", DataType.INTEGER),
    REFEMPNO( "refempno", DataType.TEXT),
    EMPNO("empNo", DataType.TEXT),
    EMAIL("Email", DataType.TEXT),
    NAME("name", DataType.TEXT),
    BRANCH("Branch", DataType.INTEGER),
    JOBTITLE("Jobtitle", DataType.INTEGER),
    PASS("pass", DataType.TEXT),
    ACTIVE("active", DataType.TEXT),
    ISMOBILEADMIN("ismobileadmin", DataType.TEXT);

    private String key;
    private String dataType;

    aStaffsKey(String key,String dataType) {
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
