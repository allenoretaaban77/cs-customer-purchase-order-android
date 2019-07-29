package com.fnc.receiving.android.enumeration;

import com.fnc.receiving.android.database.DataType;

public enum DriversKey {
    EMPLOYEE_NUMBER("EmpNo",DataType.INTEGER),
    EMPLOYEE_NAME("empname",DataType.INTEGER);

    private String key;
    private String dataType;

    DriversKey(String key,String dataType) {
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

